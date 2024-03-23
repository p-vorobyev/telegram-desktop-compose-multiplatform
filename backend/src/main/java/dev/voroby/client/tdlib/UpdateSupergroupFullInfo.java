package dev.voroby.client.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.dto.ChatGroupInfo;
import dev.voroby.client.tdlib.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;


@Component
public class UpdateSupergroupFullInfo implements UpdateNotificationListener<TdApi.UpdateSupergroupFullInfo> {
    
    private final UpdatesQueues updatesQueues;

    public UpdateSupergroupFullInfo(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
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
        updatesQueues.addIncomingSidebarUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateSupergroupFullInfo> notificationType() {
        return TdApi.UpdateSupergroupFullInfo.class;
    }
    
}
