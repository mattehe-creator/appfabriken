package se.tmconnect.styrelserummet.server

import org.junit.Test
import org.junit.Assert
import se.tmconnect.styrelserummet.regler.PlanAr
import se.tmconnect.styrelserummet.regler.kronor

class UtskriftPlanTest {
    
    @Test
    fun testUtskriftHtmlMedTomPlan() {
        val html = utskriftHtml("Brf Sol", 2025, emptyList())
        Assert.assertFalse(html.contains("Avskrivningsplan"))
    }
    
    @Test
    fun testUtskriftHtmlMedPlan() {
        val plan = listOf(PlanAr(2026, 60000, 780000))
        val html = utskriftHtml("Brf Sol", 2025, emptyList(), plan)
        
        Assert.assertTrue(html.contains("Avskrivningsplan"))
        Assert.assertTrue(html.contains("<td>2026</td>"))
        Assert.assertTrue(html.contains(kronor(60_000L)))
    }
}
