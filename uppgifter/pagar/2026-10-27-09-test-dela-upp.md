# Uppgift: Test av delaUpp (Styrelserummet)

uppdrag: uppdrag/claude/2026-10-27-testtackning-kvall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/DelaUppTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/DelaUpp.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `org.junit.Assert.assertEquals`, `assertThrows`) i paketet `se.tmconnect.styrelserummet.underlag` för `delaUpp(sidor: List<String>, maxTecken: Int): List<Del>`. Sidor slås ihop med `"\n\n"` emellan så länge delen inte blir längre än `maxTecken`. Sidnumren börjar på 1.

## Exempel
- `delaUpp(listOf("a", "b", "c"), 10)` ger `[Del(listOf(1, 2, 3), "a\n\nb\n\nc")]`.
- `delaUpp(listOf("aa", "bb", "cc"), 4)` ger `[Del(listOf(1), "aa"), Del(listOf(2), "bb"), Del(listOf(3), "cc")]`.
- `delaUpp(listOf("aa", "bb"), 6)` ger `[Del(listOf(1, 2), "aa\n\nbb")]` (exakt 6 tecken ryms).
- `delaUpp(listOf("a", "xxxxxx", "b"), 5)` ger `[Del(listOf(1), "a"), Del(listOf(2), "xxxxxx"), Del(listOf(3), "b")]` (en för lång sida blir en egen del).
- `delaUpp(emptyList(), 5)` ger en tom lista.
- `delaUpp(listOf("a"), 0)` kastar `IllegalArgumentException`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*DelaUppTest"

## Gör inte
- Ändra produktionskod eller andra filer än den nya testfilen.
- Lägga till beroenden.
