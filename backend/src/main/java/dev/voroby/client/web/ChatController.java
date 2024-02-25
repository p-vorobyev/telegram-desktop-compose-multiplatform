package dev.voroby.client.web;

import dev.voroby.client.api.*;
import dev.voroby.client.api.service.OpenChatService;
import dev.voroby.client.dto.ChatHistoryRequest;
import dev.voroby.client.dto.ChatMessage;
import dev.voroby.springframework.telegram.client.TdApi;
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

    @Autowired
    private GetChatHistory getChatHistory;

    @Autowired
    private ConvertToChatMessages convertToChatMessages;

    @PostMapping(value = "/markasread/{chatId}")
    public void markMessagesAsRead(@PathVariable("chatId") long chatId) {
        markMessagesAsRead.accept(chatId);
    }

    @PostMapping(value = "/delete/{chatId}")
    public void deleteChat(@PathVariable("chatId") long chatId) {
        deleteChat.accept(chatId);
    }

    @PostMapping(value = "/open/{chatId}")
    public List<ChatMessage> openChat(@PathVariable("chatId") long chatId) {
        openChatService.accept(chatId);
        return getChatHistory
                .andThen(convertToChatMessages)
                .apply(new ChatHistoryRequest(chatId, 0, 0));
    }

    @GetMapping(value = "/incoming")
    public List<ChatMessage> getIncomingMessages() {
        log.info("Call incoming");
        List<TdApi.Message> messages = openChatService.getIncomingMessages();
        return convertToChatMessages.apply(messages);
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
