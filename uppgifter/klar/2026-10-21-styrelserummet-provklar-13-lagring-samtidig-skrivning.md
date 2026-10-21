# Uppgift: Samtidiga meta-uppdateringar

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: 2026-10-21-styrelserummet-provklar-12-lagring-korrupt-meta.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbLagring.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbLagringTest.kt

## Uppgift
Test `samtidigSkrivMetaGerGiltigJson`:

1. Skapa jobb med giltig `JobbMeta`.
2. Från två trådar: anropa `uppdateraAktivitet(id)` (eller två `skrivMeta` med olika `senastAktivMs`) **samtidigt** minst 20 gånger totalt.
3. Efter join: `lasMeta(id)` ska lyckas och returnera **giltig** `JobbMeta` (inte korrupt JSON, inte tom fil).

Om test failar: synkronisera skrivning av `meta.json` i `JobbLagring` (t.ex. lås på jobbkatalog) utan att ändra API.

## Exempel
- Efter stress: `json.decodeFromString<JobbMeta>(fil)` lyckas.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbLagringTest.samtidigSkrivMetaGerGiltigJson"

## Gör inte
- Ändra andra filer.
- Sänka antal iterationer under 20 utan anledning.

utfall: godkänd 2026-10-21 (inget)
