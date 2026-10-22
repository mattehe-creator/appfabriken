function tal(id) {
  const rensad = String(document.getElementById(id).value).replace(/\s| /g, "").replace(",", ".");
  if (rensad === "") return 0;
  const v = Number(rensad);
  return Number.isFinite(v) ? Math.round(v) : NaN;
}

document.getElementById("exempel").addEventListener("click", () => {
  const v = (id, x) => { document.getElementById(id).value = x; };
  v("drift", "3100000"); v("kapital", "1520000"); v("avsattning", "600000");
  v("ovriga", "250000"); v("nuvarande", "4200000"); v("kvm", "5200");
});

document.getElementById("avgift-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const svar = document.getElementById("svar");
  svar.classList.remove("fel");
  const body = {
    driftkostnader: tal("drift"), kapitalkostnader: tal("kapital"), avsattning: tal("avsattning"),
    ovrigaIntakter: tal("ovriga"), nuvarandeArsavgifter: tal("nuvarande"), kvmBostadsratt: tal("kvm"),
  };
  if (Object.values(body).some((x) => Number.isNaN(x))) {
    svar.textContent = "Fyll i beloppen med siffror.";
    svar.classList.add("fel");
    return;
  }
  svar.textContent = "Räknar…";
  const res = await fetch("/api/avgift", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) });
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
  document.getElementById("anmarkning").textContent = json.anmarkning;
  document.getElementById("resultat").hidden = false;
});
