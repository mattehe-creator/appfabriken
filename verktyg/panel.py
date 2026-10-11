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
import webbrowser
from datetime import datetime, timedelta
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path

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


def release_berakna(rj, nu=None):
    """Procent klart och beräknat releasedatum ur release.json (steg med vikt, status och andel).

    Byggfarten är klarad vikt per dag sedan start. Den gäller bara utvecklingsstegen; externa steg
    (Mattias test, Googles granskning) räknas i dagar enligt antagandet i filen.
    """
    nu = nu or datetime.now()
    steg = rj.get("steg", [])
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
    prs = gh(f"repos/{REPO}/pulls?state=all&per_page=12") or []
    commits = gh(f"repos/{REPO}/commits?per_page=15") or []
    korningar = gh(f"repos/{REPO}/actions/runs?per_page=15") or {}
    ny = gh(f"repos/{REPO}/contents/uppgifter/ny?ref=main") or []
    fragor_fil = gh(f"repos/{REPO}/contents/FRAGOR.md?ref=main") or {}
    release = None
    try:
        rfil = gh(f"repos/{REPO}/contents/release.json?ref=main") or {}
        release = release_berakna(json.loads(base64.b64decode(rfil.get("content", "")).decode("utf-8")))
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

    data = {
        "ko": sorted(f["name"] for f in ny if f.get("name", "").endswith(".md")),
        "prs": [{"nr": p["number"], "titel": p["title"], "vem": aktor(gren=p["head"]["ref"]),
                 "lage": "mergad" if p.get("merged_at") else ("stängd" if p["state"] == "closed"
                         else ("KROCKAR MED MAIN, Claude måste lösa" if p["number"] in krock
                               else ("utkast" if p.get("draft") else "väntar på granskning")))}
                for p in prs],
        "handelser": handelser[:25],
        "fragor": fragor,
        "cursor": sorted(cursor_nu, key=lambda h: h["tid"]),
        "release": release,
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
    return status


SIDA = r"""<!doctype html><html lang="sv"><head><meta charset="utf-8">
<title>Appfabriken</title><meta name="viewport" content="width=device-width,initial-scale=1">
<style>
:root{--g:#00ff66;--g2:#0a8f3c;--bg:#000;--p:rgba(0,10,3,.82);--c:#7fd7ff;--k:#ffb347;--m:#d9d9d9}
*{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--g);font:14px/1.45 Consolas,"Cascadia Mono",monospace;overflow:hidden}
canvas{position:fixed;inset:0;z-index:0}
main{position:relative;z-index:1;display:grid;grid-template-columns:1.4fr 1fr;grid-template-rows:auto auto auto 1fr auto;gap:14px;height:100vh;padding:16px}
section{background:var(--p);border:1px solid var(--g2);padding:12px;overflow:auto;box-shadow:0 0 18px rgba(0,255,102,.15)}
h1{margin:0;font-size:18px;letter-spacing:.2em;text-shadow:0 0 8px var(--g)}h2{margin:0 0 8px;font-size:13px;letter-spacing:.15em;color:var(--g2)}
#topp{grid-column:1/3;display:flex;gap:24px;align-items:center;flex-wrap:wrap}
#fas{padding:2px 10px;border:1px solid var(--g);text-transform:uppercase}
#fas.kodar,#fas.testar{animation:puls 1.2s infinite}@keyframes puls{50%{box-shadow:0 0 14px var(--g)}}
#fas.fel{color:#ff5555;border-color:#ff5555}
#mat{grid-column:1/3;display:flex;gap:28px;align-items:flex-start;flex-wrap:wrap;overflow:visible}
#mat h2{margin-bottom:4px}#mat svg{display:block}
#bage{filter:drop-shadow(0 0 6px #00ff66);transition:stroke-dasharray 1.2s ease}
#eta{font-size:20px;text-shadow:0 0 8px var(--g)}#steg{columns:2;column-gap:20px;font-size:12px;margin-top:6px}
#steg li{margin:0 0 2px;break-inside:avoid}.klar{color:var(--g)}.pagar{color:var(--k)}.kvar{color:var(--g2)}
#tok{display:grid;grid-template-columns:repeat(2,auto);gap:4px 22px;margin-top:4px}#tok b{font-size:20px;font-weight:normal;text-shadow:0 0 8px var(--g)}
#kod{grid-row:3/6;white-space:pre-wrap;word-break:break-word}
#kod .rad{opacity:.95}#kod .ny{animation:in .6s}@keyframes in{from{color:#fff;text-shadow:0 0 10px #fff}}
.cursor::after{content:"█";animation:blink 1s steps(1) infinite}@keyframes blink{50%{opacity:0}}
ul{list-style:none;margin:0;padding:0}li{margin:0 0 6px}
.lokal{color:var(--g)}.cursor-a{color:var(--c)}.claude{color:var(--k)}.mattias{color:var(--m)}
.tag{display:inline-block;min-width:66px}.liten{color:var(--g2);font-size:12px}
@media(max-width:900px){main{grid-template-columns:1fr;grid-template-rows:auto;height:auto}body{overflow:auto}#kod{grid-row:auto;max-height:50vh}}
</style></head><body><canvas id="regn"></canvas><main>
<section id="topp"><h1>APPFABRIKEN</h1><span id="fas">...</span><span id="uppgift"></span><span id="text" class="liten"></span></section>
<section id="mat">
<div><h2>MÄTARE // AVSTÅND TILL RELEASE</h2>
<svg viewBox="0 0 200 124" width="230" role="img" aria-label="Procent klart till release">
<path d="M20 100 A80 80 0 0 1 180 100" pathLength="100" fill="none" stroke="#0a8f3c" stroke-width="12" opacity=".35"/>
<path id="bage" d="M20 100 A80 80 0 0 1 180 100" pathLength="100" fill="none" stroke="#00ff66" stroke-width="12" stroke-dasharray="0 100"/>
<text id="proc" x="100" y="92" text-anchor="middle" font-size="34" fill="#00ff66" font-family="Consolas,monospace">--</text>
<text x="100" y="112" text-anchor="middle" font-size="9" fill="#0a8f3c" font-family="Consolas,monospace">KLART AV 100</text>
<text x="20" y="118" text-anchor="middle" font-size="9" fill="#0a8f3c" font-family="Consolas,monospace">0</text>
<text x="180" y="118" text-anchor="middle" font-size="9" fill="#0a8f3c" font-family="Consolas,monospace">100</text></svg></div>
<div style="flex:1;min-width:300px"><h2 id="malrubrik">RELEASE</h2><div id="eta">Räknar...</div><div id="spann" class="liten"></div><ul id="steg"></ul><div id="anm" class="liten"></div></div>
<div><h2>TOKENS // LOKAL MODELL</h2><div id="tok"><span class="liten">Totalt</span><span class="liten">Idag</span><b id="t-totalt">-</b><b id="t-idag">-</b><span class="liten">Denna vecka</span><span class="liten">Per sekund (snitt)</span><b id="t-vecka">-</b><b id="t-sek">-</b></div>
<div id="t-not" class="liten" style="max-width:300px;margin-top:6px"></div></div>
</section>
<section id="kod"><h2>LOKAL MODELL // AIDER</h2><div id="rader"></div><span class="cursor"></span></section>
<section id="cur"><h2>CURSOR // NU</h2><ul id="cursornu"></ul><div class="liten" style="margin-top:6px">Detaljer: <a href="https://cursor.com/agents" target="_blank" style="color:var(--c)">cursor.com/agents</a> · <a href="https://github.com/mattehe-creator/appfabriken/actions" target="_blank" style="color:var(--c)">GitHub Actions</a></div></section>
<section><h2>HÄNDELSER</h2><ul id="handelser"></ul></section>
<section><h2>KÖ OCH PR</h2><div class="liten">Frågor till Mattias</div><ul id="fragor"></ul><div class="liten">Uppgifter som väntar</div><ul id="ko"></ul><div class="liten" style="margin-top:8px">Pull requests</div><ul id="prs"></ul></section>
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
const f=document.getElementById("fas");f.textContent=d.fas;f.className=d.fas;
document.getElementById("uppgift").textContent=d.uppgift||"";
let t=d.text||"";if(d.fas==="kodar"&&d.aider_sekunder_sedan!=null)t+=" Senaste utskrift för "+d.aider_sekunder_sedan+" s sedan.";
document.getElementById("text").textContent=t;
const r=d.aider||[];if(r.join("\n")!==sist.join("\n")){const ny=r.length-sist.length;
document.getElementById("rader").innerHTML=r.map((l,i)=>`<div class="rad${i>=r.length-Math.max(ny,0)?" ny":""}">${esc(l)}</div>`).join("");
sist=r;const k=document.getElementById("kod");k.scrollTop=k.scrollHeight}}catch(e){}}
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
document.getElementById("anm").textContent=r.anm||""}
async function tokens(){try{const d=await (await fetch("/api/tokens")).json();
document.getElementById("t-totalt").textContent=sv(d.totalt);document.getElementById("t-idag").textContent=sv(d.idag);
document.getElementById("t-vecka").textContent=sv(d.vecka);
document.getElementById("t-sek").textContent=d.per_sek==null?"-":sv(d.per_sek,1);
document.getElementById("t-not").textContent=`${d.korningar} körningar. Aiders egen räkning, avrundad till tusental, skickat plus mottaget.`+(d.per_sek_senaste!=null?` Senaste körningen ${sv(d.per_sek_senaste,1)} per sekund.`:"")+" Cursor och Claude mäts inte här."}catch(e){}}
async function github(){try{const d=await (await fetch("/api/github")).json();release(d.release);
document.getElementById("handelser").innerHTML=d.handelser.map(h=>`<li class="${kls(h.vem)}"><span class="tag">${vemNamn[h.vem]}</span><span class="liten">${new Date(h.tid).toLocaleString("sv-SE",{dateStyle:"short",timeStyle:"short"})}</span> ${esc(h.text)}</li>`).join("");
document.getElementById("cursornu").innerHTML=(d.cursor||[]).length?d.cursor.map(h=>`<li class="cursor-a"><span class="liten">sedan ${new Date(h.tid).toLocaleTimeString("sv-SE",{timeStyle:"short"})}</span> ${esc(h.text)}</li>`).join(""):"<li class='liten'>Vilar. Inget skickat till Cursor just nu.</li>";
document.getElementById("fragor").innerHTML=d.fragor.length?d.fragor.map(f=>`<li class="claude">${esc(f)}</li>`).join(""):"<li class='liten'>Inga</li>";
document.getElementById("ko").innerHTML=d.ko.length?d.ko.map(k=>`<li>${esc(k.replace(".md",""))}</li>`).join(""):"<li class='liten'>Tom</li>";
document.getElementById("prs").innerHTML=d.prs.map(p=>`<li class="${kls(p.vem)}"><span class="tag">#${p.nr}</span>${esc(p.titel)} <span class="liten">(${p.lage})</span></li>`).join("")}catch(e){}}
let ver=null;async function version(){try{const v=(await (await fetch("/api/version")).json()).v;if(ver&&v!==ver)location.reload();ver=v}catch(e){}}
version();setInterval(version,5000);
lokal();github();tokens();setInterval(lokal,2000);setInterval(github,30000);setInterval(tokens,5000);
</script></body></html>"""


class Hanterare(BaseHTTPRequestHandler):
    def svara(self, kropp, typ):
        b = kropp.encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", typ + "; charset=utf-8")
        self.send_header("Content-Length", str(len(b)))
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
            self.svara(json.dumps(hamta_github(), ensure_ascii=False), "application/json")
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
    while True:
        time.sleep(5)
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
    server.serve_forever()
