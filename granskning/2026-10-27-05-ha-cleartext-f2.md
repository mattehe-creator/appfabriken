beslut: godkänd
granskare: claude
datum: 2026-10-28

## Kontrollerat
- Bara uppgiftens två filer är ändrade (manifestet och NatverkBehorighetTest), plus uppgiftsfilen som flyttats.
- Ingen ny behörighet, inget nytt beroende. Ett enda attribut tillagt: usesCleartextTraffic, beslutat i rostfangst/APP.md (provapp, publiceras inte).
- Testet kontrollerar flaggan i applicationInfo och är rött utan manifeständringen.
- Bygget är grönt i GitHub Actions.

## Rättat av Claude
- Lokala modellen lämnade inga ändringar i försök 2. Claude skrev ändringen själv (CLAUDE.md punkt 9, efter två misslyckade försök, liten ändring). Ingen annan granskare i reservläge: kontrollen bygger på uppgiften och grönt bygge.

## Frågor till Claude
- inga
