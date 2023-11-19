package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Service;

@Service
public class GetChatNewOrder extends AbstractUpdatesSupplier<ChatPreview> {

    protected GetChatNewOrder(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public ChatPreview get() {
        TdApi.UpdateChatPosition updateChatPosition = updatesQueues.pollUpdateChatPosition();
        if (updateChatPosition != null) {
            TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(updateChatPosition.chatId));
            String msgText = Utils.getMessageText(chat.lastMessage);
            long order = updateChatPosition.position.order;
            return new ChatPreview(chat.id, chat.title, null, msgText, chat.unreadCount, order);
        }

        return null;
    }
}
