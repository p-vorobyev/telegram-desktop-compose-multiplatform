package dev.voroby.client.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetNewChat extends AbstractUpdates implements Function<TdApi.UpdateNewChat, ChatPreview> {

    protected GetNewChat(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public ChatPreview apply(TdApi.UpdateNewChat updateNewChat) {
        TdApi.Chat chat = updateNewChat.chat;
        if (Caches.mainListChatIds.contains(chat.id)) {
            return getCurrentChatPreview(chat.id);
        }
        return null;
    }

}
