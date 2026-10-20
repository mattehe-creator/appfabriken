# Uppgift: Telefon och e-post i fler format

## Vad som saknades
- Ingen implementation: varken `MaskaPersonuppgifter.kt` eller testfilen ändrades eller skapades på grenen.
- Testet `MaskaPersonuppgifterTest.maskaTelefonOchEpost` saknas helt, så uppgiftens «Test som ska gå igenom» kan inte köras.
- Körskriptet rapporterade röda tester och att de angivna filerna «saknas» — det beror på att testfilen inte finns och att inget arbete gjordes i de listade filerna.

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifterTest.kt

## Läs som förebild (ändra inte)
- Befintlig `MaskaPersonuppgifter.kt` på grenen (personnummer, e-post, telefon med `+46` och `0…`).

## Uppgift
Utöka `maskaPersonuppgifter` så att:

Telefon maskas till `[telefon]` för bland annat:
- `070 123 45 67`
- `0701-23 45 67`
- `0046 70 123 45 67`
- befintliga format (`070-123 45 67`, `+46 70 123 45 67`)

E-post med versaler och bindestreck i domänen maskas till `[e-post]`, t.ex. `Styrelse@Min-Brf.se`.

Organisationsnummer som `769612-3456` ska **inte** maskas som personnummer (oförändrat enligt exempel).

## Exempel
- Indata: `Ring 070 123 45 67` → `Ring [telefon]`
- Indata: `Mail Styrelse@Min-Brf.se` → `Mail [e-post]`
- Indata: `769612-3456` → oförändrat

## Test som ska gå igenom
Skapa eller utöka `MaskaPersonuppgifterTest` med metoden `maskaTelefonOchEpost` som assertar exakt ovanstående exempel (JUnit 4: `org.junit.Assert`, inte `kotlin.test`).

gradle :styrelserummet:test --tests "*MaskaPersonuppgifterTest.maskaTelefonOchEpost"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
