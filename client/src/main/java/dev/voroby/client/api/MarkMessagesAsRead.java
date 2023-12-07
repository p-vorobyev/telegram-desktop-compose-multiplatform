package dev.voroby.client.api;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class MarkMessagesAsRead implements Consumer<Long> {

    private final TelegramClient telegramClient;

    public MarkMessagesAsRead(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    synchronized public void accept(Long chatId) {
        long lastReadInboxMessageId;
        long nextFromMessageId = 0;
        do {
            TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(chatId));
            lastReadInboxMessageId = chat.lastReadInboxMessageId;
            var getChatHistory = new TdApi.GetChatHistory(chatId, nextFromMessageId, 0, 100, false);
            TdApi.Messages messages = telegramClient.sendSync(getChatHistory);
            TdApi.Message[] msgs = messages.messages;
            if (msgs.length == 0) break;
            long[] ids = new long[msgs.length];
            for (int i = 0; i < msgs.length; i++) {
                ids[i] = msgs[i].id;
            }
            var viewMessages = new TdApi.ViewMessages(chatId, ids, null, true);
            telegramClient.sendSync(viewMessages);
            nextFromMessageId = ids[ids.length - 1];
        } while (nextFromMessageId != lastReadInboxMessageId);
    }

}
