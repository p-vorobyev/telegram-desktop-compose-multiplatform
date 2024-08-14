package dev.voroby.client.chat.convertChatMessage.application.api;

import dev.voroby.client.chat.common.application.GetPhotoPreviewService;
import dev.voroby.client.chat.common.dto.MessagePhotoInfo;
import dev.voroby.client.chat.convertChatMessage.dto.ConvertChatMessageContext;
import dev.voroby.client.chat.common.dto.ChatMessage;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class FillPhotoContent implements Function<ConvertChatMessageContext, ConvertChatMessageContext> {

    private final GetPhotoPreviewService getPhotoPreviewService;

    public FillPhotoContent(GetPhotoPreviewService getPhotoPreviewService) {
        this.getPhotoPreviewService = getPhotoPreviewService;
    }

    @Override
    public ConvertChatMessageContext apply(ConvertChatMessageContext convertChatMessageContext) {
        TdApi.Message message = convertChatMessageContext.message();
        String photoPreview = null;
        if (message.content instanceof TdApi.MessagePhoto messagePhoto) {
            var messagePhotoInfo = new MessagePhotoInfo(message.id, message.chatId, messagePhoto);
            photoPreview = getPhotoPreviewService.apply(messagePhotoInfo);
        }
        ChatMessage chatMessage = convertChatMessageContext.chatMessage()
                .withPhotoPreview(photoPreview);

        return new ConvertChatMessageContext(message, chatMessage);
    }
}
