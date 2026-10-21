"""Gör om <app>/butik/integritetspolicy-sv.md och -en.md till publika sidor.

Resultat: _site/<app>/integritet/index.html (svenska) och _site/<app>/privacy/index.html (engelska).
Kontaktadressen står på ett ställe, webb/kontakt.txt. Saknas den visas platshållaren från texten.
Bara rubriker (#, ##), stycken, punktlistor, **fetstil** och [länkar](url) stöds, vilket är allt policyerna använder.
"""
import html
import pathlib
import re

ROT = pathlib.Path(__file__).resolve().parent.parent
PLATSHALLARE = ("[kontaktadress läggs in vid publicering]", "[contact address added at publication]")

STIL = """<style>
:root{--bg:#fbfaf7;--text:#1d1f21;--mut:#5a5f66;--accent:#1f5f8b;--line:#e3e0d8;color-scheme:light dark}
@media (prefers-color-scheme:dark){:root{--bg:#141618;--text:#e9e7e2;--mut:#a7abb0;--accent:#8cc3ea;--line:#2c3034}}
body{background:var(--bg);color:var(--text);font:17px/1.6 system-ui,-apple-system,"Segoe UI",Roboto,sans-serif}
main{max-width:42rem;margin:0 auto;padding:2.5rem 1rem 4rem}
h1{font-size:1.7rem;line-height:1.25;margin:0 0 .5rem}
h2{font-size:1.15rem;margin:2rem 0 .4rem;padding-top:1rem;border-top:1px solid var(--line)}
p,li{max-width:40rem}ul{padding-left:1.2rem}a{color:var(--accent)}
.sprak{font-size:.9rem;color:var(--mut);margin-bottom:1.5rem}
</style>"""


def inline(t: str) -> str:
    t = html.escape(t, quote=False)
    t = re.sub(r"\*\*(.+?)\*\*", r"<strong>\1</strong>", t)
    t = re.sub(r"\[([^\]]+)\]\((https?://[^)\s]+|mailto:[^)\s]+)\)", r'<a href="\2">\1</a>', t)
    return t


def md_till_html(md: str) -> tuple[str, str]:
    ut, lista, titel = [], False, ""
    for rad in md.splitlines():
        r = rad.rstrip()
        if r.startswith("- "):
            if not lista:
                ut.append("<ul>")
                lista = True
            ut.append(f"<li>{inline(r[2:])}</li>")
            continue
        if lista:
            ut.append("</ul>")
            lista = False
        if not r:
            continue
        if r.startswith("## "):
            ut.append(f"<h2>{inline(r[3:])}</h2>")
        elif r.startswith("# "):
            titel = r[2:]
            ut.append(f"<h1>{inline(titel)}</h1>")
        else:
            ut.append(f"<p>{inline(r)}</p>")
    if lista:
        ut.append("</ul>")
    return titel, "\n".join(ut)


def kontakt() -> str | None:
    f = ROT / "webb" / "kontakt.txt"
    if f.exists():
        v = f.read_text(encoding="utf-8").strip()
        return v or None
    return None


def bygg(ut: pathlib.Path) -> list[pathlib.Path]:
    adress = kontakt()
    skapade = []
    for policy in sorted(ROT.glob("*/butik/integritetspolicy-*.md")):
        app = policy.parent.parent.name
        sprak = policy.stem.rsplit("-", 1)[1]
        if sprak not in ("sv", "en"):
            continue
        md = policy.read_text(encoding="utf-8")
        if adress:
            lank = f"[{adress}](mailto:{adress})" if "@" in adress else adress
            for p in PLATSHALLARE:
                md = md.replace(p, lank)
        titel, kropp = md_till_html(md)
        mapp, annan, annan_text = (
            ("integritet", "privacy", "English") if sprak == "sv" else ("privacy", "integritet", "Svenska")
        )
        sida = (
            f'<!doctype html>\n<html lang="{sprak}">\n<head>\n<meta charset="utf-8">\n'
            '<meta name="viewport" content="width=device-width, initial-scale=1">\n'
            f"<title>{html.escape(titel)}</title>\n{STIL}\n</head>\n<body>\n<main>\n"
            f'<p class="sprak"><a href="../{annan}/">{annan_text}</a></p>\n{kropp}\n</main>\n</body>\n</html>\n'
        )
        m = ut / app / mapp
        m.mkdir(parents=True, exist_ok=True)
        (m / "index.html").write_text(sida, encoding="utf-8")
        skapade.append(m / "index.html")
    return skapade


if __name__ == "__main__":
    import sys

    for s in bygg(pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else "_site")):
        print("byggd:", s)
