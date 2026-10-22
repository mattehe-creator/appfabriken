package se.tmconnect.flextid.regler

import se.tmconnect.flextid.data.Dag

/** Vad nästa tryck på Stämpla ska göra för dagens post. */
enum class StampelLage { STAMPLA_IN, STAMPLA_UT, KLAR }

fun stampelLage(dag: Dag?): StampelLage =
    when {
        dag?.start == null -> StampelLage.STAMPLA_IN
        dag.slut == null -> StampelLage.STAMPLA_UT
        else -> StampelLage.KLAR
    }
