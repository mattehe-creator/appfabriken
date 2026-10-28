beslut: godkänd
granskare: claude
datum: 2026-10-28

## Kontrollerat
- Ändrar inget utanför uppgiften: bara de filer som uppgiften anger (och uppgiftsfilen som flyttats).
- Ingen ny behörighet, inget nytt beroende och inget nätverksanrop.
- Ingen hårdkodad text, inga hemligheter och ingen token i loggar.
- Ingen databasändring.
- Testerna kontrollerar beteendet i uppgiften: tomma värden från start, trimning vid sparande och att apiNyckel inte påverkas av sparaHa.
- Bygget är grönt i GitHub Actions.

## Rättat av Claude
- inget

## Frågor till Claude
- inga
