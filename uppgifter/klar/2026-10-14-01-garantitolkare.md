# Uppgift: läs garantitiden ur kvittotexten

uppdrag: ingen (Mattias önskemål 2026-10-14: appen ska föreslå garantitid när kvittot anger den)
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GarantiTolkare.kt (ny)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/GarantiTolkareTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/OppetKopTolkare.kt (stil: ren Kotlin, regex, ingen Android)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoTolkare.kt

## Uppgift
Skapa en ren funktion (ingen Android, inga beroenden) i paketet `se.tmconnect.garantivalvet.regler`:

```kotlin
fun tolkaGarantiManader(text: String): Int?
```

Den läser garantitiden i månader ur kvittotexten, eller ger `null` om inget tydligt anges.

Regler:
1. Dela texten i rader och gör dem till gemener. Bara rader som innehåller något av orden `garanti` eller `warranty` (som delsträng, så "garantitid" och "garantin" räknas) kan ge ett svar. Hoppa över en rad som innehåller `reklamationsrätt`, `ingen garanti`, `utan garanti` eller `garanti ingår ej`.
2. På en sådan rad letar du efter alla förekomster av ett **antal** följt av en **enhet**, med högst ett blanksteg eller en punkt emellan:
   - antal: ett heltal (siffror), eller orden `ett`, `en`, `två`, `tre`, `fyra`, `fem`, `sex`, `sju`, `åtta`, `nio`, `tio` (1 till 10).
   - enhet i år: `år`, `års`, `ar`, `year`, `years`, `yr`, `yrs`.
   - enhet i månader: `månad`, `månader`, `månaders`, `manader`, `mån`, `month`, `months`.
   - Enheten ska vara ett helt ord (efter enheten kommer inget bokstavstecken). Enheten "dagar" och andra enheter ger ingen träff.
3. Antal år blir antal × 12 månader; antal månader är månader. Ett resultat är giltigt bara om det är mellan 1 och 120 månader, annars ignoreras träffen.
4. Finns flera giltiga träffar väljs den som ligger **närmast** ordet `garanti`/`warranty` på samma rad (avstånd i tecken mellan träffen och ordet, ignorera vilket håll). Vid lika avstånd väljs den första raden och den första träffen. Svaret är den bästa träffen över alla rader.
5. En tydlig KDoc förklarar reglerna kort, på svenska.

## Exempel (alla ska bli testfall i `GarantiTolkareTest`)
- "3 års garanti" → 36
- "Garanti: 24 månader" → 24
- "Garantitid 12 mån." → 12
- "2 år garanti" → 24
- "Ett års garanti" → 12
- "GARANTI 2 AR" → 24
- "Warranty: 2 years" → 24
- "Köp 24 månader räntefritt, 3 års garanti" → 36 (träffen "3 års" ligger närmast ordet garanti)
- "Elgiganten\n2026-08-01\nSumma 1 299,00\n3 års garanti" → 36
- "6 månaders garanti" → 6
- "Garanti 30 dagar" → null
- "3 års reklamationsrätt" → null
- "Ingen garanti" → null
- "Garanti: 0 månader" → null
- "Garanti 200 år" → null
- "Garanti\n3 år" → null (antal och enhet står på en annan rad än ordet garanti)
- "24 månader" → null (ingen rad med garanti)
- "" → null

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*GarantiTolkareTest"
gradle testDebugUnitTest

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-15 (JUnit-import, regex för svenska enheter, return@forEach)
