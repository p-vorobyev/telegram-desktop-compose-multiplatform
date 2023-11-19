package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Service;

@Service
public class GetChatLastMessage extends AbstractUpdatesSupplier<ChatPreview> {

    protected GetChatLastMessage(UpdatesQueues updatesQueues,
                                 TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public ChatPreview get() {
        TdApi.UpdateChatLastMessage updateChatLastMessage = updatesQueues.pollChatLastMessage();
        if (updateChatLastMessage != null && updateChatLastMessage.lastMessage != null) {
            TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(updateChatLastMessage.chatId));
            String msgText = Utils.getMessageText(chat.lastMessage);
            long order = Utils.mainChatListPositionOrder(chat.positions);
            return new ChatPreview(chat.id, chat.title, null, msgText, chat.unreadCount, order);
        }
        return null;
    }

}
