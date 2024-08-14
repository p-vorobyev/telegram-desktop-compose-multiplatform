package dev.voroby.client.chat.convertChatMessage.application;

import dev.voroby.client.chat.convertChatMessage.application.api.FillMetaInfo;
import dev.voroby.client.chat.convertChatMessage.application.api.FillPhotoContent;
import dev.voroby.client.chat.convertChatMessage.application.api.FillSenderInfo;
import dev.voroby.client.chat.convertChatMessage.application.api.FillTextContent;
import dev.voroby.client.chat.convertChatMessage.dto.ConvertChatMessageContext;
import dev.voroby.client.chat.common.dto.ChatMessage;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ChatMessageConverter implements Function<TdApi.Message, ChatMessage> {

    private final FillMetaInfo fillMetaInfo;

    private final FillSenderInfo fillSenderInfo;

    private final FillTextContent fillTextContent;

    private final FillPhotoContent fillPhotoContent;

    public ChatMessageConverter(FillMetaInfo fillMetaInfo,
                                FillSenderInfo fillSenderInfo,
                                FillTextContent fillTextContent,
                                FillPhotoContent fillPhotoContent) {
        this.fillMetaInfo = fillMetaInfo;
        this.fillSenderInfo = fillSenderInfo;
        this.fillTextContent = fillTextContent;
        this.fillPhotoContent = fillPhotoContent;
    }

    @Override
    public ChatMessage apply(TdApi.Message message) {
        return fillMetaInfo
                .andThen(fillSenderInfo)
                .andThen(fillPhotoContent)
                .andThen(fillTextContent)
                .apply(new ConvertChatMessageContext(message, new ChatMessage()))
                .chatMessage();
    }
}
