beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Kontrast.kt`, `KontrastTest.kt` och uppgiftsflytt (pagar→klar) ändrade; korrekta sökvägar under `styrelserummet-server/`.
- `kontrastforhallande` implementerar WCAG relativ luminans, `#rgb`/`#rrggbb`, avrundning half-up till en decimal; exemplen i uppgiften täcks av test.
- CSS-test läser `static/gemensam.css` från classpath, `:root` och `@media (prefers-color-scheme: dark)`, kräver ≥4,5 respektive ≥3,0; saknad variabel ger feltext med variabelnamnet.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- Tester använder JUnit 4 (`org.junit`); `:styrelserummet-server:testDebugUnitTest` grönt.

## Rättat av Cursor
- `KontrastTest.kt`: bytte från JUnit Jupiter/kotlin.test till JUnit 4 så testerna körs med `useJUnit()`.
- CSS-parser: rätt mörkt läge (`prefers-color-scheme: dark`), variabelnamn med bindestreck (`--accent-text`), nycklar med `--`-prefix, krav som `>=` i stället för exakt `assertEquals(4.5, …)`.
- Tog bort filsystem-fallback vid CSS-läsning; endast classpath enligt uppgiften.
- `Kontrast.kt`: borttagna oanvända imports.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
