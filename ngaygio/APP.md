# Ngày giỗ (arbetsnamn)

Regler för appen utöver CLAUDE.md. Val i IDEURVAL.md (2026-10-18). Modul `:ngaygio`, paket `se.tmconnect.ngaygio`. Appnamn och applicationId beslutar Mattias vid första publiceringen.

## Vad appen gör

Håller reda på familjens dagar efter den vietnamesiska månkalendern (âm lịch): dödsdagsminnen (giỗ), födelsedagar efter månkalendern och högtider. Visar vilket soldatum dagen infaller på i år och kommande år och påminner i förväg.

## Avvikelser från CLAUDE.md (BESLUTSLOGG.md 2026-10-18)

- Språk: engelska i `values/` och vietnamesiska i `values-vi/`. Ingen svenska.
- Priset på Pro sätts i dong av Mattias i Play Console.

## Begrepp

- **Egen dag**: namn, typ (`GIO`, `SINH_NHAT`, `KHAC`), månadatum (dag, månad, skottmånad ja/nej), valfritt år, anteckning, påminnelse.
- **Högtid**: fast månadatum som appen känner till (Tết 1/1, rằm tháng Giêng 15/1, Tết Hàn thực 3/3, Phật đản 15/4, Tết Đoan ngọ 5/5, Vu Lan 15/7, Trung thu 15/8, ông Công ông Táo 23/12), samt rằm (15) och mùng 1 varje månad. Var och en kan slås av.

## Regler

- Omräkningen följer Hồ Ngọc Đức's algoritm med tidszon UTC+7. Den vietnamesiska kalendern skiljer sig ibland från den kinesiska (Tết 1985 är 1985-01-21 i Vietnam). Koden ligger på ett ställe i `regler/` och testas mot kända datum, bland annat Tết 2024-02-10, 2025-01-29, 2026-02-17, 2027-02-06, skottmånad 2 år 2023 och skottmånad 6 år 2025. Giltigt intervall 1900–2100.
- Giỗ och födelsedag efter månkalendern: firas på samma månadatum varje år. Föll dagen i en skottmånad firas den i den vanliga månaden med samma nummer. Är dagen 30 och månaden har 29 dagar firas den dag 29. (Tao bekräftar konventionerna.)
- Gratisgränsen är 15 egna dagar. Högtider, påminnelser, säkerhetskopia och delning är alltid gratis. Gränsen ligger på ett ställe i `regler/`.
- Delning: listan exporteras som fil via systemets delning och kan importeras av en annan i familjen. Ingen server.
- Ingen `INTERNET`, ingen plats, inga konton. Behörighet bara för notiser.
- Lite text: Tao har ett par timmar i veckan. Varje uppdrag som lägger till strängar uppdaterar `ngaygio/sprak/GRANSKA.md` (tabell: nyckel, engelska, vietnamesiska, status). Ingen vietnamesisk text publiceras ogranskad.
- Inget om religion eller ritualer utöver namnen på dagarna. Appen ger inga råd om hur dagarna firas.
