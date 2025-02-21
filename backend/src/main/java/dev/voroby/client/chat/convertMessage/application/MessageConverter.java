package dev.voroby.client.chat.convertMessage.application;

import dev.voroby.client.chat.common.dto.ChatMessage;
import dev.voroby.client.chat.convertMessage.application.api.*;
import dev.voroby.client.chat.convertMessage.dto.ConvertChatMessageContext;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MessageConverter implements Function<TdApi.Message, ChatMessage> {

    private final FillMetaInfo fillMetaInfo;

    private final FillSenderInfo fillSenderInfo;

    private final FillTextContent fillTextContent;

    private final FillEncodedContent fillEncodedContent;

    private final FillUrlContent fillUrlContent;

    public MessageConverter(FillMetaInfo fillMetaInfo,
                            FillSenderInfo fillSenderInfo,
                            FillTextContent fillTextContent,
                            FillEncodedContent fillEncodedContent,
                            FillUrlContent fillUrlContent) {
        this.fillMetaInfo = fillMetaInfo;
        this.fillSenderInfo = fillSenderInfo;
        this.fillTextContent = fillTextContent;
        this.fillEncodedContent = fillEncodedContent;
        this.fillUrlContent = fillUrlContent;
    }

    @Override
    public ChatMessage apply(TdApi.Message message) {
        return fillMetaInfo
                .andThen(fillSenderInfo)
                .andThen(fillEncodedContent)
                .andThen(fillTextContent)
                .andThen(fillUrlContent)
                .apply(new ConvertChatMessageContext(message, new ChatMessage()))
                .chatMessage();
    }
}
