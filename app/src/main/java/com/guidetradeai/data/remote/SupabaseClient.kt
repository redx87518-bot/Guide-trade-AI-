package com.guidetradeai.data.remote

import io.github.supabase.auth.Auth
import io.github.supabase.auth.auth
import io.github.supabase.createSupabaseClient
import io.github.supabase.functions.Functions
import io.github.supabase.functions.functions
import io.github.supabase.postgrest.Postgrest
import io.github.supabase.postgrest.postgrest
import io.github.supabase.storage.Storage
import io.github.supabase.storage.storage
import kotlinx.serialization.json.Json

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://dnfutvafibliysnsetwm.supabase.co",
        supabaseKey = "sb_publishable_Y-kuMPpPKDT9NKKi9fCKcw_YaVdkIpL",
    ) {
        install(Auth)
        install(Postgrest)
        install(Functions)
        install(Storage)
        json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    val auth get() = client.auth
    val postgrest get() = client.postgrest
    val functions get() = client.functions
    val storage get() = client.storage
}
