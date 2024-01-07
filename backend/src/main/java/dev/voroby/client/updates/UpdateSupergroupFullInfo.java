package dev.voroby.client.updates;

import dev.voroby.client.dto.ChatGroupInfo;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

import static dev.voroby.client.api.AbstractUpdates.groupIdToGroupInfoCache;

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
        if (groupIdToGroupInfoCache.containsKey(supergroupId)) {
            ChatGroupInfo chatGroupInfo = groupIdToGroupInfoCache.get(supergroupId);
            synchronized (chatGroupInfo) {
                chatGroupInfo.setSupergroupFullInfo(supergroupFullInfo);
            }
        } else {
            var chatGroupInfo = new ChatGroupInfo();
            chatGroupInfo.setSupergroupFullInfo(supergroupFullInfo);
            groupIdToGroupInfoCache.put(supergroupId, chatGroupInfo);
        }
        updatesQueues.addIncomingSidebarUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateSupergroupFullInfo> notificationType() {
        return TdApi.UpdateSupergroupFullInfo.class;
    }
    
}
