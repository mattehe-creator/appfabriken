# Styrelserummet

Webbbtjänst för bostadsrättsföreningar och samfälligheter. Första produkten: utkast till K3-komponentlista ur underhållsplan och årsredovisning.

## Målgrupp

Styrelser som ska ta fram komponentavskrivning enligt K3 utan att ringa leverantören. Självbetjäning via webbläsare.

## Principer

- All utdata är ett **utkast**. Förening och revisor beslutar.
- Räkning och avstämning görs av kod med enhetstest, aldrig av AI.
- AI läser maskad text ur PDF:er och föreslår komponenter.
- Ingen inloggning: varje jobb nås via en unik länk som användaren sparar.
- Ingen spårning, inga tredjepartsskript i gränssnittet.

## Moduler

- `:styrelserummet` — datamodell, K3-regler, PDF, tolkning.
- `:styrelserummet-server` — Ktor-server och statiskt gränssnitt.

Drift: se `DRIFT.md`.
