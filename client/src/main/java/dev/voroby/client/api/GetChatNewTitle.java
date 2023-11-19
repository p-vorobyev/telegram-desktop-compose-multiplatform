package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Service;

@Service
public class GetChatNewTitle extends AbstractUpdatesSupplier<ChatPreview> {

    protected GetChatNewTitle(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public ChatPreview get() {
        TdApi.UpdateChatTitle updateChatTitle = updatesQueues.pollUpdateChatTitle();
        if (updateChatTitle != null) {
            TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(updateChatTitle.chatId));
            String msgText = Utils.getMessageText(chat.lastMessage);
            long order = Utils.mainChatListPositionOrder(chat.positions);
            return new ChatPreview(chat.id, updateChatTitle.title, null, msgText, chat.unreadCount, order);
        }
        return null;
    }

}
