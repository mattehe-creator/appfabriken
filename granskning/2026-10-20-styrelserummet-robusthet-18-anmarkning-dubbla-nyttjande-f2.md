beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: lokala modellen levererade bara flytt till `pagar/`; inga ändringar i `Anmarkningar.kt` / `AnmarkningarTest.kt` (försök 2 utan kod, samma som försök 1).
- Efter granskning: endast de två tillåtna filerna under `styrelserummet/…/underlag/` plus `granskning/` och flytt av uppgiftsfil; inga filer i repots rot.
- Dubbla komponentnamn (case-sensitive gruppering, en anmärkning per namn) och utgången nyttjandeperiod med `bokfortVarde > restvarde` enligt uppgiften; befintliga regler och `distinct()` kvar.
- `AnmarkningarTest.anmarkningDubblaNamnOchNyttjande` med JUnit 4; test för nyttjandeperiod hanterar att linjär avskrivning sällan ger `bokfort > rest` efter periodslut (ingen falsk anmärkning).
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar.
- `:styrelserummet:test --tests "*AnmarkningarTest*"` grönt lokalt.

## Rättat av Cursor
- Hela implementationen i `Anmarkningar.kt` och `AnmarkningarTest.kt` (lokal modell levererade ingen produktionskod i försök 2).

## Vid underkänd: försök 2

## Frågor till Claude
- Uppgiftens testpunkt 2 kräver samtidigt `anskaffningsar + nyttjandeperiodAr <= arsslut` och `bokfortVarde(k, arsslut) > restvarde`. Med nuvarande `bokfortVarde`/`avskrivningFor` verkar det inte finnas någon giltig `Komponent` där båda gäller efter periodslut. Ska regeln reserveras för framtida inkonsistenta utkast, eller ska villkoret/bokföringsmodellen justeras?
