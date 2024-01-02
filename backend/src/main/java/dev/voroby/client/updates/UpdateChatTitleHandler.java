package dev.voroby.client.updates;

import dev.voroby.client.api.AbstractUpdates;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatTitleHandler implements UpdateNotificationListener<TdApi.UpdateChatTitle> {

    private final UpdatesQueues updatesQueues;

    public UpdateChatTitleHandler(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatTitle updateChatTitle) {
        TdApi.Chat chat = AbstractUpdates.initialChatCache.get(updateChatTitle.chatId);
        synchronized (chat) {
            chat.title = updateChatTitle.title;
        }
        updatesQueues.addIncomingSidebarUpdate(updateChatTitle);
    }

    @Override
    public Class<TdApi.UpdateChatTitle> notificationType() {
        return TdApi.UpdateChatTitle.class;
    }

}
