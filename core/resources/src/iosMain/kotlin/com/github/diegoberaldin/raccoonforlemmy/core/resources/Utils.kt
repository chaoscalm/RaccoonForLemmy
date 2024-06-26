package com.github.diegoberaldin.raccoonforlemmy.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes
import androidx.compose.ui.text.platform.Font as PlatformFont

private val cache: MutableMap<String, Font> = mutableMapOf()

@OptIn(InternalResourceApi::class)
@Composable
actual fun font(
    name: String,
    res: String,
    weight: FontWeight,
    style: FontStyle,
): Font {
    return cache.getOrPut(res) {
        val byteArray =
            runBlocking {
                readResourceBytes("font/$res.ttf")
            }
        PlatformFont(res, byteArray, weight, style)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun drawable(res: String): Painter {
    return drawable("drawable/$res")
}
