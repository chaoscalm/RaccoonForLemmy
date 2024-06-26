package com.github.diegoberaldin.raccoonforlemmy.unit.rawcontent

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

private const val ACTION_ID_COPY = 0
private const val ACTION_ID_SEARCH = 1
private const val ACTION_ID_QUOTE = 2
private const val GROUP_ID = 0

class CustomTextToolbar(
    private val view: View,
    private val isLogged: Boolean,
    private val quoteActionLabel: String,
    private val shareActionLabel: String,
    private val onShare: () -> Unit,
    private val onQuote: () -> Unit,
) : TextToolbar {
    private var actionMode: ActionMode? = null

    override var status: TextToolbarStatus = TextToolbarStatus.Hidden
        private set

    override fun hide() {
        status = TextToolbarStatus.Hidden
        actionMode?.finish()
        actionMode = null
    }

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?,
    ) {
        if (actionMode == null) {
            status = TextToolbarStatus.Shown
            actionMode =
                view.startActionMode(
                    CustomTextActionModeCallback(
                        rect = rect,
                        isLogged = isLogged,
                        quoteActionLabel = quoteActionLabel,
                        shareActionLabel = shareActionLabel,
                        onCopy = {
                            onCopyRequested?.invoke()
                        },
                        onShare = {
                            onCopyRequested?.invoke()
                            onShare()
                        },
                        onQuote = {
                            onCopyRequested?.invoke()
                            onQuote()
                        },
                    ),
                    ActionMode.TYPE_FLOATING,
                )
        } else {
            actionMode?.invalidate()
            actionMode = null
        }
    }
}

private class CustomTextActionModeCallback(
    private val rect: Rect,
    private val quoteActionLabel: String,
    private val shareActionLabel: String,
    private val isLogged: Boolean,
    private val onCopy: () -> Unit,
    private val onShare: () -> Unit,
    private val onQuote: () -> Unit,
) : ActionMode.Callback2() {
    override fun onCreateActionMode(
        mode: ActionMode?,
        menu: Menu?,
    ): Boolean {
        menu?.apply {
            if (isLogged) {
                add(
                    GROUP_ID,
                    ACTION_ID_QUOTE,
                    0, // position
                    quoteActionLabel,
                ).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            }
            add(
                GROUP_ID,
                ACTION_ID_COPY,
                1, // position
                android.R.string.copy,
            ).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            add(
                GROUP_ID,
                ACTION_ID_SEARCH,
                2, // position
                shareActionLabel,
            ).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        return true
    }

    override fun onPrepareActionMode(
        mode: ActionMode?,
        menu: Menu?,
    ): Boolean = false

    override fun onActionItemClicked(
        mode: ActionMode?,
        item: MenuItem?,
    ): Boolean {
        val res =
            when (item?.itemId) {
                ACTION_ID_COPY -> {
                    onCopy()
                    true
                }

                ACTION_ID_SEARCH -> {
                    onShare()
                    true
                }

                ACTION_ID_QUOTE -> {
                    onQuote()
                    true
                }

                else -> false
            }
        mode?.finish()
        return res
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        // no-op
    }

    override fun onGetContentRect(
        mode: ActionMode?,
        view: View?,
        outRect: android.graphics.Rect?,
    ) {
        rect.apply {
            outRect?.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }
    }
}
