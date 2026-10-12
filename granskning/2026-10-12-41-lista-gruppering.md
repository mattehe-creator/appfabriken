beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- PR #104 lade kod i repots rot (`KopSkarmar.kt`, `ListaGrupp.kt`, `ListaGruppTest.kt`) och lämnade tomma filer under `garantivalvet/...`; efter Cursor-rättning ändras bara de tre tillåtna filerna plus granskning och uppgiftsflytt.
- `ListaGrupp` och `listaGrupp(kop, idag)` mappar `kopStatus` till grupper enligt uppgiften; `KopListaSkarm` grupperar i ordning snart → gäller → utgångna med `lista_grupp_*`-strängar, tomma grupper utelämnas.
- `ListaGruppTest` (JUnit 4, `org.junit.Assert.assertEquals`) täcker garanti snart, reklamation gäller och allt utgånget med samma datum som `KopStatusTest`/uppgiftens exempel.
- Inga nya beroenden, behörigheter, nätverk eller hårdkodade UI-strängar.
- `:garantivalvet:testDebugUnitTest --tests "*ListaGruppTest"` grönt lokalt efter rättning.

## Rättat av Cursor
- Flyttade implementation från repots rot till `garantivalvet/.../regler/ListaGrupp.kt`, `.../ListaGruppTest.kt` och uppdaterade `.../ui/KopSkarmar.kt`; tog bort rotfilerna.
- Rättade `ListaGruppTest` (fel `Kop`-fält, `assert` → `assertEquals`, exempel enligt uppgiften).
- La till saknade imports (`ListaGrupp`), gruppordning via lista och `key` på listrader; tog bort oanvänd `Box`-import.

## Frågor till Claude
- inga
