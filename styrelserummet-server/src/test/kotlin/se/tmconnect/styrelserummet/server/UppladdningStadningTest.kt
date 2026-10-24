package se.tmconnect.styrelserummet.server

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import java.io.ByteArrayInputStream
import kotlin.io.path.createTempDirectory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Ett avvisat uppladdningsförsök ska inte lämna ett jobb kvar på disk. */
class UppladdningStadningTest {
    private val minstaPdf = "%PDF-1.4\n%%EOF\n".encodeToByteArray()

    @Test
    fun forMangaFilerLamnarInteJobbKvar() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-stadning")
        testApplication {
            application { module(ServerMiljo(tmp, 0, FalskTolkare(), synkronJobb = true)) }
            val res =
                createClient { }.post("/api/jobb") {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("forening", "Brf Test")
                                append("arsslut", "2025")
                                repeat(MAX_FILER + 1) { i ->
                                    append(
                                        "filer",
                                        minstaPdf,
                                        Headers.build { append(HttpHeaders.ContentDisposition, "filename=f$i.pdf") },
                                    )
                                }
                            },
                        ),
                    )
                }
            assertEquals(HttpStatusCode.BadRequest, res.status)
        }
        assertTrue(JobbLagring(tmp).allaJobb().isEmpty())
    }

    @Test
    fun lasHogstStopparVidGransen() {
        val data = ByteArray(1000) { it.toByte() }
        assertArrayEquals(data, lasHogst(ByteArrayInputStream(data), 1000))
        assertNull(lasHogst(ByteArrayInputStream(data), 999))
        assertArrayEquals(ByteArray(0), lasHogst(ByteArrayInputStream(ByteArray(0)), 0))
        assertNull(lasHogst(ByteArrayInputStream(data), -1))
    }
}
