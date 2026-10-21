# Drift — Styrelserummet

## Miljövariabler

| Variabel | Krävs | Beskrivning |
|---|---|---|
| `STYRELSERUMMET_DATA` | ja | Katalog för jobb (uppladdade PDF:er och utkast). Montera som volym i Docker. |
| `ANTHROPIC_API_KEY` | ja i produktion | API-nyckel för tolkning. Ska inte ligga i repot. |
| `STYRELSERUMMET_MODELL` | nej | Anthropic-modellnamn; standard enligt `AnthropicTolkare` i `:styrelserummet`. |
| `PORT` | nej | Standard `8080`. |
| `STRIPE_SECRET_KEY` | nej | Stripes hemliga nyckel. Utan den (och utan `STYRELSERUMMET_BAS_URL`) är betalningen avstängd och CSV och utskrift är fria. |
| `STYRELSERUMMET_BAS_URL` | med Stripe | Tjänstens publika adress med `https://`, till exempel `https://styrelserummet.se`. Stripe skickar kunden tillbaka hit. |
| `STYRELSERUMMET_PRIS_ORE` | nej | Pris per utkast i öre, standard `99000` (990 kr). |
| `STYRELSERUMMET_PROXY` | nej | Sätt till `1` bakom värdens omvända proxy så att klientens IP läses från `X-Forwarded-For` (sista adressen, den som värdens proxy lägger till; en förfalskad första adress ignoreras). **Ska vara på i produktion** när trafiken går via proxy; annars delar alla besökare samma gräns (5 jobb per timme). |

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
4. Peka domän till containern (HTTPS av värden). Sätt `STYRELSERUMMET_PROXY=1` om värden sätter `X-Forwarded-For`.
5. Kontrollera att `GET /halsa` svarar `ok` (hälsokontroll utan nyckel).
6. Prova flödet: ladda upp två PDF:er med markerbar text, spara länken, granska utkast, ladda ned CSV, radera jobbet.
7. Betalning (valfritt): skapa ett Stripe-konto för TM Connect, slå på kvitton/fakturor i Stripe, och sätt `STRIPE_SECRET_KEY` och `STYRELSERUMMET_BAS_URL` som hemligheter. Prova först med en testnyckel (`sk_test_...`) och Stripes testkort.

## Betalning

Stripe Checkout, engångsbetalning per utkast. Utkastet går alltid att granska och rätta gratis; CSV och utskrift kräver betalning när den är påslagen. Kortuppgifter når aldrig servern. Servern frågar Stripe om kassan är betald när kunden kommer tillbaka, och vid nästa hämtning om kunden inte kom tillbaka, så ingen webhook behövs. Stripe skapar kvitto/faktura till kundens e-post (`invoice_creation`). Moms: priset skickas som det är; momsfrågan för TM Connect avgör Mattias (Stripe Tax är inte påslaget).

## Vad som visas i utkastet

Jobbsidan visar sammanfattning, formaterade belopp, förslag på nyttjandeperiod och gruppförslag (allt märkt som utkast), valfri jämförelse mot föregående års CSV (skickas bara för visning — **sparas inte**), utskrift/PDF och CSV-export. Uppladdade PDF:er och utkast lagras som tidigare i `STYRELSERUMMET_DATA`; jämförelse-CSV och klistrad text lagras inte.

## Gränser

- 10 filer och 25 MB per jobb.
- 5 nya jobb per IP och timme.
- Jobb raderas efter 30 dagars inaktivitet.
