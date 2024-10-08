package dev.voroby.client.chatList.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatPreview;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetChatLastMessage extends AbstractChatListApi implements Function<TdApi.UpdateChatLastMessage, ChatPreview> {

    protected GetChatLastMessage(TelegramClient telegramClient) {
        super(telegramClient);
    }

    @Override
    public ChatPreview apply(TdApi.UpdateChatLastMessage updateChatLastMessage) {
        if (updateChatLastMessage.lastMessage != null && Caches.mainListChatIds.contains(updateChatLastMessage.chatId)) {
            return getCurrentChatPreview(updateChatLastMessage.chatId);
        }
        return null;
    }

}
