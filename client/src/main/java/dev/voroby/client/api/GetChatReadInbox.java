package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetChatReadInbox extends AbstractUpdates implements Function<TdApi.UpdateChatReadInbox, ChatPreview> {

    protected GetChatReadInbox(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public ChatPreview apply(TdApi.UpdateChatReadInbox updateChatReadInbox) {
        if (mainListChatIds.contains(updateChatReadInbox.chatId)) {
            return getCurrentChatPreview(updateChatReadInbox.chatId);
        }
        return null;
    }

}
