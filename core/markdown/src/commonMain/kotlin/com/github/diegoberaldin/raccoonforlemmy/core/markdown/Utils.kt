package com.github.diegoberaldin.raccoonforlemmy.core.markdown

import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

internal fun ASTNode.findChildOfTypeRecursive(type: IElementType): ASTNode? {
    children.forEach {
        if (it.type == type) {
            return it
        } else {
            val found = it.findChildOfTypeRecursive(type)
            if (found != null) {
                return found
            }
        }
    }
    return null
}

internal fun String.sanitize(): String =
    this
        .removeEntities()
        .spoilerFixUp()
        .quoteFixUp()
        .expandLemmyHandles()
        .cleanupEscapes()

private fun String.removeEntities(): String =
    replace("&amp;", "&")
        .replace("&nbsp;", " ")
        .replace("&hellip;", "…")

private fun String.spoilerFixUp(): String =
    run {
        val finalLines = mutableListOf<String>()
        var isInsideSpoiler = false
        lines().forEach { line ->
            if (line.contains(SpoilerRegex.spoilerOpening)) {
                if (finalLines.lastOrNull()?.isEmpty() == false) {
                    finalLines += ""
                }
                finalLines += line
                isInsideSpoiler = true
            } else if (line.contains(SpoilerRegex.spoilerClosing)) {
                isInsideSpoiler = false
            } else if (line.isNotBlank()) {
                if (isInsideSpoiler) {
                    // spoilers must be treated as a single paragraph, so if inside spoilers it is necessary to remove
                    // all bulleted lists, numbered lists and blank lines in general
                    val cleanLine =
                        line
                            .replace(Regex("^\\s*?-\\s*?"), "")
                            .replace(Regex("^\\s*?\\d?\\.\\s*?"), "")
                            .trim()
                    if (cleanLine.isNotBlank()) {
                        finalLines += cleanLine
                    }
                } else {
                    finalLines += line
                }
            } else if (!isInsideSpoiler) {
                finalLines += ""
            }
        }
        finalLines.joinToString("\n")
    }

private fun String.quoteFixUp(): String =
    run {
        val finalLines = mutableListOf<String>()
        lines().forEach { line ->
            // removes list inside quotes
            val quoteAndList = Regex("^>-")
            val cleanLine = line.replace(quoteAndList, "-")
            val isLastEmpty = finalLines.isNotEmpty() && finalLines.last().isEmpty()
            if (!isLastEmpty || cleanLine.isNotEmpty()) {
                finalLines += cleanLine
            }
        }
        finalLines.joinToString("\n")
    }

private fun String.expandLemmyHandles(): String =
    let { content ->
        buildString {
            val matches = LemmyLinkRegex.lemmyHandle.findAll(content)
            var lastIndex = 0
            for (match in matches) {
                val start = match.range.first
                val end = match.range.last
                if (start > lastIndex) {
                    append(content.substring(startIndex = lastIndex, endIndex = start))
                }
                val detail = match.groups["detail"]?.value.orEmpty()
                val instance = match.groups["instance"]?.value.orEmpty()
                append("[$detail@$instance](!$detail@$instance)")
                lastIndex = end + 1
            }
            if (lastIndex < content.lastIndex) {
                append(content.substring(startIndex = lastIndex))
            }
        }
    }

private fun String.cleanupEscapes(): String = replace("\\#", "#")
