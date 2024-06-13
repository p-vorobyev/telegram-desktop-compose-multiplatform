package dev.voroby.client.chat.membersCount.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatGroupInfo;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetChatMemberCount implements Function<Long, Long> {

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
