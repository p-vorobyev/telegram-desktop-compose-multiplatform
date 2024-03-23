package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.tdlib.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetChatNewTitle extends AbstractUpdates implements Function<TdApi.UpdateChatTitle, ChatPreview> {

    protected GetChatNewTitle(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public ChatPreview apply(TdApi.UpdateChatTitle updateChatTitle) {
        return checkMainListChatIds_And_GetCurrentChatPreview(updateChatTitle.chatId);
    }

}
