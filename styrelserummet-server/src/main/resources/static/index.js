const form = document.getElementById("upload");
const svar = document.getElementById("svar");
const lankRuta = document.getElementById("lank-ruta");
const lankText = document.getElementById("lank-text");
const lankKnapp = document.getElementById("lank-knapp");

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  svar.textContent = "Laddar upp…";
  svar.classList.remove("fel");
  lankRuta.hidden = true;
  const data = new FormData(form);
  const filer = document.getElementById("filer").files;
  data.delete("filer");
  for (const fil of filer) {
    data.append("filer", fil, fil.name);
  }
  try {
    const res = await fetch("/api/jobb", { method: "POST", body: data });
    const json = await res.json();
    if (!res.ok) {
      svar.textContent = json.fel || "Uppladdningen misslyckades.";
      svar.classList.add("fel");
      return;
    }
    svar.textContent = json.meddelande || "Klart.";
    const full = new URL(json.lank, window.location.origin).href;
    lankText.textContent = full;
    lankKnapp.href = json.lank;
    lankRuta.hidden = false;
  } catch {
    svar.textContent = "Kunde inte nå servern.";
    svar.classList.add("fel");
  }
});
