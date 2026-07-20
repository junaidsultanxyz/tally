package app.tally.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

@Serializable
private data class ExchangeRateResponse(
    val result: String? = null,
    val rates: Map<String, Double>? = null,
)

/** Extracted so `ExchangeRateRepository` can be unit-tested with a fake, no real network or Ktor `MockEngine` involved. */
public interface ExchangeRateSource {
    public suspend fun fetchRates(baseCurrency: String): Map<String, Double>
}

/**
 * A free, keyless FX-rate endpoint (open.er-api.com — no signup, no embedded secret to ship
 * in the app). Returns every target currency's rate relative to [baseCurrency] in one call, so
 * one request covers the whole 30-currency table rather than needing a call per pair.
 */
public class ExchangeRateApi(private val httpClient: HttpClient) : ExchangeRateSource {

    override suspend fun fetchRates(baseCurrency: String): Map<String, Double> {
        val response: ExchangeRateResponse = httpClient.get("https://open.er-api.com/v6/latest/$baseCurrency") {
            timeout { requestTimeoutMillis = 10_000 }
        }.body()
        return if (response.result == "success") response.rates.orEmpty() else emptyMap()
    }
}
