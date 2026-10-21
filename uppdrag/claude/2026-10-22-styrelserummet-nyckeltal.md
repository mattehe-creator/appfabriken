# Uppdrag: Styrelserummet — nyckeltal till förvaltningsberättelsen

Skrivet av Claude 2026-10-22. Uppgifterna skrivs direkt av Claude i `uppgifter/ny/`. Bara modulen `:styrelserummet`.

## Bakgrund

Sedan 2023 ska bostadsrättsföreningar lämna nyckeltal i förvaltningsberättelsen (6 kap. 3 a § ÅRL och BFNAR 2023:1), för året och tre år bakåt. Definitionerna kontrollerades mot BFNAR 2023:1 punkterna 6–11 den 2026-10-22:

- Skuldsättning per kvm: räntebärande skulder på balansdagen / (kvm bostadsrätt + kvm hyresrätt). Även per kvm upplåten med bostadsrätt (bara bostadsrättsyta).
- Sparande per kvm: justerat resultat / (kvm bostadsrätt + kvm hyresrätt). Justerat resultat = årets resultat + avskrivningar + utrangeringar + planerat underhåll, justerat för väsentliga poster utanför den normala verksamheten.
- Räntekänslighet: räntebärande skulder / årsavgifter under året (en kvot).
- Energikostnad per kvm: värme + el + vatten / (kvm bostadsrätt + kvm hyresrätt).
- Årsavgifternas andel av totala rörelseintäkter, i procent.
- Årsavgift per kvm upplåten med bostadsrätt: årsavgifter / kvm bostadsrätt.

Det är samma underlag (årsredovisningen) som tjänsten redan läser, och samma kunder. Räkningen görs av kod; AI läser bara handlingarna.

## Uppgift

Rena funktioner med heltalsräkning och avrundning till närmaste (halva bort från noll): kronor per kvm i hela kronor, räntekänslighet i tiondelar, andelen i tiondels procent. Sist en sammanställning med svenska etiketter.

## Klart när

Alla uppgifter mergade med gröna tester.

## Gör inte

- Inga råd om avgifter. Utdata är utkast.
- Servern rörs inte.
