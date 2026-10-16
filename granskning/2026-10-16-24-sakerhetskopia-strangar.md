beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast tillåtna filer ändrade för uppgiftsinnehåll (`husloggen/.../values/strings.xml`, `husloggen/.../values-sv/strings.xml`); uppgiftsfilens flytt ny→pagar gjordes av lokala modellen enligt körflöde.
- Alla nio `sakerhetskopia_*`-nycklar tillagda på engelska och svenska; engelska följer uppgift och garantivalvet-mönster (poster/log entries).
- `StringsNycklarTest` grönt (`:husloggen:testDebugUnitTest --tests "*StringsNycklarTest"`); testet använder JUnit 4 (`org.junit`).
- Inga nya beroenden, behörigheter eller nätverk.
- Inga hårdkodade UI-strängar utanför `strings.xml`.
- Ingen databasschemaändring.

## Rättat av Cursor
- Svenska `sakerhetskopia_beskrivning` och `sakerhetskopia_import_beskrivning`: markdown-asterisker (`**`) från uppgiftstexten togs bort; formulering i linje med garantivalvet (`values-sv/strings.xml`).

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
