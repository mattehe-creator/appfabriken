# Föreningsstämma och avgifter — regler för Styrelserummets verktyg

Sammanfattning för tidplan och avgiftskontroll. **Allmän information, inte juridisk rådgivning. Kontrollera era stadgar.**

Källor lästa 2026-10-22: **Bostadsrättslagen (1991:614)**, **lagen (2018:672) om ekonomiska föreningar** (via hänvisningar i BRL), **lag (1989:253) om allmänna helgdagar**, **socialförsäkringsbalken** (prisbasbelopp). Föreningens **stadgar** kan ändra frister där lagen uttryckligen tillåter det — verktyget tar då in fristen som inmatning, inte som fast regel.

## Ordinarie föreningsstämma

Medlemmarna ska hålla en **ordinarie föreningsstämma** (årsstämma) **inom sex månader från utgången av varje räkenskapsår**. Styrelsen ska då lägga fram **årsredovisningen** och **revisionsberättelsen**.

- **Källa:** EFL **6 kap. 9 §** (via BRL **9 kap. 14 §** — bestämmelserna om föreningsstämman i EFL 6 kap. gäller för bostadsrättsföreningar med undantag som anges där).

**Senaste dag för stämman** räknas som **sex kalendermånader** efter räkenskapsårets sista dag (samma datumregler som `LocalDate.plusMonths(6)` — t.ex. 31 augusti + sex månader → sista februari, även i skottår).

## Kallelse till ordinarie stämma

Kallelse ska utfärdas **tidigast sex veckor** och **senast fyra veckor** före stämman.

I **stadgarna** får det bestämmas att kallelse får utfärdas **senare**, men **senast två veckor** före stämman.

- **Källa:** EFL **6 kap. 17 §** (via BRL **9 kap. 14 §**).

Verktyget räknar:

| Utdata | Innebörd |
|---|---|
| Tidigaste kallelsedag | stämmodatum minus **6 veckor** (lagens tidigaste) |
| Senaste kallelsedag | stämmodatum minus **N veckor**, där **N** anges av användaren (**2–4** om stadgar tillåter kortare frist; **4** om stadgar följer lagens normalfrist) |

**Veckor** räknas som **7 kalenderdagar** per vecka (`minusWeeks` / `plusWeeks`).

## Revisionsberättelse till styrelsen

Revisionsberättelsen ska lämnas till styrelsen **senast tre veckor före årsstämman**.

- **Källa:** EFL **8 kap. 32 § första stycket** (via BRL **9 kap. 26 §** — EFL 8 kap. om revision gäller för bostadsrättsföreningar).

## Årsredovisning tillgänglig för medlemmar

Styrelsen ska hålla **årsredovisningen** och **revisionsberättelsen** (kopior räcker) **tillgängliga** för medlemmar **under minst två veckor närmast före årsstämman**, hos föreningen på styrelsens sätesort.

- **Källa:** EFL **6 kap. 23 §** (via BRL **9 kap. 14 §**).

Verktyget anger **första dag** i denna tvåveckorsperiod som stämmodatum minus **2 veckor** (medlemmarna ska kunna ta del från den dagen).

## Avgifter vid överlåtelse och pantsättning

Föreningen får ta ut **överlåtelseavgift** och **pantsättningsavgift** **om det anges i stadgarna**. Storleken **regleras inte i lag** utöver att stadgarna ska ange **grunderna för beräkning**.

- **Källa:** BRL **7 kap. 14 §** (gällande lydelse med pantsättningsavgift), BRL **9 kap. 5 §** punkt 4–5 (stadgar ska bl.a. ange huruvida avgifterna får tas ut och beräkningsgrunder).

**Prisbasbelopp** för beräkningar som knyter an till prisbasbeloppet hämtas från `stamma.csv` (SCB/Försäkringskassan, per år).

### Lagstadgat tak: andrahandsupplåtelse (referens)

Avgift för **andrahandsupplåtelse** får årligen uppgå till **högst 10 procent av prisbasbeloppet** (del av år räknas efter kalendermånader).

- **Källa:** BRL **7 kap. 14 §** tredje stycket / sista meningen om andrahandsupplåtelse; prisbasbelopp enligt **2 kap. 6 och 7 §§ socialförsäkringsbalken**.

Verktyget för överlåtelse/pantsättning jämför mot **stadgarnas procentsats** (inmatning) × prisbasbelopp, inte mot ett lagtak som saknas.

## Svenska helgdagar (datumhjälp)

**Allmänna helgdagar** enligt lag (1989:253) **1 §** och datum i **2 §**: nyårsdagen, trettondedag jul, långfredag, påskdagen, annandag påsk, första maj, Kristi himmelsfärdsdag, nationaldagen, midsommardagen, alla helgons dag, juldagen, annandag jul (samt söndagar inkl. pingst).

Verktygets `arSvenskHelgdag` följer uppdragets lista och räknar **påsk** med **Gauss/Gregorian algoritm** (samma princip som i `flextid`-modulens `paskdagen`).

## Prisbasbelopp i datafil

`styrelserummet/src/main/resources/stamma.csv` innehåller prisbasbelopp per år med källa. Referensvärden för **typiska** stadgar (promille av prisbasbelopp) ligger i samma fil men är **inte lagstadgade tak** — se **Osäkert**.

## Osäkert

- **2,5 % och 1 % av prisbasbeloppet** som “max” för överlåtelse- respektive pantsättningsavgift förekommer i **förarbeten och branschpraxis**, men **finns inte som tak i BRL 7 kap. 14 §**. Bindande nivå står i **stadgarna**. CSV-filens referenspromille är endast för jämförelse när stadgar följer vanliga formuleringar.
- **Midsommarafton** är **inte** allmän helgdag i lag (1989:253) men räknas som helgdag i verktyget enligt uppdraget (motsvarar ofta Skatteverkets kalender); **julafton och nyårsafton** omfattas inte av verktygets lista om de inte anges i uppdraget.
- **Om “veckor före”** ska räknas som kalenderveckor (mån–sön) eller som exakt 7×N dagar — lagtexten anger antal veckor utan kalenderveckodefinition; verktyget använder **7×N kalenderdagar** (ISO-8601-veckor via `minusWeeks`).
- **Utfärdas** kallelse vs **når** medlemmen — postgång och stadgars utskickssätt påverkar praktiken; verktyget räknar bara **datum**.
- **Digital kallelse**, **fullmakt**, **korum** och **vilka ärenden** som måste på dagordningen styrs av stadgar och EFL 6 kap. — ingår inte i tidplanen.
- **Framtida BRL-lydelse** (t.ex. pantsättningsavgift som tas bort i kommande 7 kap. 14 § enligt SFS 2026:486) — verktyget följer **gällande** text vid källgranskning 2026-10-22.

## Källhänvisningar

| Ämne | Källa |
|---|---|
| BRL → EFL föreningsstämma | BRL **9 kap. 14 §** → EFL **6 kap.** |
| Senast sex månader till årsstämma | EFL **6 kap. 9 §** |
| Kallelse ordinarie stämma | EFL **6 kap. 17 §** |
| Årsredovisning tillgänglig | EFL **6 kap. 23 §** |
| Revisionsberättelse till styrelse | EFL **8 kap. 32 §**; BRL **9 kap. 26 §** |
| Avgifter till föreningen | BRL **7 kap. 14 §** |
| Stadgars innehåll om avgifter | BRL **9 kap. 5 §** |
| Allmänna helgdagar | Lag **(1989:253)** **1–2 §§** |
| Prisbasbelopp | **2 kap. 6–7 §§ socialförsäkringsbalken**; belopp per år i **stamma.csv** (SCB) |
