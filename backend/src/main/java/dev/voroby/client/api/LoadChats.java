package dev.voroby.client.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.tdlib.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
        Caches.initialChatCache.values().forEach(chat -> {
            if (Caches.mainListChatIds.contains(chat.id)) {
                previews.add(getCurrentChatPreview(chat));
            }
        });

        return previews;
    }

}
