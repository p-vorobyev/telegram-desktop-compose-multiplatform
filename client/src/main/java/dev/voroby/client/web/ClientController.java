package dev.voroby.client.web;

import dev.voroby.client.api.DeleteChat;
import dev.voroby.client.api.GetSidebarUpdates;
import dev.voroby.client.api.LoadChats;
import dev.voroby.client.api.MarkMessagesAsRead;
import dev.voroby.client.dto.ChatPreview;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @Slf4j
@RequestMapping(value = "/client")
public class ClientController {

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

    @PostMapping(value = "/markasread/{chatId}")
    public void markMessagesAsRead(@PathVariable("chatId") long chatId) {
        markMessagesAsRead.accept(chatId);
    }

    @PostMapping(value = "/delete/{chatId}")
    public void deleteChat(@PathVariable("chatId") long chatId) {
        deleteChat.accept(chatId);
    }

}
