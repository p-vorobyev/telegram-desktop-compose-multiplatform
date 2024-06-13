package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatGroupInfo;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;


@Component
public class UpdateSupergroup implements UpdateNotificationListener<TdApi.UpdateSupergroup> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateSupergroup(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateSupergroup notification) {
        TdApi.Supergroup supergroup = notification.supergroup;
        if (Caches.groupIdToGroupInfoCache.containsKey(supergroup.id)) {
            ChatGroupInfo chatGroupInfo = Caches.groupIdToGroupInfoCache.get(supergroup.id);
            synchronized (chatGroupInfo) {
                chatGroupInfo.setSupergroup(supergroup);
            }
        } else {
            var chatGroupInfo = new ChatGroupInfo();
            chatGroupInfo.setSupergroup(supergroup);
            Caches.groupIdToGroupInfoCache.put(supergroup.id, chatGroupInfo);
        }
        chatListUpdatesQueue.addIncomingUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateSupergroup> notificationType() {
        return TdApi.UpdateSupergroup.class;
    }
    
}
