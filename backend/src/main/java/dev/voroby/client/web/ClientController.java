package dev.voroby.client.web;

import dev.voroby.client.api.LoadChats;
import dev.voroby.client.api.service.GetSidebarUpdates;
import dev.voroby.client.dto.ChatPreview;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

    @PostMapping(value = "/shutdown")
    public void shutDownApp() {
        CompletableFuture.runAsync(() -> SpringApplication.exit(ctx, () -> 0));
    }

}
