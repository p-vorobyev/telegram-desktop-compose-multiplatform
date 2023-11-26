package dev.voroby.client.updates;

import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class UpdatesQueues {

    private final Deque<TdApi.Update> incomingUpdates = new ConcurrentLinkedDeque<>();

    public void addIncomingSidebarUpdate(TdApi.Update update) {
        incomingUpdates.addLast(update);
    }

    public TdApi.Update pollIncomingSidebarUpdate() {
        return incomingUpdates.pollFirst();
    }

}
