package se.tmconnect.styrelserummet.server

import org.junit.Test
import org.junit.Assert
import se.tmconnect.styrelserummet.regler.PlanAr

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
        Assert.assertTrue(html.contains("60\u00A0000 kr")) // 60 000 kr formaterat med mellanslag
    }
}
