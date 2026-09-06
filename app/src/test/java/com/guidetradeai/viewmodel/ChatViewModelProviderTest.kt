package com.guidetradeai.viewmodel

import app.cash.turbine.test
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.MarketIntelligenceRepository
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import com.guidetradeai.domain.model.SymbolItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelProviderTest {

    private val mockChatRepository: ChatRepository = mockk(relaxed = true)
    private val mockAuthRepository: AuthRepository = mockk(relaxed = true)
    private val mockMarketIntelligenceRepository: MarketIntelligenceRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setProvider updates provider state and resets feature`() = runTest {
        val viewModel = ChatViewModel(
            chatRepository = mockChatRepository,
            authRepository = mockAuthRepository,
            marketIntelligenceRepository = mockMarketIntelligenceRepository,
        )

        viewModel.setProvider("Guavy")
        assertEquals("Guavy", viewModel.selectedProvider.value)
        assertEquals("Full Analysis", viewModel.selectedFeature.value)
        assertEquals(null, viewModel.selectedMarket.value)
        assertEquals(null, viewModel.selectedSymbol.value)
    }

    @Test
    fun `setProvider SiftingIO sets correct default feature`() = runTest {
        val viewModel = ChatViewModel(
            chatRepository = mockChatRepository,
            authRepository = mockAuthRepository,
            marketIntelligenceRepository = mockMarketIntelligenceRepository,
        )

        viewModel.setProvider("SiftingIO")
        assertEquals("SiftingIO", viewModel.selectedProvider.value)
        assertEquals("Full Analysis", viewModel.selectedFeature.value)
    }

    @Test
    fun `setProvider StockUp preserves chat feature`() = runTest {
        val viewModel = ChatViewModel(
            chatRepository = mockChatRepository,
            authRepository = mockAuthRepository,
            marketIntelligenceRepository = mockMarketIntelligenceRepository,
        )

        viewModel.setProvider("StockUp")
        assertEquals("StockUp", viewModel.selectedProvider.value)
        assertEquals("Chat", viewModel.selectedFeature.value)
    }

    @Test
    fun `loadSymbols returns cached result on second call`() = runTest {
        val symbols = listOf(SymbolItem(symbol = "BTC", name = "Bitcoin", market = "crypto"))
        coEvery { mockMarketIntelligenceRepository.listSymbols("guavy", "crypto") } returns Result.success(symbols)

        val viewModel = ChatViewModel(
            chatRepository = mockChatRepository,
            authRepository = mockAuthRepository,
            marketIntelligenceRepository = mockMarketIntelligenceRepository,
        )

        val result1 = viewModel.loadSymbols("guavy", "crypto")
        val result2 = viewModel.loadSymbols("guavy", "crypto")

        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        assertEquals(symbols, (result1 as Result.Success).data)
        assertEquals(symbols, (result2 as Result.Success).data)
    }
}
