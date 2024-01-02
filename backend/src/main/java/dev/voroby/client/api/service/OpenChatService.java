package dev.voroby.client.api.service;

import dev.voroby.client.api.OpenChat;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component @Slf4j
public class OpenChatService {

    private final TelegramClient telegramClient;

    private final OpenChat openChatFunc;

    private final AtomicReference<Long> openedChat = new AtomicReference<>(null);

    public OpenChatService(TelegramClient telegramClient,
                           OpenChat openChat) {
        this.telegramClient = telegramClient;
        this.openChatFunc = openChat;
    }

    synchronized public void openChat(long chatId) {
        closeCurrentChatIfOpened();
        openChatFunc.accept(chatId);
        openedChat.set(chatId);
        log.info("Chat opened: [chatId: {}]", chatId);
    }

    private void closeCurrentChatIfOpened() {
        Long chatId = openedChat.get();
        if (chatId != null) {
            telegramClient.sendSync(new TdApi.CloseChat(chatId));
            log.info("Chat closed: [chatId: {}]", chatId);
        }
    }

    @PreDestroy
    void onDestroy() {
        closeCurrentChatIfOpened();
    }

}
