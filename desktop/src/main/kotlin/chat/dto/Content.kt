package chat.dto

import chat.dto.Content.TextEntityType.*
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

object Content {

    data class TextContent(
        val text: String,
        val entities: Collection<TextEntity>
    )

    enum class TextEntityType {
        TextEntityTypeBlockQuote,
        TextEntityTypeTextUrl,
        TextEntityTypeUrl
    }

    const val BLOCK_QUOTE: String = "TextEntityTypeBlockQuote"
    const val TEXT_URL: String = "TextEntityTypeTextUrl"
    const val URL: String = "TextEntityTypeUrl"

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes(
        JsonSubTypes.Type(value = TextEntity.BlockQuote::class, name = BLOCK_QUOTE),
        JsonSubTypes.Type(value = TextEntity.TextUrl::class, name = TEXT_URL),
        JsonSubTypes.Type(value = TextEntity.Url::class, name = URL)
    )
    sealed interface TextEntity {

        val type: TextEntityType
        val length: Int
        val offset: Int

        data class BlockQuote(
            override val length: Int,
            override val offset: Int
        ): TextEntity {

            override val type: TextEntityType = TextEntityTypeBlockQuote
        }

        data class TextUrl(
            override val length: Int,
            override val offset: Int,
            val url: String
        ): TextEntity {

            override val type: TextEntityType = TextEntityTypeTextUrl
        }

        data class Url(
            override val length: Int,
            override val offset: Int
        ): TextEntity {

            override val type: TextEntityType = TextEntityTypeUrl
        }
    }
}
