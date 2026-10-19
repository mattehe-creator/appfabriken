const GRUPPER = [
  "STOMME", "FASAD", "TAK", "FONSTER_DORRAR", "STAMMAR", "EL", "VARME",
  "VENTILATION", "HISS", "TVATTSTUGA", "MARK_OCH_GARAGE", "OVRIGT",
];

function jobbId() {
  const del = location.pathname.split("/").filter(Boolean);
  const j = del.indexOf("j");
  if (j >= 0 && del[j + 1]) return del[j + 1];
  const p = new URLSearchParams(location.search);
  return p.get("id");
}

const id = jobbId();
const statusEl = document.getElementById("status");
const tbody = document.querySelector("#komponent-tabell tbody");
let utkast = null;

async function hamta() {
  if (!id) {
    statusEl.textContent = "Ogiltig länk.";
    return;
  }
  const res = await fetch(`/api/jobb/${id}`);
  const json = await res.json();
  if (!res.ok) {
    statusEl.textContent = json.fel || "Jobbet hittades inte.";
    return;
  }
  const meta = json.meta;
  document.getElementById("rubrik").textContent = meta.forening || "Komponentlista";
  if (meta.status === "VANTAR" || meta.status === "BEARBETAR") {
    statusEl.textContent = "Bearbetar handlingarna… Utkastet kommer om en stund.";
    setTimeout(hamta, 2500);
    return;
  }
  if (meta.status === "FEL") {
    statusEl.textContent = meta.felmeddelande || "Bearbetningen misslyckades.";
    return;
  }
  utkast = json.utkast;
  statusEl.textContent = `Räkenskapsår slut ${utkast.arsslut}. UTKAST — beslutas av förening och revisor.`;
  visaAvstamning(utkast);
  visaAnmarkningar(utkast.anmarkningar);
  visaTabell(utkast.komponenter);
  document.getElementById("tabell-sektion").hidden = false;
  document.getElementById("csv").href = `/api/jobb/${id}.csv`;
}

function visaAvstamning(u) {
  const a = u.avstamning;
  const el = document.getElementById("avstamning-text");
  el.textContent =
    `Byggnadens värde: ${a.byggnadensVarde.toLocaleString("sv-SE")} kr. ` +
    `Summa komponenter: ${a.summa.toLocaleString("sv-SE")} kr. ` +
    (a.stammer ? "Stämmer." : `Differens: ${a.differens.toLocaleString("sv-SE")} kr.`);
  document.getElementById("avstamning").hidden = false;
}

function visaAnmarkningar(lista) {
  if (!lista || !lista.length) return;
  const ul = document.getElementById("anmarkningar");
  ul.replaceChildren();
  lista.forEach((t) => {
    const li = document.createElement("li");
    li.textContent = t;
    ul.appendChild(li);
  });
  document.getElementById("anmarkningar-sektion").hidden = false;
}

function visaTabell(komponenter) {
  tbody.replaceChildren();
  komponenter.forEach((k, idx) => {
    const tr = document.createElement("tr");
    if (k.sakerhet === "LAG") tr.classList.add("osaker");
    if (k.sakerhet === "UPPSKATTNING") tr.classList.add("uppskattning");
    tr.innerHTML = `
      <td><input aria-label="Namn rad ${idx + 1}" data-f="namn" value="${esc(k.namn)}"></td>
      <td><select data-f="grupp">${GRUPPER.map((g) => `<option ${g === k.grupp ? "selected" : ""}>${g}</option>`).join("")}</select></td>
      <td class="tal"><input data-f="anskaffningsvarde" inputmode="numeric" value="${k.anskaffningsvarde}"></td>
      <td class="tal"><input data-f="anskaffningsar" inputmode="numeric" value="${k.anskaffningsar}"></td>
      <td class="tal"><input data-f="nyttjandeperiodAr" inputmode="numeric" value="${k.nyttjandeperiodAr}"></td>
      <td><input data-f="kalla" value="${esc(k.kalla)}"></td>
      <td>${k.sakerhet}</td>`;
    tr.dataset.index = String(idx);
    tbody.appendChild(tr);
  });
}

function esc(s) {
  return String(s).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;");
}

function lasRader() {
  return [...tbody.querySelectorAll("tr")].map((tr) => {
    const get = (f) => tr.querySelector(`[data-f="${f}"]`).value;
    return {
      namn: get("namn"),
      grupp: get("grupp"),
      anskaffningsvarde: Number(get("anskaffningsvarde")),
      anskaffningsar: Number(get("anskaffningsar")),
      nyttjandeperiodAr: Number(get("nyttjandeperiodAr")),
      restvarde: 0,
      kalla: get("kalla"),
      sakerhet: utkast.komponenter[Number(tr.dataset.index)]?.sakerhet || "MEDEL",
    };
  });
}

document.getElementById("spara").addEventListener("click", async () => {
  const komponenter = lasRader();
  const res = await fetch(`/api/jobb/${id}/rader`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ komponenter }),
  });
  const json = await res.json();
  if (!res.ok) {
    alert(json.fel || "Kunde inte spara.");
    return;
  }
  utkast = json;
  visaAvstamning(utkast);
  visaAnmarkningar(utkast.anmarkningar);
  visaTabell(utkast.komponenter);
});

document.getElementById("skrivUt").addEventListener("click", () => window.print());
document.getElementById("radera").addEventListener("click", async () => {
  if (!confirm("Radera uppladdade filer och utkastet permanent?")) return;
  await fetch(`/api/jobb/${id}`, { method: "DELETE" });
  location.href = "/";
});

hamta();
