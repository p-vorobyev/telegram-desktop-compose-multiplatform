package dev.voroby.client.updates;

import dev.voroby.client.dto.ChatGroupInfo;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

import static dev.voroby.client.api.AbstractUpdates.groupIdToGroupInfoCache;

@Component
public class UpdateBasicGroupFullInfo implements UpdateNotificationListener<TdApi.UpdateBasicGroupFullInfo> {

    private final UpdatesQueues updatesQueues;

    public UpdateBasicGroupFullInfo(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateBasicGroupFullInfo notification) {
        long basicGroupId = notification.basicGroupId;
        TdApi.BasicGroupFullInfo basicGroupFullInfo = notification.basicGroupFullInfo;
        if (groupIdToGroupInfoCache.containsKey(basicGroupId)) {
            ChatGroupInfo chatGroupInfo = groupIdToGroupInfoCache.get(basicGroupId);
            synchronized (chatGroupInfo) {
                chatGroupInfo.setBasicGroupFullInfo(basicGroupFullInfo);
            }
        } else {
            var chatGroupInfo = new ChatGroupInfo();
            chatGroupInfo.setBasicGroupFullInfo(basicGroupFullInfo);
            groupIdToGroupInfoCache.put(basicGroupId, chatGroupInfo);
        }
        updatesQueues.addIncomingSidebarUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateBasicGroupFullInfo> notificationType() {
        return TdApi.UpdateBasicGroupFullInfo.class;
    }

}
