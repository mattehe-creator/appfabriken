package se.tmconnect.styrelserummet.underlag

private val personnummer = Regex(
    """(?<!\d)(?:\d{6}|\d{8})[- ]?\d{4}(?!\d)""",
)
private val epost = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")
private val telefon = Regex(
    """(?:\+46\s?|0)(?:7[0236]\d(?:[\s-]?\d){7}|(?:8|1\d)(?:[\s-]?\d){6,8})""",
)

fun maskaPersonuppgifter(text: String): String {
    var s = text
    s = personnummer.replace(s) { "[personnummer]" }
    s = epost.replace(s) { "[e-post]" }
    s = telefon.replace(s) { "[telefon]" }
    return s
}
