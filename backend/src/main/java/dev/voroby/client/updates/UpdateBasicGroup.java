package dev.voroby.client.updates;

import dev.voroby.client.dto.ChatGroupInfo;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;

import static dev.voroby.client.api.AbstractUpdates.groupIdToGroupInfoCache;

@Component
public class UpdateBasicGroup implements UpdateNotificationListener<TdApi.UpdateBasicGroup> {

    private final UpdatesQueues updatesQueues;

    public UpdateBasicGroup(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateBasicGroup notification) {
        TdApi.BasicGroup basicGroup = notification.basicGroup;
        if (groupIdToGroupInfoCache.containsKey(basicGroup.id)) {
            ChatGroupInfo chatGroupInfo = groupIdToGroupInfoCache.get(basicGroup.id);
            synchronized (chatGroupInfo) {
                chatGroupInfo.setBasicGroup(basicGroup);
            }
        } else {
            var chatGroupInfo = new ChatGroupInfo();
            chatGroupInfo.setBasicGroup(basicGroup);
            groupIdToGroupInfoCache.put(basicGroup.id, chatGroupInfo);
        }
        updatesQueues.addIncomingSidebarUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateBasicGroup> notificationType() {
        return TdApi.UpdateBasicGroup.class;
    }

}
