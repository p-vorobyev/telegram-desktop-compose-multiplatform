package dev.voroby.client.chat.open.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.open.application.OpenChatService;
import org.drinkless.tdlib.TdApi;
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
        if (Caches.openedChat.get() != null &&
            updateNewMessage.message.chatId == Caches.openedChat.get()) {
            openChatService.addIncomingMessage(updateNewMessage.message);
        }
    }

    @Override
    public Class<TdApi.UpdateNewMessage> notificationType() {
        return TdApi.UpdateNewMessage.class;
    }

}
