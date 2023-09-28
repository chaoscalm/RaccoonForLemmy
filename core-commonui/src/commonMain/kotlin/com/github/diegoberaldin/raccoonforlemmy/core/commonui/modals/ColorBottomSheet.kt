package com.github.diegoberaldin.raccoonforlemmy.core.commonui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.Spacing
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.components.BottomSheetHandle
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.NotificationCenterContractKeys
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.di.getNotificationCenter
import com.github.diegoberaldin.raccoonforlemmy.core.utils.onClick
import com.github.diegoberaldin.raccoonforlemmy.resources.MR
import dev.icerock.moko.resources.compose.stringResource

class ColorBottomSheet : Screen {

    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val notificationCenter = remember { getNotificationCenter() }
        Column(
            modifier = Modifier.padding(
                top = Spacing.s,
                start = Spacing.s,
                end = Spacing.s,
                bottom = Spacing.m,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BottomSheetHandle()
                Text(
                    modifier = Modifier.padding(start = Spacing.s, top = Spacing.s),
                    text = stringResource(MR.strings.settings_custom_seed_color),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            val values: List<Pair<Color?, String>> = listOf(
                Color(0xFF001F7D) to stringResource(MR.strings.settings_color_blue),
                Color(0xFF36B3B3) to stringResource(MR.strings.settings_color_aquamarine),
                Color(0xFF884DFF) to stringResource(MR.strings.settings_color_purple),
                Color(0xFF00B300) to stringResource(MR.strings.settings_color_green),
                Color(0xFFFF0000) to stringResource(MR.strings.settings_color_red),
                Color(0xFFFF66600) to stringResource(MR.strings.settings_color_orange),
                Color(0xFFFC0FC0) to stringResource(MR.strings.settings_color_pink),
                Color(0xFF303B47) to stringResource(MR.strings.settings_color_gray),
                null to stringResource(MR.strings.button_reset),
            )
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxxs),
            ) {
                for (value in values) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = Spacing.s,
                            vertical = Spacing.s,
                        ).fillMaxWidth().onClick {
                            notificationCenter.getObserver(NotificationCenterContractKeys.ChangeColor)
                                ?.also {
                                    it.invoke(value.first ?: Unit)
                                }
                            bottomSheetNavigator.hide()
                        },
                    ) {
                        Text(
                            text = value.second,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier.size(36.dp).background(
                                color = value.first ?: Color.Transparent, shape = CircleShape
                            )
                        )
                    }
                }
            }
        }
    }
}