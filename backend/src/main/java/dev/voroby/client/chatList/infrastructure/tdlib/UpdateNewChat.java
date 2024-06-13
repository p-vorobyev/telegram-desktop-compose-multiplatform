package dev.voroby.client.chatList.infrastructure.tdlib;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatGroupInfo;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import org.springframework.stereotype.Service;

@Service
public class UpdateNewChat implements UpdateNotificationListener<TdApi.UpdateNewChat> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public UpdateNewChat(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

    @Override
    public void handleNotification(TdApi.UpdateNewChat updateNewChat) {
        long chatId = updateNewChat.chat.id;
        if (chatId != 0 && chatId != -1) {
            Caches.initialChatCache.put(updateNewChat.chat.id, updateNewChat.chat);
            Caches.mainListChatIds.add(updateNewChat.chat.id);
            if (updateNewChat.chat.type instanceof TdApi.ChatTypeBasicGroup basic) {
                cacheGroupIds(updateNewChat.chat.id, basic.basicGroupId);
            }
            if (updateNewChat.chat.type instanceof TdApi.ChatTypeSupergroup supergroup) {
                cacheGroupIds(updateNewChat.chat.id, supergroup.supergroupId);
            }
            chatListUpdatesQueue.addIncomingUpdate(updateNewChat);
        }
    }

    private void cacheGroupIds(long chatId, long groupId) {
        Caches.chatIdToGroupIdCache.put(chatId, groupId);
        Caches.groupIdToGroupInfoCache.compute(groupId, (groupId1, chatGroupInfo) -> {
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
