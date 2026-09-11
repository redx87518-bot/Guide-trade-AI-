package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClientWrapper
import com.guidetradeai.data.remote.McpConnectionData
import com.guidetradeai.domain.Result

class McpRepository(private val supabase: SupabaseClientWrapper = SupabaseClientWrapper) {

    suspend fun getConnections(): Result<List<McpConnectionData>> {
        return supabase.getMcpConnections()
    }

    suspend fun saveConnection(connection: McpConnectionData): Result<McpConnectionData> {
        return supabase.saveMcpConnection(connection)
    }

    suspend fun deleteConnection(connectionId: String): Result<Unit> {
        return supabase.deleteMcpConnection(connectionId)
    }
}
