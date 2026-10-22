const FALT = [
  ["arsavgifter", "Årsavgifter"],
  ["rorelseintakter", "Totala rörelseintäkter"],
  ["rantebarandeSkulder", "Räntebärande skulder på balansdagen"],
  ["aretsResultat", "Årets resultat (minus för förlust)"],
  ["avskrivningar", "Avskrivningar"],
  ["utrangeringar", "Utrangeringar"],
  ["planeratUnderhall", "Planerat underhåll (kostnad)"],
  ["justeringar", "Justeringar (+/−)"],
  ["energikostnad", "Värme, el och vatten"],
  ["kvmBostadsratt", "Kvm upplåtna med bostadsrätt"],
  ["kvmHyresratt", "Kvm upplåtna med hyresrätt"],
];
const NEGATIVA = new Set(["aretsResultat", "justeringar"]);
const nu = new Date().getFullYear() - 1;
const AR = [nu, nu - 1, nu - 2, nu - 3];

const arRad = document.getElementById("ar-rad");
AR.forEach((ar, i) => {
  const th = document.createElement("th");
  th.scope = "col";
  th.className = "tal";
  const input = document.createElement("input");
  input.type = "number";
  input.value = String(ar);
  input.id = `ar-${i}`;
  input.setAttribute("aria-label", `År, kolumn ${i + 1}`);
  input.min = "1990";
  input.max = "2100";
  th.appendChild(input);
  arRad.appendChild(th);
});

const tbody = document.querySelector("#underlag tbody");
FALT.forEach(([nyckel, etikett]) => {
  const tr = document.createElement("tr");
  const th = document.createElement("th");
  th.scope = "row";
  th.textContent = etikett;
  tr.appendChild(th);
  AR.forEach((_, i) => {
    const td = document.createElement("td");
    td.className = "tal";
    const input = document.createElement("input");
    input.inputMode = NEGATIVA.has(nyckel) ? "text" : "numeric";
    input.dataset.falt = nyckel;
    input.dataset.kolumn = String(i);
    input.setAttribute("aria-label", `${etikett}, kolumn ${i + 1}`);
    td.appendChild(input);
    tr.appendChild(td);
  });
  tbody.appendChild(tr);
});

function tal(text) {
  const rensad = String(text).replace(/\s| /g, "").replace("−", "-").replace(",", ".");
  if (rensad === "") return null;
  const v = Number(rensad);
  return Number.isFinite(v) ? Math.round(v) : NaN;
}

function lasAr() {
  const resultat = [];
  for (let i = 0; i < AR.length; i++) {
    const ar = tal(document.getElementById(`ar-${i}`).value);
    const rad = { ar };
    let nagot = false;
    let fel = null;
    tbody.querySelectorAll(`input[data-kolumn="${i}"]`).forEach((input) => {
      const v = tal(input.value);
      if (v === null) return;
      if (Number.isNaN(v)) fel = `Ogiltigt tal: ${input.getAttribute("aria-label")}`;
      rad[input.dataset.falt] = v;
      nagot = true;
    });
    if (fel) throw new Error(fel);
    if (nagot) resultat.push(rad);
  }
  return resultat;
}

document.getElementById("exempel").addEventListener("click", () => {
  const ex = {
    arsavgifter: 4200000, rorelseintakter: 4600000, rantebarandeSkulder: 38000000, aretsResultat: -350000,
    avskrivningar: 1600000, utrangeringar: 0, planeratUnderhall: 450000, justeringar: 0,
    energikostnad: 1150000, kvmBostadsratt: 5200, kvmHyresratt: 300,
  };
  tbody.querySelectorAll('input[data-kolumn="0"]').forEach((input) => {
    input.value = String(ex[input.dataset.falt] ?? "");
  });
});

document.getElementById("nyckeltal-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const svar = document.getElementById("svar");
  svar.classList.remove("fel");
  let ar;
  try {
    ar = lasAr();
  } catch (err) {
    svar.textContent = err.message;
    svar.classList.add("fel");
    return;
  }
  if (!ar.length) {
    svar.textContent = "Fyll i minst ett år.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "Räknar…";
  const res = await fetch("/api/nyckeltal", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ ar }),
  });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) {
    svar.textContent = json.fel || "Nyckeltalen kunde inte räknas.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "";
  visa(json);
});

function visa(json) {
  const thead = document.querySelector("#nyckeltal thead");
  const kropp = document.querySelector("#nyckeltal tbody");
  thead.replaceChildren();
  kropp.replaceChildren();
  const htr = document.createElement("tr");
  const forsta = document.createElement("th");
  forsta.scope = "col";
  forsta.textContent = "Nyckeltal";
  htr.appendChild(forsta);
  json.ar.forEach((a) => {
    const th = document.createElement("th");
    th.scope = "col";
    th.className = "tal";
    th.textContent = String(a.ar);
    htr.appendChild(th);
  });
  thead.appendChild(htr);
  const antal = json.ar[0]?.rader.length || 0;
  for (let r = 0; r < antal; r++) {
    const tr = document.createElement("tr");
    const th = document.createElement("th");
    th.scope = "row";
    th.textContent = json.ar[0].rader[r].etikett;
    tr.appendChild(th);
    json.ar.forEach((a) => {
      const td = document.createElement("td");
      td.className = "tal";
      td.textContent = a.rader[r].varde;
      tr.appendChild(td);
    });
    kropp.appendChild(tr);
  }
  document.getElementById("anmarkning").textContent = json.anmarkning;
  document.getElementById("resultat").hidden = false;
}
