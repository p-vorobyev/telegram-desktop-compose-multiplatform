package dev.voroby.client.api.service;

import dev.voroby.client.api.OpenChat;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Component @Slf4j
public class OpenChatService implements Consumer<Long> {

    private final TelegramClient telegramClient;

    private final OpenChat openChatFunc;

    private final AtomicReference<Long> openedChat = new AtomicReference<>(null);

    private final Deque<TdApi.Message> incomingChatMessages = new ConcurrentLinkedDeque<>();

    private final Deque<TdApi.Message> updatedChatMessages = new ConcurrentLinkedDeque<>();

    private final Deque<Long> deletedMsgIds = new ConcurrentLinkedDeque<>();

    private final Lock lock = new ReentrantLock();

    public OpenChatService(@Lazy TelegramClient telegramClient,
                           OpenChat openChat) {
        this.telegramClient = telegramClient;
        this.openChatFunc = openChat;
    }

    @Override
    public void accept(Long chatId) {
        lock.lock();
        try {
            closeCurrentChatIfOpened();
            openChatFunc.accept(chatId);
            openedChat.set(chatId);
            log.info("Chat opened: [chatId: {}]", chatId);
        } finally {
            lock.unlock();
        }
    }

    public Long openedChat() {
        return openedChat.get();
    }

    public void addIncomingMessage(TdApi.Message message) {
        if (addMessageToQueue(incomingChatMessages, message)) {
            log.info("Incoming message in opened chat: [chatId: {}, msgId: {}]", message.chatId, message.id);
        }
    }

    public void addUpdatedMessage(TdApi.Message message) {
        if (addMessageToQueue(updatedChatMessages, message)) {
            log.info("Updated message content in opened chat: [chatId: {}, msgId: {}]", message.chatId, message.id);
        }
    }

    private boolean addMessageToQueue(Deque<TdApi.Message> messageQueue, TdApi.Message message) {
        lock.lock();
        try {
            if (message.chatId == openedChat.get()) {
                messageQueue.addLast(message);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public List<TdApi.Message> getIncomingMessages() {
        return pollMessagesFromQueue(incomingChatMessages);
    }

    public List<TdApi.Message> getEditedMessages() {
        return pollMessagesFromQueue(updatedChatMessages);
    }

    private List<TdApi.Message> pollMessagesFromQueue(Deque<TdApi.Message> messageQueue) {
        List<TdApi.Message> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TdApi.Message message = messageQueue.pollFirst();
            if (message == null) break;
            messages.add(message);
        }
        return messages;
    }

    public void addDeletedMsgIds(long chatId, Collection<Long> ids) {
        lock.lock();
        try {
            if (openedChat.get() == chatId) {
                deletedMsgIds.addAll(ids);
            }
        } finally {
            lock.unlock();
        }
    }

    public List<Long> getDeletedMsgIds() {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Long id = deletedMsgIds.pollFirst();
            if (id == null) break;
            ids.add(id);
        }
        return ids;
    }

    private void closeCurrentChatIfOpened() {
        Long chatId = openedChat.get();
        if (chatId != null) {
            telegramClient.sendAsync(new TdApi.CloseChat(chatId));
            incomingChatMessages.clear();
            updatedChatMessages.clear();
            deletedMsgIds.clear();
            log.info("Chat closed: [chatId: {}]", chatId);
        }
    }

    @PreDestroy
    void onDestroy() {
        closeCurrentChatIfOpened();
    }

}
