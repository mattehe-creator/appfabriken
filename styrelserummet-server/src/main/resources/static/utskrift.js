// Öppnar utskriftsdialogen när utskriftssidan har laddats. Egen fil eftersom CSP blockerar inline-skript.
window.addEventListener("load", () => window.print());
