package com.github.diegoberaldin.raccoonforlemmy.core.commonui.instanceinfo

import com.github.diegoberaldin.raccoonforlemmy.core.architecture.MviModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.CommunityModel

interface InstanceInfoMviModel :
    MviModel<InstanceInfoMviModel.Intent, InstanceInfoMviModel.UiState, InstanceInfoMviModel.Effect> {

    sealed interface Intent {
        object Refresh : Intent
        object LoadNextPage : Intent
    }

    data class UiState(
        val title: String = "",
        val description: String = "",
        val canFetchMore: Boolean = true,
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val communities: List<CommunityModel> = emptyList(),
    )

    sealed interface Effect
}