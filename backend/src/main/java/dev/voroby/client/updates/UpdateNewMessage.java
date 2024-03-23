package dev.voroby.client.updates;

import dev.voroby.client.service.OpenChatService;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

@Component
public class UpdateNewMessage implements UpdateNotificationListener<TdApi.UpdateNewMessage> {

    private final OpenChatService openChatService;

    public UpdateNewMessage(OpenChatService openChatService) {
        this.openChatService = openChatService;
    }

    @Override
    public void handleNotification(TdApi.UpdateNewMessage updateNewMessage) {
        if (openChatService.openedChat() != null &&
            updateNewMessage.message.chatId == openChatService.openedChat()) {
            openChatService.addIncomingMessage(updateNewMessage.message);
        }
    }

    @Override
    public Class<TdApi.UpdateNewMessage> notificationType() {
        return TdApi.UpdateNewMessage.class;
    }

}
