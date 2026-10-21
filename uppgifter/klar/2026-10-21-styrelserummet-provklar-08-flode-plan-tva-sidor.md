# Uppgift: Syntetisk underhållsplan två sidor

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTvaSidorTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasareTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Utkast.kt

## Uppgift
Test `planTvaSidorFortsattning`:

1. PDF med **två sidor**: sida 1 har början av planen, sida 2 har fortsattning (t.ex. extra åtgärd med kostnad).
2. Stub `Tolkare` som **registrerar** `sidor`-argument och returnerar JSON med komponenter från båda delarna (anropa kan ske en eller flera gånger beroende på `delaUpp` — assert att **alla** komponenter finns i slutligt utkast).
3. Byggnadens värde i årsredovisning = summa komponenter.
4. Assert antal komponenter, avstämning, säkerhet och LAG/UPPSKATTNING-anmärkningar.

## Exempel
- Sida 1: Ventilation 800 000, sida 2: Hiss 200 000, byggnad 1 000 000 → 2 komponenter, `stammer`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SyntetiskPlanTvaSidorTest"

## Gör inte
- Ändra `Komponent` eller `Forening`.
- Riktiga PDF:er i repot.

utfall: underkänd 2026-10-21 av Cursor (PR #421). Ersatt av 2026-10-21-styrelserummet-provklar-08-flode-plan-tva-sidor-f2.md
