package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.MarketIntelligenceRequest
import com.guidetradeai.domain.model.SymbolItem
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketIntelligenceRepositoryTest {

    private val mockSupabase: SupabaseClient = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `queryProvider with StockUp calls ai-chat function`() = runTest {
        val mockFunctions: Functions = mockk(relaxed = true)
        coEvery { mockSupabase.functions } returns mockFunctions
        val responseBody = buildJsonObject {
            put("content", JsonPrimitive("Hello from Quan"))
            put("role", JsonPrimitive("assistant"))
        }
        coEvery { mockFunctions.invoke(eq("ai-chat"), any()) } returns responseBody

        val repository = MarketIntelligenceRepository(mockSupabase)
        val request = MarketIntelligenceRequest(
            provider = "stockup",
            feature = "chat",
            query = "Hello",
        )
        val result = repository.queryProvider(request)

        assertTrue(result is Result.Success)
        assertEquals("Hello from Quan", (result as Result.Success).data.result?.get("content")?.jsonPrimitive?.content)
    }

    @Test
    fun `listSymbols parses symbols from response`() = runTest {
        val mockFunctions: Functions = mockk(relaxed = true)
        coEvery { mockSupabase.functions } returns mockFunctions
        val responseBody = buildJsonObject {
            put("result", buildJsonObject {
                put("data", Json.parseToJsonElement("""[{"symbol":"BTC","name":"Bitcoin"},{"symbol":"ETH","name":"Ethereum"}]""").jsonArray)
            })
        }
        coEvery { mockFunctions.invoke(eq("market-intelligence"), any()) } returns responseBody

        val repository = MarketIntelligenceRepository(mockSupabase)
        val result = repository.listSymbols("guavy", "crypto")

        assertTrue(result is Result.Success)
        val symbols = (result as Result.Success).data
        assertEquals(2, symbols.size)
        assertEquals("BTC", symbols[0].symbol)
        assertEquals("Bitcoin", symbols[0].name)
        assertEquals("ETH", symbols[1].symbol)
    }
}
