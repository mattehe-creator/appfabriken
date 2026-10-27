package se.tmconnect.rostfangst.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class InstallningarLagerHaTest {
    private lateinit var installningarLager: InstallningarLager

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        installningarLager = InstallningarLager(context)
    }

    @Test
    fun testInitialValues() = runBlocking {
        assertEquals("", installningarLager.haAdress.first())
        assertEquals("", installningarLager.haToken.first())
        assertEquals("", installningarLager.haTodo.first())
    }

    @Test
    fun testSparaHa() = runBlocking {
        installningarLager.sparaHa(" http://ha.local:8123 ", " abc ", " todo.x ")
        assertEquals("http://ha.local:8123", installningarLager.haAdress.first())
        assertEquals("abc", installningarLager.haToken.first())
        assertEquals("todo.x", installningarLager.haTodo.first())
    }

    @Test
    fun testSparaHaDoesNotChangeApiNyckel() = runBlocking {
        installningarLager.sparaApiNyckel("k")
        installningarLager.sparaHa(" http://ha.local:8123 ", " abc ", " todo.x ")
        assertEquals("k", installningarLager.apiNyckel.first())
    }

    @Test
    fun testSparaHaWithEmptyValues() = runBlocking {
        installningarLager.sparaHa("", "", "")
        assertEquals("", installningarLager.haAdress.first())
        assertEquals("", installningarLager.haToken.first())
        assertEquals("", installningarLager.haTodo.first())
    }
}
