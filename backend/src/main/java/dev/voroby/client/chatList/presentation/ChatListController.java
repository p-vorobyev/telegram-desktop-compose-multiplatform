package dev.voroby.client.chatList.presentation;

import dev.voroby.client.chatList.application.api.GetChatList;
import dev.voroby.client.chatList.dto.ChatPreview;
import dev.voroby.client.chatList.application.ChatListUpdatesService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class ChatListController {

    private final GetChatList getChatList;

    private final ChatListUpdatesService chatListUpdatesService;

    public ChatListController(GetChatList getChatList, ChatListUpdatesService chatListUpdatesService) {
        this.getChatList = getChatList;
        this.chatListUpdatesService = chatListUpdatesService;
    }

    @GetMapping(value = "/loadChats", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatPreview> loadChats() {
        return getChatList.get();
    }

    @GetMapping(value = "/chatListUpdates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatPreview> updateSidebar() {
        return chatListUpdatesService.get();
    }
}
