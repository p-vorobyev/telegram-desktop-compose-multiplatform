package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatGroupInfo;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;


@Component
public class UpdateBasicGroup implements UpdateNotificationListener<TdApi.UpdateBasicGroup> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateBasicGroup(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateBasicGroup notification) {
        TdApi.BasicGroup basicGroup = notification.basicGroup;
        if (Caches.groupIdToGroupInfoCache.containsKey(basicGroup.id)) {
            ChatGroupInfo chatGroupInfo = Caches.groupIdToGroupInfoCache.get(basicGroup.id);
            synchronized (chatGroupInfo) {
                chatGroupInfo.setBasicGroup(basicGroup);
            }
        } else {
            var chatGroupInfo = new ChatGroupInfo();
            chatGroupInfo.setBasicGroup(basicGroup);
            Caches.groupIdToGroupInfoCache.put(basicGroup.id, chatGroupInfo);
        }
        chatListUpdatesQueue.addIncomingUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateBasicGroup> notificationType() {
        return TdApi.UpdateBasicGroup.class;
    }

}
