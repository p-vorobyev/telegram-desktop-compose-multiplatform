package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateChatLastMessage implements UpdateNotificationListener<TdApi.UpdateChatLastMessage> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateChatLastMessage(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatLastMessage updateChatLastMessage) {
        TdApi.Chat chat = Caches.initialChatCache.get(updateChatLastMessage.chatId);
        synchronized (chat) {
            chat.lastMessage = updateChatLastMessage.lastMessage;
        }
        chatListUpdatesQueue.addIncomingUpdate(updateChatLastMessage);
    }

    @Override
    public Class<TdApi.UpdateChatLastMessage> notificationType() {
        return TdApi.UpdateChatLastMessage.class;
    }

}
