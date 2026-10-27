package se.tmconnect.rostfangst.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class InstallningarLagerHaTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val lager = InstallningarLager(context)

    @Test
    fun testHaInstallningarInnanSparad() = runBlocking {
        assertEquals("", lager.haAdress.first())
        assertEquals("", lager.haToken.first())
        assertEquals("", lager.haTodo.first())
    }

    @Test
    fun testSparaHa() = runBlocking {
        lager.sparaHa(" http://ha.local:8123 ", " abc ", " todo.x ")
        
        assertEquals("http://ha.local:8123", lager.haAdress.first())
        assertEquals("abc", lager.haToken.first())
        assertEquals("todo.x", lager.haTodo.first())
    }

    @Test
    fun testSparaHaBehallerApiNyckel() = runBlocking {
        lager.sparaApiNyckel("k")
        lager.sparaHa(" http://ha.local:8123 ", " abc ", " todo.x ")
        
        assertEquals("k", lager.apiNyckel.first())
        assertEquals("http://ha.local:8123", lager.haAdress.first())
        assertEquals("abc", lager.haToken.first())
        assertEquals("todo.x", lager.haTodo.first())
    }
}
