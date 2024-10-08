package dev.voroby.client.messages.delete.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.open.application.OpenChatService;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UpdateDeleteMessages implements UpdateNotificationListener<TdApi.UpdateDeleteMessages> {

    private final OpenChatService openChatService;

    public UpdateDeleteMessages(OpenChatService openChatService) {
        this.openChatService = openChatService;
    }

    @Override
    public void handleNotification(TdApi.UpdateDeleteMessages updateDeleteMessages) {
        if (updateDeleteMessages.isPermanent &&
            Caches.openedChat.get() != null &&
                Caches.openedChat.get() == updateDeleteMessages.chatId) {
            List<Long> messageIds = Arrays.stream(updateDeleteMessages.messageIds).boxed().toList();
            openChatService.addDeletedMsgIds(updateDeleteMessages.chatId, messageIds);
        }
    }

    @Override
    public Class<TdApi.UpdateDeleteMessages> notificationType() {
        return TdApi.UpdateDeleteMessages.class;
    }

}
