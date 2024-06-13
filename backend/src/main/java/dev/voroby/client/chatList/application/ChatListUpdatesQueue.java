package dev.voroby.client.chatList.application;

import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class ChatListUpdatesQueue {

    private final Deque<TdApi.Update> incomingUpdates = new ConcurrentLinkedDeque<>();

    public void addIncomingUpdate(TdApi.Update update) {
        incomingUpdates.addLast(update);
    }

    public TdApi.Update pollIncomingUpdate() {
        return incomingUpdates.pollFirst();
    }

}
