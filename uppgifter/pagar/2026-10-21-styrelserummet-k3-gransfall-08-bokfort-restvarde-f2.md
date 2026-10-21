# Uppgift: Bokfört värde med restvärde

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- PR #359 innehåller bara commit «Uppgift startad: …» (flytt till `pagar/`). Ingen testfil skapades.
- Uppgiften kräver `BokfortVardeGransfallRestvardeTest.kt`; `gradle :styrelserummet:test --tests "*BokfortVardeGransfallRestvardeTest"` hittar inget.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeGransfallRestvardeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeTest.kt
- styrelserummet/regler/K3.md (restvärde enligt punkt 17.14–17.15)

## Uppgift
Implementera testklassen `BokfortVardeGransfallRestvardeTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` — **inte** `kotlin.test`).

Testa `bokfortVarde(k: Komponent, arsslut: Int)` för komponenter med **restvärde > 0**:

1. Efter sista avskrivningsåret och långt därefter ska bokfört värde ligga kvar på **restvärdet**, inte under.
2. Mitt i avskrivningsperioden: bokfört = anskaffningsvärde minus ackumulerad avskrivning, men **minst** restvärde.

Återanvänd samma mönster som `BokfortVardeTest.kt` (privat `komponent(...)` med `Komponentgrupp`, `Sakerhet`, Long för kronor).

Om ett test visar fel beteende får du **endast** ändra `BokfortVarde.kt` (lägg till filen under «Ändra bara dessa filer» i PR:en och håll dig inom cirka 150 rader totalt).

## Exempel
- Komponent: anskaffningsvärde 1_000_000 kr, anskaffningsår 2010, nyttjandeperiod 10 år, restvärde 100_000 kr.
  - `bokfortVarde(k, 2019)` (sista avskrivningsår): förväntat **100_000** kr.
  - `bokfortVarde(k, 2025)` (efter perioden): förväntat **100_000** kr.
  - `bokfortVarde(k, 2014)` (mitt i perioden; avskrivning 90_000 kr/år): förväntat **550_000** kr.

Lägg minst till `@Test`-metoder som assertar exakt dessa värden med `assertEquals(100_000L, …)` respektive `assertEquals(550_000L, …)`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*BokfortVardeGransfallRestvardeTest"

(Cursor kör `:styrelserummet:testDebugUnitTest` vid granskning.)

## Gör inte
- Ändra `Komponent.kt`, `BokfortVardeTest.kt` eller andra filer (utom `BokfortVarde.kt` om test visar bugg enligt regeln ovan).
- Lägga till beroenden.
