package dev.voroby.client.chat.open.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.open.application.OpenChatService;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.drinkless.tdlib.TdApi;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UpdateMessageContent implements UpdateNotificationListener<TdApi.UpdateMessageContent> {

    private final OpenChatService openChatService;

    private final TelegramClient telegramClient;

    public UpdateMessageContent(OpenChatService openChatService,
                                @Lazy TelegramClient telegramClient) {
        this.openChatService = openChatService;
        this.telegramClient = telegramClient;
    }

    @Override
    public void handleNotification(TdApi.UpdateMessageContent updateMessageContent) {
        if (Caches.openedChat.get() != null &&
            updateMessageContent.chatId == Caches.openedChat.get()) {
            telegramClient.sendAsync(new TdApi.GetMessage(updateMessageContent.chatId, updateMessageContent.messageId))
                    .thenAccept(updatedMessage -> openChatService.addUpdatedMessage(updatedMessage.getObjectOrThrow()));
        }
    }

    @Override
    public Class<TdApi.UpdateMessageContent> notificationType() {
        return TdApi.UpdateMessageContent.class;
    }

}
