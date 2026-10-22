function tal(id) {
  const rensad = String(document.getElementById(id).value).replace(/\s| /g, "").replace(",", ".");
  if (rensad === "") return 0;
  const v = Number(rensad);
  return Number.isFinite(v) ? Math.round(v) : NaN;
}

/** Rader med andelstal till heltal med samma skala. null = ogiltig rad. */
function andelstal() {
  const rader = document.getElementById("andelstal").value.split(/\r?\n/)
    .map((r) => r.replace(/\s| /g, "").replace(",", ".")).filter((r) => r !== "");
  if (rader.some((r) => !/^\d+(\.\d+)?$/.test(r))) return null;
  const decimaler = Math.max(0, ...rader.map((r) => (r.split(".")[1] || "").length));
  const tal = rader.map((r) => {
    const [hel, del = ""] = r.split(".");
    return Number(hel + del.padEnd(decimaler, "0"));
  });
  return tal.every(Number.isSafeInteger) ? tal : null;
}

document.getElementById("exempel").addEventListener("click", () => {
  const v = (id, x) => { document.getElementById(id).value = x; };
  v("drift", "3100000"); v("kapital", "1520000"); v("avsattning", "600000");
  v("ovriga", "250000"); v("nuvarande", "4200000"); v("kvm", "5200");
  v("andelstal", "0,0215\n0,0184\n0,0231\n0,0198");
});

document.getElementById("avgift-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const svar = document.getElementById("svar");
  svar.classList.remove("fel");
  const body = {
    driftkostnader: tal("drift"), kapitalkostnader: tal("kapital"), avsattning: tal("avsattning"),
    ovrigaIntakter: tal("ovriga"), nuvarandeArsavgifter: tal("nuvarande"), kvmBostadsratt: tal("kvm"),
  };
  const andelar = andelstal();
  if (andelar === null) {
    svar.textContent = "Andelstalen ska vara tal, ett per rad.";
    svar.classList.add("fel");
    return;
  }
  if (Object.values(body).some((x) => Number.isNaN(x))) {
    svar.textContent = "Fyll i beloppen med siffror.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "Räknar…";
  const res = await fetch("/api/avgift", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ ...body, andelstal: andelar }) });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) {
    svar.textContent = json.fel || "Det gick inte att räkna.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "";
  const ul = document.getElementById("sammanfattning");
  ul.replaceChildren();
  [
    `Behövda årsavgifter: ${json.behov}`,
    `Per kvm och månad: ${json.behovPerKvmManad} (i dag ${json.nuvarandePerKvmManad})`,
    `Förändring mot dagens avgifter: ${json.forandring}`,
  ].forEach((t) => {
    const li = document.createElement("li");
    li.textContent = t;
    ul.appendChild(li);
  });
  const kropp = document.querySelector("#lagenheter tbody");
  kropp.replaceChildren();
  (json.lagenheter || []).forEach((l) => {
    const tr = document.createElement("tr");
    [String(l.nr), l.ar, l.manad].forEach((t, i) => {
      const td = document.createElement(i === 0 ? "th" : "td");
      if (i === 0) td.scope = "row"; else td.className = "tal";
      td.textContent = t;
      tr.appendChild(td);
    });
    kropp.appendChild(tr);
  });
  document.getElementById("lagenheter-ram").hidden = !(json.lagenheter || []).length;
  document.getElementById("anmarkning").textContent = json.anmarkning;
  document.getElementById("resultat").hidden = false;
});
