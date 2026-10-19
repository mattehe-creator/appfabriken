# Drift — Styrelserummet

## Miljövariabler

| Variabel | Krävs | Beskrivning |
|---|---|---|
| `STYRELSERUMMET_DATA` | ja | Katalog för jobb (uppladdade PDF:er och utkast). Montera som volym i Docker. |
| `ANTHROPIC_API_KEY` | ja i produktion | API-nyckel för tolkning. Ska inte ligga i repot. |
| `STYRELSERUMMET_MODELL` | nej | Anthropic-modellnamn; standard enligt `AnthropicTolkare` i `:styrelserummet`. |
| `PORT` | nej | Standard `8080`. |

## Lokalt

```bash
export STYRELSERUMMET_DATA=/tmp/styrelserummet-data
export ANTHROPIC_API_KEY=dummy   # eller riktig nyckel
gradle :styrelserummet-server:installDist
gradle :styrelserummet-server:run
```

Öppna http://localhost:8080/

Tester (falsk tolkare, ingen nyckel):

```bash
gradle :styrelserummet:test :styrelserummet-server:test
```

## Docker

```bash
gradle :styrelserummet-server:installDist
docker build -f styrelserummet/Dockerfile -t styrelserummet .
docker run --rm -p 8080:8080 \
  -e ANTHROPIC_API_KEY=... \
  -v styrelserummet-data:/data \
  -e STYRELSERUMMET_DATA=/data \
  styrelserummet
```

## Första driftsättning (Mattias)

1. Skapa konto hos vald värd (Claude beslutar vilken).
2. Sätt `ANTHROPIC_API_KEY` som hemlighet i miljön (inte i git).
3. Montera persistent volym på `STYRELSERUMMET_DATA`.
4. Peka domän till containern (HTTPS av värden).
5. Prova flödet: ladda upp två PDF:er med markerbar text, spara länken, granska utkast, ladda ned CSV, radera jobbet.
6. Betalning och pris på landningssidan kommer i eget uppdrag när betalkonto finns.

## Gränser

- 10 filer och 25 MB per jobb.
- 5 nya jobb per IP och timme.
- Jobb raderas efter 30 dagars inaktivitet.
