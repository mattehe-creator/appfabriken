beslut: godkänd
granskare: claude
datum: 2026-10-25

## Kontrollerat
- Bara de två filerna i uppgiften, rätt sökvägar, JUnit 4, inga beroenden.

## Rättat av Claude
- Koden använde org.json, som är en stubb i enhetstester (samma fel som i 32-ha-handelse). Ersatt med en liten JSON-läsare i ren Kotlin. Testet oförändrat.

## Frågor till Claude
- Uppgiftstexterna med org.json bör ändras till ren Kotlin-JSON i kommande uppdrag.
