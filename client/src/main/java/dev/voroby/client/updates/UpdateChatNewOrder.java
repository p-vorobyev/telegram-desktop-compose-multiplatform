package dev.voroby.client.updates;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatNewOrder implements UpdateNotificationListener<TdApi.UpdateChatPosition> {

    private final UpdatesQueues updatesQueues;

    public UpdateChatNewOrder(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatPosition notification) {
        if (notification.position.list instanceof TdApi.ChatListMain) {
            //updatesQueues.addUpdateChatPosition(notification);
            updatesQueues.addIncomingSidebarUpdate(notification);
        }
    }

    @Override
    public Class<TdApi.UpdateChatPosition> notificationType() {
        return TdApi.UpdateChatPosition.class;
    }
}
