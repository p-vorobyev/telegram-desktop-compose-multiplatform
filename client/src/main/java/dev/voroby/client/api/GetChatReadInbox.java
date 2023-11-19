package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Service;

@Service
public class GetChatReadInbox extends AbstractUpdatesSupplier<ChatPreview> {

    protected GetChatReadInbox(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public ChatPreview get() {
        TdApi.UpdateChatReadInbox updateChatReadInbox = updatesQueues.pollUpdateChatReadInbox();
        if (updateChatReadInbox != null) {
            TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(updateChatReadInbox.chatId));
            String msgText = Utils.getMessageText(chat.lastMessage);
            long order = Utils.mainChatListPositionOrder(chat.positions);
            return new ChatPreview(chat.id, chat.title, null, msgText, chat.unreadCount, order);
        }
        return null;
    }
}
