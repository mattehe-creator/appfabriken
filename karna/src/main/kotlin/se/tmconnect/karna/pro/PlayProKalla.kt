package se.tmconnect.karna.pro

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class PlayProKalla(
    context: Context,
    private val produktId: String,
    private val proStatusLager: ProStatusLager,
    private val coroutineScope: CoroutineScope,
) : ProKalla, PurchasesUpdatedListener {

    private val appContext = context.applicationContext
    private val _arPro = MutableStateFlow(false)
    override val arPro: StateFlow<Boolean> = _arPro.asStateFlow()

    private val billingClient: BillingClient =
        BillingClient.newBuilder(appContext)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build(),
            )
            .enableAutoServiceReconnection()
            .build()

    init {
        coroutineScope.launch {
            _arPro.value = proStatusLager.hamtaArPro()
        }
    }

    override suspend fun kontrollera() {
        try {
            if (!anslutMedAterforsok()) {
                return
            }
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            val result = billingClient.queryPurchasesAsync(params)
            if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                return
            }
            behandlaKop(result.purchasesList)
        } catch (_: Exception) {
            // Behåll cachad status vid fel.
        }
    }

    override fun kop(activity: Activity) {
        coroutineScope.launch {
            try {
                if (!anslutMedAterforsok()) {
                    return@launch
                }
                val productDetails = hamtaProduktDetaljer() ?: return@launch
                val flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build(),
                        ),
                    )
                    .build()
                billingClient.launchBillingFlow(activity, flowParams)
            } catch (_: Exception) {
                // Avbrutet eller fel — status oförändrad.
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            return
        }
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            return
        }
        val lista = purchases ?: return
        coroutineScope.launch {
            try {
                behandlaKop(lista)
            } catch (_: Exception) {
                // Ingen krasch vid fel i köpuppdatering.
            }
        }
    }

    private suspend fun behandlaKop(kop: List<Purchase>) {
        for (purchase in kop) {
            kvitteraOmBehovs(purchase)
        }
        val infos = kop.map { it.tillKopInfo() }
        val nyStatus = arProEfter(infos, produktId)
        _arPro.value = nyStatus
        proStatusLager.sattArPro(nyStatus)
    }

    private suspend fun kvitteraOmBehovs(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            return
        }
        if (purchase.isAcknowledged) {
            return
        }
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params)
    }

    private suspend fun hamtaProduktDetaljer(): ProductDetails? {
        val queryParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(produktId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                ),
            )
            .build()
        val result = billingClient.queryProductDetails(queryParams)
        if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            return null
        }
        return result.productDetailsList?.firstOrNull()
    }

    private suspend fun anslutMedAterforsok(maxForsok: Int = 3): Boolean {
        repeat(maxForsok) { forsok ->
            if (billingClient.isReady) {
                return true
            }
            val result = anslutEnGang()
            if (result.responseCode == BillingClient.BillingResponseCode.OK && billingClient.isReady) {
                return true
            }
            if (forsok < maxForsok - 1) {
                kotlinx.coroutines.delay(300L * (forsok + 1))
            }
        }
        return billingClient.isReady
    }

    private suspend fun anslutEnGang(): BillingResult =
        suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (continuation.isActive) {
                            continuation.resume(billingResult)
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        // enableAutoServiceReconnection hanterar återanslutning.
                    }
                },
            )
        }

    private fun Purchase.tillKopInfo(): KopInfo {
        val id = products.firstOrNull().orEmpty()
        val tillstand = when (purchaseState) {
            Purchase.PurchaseState.PURCHASED -> KopTillstand.PURCHASED
            Purchase.PurchaseState.PENDING -> KopTillstand.PENDING
            else -> KopTillstand.PENDING
        }
        return KopInfo(produktId = id, tillstand = tillstand)
    }
}
