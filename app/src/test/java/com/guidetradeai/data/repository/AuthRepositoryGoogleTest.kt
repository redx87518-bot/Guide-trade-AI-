package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
class AuthRepositoryGoogleTest {

    private val mockSupabase: SupabaseClient = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        mockkStatic(io.github.jan.supabase.gotrue.auth::class)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signInWithGoogle returns success when user is authenticated`() = runTest {
        val mockUser = mockk<io.github.jan.supabase.gotrue.user.UserSession>(relaxed = true)
        every { mockSupabase.auth.signInWith(Google) } returns mockk(relaxed = true)
        every { mockSupabase.auth.currentUserOrNull() } returns mockk {
            every { id } returns "user-123"
            every { email } returns "test@example.com"
            every { userMetadata } returns mockk {
                every { jsonObject } returns mockk {
                    every { get("full_name") } returns mockk {
                        every { jsonPrimitive } returns mockk {
                            every { content } returns "Test User"
                        }
                    }
                    every { get("avatar_url") } returns null
                }
            }
            every { createdAt } returns mockk()
            every { updatedAt } returns mockk()
        }

        val repository = AuthRepository(mockSupabase)
        val result = repository.signInWithGoogle()

        assertTrue(result is Result.Success)
        assertEquals("user-123", (result as Result.Success).data.id)
        assertEquals("test@example.com", result.data.email)
    }
}
