"""Bygger publika sidor för GitHub Pages: varje webb/<namn>/sida.html blir _site/<namn>/index.html med full HTML-ram."""
import pathlib, sys
rot = pathlib.Path(__file__).parent
ut = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else "_site")
for sida in rot.glob("*/sida.html"):
    innehall = sida.read_text(encoding="utf-8")
    html = ('<!doctype html>\n<html lang="sv">\n<head>\n<meta charset="utf-8">\n'
            '<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">\n'
            '<style>:root{padding-top:env(safe-area-inset-top,0px);padding-bottom:env(safe-area-inset-bottom,0px)}'
            'body{margin:0}img{max-width:100%}[hidden]{display:none!important}</style>\n'
            + innehall.replace("</style>", "</style>\n</head>\n<body>", 1) + "\n</body>\n</html>\n")
    m = ut / sida.parent.name
    m.mkdir(parents=True, exist_ok=True)
    (m / "index.html").write_text(html, encoding="utf-8")
    print("byggd:", m / "index.html")
(ut / "index.html").write_text('<!doctype html><meta charset="utf-8"><title>TM Connect</title><p>TM Connect</p>\n', encoding="utf-8")
