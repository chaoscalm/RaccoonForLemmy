package com.github.diegoberaldin.raccoonforlemmy.feature.search.di

import com.github.diegoberaldin.raccoonforlemmy.core.architecture.DefaultMviModel
import com.github.diegoberaldin.raccoonforlemmy.feature.search.content.ExploreMviModel
import com.github.diegoberaldin.raccoonforlemmy.feature.search.content.ExploreViewModel
import org.koin.dsl.module

val searchTabModule = module {
    factory {
        ExploreViewModel(
            mvi = DefaultMviModel(ExploreMviModel.UiState()),
            apiConfigRepository = get(),
            identityRepository = get(),
            communityRepository = get(),
            postsRepository = get(),
            commentRepository = get(),
            keyStore = get(),
            notificationCenter = get(),
            hapticFeedback = get(),
        )
    }
}
