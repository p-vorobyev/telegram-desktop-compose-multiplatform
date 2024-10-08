package dev.voroby.client.chatList.application.api;

import dev.voroby.client.chatList.dto.ChatPreview;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GetChatReadInbox extends AbstractChatListApi implements Function<TdApi.UpdateChatReadInbox, ChatPreview> {

    protected GetChatReadInbox(TelegramClient telegramClient) {
        super(telegramClient);
    }

    @Override
    public ChatPreview apply(TdApi.UpdateChatReadInbox updateChatReadInbox) {
        return checkMainListChatIds_And_GetCurrentChatPreview(updateChatReadInbox.chatId);
    }

}
