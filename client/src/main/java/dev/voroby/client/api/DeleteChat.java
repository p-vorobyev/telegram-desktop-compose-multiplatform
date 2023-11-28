package dev.voroby.client.api;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class DeleteChat implements Consumer<Long> {

    private final TelegramClient telegramClient;

    public DeleteChat(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void accept(Long chatId) {
        telegramClient.sendAsync(new TdApi.GetChat(chatId))
                .thenAccept(chat -> {
                    TdApi.ChatType type = chat.type;
                    if (type instanceof TdApi.ChatTypePrivate || type instanceof TdApi.ChatTypeSecret) {
                        telegramClient.sendAsync(new TdApi.DeleteChat(chatId));
                    } else {
                        telegramClient.sendAsync(new TdApi.LeaveChat(chatId));
                    }
                });
    }
}
