package dev.voroby.client.initialActions.application;

import dev.voroby.client.cache.Caches;
import dev.voroby.springframework.telegram.TelegramRunner;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoadChatsUpdates implements TelegramRunner {

    private final TelegramClient telegramClient;

    public LoadChatsUpdates(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        var loadChatsQuery = new TdApi.LoadChats(new TdApi.ChatListMain(), 500);
        telegramClient.sendWithCallback(loadChatsQuery, this::loadChatsHandler);
    }

    public void loadChatsHandler(TdApi.Ok object, TdApi.Error error) {
        // https://core.telegram.org/tdlib/docs/classtd_1_1td__api_1_1load_chats.html
        // Returns a 404 error if all chats have been loaded.
        if (error == null) {
            var loadChatsQuery = new TdApi.LoadChats(new TdApi.ChatListMain(), 500);
            telegramClient.sendWithCallback(loadChatsQuery, this::loadChatsHandler);
        } else {
            Caches.initialApplicationLoadDone.set(true);
            log.info("Chats loaded.");
        }
    }
}
