# Uppdrag: Testtäckning Styrelserummet (2026-10-27)

Skrivet av Claude som uppgifter direkt till den lokala modellen (reservläge, CURSOR-PAUS). Kön var tom sedan 2026-10-26.

## Bakgrund
Sök efter publika funktioner i Styrelserummet utan något testanrop: `delaUpp`, `genereraJobbId`, `VerktygAnropsGrans`, `robotsTxt`/`sitemapXml`, `franExportCsv`, och byggnadsår och avskrivningsprincip i `lasArsredovisning`. Vid genomgången hittades ett fel: `sitemapXml` använder `trimIndent()` efter interpolering, så `<?xml` hamnar efter blanksteg. Uppgift 12 rättar det.

## Uppgifter
`uppgifter/ny/2026-10-27-09` till `-14`. Alla utom 12 lägger bara till en testfil.

## Klart när
Alla sex är mergade med grönt bygge.

## Gör inte
- Ändra produktionskod (utom `Seo.kt` i uppgift 12), lägga till beroenden eller röra Garantivalvet.
