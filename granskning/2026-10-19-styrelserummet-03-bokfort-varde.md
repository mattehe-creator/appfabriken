beslut: underkänd
granskare: cursor
datum: 2026-10-19

## Kontrollerat
- PR #284 ändrar enbart flytt av uppgiftsfilen `uppgifter/ny/` → `uppgifter/pagar/`; varken `BokfortVarde.kt` eller `BokfortVardeTest.kt` finns på grenen.
- Uppgiftens beteende (`bokfortVarde`) är inte implementerat; inga enhetstester mot exemplen.
- Inga otillåtna filer i repots rot, inga nya beroenden, behörigheter eller nätverk (kod saknas).
- Ingen databasschemaändring.
- `:styrelserummet:testDebugUnitTest` grönt på grenen utan de saknade filerna (lokalt verifierat).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Bokfört värde

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 2
beror-på: 2026-10-19-styrelserummet-02-arsavskrivning-f2.md

## Vad som saknades
Ingen produktionskod eller tester levererades. Du ska skapa båda filerna som listas under «Ändra bara dessa filer» och implementera `bokfortVarde` enligt uppgiften nedan. Använd befintliga `avskrivningFor` (i `Avskrivning.kt`) — importera eller anropa den från samma paket; ändra inte `Avskrivning.kt`.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt (hjälpfunktion `komponent`, JUnit 4)

## Uppgift
`fun bokfortVarde(k: Komponent, arsslut: Int): Long`

Anskaffningsvärde minus summan av `avskrivningFor(k, ar)` för alla år `ar` från `k.anskaffningsar` till och med `arsslut` (inklusive). Om `arsslut` ligger före `k.anskaffningsar` blir summan 0 (tom intervall i en `for`-loop är OK).

Resultatet ska **aldrig** understiga `k.restvarde` (använd t.ex. `maxOf(k.restvarde, …)`).

## Exempel
- Komponent: 1_200_000 kr, anskaffningsår 2010, nyttjandeperiod 40 år, restvärde 0. Vid `arsslut` 2025: avskrivning 16 år × 30_000 = 480_000 → bokfört värde **720_000**.
- Samma komponent, `arsslut` 2009 (före anskaffningsår): ingen avskrivning → **1_200_000**.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*BokfortVardeTest"

Testerna ska använda JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`) och jämföra mot exemplen ovan med samma komponentdata som i `AvskrivningTest` (1_200_000 / 2010 / 40 / restvärde 0).

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

## Frågor till Claude
- inga
