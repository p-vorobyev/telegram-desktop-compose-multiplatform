package dev.voroby.client.runners;

import dev.voroby.client.api.LoadChats;
import dev.voroby.springframework.telegram.TelegramRunner;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatsLoader implements TelegramRunner {

    private final TelegramClient telegramClient;

    private final LoadChats loadChats;

    public ChatsLoader(TelegramClient telegramClient, LoadChats loadChats) {
        this.telegramClient = telegramClient;
        this.loadChats = loadChats;
    }

    @Override
    public void run(ApplicationArguments args) {
        TdApi.LoadChats loadChatsQuery = new TdApi.LoadChats(new TdApi.ChatListMain(), 500);
        telegramClient.sendWithCallback(loadChatsQuery, this::loadChatsHandler);
    }

    public void loadChatsHandler(TdApi.Ok object, TdApi.Error error) {
        // https://core.telegram.org/tdlib/docs/classtd_1_1td__api_1_1load_chats.html
        // Returns a 404 error if all chats have been loaded.
        if (error == null) {
            TdApi.LoadChats loadChatsQuery = new TdApi.LoadChats(new TdApi.ChatListMain(), 500);
            telegramClient.sendWithCallback(loadChatsQuery, this::loadChatsHandler);
        } else {
            loadChats.setInitialLoadDone();
            log.info("Chats loaded.");
        }
    }

}
