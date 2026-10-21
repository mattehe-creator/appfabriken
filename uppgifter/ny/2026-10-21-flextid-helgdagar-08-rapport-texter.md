# Uppgift: Etikett för månadens normaltid

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 1
beror-på: 2026-10-21-flextid-helgdagar-07-rapport-normaltid.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/export/ManadsRapportTexter.kt
- flextid/src/main/res/values/strings.xml
- flextid/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ManadsRapport.kt

## Uppgift
1. Lägg till strängen `pdf_normaltid_manad` sist i båda `strings.xml` (före `</resources>`):
   - engelska (`values/strings.xml`): `Normal hours this month (%1$d working days)`
   - svenska (`values-sv/strings.xml`): `Normaltid i månaden (%1$d arbetsdagar)`
2. I `ManadsRapportTexter.kt`, där `ManadsRapportTexter(` skapas: sätt inte etiketten där (antalet dagar är okänt då). Lägg i stället till en funktion i samma fil:

```kotlin
fun normaltidManadEtikett(context: Context, arbetsdagar: Int): String =
    context.getString(R.string.pdf_normaltid_manad, arbetsdagar)
```

## Exempel
- Svenska, 20 → "Normaltid i månaden (20 arbetsdagar)"
- Engelska, 22 → "Normal hours this month (22 working days)"

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra andra filer eller andra strängar.
- Lägga till beroenden.
