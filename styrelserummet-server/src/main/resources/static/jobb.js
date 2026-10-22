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
let visning = null;
let betalning = null;

async function bekraftaBetalning() {
  const p = new URLSearchParams(location.search);
  const kassa = p.get("kassa");
  if (!kassa || !id) return;
  history.replaceState(null, "", location.pathname);
  const res = await fetch(`/api/jobb/${id}/bekrafta`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ kassa }),
  });
  if (!res.ok) {
    const json = await res.json().catch(() => ({}));
    document.getElementById("betal-status").textContent = json.fel || "Betalningen kunde inte bekräftas.";
  }
}

function visaBetalning() {
  const kraver = betalning && betalning.aktiv && !betalning.betald;
  document.getElementById("betal-sektion").hidden = !kraver;
  document.getElementById("csv").hidden = !!kraver;
  document.getElementById("skrivUt").hidden = !!kraver;
  document.getElementById("plan-csv").hidden = !!kraver;
  if (kraver) {
    document.getElementById("betal-text").textContent =
      `Granska och rätta utkastet här utan kostnad. CSV och utskrift för revisorn kostar ${betalning.prisKr} kr för det här utkastet. Du betalar hos Stripe och får ett kvitto till föreningens e-post.`;
    document.getElementById("betala").textContent = `Betala ${betalning.prisKr} kr`;
  }
}

document.getElementById("betala").addEventListener("click", async () => {
  const knapp = document.getElementById("betala");
  const status = document.getElementById("betal-status");
  knapp.disabled = true;
  status.textContent = "Öppnar betalningen…";
  const res = await fetch(`/api/jobb/${id}/kassa`, { method: "POST" });
  const json = await res.json().catch(() => ({}));
  if (res.ok && json.url) {
    location.href = json.url;
    return;
  }
  if (res.ok && json.betald) {
    await hamta();
    return;
  }
  knapp.disabled = false;
  status.textContent = json.fel || "Betalningen kunde inte startas.";
});

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
  visning = json.visning;
  betalning = json.betalning || null;
  visaBetalning();
  statusEl.textContent = `Räkenskapsår slut ${utkast.arsslut}. UTKAST — beslutas av förening och revisor.`;
  visaSammanfattning(visning?.sammanfattning);
  visaAvstamning(visning?.avstamning);
  visaAnmarkningar(utkast.anmarkningar);
  visaTabell(visning?.komponenter || utkast.komponenter);
  document.getElementById("tabell-sektion").hidden = false;
  document.getElementById("jamforelse-sektion").hidden = false;
  const atgarder = utkast.atgarder || [];
  if (atgarder.length) {
    const summa = atgarder.reduce((s, a) => s + a.belopp, 0);
    document.getElementById("fond-text").textContent =
      `Tolkningen hittade ${atgarder.length} planerade åtgärder på sammanlagt ${summa.toLocaleString("sv-SE")} kr. Kontrollera dem mot planen. Fondplanen visar vilken årlig avsättning som räcker.`;
    document.getElementById("fond-lank").href = `/fondplan?jobb=${encodeURIComponent(id)}`;
    document.getElementById("fond-sektion").hidden = false;
  }
  document.getElementById("csv").href = `/api/jobb/${id}.csv`;
  document.getElementById("plan-csv").href = `/api/jobb/${id}/plan.csv`;
  document.getElementById("skrivUt").onclick = () => window.open(`/j/${id}/utskrift`, "_blank", "noopener");
}

function visaSammanfattning(rader) {
  if (!rader || !rader.length) return;
  const ul = document.getElementById("sammanfattning");
  ul.replaceChildren();
  rader.forEach((t) => {
    const li = document.createElement("li");
    li.textContent = t;
    ul.appendChild(li);
  });
  document.getElementById("sammanfattning-sektion").hidden = false;
}

function visaAvstamning(a) {
  if (!a) return;
  document.getElementById("avstamning-text").textContent = a.text;
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
    const beloppText = k.anskaffningsvardeText || String(k.anskaffningsvarde);
    const hjalp = k.nyttjandeperiodHjalp || "";
    const varning = k.nyttjandeperiodVarning
      ? '<p class="hjalp varning">Perioden ligger utanför typintervallet i standardtabellen — kontrollera mot handlingarna.</p>'
      : "";
    const gruppForslag = k.gruppForslag
      ? `<button type="button" class="knapp-mini anvand-grupp" data-grupp="${esc(k.gruppForslag)}">Använd gruppförslag: ${esc(k.gruppForslag)}</button>`
      : "";
    tr.innerHTML = `
      <td><input aria-label="Namn rad ${idx + 1}" data-f="namn" value="${esc(k.namn)}"></td>
      <td>
        <select data-f="grupp" aria-label="Grupp rad ${idx + 1}">${GRUPPER.map((g) => `<option ${g === k.grupp ? "selected" : ""}>${g}</option>`).join("")}</select>
        ${gruppForslag}
      </td>
      <td class="tal">
        <span class="belopp-visning">${esc(beloppText)}</span>
        <input data-f="anskaffningsvarde" inputmode="numeric" value="${k.anskaffningsvarde}" aria-label="Belopp rad ${idx + 1}">
      </td>
      <td class="tal"><input data-f="anskaffningsar" inputmode="numeric" value="${k.anskaffningsar}"></td>
      <td class="tal">
        <input data-f="nyttjandeperiodAr" inputmode="numeric" value="${k.nyttjandeperiodAr}" aria-describedby="np-h${idx}">
        ${hjalp ? `<p class="hjalp" id="np-h${idx}">${esc(hjalp)}</p>` : ""}
        ${varning}
      </td>
      <td><input data-f="kalla" value="${esc(k.kalla)}"></td>
      <td>${k.sakerhet}</td>`;
    tr.dataset.index = String(idx);
    tbody.appendChild(tr);
    const gruppKnapp = tr.querySelector(".anvand-grupp");
    if (gruppKnapp) {
      gruppKnapp.addEventListener("click", () => {
        tr.querySelector('[data-f="grupp"]').value = gruppKnapp.dataset.grupp;
      });
    }
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
  utkast = json.utkast;
  visning = json.visning;
  visaSammanfattning(visning?.sammanfattning);
  visaAvstamning(visning?.avstamning);
  visaAnmarkningar(utkast.anmarkningar);
  visaTabell(visning?.komponenter || utkast.komponenter);
});

document.getElementById("radera").addEventListener("click", async () => {
  if (!confirm("Radera uppladdade filer och utkastet permanent?")) return;
  await fetch(`/api/jobb/${id}`, { method: "DELETE" });
  location.href = "/";
});

document.getElementById("jamforelse-knapp").addEventListener("click", () => jamforelse(document.getElementById("foreg-csv").value));

document.getElementById("foreg-fil").addEventListener("change", async (ev) => {
  const fil = ev.target.files?.[0];
  if (!fil) return;
  const text = await fil.text();
  document.getElementById("foreg-csv").value = text;
  jamforelse(text);
});

async function jamforelse(csv) {
  const status = document.getElementById("jamforelse-status");
  const lista = document.getElementById("forandringar");
  if (!csv.trim()) {
    status.textContent = "Klistra in eller ladda upp en CSV-fil först.";
    lista.hidden = true;
    return;
  }
  status.textContent = "Jämför…";
  const res = await fetch(`/api/jobb/${id}/jamforelse`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ csv }),
  });
  const json = await res.json();
  if (!res.ok) {
    status.textContent = json.fel || "Jämförelsen misslyckades.";
    lista.hidden = true;
    return;
  }
  const andringar = json.forandringar || [];
  if (!andringar.length) {
    status.textContent = "Inga förändringar mot föregående års lista.";
    lista.hidden = true;
    return;
  }
  status.textContent = `${andringar.length} förändring(ar) — utkast, kontrollera mot beslutade listor.`;
  lista.replaceChildren();
  andringar.forEach((f) => {
    const li = document.createElement("li");
    const typ =
      f.typ === "NY" ? "Ny komponent" : f.typ === "BORTTAGEN" ? "Borttagen" : "Ändrad";
    li.textContent = `${typ}: ${f.namn}`;
    lista.appendChild(li);
  });
  lista.hidden = false;
}

bekraftaBetalning().finally(hamta);
