package com.github.diegoberaldin.raccoonforlemmy.core.api.service

import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.BlockPersonForm
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.BlockPersonResponse
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.CommentSortType
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.CommunityId
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.GetPersonDetailsResponse
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.GetPersonMentionsResponse
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.GetRepliesResponse
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.MarkAllAsReadForm
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.MarkPersonMentionAsReadForm
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.PersonId
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.PersonMentionResponse
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.PurgePersonForm
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.SaveUserSettingsForm
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.SaveUserSettingsResponse
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.SuccessResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Query

interface UserService {
    @GET("user")
    suspend fun getDetails(
        @Header("Authorization") authHeader: String? = null,
        @Query("auth") auth: String? = null,
        @Query("community_id") communityId: CommunityId? = null,
        @Query("person_id") personId: PersonId? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("sort") sort: CommentSortType = CommentSortType.New,
        @Query("username") username: String? = null,
        @Query("saved_only") savedOnly: Boolean? = null,
    ): Response<GetPersonDetailsResponse>

    @GET("user/mention")
    suspend fun getMentions(
        @Header("Authorization") authHeader: String? = null,
        @Query("auth") auth: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("sort") sort: CommentSortType = CommentSortType.New,
        @Query("unread_only") unreadOnly: Boolean? = null,
    ): Response<GetPersonMentionsResponse>

    @GET("user/replies")
    suspend fun getReplies(
        @Header("Authorization") authHeader: String? = null,
        @Query("auth") auth: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("sort") sort: CommentSortType = CommentSortType.New,
        @Query("unread_only") unreadOnly: Boolean? = null,
    ): Response<GetRepliesResponse>

    @POST("user/mark_all_as_read")
    @Headers("Content-Type: application/json")
    suspend fun markAllAsRead(
        @Header("Authorization") authHeader: String? = null,
        @Body form: MarkAllAsReadForm,
    ): Response<GetRepliesResponse>

    @POST("user/mention/mark_as_read")
    @Headers("Content-Type: application/json")
    suspend fun markPersonMentionAsRead(
        @Header("Authorization") authHeader: String? = null,
        @Body form: MarkPersonMentionAsReadForm,
    ): Response<PersonMentionResponse>

    @POST("user/block")
    @Headers("Content-Type: application/json")
    suspend fun block(
        @Header("Authorization") authHeader: String? = null,
        @Body form: BlockPersonForm,
    ): Response<BlockPersonResponse>

    @PUT("user/save_user_settings")
    @Headers("Content-Type: application/json")
    suspend fun saveUserSettings(
        @Header("Authorization") authHeader: String? = null,
        @Body form: SaveUserSettingsForm,
    ): Response<SaveUserSettingsResponse>

    @POST("admin/purge/person")
    @Headers("Content-Type: application/json")
    suspend fun purge(
        @Header("Authorization") authHeader: String? = null,
        @Body form: PurgePersonForm,
    ): Response<SuccessResponse>
}
