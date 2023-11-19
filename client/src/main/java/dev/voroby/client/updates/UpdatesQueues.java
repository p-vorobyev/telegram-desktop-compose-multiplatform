package dev.voroby.client.updates;

import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class UpdatesQueues {

    private final Deque<TdApi.UpdateChatLastMessage> updateChatLastMessages = new ConcurrentLinkedDeque<>();
    private final Deque<TdApi.UpdateChatTitle> updateChatTitles = new ConcurrentLinkedDeque<>();
    private final Deque<TdApi.UpdateChatPosition> updateChatPosition = new ConcurrentLinkedDeque<>();
    private final Deque<TdApi.UpdateChatReadInbox> updateChatReadInbox = new ConcurrentLinkedDeque<>();

    public void addChatLastMessage(TdApi.UpdateChatLastMessage update) {
        updateChatLastMessages.addLast(update);
    }

    public TdApi.UpdateChatLastMessage pollChatLastMessage() {
        return updateChatLastMessages.pollFirst();
    }

    public void addUpdateChatTitle(TdApi.UpdateChatTitle update) {
        updateChatTitles.addLast(update);
    }

    public TdApi.UpdateChatTitle pollUpdateChatTitle() {
        return updateChatTitles.pollFirst();
    }

    public void addUpdateChatPosition(TdApi.UpdateChatPosition update) {
        updateChatPosition.addLast(update);
    }

    public TdApi.UpdateChatPosition pollUpdateChatPosition() {
        return updateChatPosition.pollFirst();
    }

    public void addUpdateChatReadInbox(TdApi.UpdateChatReadInbox update) {
        updateChatReadInbox.addLast(update);
    }

    public TdApi.UpdateChatReadInbox pollUpdateChatReadInbox() {
        return updateChatReadInbox.pollFirst();
    }

}
