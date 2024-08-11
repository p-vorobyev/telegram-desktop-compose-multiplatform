package dev.voroby.client.chatList.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatPreview;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Component
public class GetChatList extends AbstractChatListApi implements Supplier<List<ChatPreview>> {

    protected GetChatList(TelegramClient telegramClient) {
        super(telegramClient);
    }

    @Override
    public List<ChatPreview> get() {
        List<ChatPreview> previews = new ArrayList<>();
        Caches.initialChatCache.values().forEach(chat -> {
            if (Caches.mainListChatIds.contains(chat.id)) {
                ChatPreview chatPreview = getCurrentChatPreview(chat.id);
                if (chatPreview != null) {
                    previews.add(chatPreview);
                }
            }
        });

        return previews;
    }

}
