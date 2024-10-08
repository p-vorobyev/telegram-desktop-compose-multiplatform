package dev.voroby.client.chatList.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatPreview;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetNewChat extends AbstractChatListApi implements Function<TdApi.UpdateNewChat, ChatPreview> {

    protected GetNewChat(TelegramClient telegramClient) {
        super(telegramClient);
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
