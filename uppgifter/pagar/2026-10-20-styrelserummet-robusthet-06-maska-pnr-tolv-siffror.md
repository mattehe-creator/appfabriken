# Uppgift: Personnummer utan bindestreck, samordning och plus

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifterTest.kt (ny)

## Uppgift
Utöka `fun maskaPersonuppgifter(text: String): String` och vid behov `internal fun serUtSomPersonnummer(text: String): Boolean` så att dessa maskas till `[personnummer]`:
- tolv siffror i följd utan bindestreck, t.ex. `198501011234`,
- **samordningsnummer** (dag 61–91), t.ex. `650161-1234`,
- personnummer med **plus** (över 100 år), t.ex. `050101-1234+`.

## Exempel
- Indata: `Styrelseledamot 198501011234` → `Styrelseledamot [personnummer]`
- Indata: `Kontakt 650161-1234` → `Kontakt [personnummer]`
- Indata: `Arvtagare 050101-1234+` → `Arvtagare [personnummer]`
- Indata: `769612-3456` → oförändrat (organisationsnummer)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*MaskaPersonuppgifterTest.maskaPersonnummerVarianter"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
