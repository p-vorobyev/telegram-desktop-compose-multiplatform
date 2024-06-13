package dev.voroby.client.chat.delete.application.api;

import dev.voroby.client.util.Utils;
import dev.voroby.springframework.telegram.client.QueryResultHandler;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static dev.voroby.client.util.Utils.objectOrThrow;

@Component @Slf4j
public class DeleteChat implements Consumer<Long> {

    private final TelegramClient telegramClient;

    public DeleteChat(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void accept(Long chatId) {
        telegramClient.sendAsync(new TdApi.GetChat(chatId))
                .thenAccept(chat -> {
                    TdApi.ChatType type = objectOrThrow(chat).type;
                    if (type instanceof TdApi.ChatTypePrivate || type instanceof TdApi.ChatTypeSecret) {
                        telegramClient.sendWithCallback(new TdApi.DeleteChat(chatId), deleteChatHandler(chatId));
                    } else {
                        telegramClient.sendWithCallback(new TdApi.LeaveChat(chatId), deleteChatHandler(chatId));
                    }
                });
    }

    private QueryResultHandler<TdApi.Ok> deleteChatHandler(Long chatId) {
        return (obj, error) -> {
            if (error == null) {
                log.info("Chat deleted: [chatId: {}]", chatId);
            } else {
                Utils.logError(error);
            }
        };
    }
}
