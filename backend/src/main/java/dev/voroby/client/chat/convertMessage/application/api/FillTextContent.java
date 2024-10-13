package dev.voroby.client.chat.convertMessage.application.api;

import dev.voroby.client.chat.convertMessage.dto.ConvertChatMessageContext;
import dev.voroby.client.chat.common.dto.ChatMessage;
import dev.voroby.client.chat.common.dto.TextContent;
import dev.voroby.client.chat.common.dto.TextEntity;
import dev.voroby.client.chat.common.dto.TextEntityType;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static dev.voroby.client.chat.common.dto.TextEntityType.*;
import static java.util.Collections.emptyList;
import static org.springframework.util.StringUtils.hasText;

@Component
public class FillTextContent implements Function<ConvertChatMessageContext, ConvertChatMessageContext> {

    private final String UNSUPPORTED_MESSAGE_DISCLAIMER = "Unsupported message type";

    private final Map<Integer, TextEntityType> tdApiIdentifierToTextEntityType = Map.of(
            TdApi.TextEntityTypeBlockQuote.CONSTRUCTOR, TextEntityTypeBlockQuote,
            TdApi.TextEntityTypeTextUrl.CONSTRUCTOR, TextEntityTypeTextUrl,
            TdApi.TextEntityTypeUrl.CONSTRUCTOR, TextEntityTypeUrl
    );

    @Override
    public ConvertChatMessageContext apply(ConvertChatMessageContext convertChatMessageContext) {
        TdApi.Message message = convertChatMessageContext.message();
        TdApi.MessageContent content = message.content;
        ChatMessage chatMessage = convertChatMessageContext.chatMessage()
                .withTextContent(getTextContent(content));

        return new ConvertChatMessageContext(message, chatMessage);
    }

    private TextContent getTextContent(TdApi.MessageContent content) {
        return switch (content) {
            case TdApi.MessageText msgText ->
                    new TextContent(msgText.text.text, convertTextEntities(msgText.text.entities));
            case TdApi.MessagePhoto msgPhoto ->
                    new TextContent(msgPhoto.caption.text, convertTextEntities(msgPhoto.caption.entities));
            case TdApi.MessageVideo msgVideo ->
                    new TextContent(textOrDisclaimerIfBlank(msgVideo.caption), convertTextEntities(msgVideo.caption.entities));
            case TdApi.MessageDocument msgDocument ->
                    new TextContent(textOrDisclaimerIfBlank(msgDocument.caption), convertTextEntities(msgDocument.caption.entities));
            case TdApi.MessageAnimatedEmoji emoji -> new TextContent(emoji.emoji, emptyList());
            case TdApi.MessageAnimation messageAnimation ->
                    new TextContent(messageAnimation.caption.text, convertTextEntities(messageAnimation.caption.entities));
            default -> new TextContent(UNSUPPORTED_MESSAGE_DISCLAIMER, emptyList());
        };
    }

    private Collection<TextEntity> convertTextEntities(TdApi.TextEntity[] textEntities) {
        return Arrays.stream(textEntities)
                .filter(t -> tdApiIdentifierToTextEntityType.containsKey(t.type.getConstructor()))
                .map(this::convertTextEntity)
                .toList();
    }

    private TextEntity convertTextEntity(TdApi.TextEntity t) {
        TextEntityType textEntityType = tdApiIdentifierToTextEntityType.get(t.type.getConstructor());
        return switch (textEntityType) {
            case TextEntityTypeBlockQuote -> new TextEntity.BlockQuote(t.length, t.offset);
            case TextEntityTypeTextUrl -> new TextEntity.TextUrl(t.length, t.offset, ((TdApi.TextEntityTypeTextUrl) t.type).url);
            case TextEntityTypeUrl -> new TextEntity.Url(t.length, t.offset);
        };
    }

    private String textOrDisclaimerIfBlank(TdApi.FormattedText formattedText) {
        return hasText(formattedText.text) ? formattedText.text : UNSUPPORTED_MESSAGE_DISCLAIMER;
    }
}
