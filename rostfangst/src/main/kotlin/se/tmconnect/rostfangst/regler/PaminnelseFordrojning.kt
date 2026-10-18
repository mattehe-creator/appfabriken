package se.tmconnect.rostfangst.regler

import java.time.Duration
import java.time.Instant

fun fordrojning(nu: Instant, start: Instant): Duration? {
    if (!start.isAfter(nu)) {
        return null
    }
    return Duration.between(nu, start)
}
