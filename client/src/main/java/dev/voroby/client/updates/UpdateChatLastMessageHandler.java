package dev.voroby.client.updates;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatLastMessageHandler implements UpdateNotificationListener<TdApi.UpdateChatLastMessage> {

    private final UpdatesQueues updatesQueues;

    public UpdateChatLastMessageHandler(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatLastMessage updateChatLastMessage) {
        updatesQueues.addChatLastMessage(updateChatLastMessage);
    }

    @Override
    public Class<TdApi.UpdateChatLastMessage> notificationType() {
        return TdApi.UpdateChatLastMessage.class;
    }

}
