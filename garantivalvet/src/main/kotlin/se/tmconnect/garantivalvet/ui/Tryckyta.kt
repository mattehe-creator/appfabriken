package se.tmconnect.garantivalvet.ui

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Minsta tryckyta enligt CLAUDE.md punkt 6 (Material 3: 48×48 dp). */
fun Modifier.tryckyta48(): Modifier = heightIn(min = 48.dp).defaultMinSize(minWidth = 48.dp)
