package com.guidetradeai.viewmodel

import app.cash.turbine.test
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.GuideTradeAgentRepository
import com.guidetradeai.data.repository.MarketIntelligenceRepository
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.SymbolItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ChatViewModelSymbolSearchTest {

    private val mockChatRepository: ChatRepository = mockk(relaxed = true)
    private val mockAuthRepository: AuthRepository = mockk(relaxed = true)
    private val mockMarketIntelligenceRepository: MarketIntelligenceRepository = mockk(relaxed = true)
    private val mockAgentRepository: GuideTradeAgentRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSymbols Guavy returns success`() = runTest {
        val symbols = listOf(SymbolItem(symbol = "BTC", name = "Bitcoin", market = "crypto"))
        coEvery { mockMarketIntelligenceRepository.listSymbols("guavy", "crypto") } returns Result.success(symbols)

        val viewModel = ChatViewModel(
            chatRepository = mockChatRepository,
            authRepository = mockAuthRepository,
            marketIntelligenceRepository = mockMarketIntelligenceRepository,
            agentRepository = mockAgentRepository,
        )

        viewModel.loadSymbols("guavy", "crypto")

        viewModel.symbolSuggestions.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("BTC", result[0].symbol)
            assertEquals("Bitcoin", result[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSymbols SiftingIO returns error`() = runTest {
        val viewModel = ChatViewModel(
            chatRepository = mockChatRepository,
            authRepository = mockAuthRepository,
            marketIntelligenceRepository = mockMarketIntelligenceRepository,
            agentRepository = mockAgentRepository,
        )

        viewModel.loadSymbols("siftingio", "crypto")

        viewModel.symbolError.test {
            val error = awaitItem()
            assertEquals("SiftingIO does not support symbol listing", error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSymbols Agent returns success`() = runTest {
        val symbols = listOf(SymbolItem(symbol = "ETH", name = "Ethereum", market = "crypto"))
        coEvery { mockAgentRepository.listSymbols("guidetrade_agent", "crypto") } returns Result.success(symbols)

        val viewModel = ChatViewModel(
            chatRepository = mockChatRepository,
            authRepository = mockAuthRepository,
            marketIntelligenceRepository = mockMarketIntelligenceRepository,
            agentRepository = mockAgentRepository,
        )

        viewModel.loadSymbols("guidetrade_agent", "crypto")

        viewModel.symbolSuggestions.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("ETH", result[0].symbol)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSymbols sets loading state`() = runTest {
        val viewModel = ChatViewModel(
            chatRepository = mockChatRepository,
            authRepository = mockAuthRepository,
            marketIntelligenceRepository = mockMarketIntelligenceRepository,
            agentRepository = mockAgentRepository,
        )

        viewModel.loadSymbols("guavy", "crypto")

        viewModel.isLoadingSymbols.test {
            assertTrue(awaitItem())
            assertTrue(!awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
