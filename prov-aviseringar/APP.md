# Aviseringslogg (provapp)

Regler utöver CLAUDE.md. Modul `:prov-aviseringar`, paket `se.tmconnect.prov.aviseringar`, applicationId `se.tmconnect.prov.aviseringar`. Appnamn **Aviseringslogg**.

## Syfte

Privat provapp för Mattias telefon. **Publiceras aldrig** på Google Play. Syftet är att undersöka om aviseringar från skolappar (t.ex. Tempus och InfoMentor) innehåller tillräcklig information för en framtida Skolinkorgen-app.

## Data och integritet

- Ingen `INTERNET`-behörighet. Inget nätverk.
- Aviseringsinnehåll sparas **bara** för paket som användaren kryssat i i inställningar.
- Alla appar som skickat aviseringar räknas (paketnamn, appnamn, antal) utan att innehåll sparas.
- Data stannar på telefonen tills Mattias själv exporterar via systemets filväljare.
- Ingen analys, inga konton, ingen molntjänst i appen.

## Teknik

- `NotificationListenerService` med `BIND_NOTIFICATION_LISTENER_SERVICE`.
- Room för logg och paketräknare. Valda paket i DataStore.
- Tema från `:karna` (`AppfabrikTema`).

## Inte mål

Butikstext, Pro, publicering eller tolkning av datum/tid/klassning (lokal modell i separata uppgifter).
