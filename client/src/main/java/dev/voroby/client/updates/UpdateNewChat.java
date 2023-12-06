package dev.voroby.client.updates;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateNewChat implements UpdateNotificationListener<TdApi.UpdateNewChat> {

    private final UpdatesQueues updatesQueues;

    public UpdateNewChat(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateNewChat updateNewChat) {
        updatesQueues.addIncomingSidebarUpdate(updateNewChat);
    }

    @Override
    public Class<TdApi.UpdateNewChat> notificationType() {
        return TdApi.UpdateNewChat.class;
    }
}
