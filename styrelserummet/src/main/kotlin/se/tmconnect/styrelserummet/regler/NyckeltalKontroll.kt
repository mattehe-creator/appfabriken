package se.tmconnect.styrelserummet.regler

fun kontrolleraUnderlag(u: NyckeltalUnderlag): List<String> {
    val meddelanden = mutableListOf<String>()

    if (u.kvmBostadsratt == 0L && u.arsavgifter > 0) {
        meddelanden.add("Ytan upplåten med bostadsrätt saknas, men det finns årsavgifter.")
    }
    if (u.arsavgifter > u.rorelseintakter && u.rorelseintakter > 0) {
        meddelanden.add(
            "Årsavgifterna är större än de totala rörelseintäkterna. Kontrollera beloppen.",
        )
    }
    if (u.energikostnad > u.rorelseintakter && u.rorelseintakter > 0) {
        meddelanden.add(
            "Energikostnaden är större än de totala rörelseintäkterna. Kontrollera beloppen.",
        )
    }
    if (
        u.kvmBostadsratt > 0 &&
            u.arsavgifter > 0 &&
            avrundaKvot(u.arsavgifter, u.kvmBostadsratt) < 100
    ) {
        meddelanden.add(
            "Årsavgiften är under 100 kr per kvm och år. Är beloppet angivet i tusental kronor?",
        )
    }
    val totalKvm = u.kvmBostadsratt + u.kvmHyresratt
    if (totalKvm > 0 && avrundaKvot(u.rantebarandeSkulder, totalKvm) > 50_000) {
        meddelanden.add("Skulden är över 50 000 kr per kvm. Kontrollera beloppet och ytan.")
    }

    return meddelanden
}
