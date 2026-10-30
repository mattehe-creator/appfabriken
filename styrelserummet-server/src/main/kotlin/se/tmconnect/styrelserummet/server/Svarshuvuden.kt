package se.tmconnect.styrelserummet.server

const val CSP_STYRELSERUMMET =
    "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; base-uri 'none'; frame-ancestors 'none'"

fun gemensammaSvarshuvuden(): Map<String, String> = mapOf(
    "Referrer-Policy" to "no-referrer",
    "X-Content-Type-Options" to "nosniff",
    "Content-Security-Policy" to CSP_STYRELSERUMMET,
    "Strict-Transport-Security" to "max-age=31536000",
)

fun extraSvarshuvudenForSokvag(sokvag: String): Map<String, String> {
    if (!sokvag.startsWith("/api/") && !sokvag.startsWith("/j/")) {
        return emptyMap()
    }
    return mapOf(
        "Cache-Control" to "no-store",
        "X-Robots-Tag" to "noindex",
    )
}

fun svarshuvudenForSokvag(sokvag: String): Map<String, String> =
    gemensammaSvarshuvuden() + extraSvarshuvudenForSokvag(sokvag)
