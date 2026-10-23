function tal(id) {
  const rensad = String(document.getElementById(id).value).replace(/\s| /g, "").replace(",", ".");
  if (rensad === "") return null;
  const v = Number(rensad);
  return Number.isFinite(v) ? Math.round(v) : NaN;
}

function datum(id) {
  const v = document.getElementById(id).value;
  return v === "" ? null : v;
}

let csvUrl = null;

document.getElementById("exempel").addEventListener("click", () => {
  document.getElementById("arsslut").value = "2025-12-31";
  document.getElementById("stammodatum").value = "2026-05-15";
  document.getElementById("kallelse-fran").value = "4";
  document.getElementById("kallelse-till").value = "6";
  document.getElementById("pbb-ar").value = "2026";
  document.getElementById("overlatelse").value = "1480";
  document.getElementById("pantsattning").value = "592";
});

document.getElementById("stamma-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const svar = document.getElementById("svar");
  svar.classList.remove("fel");
  const arsslut = datum("arsslut");
  const stammodatum = datum("stammodatum");
  const kallelseFran = tal("kallelse-fran");
  const kallelseTill = tal("kallelse-till");
  if (!arsslut || !stammodatum || kallelseFran === null || kallelseTill === null) {
    svar.textContent = "Fyll i tidplanens datum och kallelsefrister.";
    svar.classList.add("fel");
    return;
  }
  if ([kallelseFran, kallelseTill].some((x) => Number.isNaN(x))) {
    svar.textContent = "Kallelsefristerna ska vara tal.";
    svar.classList.add("fel");
    return;
  }
  const body = {
    arsslut,
    stammodatum,
    kallelseVeckorFran: kallelseFran,
    kallelseVeckorTill: kallelseTill,
  };
  const overlatelse = tal("overlatelse");
  const pantsattning = tal("pantsattning");
  const pbbAr = tal("pbb-ar");
  if (overlatelse !== null || pantsattning !== null) {
    if (pbbAr === null || Number.isNaN(pbbAr)) {
      svar.textContent = "Ange år för prisbasbelopp när ni fyller i en avgift.";
      svar.classList.add("fel");
      return;
    }
    if (overlatelse !== null && !Number.isNaN(overlatelse)) body.overlatelseAvgift = overlatelse;
    if (pantsattning !== null && !Number.isNaN(pantsattning)) body.pantsattningsAvgift = pantsattning;
    body.prisbasbeloppAr = pbbAr;
  }
  if (overlatelse !== null && Number.isNaN(overlatelse)) {
    svar.textContent = "Överlåtelseavgiften ska vara ett tal.";
    svar.classList.add("fel");
    return;
  }
  if (pantsattning !== null && Number.isNaN(pantsattning)) {
    svar.textContent = "Pantsättningsavgiften ska vara ett tal.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "Räknar…";
  const res = await fetch("/api/stamma", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) {
    svar.textContent = json.fel || "Det gick inte att räkna.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "";
  const kropp = document.querySelector("#tidplan tbody");
  kropp.replaceChildren();
  (json.rader || []).forEach((rad) => {
    const tr = document.createElement("tr");
    [rad.etikett, rad.datum, rad.anmarkning, rad.kalla].forEach((t, i) => {
      const cell = document.createElement(i === 0 ? "th" : "td");
      if (i === 0) cell.scope = "row";
      cell.textContent = t;
      tr.appendChild(cell);
    });
    kropp.appendChild(tr);
  });
  const radgivning = document.createElement("tr");
  const th = document.createElement("th");
  th.scope = "row";
  th.colSpan = 4;
  th.textContent = json.radgivning || "";
  radgivning.appendChild(th);
  kropp.appendChild(radgivning);
  if (csvUrl) URL.revokeObjectURL(csvUrl);
  csvUrl = URL.createObjectURL(new Blob(["\uFEFF" + (json.csv || "")], { type: "text/csv;charset=utf-8" }));
  document.getElementById("csv").href = csvUrl;
  const avgiftRuta = document.getElementById("avgift-resultat");
  const lista = document.getElementById("avgift-lista");
  lista.replaceChildren();
  if (json.prisbasbeloppInfo || json.overlatelse || json.pantsattning) {
    document.getElementById("pbb-info").textContent = json.prisbasbeloppInfo || "";
    function laggTill(rubrik, obj) {
      if (!obj) return;
      const li = document.createElement("li");
      if (obj.hogsta) {
        li.textContent = `${rubrik}: högsta tillåtna enligt referens i datafilen ${obj.hogsta}.`;
        if (obj.fel) {
          const em = document.createElement("em");
          em.className = "fel";
          em.textContent = ` ${obj.fel}`;
          li.appendChild(em);
        }
      }
      lista.appendChild(li);
    }
    laggTill("Överlåtelseavgift", json.overlatelse);
    laggTill("Pantsättningsavgift", json.pantsattning);
    avgiftRuta.hidden = false;
  } else {
    avgiftRuta.hidden = true;
  }
  document.getElementById("resultat").hidden = false;
});
