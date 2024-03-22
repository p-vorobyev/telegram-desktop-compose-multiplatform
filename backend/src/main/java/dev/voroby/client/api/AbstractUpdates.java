package dev.voroby.client.api;

import dev.voroby.client.api.util.Utils;
import dev.voroby.client.cache.Caches;
import dev.voroby.client.dto.ChatPhotoFile;
import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.dto.ChatType;
import dev.voroby.client.dto.MessageId;
import dev.voroby.client.updates.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import static dev.voroby.client.cache.Caches.chatIdToPhotoCache;
import static dev.voroby.client.cache.Caches.initialChatCache;

abstract public class AbstractUpdates {

    final UpdatesQueues updatesQueues;

    final TelegramClient telegramClient;

    @Autowired @Lazy
    private NotifyChatPhotoCached notifyChatPhotoCached;

    @Autowired @Lazy
    private DownloadPhotoPreview downloadPhotoPreview;

    protected AbstractUpdates(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        this.updatesQueues = updatesQueues;
        this.telegramClient = telegramClient;
    }

    /**
     * For some reason, TDLib sends updates with deleted chats.
     * For this reason, we check it so as not calling deleted chats.
     */
    ChatPreview checkMainListChatIds_And_GetCurrentChatPreview(long chatId) {
        if (Caches.mainListChatIds.contains(chatId)) {
            return getCurrentChatPreview(chatId);
        }
        return null;
    }

    ChatPreview getCurrentChatPreview(long chatId) {
        TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(chatId));
        initialChatCache.put(chat.id, chat);
        return getCurrentChatPreview(chat);
    }

    ChatPreview getCurrentChatPreview(TdApi.Chat chat) {
        String msgText = Utils.getMessageText(chat.lastMessage);
        String photoBase64 = null;

        if (chat.photo != null && !Caches.photoIdToChatIdCache.containsKey(chat.photo.small.id)) {
            cacheChatPhoto(chat.photo, chat.id);
        } else if (chatIdToPhotoCache.containsKey(chat.id)) {
            photoBase64 = chatIdToPhotoCache.get(chat.id);
        }

        long order = Utils.mainChatListPositionOrder(chat.positions);

        ChatType chatType = null;
        switch (chat.type.getConstructor()) {
            case TdApi.ChatTypePrivate.CONSTRUCTOR -> chatType = ChatType.Private;
            case TdApi.ChatTypeSecret.CONSTRUCTOR -> chatType = ChatType.Secret;
            case TdApi.ChatTypeBasicGroup.CONSTRUCTOR -> chatType = ChatType.BasicGroup;
            case TdApi.ChatTypeSupergroup.CONSTRUCTOR -> chatType = ChatType.Supergroup;
        }

        long unreadCount = chat.unreadCount;
        //if chat is a forum and contains topics. not implemented
        if (chat.viewAsTopics) {
            unreadCount = 0;
        }

        return new ChatPreview(chat.id, chat.title, photoBase64, msgText, unreadCount, order, chatType);
    }

    void cacheChatPhoto(TdApi.ChatPhotoInfo photo, long chatId) {
        TdApi.File small = photo.small;
        if (small.local.isDownloadingCompleted) {
            Caches.photoIdToChatIdCache.put(small.id, chatId);
            notifyChatPhotoCached.accept(new ChatPhotoFile(chatId, small));
        } else {
            telegramClient.sendWithCallback(new TdApi.DownloadFile(small.id, 32, 0, 0, false), (file, error) -> {
                if (error == null) {
                    Caches.photoIdToChatIdCache.put(file.id, chatId);
                }
            });
        }
    }

    void loadMessagePhotoPreviewIfExist(TdApi.Message message) {
        if (message.content instanceof TdApi.MessagePhoto messagePhoto) {
            Integer photoPreviewId = downloadPhotoPreview.apply(messagePhoto);
            if (photoPreviewId != null) {
                Caches.messageIdToPhotoPreviewIdCache.put(message.id, photoPreviewId);
                Caches.photoPreviewIdToMessageIdCache.put(photoPreviewId, new MessageId(message.chatId, message.id));
            }
        }
    }

}
