package se.tmconnect.husloggen.ui

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.InstallningarLager
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.data.skapaHusRepository
import se.tmconnect.husloggen.data.skapaTestHusDatabas
import se.tmconnect.husloggen.export.AvlasningCsvTexter
import se.tmconnect.husloggen.export.HusHistorikTexter
import se.tmconnect.husloggen.export.KommandeUnderhallCsvTexter
import se.tmconnect.husloggen.paminnelse.HusPaminnelseKalla
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager
import se.tmconnect.karna.pro.FakeProKalla

// ... (resten av filen är oförändrad)

    @Test
    fun exportHistorikCsvTarBaraMedValdBostad() = runBlocking {
        // Skapa en andra bostad
        val bostadBId = repository.sparaBostad(
            se.tmconnect.husloggen.data.Bostad(namn = "Bostad B")
        ).let { 
            if (it is se.tmconnect.husloggen.data.SparaBostadResult.Sparad) it.id else throw Exception("Kunde inte skapa bostad B")
        }
        
        // Spara en post i bostad A (vald bostad)
        repository.sparaPost(
            Post(bostadId = bostadId, datum = idag, vad = "A1", typ = PostTyp.OMBYGGNAD),
            arPro = false,
        )
        
        // Spara en post i bostad B
        repository.sparaPost(
            Post(bostadId = bostadBId, datum = idag, vad = "B1", typ = PostTyp.OMBYGGNAD),
            arPro = false,
        )
        
        val (uri, utdata) = registreraFil("historik.csv")

        val meddelande = vantaPaMeddelande { sparaHistorikCsv(uri) }

        assertEquals(HusUiMeddelande.HistorikCsvSparad, meddelande)
        val csvInnehall = utdata.toString("UTF-8")
        assertTrue(csvInnehall.contains("A1"))
        assertFalse(csvInnehall.contains("B1"))
    }
    
    // ... (resten av filen är oförändrad)
