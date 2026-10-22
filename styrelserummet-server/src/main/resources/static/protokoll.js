// Protokollet byggs helt i webbläsaren. Ingenting skickas till servern och ingenting sparas.
const punkterEl = document.getElementById("punkter");

function nyPunkt(rubrik = "", text = "", beslut = "") {
  const n = punkterEl.children.length + 1;
  const div = document.createElement("div");
  div.className = "yta punkt";
  div.innerHTML = `
    <div class="falt"><label>Rubrik</label><input data-f="rubrik"></div>
    <div class="falt"><label>Anteckning (valfritt)</label><textarea data-f="text" rows="3"></textarea></div>
    <div class="falt"><label>Beslut (valfritt)</label><textarea data-f="beslut" rows="2"></textarea></div>
    <button type="button" class="knapp-mini">Ta bort punkten</button>`;
  const [r, t, b] = div.querySelectorAll("input, textarea");
  r.setAttribute("aria-label", `Rubrik, punkt ${n}`);
  t.setAttribute("aria-label", `Anteckning, punkt ${n}`);
  b.setAttribute("aria-label", `Beslut, punkt ${n}`);
  r.value = rubrik;
  t.value = text;
  b.value = beslut;
  div.querySelector("button").addEventListener("click", () => div.remove());
  punkterEl.appendChild(div);
}
nyPunkt();
document.getElementById("ny-punkt").addEventListener("click", () => nyPunkt());

document.getElementById("exempel").addEventListener("click", () => {
  const v = (id, x) => { document.getElementById(id).value = x; };
  v("forening", "Brf Exempelgården");
  v("motesnr", "Styrelsemöte nr 7");
  v("datum", "2026-11-12 kl. 18.30");
  v("plats", "Föreningslokalen");
  v("narvarande", "Anna Andersson, ordförande\nBo Berg\nCecilia Carlsson\nDavid Dahl");
  v("ovriga", "");
  v("ordforande", "Anna Andersson");
  v("sekreterare", "Bo Berg");
  v("justerare", "Cecilia Carlsson");
  v("nasta", "2026-12-10 kl. 18.30");
  punkterEl.replaceChildren();
  nyPunkt("Ekonomisk rapport", "Kassören redovisade resultatet till och med oktober. Utfallet följer budget.", "Rapporten lades till handlingarna.");
  nyPunkt("Underhållsplan, fönsterbyte", "Två offerter har kommit in.", "Styrelsen beslutade att anta offerten från Fönsterfirman AB.");
  nyPunkt("Övriga frågor", "Inga.", "");
});

function varde(id) { return document.getElementById(id).value.trim(); }

function bygg() {
  const forening = varde("forening");
  const rubrik = varde("motesnr") || "Protokoll fört vid styrelsemöte";
  const narvarande = varde("narvarande").split("\n").map((x) => x.trim()).filter(Boolean);
  const ovriga = varde("ovriga").split("\n").map((x) => x.trim()).filter(Boolean);
  const ordf = varde("ordforande");
  const sekr = varde("sekreterare");
  const just = varde("justerare");
  const egna = [...punkterEl.querySelectorAll(".punkt")]
    .map((d) => ({
      rubrik: d.querySelector('[data-f="rubrik"]').value.trim(),
      text: d.querySelector('[data-f="text"]').value.trim(),
      beslut: d.querySelector('[data-f="beslut"]').value.trim(),
    }))
    .filter((p) => p.rubrik || p.text || p.beslut);

  const paragrafer = [];
  paragrafer.push({ rubrik: "Mötets öppnande", text: ordf ? `${ordf} förklarade mötet öppnat.` : "Mötet förklarades öppnat." });
  paragrafer.push({ rubrik: "Val av mötesordförande, protokollförare och justerare", text: [ordf && `Till mötesordförande valdes ${ordf}.`, sekr && `Till protokollförare valdes ${sekr}.`, just && `Till justerare valdes ${just}.`].filter(Boolean).join(" ") || "–" });
  paragrafer.push({ rubrik: "Godkännande av dagordning", text: "Dagordningen godkändes." });
  paragrafer.push({ rubrik: "Föregående protokoll", text: "Föregående protokoll gicks igenom och lades till handlingarna." });
  egna.forEach((p) => paragrafer.push({ rubrik: p.rubrik || "Punkt", text: p.text, beslut: p.beslut }));
  if (varde("nasta")) paragrafer.push({ rubrik: "Nästa möte", text: `Nästa möte hålls ${varde("nasta")}.` });
  paragrafer.push({ rubrik: "Mötets avslutande", text: ordf ? `${ordf} tackade för visat intresse och förklarade mötet avslutat.` : "Mötet förklarades avslutat." });

  return { forening, rubrik, datum: varde("datum"), plats: varde("plats"), narvarande, ovriga, ordf, sekr, just, paragrafer };
}

function el(tag, text, klass) {
  const e = document.createElement(tag);
  if (text) e.textContent = text;
  if (klass) e.className = klass;
  return e;
}

function visa(p) {
  const art = document.getElementById("protokoll");
  art.replaceChildren();
  if (p.forening) art.appendChild(el("p", p.forening, "etikett"));
  art.appendChild(el("h2", p.rubrik));
  const meta = [p.datum && `Datum: ${p.datum}`, p.plats && `Plats: ${p.plats}`].filter(Boolean).join(" · ");
  if (meta) art.appendChild(el("p", meta, "meta"));
  if (p.narvarande.length) {
    art.appendChild(el("h3", "Närvarande ledamöter"));
    const ul = el("ul");
    p.narvarande.forEach((n) => ul.appendChild(el("li", n)));
    art.appendChild(ul);
  }
  if (p.ovriga.length) {
    art.appendChild(el("h3", "Övriga närvarande"));
    const ul = el("ul");
    p.ovriga.forEach((n) => ul.appendChild(el("li", n)));
    art.appendChild(ul);
  }
  p.paragrafer.forEach((par, i) => {
    art.appendChild(el("h3", `§ ${i + 1} ${par.rubrik}`));
    if (par.text) art.appendChild(el("p", par.text));
    if (par.beslut) {
      const b = el("p");
      b.appendChild(el("strong", "Beslut: "));
      b.appendChild(document.createTextNode(par.beslut));
      art.appendChild(b);
    }
  });
  const sign = el("div", null, "underskrifter");
  [["Vid protokollet", p.sekr], ["Justeras", p.ordf], ["Justeras", p.just]].forEach(([roll, namn]) => {
    const d = el("div", null, "underskrift");
    d.appendChild(el("p", roll));
    d.appendChild(el("p", "………………………………………"));
    d.appendChild(el("p", namn || ""));
    sign.appendChild(d);
  });
  art.appendChild(sign);
  art.hidden = false;
  document.getElementById("protokoll-knappar").hidden = false;
  art.scrollIntoView({ behavior: "smooth" });
}

function somText(p) {
  const rader = [];
  if (p.forening) rader.push(p.forening);
  rader.push(p.rubrik);
  if (p.datum) rader.push(`Datum: ${p.datum}`);
  if (p.plats) rader.push(`Plats: ${p.plats}`);
  if (p.narvarande.length) rader.push("", "Närvarande ledamöter:", ...p.narvarande);
  if (p.ovriga.length) rader.push("", "Övriga närvarande:", ...p.ovriga);
  p.paragrafer.forEach((par, i) => {
    rader.push("", `§ ${i + 1} ${par.rubrik}`);
    if (par.text) rader.push(par.text);
    if (par.beslut) rader.push(`Beslut: ${par.beslut}`);
  });
  rader.push("", `Vid protokollet: ${p.sekr || ""}`, `Justeras: ${p.ordf || ""}`, `Justeras: ${p.just || ""}`);
  return rader.join("\n");
}

let senaste = null;
document.getElementById("protokoll-form").addEventListener("submit", (e) => {
  e.preventDefault();
  senaste = bygg();
  visa(senaste);
});
document.getElementById("skriv-ut").addEventListener("click", () => window.print());
document.getElementById("kopiera").addEventListener("click", async () => {
  const status = document.getElementById("kopiera-status");
  try {
    await navigator.clipboard.writeText(somText(senaste || bygg()));
    status.textContent = "Kopierat.";
  } catch (_) {
    status.textContent = "Kunde inte kopiera. Markera texten och kopiera själv.";
  }
});
