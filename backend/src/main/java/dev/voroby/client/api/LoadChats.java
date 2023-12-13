package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Component
public class LoadChats extends AbstractUpdates implements Supplier<List<ChatPreview>> {

    private final AtomicBoolean initialLoadDone = new AtomicBoolean(false);

    protected LoadChats(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    public boolean chatsLoaded() {
        return initialLoadDone.get();
    }

    public void setInitialLoadDone() {
        initialLoadDone.set(true);
    }

    @Override
    public List<ChatPreview> get() {
        List<ChatPreview> previews = new ArrayList<>();
        CompletableFuture<List<Long>> future = telegramClient.sendAsync(new TdApi.GetChats(new TdApi.ChatListMain(), 500))
                .thenApply(chats -> Arrays.stream(chats.chatIds).boxed().toList())
                .thenApply(ids -> {
                    for (Long chatId : ids) {
                        previews.add(getCurrentChatPreview(chatId));
                    }
                    return ids;
                });
        future.join();

        return previews;
    }

}
