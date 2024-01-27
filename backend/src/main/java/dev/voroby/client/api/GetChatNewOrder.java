package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetChatNewOrder extends AbstractUpdates implements Function<TdApi.UpdateChatPosition, ChatPreview> {

    protected GetChatNewOrder(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public ChatPreview apply(TdApi.UpdateChatPosition updateChatPosition) {
        return checkMainListChatIds_And_GetCurrentChatPreview(updateChatPosition.chatId);
    }

}
