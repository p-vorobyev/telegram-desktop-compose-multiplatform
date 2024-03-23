package dev.voroby.client.updates;

import dev.voroby.client.service.OpenChatService;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
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
        if (openChatService.openedChat() != null &&
            updateMessageContent.chatId == openChatService.openedChat()) {
            telegramClient.sendAsync(new TdApi.GetMessage(updateMessageContent.chatId, updateMessageContent.messageId))
                    .thenAccept(openChatService::addUpdatedMessage);
        }
    }

    @Override
    public Class<TdApi.UpdateMessageContent> notificationType() {
        return TdApi.UpdateMessageContent.class;
    }

}
