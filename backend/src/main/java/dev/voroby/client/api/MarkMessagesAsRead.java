package dev.voroby.client.api;

import dev.voroby.client.api.service.OpenChatService;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component @Slf4j
public class MarkMessagesAsRead implements Consumer<Long> {

    private final TelegramClient telegramClient;

    private final OpenChatService openChatService;

    public MarkMessagesAsRead(TelegramClient telegramClient,
                              OpenChatService openChatService) {
        this.telegramClient = telegramClient;
        this.openChatService = openChatService;
    }

    @Override
    synchronized public void accept(Long chatId) {
        telegramClient.sendSync(new TdApi.OpenChat(chatId));
        try {
            long lastReadInboxMessageId;
            long nextFromMessageId = 0;
            do {
                TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(chatId));
                lastReadInboxMessageId = chat.lastReadInboxMessageId;
                var getChatHistory = new TdApi.GetChatHistory(chatId, nextFromMessageId, 0, 100, true);
                TdApi.Messages messages = telegramClient.sendSync(getChatHistory);
                TdApi.Message[] msgs = messages.messages;
                if (msgs.length == 0) break;
                long[] ids = new long[msgs.length];
                for (int i = 0; i < msgs.length; i++) {
                    ids[i] = msgs[i].id;
                }
                var viewMessages = new TdApi.ViewMessages(chatId, ids, null, false);
                telegramClient.sendSync(viewMessages);
                nextFromMessageId = ids[ids.length - 1];
            } while (nextFromMessageId != lastReadInboxMessageId);
            log.info("Mark messages as read: [chatId: {}]", chatId);
        } finally {
            // closing only if chat is not opened on frontend
            if (!openChatService.openedChat().equals(chatId)) {
                telegramClient.sendAsync(new TdApi.CloseChat(chatId));
            }
        }
    }

}
