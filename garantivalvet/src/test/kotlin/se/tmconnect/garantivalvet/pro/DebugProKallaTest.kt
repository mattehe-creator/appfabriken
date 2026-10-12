package se.tmconnect.garantivalvet.pro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.garantivalvet.BuildConfig
import se.tmconnect.garantivalvet.data.InstallningarLager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DebugProKallaTest {

    @Test
    fun debugProKallaFinnsBaraIDebugBygge() {
        val debugKlassFinns =
            try {
                Class.forName("se.tmconnect.garantivalvet.pro.DebugProKalla")
                true
            } catch (_: ClassNotFoundException) {
                false
            }
        assertEquals(BuildConfig.DEBUG, debugKlassFinns)
    }

    @Test
    fun foljerDataStoreFlagga() = runTest {
        if (!BuildConfig.DEBUG) {
            return@runTest
        }
        val context = ApplicationProvider.getApplicationContext<Context>()
        val installningarLager = InstallningarLager(context)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val proKalla = DebugProKalla(installningarLager, scope)

        assertFalse(proKalla.arPro.value)

        installningarLager.sattDebugProTestlagePa(true)
        assertTrue(proKalla.arPro.first { it })

        installningarLager.sattDebugProTestlagePa(false)
        assertFalse(proKalla.arPro.first { !it })
    }
}
