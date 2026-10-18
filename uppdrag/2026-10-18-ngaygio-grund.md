# Uppdrag: Ngày giỗ, grunden (ny appmodul, månkalender, databas, lista)

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och ngaygio/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

App 4 är Ngày giỗ (arbetsnamn) för Vietnam: familjens dödsdagsminnen och högtider efter den vietnamesiska månkalendern (IDEURVAL.md och BESLUTSLOGG.md 2026-10-18). Det här uppdraget lägger grunden: modul, omräkning mellan sol- och månkalender, databas version 1 och en lista över kommande dagar. Formulär, högtider, påminnelser, Pro och delning kommer i senare uppdrag.

## Beslut som redan är fattade

- Modul `:ngaygio`, `namespace` och `applicationId` `se.tmconnect.ngaygio`. Appnamnet är "Ngày giỗ" på vietnamesiska och "Ngay Gio" på engelska tills Mattias beslutar annat.
- Samma versioner, inställningar och release-bygge som `husloggen/build.gradle.kts`. Beroenden nu: `:karna`, Compose, activity-compose, Room med KSP och schemaexport till `ngaygio/schemas/`, lifecycle. Inga andra.
- Ingen `INTERNET`. Lista den sammanslagna manifestets behörigheter i PR-beskrivningen.
- Språk: engelska i `values/strings.xml`, vietnamesiska i `values-vi/strings.xml`. Alla vietnamesiska strängar läggs också i `ngaygio/sprak/GRANSKA.md` med status "ny".
- Databasen `GioDatabas`, version 1, med alla fält som 1.0 behöver:
  - `ngay`: `id`, `namn`, `typ` (`GIO`, `SINH_NHAT`, `KHAC`), `am_ngay` (Int 1–30), `am_thang` (Int 1–12), `am_nhuan` (Boolean), `nam` (Int?, året då det hände, för "lần thứ N"), `paminnelse_dagar` (String, kommaseparerade dagar före, standard "7,1"), `anteckning` (String?), `skapad` (Long).
  - `hogtid_av`: `nyckel` (String, primärnyckel) för högtider som användaren har slagit av.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

PR 1 med uppgiftsfiler för del B (`beror-på: cursor:2026-10-18-ngaygio-grund`), sedan PR 2 med del A.

## Del A (Cursor)

1. Modulen: Gradle, manifest, R8-regler, enkel adaptiv ikon (en lykta eller ett rökelsekar, egen ritning i temats färger), strängar för det som del A visar.
2. Omräkningen i `regler/AmLich.kt`: `tillAm(LocalDate): AmDatum` och `tillDuong(AmDatum): LocalDate?` (null om datumet inte finns), med `AmDatum(ngay, thang, nam, nhuan)`. Hồ Ngọc Đức's algoritm, tidszon UTC+7, 1900–2100. Tester mot kända datum: Tết 2024-02-10, 2025-01-29, 2026-02-17, 2027-02-06, Tết 1985 = 1985-01-21 (skiljer sig från den kinesiska kalendern), skottmånad 2 år 2023 och skottmånad 6 år 2025, samt att `tillDuong(tillAm(d)) == d` för varje dag 1900–2100. Ta minst tio fler kontrolldatum ur en publicerad vietnamesisk tabell och ange källan i testet.
3. Databasen: entiteter, typkonverterare, DAO med `Flow`, repository, schema 1, Robolectric-test.
4. `MainActivity`, `GioViewModel` och en startsida med listan över egna dagar sorterade efter nästa soldatum, och en tomtext. Temat från `:karna`.

## Del B (uppgifter för den lokala modellen)

Förslag, högst 3 filer och test per uppgift, i `ngaygio/.../regler/`.

- **Nästa tillfälle**: `nastaTillfalle(ngay: Int, thang: Int, nhuan: Boolean, fran: LocalDate): LocalDate`. Skottmånad räknas som vanlig månad; dag 30 i en månad med 29 dagar blir dag 29. Exempel med Tết: (1, 1, false, 2026-01-01) → 2026-02-17; (1, 1, false, 2026-02-18) → 2027-02-06.
- **Vilket år i ordningen**: `lanThu(nam: Int?, amNamNu: Int): Int?`, null om året saknas.
- **Dagar kvar**: `dagarKvar(nasta, idag)` och en text "hôm nay", "ngày mai", "còn N ngày" via strängresurser.
- **Raden**: namn, månadatum ("15/7 âm lịch"), soldatum och dagar kvar. Tryckyta minst 48 dp.
- **Strängar**: typerna och tomtexten på engelska och vietnamesiska, med test som `StringsNycklarTest`, och raderna i `GRANSKA.md`.

## Klart när

- Bygget och testerna är gröna för alla moduler, och `senaste-main` får en `ngaygio-<sha>.apk`.
- Manifestet för `:ngaygio` saknar `INTERNET`.
- Omräkningen klarar alla kontrolldatum och rundturen 1900–2100.
- `GRANSKA.md` innehåller alla vietnamesiska strängar.

## Gör inte

- Ändra `garantivalvet/`, `husloggen/` eller `flextid/`.
- Lägga till beroenden utöver ovan.
- Formulär, högtider, påminnelser, Pro, delning eller månadsvy.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
