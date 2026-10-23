beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- Endast tillåtna filer ändrade (ExempelData.kt, ExempelDataForeningTest.kt; uppgiftsflytt pagar är lokal modells workflow).
- Ingen fil i fel sökväg (rot eller annan modul).
- Paket, konstanter, ExempelAtgard, tom EXEMPEL_ATGARDER och exempelForening() enligt uppgiften.
- Test använder org.junit (JUnit 4), assertEquals mot uppgiftens värden och exempel för exempelForening().
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar.
- Bygge på grenen grönt före granskning; testDebugUnitTest grönt efter rättelse.

## Rättat av Cursor
- ORGANISATIONSNUMMER satt till `"000000-0000"` (uppgiften), test uppdaterat; placeholder-kontroll med nollor i stället för felaktig regex som missade bindestreck-formatet.
- assertEquals för exempelForening().namn och .byggnadsar enligt uppgiftens exempel.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
