package dev.voroby.client.web;

import dev.voroby.client.api.DeleteChat;
import dev.voroby.client.api.GetChatMemberCount;
import dev.voroby.client.api.service.GetSidebarUpdates;
import dev.voroby.client.api.LoadChats;
import dev.voroby.client.api.MarkMessagesAsRead;
import dev.voroby.client.api.service.OpenChatService;
import dev.voroby.client.dto.ChatPreview;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController @Slf4j
@RequestMapping(value = "/client")
public class ClientController {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private LoadChats loadChats;

    @Autowired
    private TelegramClient telegramClient;

    @Autowired
    private MarkMessagesAsRead markMessagesAsRead;

    @Autowired
    private DeleteChat deleteChat;

    @Autowired
    private GetSidebarUpdates getSidebarUpdates;

    @Autowired
    private OpenChatService openChatService;

    @Autowired
    private GetChatMemberCount getChatMemberCount;

    @GetMapping(value = "/loadChats", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatPreview> loadChats() {
        return loadChats.get();
    }

    @GetMapping(value = "/chatsLoaded")
    public boolean chatsLoaded() {
        return loadChats.chatsLoaded();
    }

    @GetMapping(value = "/updateSidebar", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatPreview> updateSidebar() {
        return getSidebarUpdates.get();
    }

    @PostMapping(value = "/chat/markasread/{chatId}")
    public void markMessagesAsRead(@PathVariable("chatId") long chatId) {
        markMessagesAsRead.accept(chatId);
    }

    @PostMapping(value = "/chat/delete/{chatId}")
    public void deleteChat(@PathVariable("chatId") long chatId) {
        deleteChat.accept(chatId);
    }

    @PostMapping(value = "/chat/open/{chatId}")
    public void openChat(@PathVariable("chatId") long chatId) {
        openChatService.openChat(chatId);
    }

    @PostMapping(value = "/shutdown")
    public void shutDownApp() {
        CompletableFuture.runAsync(() -> SpringApplication.exit(ctx, () -> 0));
    }

    @GetMapping(value = "/chat/members/{chatId}")
    public Long getChatMemberCount(@PathVariable("chatId") long chatId) {
        Long memberCount = getChatMemberCount.apply(chatId);
        log.info("Get chat member count: [chatId: {}, count: {}]", chatId, memberCount);
        return memberCount;
    }

    @PostMapping(value = "/chat/members", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
