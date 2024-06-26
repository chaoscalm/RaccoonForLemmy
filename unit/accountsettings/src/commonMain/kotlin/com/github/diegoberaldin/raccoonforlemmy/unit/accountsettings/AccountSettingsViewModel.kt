package com.github.diegoberaldin.raccoonforlemmy.unit.accountsettings

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.diegoberaldin.raccoonforlemmy.core.architecture.DefaultMviModel
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.NotificationCenter
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.NotificationCenterEvent
import com.github.diegoberaldin.raccoonforlemmy.domain.identity.repository.IdentityRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.AccountSettingsModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.ListingType
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.SortType
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.GetSortTypesUseCase
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.PostRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.SiteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AccountSettingsViewModel(
    private val siteRepository: SiteRepository,
    private val identityRepository: IdentityRepository,
    private val postRepository: PostRepository,
    private val getSortTypesUseCase: GetSortTypesUseCase,
    private val notificationCenter: NotificationCenter,
) : AccountSettingsMviModel,
    DefaultMviModel<AccountSettingsMviModel.Intent, AccountSettingsMviModel.UiState, AccountSettingsMviModel.Effect>(
        initialState = AccountSettingsMviModel.UiState(),
    ) {
    private var accountSettings: AccountSettingsModel? = null

    init {
        screenModelScope.launch {
            notificationCenter.subscribe(NotificationCenterEvent.ChangeSortType::class)
                .onEach { evt ->
                    if (evt.screenKey == "accountSettings") {
                        updateState { it.copy(defaultSortType = evt.value) }
                    }
                }.launchIn(this)
            notificationCenter.subscribe(NotificationCenterEvent.ChangeFeedType::class)
                .onEach { evt ->
                    if (evt.screenKey == "accountSettings") {
                        updateState { it.copy(defaultListingType = evt.value) }
                    }
                }.launchIn(this)

            if (accountSettings == null) {
                refreshSettings()
                val availableSortTypes = getSortTypesUseCase.getTypesForPosts()
                updateState { it.copy(availableSortTypes = availableSortTypes) }
            }
        }
    }

    override fun reduce(intent: AccountSettingsMviModel.Intent) {
        when (intent) {
            is AccountSettingsMviModel.Intent.ChangeDisplayName -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            displayName = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.ChangeEmail -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            email = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.ChangeMatrixUserId -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            matrixUserId = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.ChangeBio -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            bio = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.ChangeBot -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            bot = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.ChangeSendNotificationsToEmail -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            sendNotificationsToEmail = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.ChangeShowBotAccounts -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            showBotAccounts = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.ChangeShowNsfw -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            showNsfw = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.ChangeShowScores -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            showScores = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.ChangeShowReadPosts -> {
                screenModelScope.launch {
                    updateState {
                        it.copy(
                            showReadPosts = intent.value,
                            hasUnsavedChanges = true,
                        )
                    }
                }
            }

            is AccountSettingsMviModel.Intent.AvatarSelected -> {
                loadImageAvatar(intent.value)
            }

            is AccountSettingsMviModel.Intent.BannerSelected -> {
                loadImageBanner(intent.value)
            }

            AccountSettingsMviModel.Intent.Submit -> submit()
        }
    }

    private suspend fun refreshSettings() {
        updateState { it.copy(loading = true) }
        val auth = identityRepository.authToken.value.orEmpty()
        accountSettings = siteRepository.getAccountSettings(auth)
        updateState {
            it.copy(
                loading = false,
                avatar = accountSettings?.avatar.orEmpty(),
                banner = accountSettings?.banner.orEmpty(),
                bio = accountSettings?.bio.orEmpty(),
                bot = accountSettings?.bot ?: false,
                sendNotificationsToEmail = accountSettings?.sendNotificationsToEmail ?: false,
                displayName = accountSettings?.displayName.orEmpty(),
                matrixUserId = accountSettings?.matrixUserId.orEmpty(),
                email = accountSettings?.email.orEmpty(),
                showBotAccounts = accountSettings?.showBotAccounts ?: false,
                showReadPosts = accountSettings?.showReadPosts ?: false,
                showNsfw = accountSettings?.showNsfw ?: false,
                showScores = accountSettings?.showScores ?: true,
                defaultListingType = accountSettings?.defaultListingType ?: ListingType.All,
                defaultSortType = accountSettings?.defaultSortType ?: SortType.Active,
            )
        }
    }

    private fun loadImageAvatar(bytes: ByteArray) {
        if (bytes.isEmpty()) {
            return
        }
        screenModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(loading = true) }
            val auth = identityRepository.authToken.value.orEmpty()
            val url = postRepository.uploadImage(auth, bytes)
            if (url != null) {
                updateState {
                    it.copy(
                        avatar = url,
                        hasUnsavedChanges = true,
                        loading = false,
                    )
                }
            }
        }
    }

    private fun loadImageBanner(bytes: ByteArray) {
        if (bytes.isEmpty()) {
            return
        }
        screenModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(loading = true) }
            val auth = identityRepository.authToken.value.orEmpty()
            val url = postRepository.uploadImage(auth, bytes)
            if (url != null) {
                updateState {
                    it.copy(
                        banner = url,
                        hasUnsavedChanges = true,
                        loading = false,
                    )
                }
            }
        }
    }

    private fun submit() {
        val currentState = uiState.value
        val settingsToSave =
            accountSettings?.copy(
                avatar = currentState.avatar,
                banner = currentState.banner,
                bio = currentState.bio,
                bot = currentState.bot,
                defaultListingType = currentState.defaultListingType,
                defaultSortType = currentState.defaultSortType,
                displayName = currentState.displayName,
                email = currentState.email,
                matrixUserId = currentState.matrixUserId,
                sendNotificationsToEmail = currentState.sendNotificationsToEmail,
                showBotAccounts = currentState.showBotAccounts,
                showNsfw = currentState.showNsfw,
                showScores = currentState.showScores,
                showReadPosts = currentState.showReadPosts,
            ) ?: return
        screenModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(loading = true) }
            try {
                val auth = identityRepository.authToken.value.orEmpty()
                siteRepository.updateAccountSettings(
                    auth = auth,
                    value = settingsToSave,
                )
                refreshSettings()
                updateState {
                    it.copy(
                        loading = false,
                        hasUnsavedChanges = false,
                    )
                }
                emitEffect(
                    AccountSettingsMviModel.Effect.Success,
                )
            } catch (e: Exception) {
                updateState { it.copy(loading = false) }
                emitEffect(
                    AccountSettingsMviModel.Effect.Failure,
                )
            }
        }
    }
}
