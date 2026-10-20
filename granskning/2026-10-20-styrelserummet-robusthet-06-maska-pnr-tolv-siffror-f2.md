beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- PR #310 innehöll ingen kodändring (endast flytt av uppgiftsfil till pagar/); implementation saknades helt vid granskning.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3.
- Endast filer under "Ändra bara dessa filer" är ändrade; korrekta sökvägar under styrelserummet/.
- Regex tillåter valfritt `+` efter de fyra sista siffrorna; datumkontroll accepterar samordningsnummer (dag 61–91).
- Tolv siffror utan bindestreck, samordningsnummer och plus-exempel maskas; organisationsnummer 769612-3456 lämnas oförändrat.
- Testet `maskaPersonnummerVarianter` jämför mot uppgiftens exempel.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; inga hårdkodade användarsträngar utöver befintliga maskeringsplatshållare.
- Enhetstest använder JUnit 4 (org.junit).

## Rättat av Cursor
- Hela uppgiftslösningen: regex `\+?`, samordningsdag 61–91 i `arGiltigtPersonDatum`, ny testklass `MaskaPersonuppgifterTest` med `maskaPersonnummerVarianter`.

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
