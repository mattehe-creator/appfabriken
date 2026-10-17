package se.tmconnect.karna.ui

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Minst 48 dp tryckyta enligt projektets tillgänglighetskrav. */
fun Modifier.tryckyta48(): Modifier = heightIn(min = 48.dp).defaultMinSize(minWidth = 48.dp)
