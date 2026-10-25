package se.tmconnect.husloggen.ui

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import se.tmconnect.karna.tema.TemaLage
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.InstallningarLager
import se.tmconnect.husloggen.data.MarkeraGjordResult
import se.tmconnect.husloggen.data.SparaPostResult
import se.tmconnect.husloggen.export.HusHistorikTexter
import se.tmconnect.husloggen.export.Sakerhetskopia
import se.tmconnect.husloggen.export.SakerhetskopiaLasResult
import se.tmconnect.husloggen.export.SakerhetskopiaPlanerare
import se.tmconnect.husloggen.export.byggHusHistorik
import se.tmconnect.husloggen.export.KommandeUnderhallCsvTexter
import se.tmconnect.husloggen.export.husHistorikCsv
import se.tmconnect.husloggen.export.kommandeUnderhallCsv
import se.tmconnect.husloggen.regler.filtreraPaBostad
import se.tmconnect.husloggen.regler.kommandeUnderhall
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr
import se.tmconnect.husloggen.export.skrivHusHistorikTillPdf
import se.tmconnect.husloggen.regler.ForbattringMojlighet
import se.tmconnect.husloggen.regler.standardForsaljningsar
import se.tmconnect.karna.export.PdfExportResult
import se.tmconnect.karna.export.skrivCsvMedBom
import java.util.Locale
import se.tmconnect.husloggen.data.BostadBorttagningSatt
import se.tmconnect.husloggen.regler.kanLaggaTillPost
import se.tmconnect.karna.pro.ProKalla
import se.tmconnect.karna.sakerhetskopia.AutoBackupTillstand
import se.tmconnect.husloggen.regler.formateraBilder
import se.tmconnect.husloggen.data.Avlasning
import se.tmconnect.husloggen.data.Bostad
import se.tmconnect.husloggen.data.PlaneradKostnad
import se.tmconnect.husloggen.data.Matare
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.SparaBostadResult
import se.tmconnect.husloggen.data.TaBortBostadResult
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.export.AvlasningCsvTexter
import se.tmconnect.husloggen.export.avlasningCsv
import se.tmconnect.husloggen.regler.AvlasningFel
import se.tmconnect.husloggen.regler.MatareFel
import se.tmconnect.husloggen.regler.PostFel
import se.tmconnect.husloggen.regler.UppgiftFel
import se.tmconnect.husloggen.regler.beraknaAvlasningarMedForbrukning
import se.tmconnect.husloggen.regler.foregaendeAvlasning
import se.tmconnect.husloggen.regler.tolkaMatareVardeTillTusendel
import se.tmconnect.husloggen.regler.valideraAvlasning
import se.tmconnect.husloggen.regler.valideraMatare
import se.tmconnect.husloggen.regler.beloppOreTillFaltText
import se.tmconnect.husloggen.regler.sorteraUppgifter
import se.tmconnect.husloggen.regler.tolkaBeloppTillOre
import se.tmconnect.husloggen.regler.tolkaValfrittOreHeltal
import se.tmconnect.husloggen.regler.valideraPost
import se.tmconnect.husloggen.regler.valideraUppgift
import se.tmconnect.husloggen.paminnelse.HusPaminnelseKalla
import se.tmconnect.husloggen.paminnelse.PaminnelseWorker
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager
import se.tmconnect.karna.paminnelse.PaminnelsePlanerare
import se.tmconnect.husloggen.data.Dokument
import se.tmconnect.husloggen.data.DokumentKategori
import se.tmconnect.husloggen.data.KopieraDokumentResult
import se.tmconnect.husloggen.data.skapaHusBildLager
import se.tmconnect.husloggen.data.skapaHusDokumentLager
import se.tmconnect.husloggen.regler.DokumentFel
import se.tmconnect.husloggen.regler.valideraDokument
import se.tmconnect.husloggen.regler.PlaneradKostnadFel
import se.tmconnect.husloggen.regler.valideraPlaneradKostnad

class HusViewModel(
    private val repository: HusRepository,
    private val installningarLager: InstallningarLager,
    private val proKalla: ProKalla,
    private val paminnelseInstallningarLager: PaminnelseInstallningarLager,
    private val husPaminnelseKalla: HusPaminnelseKalla,
) : ViewModel() {

    // ... (resten av filen är oförändrad)

    /** Poster för exporten, hämtade från databasen när filen skrivs (inte från UI-flödet). */
    private suspend fun posterForExport(bostadId: Long?): List<Post> =
        if (bostadId == null) emptyList() else filtreraPaBostad(repository.allaPoster(), bostadId) { it.bostadId }

    /** Uppgifter för kommande underhåll, hämtade från databasen när filen skrivs (inte från UI-flödet). */
    private suspend fun uppgifterForKommandeUnderhall(bostadId: Long?): List<Uppgift> =
        if (bostadId == null) emptyList() else filtreraPaBostad(repository.allaUppgifter(), bostadId) { it.bostadId }

    // ... (resten av filen är oförändrad)
}
