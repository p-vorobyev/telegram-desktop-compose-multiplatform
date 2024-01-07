package dev.voroby.client.updates;

import dev.voroby.client.api.AbstractUpdates;
import dev.voroby.client.dto.ChatGroupInfo;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateNewChat implements UpdateNotificationListener<TdApi.UpdateNewChat> {

    private final UpdatesQueues updatesQueues;

    public UpdateNewChat(UpdatesQueues updatesQueues) {
        this.updatesQueues = updatesQueues;
    }

    @Override
    public void handleNotification(TdApi.UpdateNewChat updateNewChat) {
        long chatId = updateNewChat.chat.id;
        if (chatId != 0 && chatId != -1) {
            AbstractUpdates.initialChatCache.put(updateNewChat.chat.id, updateNewChat.chat);
            AbstractUpdates.mainListChatIds.add(updateNewChat.chat.id);
            if (updateNewChat.chat.type instanceof TdApi.ChatTypeBasicGroup basic) {
                cacheGroupIds(updateNewChat.chat.id, basic.basicGroupId);
            }
            if (updateNewChat.chat.type instanceof TdApi.ChatTypeSupergroup supergroup) {
                cacheGroupIds(updateNewChat.chat.id, supergroup.supergroupId);
            }
            updatesQueues.addIncomingSidebarUpdate(updateNewChat);
        }
    }

    private void cacheGroupIds(long chatId, long groupId) {
        AbstractUpdates.chatIdToGroupIdCache.put(chatId, groupId);
        AbstractUpdates.groupIdToGroupInfoCache.compute(groupId, (groupId1, chatGroupInfo) -> {
            if (chatGroupInfo == null) {
                chatGroupInfo = new ChatGroupInfo();
            }
            chatGroupInfo.setChatId(chatId);
            return chatGroupInfo;
        });
    }

    @Override
    public Class<TdApi.UpdateNewChat> notificationType() {
        return TdApi.UpdateNewChat.class;
    }
}
