package de.henninglanghorst.telegram

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateResponse(
    @JsonProperty("ok") val ok: Boolean,
    @JsonProperty("result") val result: List<Update>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Update(
    @JsonProperty("update_id") val updateId: Int,
    @JsonProperty("message") val message: Message?,
    @JsonProperty("edited_message") val editedMessage: Message?,
    @JsonProperty("channel_post") val channelPost: Message?,
    @JsonProperty("edited_channel_post") val editedChannelPost: Message?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Message(
    @JsonProperty("message_id") val message_id: Int,
    @JsonProperty("from") val from: User?,
    @JsonProperty("date") val date: Int,
    @JsonProperty("chat") val chat: Chat,
    @JsonProperty("forward_from") val forwardFrom: User?,
    @JsonProperty("forward_from_chat") val forwardFromChat: Chat?,
    @JsonProperty("forward_from_message_id") val forwardFromMessageId: Int?,
    @JsonProperty("forward_signature") val forwardSignature: String?,
    @JsonProperty("forward_sender_name") val forwardSenderName: String?,
    @JsonProperty("forward_date") val forwardDate: Int?,
    @JsonProperty("reply_to_message") val replyToMessage: Message?,
    @JsonProperty("via_bot") val viaBot: User?,
    @JsonProperty("edit_date") val editDate: Int?,
    @JsonProperty("media_group_id") val mediaGroupId: String?,
    @JsonProperty("author_signature") val authorSignature: String?,
    @JsonProperty("text") val text: String?,
    @JsonProperty("entities") val entities: List<MessageEntity>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageEntity(
    @JsonProperty("type") val type: String,
    @JsonProperty("offset") val offset: Int,
    @JsonProperty("length") val length: Int,
    @JsonProperty("url") val url: String?,
    @JsonProperty("user") val user: User?,
    @JsonProperty("language") val language: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    @JsonProperty("id") val id: Int,
    @JsonProperty("is_bot") val bot: Boolean,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String?,
    @JsonProperty("username") val username: String?,
    @JsonProperty("language_code") val languageCode: String?,
    @JsonProperty("can_join_groups") val canJoinGroups: Boolean?,
    @JsonProperty("can_read_all_group_messages") val canReadAllGroupMessages: Boolean?,
    @JsonProperty("supports_inline_queries") val supportsInlineQueries: Boolean?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Chat(
    @JsonProperty("id") val id: Int,
    @JsonProperty("type") val type: String,
    @JsonProperty("title") val title: String?,
    @JsonProperty("username") val username: String?,
    @JsonProperty("first_name") val firstName: String?,
    @JsonProperty("last_name") val lastName: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SendMessageRequest(
    @JsonProperty("chat_id") val chatId: String,
    @JsonProperty("text") val test: String,
    @JsonProperty("parse_mode") val parseMode: String? = null
)


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SendMessageResponse(
    @JsonProperty("ok") val ok: Boolean,
    @JsonProperty("result") val result: Message
)