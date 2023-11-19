package dev.voroby.client.updates;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatReadInbox  implements UpdateNotificationListener<TdApi.UpdateChatReadInbox> {

    private final UpdatesQueues updatesQueues;

    public UpdateChatReadInbox(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatReadInbox notification) {
        updatesQueues.addUpdateChatReadInbox(notification);
    }

    @Override
    public Class<TdApi.UpdateChatReadInbox> notificationType() {
        return TdApi.UpdateChatReadInbox.class;
    }

}
