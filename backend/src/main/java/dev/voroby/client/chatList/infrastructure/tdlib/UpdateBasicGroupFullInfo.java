package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatGroupInfo;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;


@Component
public class UpdateBasicGroupFullInfo implements UpdateNotificationListener<TdApi.UpdateBasicGroupFullInfo> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateBasicGroupFullInfo(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateBasicGroupFullInfo notification) {
        long basicGroupId = notification.basicGroupId;
        TdApi.BasicGroupFullInfo basicGroupFullInfo = notification.basicGroupFullInfo;
        if (Caches.groupIdToGroupInfoCache.containsKey(basicGroupId)) {
            ChatGroupInfo chatGroupInfo = Caches.groupIdToGroupInfoCache.get(basicGroupId);
            synchronized (chatGroupInfo) {
                chatGroupInfo.setBasicGroupFullInfo(basicGroupFullInfo);
            }
        } else {
            var chatGroupInfo = new ChatGroupInfo();
            chatGroupInfo.setBasicGroupFullInfo(basicGroupFullInfo);
            Caches.groupIdToGroupInfoCache.put(basicGroupId, chatGroupInfo);
        }
        chatListUpdatesQueue.addIncomingUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateBasicGroupFullInfo> notificationType() {
        return TdApi.UpdateBasicGroupFullInfo.class;
    }

}
