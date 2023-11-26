package dev.voroby.client.updates;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatPhoto implements UpdateNotificationListener<TdApi.UpdateChatPhoto> {

    private final UpdatesQueues updatesQueues;

    public UpdateChatPhoto(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatPhoto updateChatPhoto) {
        //updatesQueues.addUpdateChatPhoto(updateChatPhoto);
        updatesQueues.addIncomingSidebarUpdate(updateChatPhoto);
    }

    @Override
    public Class<TdApi.UpdateChatPhoto> notificationType() {
        return TdApi.UpdateChatPhoto.class;
    }
}
