"""Appfabrikens panel: visar vad den lokala modellen, Cursor och Claude gör.

Start:  python verktyg\\panel.py      (öppnar http://127.0.0.1:8765 i webbläsaren)
Bara standardbiblioteket. GitHub läses via gh (samma inloggning som körskriptet).
Panelen läser och visar, den ändrar ingenting.
"""
import json
import os
import subprocess
import threading
import time
import webbrowser
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path

REPO = "mattehe-creator/appfabriken"
PORT = 8765
DATA = Path(os.environ.get("LOCALAPPDATA", ".")) / "appfabriken"
STATUS = DATA / "status.json"
AIDER = DATA / "logg" / "aider-senaste.log"

_cache = {"tid": 0.0, "github": {}}
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
    fragor = []
    try:
        import base64
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

    data = {
        "ko": sorted(f["name"] for f in ny if f.get("name", "").endswith(".md")),
        "prs": [{"nr": p["number"], "titel": p["title"], "vem": aktor(gren=p["head"]["ref"]),
                 "lage": "mergad" if p.get("merged_at") else ("stängd" if p["state"] == "closed"
                         else ("utkast" if p.get("draft") else "väntar på granskning"))}
                for p in prs],
        "handelser": handelser[:25],
        "fragor": fragor,
        "cursor": sorted(cursor_nu, key=lambda h: h["tid"]),
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
main{position:relative;z-index:1;display:grid;grid-template-columns:1.4fr 1fr;grid-template-rows:auto auto 1fr auto;gap:14px;height:100vh;padding:16px}
section{background:var(--p);border:1px solid var(--g2);padding:12px;overflow:auto;box-shadow:0 0 18px rgba(0,255,102,.15)}
h1{margin:0;font-size:18px;letter-spacing:.2em;text-shadow:0 0 8px var(--g)}h2{margin:0 0 8px;font-size:13px;letter-spacing:.15em;color:var(--g2)}
#topp{grid-column:1/3;display:flex;gap:24px;align-items:center;flex-wrap:wrap}
#fas{padding:2px 10px;border:1px solid var(--g);text-transform:uppercase}
#fas.kodar,#fas.testar{animation:puls 1.2s infinite}@keyframes puls{50%{box-shadow:0 0 14px var(--g)}}
#fas.fel{color:#ff5555;border-color:#ff5555}
#kod{grid-row:2/5;white-space:pre-wrap;word-break:break-word}
#kod .rad{opacity:.95}#kod .ny{animation:in .6s}@keyframes in{from{color:#fff;text-shadow:0 0 10px #fff}}
.cursor::after{content:"█";animation:blink 1s steps(1) infinite}@keyframes blink{50%{opacity:0}}
ul{list-style:none;margin:0;padding:0}li{margin:0 0 6px}
.lokal{color:var(--g)}.cursor-a{color:var(--c)}.claude{color:var(--k)}.mattias{color:var(--m)}
.tag{display:inline-block;min-width:66px}.liten{color:var(--g2);font-size:12px}
@media(max-width:900px){main{grid-template-columns:1fr;grid-template-rows:auto;height:auto}body{overflow:auto}#kod{grid-row:auto;max-height:50vh}}
</style></head><body><canvas id="regn"></canvas><main>
<section id="topp"><h1>APPFABRIKEN</h1><span id="fas">...</span><span id="uppgift"></span><span id="text" class="liten"></span></section>
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
async function github(){try{const d=await (await fetch("/api/github")).json();
document.getElementById("handelser").innerHTML=d.handelser.map(h=>`<li class="${kls(h.vem)}"><span class="tag">${vemNamn[h.vem]}</span><span class="liten">${new Date(h.tid).toLocaleString("sv-SE",{dateStyle:"short",timeStyle:"short"})}</span> ${esc(h.text)}</li>`).join("");
document.getElementById("cursornu").innerHTML=(d.cursor||[]).length?d.cursor.map(h=>`<li class="cursor-a"><span class="liten">sedan ${new Date(h.tid).toLocaleTimeString("sv-SE",{timeStyle:"short"})}</span> ${esc(h.text)}</li>`).join(""):"<li class='liten'>Vilar. Inget skickat till Cursor just nu.</li>";
document.getElementById("fragor").innerHTML=d.fragor.length?d.fragor.map(f=>`<li class="claude">${esc(f)}</li>`).join(""):"<li class='liten'>Inga</li>";
document.getElementById("ko").innerHTML=d.ko.length?d.ko.map(k=>`<li>${esc(k.replace(".md",""))}</li>`).join(""):"<li class='liten'>Tom</li>";
document.getElementById("prs").innerHTML=d.prs.map(p=>`<li class="${kls(p.vem)}"><span class="tag">#${p.nr}</span>${esc(p.titel)} <span class="liten">(${p.lage})</span></li>`).join("")}catch(e){}}
lokal();github();setInterval(lokal,2000);setInterval(github,30000);
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
        elif self.path == "/api/github":
            self.svara(json.dumps(hamta_github(), ensure_ascii=False), "application/json")
        else:
            self.svara(SIDA, "text/html")

    def log_message(self, *args):
        pass


if __name__ == "__main__":
    try:
        server = ThreadingHTTPServer(("127.0.0.1", PORT), Hanterare)
    except OSError:
        webbrowser.open(f"http://127.0.0.1:{PORT}")  # panelen kör redan
        raise SystemExit(0)
    webbrowser.open(f"http://127.0.0.1:{PORT}")
    print(f"Panelen körs på http://127.0.0.1:{PORT}. Stäng fönstret för att stoppa den.")
    server.serve_forever()
