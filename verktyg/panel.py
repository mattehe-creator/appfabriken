"""Appfabrikens panel: visar vad den lokala modellen, Cursor och Claude gör.

Start:  python verktyg\\panel.py      (öppnar http://127.0.0.1:8765 i webbläsaren)
Bara standardbiblioteket. GitHub läses via gh (samma inloggning som körskriptet).
Panelen läser och visar, den ändrar ingenting.
"""
import base64
import json
import os
import re
import subprocess
import sys
import threading
import time
import urllib.request
import webbrowser
import calendar
from datetime import datetime, timedelta
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import parse_qs, urlparse

REPO = "mattehe-creator/appfabriken"
PORT = 8765
DATA = Path(os.environ.get("LOCALAPPDATA", ".")) / "appfabriken"
STATUS = DATA / "status.json"
AIDER = DATA / "logg" / "aider-senaste.log"
LOGGMAPP = DATA / "logg"

STARTAD = str(time.time())  # ny process = nytt värde; sidan laddar om sig då
_cache = {"tid": 0.0, "github": {}}
_tokcache = {}  # loggfil -> (mtime, storlek, skickade, mottagna)
_commit = {}  # sha -> (författare, datum, rubrik); en commit ändras aldrig
_lock = threading.Lock()


def gh(path):
    try:
        ut = subprocess.run(["gh", "api", path], capture_output=True, text=True,
                            encoding="utf-8", timeout=20,
                            creationflags=getattr(subprocess, "CREATE_NO_WINDOW", 0))
        return json.loads(ut.stdout) if ut.returncode == 0 else None
    except Exception:
        return None


def _auto_andel(s, klara):
    """Andel klara uppgifter för ett steg med {"uppgifter": {"datum", "fran", "till"}} i release.json.
    Uppgiftsfiler heter <datum>-<nr>-<namn>[-f2].md; försök 2 räknas inte dubbelt."""
    u = s.get("uppgifter")
    if not u or klara is None:
        return None
    klart = set()
    for namn in klara:
        d = namn.split("-")
        if len(d) > 3 and "-".join(d[:3]) == u["datum"] and d[3].isdigit() and u["fran"] <= int(d[3]) <= u["till"]:
            klart.add(int(d[3]))
    return len(klart) / (u["till"] - u["fran"] + 1)


def release_berakna(rj, nu=None, klara_uppgifter=None):
    """Procent klart och beräknat releasedatum ur release.json (steg med vikt, status och andel).

    Byggfarten är klarad vikt per dag sedan start. Den gäller bara utvecklingsstegen; externa steg
    (Mattias test, Googles granskning) räknas i dagar enligt antagandet i filen.
    """
    nu = nu or datetime.now()
    steg = [dict(s) for s in rj.get("steg", [])]
    for s in steg:  # automatisk andel går före den manuella
        a = _auto_andel(s, klara_uppgifter)
        if a is not None:
            s["andel"] = a
            if a >= 1:
                s["status"] = "klar"
            elif a > 0:
                s["status"] = "pagar"
    total = sum(s["vikt"] for s in steg) or 1

    def klart(s):
        if s["status"] == "klar":
            return s["vikt"]
        return s["vikt"] * min(max(s.get("andel", 0), 0), 1) if s["status"] == "pagar" else 0

    klar_vikt = sum(klart(s) for s in steg)
    dev = [s for s in steg if not s.get("extern")]
    dev_klart = sum(klart(s) for s in dev)
    dev_kvar = sum(s["vikt"] for s in dev) - dev_klart
    dagar = max((nu - datetime.fromisoformat(rj["start"])).total_seconds() / 86400, 0.5)
    fart = max(dev_klart / dagar, 0.1)  # vikt per dygn
    dev_dagar = dev_kvar / fart
    ext_dagar = sum(s.get("dagar", 0) for s in steg if s.get("extern") and s["status"] != "klar")
    klar_helt = all(s["status"] == "klar" for s in steg)

    def datum(d):
        return (nu + timedelta(days=d)).date().isoformat()

    return {
        "mal": rj.get("mal", ""),
        "procent": round(100 * klar_vikt / total),
        "fart": round(fart, 1),
        "klar": klar_helt,
        "eta": None if klar_helt else datum(dev_dagar + ext_dagar),
        "eta_tidigast": None if klar_helt else datum(dev_dagar / 1.5 + ext_dagar),
        "eta_senast": None if klar_helt else datum(dev_dagar * 2 + ext_dagar),
        "externa_dagar": ext_dagar,
        "anm": rj.get("anm", ""),
        "steg": [{"namn": s["namn"], "status": s["status"],
                  "andel": s.get("andel", 1 if s["status"] == "klar" else 0),
                  "extern": bool(s.get("extern"))} for s in steg],
    }


def _tal(text, suffix):
    v = float(text.replace(",", "."))
    return v * {"k": 1_000, "m": 1_000_000}.get(suffix.lower(), 1)


_SKICKADE = re.compile(r"([\d.,]+)\s*([kKmM]?)\s+sent")
_MOTTAGNA = re.compile(r"([\d.,]+)\s*([kKmM]?)\s+received")


def tokens_i_logg(sokvag):
    """Aiders rader 'Tokens: 4.1k sent, 1.2k received.' i en körlogg. Aider avrundar till tusental."""
    st = sokvag.stat()
    k = _tokcache.get(sokvag)
    if k and k[0] == st.st_mtime and k[1] == st.st_size:
        return k[2], k[3]
    rå = sokvag.read_bytes()
    text = rå.decode("utf-16", errors="replace") if rå[:2] in (b"\xff\xfe", b"\xfe\xff") \
        else rå.decode("utf-8-sig", errors="replace")
    skickade = mottagna = 0.0
    for rad in text.splitlines():
        if "Tokens:" not in rad:
            continue
        s, m = _SKICKADE.search(rad), _MOTTAGNA.search(rad)
        if s:
            skickade += _tal(s.group(1), s.group(2))
        if m:
            mottagna += _tal(m.group(1), m.group(2))
    _tokcache[sokvag] = (st.st_mtime, st.st_size, skickade, mottagna)
    return skickade, mottagna


def tokens_sammanstall(nu=None):
    """Lokala modellens tokens (skickade + mottagna) totalt, idag, denna vecka och per sekund.

    Cursor och Claude saknar mätning här; bara Aiders egen räkning i körloggarna finns.
    """
    nu = nu or datetime.now()
    idag0 = nu.replace(hour=0, minute=0, second=0, microsecond=0)
    vecka0 = idag0 - timedelta(days=idag0.weekday())
    ut = {"totalt": 0, "idag": 0, "vecka": 0, "korningar": 0, "per_sek": None, "per_sek_senaste": None}
    sek_tot = tok_tot = 0.0
    senaste = None
    try:
        loggar = sorted(LOGGMAPP.glob("kor-*.log"))
    except Exception:
        loggar = []
    for f in loggar:
        try:
            start = datetime.strptime(f.stem[4:], "%Y%m%d-%H%M%S")
            skickade, mottagna = tokens_i_logg(f)
            sek = max(f.stat().st_mtime - start.timestamp(), 1.0)
        except Exception:
            continue
        t = skickade + mottagna
        if t <= 0:
            continue
        ut["totalt"] += t
        ut["korningar"] += 1
        if start >= idag0:
            ut["idag"] += t
        if start >= vecka0:
            ut["vecka"] += t
        sek_tot += sek
        tok_tot += t
        senaste = (t, sek)
    if sek_tot:
        ut["per_sek"] = round(tok_tot / sek_tot, 1)
    if senaste:
        ut["per_sek_senaste"] = round(senaste[0] / senaste[1], 1)
    for k in ("totalt", "idag", "vecka"):
        ut[k] = int(round(ut[k]))
    return ut


def _utc(s):
    return calendar.timegm(time.strptime(s, "%Y-%m-%dT%H:%M:%SZ"))


_dator_cache = {"tid": 0.0, "v": {}}


def dator():
    """GPU (nvidia-smi) och Ollama (api/ps). Båda är valfria: saknas de visas inget."""
    if time.time() - _dator_cache["tid"] < 5:
        return _dator_cache["v"]
    ut = {"gpu": None, "ollama": None}
    try:
        r = subprocess.run(["nvidia-smi", "--query-gpu=utilization.gpu,temperature.gpu,memory.used,memory.total",
                            "--format=csv,noheader,nounits"], capture_output=True, text=True, timeout=3,
                           creationflags=getattr(subprocess, "CREATE_NO_WINDOW", 0))
        if r.returncode == 0 and r.stdout.strip():
            a = [x.strip() for x in r.stdout.strip().splitlines()[0].split(",")]
            ut["gpu"] = {"last": int(a[0]), "temp": int(a[1]), "vram": int(a[2]), "vram_max": int(a[3])}
    except Exception:
        pass
    try:
        with urllib.request.urlopen("http://127.0.0.1:11434/api/ps", timeout=1) as f:
            ut["ollama"] = [m.get("name", "?") for m in json.load(f).get("models", [])]
    except Exception:
        pass
    _dator_cache.update(tid=time.time(), v=ut)
    return ut


def stilla_larm(nu, aktivitet, ko, oppna_arbete, cursor_nu, lokal_fas, lokal_tid, release_klar, grans_min=30):
    """None om flödet rör sig, annars {"min", "orsak"}.

    nu, aktivitet och lokal_tid är epoksekunder. Flödet räknas som stilla när inget hänt på GitHub
    eller i den lokala körningen på grans_min minuter, den lokala modellen inte kodar, och något
    väntar (uppgifter i kön, öppna lokal/- eller cursor/-PR:er) eller releasen inte är klar.
    """
    if lokal_fas in ("kodar", "testar"):
        return None
    senast = max(aktivitet or 0, lokal_tid or 0)
    minuter = int((nu - senast) / 60) if senast else 0
    if minuter < grans_min:
        return None
    krock = [p for p in oppna_arbete if p["lage"].startswith("KROCKAR")]
    if krock:
        orsak = f"PR #{krock[0]['nr']} krockar med main och måste lösas av Claude."
    elif cursor_nu:
        orsak = "Cursor: " + cursor_nu[0]["text"] + "."
    elif oppna_arbete:
        p = oppna_arbete[0]
        orsak = f"PR #{p['nr']} ({p['lage']}) rör sig inte."
    elif ko:
        orsak = f"{len(ko)} uppgift(er) i kön, men den lokala modellen arbetar inte (läge: {lokal_fas})."
    elif not release_klar:
        orsak = "Kön är tom men releasen är inte klar. Nästa uppdrag måste skrivas."
    else:
        return None
    return {"min": minuter, "orsak": orsak}


def vantar_pa_mattias(prs, fragor, release):
    """Det bara Mattias kan göra: merga Claudes PR:er, svara på frågor, stegen som är hans tur."""
    ut = [{"typ": "MERGA", "text": f"PR #{p['number']}: {p['title']}"}
          for p in prs if p["state"] == "open" and p["head"]["ref"].startswith("claude/") and not p.get("draft")]
    ut += [{"typ": "FRÅGA", "text": f} for f in fragor]
    if release and not release["klar"]:
        steg = release["steg"]
        if all(s["status"] == "klar" for s in steg if not s["extern"]):
            kvar = [s for s in steg if s["extern"] and s["status"] != "klar"]
            if kvar:
                ut.append({"typ": "DIN TUR", "text": kvar[0]["namn"]})
    return ut


def aktor(gren="", forfattare="", meddelande=""):
    if gren.startswith("lokal/"):
        return "lokal"
    # github-actions verkställer Cursors beslut (cursor-beslut), så det räknas som Cursor.
    if gren.startswith("cursor/") or "cursor" in forfattare.lower() or "github-actions" in forfattare.lower():
        return "cursor"
    if forfattare == "Claude" or "Claude-Session" in meddelande:
        return "claude"
    return "mattias"


def commit(sha):
    if sha not in _commit:
        c = gh(f"repos/{REPO}/commits/{sha}")
        if not c:
            return None
        _commit[sha] = (c["commit"]["author"]["name"], c["commit"]["author"]["date"],
                        c["commit"]["message"].split("\n")[0])
    return _commit[sha]


def cursor_lage(prs, korningar):
    """Vad Cursor gör nu, och Cursors beslut på lokal/-grenar (de syns inte i mains historik)."""
    nu, beslut = [], []
    runs = korningar.get("workflow_runs", [])
    for p in prs:
        gren = p["head"]["ref"]
        if not gren.startswith("lokal/"):
            continue
        huvud = commit(p["head"]["sha"])
        if not huvud:
            continue
        forf, datum, rubrik = huvud
        if "cursor" in forf.lower():
            beslut.append({"tid": datum, "vem": "cursor", "text": "Cursor: " + rubrik})
        elif p["state"] == "open":
            startad = [r for r in runs if r["name"] == "Cursor-granskning"
                       and r.get("display_title", "").endswith(gren) and r["created_at"] >= datum]
            if startad:
                nu.append({"tid": startad[0]["created_at"],
                           "text": f"Granskar PR #{p['number']} ({gren[6:]})"})
            else:
                nu.append({"tid": datum, "text": f"PR #{p['number']} väntar på bygge innan granskning"})
    for r in runs:
        if r["name"] == "Cursor-uppdrag" and r["status"] != "completed":
            nu.append({"tid": r["created_at"], "text": "Skickar uppdrag: " + r["display_title"]})
    # Uppdrag som skickats men där Cursor inte öppnat någon PR än
    cursor_prs = [p["created_at"] for p in prs if p["head"]["ref"].startswith("cursor/")]
    for r in runs:
        if r["name"] == "Cursor-uppdrag" and r["conclusion"] == "success" \
                and not any(t >= r["created_at"] for t in cursor_prs) \
                and time.time() - time.mktime(time.strptime(r["created_at"], "%Y-%m-%dT%H:%M:%SZ")) < 6 * 3600:
            nu.append({"tid": r["created_at"], "text": "Arbetar med uppdrag: " + r["display_title"]})
    return nu, beslut


def hamta_github():
    with _lock:
        if time.time() - _cache["tid"] < 30:
            return _cache["github"]
    prs = gh(f"repos/{REPO}/pulls?state=all&per_page=30") or []
    commits = gh(f"repos/{REPO}/commits?per_page=15") or []
    korningar = gh(f"repos/{REPO}/actions/runs?per_page=15") or {}
    ny = gh(f"repos/{REPO}/contents/uppgifter/ny?ref=main") or []
    fragor_fil = gh(f"repos/{REPO}/contents/FRAGOR.md?ref=main") or {}
    klara = gh(f"repos/{REPO}/contents/uppgifter/klar?ref=main")
    klara = [f["name"] for f in klara] if isinstance(klara, list) else None
    bygge_run = ((gh(f"repos/{REPO}/actions/workflows/bygg.yml/runs?branch=main&per_page=1") or {})
                 .get("workflow_runs") or [None])[0]
    release = None
    try:
        rfil = gh(f"repos/{REPO}/contents/release.json?ref=main") or {}
        release = release_berakna(json.loads(base64.b64decode(rfil.get("content", "")).decode("utf-8")),
                                  klara_uppgifter=klara)
    except Exception:
        pass
    fragor = []
    try:
        text = base64.b64decode(fragor_fil.get("content", "")).decode("utf-8")
        oppna = text.split("## Öppna", 1)[1].split("\n## ", 1)[0]
        fragor = [r[2:].strip() for r in oppna.splitlines() if r.startswith("- ")]
    except Exception:
        pass

    handelser = []
    for c in commits:
        m = c["commit"]["message"]
        handelser.append({"tid": c["commit"]["author"]["date"],
                          "vem": aktor(forfattare=c["commit"]["author"]["name"], meddelande=m),
                          "text": m.split("\n")[0]})
    for r in korningar.get("workflow_runs", []):
        if r["name"] == "Cursor-uppdrag" and r["conclusion"] == "success":
            handelser.append({"tid": r["created_at"], "vem": "claude",
                              "text": "Claude skickade uppdrag till Cursor: " + r["display_title"]})
    for p in prs:
        vem = aktor(gren=p["head"]["ref"])
        handelser.append({"tid": p["created_at"], "vem": vem,
                          "text": f"PR #{p['number']} öppnad: {p['title']}"})
        lokal = p["head"]["ref"].startswith("lokal/")
        if p["state"] == "closed" and not p.get("merged_at"):
            handelser.append({"tid": p["closed_at"], "vem": "cursor" if lokal else "claude",
                              "text": f"PR #{p['number']} underkänd och stängd"})
        if p.get("merged_at"):
            handelser.append({"tid": p["merged_at"], "vem": "cursor" if lokal else "claude",
                              "text": f"PR #{p['number']} mergad" + (" efter Cursors godkännande" if lokal
                                                                       else " efter Claudes granskning")})
    cursor_nu, cursor_beslut = cursor_lage(prs, korningar)
    handelser += cursor_beslut
    handelser.sort(key=lambda h: h["tid"], reverse=True)

    # En godkänd PR som krockar med main kan inte mergas av cursor-beslut (workflowet fallerar tyst).
    krock = set()
    for p in prs:
        if p["state"] == "open":
            d = gh(f"repos/{REPO}/pulls/{p['number']}") or {}
            if d.get("mergeable_state") == "dirty":
                krock.add(p["number"])

    # Senaste aktivitet på GitHub: commits, workflowkörningar och PR:er.
    tider = [_utc(c["commit"]["author"]["date"]) for c in commits[:3]]
    tider += [_utc(r["updated_at"]) for r in korningar.get("workflow_runs", [])[:5]]
    tider += [_utc(p["updated_at"]) for p in prs if p["head"]["ref"].startswith(("lokal/", "cursor/"))]
    idag0 = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0).timestamp()
    klar_idag = sum(1 for p in prs if p["head"]["ref"].startswith("lokal/") and p.get("merged_at")
                    and _utc(p["merged_at"]) >= idag0)
    arbete = [{"nr": p["number"], "lage": ("KROCKAR MED MAIN" if p["number"] in krock
                                           else ("utkast" if p.get("draft") else "väntar på granskning"))}
              for p in prs if p["state"] == "open" and p["head"]["ref"].startswith(("lokal/", "cursor/"))]
    data = {
        "ko": sorted(f["name"] for f in ny if f.get("name", "").endswith(".md")),
        "prs": [{"nr": p["number"], "titel": p["title"], "vem": aktor(gren=p["head"]["ref"]),
                 "lage": "mergad" if p.get("merged_at") else ("stängd" if p["state"] == "closed"
                         else ("KROCKAR MED MAIN, Claude måste lösa" if p["number"] in krock
                               else ("utkast" if p.get("draft") else "väntar på granskning")))}
                for p in prs[:12]],
        "handelser": handelser[:25],
        "fragor": fragor,
        "cursor": sorted(cursor_nu, key=lambda h: h["tid"]),
        "release": release,
        "aktivitet": max(tider) if tider else 0,
        "arbete": arbete,
        "bygge": ({"status": bygge_run["status"], "slutsats": bygge_run["conclusion"],
                   "tid": bygge_run["updated_at"]} if bygge_run else None),
        "vantar": vantar_pa_mattias(prs, fragor, release),
        "klar_idag": klar_idag,
    }
    with _lock:
        _cache.update(tid=time.time(), github=data)
    return data


def lokalt():
    try:
        status = json.loads(STATUS.read_text(encoding="utf-8-sig"))
    except Exception:
        status = {"fas": "okänd", "text": "Ingen körning har skrivit status än."}
    try:
        rader = AIDER.read_text(encoding="utf-8-sig", errors="replace").splitlines()[-60:]
        andrad = AIDER.stat().st_mtime
    except Exception:
        rader, andrad = [], 0
    status["aider"] = rader
    status["aider_sekunder_sedan"] = int(time.time() - andrad) if andrad else None
    status["dator"] = dator()
    return status


SIDA = r"""<!doctype html><html lang="sv"><head><meta charset="utf-8">
<title>Appfabriken</title><meta name="viewport" content="width=device-width,initial-scale=1">
<style>
:root{--g:#00ff66;--g2:#0a8f3c;--bg:#000;--p:rgba(0,10,3,.82);--c:#7fd7ff;--k:#ffb347;--m:#d9d9d9}
*{box-sizing:border-box;scrollbar-width:none}*::-webkit-scrollbar{display:none}body{margin:0;background:var(--bg);color:var(--g);font:14px/1.45 Consolas,"Cascadia Mono",monospace;overflow:hidden}
canvas{position:fixed;inset:0;z-index:0}
main{position:relative;z-index:1;display:grid;grid-template-columns:1.4fr 1fr;grid-template-rows:auto auto auto minmax(180px,1fr) auto;gap:14px;height:100vh;padding:16px}
section{background:var(--p);border:1px solid var(--g2);padding:12px;overflow:auto;box-shadow:0 0 18px rgba(0,255,102,.15)}
h1{margin:0;font-size:18px;letter-spacing:.2em;text-shadow:0 0 8px var(--g)}h2{margin:0 0 8px;font-size:13px;letter-spacing:.15em;color:var(--g2)}
#topp{grid-column:1/3;display:flex;gap:24px;align-items:center;flex-wrap:wrap}
#fas{padding:2px 10px;border:1px solid var(--g);text-transform:uppercase}
#fas.kodar,#fas.testar{animation:puls 1.2s infinite}@keyframes puls{50%{box-shadow:0 0 14px var(--g)}}
#fas.fel{color:#ff5555;border-color:#ff5555}
.chip{text-decoration:none;padding:2px 10px;border:1px solid var(--g2);color:var(--g2);text-transform:uppercase}
.chip.ok{color:var(--g);border-color:var(--g)}.chip.var{color:var(--k);border-color:var(--k)}
.chip.fel{color:#ff5555;border-color:#ff5555;animation:pulsr 1.2s infinite}@keyframes pulsr{50%{box-shadow:0 0 14px #ff5555}}
#larm{flex-basis:100%;color:#ff5555;text-shadow:0 0 8px #ff5555}#larm:empty{display:none}
.vantar{color:var(--k)}.vantar b{font-weight:normal;border:1px solid var(--k);padding:0 6px;margin-right:6px;font-size:11px}
#mat{grid-column:1/3;display:flex;gap:28px;align-items:flex-start;flex-wrap:wrap;overflow:visible}
#mat h2{margin-bottom:4px}#mat svg{display:block}
#bage{filter:drop-shadow(0 0 6px #00ff66);transition:stroke-dasharray 1.2s ease}
#eta{font-size:20px;text-shadow:0 0 8px var(--g)}#steg{columns:3;column-gap:20px;font-size:12px;margin-top:6px}
#steg li{margin:0 0 2px;break-inside:avoid}.klar{color:var(--g)}.pagar{color:var(--k)}.kvar{color:var(--g2)}
#tok{display:grid;grid-template-columns:repeat(2,auto);gap:4px 22px;margin-top:4px}#tok b{font-size:20px;font-weight:normal;text-shadow:0 0 8px var(--g)}
[data-tip]{position:relative;cursor:help}
[data-tip]::after{content:attr(data-tip);position:absolute;left:0;top:calc(100% + 8px);z-index:30;width:380px;max-width:85vw;padding:10px 12px;background:#001a08;border:1px solid var(--g);color:var(--g);font:12px/1.5 Consolas,"Cascadia Mono",monospace;letter-spacing:0;text-transform:none;text-shadow:none;white-space:pre-line;box-shadow:0 0 18px rgba(0,255,102,.35);opacity:0;pointer-events:none;transition:opacity .15s}
[data-tip]:hover::after{opacity:1}[data-tip].tip-r::after{left:auto;right:0}
#topp{overflow:visible;position:relative;z-index:6}#mat{position:relative;z-index:5}
#kopr{max-height:30vh}
#kod{grid-row:3/6;white-space:pre-wrap;word-break:break-word}
#kod .rad{opacity:.95}#kod .ny{animation:in .6s}@keyframes in{from{color:#fff;text-shadow:0 0 10px #fff}}
.cursor::after{content:"█";animation:blink 1s steps(1) infinite}@keyframes blink{50%{opacity:0}}
ul{list-style:none;margin:0;padding:0}li{margin:0 0 6px}
.lokal{color:var(--g)}.cursor-a{color:var(--c)}.claude{color:var(--k)}.mattias{color:var(--m)}
.tag{display:inline-block;min-width:66px}.liten{color:var(--g2);font-size:12px}
@media(max-width:900px){main{grid-template-columns:1fr;grid-template-rows:auto;height:auto}body{overflow:auto}#kod{grid-row:auto;max-height:50vh}}
</style></head><body><canvas id="regn"></canvas><main>
<section id="topp"><h1>APPFABRIKEN</h1><span id="fas">...</span><span id="kon" class="chip">Kön ...</span><span id="bygge" class="chip">Bygge ...</span><span id="vchip" class="chip">...</span><a id="anv" class="chip ok" href="/anvandning">Användning</a><span id="uppgift"></span><span id="text" class="liten"></span><div id="larm"></div></section>
<section id="mat">
<div><h2 id="h-matare">MÄTARE // AVSTÅND TILL RELEASE</h2>
<svg viewBox="0 0 200 124" width="230" role="img" aria-label="Procent klart till release">
<path d="M20 100 A80 80 0 0 1 180 100" pathLength="100" fill="none" stroke="#0a8f3c" stroke-width="12" opacity=".35"/>
<path id="bage" d="M20 100 A80 80 0 0 1 180 100" pathLength="100" fill="none" stroke="#00ff66" stroke-width="12" stroke-dasharray="0 100"/>
<text id="proc" x="100" y="92" text-anchor="middle" font-size="34" fill="#00ff66" font-family="Consolas,monospace">--</text>
<text x="100" y="112" text-anchor="middle" font-size="9" fill="#0a8f3c" font-family="Consolas,monospace">KLART AV 100</text>
<text x="20" y="118" text-anchor="middle" font-size="9" fill="#0a8f3c" font-family="Consolas,monospace">0</text>
<text x="180" y="118" text-anchor="middle" font-size="9" fill="#0a8f3c" font-family="Consolas,monospace">100</text></svg></div>
<div style="flex:1;min-width:300px"><h2 id="malrubrik">RELEASE</h2><div id="eta">Räknar...</div><div id="spann" class="liten"></div><ul id="steg"></ul><div id="anm" class="liten"></div></div>
<div><h2 id="h-tokens">TOKENS // LOKAL MODELL</h2><div id="tok"><span class="liten">Totalt</span><span class="liten">Idag</span><b id="t-totalt">-</b><b id="t-idag">-</b><span class="liten">Denna vecka</span><span class="liten">Per sekund (snitt)</span><b id="t-vecka">-</b><b id="t-sek">-</b></div>
<div id="t-not" class="liten" style="max-width:300px;margin-top:6px"></div></div>
<div><h2 id="h-dator">DATORN // MODELLEN</h2><div id="dator" class="liten" style="font-size:14px;line-height:1.6"></div></div>
</section>
<section id="kod"><h2>LOKAL MODELL // AIDER</h2><div id="rader"></div><span class="cursor"></span></section>
<section id="cur"><h2>CURSOR // NU</h2><ul id="cursornu"></ul><div class="liten" style="margin-top:6px">Detaljer: <a href="https://cursor.com/agents" target="_blank" style="color:var(--c)">cursor.com/agents</a> · <a href="https://github.com/mattehe-creator/appfabriken/actions" target="_blank" style="color:var(--c)">GitHub Actions</a></div></section>
<section><h2>HÄNDELSER</h2><ul id="handelser"></ul></section>
<section id="kopr"><h2>KÖ OCH PR</h2><div class="liten">Väntar på dig</div><ul id="fragor"></ul><div class="liten">Uppgifter som väntar</div><ul id="ko"></ul><div class="liten" style="margin-top:8px">Pull requests</div><ul id="prs"></ul></section>
</main><script>
const c=document.getElementById("regn"),x=c.getContext("2d");let kol=[];
function storlek(){c.width=innerWidth;c.height=innerHeight;kol=Array(Math.ceil(c.width/16)).fill(0).map(()=>Math.random()*c.height/16)}
storlek();addEventListener("resize",storlek);
const tecken="アイウエオカキクケコサシスセソタチツテトナニヌネノ0123456789{}()<>=;:+-*/fun val Kop";
function regn(){x.fillStyle="rgba(0,0,0,.08)";x.fillRect(0,0,c.width,c.height);x.fillStyle="#0f6";x.font="16px monospace";
kol.forEach((y,i)=>{x.fillText(tecken[Math.random()*tecken.length|0],i*16,y*16);kol[i]=y*16>c.height&&Math.random()>.975?0:y+1})}
setInterval(regn,55);
const vemNamn={lokal:"LOKAL",cursor:"CURSOR",claude:"CLAUDE",mattias:"MATTIAS"};
const kls=v=>v==="cursor"?"cursor-a":v;
const esc=s=>String(s).replace(/[&<>]/g,t=>({"&":"&amp;","<":"&lt;",">":"&gt;"}[t]));
let sist=[];
async function lokal(){try{const d=await (await fetch("/api/lokal")).json();
const f=document.getElementById("fas");f.textContent=d.fas;f.className=d.fas;sat("fas",fasTip(d.fas));
document.getElementById("uppgift").textContent=d.uppgift||"";
let t=d.text||"";if(d.fas==="kodar"&&d.aider_sekunder_sedan!=null)t+=" Senaste utskrift för "+d.aider_sekunder_sedan+" s sedan.";
document.getElementById("text").textContent=t;dator(d.dator);
const r=d.aider||[];if(r.join("\n")!==sist.join("\n")){const ny=r.length-sist.length;
document.getElementById("rader").innerHTML=r.map((l,i)=>`<div class="rad${i>=r.length-Math.max(ny,0)?" ny":""}">${esc(l)}</div>`).join("");
sist=r;const k=document.getElementById("kod");k.scrollTop=k.scrollHeight}}catch(e){}}
const sat=(id,t,r)=>{const e=document.getElementById(id);if(!e)return;e.setAttribute("data-tip",t);if(r)e.classList.add("tip-r")};
const FAS={kodar:"Aider skriver kod för en uppgift.",testar:"Koden är skriven och testerna körs.",pushar:"Resultatet skickas till GitHub som en PR.",klar:"Uppgiften är klar och inskickad.",vilar:"Ingen uppgift pågår. Modellen väntar på nästa i kön.",fel:"Körningen avbröts. Orsaken står bredvid.","okänd":"Ingen körning har skrivit status än."};
const fasTip=f=>"LOKAL MODELL (Aider på din dator)\nJust nu: "+(f||"okänd").toUpperCase()+". "+(FAS[f]||FAS["okänd"])+"\n\nAlla lägen:\n"+Object.entries(FAS).map(([k,v])=>"• "+k.toUpperCase()+": "+v).join("\n");
const KON_TIP="KÖN\nVisar om arbetsflödet rör sig.\n\n• AKTIV: något har hänt på GitHub eller lokalt de senaste 30 minuterna, eller den lokala modellen kodar just nu.\n• STÅR STILL: inget har hänt på 30 minuter, modellen kodar inte, och något väntar (uppgifter i kön eller en öppen PR) eller releasen är inte klar. Orsaken står på raden under.";
const BYGGE_TIP="BYGGE MAIN\nGitHub kompilerar appen och kör alla tester varje gång något hamnar på main.\n\n• GRÖNT: bygget och alla tester gick igenom.\n• PÅGÅR: bygget körs just nu och tar några minuter. Inget att göra, bara vänta.\n• RÖTT: bygget eller ett test föll. Något på main är trasigt och Claude behöver rätta det.\n• OKÄNT: panelen kunde inte läsa byggstatus (GitHub svarar inte, eller gh är utloggad).";
const VANTAR_TIP="VÄNTAR PÅ DIG\nSådant som bara du kan göra. Listan finns i rutan Kö och PR.\n\n• MERGA: en PR från Claude som väntar på din merge på GitHub.\n• FRÅGA: en öppen fråga i FRAGOR.md som Claude behöver svar på.\n• DIN TUR: ett steg mot release där du är näst på tur, till exempel att prova appen på telefonen eller Play Console.\n• Inget väntar på dig: du behöver inte göra något.";
const TIPS={"h-matare":"MÄTARE\nHur långt Garantivalvet 1.0 har kommit, 0 till 100.\n\nVarje steg har en vikt efter uppskattad arbetsinsats (summan är 100). Klara steg räknas helt. Steg som pågår räknas efter andelen klara uppgifter.","malrubrik":"BERÄKNAT RELEASEDATUM\nByggfarten är klarad vikt per dygn sedan start. Det som återstår av byggarbetet delas med farten.\n\nSedan läggs dygn till för externa steg: dina tester på telefon och Googles granskning (antaganden i release.json).\n\nSpannet är farten gånger 1,5 (tidigast) och farten delad på 2 (senast). Det är en uppskattning, inte ett löfte.","h-tokens":"TOKENS\nRäknas bara för den lokala modellen, ur Aiders rader i körloggarna (skickat plus mottaget). Aider avrundar till tusental, så siffrorna är ungefärliga.\n\n• TOTALT: sedan första loggen.\n• IDAG: sedan midnatt.\n• DENNA VECKA: sedan måndag.\n• PER SEKUND: snitt över körtiden (inklusive test och git), ingen live-mätare.\n\nCursor och Claude mäts inte: det finns ingen datakälla.","h-dator":"DATORN OCH MODELLEN\n• GPU: last och temperatur från nvidia-smi.\n• VRAM: använt minne på grafikkortet av totalt.\n• OLLAMA: vilken modell som ligger laddad. \"Svarar inte\" betyder att Ollama inte körs.\n• KLARA IDAG: lokala uppgifter som mergats sedan midnatt.\n\nSaknas raden finns ingen mätning (till exempel inget NVIDIA-kort)."};
for(const [id,tx] of Object.entries(TIPS))sat(id,tx,id==="h-tokens"||id==="h-dator");
sat("kon",KON_TIP);sat("bygge",BYGGE_TIP);sat("vchip",VANTAR_TIP);sat("fas",fasTip("okänd"));sat("anv","ANVÄNDNING\nÖppnar sidan som visar när GPU:n var aktiv och inte, och vem som höll bollen när modellen väntade. Mäts var 15:e sekund och sparas över tid.\n\nPekar du på en ruta i den sidan ser du exakt vad som hände.");
let klarIdag=0,senasteDator=null,gpuIdag="";
const tidhm=s=>{const m=Math.floor(Math.round(s)/60),h=Math.floor(m/60);return h?h+" h "+String(m%60).padStart(2,"0")+" min":m+" min"};
async function anv(){try{const d=await (await fetch("/api/anvandning?dagar=1")).json();const x=d.dagar[0];gpuIdag=x&&x.matt_s>0?"GPU aktiv idag: "+tidhm(x.kat.aktiv)+" ("+Math.round(100*x.kat.aktiv/x.matt_s)+" %)":"GPU aktiv idag: ingen mätning än";dator()}catch(e){}}
function dator(x){if(x!==undefined)senasteDator=x;const d=senasteDator||{};const g=d.gpu;const rader=[];
rader.push(g?`GPU ${g.last} % · ${g.temp} °C`:"GPU: ingen mätning");
if(g)rader.push(`VRAM ${(g.vram/1024).toLocaleString("sv-SE",{maximumFractionDigits:1})} av ${(g.vram_max/1024).toLocaleString("sv-SE",{maximumFractionDigits:1})} GB`);
rader.push(d.ollama==null?"Ollama svarar inte":d.ollama.length?"Ollama: "+d.ollama.map(esc).join(", ")+" i minnet":"Ollama: ingen modell laddad");
if(gpuIdag)rader.push(gpuIdag);rader.push("Klara idag: "+klarIdag);
document.getElementById("dator").innerHTML=rader.map(r=>`<div>${r}</div>`).join("")}
const sv=(n,dec)=>Number(n).toLocaleString("sv-SE",{maximumFractionDigits:dec||0});
const dag=s=>new Date(s+"T12:00").toLocaleDateString("sv-SE",{weekday:"long",day:"numeric",month:"long"});
const kort=s=>new Date(s+"T12:00").toLocaleDateString("sv-SE",{day:"numeric",month:"short"});
function release(r){if(!r)return;
document.getElementById("bage").setAttribute("stroke-dasharray",r.procent+" 100");
document.getElementById("proc").textContent=r.procent;
document.getElementById("malrubrik").textContent="RELEASE // "+r.mal.toUpperCase();
document.getElementById("eta").textContent=r.klar?"Klart för release":"Beräknad release: "+dag(r.eta);
document.getElementById("spann").textContent=r.klar?"":`Spann ${kort(r.eta_tidigast)} till ${kort(r.eta_senast)}, med byggfart ${sv(r.fart,1)} poäng per dygn plus ${r.externa_dagar} dygn för test och Googles granskning.`;
document.getElementById("steg").innerHTML=r.steg.map(s=>{const m=s.status==="klar"?"[x]":s.status==="pagar"?`[${Math.round(s.andel*100)}%]`:"[ ]";
return `<li class="${s.status}">${m} ${esc(s.namn)}${s.extern?" (extern)":""}</li>`}).join("");
document.getElementById("anm").textContent=""}
async function tokens(){try{const d=await (await fetch("/api/tokens")).json();
document.getElementById("t-totalt").textContent=sv(d.totalt);document.getElementById("t-idag").textContent=sv(d.idag);
document.getElementById("t-vecka").textContent=sv(d.vecka);
document.getElementById("t-sek").textContent=d.per_sek==null?"-":sv(d.per_sek,1);
document.getElementById("t-not").textContent=`${d.korningar} körningar`+(d.per_sek_senaste!=null?`, senaste ${sv(d.per_sek_senaste,1)} per sekund`:"")}catch(e){}}
function chips(d){const kon=document.getElementById("kon"),larm=document.getElementById("larm");
if(d.stilla){kon.textContent="Kön står still "+d.stilla.min+" min";kon.className="chip fel";larm.textContent=d.stilla.orsak;sat("kon",KON_TIP+"\n\nJust nu: STÅR STILL i "+d.stilla.min+" min. "+d.stilla.orsak)}
else{kon.textContent="Kön aktiv";kon.className="chip ok";larm.textContent="";sat("kon",KON_TIP+"\n\nJust nu: AKTIV.")}
const b=document.getElementById("bygge"),x=d.bygge;
const nu=s=>BYGGE_TIP+"\n\nJust nu: "+s+".";
if(!x){b.textContent="Bygge okänt";b.className="chip";sat("bygge",nu("OKÄNT"))}
else if(x.status!=="completed"){b.textContent="Bygge main pågår";b.className="chip var";sat("bygge",nu("PÅGÅR"))}
else if(x.slutsats==="success"){b.textContent="Bygge main grönt";b.className="chip ok";sat("bygge",nu("GRÖNT"))}
else{b.textContent="Bygge main RÖTT";b.className="chip fel";sat("bygge",nu("RÖTT"))}
const v=document.getElementById("vchip"),n=(d.vantar||[]).length;
v.textContent=n?"Väntar på dig: "+n:"Inget väntar på dig";v.className="chip "+(n?"var":"ok");sat("vchip",VANTAR_TIP+"\n\nJust nu: "+(n?n+" sak(er) väntar, se rutan Kö och PR.":"inget väntar."));
klarIdag=d.klar_idag||0;dator()}
async function github(){try{const d=await (await fetch("/api/github")).json();release(d.release);chips(d);
document.getElementById("handelser").innerHTML=d.handelser.map(h=>`<li class="${kls(h.vem)}"><span class="tag">${vemNamn[h.vem]}</span><span class="liten">${new Date(h.tid).toLocaleString("sv-SE",{dateStyle:"short",timeStyle:"short"})}</span> ${esc(h.text)}</li>`).join("");
document.getElementById("cursornu").innerHTML=(d.cursor||[]).length?d.cursor.map(h=>`<li class="cursor-a"><span class="liten">sedan ${new Date(h.tid).toLocaleTimeString("sv-SE",{timeStyle:"short"})}</span> ${esc(h.text)}</li>`).join(""):"<li class='liten'>Vilar. Inget skickat till Cursor just nu.</li>";
document.getElementById("fragor").innerHTML=(d.vantar||[]).length?d.vantar.map(f=>`<li class="vantar"><b>${esc(f.typ)}</b>${esc(f.text)}</li>`).join(""):"<li class='liten'>Inget</li>";
document.getElementById("ko").innerHTML=d.ko.length?d.ko.map(k=>`<li>${esc(k.replace(".md",""))}</li>`).join(""):"<li class='liten'>Tom</li>";
document.getElementById("prs").innerHTML=d.prs.map(p=>`<li class="${kls(p.vem)}"><span class="tag">#${p.nr}</span>${esc(p.titel)} <span class="liten">(${p.lage})</span></li>`).join("")}catch(e){}}
let ver=null;async function version(){try{const v=(await (await fetch("/api/version")).json()).v;if(ver&&v!==ver)location.reload();ver=v}catch(e){}}
version();setInterval(version,5000);
lokal();github();tokens();anv();setInterval(anv,60000);setInterval(lokal,2000);setInterval(github,30000);setInterval(tokens,5000);
</script></body></html>"""


# ---- Användning: GPU och väntetid över tid ---------------------------------------------------
# Panelen mäter GPU:n var 15:e sekund och sparar raden i %LOCALAPPDATA%\appfabriken\anvandning.csv.
# Sammanställningen görs vid läsning, så att reglerna för vad som räknas som aktivitet och väntan
# kan förbättras i efterhand på redan sparade prover.

ANV = DATA / "anvandning.csv"
ANV_RUBRIK = "t;gpu;vram;fas;fas_min;ollama;bollen;ko"
PROV_SEK = 15      # hur ofta GPU:n mäts
GPU_AKTIV = 15     # procent last som räknas som aktiv
STALE_MIN = 45     # en status kodar/testar/pushar som är äldre än så anses övergiven
LUCKA_SEK = 60     # längsta intervall ett prov står för; resten räknas som "ej mätt"
KATEGORIER = ["aktiv", "utan_gpu", "cursor", "claude", "mattias", "av", "lokal_vilar", "ovrigt"]
VILA_FRAS = {
    "cursor": "väntan på Cursor",
    "claude": "väntan på Claude",
    "mattias": "väntan på dig",
    "av": "tid då körslingan var av (datorn i bruk eller fönstret stängt)",
    "lokal_vilar": "tid då modellen vilade fast kön hade uppgifter",
    "ovrigt": "övrigt (fel, annan GPU-användning, okänd orsak)",
}
_anv_lock = threading.Lock()
_bollen = {"v": "okand", "ko": 0, "tid": 0.0}
_anv_cache = {"nyckel": None, "v": None}


def bollen_av(d):
    """Vem håller bollen när den lokala modellen inte har något att göra, ur GitHub-datat."""
    if not d or not d.get("aktivitet"):  # tomt data betyder att gh inte svarade
        return "okand"
    arbete = d.get("arbete") or []
    if any(p["lage"].startswith("KROCKAR") for p in arbete):
        return "claude"
    if arbete or d.get("cursor"):
        return "cursor"
    if d.get("ko"):
        return "lokal"
    rel = d.get("release") or {}
    if rel.get("klar") or any(v["typ"] == "DIN TUR" for v in d.get("vantar") or []):
        return "mattias"
    return "claude"


def klassa(p):
    """En kategori för ett prov (t, gpu, vram, fas, fas_min, ollama, bollen, ko)."""
    t, gpu, vram, fas, fas_min, oll, bollen, ko = p
    jobb = fas in ("kodar", "testar", "pushar")
    arbetar = jobb and fas_min <= STALE_MIN
    if gpu is not None and gpu >= GPU_AKTIV:
        return "aktiv" if (arbetar or oll == 1) else "ovrigt"  # last utan modellen: något annat använder GPU:n
    if arbetar:
        return "aktiv" if (gpu is None and fas == "kodar") else "utan_gpu"
    if (jobb and fas_min > STALE_MIN) or fas == "okand" or (fas in ("vilar", "klar") and fas_min > 10) \
            or (fas == "fel" and fas_min > 40):
        return "av"  # körslingan skriver status var 3:e minut; tystnad betyder att den inte körs
    if fas == "fel":
        return "ovrigt"
    return {"cursor": "cursor", "claude": "claude", "mattias": "mattias",
            "lokal": "lokal_vilar"}.get(bollen, "ovrigt")


def _fas_nu():
    try:
        s = json.loads(STATUS.read_text(encoding="utf-8-sig"))
        alder = int((time.time() - STATUS.stat().st_mtime) / 60)
        return str(s.get("fas", "okand")).replace("ä", "a").replace("å", "a").replace("ö", "o").replace(";", ","), alder
    except Exception:
        return "okand", 0


def prov_rad(nu=None):
    nu = nu or time.time()
    d = dator()
    g = d.get("gpu")
    fas, alder = _fas_nu()
    oll = "" if d.get("ollama") is None else int(bool(d["ollama"]))
    return ";".join(str(x) for x in (int(nu), g["last"] if g else "", g["vram"] if g else "", fas, alder, oll,
                                     _bollen["v"], _bollen["ko"]))


def skriv_prov(rad):
    with _anv_lock:
        ny = not ANV.exists() or ANV.stat().st_size == 0
        ANV.parent.mkdir(parents=True, exist_ok=True)
        with open(ANV, "a", encoding="utf-8", newline="\n") as f:
            if ny:
                f.write(ANV_RUBRIK + "\n")
            f.write(rad + "\n")


def rensa_gammalt(dagar=90, grans_mb=20):
    """Filen växer cirka 0,4 MB per dygn. Över grans_mb behålls bara de senaste dagarna."""
    try:
        if ANV.stat().st_size < grans_mb * 1024 * 1024:
            return
        grans = time.time() - dagar * 86400
        with _anv_lock:
            rader = ANV.read_text(encoding="utf-8").splitlines()
            behall = [rader[0]] + [r for r in rader[1:] if r.split(";", 1)[0].isdigit()
                                   and int(r.split(";", 1)[0]) >= grans]
            tmp = ANV.with_suffix(".tmp")
            tmp.write_text("\n".join(behall) + "\n", encoding="utf-8")
            tmp.replace(ANV)
    except Exception:
        pass


def las_prov(sokvag=None, sedan=0):
    ut = []
    try:
        text = (sokvag or ANV).read_text(encoding="utf-8", errors="replace")
    except OSError:
        return ut
    for rad in text.splitlines()[1:]:
        a = rad.split(";")
        if len(a) != 8 or not a[0].isdigit() or int(a[0]) < sedan:
            continue
        try:
            ut.append((int(a[0]), int(a[1]) if a[1] else None, int(a[2]) if a[2] else None, a[3], int(a[4] or 0),
                       None if a[5] == "" else int(a[5]), a[6], int(a[7] or 0)))
        except ValueError:
            continue
    return ut


def _hm(s):
    m = int(round(s)) // 60
    h, m = divmod(m, 60)
    return f"{h} h {m:02d} min" if h else f"{m} min"


def anvandning_insikter(kat, matt):
    if matt <= 0:
        return []
    jobb = kat["aktiv"] + kat["utan_gpu"]
    ut = [f"Modellen arbetade {_hm(jobb)} av {_hm(matt)} mätt tid ({round(100 * jobb / matt)} %). "
          f"GPU:n var aktiv {_hm(kat['aktiv'])} ({round(100 * kat['aktiv'] / matt)} %)."]
    vila = {k: kat[k] for k in VILA_FRAS}
    k, s = max(vila.items(), key=lambda x: x[1])
    if s >= 300:
        ut.append(f"Störst del av stilleståndet: {VILA_FRAS[k]}, {_hm(s)} ({round(100 * s / matt)} % av tiden).")
    if kat["utan_gpu"] >= 600 and kat["utan_gpu"] >= 0.2 * jobb:
        ut.append(f"{_hm(kat['utan_gpu'])} av arbetstiden gick åt till test, bygge och git medan GPU:n vilade.")
    return ut


def anvandning_sammanstall(prov, nu, dagar=7):
    """Dygnsvis sammanställning. Varje dygn har 96 block om 15 minuter; ett block är
    [mätta sekunder, sekunder per kategori i ordningen KATEGORIER]."""
    dagar = max(1, min(int(dagar), 90))
    idag0 = datetime.fromtimestamp(nu).replace(hour=0, minute=0, second=0, microsecond=0)
    start0 = idag0 - timedelta(days=dagar - 1)
    prov = sorted((p for p in prov if p[0] >= start0.timestamp()), key=lambda p: p[0])
    dag = {}
    for i in range(dagar):
        d0 = start0 + timedelta(days=i)
        dag[d0.date().isoformat()] = {"datum": d0.date().isoformat(), "matt_s": 0, "ej_matt_s": 0,
                                      "kat": {k: 0 for k in KATEGORIER},
                                      "b": [[0] * (1 + len(KATEGORIER)) for _ in range(96)], "_f": None, "_l": None}
    stilla, aktuell, gpu_prov = [], None, 0
    for i, p in enumerate(prov):
        t = p[0]
        dt = max(0, min((prov[i + 1][0] if i + 1 < len(prov) else nu) - t, LUCKA_SEK))
        k = klassa(p)
        lt = datetime.fromtimestamp(t)
        d = dag.get(lt.date().isoformat())
        if d is None:
            continue
        ki = KATEGORIER.index(k)
        d["kat"][k] += dt
        d["matt_s"] += dt
        bi = min(95, (lt.hour * 3600 + lt.minute * 60 + lt.second) // 900)
        d["b"][bi][0] += dt
        d["b"][bi][1 + ki] += dt
        d["_f"] = t if d["_f"] is None else d["_f"]
        d["_l"] = t + dt
        gpu_prov += p[1] is not None
        ledig = k not in ("aktiv", "utan_gpu")
        if ledig and aktuell and t - aktuell["sist"] <= LUCKA_SEK:
            aktuell["sist"], aktuell["slut"] = t, t + dt
            aktuell["per"][k] += dt
        else:
            if aktuell:
                stilla.append(aktuell)
            aktuell = None
            if ledig:
                aktuell = {"start": t, "sist": t, "slut": t + dt, "per": {x: 0 for x in KATEGORIER}}
                aktuell["per"][k] += dt
    if aktuell:
        stilla.append(aktuell)
    summa = {k: 0 for k in KATEGORIER}
    lista = []
    for d in sorted(dag.values(), key=lambda x: x["datum"], reverse=True):
        if d["_f"] is not None:
            d["ej_matt_s"] = max(0, (d["_l"] - d["_f"]) - d["matt_s"])
        del d["_f"], d["_l"]
        for k in KATEGORIER:
            summa[k] += d["kat"][k]
        lista.append(d)
    matt = sum(summa.values())
    stilla = [{"start": s["start"], "slut": s["slut"], "sek": sum(s["per"].values()),
               "orsak": max(s["per"], key=s["per"].get)} for s in stilla if sum(s["per"].values()) >= 600]
    stilla.sort(key=lambda s: s["sek"], reverse=True)
    return {"nu": int(nu), "dagar": lista, "kategorier": KATEGORIER,
            "summa": {"matt_s": matt, "ej_matt_s": sum(d["ej_matt_s"] for d in lista), "kat": summa},
            "stillestand": stilla[:6], "insikt": anvandning_insikter(summa, matt),
            "gpu_andel": round(gpu_prov / len(prov), 2) if prov else None}


def anvandning_api(dagar):
    nu = time.time()
    try:
        st = ANV.stat()
        sig = (st.st_mtime, st.st_size)
    except OSError:
        sig = None
    nyckel = (dagar, sig, int(nu // 10))
    if _anv_cache["nyckel"] != nyckel:
        v = anvandning_sammanstall(las_prov(sedan=nu - (dagar + 1) * 86400), nu, dagar)
        _anv_cache.update(nyckel=nyckel, v=v)
    return _anv_cache["v"]


def provtagning():
    rensa_gammalt()
    while True:
        try:
            skriv_prov(prov_rad())
        except Exception:
            pass
        time.sleep(PROV_SEK)


def bollen_traad():
    """Uppdaterar vem som håller bollen var 2:a minut, i en egen tråd så att proverna inte fördröjs av gh."""
    while True:
        try:
            d = hamta_github()
            _bollen.update(v=bollen_av(d), ko=len(d.get("ko") or []), tid=time.time())
        except Exception:
            _bollen.update(v="okand", tid=time.time())
        time.sleep(120)


ANVANDNING_SIDA = r"""<!doctype html><html lang="sv"><head><meta charset="utf-8">
<title>Appfabriken // Användning</title><meta name="viewport" content="width=device-width,initial-scale=1">
<style>
:root{--bg:#000803;--panel:#03140a;--line:#0a5f2a;--g:#00ff66;--ink:#c8f7d8;--mut:#7fb895;--on:#00140a;
--aktiv:#17a750;--cursor:#0099e3;--claude:#d46d00;--mattias:#a56cde;--ovrigt:#6f7f76;
--s0:#172c1c;--s1:#235430;--s2:#287c42;--s3:#17a750;--s4:#49de78}
*{box-sizing:border-box}
body{margin:0;background:var(--bg);color:var(--ink);font:14px/1.5 Consolas,"Cascadia Mono",monospace;padding:20px 24px 64px}
a{color:#7fd7ff}h1{margin:0;font-size:18px;letter-spacing:.2em;color:var(--g);text-shadow:0 0 8px var(--g)}
h2{margin:30px 0 10px;font-size:13px;letter-spacing:.15em;color:var(--mut);font-weight:normal}
.topp{display:flex;gap:14px;align-items:center;flex-wrap:wrap}
.chip{padding:2px 10px;border:1px solid var(--line);color:var(--mut);text-decoration:none;text-transform:uppercase;font-size:13px}
.chip:hover,.chip.vald{border-color:var(--g);color:var(--g)}.chip.vald{box-shadow:0 0 10px rgba(0,255,102,.3)}
.liten{color:var(--mut);font-size:12px}
.kpi{display:grid;grid-template-columns:repeat(auto-fit,minmax(230px,1fr));gap:12px;margin-top:18px}
.kpi>div{background:var(--panel);border:1px solid var(--line);padding:12px 14px}
.kpi .v{font-size:30px;line-height:1.2;color:var(--ink)}.kpi .hero .v{font-size:48px;color:var(--g);text-shadow:0 0 10px rgba(0,255,102,.5)}
.kpi .l{color:var(--mut);font-size:12px;letter-spacing:.08em;text-transform:uppercase}
ul{margin:8px 0 0;padding:0;list-style:none}li{margin:0 0 4px}
.rad{display:grid;grid-template-columns:104px 1fr 112px;gap:10px;align-items:center;margin:0 0 6px}
.dl{color:var(--mut);white-space:nowrap}.ds{color:var(--ink);text-align:right;white-space:nowrap}
.ticks{position:relative;height:16px;margin-bottom:2px}.ticks span{position:absolute;top:0;font-size:11px;color:var(--mut);transform:translateX(-50%)}
.celler{display:grid;grid-template-columns:repeat(96,1fr);gap:2px}
.c{height:24px;border-radius:2px;min-width:0}.c:hover{outline:1px solid var(--ink);outline-offset:1px}
.l0{background:var(--s0)}.l1{background:var(--s1)}.l2{background:var(--s2)}.l3{background:var(--s3)}.l4{background:var(--s4)}
.n{background:transparent;border:1px dashed #1c3d29}
.leg{display:flex;gap:6px;align-items:center;flex-wrap:wrap;margin-top:10px;font-size:12px;color:var(--mut)}
.sw{display:inline-block;width:18px;height:12px;border-radius:2px;vertical-align:middle}
.sb{display:flex;height:28px}
.sg{height:100%;display:flex;align-items:center;overflow:hidden;white-space:nowrap;padding:0 6px;font-size:11px;color:var(--on);margin-right:2px;min-width:0}
.sg:first-child{border-radius:4px 0 0 4px}.sg:last-child{border-radius:0 4px 4px 0;margin-right:0}.sg:only-child{border-radius:4px}
.sg:hover{filter:brightness(1.25)}
.rand{background:repeating-linear-gradient(135deg,var(--aktiv) 0 5px,#08301a 5px 10px)}
.rand b{background:var(--bg);color:var(--ink);font-weight:normal;padding:0 4px;border-radius:2px}
.tom{background:transparent;border:1px dashed #1c3d29;color:var(--mut)}
table{border-collapse:collapse;width:100%;font-size:12px}#still table{max-width:760px}#still th,#still td{text-align:left}th,td{padding:4px 8px;border-bottom:1px solid #0b2a17;text-align:right;white-space:nowrap}
th{color:var(--mut);font-weight:normal}th:first-child,td:first-child{text-align:left}
details{margin-top:14px}summary{cursor:pointer;color:var(--mut)}summary:hover{color:var(--g)}
#tt{position:fixed;z-index:50;pointer-events:none;background:#001a08;border:1px solid var(--g);padding:8px 10px;font-size:12px;max-width:340px;box-shadow:0 0 18px rgba(0,255,102,.35);display:none}
#tt .v{font-size:15px;color:var(--ink)}#tt .r{display:flex;gap:8px;align-items:center;margin-top:2px;color:var(--mut)}
#tt .k{width:12px;height:0;border-top:3px solid}
.varn{border:1px solid var(--claude);color:var(--ink);padding:8px 12px;margin-top:14px}
@media(max-width:760px){.rad{grid-template-columns:70px 1fr 70px}.sg{font-size:10px}}
</style></head><body>
<div class="topp"><h1>ANVÄNDNING // GPU OCH VÄNTETID</h1>
<a class="chip" href="/">Panelen</a><span id="val"></span><a class="chip" href="/anvandning.csv">Ladda ner CSV</a></div>
<div class="liten" style="margin-top:6px" id="ram"></div>
<div id="varn"></div>
<div class="kpi" id="kpi"></div>
<h2>SLUTSATSER</h2><ul id="insikt"></ul>
<h2>NÄR GPU:N VAR AKTIV // ETT BLOCK = 15 MINUTER</h2>
<div id="hm"></div>
<div class="leg" id="hmleg"></div>
<h2>VAR TIDEN GICK // ANDEL AV MÄTT TID PER DYGN</h2>
<div class="leg" id="sbleg" style="margin:0 0 10px"></div>
<div id="sb"></div>
<h2>LÄNGSTA STILLESTÅND</h2><div id="still"></div>
<details><summary>Visa alla siffror som tabell</summary><div style="overflow-x:auto;margin-top:8px"><table id="tab"></table></div></details>
<h2>SÅ MÄTS DET</h2>
<div class="liten" style="max-width:900px">GPU:n läses var 15:e sekund (nvidia-smi) av panelen och sparas på din dator. Last från 15 % räknas som aktiv, om modellen kodar eller Ollama har den laddad (annars räknas det som annan GPU-användning). Arbetar körslingan men GPU:n vilar (test, bygge, git) räknas det som egen kategori. Står modellen still avgörs vem som håller bollen ur GitHub: öppna PR:er och Cursors arbete betyder Cursor, tom kö utan PR betyder att Claude måste skriva nästa uppdrag, och återstår bara dina steg räknas det på dig. Utebliven statusuppdatering från körslingan i mer än 10 minuter räknas som att den är av (datorn i bruk eller fönstret stängt). Luckor över 60 sekunder mellan prover är Ej mätt, till exempel när datorn sov eller panelen var stängd. Bollhållaren uppdateras var 2:a minut.</div>
<div id="tt"></div>
<script>
const KAT={aktiv:"GPU aktiv",utan_gpu:"Arbetar, GPU vilar (test, bygge, git)",cursor:"Väntar på Cursor",claude:"Väntar på Claude",mattias:"Väntar på dig (ditt steg)",av:"Körslingan av (datorn i bruk eller fönstret stängt)",lokal_vilar:"Modellen vilar fast kön har uppgift",ovrigt:"Övrigt (fel, annan GPU-användning, okänt)"};
const SEG=[{n:"GPU aktiv",k:["aktiv"],c:"var(--aktiv)"},{n:"Arbetar, GPU vilar",k:["utan_gpu"],c:"var(--aktiv)",rand:1},{n:"Väntar på Cursor",k:["cursor"],c:"var(--cursor)"},{n:"Väntar på Claude",k:["claude"],c:"var(--claude)"},{n:"Väntar på dig",k:["mattias","av"],c:"var(--mattias)"},{n:"Övrigt",k:["lokal_vilar","ovrigt"],c:"var(--ovrigt)"}];
const $=id=>document.getElementById(id);
const hm=s=>{s=Math.round(s);const m=Math.floor(s/60),h=Math.floor(m/60);return h?h+" h "+String(m%60).padStart(2,"0")+" min":m+" min"};
const pct=(a,b)=>b>0?Math.round(100*a/b)+" %":"-";
const dagnamn=d=>new Date(d+"T12:00").toLocaleDateString("sv-SE",{weekday:"short",day:"numeric",month:"short"});
const klock=t=>new Date(t*1000).toLocaleTimeString("sv-SE",{hour:"2-digit",minute:"2-digit"});
const DAGAR=Math.min(Math.max(parseInt(new URLSearchParams(location.search).get("dagar")||"7",10)||7,1),90);
function el(t,c,x){const e=document.createElement(t);if(c)e.className=c;if(x!=null)e.textContent=x;return e}
function niva(f){return f<0.05?0:f<0.25?1:f<0.5?2:f<0.75?3:4}
const tt=$("tt");
function visa(e,rader,titel,varde){tt.replaceChildren();tt.appendChild(el("div","liten",titel));tt.appendChild(el("div","v",varde));
for(const [n,v,c] of rader){const r=el("div","r");const k=el("span","k");k.style.borderColor=c;r.appendChild(k);r.appendChild(el("span","",n+": "+v));tt.appendChild(r)}
tt.style.display="block";const w=tt.offsetWidth,h=tt.offsetHeight;let x=e.clientX+14,y=e.clientY+14;
if(x+w>innerWidth-8)x=e.clientX-w-14;if(y+h>innerHeight-8)y=e.clientY-h-14;tt.style.left=Math.max(4,x)+"px";tt.style.top=Math.max(4,y)+"px"}
function dold(){tt.style.display="none"}
function segtider(kat){return SEG.map(s=>({s,sek:s.k.reduce((a,k)=>a+(kat[k]||0),0)}))}
function bygg(d){
$("ram").textContent="Senaste "+(DAGAR===1?"dygnet":DAGAR+" dygnen")+". Uppdateras var 30:e sekund.";
const v=$("val");v.replaceChildren();for(const [n,t] of [[1,"Idag"],[7,"7 dagar"],[14,"14 dagar"],[30,"30 dagar"]]){const a=el("a","chip"+(n===DAGAR?" vald":""),t);a.href="?dagar="+n;v.appendChild(a);v.appendChild(document.createTextNode(" "))}
const S=d.summa,idag=d.dagar[0];
$("varn").replaceChildren();
if(S.matt_s===0){$("varn").appendChild(el("div","varn","Ingen mätning ännu. Panelen mäter var 15:e sekund så länge den körs. Kom tillbaka om en stund."))}
else if(d.gpu_andel!=null&&d.gpu_andel<0.5)$("varn").appendChild(el("div","varn","GPU-mätning saknas för det mesta (nvidia-smi svarade inte). Aktiv tid räknas då från körslingans status i stället."));
const kpi=$("kpi");kpi.replaceChildren();
const kort=(l,v,u,h)=>{const x=el("div",h?"hero":"");x.appendChild(el("div","l",l));x.appendChild(el("div","v",v));if(u)x.appendChild(el("div","liten",u));kpi.appendChild(x)};
kort("GPU aktiv idag",hm(idag.kat.aktiv),pct(idag.kat.aktiv,idag.matt_s)+" av "+hm(idag.matt_s)+" mätt tid",1);
const dm=d.dagar.filter(x=>x.matt_s>0);
kort("Snitt per mätt dag",dm.length?hm(dm.reduce((a,x)=>a+x.kat.aktiv,0)/dm.length):"-","GPU aktiv, "+dm.length+" dygn med mätning");
kort("Arbetar utan GPU",hm(S.kat.utan_gpu),"test, bygge och git, i perioden");
const vila=SEG.slice(2).map(s=>({s,sek:s.k.reduce((a,k)=>a+S.kat[k],0)})).sort((a,b)=>b.sek-a.sek)[0];
kort("Mest väntan",S.matt_s>0&&vila.sek>0?vila.s.n.replace("Väntar på ",""):"-",S.matt_s>0&&vila.sek>0?hm(vila.sek)+" i perioden":"");
const ul=$("insikt");ul.replaceChildren();(d.insikt.length?d.insikt:["Inga slutsatser ännu."]).forEach(t=>ul.appendChild(el("li","",t)));
// värmerad
const hmEl=$("hm");hmEl.replaceChildren();
const huvud=el("div","rad");huvud.appendChild(el("span"));const tk=el("div","ticks");
for(let h=0;h<24;h+=3){const s=el("span","",String(h).padStart(2,"0"));s.style.left=(h/24*100)+"%";tk.appendChild(s)}
huvud.appendChild(tk);huvud.appendChild(el("span"));hmEl.appendChild(huvud);
d.dagar.forEach((dg,di)=>{const r=el("div","rad");r.appendChild(el("span","dl",dagnamn(dg.datum)));
const c=el("div","celler");
dg.b.forEach((b,bi)=>{const x=el("div","c");const m=b[0];
if(m<120)x.className="c n";else x.className="c l"+niva(b[1]/m);x.dataset.d=di;x.dataset.b=bi;c.appendChild(x)});
r.appendChild(c);r.appendChild(el("span","ds",dg.matt_s?hm(dg.kat.aktiv):"-"));hmEl.appendChild(r)});
hmEl.onmousemove=e=>{const x=e.target;if(!x.dataset||x.dataset.b===undefined){dold();return}
const dg=d.dagar[+x.dataset.d],bi=+x.dataset.b,b=dg.b[bi];const h0=String(Math.floor(bi/4)).padStart(2,"0")+":"+String((bi%4)*15).padStart(2,"0");
const h1=String(Math.floor((bi+1)/4)%24).padStart(2,"0")+":"+String(((bi+1)%4)*15).padStart(2,"0");
if(b[0]<120){visa(e,[],dagnamn(dg.datum)+" "+h0+"-"+h1,"Ej mätt");return}
const kat={};d.kategorier.forEach((k,i)=>kat[k]=b[1+i]);
visa(e,segtider(kat).filter(x=>x.sek>0).map(x=>[x.s.n,pct(x.sek,b[0])+" ("+hm(x.sek)+")",x.s.c]),dagnamn(dg.datum)+" "+h0+"-"+h1,"GPU aktiv "+pct(kat.aktiv,b[0]))};
hmEl.onmouseleave=dold;
const lg=$("hmleg");lg.replaceChildren(el("span","","GPU aktiv av mätt tid:"));
["0","<25 %","<50 %","<75 %",">75 %"].forEach((t,i)=>{const s=el("span","sw l"+i);lg.appendChild(s);lg.appendChild(el("span","",t))});
const nn=el("span","sw n");lg.appendChild(nn);lg.appendChild(el("span","","Ej mätt"));
// staplar
const sl=$("sbleg");sl.replaceChildren();
SEG.forEach(s=>{const sw=el("span","sw"+(s.rand?" rand":""));sw.style.background=s.rand?"":s.c;sl.appendChild(sw);sl.appendChild(el("span","",s.n+"  "))});
const sb=$("sb");sb.replaceChildren();
const sett=(g,tx,rand)=>{g.replaceChildren();if(!tx)return;if(rand)g.appendChild(el("b","",tx));else g.textContent=tx};
const rad=(namn,kat,matt,ekstra)=>{const r=el("div","rad");r.appendChild(el("span","dl",namn));const b=el("div","sb");
if(matt<=0){b.appendChild(el("div","sg tom","Ingen mätning"));b.firstChild.style.flex="1"}
else segtider(kat).forEach(x=>{if(x.sek<=0)return;const g=el("div","sg"+(x.s.rand?" rand":""));g.style.flex="0 0 calc("+(100*x.sek/matt)+"% - 2px)";
if(!x.s.rand)g.style.background=x.s.c;const andel=100*x.sek/matt;
g._f=[x.s.n+" "+hm(x.sek),hm(x.sek),""];g._r=!!x.s.rand;
g.onmousemove=e=>{const sub=x.s.k.map(k=>[KAT[k],hm(kat[k]||0),x.s.c]).filter(z=>z[1]!=="0 min"||x.s.k.length===1);visa(e,x.s.k.length>1?sub:[],namn,x.s.n+" "+pct(x.sek,matt)+" ("+hm(x.sek)+")")};
g.onmouseleave=dold;b.appendChild(g)});
r.appendChild(b);r.appendChild(el("span","ds",ekstra));sb.appendChild(r);
b.querySelectorAll(".sg").forEach(g=>{if(!g._f)return;for(const tx of g._f){sett(g,tx,g._r);if(g.scrollWidth<=g.clientWidth+1)break}})};
rad("Perioden",S.kat,S.matt_s,hm(S.matt_s));
d.dagar.forEach(dg=>rad(dagnamn(dg.datum),dg.kat,dg.matt_s,dg.matt_s?hm(dg.matt_s):"-"));
// stillestånd
const st=$("still");st.replaceChildren();
if(!d.stillestand.length)st.appendChild(el("div","liten","Inga stillestånd över 10 minuter i perioden."));
else{const t=el("table");const h=el("tr");["Start","Längd","Orsak"].forEach(x=>h.appendChild(el("th","",x)));t.appendChild(h);
d.stillestand.forEach(s=>{const r=el("tr");const dd=new Date(s.start*1000).toLocaleDateString("sv-SE",{weekday:"short",day:"numeric",month:"short"});
r.appendChild(el("td","",dd+" "+klock(s.start)+"-"+klock(s.slut)));r.appendChild(el("td","",hm(s.sek)));r.appendChild(el("td","",KAT[s.orsak]));t.appendChild(r)});st.appendChild(t)}
// tabell
const tab=$("tab");tab.replaceChildren();const th=el("tr");
["Dag","Mätt","GPU aktiv","Arbetar utan GPU","Cursor","Claude","Dig (ditt steg)","Körslingan av","Kön har uppgift","Övrigt","Ej mätt"].forEach(x=>th.appendChild(el("th","",x)));tab.appendChild(th);
const tr=(n,k,m,e)=>{const r=el("tr");[n,hm(m),...["aktiv","utan_gpu","cursor","claude","mattias","av","lokal_vilar","ovrigt"].map(x=>hm(k[x])),hm(e)].forEach(x=>r.appendChild(el("td","",x)));tab.appendChild(r)};
tr("Perioden",S.kat,S.matt_s,S.ej_matt_s);d.dagar.forEach(dg=>tr(dagnamn(dg.datum),dg.kat,dg.matt_s,dg.ej_matt_s))}
let sist=null;
async function hamta(){try{sist=await (await fetch("/api/anvandning?dagar="+DAGAR)).json();bygg(sist)}catch(e){}}
addEventListener("resize",()=>{if(sist)bygg(sist)});
hamta();setInterval(hamta,30000);
</script></body></html>
"""


class Hanterare(BaseHTTPRequestHandler):
    def svara(self, kropp, typ, extra=None):
        b = kropp.encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", typ + "; charset=utf-8")
        self.send_header("Content-Length", str(len(b)))
        for k, v in (extra or {}).items():
            self.send_header(k, v)
        self.end_headers()
        self.wfile.write(b)

    def do_GET(self):
        if self.path == "/api/lokal":
            self.svara(json.dumps(lokalt(), ensure_ascii=False), "application/json")
        elif self.path == "/api/version":
            self.svara(json.dumps({"v": STARTAD}), "application/json")
        elif self.path == "/api/tokens":
            self.svara(json.dumps(tokens_sammanstall(), ensure_ascii=False), "application/json")
        elif self.path == "/api/github":
            d = dict(hamta_github())
            try:
                lok = lokalt()
                try:
                    lok_tid = datetime.fromisoformat(lok.get("tid", "")).timestamp()
                except Exception:
                    lok_tid = 0
                d["stilla"] = stilla_larm(time.time(), d.get("aktivitet", 0), d.get("ko", []), d.get("arbete", []),
                                          d.get("cursor", []), lok.get("fas"), lok_tid,
                                          bool((d.get("release") or {}).get("klar")))
            except Exception:
                d["stilla"] = None
            self.svara(json.dumps(d, ensure_ascii=False), "application/json")
        elif self.path.startswith("/api/anvandning"):
            q = parse_qs(urlparse(self.path).query)
            try:
                dg = int((q.get("dagar") or ["7"])[0])
            except ValueError:
                dg = 7
            self.svara(json.dumps(anvandning_api(max(1, min(dg, 90))), ensure_ascii=False), "application/json")
        elif self.path.split("?")[0] == "/anvandning.csv":
            try:
                kropp = ANV.read_text(encoding="utf-8")
            except OSError:
                kropp = ANV_RUBRIK + "\n"
            self.svara(kropp, "text/csv", {"Content-Disposition": 'attachment; filename="appfabriken-anvandning.csv"'})
        elif self.path.split("?")[0] == "/anvandning":
            self.svara(ANVANDNING_SIDA, "text/html")
        else:
            self.svara(SIDA, "text/html")

    def log_message(self, *args):
        pass


def bevaka_filen(server):
    """Startar om panelen när panel.py ändras på disken (till exempel efter git pull), så att
    en gammal process inte blir kvar och visar gammal sida. Den nya processen öppnar ingen flik;
    sidan laddar om sig själv när servern är tillbaka."""
    fil = Path(__file__).resolve()
    senast = fil.stat().st_mtime
    egen_kopia = DATA.resolve() in fil.parents  # körs från LOCALAPPDATA, inte från repots arbetskopia
    varv = 0
    while True:
        time.sleep(5)
        varv += 1
        if egen_kopia and varv % 24 == 0:  # var 2:a minut: hämta senaste panel.py från main
            try:
                d = gh(f"repos/{REPO}/contents/verktyg/panel.py?ref=main") or {}
                ny_kod = base64.b64decode(d.get("content", ""))
                if ny_kod and ny_kod != fil.read_bytes():
                    fil.write_bytes(ny_kod)
            except Exception:
                pass
        try:
            ny = fil.stat().st_mtime
        except OSError:
            continue
        if ny != senast:
            # Den nya processen startas först och väntar på porten. Avslutas servern först hinner
            # huvudtråden avsluta processen innan den nya startats.
            subprocess.Popen([sys.executable, str(fil), "--omstart"],
                             creationflags=getattr(subprocess, "CREATE_NO_WINDOW", 0))
            server.server_close()
            os._exit(0)


if __name__ == "__main__":
    omstart = "--omstart" in sys.argv
    server = None
    for _ in range(10 if omstart else 1):  # vid omstart kan porten behöva en stund på sig att släppas
        try:
            server = ThreadingHTTPServer(("127.0.0.1", PORT), Hanterare)
            break
        except OSError:
            time.sleep(1)
    if server is None:
        webbrowser.open(f"http://127.0.0.1:{PORT}")  # panelen kör redan
        raise SystemExit(0)
    if not omstart:
        webbrowser.open(f"http://127.0.0.1:{PORT}")
    print(f"Panelen körs på http://127.0.0.1:{PORT}. Stäng fönstret för att stoppa den.")
    threading.Thread(target=bevaka_filen, args=(server,), daemon=True).start()
    threading.Thread(target=provtagning, daemon=True).start()
    threading.Thread(target=bollen_traad, daemon=True).start()
    server.serve_forever()
