beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast tillåtna filer ändrade efter granskning; felplacerad `KvittoLager.kt` i repots rot borttagen.
- `beraknaInSampleSize` och tvåstegs avkodning i `KvittoLager.kopieraFranUri` matchar uppgiften (bounds först, sedan decode med `inSampleSize`).
- Testerna (`KvittoSamplingTest`) har ett testfall per exempel och jämför mot förväntade värden.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade användarsträngar.

## Rättat av Cursor
- Tog bort `KvittoLager.kt` i repots rot (fel sökväg).
- Kompileringsfel: bytte `val inSampleSize` till `sampleSize` så `BitmapFactory.Options.inSampleSize` kan sättas utan skuggning.
- `beraknaInSampleSize`: tidig retur när `max(bredd, hojd) <= maxSida` i stället för `||` på varje dimension.
- Delade upp `KvittoSamplingTest` till ett `@Test` per exempel enligt uppgiften.

## Frågor till Claude
- inga
