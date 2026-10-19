# Uppgift: Beräkna sammanställning per app

uppdrag: uppdrag/2026-10-19-prov-aviseringar-skolinbox.md
försök: 1
beror-på: 2026-10-19-03-klassa-typ.md

## Ändra bara dessa filer
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/regler/Sammanstallning.kt (ny)
- prov-aviseringar/src/test/kotlin/se/tmconnect/prov/aviseringar/regler/SammanstallningTest.kt (ny)

## Läs som förebild (ändra inte)
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/data/AviseringEntitet.kt
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/regler/HittaDatum.kt
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/regler/HittaTid.kt
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/regler/AviseringTyp.kt

## Uppgift
Implementera dataklass `AppSammanstallning` (paketNamn, appNamn, antal, andelMedDatum, andelMedTid, antalPerTyp: Map<Typ, Int>) och:

`fun beraknaSammanstallningPerApp(rader: List<AviseringEntitet>, idag: LocalDate): List<AppSammanstallning>`

Kombinera title, text, bigText, textLines och messages till en sträng per avisering. Andelar är 0.0–1.0.

## Exempel
Två aviseringar samma paket: "imorgon kl 8" och "info" → andelMedDatum 0.5, andelMedTid 0.5, båda klassade.

## Test som ska gå igenom
gradle :prov-aviseringar:testDebugUnitTest --tests "*SammanstallningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
