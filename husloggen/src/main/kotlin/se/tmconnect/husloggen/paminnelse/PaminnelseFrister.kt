package se.tmconnect.husloggen.paminnelse

import java.time.LocalDate
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.nastaDatum
import se.tmconnect.karna.paminnelse.PaminnelseFrist

fun paminnelseFristerFranUppgifter(
    uppgifter: List<Uppgift>,
    idag: LocalDate,
): List<PaminnelseFrist> =
    uppgifter
        .filter { it.paminnelse }
        .map { uppgift ->
            val nasta = nastaDatum(uppgift.senastGjord, uppgift.startDatum, uppgift.intervallManader)
            PaminnelseFrist(
                nyckel = "${uppgift.id}:$nasta",
                slutdatum = nasta,
            )
        }
