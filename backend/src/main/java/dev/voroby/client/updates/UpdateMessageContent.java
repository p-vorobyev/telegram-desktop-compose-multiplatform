package dev.voroby.client.updates;

import dev.voroby.client.api.service.OpenChatService;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

@Component
public class UpdateMessageContent implements UpdateNotificationListener<TdApi.UpdateMessageContent> {

    private final OpenChatService openChatService;

    public UpdateMessageContent(OpenChatService openChatService) {
        this.openChatService = openChatService;
    }

    @Override
    public void handleNotification(TdApi.UpdateMessageContent updateMessageContent) {
        if (openChatService.openedChat() != null &&
            updateMessageContent.chatId == openChatService.openedChat()) {
            openChatService.addUpdatedContent(updateMessageContent);
        }
    }

    @Override
    public Class<TdApi.UpdateMessageContent> notificationType() {
        return TdApi.UpdateMessageContent.class;
    }

}