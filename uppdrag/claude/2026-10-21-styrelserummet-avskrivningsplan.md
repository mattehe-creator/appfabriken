# Uppdrag: Styrelserummet — avskrivningsplan och utbyten

Skrivet av Claude 2026-10-21. Uppgifterna skrivs direkt av Claude i `uppgifter/ny/`. Bara modulen `:styrelserummet` (ren Kotlin); servern rörs inte, eftersom den lokala modellen arbetar där med provklar-uppdraget. Claude kopplar in planen i servern och utskriften efteråt.

## Bakgrund

Revisorn och förvaltaren behöver inte bara årets avskrivning utan en plan framåt: avskrivning och bokfört värde per år de kommande tio åren. Föreningen behöver den för budget och avgiftsnivå. När underhållsplanen säger att en komponent byts ett visst år ska det gamla restvärdet utrangeras (kostnadsföras) och den nya komponenten skrivas av från början. `avskrivningFor` räknar i dag med flyttal, vilket APP.md förbjuder för pengar.

## Uppgift

1. `avskrivningFor` med heltalsavrundning, samma resultat som i dag.
2. Avskrivningar per år för en komponent.
3. Plan för hela listan: avskrivning och bokfört värde per år.
4. Utrangeringsbelopp vid utbyte.
5. Ersätt en komponent vid utbyte.
6. Planen som CSV, märkt som utkast.

## Klart när

Alla uppgifter mergade med gröna tester, och alla befintliga tester i `:styrelserummet` gröna.

## Gör inte

- Ändra servern (`:styrelserummet-server`).
- Nya beroenden.
- Ändra avskrivningsprincipen (helt år från anskaffningsåret, linjärt, sista året tar resten).
