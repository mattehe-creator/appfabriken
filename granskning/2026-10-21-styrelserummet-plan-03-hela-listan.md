beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade (Avskrivningsplan.kt, AvskrivningsplanTest.kt); inga filer i repots rot.
- `avskrivningsplan` följer samma årsintervall och validering av `antalAr` som `planForKomponent`; summerar avskrivning och bokfört värde per år.
- Tester jämför mot exemplen i uppgiften (inkl. tom lista); JUnit 4 (`org.junit`).
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar.
- `planForKomponent` oförändrad i beteende (inga duplicerade hjälpfunktioner kvar).

## Rättat av Cursor
- Tog bort duplicerade `avskrivningFor` och `bokfortVarde` i Avskrivningsplan.kt (fanns redan i Avskrivning.kt och BokfortVarde.kt; gav kompileringsfel).
- Rättade anskaffningsvärde för komponent A i testet från 200_000 till 1_200_000 enligt uppgiftens exempel.

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
