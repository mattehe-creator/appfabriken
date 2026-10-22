# Uppgift: Filtrera loggposter

uppdrag: uppdrag/claude/2026-10-22-husloggen-sok-forbrukning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/LoggFilter.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/LoggFilterTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Post.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/PostTyp.kt

## Uppgift
```kotlin
fun filtreraPoster(poster: List<Post>, sok: String, typ: PostTyp?): List<Post>
```

- `sok` trimmas. Tom sökning matchar alla.
- En post matchar sökningen om något av fälten `vad`, `utforare` eller `anteckning` innehåller **alla** ord i sökningen (ord = delar av sökningen åtskilda av blanksteg), utan hänsyn till versaler och gemener (`lowercase(Locale.ROOT)`). Orden får finnas i olika fält.
- `typ` null matchar alla typer; annars bara poster med den typen.
- Ordningen i listan behålls.

## Exempel
Poster: (1, "Service värmepump", REPARATION, utförare "Kyl & Värme AB"), (2, "Målning fasad söder", UNDERHALL, anteckning "Två lager"), (3, "Ny altan", OMBYGGNAD, utförare "Byggfirma").
- ("värmepump", null) → [1]
- ("VÄRME kyl", null) → [1]
- ("byggfirma", OMBYGGNAD) → [3]
- ("", UNDERHALL) → [2]
- ("lager fasad", null) → [2]
- ("altan", REPARATION) → []

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*LoggFilterTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-22 (JUnit-assertioner i LoggFilterTest)
