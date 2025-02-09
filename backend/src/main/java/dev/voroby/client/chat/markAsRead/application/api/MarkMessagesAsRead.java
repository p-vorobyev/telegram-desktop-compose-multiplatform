package dev.voroby.client.chat.markAsRead.application.api;

import dev.voroby.client.cache.Caches;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component @Slf4j
public class MarkMessagesAsRead implements Consumer<Long> {

    private final TelegramClient telegramClient;

    public MarkMessagesAsRead(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    synchronized public void accept(Long chatId) {
        telegramClient.send(new TdApi.OpenChat(chatId)).onSuccess(ok -> markAsRead(chatId));
    }

    private void markAsRead(long chatId) {
        try {
            long lastReadInboxMessageId;
            long fromMessageId = 0;
            do {
                Optional<TdApi.Chat> chatOptional = telegramClient.send(new TdApi.GetChat(chatId)).getObject();
                if (chatOptional.isPresent()) {
                    TdApi.Chat chat = chatOptional.get();
                    lastReadInboxMessageId = chat.lastReadInboxMessageId;
                    var getChatHistory = new TdApi.GetChatHistory(chatId, fromMessageId, 0, 100, true);
                    Optional<TdApi.Messages> messagesOptional = telegramClient.send(getChatHistory).getObject();
                    if (messagesOptional.isPresent()) {
                        TdApi.Messages messages = messagesOptional.get();
                        TdApi.Message[] messagesArray = messages.messages;
                        if (messagesArray.length == 0) break;
                        fromMessageId = viewMessagesAndGetFromMessageId(chatId, messagesArray);
                    }
                } else break;
            } while (fromMessageId != lastReadInboxMessageId);
            log.info("Mark messages as read: [chatId: {}]", chatId);
        } finally {
            closeChat(chatId);
        }
    }

    private long viewMessagesAndGetFromMessageId(long chatId, TdApi.Message[] messages) {
        long[] ids = new long[messages.length];
        for (int i = 0; i < messages.length; i++) {
            ids[i] = messages[i].id;
        }
        var viewMessages = new TdApi.ViewMessages(chatId, ids, null, false);
        telegramClient.send(viewMessages);
        return ids[ids.length - 1];
    }

    private void closeChat(long chatId) {
        // closing only if chat is not opened on frontend
        if (!Caches.openedChat.get().equals(chatId)) {
            telegramClient.sendAsync(new TdApi.CloseChat(chatId));
        }
    }
}
