package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetChatLastMessage extends AbstractUpdates implements Function<TdApi.UpdateChatLastMessage, ChatPreview> {

    protected GetChatLastMessage(UpdatesQueues updatesQueues,
                                 TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public ChatPreview apply(TdApi.UpdateChatLastMessage updateChatLastMessage) {
        if (updateChatLastMessage.lastMessage != null && mainListChatIds.contains(updateChatLastMessage.chatId)) {
            return getCurrentChatPreview(updateChatLastMessage.chatId);
        }
        return null;
    }

}
