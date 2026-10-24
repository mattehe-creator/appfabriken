const tbody = document.querySelector("#atgarder tbody");
document.getElementById("franAr").value = String(new Date().getFullYear() + 1);

function tal(text) {
  const rensad = String(text).replace(/\s| /g, "").replace("−", "-").replace(",", ".");
  if (rensad === "") return null;
  const v = Number(rensad);
  return Number.isFinite(v) ? Math.round(v) : NaN;
}

function procent(text) {
  const rensad = String(text).replace(/\s| /g, "").replace("−", "-").replace(",", ".");
  if (rensad === "") return null;
  const v = Number(rensad);
  return Number.isFinite(v) ? v : NaN;
}

function nyRad(ar = "", belopp = "", text = "") {
  const tr = document.createElement("tr");
  const n = tbody.children.length + 1;
  tr.innerHTML = `
    <td><input data-f="ar" type="number" min="1990" max="2100" aria-label="År, rad ${n}"></td>
    <td class="tal"><input data-f="belopp" inputmode="numeric" aria-label="Belopp, rad ${n}"></td>
    <td><input data-f="beskrivning" maxlength="200" aria-label="Åtgärd, rad ${n}"></td>
    <td><button type="button" class="knapp-mini" aria-label="Ta bort rad ${n}">Ta bort</button></td>`;
  tr.querySelector('[data-f="ar"]').value = String(ar);
  tr.querySelector('[data-f="belopp"]').value = String(belopp);
  tr.querySelector('[data-f="beskrivning"]').value = text;
  tr.querySelector("button").addEventListener("click", () => tr.remove());
  tbody.appendChild(tr);
}

document.getElementById("ny-rad").addEventListener("click", () => nyRad());
document.getElementById("exempel").addEventListener("click", () => {
  const start = tal(document.getElementById("franAr").value) || new Date().getFullYear() + 1;
  tbody.replaceChildren();
  nyRad(start, 100000, "Takvård");
  nyRad(start + 2, 600000, "Fönster");
  nyRad(start + 2, 300000, "Balkonger");
  nyRad(start + 4, 200000, "Ventilation");
  nyRad(start + 14, 4500000, "Stambyte");
  document.getElementById("fond").value = "300000";
});
nyRad();

let csvUrl = null;

document.getElementById("fond-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const svar = document.getElementById("svar");
  svar.classList.remove("fel");
  const atgarder = [];
  for (const tr of tbody.querySelectorAll("tr")) {
    const ar = tal(tr.querySelector('[data-f="ar"]').value);
    const belopp = tal(tr.querySelector('[data-f="belopp"]').value);
    const beskrivning = tr.querySelector('[data-f="beskrivning"]').value;
    if (ar === null && belopp === null) continue;
    if (ar === null || belopp === null || Number.isNaN(ar) || Number.isNaN(belopp)) {
      svar.textContent = "Varje åtgärd behöver år och belopp.";
      svar.classList.add("fel");
      return;
    }
    atgarder.push({ ar, belopp, beskrivning });
  }
  const avs = tal(document.getElementById("avsattning").value);
  const pris = procent(document.getElementById("prisupprakning").value);
  const okning = procent(document.getElementById("avsattning-okning").value);
  const body = {
    franAr: tal(document.getElementById("franAr").value),
    antalAr: tal(document.getElementById("antalAr").value),
    ingaendeFond: tal(document.getElementById("fond").value) || 0,
    arligAvsattning: avs === null || Number.isNaN(avs) ? null : avs,
    arligPrisupprakningProcent: pris === null || Number.isNaN(pris) ? null : pris,
    avsattningOkningProcent: okning === null || Number.isNaN(okning) ? null : okning,
    atgarder,
  };
  svar.textContent = "Räknar…";
  const res = await fetch("/api/fondplan", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) {
    svar.textContent = json.fel || "Fondplanen kunde inte räknas.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "";
  visa(json, body.arligAvsattning !== null);
});

function visa(json, provad) {
  const ul = document.getElementById("sammanfattning");
  ul.replaceChildren();
  const punkter = [
    `Minsta årliga avsättning så att fonden aldrig blir negativ: ${json.minstaAvsattning}.`,
    `Jämn avsättning som täcker hela perioden totalt: ${json.jamnAvsattning}.`,
  ];
  if (provad) {
    punkter.push(
      json.forstaUnderskottsar
        ? `Med ${json.valdAvsattning} per år räcker fonden inte år ${json.forstaUnderskottsar}.`
        : `Med ${json.valdAvsattning} per år räcker fonden hela perioden.`,
    );
  }
  punkter.forEach((t) => {
    const li = document.createElement("li");
    li.textContent = t;
    ul.appendChild(li);
  });
  const kropp = document.querySelector("#utveckling tbody");
  kropp.replaceChildren();
  json.utveckling.forEach((r) => {
    const tr = document.createElement("tr");
    if (r.underskott) tr.classList.add("osaker");
    [String(r.ar), r.avsattning, r.uttag, r.utgaende].forEach((v, i) => {
      const td = document.createElement("td");
      if (i > 0) td.className = "tal";
      td.textContent = v;
      tr.appendChild(td);
    });
    kropp.appendChild(tr);
  });
  if (csvUrl) URL.revokeObjectURL(csvUrl);
  csvUrl = URL.createObjectURL(new Blob(["\uFEFF" + (json.csv || "")], { type: "text/csv;charset=utf-8" }));
  document.getElementById("csv").href = csvUrl;
  document.getElementById("anmarkning").textContent = json.anmarkning;
  document.getElementById("resultat").hidden = false;
}

// Fyll i åtgärderna från ett K3-utkast när sidan öppnas med ?jobb=ID.
(async () => {
  const jobb = new URLSearchParams(location.search).get("jobb");
  if (!jobb || !/^[0-9a-f]{32}$/.test(jobb)) return;
  const res = await fetch(`/api/jobb/${encodeURIComponent(jobb)}/atgarder`).catch(() => null);
  if (!res || !res.ok) return;
  const json = await res.json().catch(() => null);
  if (!json || !Array.isArray(json.atgarder) || !json.atgarder.length) return;
  document.getElementById("franAr").value = String(json.franAr);
  tbody.replaceChildren();
  json.atgarder.forEach((a) => nyRad(a.ar, a.belopp, a.beskrivning || ""));
  const svar = document.getElementById("svar");
  svar.textContent = `${json.atgarder.length} åtgärder hämtade från utkastet. Fyll i fondens storlek och tryck Räkna.`;
})();
