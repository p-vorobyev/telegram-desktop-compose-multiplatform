package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UpdateChatPosition implements UpdateNotificationListener<TdApi.UpdateChatPosition> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateChatPosition(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateChatPosition notification) {
        if (notification.position.list instanceof TdApi.ChatListMain) {
            Caches.mainListChatIds.add(notification.chatId);
            TdApi.Chat chat = Caches.initialChatCache.get(notification.chatId);
            synchronized (chat) {
                if (chat.positions.length == 0) {
                    chat.positions = new TdApi.ChatPosition[] {notification.position};
                } else {
                    boolean newPosition = true;
                    for (TdApi.ChatPosition chatPosition: chat.positions) {
                        if (notification.position.getConstructor() == chatPosition.getConstructor()) {
                            chatPosition.list = notification.position.list;
                            chatPosition.order = notification.position.order;
                            chatPosition.source = notification.position.source;
                            chatPosition.isPinned = notification.position.isPinned;
                            newPosition = false;
                        }
                    }
                    if (newPosition) {
                        TdApi.ChatPosition[] increasedChatPositions = Arrays.copyOf(chat.positions, chat.positions.length + 1);
                        increasedChatPositions[increasedChatPositions.length - 1] = notification.position;
                        chat.positions = increasedChatPositions;
                    }
                }
            }
            chatListUpdatesQueue.addIncomingUpdate(notification);
        }
    }

    @Override
    public Class<TdApi.UpdateChatPosition> notificationType() {
        return TdApi.UpdateChatPosition.class;
    }
}
