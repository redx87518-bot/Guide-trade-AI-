package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClientWrapper
import com.guidetradeai.data.remote.ResearchResultData
import com.guidetradeai.domain.Result

class ResearchRepository(private val supabase: SupabaseClientWrapper = SupabaseClientWrapper) {

    suspend fun getResearchResults(): Result<List<ResearchResultData>> {
        return supabase.getResearchResults()
    }

    suspend fun saveResearchResult(result: ResearchResultData): Result<ResearchResultData> {
        return supabase.saveResearchResult(result)
    }
}
