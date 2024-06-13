package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatGroupInfo;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;


@Component
public class UpdateSupergroupFullInfo implements UpdateNotificationListener<TdApi.UpdateSupergroupFullInfo> {
    
    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateSupergroupFullInfo(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateSupergroupFullInfo notification) {
        long supergroupId = notification.supergroupId;
        TdApi.SupergroupFullInfo supergroupFullInfo = notification.supergroupFullInfo;
        if (Caches.groupIdToGroupInfoCache.containsKey(supergroupId)) {
            ChatGroupInfo chatGroupInfo = Caches.groupIdToGroupInfoCache.get(supergroupId);
            synchronized (chatGroupInfo) {
                chatGroupInfo.setSupergroupFullInfo(supergroupFullInfo);
            }
        } else {
            var chatGroupInfo = new ChatGroupInfo();
            chatGroupInfo.setSupergroupFullInfo(supergroupFullInfo);
            Caches.groupIdToGroupInfoCache.put(supergroupId, chatGroupInfo);
        }
        chatListUpdatesQueue.addIncomingUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateSupergroupFullInfo> notificationType() {
        return TdApi.UpdateSupergroupFullInfo.class;
    }
    
}
