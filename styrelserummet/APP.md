# Styrelserummet

Regler för webbtjänsten utöver CLAUDE.md. CLAUDE.md punkt 5–6 (offline-Android, Pro-gräns 15 poster, konsumentapp) gäller **inte** här — Styrelserummet är en webbtjänst för bostadsrättsföreningars styrelser och revisorer.

## Vad tjänsten gör

Hjälper en bostadsrättsförening att ta fram ett **utkast** till komponentlista enligt K3 utifrån underhållsplan och årsredovisning, med anskaffningsvärden och nyttjandeperioder. Utkastet lämnas till revisor eller ekonomisk förvaltare; **beslut om indelning, värden och perioder fattas av föreningen och revisorn**, inte av tjänsten.

All export och sammanställning ska tydligt märkas som utkast.

## Målgrupp

Styrelseledamöter och ekonomiska förvaltare i bostadsrättsföreningar. Självbetjäning på webben, inga telefonsamtal som del av produkten.

## Modul

Ren Kotlin/JVM (`:styrelserummet`), paket `se.tmconnect.styrelserummet`. Ingen Android-kod. Belopp i **hela kronor** (`Long`), ingen flyttalsräkning på pengar.

Domänregler och K3-sammanfattning: `styrelserummet/regler/K3.md`. Standardförslag för nyttjandeperioder och värdeandelar: `src/main/resources/nyttjandeperioder.csv` (användaren kan alltid ändra).

## Avsteg från portföljens appregler

| CLAUDE.md | Styrelserummet |
|---|---|
| Inget nätverk / offline | Webb, server och AI kommer i senare uppdrag |
| Google Play, Pro 49 kr | Abonnemang eller engång — ej beslutat; ej Play |
| Room, Compose | Ingen databas i denna modul; persistence senare |
| Svenska + engelska från start | Svenska först; export och domän på svenska |

## Osäkerheter

Se `regler/K3.md`, avsnitt **Osäkert**. Cursor verifierar regler mot BFNAR 2012:1 och BFN:s vägledning för bostadsrättsföreningar innan kod skrivs.
