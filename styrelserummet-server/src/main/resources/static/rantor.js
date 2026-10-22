function tal(text) {
  const rensad = String(text).replace(/\s| /g, "").replace(",", ".");
  if (rensad === "") return null;
  const v = Number(rensad);
  return Number.isFinite(v) ? Math.round(v) : NaN;
}

document.getElementById("exempel").addEventListener("click", () => {
  document.getElementById("skulder").value = "38000000";
  document.getElementById("arsavgifter").value = "4200000";
  document.getElementById("kvm").value = "5200";
});

document.getElementById("rantor-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const svar = document.getElementById("svar");
  svar.classList.remove("fel");
  const skulder = tal(document.getElementById("skulder").value);
  const arsavgifter = tal(document.getElementById("arsavgifter").value);
  const kvm = tal(document.getElementById("kvm").value);
  if (skulder === null || arsavgifter === null || Number.isNaN(skulder) || Number.isNaN(arsavgifter) || Number.isNaN(kvm)) {
    svar.textContent = "Fyll i skulder och årsavgifter med siffror.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "Räknar…";
  const res = await fetch("/api/rantor", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ skulder, arsavgifter, kvmBostadsratt: kvm || 0 }),
  });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) {
    svar.textContent = json.fel || "Det gick inte att räkna.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "";
  const tbody = document.querySelector("#tabell tbody");
  tbody.replaceChildren();
  json.rader.forEach((r) => {
    const tr = document.createElement("tr");
    [r.hojning, r.perAr, r.perKvmManad, r.avgiftshojning].forEach((v, i) => {
      const cell = document.createElement(i === 0 ? "th" : "td");
      if (i === 0) cell.scope = "row";
      else cell.className = "tal";
      cell.textContent = v;
      tr.appendChild(cell);
    });
    tbody.appendChild(tr);
  });
  document.getElementById("anmarkning").textContent = json.anmarkning;
  document.getElementById("resultat").hidden = false;
});
