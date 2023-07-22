package com.github.diegoberaldin.raccoonforlemmy.feature_settings

import com.github.diegoberaldin.raccoonforlemmy.core_architecture.DefaultMviModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

actual val settingsTabModule = module {
    factory {
        SettingsScreenModel(
            themeRepository = get(),
            keyStore = get(),
            mvi = DefaultMviModel(
                SettingsScreenMviModel.UiState()
            )
        )
    }
}

actual fun getSettingsScreenModel() = SettingsScreenModelHelper.model

object SettingsScreenModelHelper : KoinComponent {
    val model: SettingsScreenModel by inject()
}