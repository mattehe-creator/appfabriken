beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Försök 2 från lokal modell: Kotlin-filer i repots rot, tom `husloggen/.../BildHelskarm.kt`, `TODO()` och ogiltiga sträng-/drawable-nycklar i `PostDetaljSkarm`, samt otillåtna ändringar i `verktyg/` och `uppgifter/MALL.md` — uppfyllde inte uppgiften (CLAUDE.md punkt 8).
- Efter Cursors övertagande: endast `BildHelskarm.kt`, `PostDetaljSkarm.kt` och `MainActivity.kt` under `husloggen/` ändrade (plus granskning och uppgiftsflytt).
- `PostDetaljSkarm`: miniatyrrad från FileProvider-URI, 48 dp tryckyta, `onOppnaBild(index)`; inga nya strängnycklar i UI.
- `BildHelskarm`: `HorizontalPager`, zoom som `KvittoHelskarm`, `bild_stang`/`bild_dela`, dela aktuell sida via `pagerState.currentPage`.
- `MainActivity`: lokal navigation med `visaBildHelskarm` före detaljgren; `lasBilderForPost` och `bildUri` kopplade.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade användarsträngar.
- `:husloggen:testDebugUnitTest --tests "*StringsNycklarTest"` grönt lokalt; appmodultester använder JUnit 4.

## Rättat av Cursor
- Hel implementation enligt uppgift (försök 2 underkänt — Cursor gör uppgiften enligt uppgifter/MALL.md).
- Tog bort rotfiler; fyllde modulfiler; återställde `verktyg/` och `uppgifter/MALL.md`.
- Rättade navigationsordning så helskärm visas; bytte till `bild_har_bilder`; `rememberPagerState` med `pageCount`.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
