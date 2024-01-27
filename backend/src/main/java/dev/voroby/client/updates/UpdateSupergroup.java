package dev.voroby.client.updates;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.dto.ChatGroupInfo;
import dev.voroby.client.updates.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Component;


@Component
public class UpdateSupergroup implements UpdateNotificationListener<TdApi.UpdateSupergroup> {

    private final UpdatesQueues updatesQueues;

    public UpdateSupergroup(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
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
        updatesQueues.addIncomingSidebarUpdate(notification);
    }

    @Override
    public Class<TdApi.UpdateSupergroup> notificationType() {
        return TdApi.UpdateSupergroup.class;
    }
    
}
