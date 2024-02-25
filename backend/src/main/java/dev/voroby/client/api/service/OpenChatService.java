package dev.voroby.client.api.service;

import dev.voroby.client.api.OpenChat;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component @Slf4j
public class OpenChatService implements Consumer<Long> {

    private final TelegramClient telegramClient;

    private final OpenChat openChatFunc;

    private final AtomicReference<Long> openedChat = new AtomicReference<>(null);

    private final Deque<TdApi.Message> incomingChatMessages = new ConcurrentLinkedDeque<>();

    private final Deque<TdApi.UpdateMessageContent> updatedContentInMessages = new ConcurrentLinkedDeque<>();

    //TODO Handle deleted messages

    public OpenChatService(@Lazy TelegramClient telegramClient,
                           OpenChat openChat) {
        this.telegramClient = telegramClient;
        this.openChatFunc = openChat;
    }

    @Override
    public synchronized void accept(Long chatId) {
        closeCurrentChatIfOpened();
        openChatFunc.accept(chatId);
        openedChat.set(chatId);
        log.info("Chat opened: [chatId: {}]", chatId);
    }

    public Long openedChat() {
        return openedChat.get();
    }

    public void addIncomingMessage(TdApi.Message message) {
        log.info("Incoming message in opened chat: [chatId: {}, msgId: {}]", message.chatId, message.id);
        incomingChatMessages.addLast(message);
    }

    public List<TdApi.Message> getIncomingMessages() {
        List<TdApi.Message> messages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TdApi.Message message = incomingChatMessages.pollFirst();
            if (message == null) break;
            messages.add(message);
        }
        return messages;
    }

    public void addUpdatedContent(TdApi.UpdateMessageContent updateMessageContent) {
        log.info("Updated message content in opened chat: [chatId: {}, msgId: {}]", updateMessageContent.chatId, updateMessageContent.messageId);
        updatedContentInMessages.addLast(updateMessageContent);
    }

    private void closeCurrentChatIfOpened() {
        Long chatId = openedChat.get();
        if (chatId != null) {
            telegramClient.sendAsync(new TdApi.CloseChat(chatId));
            incomingChatMessages.clear();
            updatedContentInMessages.clear();
            log.info("Chat closed: [chatId: {}]", chatId);
        }
    }

    @PreDestroy
    void onDestroy() {
        closeCurrentChatIfOpened();
    }

}
