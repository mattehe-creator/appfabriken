package se.tmconnect.husloggen.ui

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import se.tmconnect.husloggen.data.HusDao
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.karna.bild.BildLager

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class HusViewModelBilderTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `test att bilder raderas vid avbryt i nyskapa-läge`() = testScope.runTest {
        // Skapa mock-objekt
        val dao = mock(HusDao::class.java)
        val bildLager = mock(BildLager::class.java)
        val repository = HusRepository(dao, bildLager)

        // Skapa ViewModel
        val viewModel = HusViewModel(repository)

        // Simulera att vi har en ny post och lägger till en bild
        viewModel.visaNyPost()
        viewModel.laggTillBildUri(Uri.parse("content://test"), {})

        // Stäng formuläret (vilket ska rensa bilder i nyskapa-läge)
        viewModel.stangPostFormular()

        // Kontrollera att bildfilen är borttagen
        verify(bildLager).taBort(anyString())
    }

    @Test
    fun `test att bilder inte raderas vid avbryt i redigeringsläge`() = testScope.runTest {
        // Skapa mock-objekt
        val dao = mock(HusDao::class.java)
        val bildLager = mock(BildLager::class.java)
        val repository = HusRepository(dao, bildLager)

        // Skapa ViewModel
        val viewModel = HusViewModel(repository)

        // Simulera att vi redigerar en post med en bild
        viewModel.visaAndraPost(1L)
        
        // Stäng formuläret (vilket inte ska rensa bilder i redigeringsläge)
        viewModel.stangPostFormular()

        // Kontrollera att bildfilen inte är borttagen
        verify(bildLager, never()).taBort(anyString())
    }
}
