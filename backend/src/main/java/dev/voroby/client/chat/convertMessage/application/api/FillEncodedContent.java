package dev.voroby.client.chat.convertMessage.application.api;

import dev.voroby.client.chat.common.dto.ChatMessage;
import dev.voroby.client.chat.common.dto.EncodedContent.Photo;
import dev.voroby.client.chat.common.dto.MessagePhotoInfo;
import dev.voroby.client.chat.convertMessage.dto.ConvertChatMessageContext;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class FillEncodedContent implements Function<ConvertChatMessageContext, ConvertChatMessageContext> {

    private final GetPhotoPreview getPhotoPreview;

    public FillEncodedContent(GetPhotoPreview getPhotoPreview) {
        this.getPhotoPreview = getPhotoPreview;
    }

    @Override
    public ConvertChatMessageContext apply(ConvertChatMessageContext convertChatMessageContext) {
        TdApi.Message message = convertChatMessageContext.message();
        if (message.content instanceof TdApi.MessagePhoto msgPhoto) {
            ChatMessage chatMessage = chatMessageWithPhotoEncoded(convertChatMessageContext, msgPhoto, message);
            return new ConvertChatMessageContext(message, chatMessage);
        } else return convertChatMessageContext;
    }

    private ChatMessage chatMessageWithPhotoEncoded(ConvertChatMessageContext convertChatMessageContext,
                                                    TdApi.MessagePhoto messagePhoto,
                                                    TdApi.Message message) {
        var messagePhotoInfo = new MessagePhotoInfo(message.id, message.chatId, messagePhoto);
        Photo photoPreview = getPhotoPreview.apply(messagePhotoInfo);
        return convertChatMessageContext.chatMessage().withEncodedContent(photoPreview);
    }
}
