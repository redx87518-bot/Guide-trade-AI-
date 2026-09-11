package com.guidetradeai

import android.app.Application
import com.guidetradeai.data.local.AppPreferences
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.GuideTradeAgentRepository
import com.guidetradeai.data.repository.PaperTradingRepository
import com.guidetradeai.data.repository.ResearchRepository
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.data.repository.TelegramRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.ui.theme.GuideTradeColors
import com.guidetradeai.ui.theme.GuideTradeTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

class GuideTradeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.applicationContext = applicationContext
        AppModule.appPreferences = AppPreferences(applicationContext)
    }
}
