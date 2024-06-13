package dev.voroby.client.chat.membersCount.presentation;

import dev.voroby.client.chat.common.presentation.CommonChatController;
import dev.voroby.client.chat.membersCount.application.api.GetChatMemberCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController @Slf4j
public class MembersCountController extends CommonChatController {

    private final GetChatMemberCount getChatMemberCount;

    public MembersCountController(GetChatMemberCount getChatMemberCount) {
        this.getChatMemberCount = getChatMemberCount;
    }

    @PostMapping(value = "/members", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Long, Long> getChatsMembersCount(@RequestBody List<Long> chatIds) {
        Map<Long, Long> chatIdToMemberCount = new HashMap<>();
        chatIds.forEach(chatId -> {
            Long memberCount = getChatMemberCount.apply(chatId);
            if (memberCount != -1) {
                chatIdToMemberCount.put(chatId, memberCount);
            }
        });
        log.debug("Refresh chat member count: [chatIdToMemberCount: {}]", chatIdToMemberCount);
        return chatIdToMemberCount;
    }
}
