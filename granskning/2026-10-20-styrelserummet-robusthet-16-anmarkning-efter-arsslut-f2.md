beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- PR #314 innehöll ingen kodändring (endast flytt av uppgiftsfil till pagar/); implementation saknades helt vid granskning av försök 2.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3.
- Endast filer under "Ändra bara dessa filer" är ändrade; korrekta sökvägar under styrelserummet/.
- `anmarkningarFor` har `arsslut` som sista parameter; ny regel för anskaffningsår efter räkenskapsårets slut; befintliga anmärkningar oförändrade.
- `ByggUtkast.byggUtkast` skickar `nuAr` som tredje argument till `anmarkningarFor`.
- Tester jämför 2026 respektive 2025 mot `arsslut = 2025` enligt uppgiftens exempel.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Anmärkningstexter följer befintlig stil i `Anmarkningar.kt` (inte UI-strängresurser).
- Enhetstest använder JUnit 4 (org.junit).

## Rättat av Cursor
- Hela uppgiftslösningen: parameter `arsslut`, anmärkningsregel i loopen, uppdaterat anrop i `Utkast.kt`, ny `AnmarkningarTest.kt` med `anmarkningAnskaffningsarEfterArsslut`.

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
