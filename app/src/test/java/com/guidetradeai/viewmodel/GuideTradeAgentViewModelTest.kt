package com.guidetradeai.viewmodel

import app.cash.turbine.test
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.GuideTradeAgentRepository
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentResponse
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
class GuideTradeAgentViewModelTest {

    private val mockAgentRepository: GuideTradeAgentRepository = mockk(relaxed = true)
    private val mockAuthRepository: AuthRepository = mockk(relaxed = true)
    private val mockChatRepository: ChatRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendMessage sets loading then success`() = runTest {
        val user = com.guidetradeai.domain.model.User(id = "user1", email = "test@test.com")
        coEvery { mockAuthRepository.getCurrentUser() } returns user
        coEvery { mockChatRepository.createSession(any()) } returns Result.success("session1")
        coEvery { mockAgentRepository.sendRequest(any()) } returns Result.success(
            AgentResponse(content = "Hello", summary = "Summary", toolsUsed = listOf("tool1"))
        )

        val viewModel = GuideTradeAgentViewModel(
            agentRepository = mockAgentRepository,
            authRepository = mockAuthRepository,
        )

        viewModel.sendMessage("Hello")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is com.guidetradeai.viewmodel.AgentUiState.Success)
            val success = state as com.guidetradeai.viewmodel.AgentUiState.Success
            assertEquals("Hello", success.response.content)
            assertEquals("Summary", success.response.summary)
            assertEquals(listOf("tool1"), success.response.toolsUsed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendMessage handles error`() = runTest {
        val user = com.guidetradeai.domain.model.User(id = "user1", email = "test@test.com")
        coEvery { mockAuthRepository.getCurrentUser() } returns user
        coEvery { mockChatRepository.createSession(any()) } returns Result.success("session1")
        coEvery { mockAgentRepository.sendRequest(any()) } returns Result.error("Network failed")

        val viewModel = GuideTradeAgentViewModel(
            agentRepository = mockAgentRepository,
            authRepository = mockAuthRepository,
        )

        viewModel.sendMessage("Hello")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is com.guidetradeai.viewmodel.AgentUiState.Error)
            assertEquals("Network failed", (state as com.guidetradeai.viewmodel.AgentUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setMarket updates selected market`() = runTest {
        val viewModel = GuideTradeAgentViewModel(
            agentRepository = mockAgentRepository,
            authRepository = mockAuthRepository,
        )

        viewModel.setMarket("crypto")
        assertEquals("crypto", viewModel.selectedMarket.value)
    }

    @Test
    fun `setSymbol updates selected symbol`() = runTest {
        val viewModel = GuideTradeAgentViewModel(
            agentRepository = mockAgentRepository,
            authRepository = mockAuthRepository,
        )

        viewModel.setSymbol("BTCUSD")
        assertEquals("BTCUSD", viewModel.selectedSymbol.value)
    }

    @Test
    fun `setTimeframe updates selected timeframe`() = runTest {
        val viewModel = GuideTradeAgentViewModel(
            agentRepository = mockAgentRepository,
            authRepository = mockAuthRepository,
        )

        viewModel.setTimeframe("1h")
        assertEquals("1h", viewModel.selectedTimeframe.value)
    }

    @Test
    fun `startNewSession creates new session id`() = runTest {
        val user = com.guidetradeai.domain.model.User(id = "user1", email = "test@test.com")
        coEvery { mockAuthRepository.getCurrentUser() } returns user

        val viewModel = GuideTradeAgentViewModel(
            agentRepository = mockAgentRepository,
            authRepository = mockAuthRepository,
        )

        viewModel.startNewSession()
        val sessionId = viewModel.currentSessionId.value
        assertTrue(sessionId != null && sessionId.isNotBlank())
    }
}
