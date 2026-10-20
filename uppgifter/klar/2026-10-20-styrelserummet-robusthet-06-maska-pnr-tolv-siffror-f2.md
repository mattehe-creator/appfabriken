# Uppgift: Personnummer utan bindestreck, samordning och plus

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Vad som saknades
- Ingen ändring i `MaskaPersonuppgifter.kt` — funktionerna `maskaPersonuppgifter` och `serUtSomPersonnummer` / `arGiltigtPersonDatum` utökades inte.
- Testfilen `MaskaPersonuppgifterTest.kt` är tom; den ska innehålla JUnit 4-test (`org.junit.Test`, `org.junit.Assert.assertEquals`) med metoden `maskaPersonnummerVarianter` som jämför mot exemplen nedan.
- **Samordningsnummer** (dag 61–91 i personnumret, t.ex. `650161-1234` → dagdelen `61`) ska räknas som giltigt personnummer i `serUtSomPersonnummer` / datumkontrollen, annars maskas de inte.
- Personnummer med **plus** efter de fyra sista siffrorna (t.ex. `050101-1234+`) ska hittas av regex och maskas; utöka mönstret så valfritt `+` efter `\d{4}` ingår i träffen.
- Tolv siffror i följd utan bindestreck (t.ex. `198501011234`) ska maskas om datumdelen är giltig — kontrollera att befintligt regex och validering täcker fallet (8+4 utan separator).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifterTest.kt

## Läs som förebild (ändra inte)
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVardeTest.kt` (JUnit 4-stil)

## Uppgift
Utöka `fun maskaPersonuppgifter(text: String): String` och vid behov `internal fun serUtSomPersonnummer(text: String): Boolean` (samt privat datumhjälp om nödvändigt) så att dessa maskas till `[personnummer]`:
- tolv siffror i följd utan bindestreck, t.ex. `198501011234`,
- **samordningsnummer** (dag 61–91), t.ex. `650161-1234`,
- personnummer med **plus** (över 100 år), t.ex. `050101-1234+`.

Organisationsnummer som `769612-3456` ska fortsatt vara oförändrat (ogiltigt persondatum / ska inte maskas).

## Exempel
- Indata: `Styrelseledamot 198501011234` → `Styrelseledamot [personnummer]`
- Indata: `Kontakt 650161-1234` → `Kontakt [personnummer]`
- Indata: `Arvtagare 050101-1234+` → `Arvtagare [personnummer]`
- Indata: `769612-3456` → oförändrat (organisationsnummer)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*MaskaPersonuppgifterTest.maskaPersonnummerVarianter"

Lägg till testklass `MaskaPersonuppgifterTest` med minst:

@Test
fun maskaPersonnummerVarianter() {
    assertEquals("Styrelseledamot [personnummer]", maskaPersonuppgifter("Styrelseledamot 198501011234"))
    assertEquals("Kontakt [personnummer]", maskaPersonuppgifter("Kontakt 650161-1234"))
    assertEquals("Arvtagare [personnummer]", maskaPersonuppgifter("Arvtagare 050101-1234+"))
    assertEquals("769612-3456", maskaPersonuppgifter("769612-3456"))
}

Använd `org.junit.Test` och `org.junit.Assert.assertEquals`, inte `kotlin.test`.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-20 (hela implementationen — lokal modell levererade ingen kod på PR #310)
