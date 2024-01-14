package dev.voroby.client.web;

import dev.voroby.client.api.DeleteChat;
import dev.voroby.client.api.GetChatMemberCount;
import dev.voroby.client.api.MarkMessagesAsRead;
import dev.voroby.client.api.service.OpenChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController @Slf4j
@RequestMapping(value = "/client/chat")
public class ChatController {

    @Autowired
    private MarkMessagesAsRead markMessagesAsRead;

    @Autowired
    private DeleteChat deleteChat;

    @Autowired
    private OpenChatService openChatService;

    @Autowired
    private GetChatMemberCount getChatMemberCount;

    @PostMapping(value = "/markasread/{chatId}")
    public void markMessagesAsRead(@PathVariable("chatId") long chatId) {
        markMessagesAsRead.accept(chatId);
    }

    @PostMapping(value = "/delete/{chatId}")
    public void deleteChat(@PathVariable("chatId") long chatId) {
        deleteChat.accept(chatId);
    }

    @PostMapping(value = "/open/{chatId}")
    public void openChat(@PathVariable("chatId") long chatId) {
        openChatService.openChat(chatId);
    }

    @GetMapping(value = "/members/{chatId}")
    public Long getChatMemberCount(@PathVariable("chatId") long chatId) {
        Long memberCount = getChatMemberCount.apply(chatId);
        log.info("Get chat member count: [chatId: {}, count: {}]", chatId, memberCount);
        return memberCount;
    }

    @PostMapping(value = "/members", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Long, Long> getChatMembersCount(@RequestBody List<Long> chatIds) {
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
