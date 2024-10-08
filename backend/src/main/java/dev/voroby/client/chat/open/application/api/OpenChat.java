package dev.voroby.client.chat.open.application.api;

import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class OpenChat implements Consumer<Long> {

    private final TelegramClient telegramClient;

    public OpenChat(@Lazy TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void accept(Long chatId) {
        telegramClient.sendWithCallback(new TdApi.OpenChat(chatId), (obj, error) -> {/*do nothing*/});
    }

}
