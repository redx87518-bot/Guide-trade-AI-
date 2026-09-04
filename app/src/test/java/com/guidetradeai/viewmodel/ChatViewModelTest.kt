package com.guidetradeai.viewmodel

import app.cash.turbine.test
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.ResearchRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val mockChatRepository: ChatRepository = mockk(relaxed = true)
    private val mockResearchRepository: ResearchRepository = mockk(relaxed = true)
    private val mockAuthRepository: AuthRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `createNewSession updates state to Ready`() = runTest {
        val session = ChatSession(
            id = "test_session_id",
            userId = "user1",
            title = "New Chat",
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z",
        )
        coEvery { mockChatRepository.createChatSession(any()) } returns Result.success(session)

        val viewModel = ChatViewModel(mockChatRepository, mockResearchRepository, mockAuthRepository)
        viewModel.createNewSession()

        viewModel.uiState.test {
            assertTrue(awaitItem() is ChatUiState.Loading)
            val ready = awaitItem()
            assertTrue(ready is ChatUiState.Ready)
            assertTrue((ready as ChatUiState.Ready).sessionId == "test_session_id")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSession with non-empty messages sets state to Ready with messages`() = runTest {
        val messages = listOf(
            ChatMessage(
                id = "msg1",
                sessionId = "session1",
                userId = "user1",
                role = "user",
                content = "Hello",
                createdAt = "2026-01-01T00:00:00Z",
            ),
            ChatMessage(
                id = "msg2",
                sessionId = "session1",
                userId = "user1",
                role = "assistant",
                content = "Hi there!",
                createdAt = "2026-01-01T00:00:01Z",
            ),
        )
        coEvery { mockChatRepository.getChatMessages(any()) } returns Result.success(messages)

        val viewModel = ChatViewModel(mockChatRepository, mockResearchRepository, mockAuthRepository)
        viewModel.loadSession("session1")

        viewModel.uiState.test {
            assertTrue(awaitItem() is ChatUiState.Loading)
            val ready = awaitItem()
            assertTrue(ready is ChatUiState.Ready)
            assertTrue((ready as ChatUiState.Ready).messages.size == 2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
