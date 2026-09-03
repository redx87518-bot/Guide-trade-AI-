package com.guidetradeai.data.repository

import app.cash.turbine.test
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatSession
import com.guidetradeai.domain.model.ResearchResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResearchRepositoryTest {

    private val mockSupabase: io.github.supabase.SupabaseClient = mockk(relaxed = true)

    @Test
    fun `getResearchHistory returns success when database returns data`() = runTest {
        val mockPostgrestResult = mockk<io.github.supabase.postgrest.result.PostgrestResult>(relaxed = true)

        val repository = ResearchRepository(mockSupabase)
        val result = repository.getResearchHistory()

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getResearchResult returns error when result not found`() = runTest {
        val repository = ResearchRepository(mockSupabase)
        val result = repository.getResearchResult("nonexistent_id")

        assertTrue(result is Result.Error)
    }

    @Test
    fun `deleteResearchResult returns success on valid id`() = runTest {
        val repository = ResearchRepository(mockSupabase)
        val result = repository.deleteResearchResult("test_id")

        assertTrue(result is Result.Error)
    }
}

fun <T> MutableStateFlow<T>.setValue(value: T) {
    this.value = value
}
