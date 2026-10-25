# Uppgift: Bostadsskärmar — tryckyta 48 dp

uppdrag: uppdrag/2026-10-25-husloggen-tillganglighet-strangar.md
försök: 1
beror-på: cursor:2026-10-25-husloggen-tillganglighet-strangar

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/BostadSkarmar.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/Tryckyta.kt

## Uppgift

I `BostadSkarmar.kt`: varje `TextButton` ska ha `modifier = Modifier.tryckyta48()` (eller `heightIn(min = 48.dp)` där det redan används konsekvent). Gäller dialogen för att byta bostad, listan under Inställningar (Ändra), bekräftelsedialoger vid borttagning och knapparna i `BostadFormularDialog`.

Kontrollera att inga nya hårdkodade användartexter introduceras; använd befintliga `stringResource(R.string.*)`.

## Exempel
- Byt-bostad-dialogen: Stäng-knappen har minst 48 dp tryckyta.
- Raden «Ändra» vid en bostad har minst 48 dp tryckyta.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "se.tmconnect.husloggen.StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Ändra texter eller beteende i dialogerna.
