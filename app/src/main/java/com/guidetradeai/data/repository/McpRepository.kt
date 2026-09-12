package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.domain.Result
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

data class McpCatalogItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val permission: String = "Read Only",
    val icon: String = "ShowChart",
)

class McpRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun getCatalog(): Result<List<McpCatalogItem>> {
        return try {
            val body = JsonObject(buildMap {
                put("action", kotlinx.serialization.json.JsonPrimitive("mcp_catalog"))
            })
            val response = supabase.functions.invoke("guide-trade-agent", body = body)
            val data = response.bodyAsText()
            val items = parseCatalog(data)
            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load MCP catalog")
        }
    }

    suspend fun getConnections(): Result<List<McpCatalogItem>> {
        return try {
            val body = JsonObject(buildMap {
                put("action", kotlinx.serialization.json.JsonPrimitive("mcp_connections"))
            })
            val response = supabase.functions.invoke("guide-trade-agent", body = body)
            val data = response.bodyAsText()
            val items = parseCatalog(data)
            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load connections")
        }
    }

    private fun parseCatalog(json: String?): List<McpCatalogItem> {
        if (json == null) return emptyList()
        return try {
            val elements = Json.parseToJsonElement(json)
            if (elements is JsonObject) {
                val dataArray = elements["data"]
                if (dataArray is JsonArray) {
                    dataArray.map { elem ->
                        val obj = elem as JsonObject
                        McpCatalogItem(
                            id = obj["id"]?.jsonPrimitive?.contentOrNull ?: "",
                            name = obj["name"]?.jsonPrimitive?.contentOrNull ?: "",
                            description = obj["description"]?.jsonPrimitive?.contentOrNull ?: "",
                            category = obj["category"]?.jsonPrimitive?.contentOrNull ?: "",
                            permission = obj["permission"]?.jsonPrimitive?.contentOrNull ?: "Read Only",
                            icon = obj["icon"]?.jsonPrimitive?.contentOrNull ?: "ShowChart",
                        )
                    }
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
