package com.github.diegoberaldin.raccoonforlemmy.core.commonui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.data.VoteFormat
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.data.formatToReadableValue
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.di.getThemeRepository
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.IconSize
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.Spacing
import com.github.diegoberaldin.raccoonforlemmy.core.utils.compose.onClick
import com.github.diegoberaldin.raccoonforlemmy.core.utils.compose.rememberCallback
import com.github.diegoberaldin.raccoonforlemmy.core.utils.datetime.prettifyDate
import com.github.diegoberaldin.raccoonforlemmy.core.utils.toLocalDp

@Composable
fun PostCardFooter(
    modifier: Modifier = Modifier,
    voteFormat: VoteFormat = VoteFormat.Aggregated,
    comments: Int? = null,
    date: String? = null,
    score: Int = 0,
    upvotes: Int = 0,
    downvotes: Int = 0,
    saved: Boolean = false,
    upVoted: Boolean = false,
    downVoted: Boolean = false,
    actionButtonsActive: Boolean = true,
    options: List<Option> = emptyList(),
    onUpVote: (() -> Unit)? = null,
    onDownVote: (() -> Unit)? = null,
    onSave: (() -> Unit)? = null,
    onReply: (() -> Unit)? = null,
    onOptionSelected: ((OptionId) -> Unit)? = null,
) {
    var optionsExpanded by remember { mutableStateOf(false) }
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    val themeRepository = remember { getThemeRepository() }
    val upvoteColor by themeRepository.upvoteColor.collectAsState()
    val downvoteColor by themeRepository.downvoteColor.collectAsState()
    val defaultUpvoteColor = MaterialTheme.colorScheme.primary
    val defaultDownVoteColor = MaterialTheme.colorScheme.tertiary
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            val buttonModifier = Modifier.size(IconSize.m).padding(3.5.dp)
            if (comments != null) {
                Image(
                    modifier = buttonModifier.padding(1.dp)
                        .onClick(
                            onClick = rememberCallback {
                                onReply?.invoke()
                            },
                        ),
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = ancillaryColor),
                )
                Text(
                    modifier = Modifier.padding(end = Spacing.s),
                    text = "$comments",
                    style = MaterialTheme.typography.labelLarge,
                    color = ancillaryColor,
                )
            }
            if (date != null) {
                Icon(
                    modifier = buttonModifier.padding(1.dp),
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = ancillaryColor,
                )
                Text(
                    text = date.prettifyDate(),
                    style = MaterialTheme.typography.labelLarge,
                    color = ancillaryColor,
                )
            }
            if (options.isNotEmpty()) {
                Icon(
                    modifier = buttonModifier
                        .padding(top = Spacing.xxs)
                        .onGloballyPositioned {
                            optionsOffset = it.positionInParent()
                        }
                        .onClick(
                            onClick = rememberCallback {
                                optionsExpanded = true
                            },
                        ),
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = null,
                    tint = ancillaryColor,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (actionButtonsActive) {
                Image(
                    modifier = buttonModifier.onClick(
                        onClick = rememberCallback {
                            onSave?.invoke()
                        },
                    ),
                    imageVector = if (!saved) {
                        Icons.Default.BookmarkBorder
                    } else {
                        Icons.Default.Bookmark
                    },
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        color = if (saved) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            ancillaryColor
                        },
                    ),
                )
            }
            Image(
                modifier = buttonModifier
                    .onClick(
                        onClick = rememberCallback {
                            onUpVote?.invoke()
                        },
                    ),
                imageVector = if (actionButtonsActive) {
                    Icons.Default.ArrowCircleUp
                } else {
                    Icons.Default.ArrowUpward
                },
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = if (upVoted) {
                        upvoteColor ?: defaultUpvoteColor
                    } else {
                        ancillaryColor
                    },
                ),
            )
            Text(
                text = formatToReadableValue(
                    voteFormat = voteFormat,
                    score = score,
                    upvotes = upvotes,
                    downvotes = downvotes,
                    upvoteColor = upvoteColor ?: defaultUpvoteColor,
                    downvoteColor = downvoteColor ?: defaultDownVoteColor,
                    upVoted = upVoted,
                    downVoted = downVoted,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = ancillaryColor,
            )
            Image(
                modifier = buttonModifier
                    .onClick(
                        onClick = rememberCallback {
                            onDownVote?.invoke()
                        },
                    ),
                imageVector = if (actionButtonsActive) {
                    Icons.Default.ArrowCircleDown
                } else {
                    Icons.Default.ArrowDownward
                },
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = if (downVoted) {
                        downvoteColor ?: defaultDownVoteColor
                    } else {
                        ancillaryColor
                    },
                ),
            )
        }

        CustomDropDown(
            expanded = optionsExpanded,
            onDismiss = {
                optionsExpanded = false
            },
            offset = DpOffset(
                x = optionsOffset.x.toLocalDp(),
                y = optionsOffset.y.toLocalDp(),
            ),
        ) {
            options.forEach { option ->
                Text(
                    modifier = Modifier.padding(
                        horizontal = Spacing.m,
                        vertical = Spacing.s,
                    ).onClick(
                        onClick = rememberCallback {
                            optionsExpanded = false
                            onOptionSelected?.invoke(option.id)
                        },
                    ),
                    text = option.text,
                )
            }
        }
    }
}
