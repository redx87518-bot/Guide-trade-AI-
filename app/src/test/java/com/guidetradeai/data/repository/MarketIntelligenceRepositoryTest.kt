package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.SymbolItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketIntelligenceRepositoryTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `parse symbols from Guavy response format`() {
        val json = buildJsonObject {
            put("result", buildJsonObject {
                put("data", Json.parseToJsonElement("""[{"symbol":"BTC","name":"Bitcoin"},{"symbol":"ETH","name":"Ethereum"}]""").jsonArray)
            })
        }
        val result = json["result"]?.jsonObject
        val symbols = mutableListOf<SymbolItem>()
        
        result?.get("data")?.let { dataElement ->
            if (dataElement is kotlinx.serialization.json.JsonArray) {
                dataElement.forEach { item ->
                    val obj = item.jsonObject
                    val symbol = obj["symbol"]?.jsonPrimitive?.content ?: ""
                    val name = obj["name"]?.jsonPrimitive?.content ?: symbol
                    if (symbol.isNotBlank()) {
                        symbols.add(SymbolItem(symbol = symbol, name = name, market = "crypto"))
                    }
                }
            }
        }
        
        assertEquals(2, symbols.size)
        assertEquals("BTC", symbols[0].symbol)
        assertEquals("Bitcoin", symbols[0].name)
        assertEquals("ETH", symbols[1].symbol)
    }

    @Test
    fun `parse symbols from SiftingIO response format`() {
        val json = buildJsonObject {
            put("result", buildJsonObject {
                put("symbols", buildJsonObject {
                    put("BTCUSD", buildJsonObject { put("name", JsonPrimitive("Bitcoin USD")) })
                    put("ETHUSD", buildJsonObject { put("name", JsonPrimitive("Ethereum USD")) })
                })
            })
        }
        val result = json["result"]?.jsonObject
        val symbols = mutableListOf<SymbolItem>()
        
        result?.get("symbols")?.jsonObject?.forEach { (key, value) ->
            val name = value.jsonObject["name"]?.jsonPrimitive?.content ?: key
            symbols.add(SymbolItem(symbol = key, name = name, market = "crypto"))
        }
        
        assertEquals(2, symbols.size)
        assertEquals("BTCUSD", symbols[0].symbol)
        assertEquals("Bitcoin USD", symbols[0].name)
    }
}
