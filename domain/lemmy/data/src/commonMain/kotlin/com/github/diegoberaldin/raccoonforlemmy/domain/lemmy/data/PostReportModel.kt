package com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data

import com.github.diegoberaldin.raccoonforlemmy.core.utils.JavaSerializable
import com.github.diegoberaldin.raccoonforlemmy.core.utils.looksLikeAnImage

data class PostReportModel(
    val id: Int = 0,
    val creator: UserModel? = null,
    val postId: Int = 0,
    val reason: String? = null,
    val originalTitle: String? = null,
    val originalText: String? = null,
    val originalUrl: String? = null,
    val thumbnailUrl: String? = null,
    val resolved: Boolean = false,
    val resolver: UserModel? = null,
    val publishDate: String? = null,
    val updateDate: String? = null,
) : JavaSerializable

val PostReportModel.imageUrl: String
    get() = originalUrl?.takeIf { it.looksLikeAnImage }?.takeIf { it.isNotEmpty() } ?: run {
        thumbnailUrl
    }.orEmpty()
