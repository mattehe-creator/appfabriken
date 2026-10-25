# Uppgift: FAB-ikoner Att göra och Logg — contentDescription

uppdrag: uppdrag/2026-10-25-husloggen-tillganglighet-strangar.md
försök: 1
beror-på: cursor:2026-10-25-husloggen-tillganglighet-strangar

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt

## Läs som förebild (ändra inte)
- Samma fil: FAB för Plan, Mätare och Dokument (ikon med `contentDescription` från strängresurs).

## Uppgift

I `HusStartSkarm`, för `ExtendedFloatingActionButton` på flikarna **Att göra** och **Logg**: sätt `contentDescription` på `Icon(Icons.Filled.Add, …)` till samma strängresurs som FAB-texten (`fab_ny_uppgift` respektive `fab_ny_post`), i linje med Plan/Mätare/Dokument.

## Exempel
- Att göra-FAB: ikonen har `contentDescription = stringResource(R.string.fab_ny_uppgift)`.
- Logg-FAB: ikonen har `contentDescription = stringResource(R.string.fab_ny_post)`.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "se.tmconnect.husloggen.StringsNycklarTest"

## Gör inte
- Ändra FAB-beteende eller texter.
- Ändra andra filer.
