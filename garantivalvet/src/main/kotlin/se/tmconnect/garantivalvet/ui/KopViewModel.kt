package se.tmconnect.garantivalvet.ui

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.ButikKontakt
import se.tmconnect.garantivalvet.export.HemforsakringTexter
import se.tmconnect.garantivalvet.export.PdfExportResult
import se.tmconnect.garantivalvet.export.skrivHemforsakringTillPdf
import se.tmconnect.garantivalvet.regler.MAX_FEL_BILDER
import se.tmconnect.garantivalvet.regler.byggHemforsakring
import se.tmconnect.garantivalvet.regler.hemforsakringCsv
import se.tmconnect.garantivalvet.regler.ReklamationsUtfall
import se.tmconnect.garantivalvet.regler.TemaLage
import se.tmconnect.garantivalvet.regler.Varutyp
import se.tmconnect.garantivalvet.regler.kanLaggaTillFelBild
import se.tmconnect.garantivalvet.regler.lasFelBilder
import se.tmconnect.garantivalvet.regler.skrivFelBilder
import se.tmconnect.garantivalvet.regler.giltigaOppetKopDagar
import se.tmconnect.garantivalvet.data.KopRepository
import se.tmconnect.garantivalvet.data.KvittoLager
import se.tmconnect.garantivalvet.export.AutoBackupTillstand
import se.tmconnect.garantivalvet.export.Sakerhetskopia
import se.tmconnect.garantivalvet.export.SakerhetskopiaPlanerare
import se.tmconnect.garantivalvet.export.SakerhetskopiaLasResult
import se.tmconnect.garantivalvet.paminnelse.PaminnelsePlanerare
import se.tmconnect.garantivalvet.regler.kanLaggaTill
import se.tmconnect.garantivalvet.regler.sorteraEfterNarmasteFrist
import se.tmconnect.karna.pro.ProKalla

sealed class ExportPdfMeddelande {
    data object Sparad : ExportPdfMeddelande()
    data object Misslyckades : ExportPdfMeddelande()
}

sealed class SakerhetskopiaMeddelande {
    data object Sparad : SakerhetskopiaMeddelande()
    data object KundeInteLasas : SakerhetskopiaMeddelande()
    data class Importerad(val tillagda: Int, val hoppadeOver: Int) : SakerhetskopiaMeddelande()
    data object NyareVersion : SakerhetskopiaMeddelande()
}

class KopViewModel(
    private val repository: KopRepository,
    private val kvittoLager: KvittoLager,
    private val installningarLager: InstallningarLager,
    private val appContext: Context,
    private val proKalla: ProKalla,
    private val idag: LocalDate = LocalDate.now(),
) : ViewModel() {
    init {
        viewModelScope.launch {
            proKalla.kontrollera()
        }
        viewModelScope.launch(Dispatchers.IO) { kvittoLager.rensaCache() }
    }

    val arPro: StateFlow<Boolean> =
        proKalla.arPro.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
    val kopLista: StateFlow<List<Kop>> =
        repository.alla
            .map { lista -> sorteraEfterNarmasteFrist(lista, idag) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    /** Om dagliga påminnelser om garantislut är aktiverade. */
    val paminnelserPa: StateFlow<Boolean> =
        installningarLager.paminnelserPa.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /** Användarens val av utseende (följ telefonen, ljust eller mörkt). */
    val temaLage: StateFlow<TemaLage> =
        installningarLager.temaLage.map { TemaLage.franNamn(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TemaLage.SYSTEM,
        )

    fun sattTemaLage(lage: TemaLage) {
        viewModelScope.launch { installningarLager.sattTemaLage(lage.name) }
    }

    /** Automatisk säkerhetskopia till en mapp användaren valt (av som standard). */
    val autoBackup: StateFlow<AutoBackupTillstand> =
        combine(
            installningarLager.autoBackupMapp,
            installningarLager.autoBackupMappNamn,
            installningarLager.autoBackupIntervallDagar,
            installningarLager.autoBackupSenaste,
            installningarLager.autoBackupFel,
        ) { mapp, namn, intervall, senaste, fel ->
            AutoBackupTillstand(pa = mapp != null, mappNamn = namn, intervallDagar = intervall, senaste = senaste, fel = fel)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AutoBackupTillstand(),
        )

    /** Användaren har valt en mapp: spara valet, schemalägg och gör en första kopia direkt. */
    fun valtAutoBackupMapp(uri: Uri, namn: String?) {
        viewModelScope.launch {
            val tidigare = installningarLager.autoBackupMapp.first()
            installningarLager.sattAutoBackupMapp(uri.toString(), namn)
            // Antalet beständiga mappbehörigheter är begränsat: släpp den gamla när användaren byter mapp.
            if (tidigare != null && tidigare != uri.toString()) {
                try {
                    appContext.contentResolver.releasePersistableUriPermission(
                        Uri.parse(tidigare),
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                    )
                } catch (_: Exception) {
                }
            }
            SakerhetskopiaPlanerare.synka(appContext, true)
            SakerhetskopiaPlanerare.korNu(appContext)
        }
    }

    fun stangAvAutoBackup() {
        viewModelScope.launch {
            val mapp = installningarLager.autoBackupMapp.first()
            installningarLager.sattAutoBackupMapp(null, null)
            SakerhetskopiaPlanerare.synka(appContext, false)
            if (mapp != null) {
                try {
                    appContext.contentResolver.releasePersistableUriPermission(
                        Uri.parse(mapp),
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                    )
                } catch (_: Exception) {
                }
            }
        }
    }

    fun sattAutoBackupIntervall(dagar: Int) {
        viewModelScope.launch { installningarLager.sattAutoBackupIntervall(dagar) }
    }

    fun korNuAutoBackup() {
        SakerhetskopiaPlanerare.korNu(appContext)
    }

    /** Schemalägger det dagliga jobbet vid appstart om en mapp är vald. */
    fun synkaAutoBackupJobb() {
        viewModelScope.launch {
            SakerhetskopiaPlanerare.synka(appContext, installningarLager.autoBackupMapp.first() != null)
        }
    }

    private val _aktivSkarm = MutableStateFlow<KopSkarm>(KopSkarm.Lista)
    val aktivSkarm: StateFlow<KopSkarm> = _aktivSkarm.asStateFlow()

    private val _valtKop = MutableStateFlow<Kop?>(null)
    val valtKop: StateFlow<Kop?> = _valtKop.asStateFlow()

    private val _tillfalligtKvittoUri = MutableStateFlow<Uri?>(null)

    /** Vald eller nytt kamerafoto i formuläret, innan köpet sparas. */
    val tillfalligtKvittoUri: StateFlow<Uri?> = _tillfalligtKvittoUri.asStateFlow()

    private var kvittoSkaTasBortVidSpara = false

    /** Om Pro-dialogen är synlig. */
    private val _proDialogSynlig = MutableStateFlow(false)
    val proDialogSynlig: StateFlow<Boolean> = _proDialogSynlig.asStateFlow()

    /** State för exportmeddelanden */
    private val _exportPdfMeddelande = MutableStateFlow<ExportPdfMeddelande?>(null)
    val exportPdfMeddelande: StateFlow<ExportPdfMeddelande?> = _exportPdfMeddelande.asStateFlow()

    private val _sakerhetskopiaMeddelande = MutableStateFlow<SakerhetskopiaMeddelande?>(null)
    val sakerhetskopiaMeddelande: StateFlow<SakerhetskopiaMeddelande?> =
        _sakerhetskopiaMeddelande.asStateFlow()

    /**
     * Slår på eller av dagliga påminnelser och synkar WorkManager-jobbet.
     * Notisbehörighet ska vara hanterad i UI innan [pa] sätts till true.
     */
    fun sattPaminnelser(pa: Boolean) {
        viewModelScope.launch {
            installningarLager.sattPaminnelserPa(pa)
            PaminnelsePlanerare.synka(appContext, pa)
        }
    }

    /** Schemalägger påminnelsejobb vid appstart om inställningen är på. */
    fun synkaPaminnelseJobb() {
        viewModelScope.launch {
            val pa = installningarLager.paminnelserPa.first()
            PaminnelsePlanerare.synka(appContext, pa)
        }
    }

    /** Startar Play-köpflödet för Pro (anropas från UI). */
    fun kopPro(activity: Activity) {
        proKalla.kop(activity)
    }

    fun visaLista() {
        _aktivSkarm.value = KopSkarm.Lista
        _valtKop.value = null
        _proDialogSynlig.value = false
        rensaKvittoFormularTillstand()
    }

    fun visaInstallningar() {
        _aktivSkarm.value = KopSkarm.Installningar
        _proDialogSynlig.value = false
    }

    fun visaLaggTill() {
        _aktivSkarm.value = KopSkarm.LaggTill
        _valtKop.value = null
        _proDialogSynlig.value = false
        rensaKvittoFormularTillstand()
    }

    fun visaDetalj(kopId: Long) {
        viewModelScope.launch {
            _valtKop.value = repository.hamta(kopId)
            _aktivSkarm.value = KopSkarm.Detalj(kopId)
            _proDialogSynlig.value = false
            rensaKvittoFormularTillstand()
        }
    }

    fun visaAndra(kopId: Long) {
        viewModelScope.launch {
            _valtKop.value = repository.hamta(kopId)
            _aktivSkarm.value = KopSkarm.Andra(kopId)
            _proDialogSynlig.value = false
            rensaKvittoFormularTillstand()
        }
    }

    fun visaReklamera(kopId: Long) {
        viewModelScope.launch {
            _valtKop.value = repository.hamta(kopId)
            _aktivSkarm.value = KopSkarm.Reklamera(kopId)
            _proDialogSynlig.value = false
            rensaKvittoFormularTillstand()
        }
    }

    /** Sparar felet (datum och beskrivning) på det valda köpet. */
    fun sparaFel(felUpptackt: LocalDate, beskrivning: String) {
        val kop = _valtKop.value ?: return
        viewModelScope.launch {
            val ny = kop.copy(
                felUpptackt = felUpptackt,
                felBeskrivning = beskrivning.trim().takeIf { it.isNotEmpty() },
            )
            repository.spara(ny)
            _valtKop.value = ny
        }
    }

    /** Markerar att användaren reklamerat hos företaget och går tillbaka till köpets detaljvy. */
    fun harReklamerat(felUpptackt: LocalDate, beskrivning: String, datum: LocalDate = LocalDate.now()) {
        val kop = _valtKop.value ?: return
        viewModelScope.launch {
            val ny = kop.copy(
                felUpptackt = felUpptackt,
                felBeskrivning = beskrivning.trim().takeIf { it.isNotEmpty() },
                reklamerad = datum,
            )
            repository.spara(ny)
            _valtKop.value = ny
            _aktivSkarm.value = KopSkarm.Detalj(kop.id)
        }
    }

    /** Sätter eller tar bort datumet då användaren reklamerade (detaljvyns logg). Utfallet följer med: ingen reklamation, inget utfall. */
    fun markeraReklamerad(datum: LocalDate?) {
        val kop = _valtKop.value ?: return
        viewModelScope.launch {
            val ny = if (datum == null) {
                kop.copy(reklamerad = null, reklamationUtfall = null, reklamationUtfallDatum = null)
            } else {
                kop.copy(reklamerad = datum)
            }
            repository.spara(ny)
            _valtKop.value = ny
        }
    }

    private val felBildLas = Mutex()

    /**
     * Lägger en bild på felet till köpet [kopId] (högst [MAX_FEL_BILDER]). Raden läses om efter den långsamma
     * bildbehandlingen, så att inget som ändrats under tiden skrivs över och gränsen gäller aktuella data.
     */
    fun laggTillFelBild(kopId: Long, uri: Uri, onFel: () -> Unit = {}) {
        viewModelScope.launch {
            val filnamn = withContext(Dispatchers.IO) { kvittoLager.sparaFelBild(uri) }
            if (filnamn == null) {
                onFel()
                return@launch
            }
            felBildLas.withLock {
                val nu = repository.hamta(kopId)
                if (nu == null || !kanLaggaTillFelBild(nu.felBilder)) {
                    withContext(Dispatchers.IO) { kvittoLager.taBort(filnamn) }
                    onFel()
                    return@withLock
                }
                val ny = nu.copy(felBilder = skrivFelBilder(lasFelBilder(nu.felBilder) + filnamn))
                repository.spara(ny)
                if (_valtKop.value?.id == kopId) _valtKop.value = ny
            }
        }
    }

    /** Tar bort en bild på felet från det valda köpet och raderar filen. */
    fun taBortFelBild(filnamn: String) {
        val kopId = _valtKop.value?.id ?: return
        viewModelScope.launch {
            felBildLas.withLock {
                val nu = repository.hamta(kopId) ?: return@withLock
                val ny = nu.copy(felBilder = skrivFelBilder(lasFelBilder(nu.felBilder) - filnamn))
                repository.spara(ny)
                withContext(Dispatchers.IO) { kvittoLager.taBort(filnamn) }
                if (_valtKop.value?.id == kopId) _valtKop.value = ny
            }
        }
    }

    /** FileProvider-URI för en sparad bild på felet. */
    fun felBildUri(filnamn: String): Uri? = kvittoLager.innehallUriForFilnamn(filnamn)

    /** Anger hur reklamationen slutade (löst, avslag, anmält till ARN), eller `null` för att öppna ärendet igen. */
    fun sattReklamationsUtfall(utfall: ReklamationsUtfall?, datum: LocalDate = LocalDate.now()) {
        val kop = _valtKop.value ?: return
        viewModelScope.launch {
            val ny = kop.copy(
                reklamationUtfall = utfall?.name,
                reklamationUtfallDatum = if (utfall == null) null else datum,
            )
            repository.spara(ny)
            _valtKop.value = ny
            // Ärendets status styr påminnelserna (svar, ARN): schemat räknas om vid nästa körning.
        }
    }

    /** Öppnar helskärmsvy för köpets kvitto. */
    fun visaKvittoHelskarm(kopId: Long) {
        viewModelScope.launch {
            _valtKop.value = repository.hamta(kopId)
            _aktivSkarm.value = KopSkarm.KvittoHelskarm(kopId)
        }
    }

    /**
     * URI för kvittoförhandsvisning i formuläret: tillfällig bild om sådan finns,
     * annars sparat kvitto för [kop] om användaren inte markerat borttagning.
     */
    fun kvittoUriForFormular(kop: Kop?): Uri? {
        _tillfalligtKvittoUri.value?.let { return it }
        if (kvittoSkaTasBortVidSpara) {
            return null
        }
        val filnamn = kop?.kvittoFil ?: return null
        return kvittoLager.innehallUriForFilnamn(filnamn)
    }

    /**
     * FileProvider-URI för ett sparat kvitto, eller null om köpet saknar kvitto.
     */
    fun sparatKvittoUri(kop: Kop): Uri? {
        val filnamn = kop.kvittoFil ?: return null
        return kvittoLager.innehallUriForFilnamn(filnamn)
    }

    /** Anropas när användaren valt bild eller tagit foto i formuläret. */
    fun sattTillfalligtKvitto(uri: Uri) {
        kvittoSkaTasBortVidSpara = false
        _tillfalligtKvittoUri.value = uri
    }

    /** Bild vald i galleriet eller nytt kamerafoto: görs upprätt (EXIF) innan den visas och läses av. */
    fun valjBild(uri: Uri) {
        viewModelScope.launch {
            val upprat = withContext(Dispatchers.IO) { kvittoLager.normalisera(uri) }
            sattTillfalligtKvitto(upprat ?: uri)
        }
    }

    /** Roterar kvittot i formuläret 90 grader (medurs eller moturs) och läser av det igen. */
    fun roteraKvitto(medurs: Boolean, onFel: () -> Unit = {}) {
        val kalla = _tillfalligtKvittoUri.value ?: _valtKop.value?.let { kvittoUriForFormular(it) }
        if (kalla == null) {
            onFel()
            return
        }
        viewModelScope.launch {
            val roterad = withContext(Dispatchers.IO) { kvittoLager.normalisera(kalla, if (medurs) 90 else 270) }
            if (roterad != null) {
                sattTillfalligtKvitto(roterad)
            } else {
                onFel()
            }
        }
    }

    /**
     * Fil vald med filväljaren i formuläret (PDF eller bild). En PDF blir en bild av första sidan. Ändrar inte
     * skärm, så det fungerar både för nytt köp och när ett köp ändras.
     */
    fun valjFil(uri: Uri, mime: String?, onFel: () -> Unit) {
        viewModelScope.launch {
            val bild = withContext(Dispatchers.IO) { kvittoLager.tagEmotDelatKvitto(uri, mime) }
            if (bild == null) {
                onFel()
            } else {
                sattTillfalligtKvitto(bild)
            }
        }
    }

    private val _delatKvitto = MutableStateFlow(false)

    /** Sant när formuläret öppnades med ett kvitto som delats till appen, och det ska läsas av direkt. */
    val delatKvitto: StateFlow<Boolean> = _delatKvitto.asStateFlow()

    /**
     * Tar emot ett kvitto som delats till appen (bild eller PDF), öppnar formuläret för ett nytt köp med
     * kvittot ifyllt och startar avläsningen. Efter 15 köp utan Pro visas Pro-dialogen i stället.
     * [onFel] anropas om filen inte gick att läsa.
     */
    fun tagEmotDelatKvitto(uri: Uri, mime: String?, onFel: () -> Unit) {
        viewModelScope.launch {
            if (!kanLaggaTillNu()) {
                visaLista()
                visaProDialog()
                return@launch
            }
            val bild = withContext(Dispatchers.IO) { kvittoLager.tagEmotDelatKvitto(uri, mime) }
            if (bild == null) {
                onFel()
                return@launch
            }
            _valtKop.value = null
            _proDialogSynlig.value = false
            rensaKvittoFormularTillstand()
            _aktivSkarm.value = KopSkarm.LaggTill
            sattTillfalligtKvitto(bild)
            _delatKvitto.value = true
        }
    }

    /** Tar bort kvittot i formuläret (sparat kvitto tas bort vid Spara). */
    fun markeraKvittoForBorttagning() {
        _tillfalligtKvittoUri.value = null
        kvittoSkaTasBortVidSpara = true
    }

    /**
     * URI för kamera-intent. Anropa [sattTillfalligtKvitto] med samma URI när fotot är klart.
     */
    fun skapaKameraKvittoUri(): Uri = kvittoLager.skapaKameraFotoUri()

    /**
     * Byter eller lägger till kvitto på ett befintligt köp och sparar direkt i databasen.
     */
    fun uppdateraKvittoPaKop(kopId: Long, kallaUri: Uri) {
        viewModelScope.launch {
            val kop = repository.hamta(kopId) ?: return@launch
            kvittoLager.taBort(kop.kvittoFil)
            val filnamn = kvittoLager.kopieraFranUri(kallaUri) ?: return@launch
            val uppdaterat = kop.copy(kvittoFil = filnamn)
            repository.spara(uppdaterat)
            if (_valtKop.value?.id == kopId) {
                _valtKop.value = uppdaterat
            }
        }
    }

    /** Tar bort kvittofilen från ett köp och sparar direkt. */
    fun taBortKvittoFranKop(kopId: Long) {
        viewModelScope.launch {
            val kop = repository.hamta(kopId) ?: return@launch
            if (kop.kvittoFil == null) {
                return@launch
            }
            kvittoLager.taBort(kop.kvittoFil)
            val uppdaterat = kop.copy(kvittoFil = null)
            repository.spara(uppdaterat)
            if (_valtKop.value?.id == kopId) {
                _valtKop.value = uppdaterat
            }
        }
    }

    fun sparaNyttKop(
        vad: String,
        varKopt: String?,
        kopdatum: LocalDate,
        garantiManader: Int,
        prisOre: Long?,
        anteckning: String?,
        oppetKopDagar: Int? = null,
        varutyp: String? = null,
        kontakt: ButikKontakt = ButikKontakt(),
    ) {
        viewModelScope.launch {
            var kop = Kop(
                vad = vad.trim(),
                varKopt = varKopt?.trim()?.takeIf { it.isNotEmpty() },
                kopdatum = kopdatum,
                garantiManader = garantiManader,
                prisOre = prisOre,
                anteckning = anteckning?.trim()?.takeIf { it.isNotEmpty() },
                oppetKopDagar = giltigaOppetKopDagar(oppetKopDagar),
                varutyp = Varutyp.franNamn(varutyp)?.name,
                butikEpost = kontakt.epost?.trim()?.takeIf { it.isNotEmpty() },
                butikTelefon = kontakt.telefon?.trim()?.takeIf { it.isNotEmpty() },
                butikOrgnr = kontakt.orgnr?.trim()?.takeIf { it.isNotEmpty() },
            )
            kop = kop.copy(id = repository.spara(kop))
            kop = appliceraKvittoAndringar(kop)
            if (kop.kvittoFil != null) {
                repository.spara(kop)
            }
            visaLista()
        }
    }

    fun uppdateraKop(kop: Kop) {
        viewModelScope.launch {
            val medKvitto = appliceraKvittoAndringar(kop)
            repository.spara(medKvitto)
            visaLista()
        }
    }

    fun taBortValtKop() {
        val kop = _valtKop.value ?: return
        viewModelScope.launch {
            repository.taBort(kop)
            visaLista()
        }
    }

    /**
     * Om användaren får lägga till ett köp nu, läst direkt ur databasen och Pro-källan. Fungerar även precis efter
     * appstart, innan listan har lästs in (genväg och delning).
     */
    suspend fun kanLaggaTillNu(): Boolean = kanLaggaTill(repository.antal(), proKalla.arPro.value)

    /** Kontrollerar om användaren får lägga till ett nytt köp. */
    fun kanLaggaTillNy(): Boolean {
        return kanLaggaTill(kopLista.value.size, arPro.value)
    }

    /** Visar Pro-dialogen. */
    fun visaProDialog() {
        _proDialogSynlig.value = true
    }

    /** Stänger Pro-dialogen. */
    fun stangProDialog() {
        _proDialogSynlig.value = false
    }

    /** Slår på eller av debug-testläge Pro (endast debug-bygge). */
    fun sattDebugProTestlagePa(pa: Boolean) {
        viewModelScope.launch {
            installningarLager.sattDebugProTestlagePa(pa)
        }
    }

    /** Meddelar att PDF-export lyckades */
    fun meddelaExportSparad() {
        _exportPdfMeddelande.value = ExportPdfMeddelande.Sparad
    }

    /** Meddelar att PDF-export misslyckades */
    fun meddelaExportMisslyckades() {
        _exportPdfMeddelande.value = ExportPdfMeddelande.Misslyckades
    }

    /** Rensar exportmeddelande */
    fun rensaExportMeddelande() {
        _exportPdfMeddelande.value = null
    }

    fun rensaSakerhetskopiaMeddelande() {
        _sakerhetskopiaMeddelande.value = null
    }

    /**
     * Hämtar alla köp för export (till säkerhetskopia)
     */
    suspend fun allaKopForExport(): List<Kop> = repository.allaKop()

    /**
     * Sparar säkerhetskopia till angiven URI
     */
    fun sparaSakerhetskopia(uri: Uri) {
        viewModelScope.launch {
            try {
                val kop = allaKopForExport()
                withContext(Dispatchers.IO) {
                    val outputStream = appContext.contentResolver.openOutputStream(uri)
                        ?: throw java.io.IOException()
                    outputStream.use {
                        Sakerhetskopia.skriv(it, kop, kvittoLager)
                    }
                }
                _sakerhetskopiaMeddelande.value = SakerhetskopiaMeddelande.Sparad
            } catch (_: Exception) {
                _sakerhetskopiaMeddelande.value = SakerhetskopiaMeddelande.KundeInteLasas
            }
        }
    }

    /**
     * Sparar hemförsäkringsunderlaget som PDF (tabell med värde och summa, därefter kvittona) till [uri]. PDF:en
     * byggs först i en temporärfil och kopieras bara till målet när den är klar, så att en halvfärdig fil aldrig
     * hamnar hos användaren. Slut på minne ger ett felmeddelande i stället för en krasch.
     */
    fun sparaHemforsakringPdf(uri: Uri, texter: HemforsakringTexter, locale: java.util.Locale) {
        viewModelScope.launch {
            val tillfallig = java.io.File(appContext.cacheDir, "hemforsakring-${System.nanoTime()}.pdf")
            try {
                val sammanstallning = byggHemforsakring(allaKopForExport())
                val ok = withContext(Dispatchers.IO) {
                    val resultat = tillfallig.outputStream().use {
                        skrivHemforsakringTillPdf(sammanstallning, texter, locale, kvittoLager, it)
                    }
                    if (resultat != PdfExportResult.Ok) return@withContext false
                    val ut = appContext.contentResolver.openOutputStream(uri) ?: throw java.io.IOException()
                    ut.use { mal -> tillfallig.inputStream().use { it.copyTo(mal) } }
                    true
                }
                if (ok) meddelaExportSparad() else meddelaExportMisslyckades()
            } catch (_: Exception) {
                meddelaExportMisslyckades()
            } catch (_: OutOfMemoryError) {
                meddelaExportMisslyckades()
            } finally {
                tillfallig.delete()
            }
        }
    }

    /** Sparar hemförsäkringsunderlaget som CSV (UTF-8 med BOM, så att svenska Excel visar å, ä, ö rätt). */
    fun sparaHemforsakringCsv(uri: Uri, rubriker: List<String>, summaRubrik: String) {
        viewModelScope.launch {
            try {
                val csv = hemforsakringCsv(byggHemforsakring(allaKopForExport()), rubriker, summaRubrik)
                withContext(Dispatchers.IO) {
                    val ut = appContext.contentResolver.openOutputStream(uri) ?: throw java.io.IOException()
                    ut.use {
                        it.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                        it.write(csv.toByteArray(Charsets.UTF_8))
                    }
                }
                meddelaExportSparad()
            } catch (_: Exception) {
                meddelaExportMisslyckades()
            }
        }
    }

    /**
     * Läser in säkerhetskopia från angiven URI
     */
    fun lasInSakerhetskopia(uri: Uri) {
        viewModelScope.launch {
            try {
                val resultat = withContext(Dispatchers.IO) {
                    val inputStream = appContext.contentResolver.openInputStream(uri)
                        ?: throw java.io.IOException()
                    inputStream.use { stream ->
                        Sakerhetskopia.lasIn(stream)
                    }
                }
                when (resultat) {
                    is SakerhetskopiaLasResult.Ok -> importeraSakerhetskopia(resultat)
                    SakerhetskopiaLasResult.Fel.OkandVersion -> {
                        _sakerhetskopiaMeddelande.value = SakerhetskopiaMeddelande.NyareVersion
                    }
                    SakerhetskopiaLasResult.Fel.TrasigFil,
                    SakerhetskopiaLasResult.Fel.SaknarKopJson -> {
                        _sakerhetskopiaMeddelande.value = SakerhetskopiaMeddelande.KundeInteLasas
                    }
                }
            } catch (_: Exception) {
                _sakerhetskopiaMeddelande.value = SakerhetskopiaMeddelande.KundeInteLasas
            }
        }
    }

    /**
     * Importerar säkerhetskopia
     */
    private suspend fun importeraSakerhetskopia(resultat: SakerhetskopiaLasResult.Ok) {
        try {
            val importResultat = withContext(Dispatchers.IO) {
                repository.importeraSakerhetskopia(
                    resultat.innehall,
                    resultat.kvittoFiler,
                )
            }
            val paminnelserPa = installningarLager.paminnelserPa.first()
            if (paminnelserPa) {
                PaminnelsePlanerare.synka(appContext, true)
            }
            _sakerhetskopiaMeddelande.value = SakerhetskopiaMeddelande.Importerad(
                tillagda = importResultat.tillagda,
                hoppadeOver = importResultat.hoppadeOver,
            )
        } catch (_: Exception) {
            _sakerhetskopiaMeddelande.value = SakerhetskopiaMeddelande.KundeInteLasas
        }
    }

    private suspend fun appliceraKvittoAndringar(kop: Kop): Kop {
        when {
            kvittoSkaTasBortVidSpara -> {
                kvittoLager.taBort(kop.kvittoFil)
                rensaKvittoFormularTillstand()
                return kop.copy(kvittoFil = null)
            }
            _tillfalligtKvittoUri.value != null -> {
                kvittoLager.taBort(kop.kvittoFil)
                val filnamn = kvittoLager.kopieraFranUri(_tillfalligtKvittoUri.value!!)
                    ?: return kop
                rensaKvittoFormularTillstand()
                return kop.copy(kvittoFil = filnamn)
            }
            else -> return kop
        }
    }

    private fun rensaKvittoFormularTillstand() {
        _delatKvitto.value = false
        _tillfalligtKvittoUri.value = null
        kvittoSkaTasBortVidSpara = false
    }
}

class KopViewModelFactory(
    private val repository: KopRepository,
    private val kvittoLager: KvittoLager,
    private val installningarLager: InstallningarLager,
    private val appContext: Context,
    private val proKalla: ProKalla,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KopViewModel::class.java)) {
            return KopViewModel(
                repository,
                kvittoLager,
                installningarLager,
                appContext,
                proKalla,
            ) as T
        }
        throw IllegalArgumentException("Okänd ViewModel: ${modelClass.name}")
    }
}
