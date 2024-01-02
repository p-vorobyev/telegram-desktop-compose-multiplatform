package dev.voroby.client.api;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class OpenChat implements Consumer<Long> {

    private final TelegramClient telegramClient;

    public OpenChat(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void accept(Long chatId) {
        telegramClient.sendAsync(new TdApi.OpenChat(chatId));
    }

}
