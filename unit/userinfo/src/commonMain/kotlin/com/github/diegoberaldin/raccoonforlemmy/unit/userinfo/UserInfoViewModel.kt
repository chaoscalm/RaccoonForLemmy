package com.github.diegoberaldin.raccoonforlemmy.unit.userinfo

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.diegoberaldin.raccoonforlemmy.core.architecture.DefaultMviModel
import com.github.diegoberaldin.raccoonforlemmy.core.persistence.repository.SettingsRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.UserModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.LemmyItemCache
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.UserRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UserInfoViewModel(
    private val userId: Long,
    private val username: String,
    private val otherInstance: String,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val itemCache: LemmyItemCache,
) : UserInfoMviModel,
    DefaultMviModel<UserInfoMviModel.Intent, UserInfoMviModel.UiState, UserInfoMviModel.Effect>(
        initialState = UserInfoMviModel.UiState(),
    ) {
    init {
        screenModelScope.launch {
            val user = itemCache.getUser(userId) ?: UserModel()
            updateState {
                it.copy(user = user)
            }
            settingsRepository.currentSettings.onEach {
                updateState { it.copy(autoLoadImages = it.autoLoadImages) }
            }.launchIn(this)

            if (uiState.value.moderatedCommunities.isEmpty()) {
                val updatedUser =
                    userRepository.get(
                        id = user.id,
                        username = username,
                        otherInstance = otherInstance,
                    )
                if (updatedUser != null) {
                    updateState {
                        it.copy(user = updatedUser)
                    }
                }
                val communities = userRepository.getModeratedCommunities(id = user.id)
                updateState { it.copy(moderatedCommunities = communities) }
            }
        }
    }
}
