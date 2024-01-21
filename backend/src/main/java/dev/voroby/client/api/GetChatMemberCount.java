package dev.voroby.client.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.dto.ChatGroupInfo;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetChatMemberCount extends AbstractUpdates implements Function<Long, Long> {

    protected GetChatMemberCount(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public Long apply(Long chatId) {
        Long groupId = Caches.chatIdToGroupIdCache.get(chatId);
        if (groupId != null) {
            ChatGroupInfo chatGroupInfo = Caches.groupIdToGroupInfoCache.get(groupId);
            if (chatGroupInfo.getSupergroupFullInfo() != null) {
                return (long) chatGroupInfo.getSupergroupFullInfo().memberCount;
            } else if (chatGroupInfo.getSupergroup() != null) {
                return (long) chatGroupInfo.getSupergroup().memberCount;
            } else if (chatGroupInfo.getBasicGroup() != null) {
                return (long) chatGroupInfo.getBasicGroup().memberCount;
            } else {
                return (long) chatGroupInfo.getBasicGroupFullInfo().members.length;
            }
        }

        return -1L;
    }
}
