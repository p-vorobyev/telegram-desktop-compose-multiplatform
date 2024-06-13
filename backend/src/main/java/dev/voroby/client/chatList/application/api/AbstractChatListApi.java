package dev.voroby.client.chatList.application.api;

import dev.voroby.client.common.file.application.NotifyChatPhotoCached;
import dev.voroby.client.common.file.application.api.StartDownloadFile;
import dev.voroby.client.util.Utils;
import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatPhotoFile;
import dev.voroby.client.chatList.dto.ChatPreview;
import dev.voroby.client.chatList.dto.ChatType;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.voroby.client.cache.Caches.chatIdToPhotoCache;
import static dev.voroby.client.cache.Caches.initialChatCache;

abstract public class AbstractChatListApi {

    public final TelegramClient telegramClient;

    @Autowired
    private StartDownloadFile startDownloadFile;

    @Autowired
    private NotifyChatPhotoCached notifyChatPhotoCached;

    protected AbstractChatListApi(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    /**
     * For some reason, TDLib sends updates with deleted chats.
     * For this reason, we check it so as not calling deleted chats.
     */
    public ChatPreview checkMainListChatIds_And_GetCurrentChatPreview(long chatId) {
        if (Caches.mainListChatIds.contains(chatId)) {
            return getCurrentChatPreview(chatId);
        }
        return null;
    }

    public ChatPreview getCurrentChatPreview(long chatId) {
        TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(chatId));
        initialChatCache.put(chat.id, chat);
        return getCurrentChatPreview(chat);
    }

    public ChatPreview getCurrentChatPreview(TdApi.Chat chat) {
        String msgText = Utils.getMessageText(chat.lastMessage);
        String photoBase64 = null;

        if (chat.photo != null && !Caches.photoIdToChatIdCache.containsKey(chat.photo.small.id)) {
            cacheChatPhoto(chat.photo.small, chat.id);
        } else if (chatIdToPhotoCache.containsKey(chat.id)) {
            photoBase64 = chatIdToPhotoCache.get(chat.id);
        }

        long order = mainChatListPositionOrder(chat.positions);

        ChatType chatType = null;
        boolean isChannel = false;
        switch (chat.type.getConstructor()) {
            case TdApi.ChatTypePrivate.CONSTRUCTOR -> chatType = ChatType.Private;
            case TdApi.ChatTypeSecret.CONSTRUCTOR -> chatType = ChatType.Secret;
            case TdApi.ChatTypeBasicGroup.CONSTRUCTOR -> chatType = ChatType.BasicGroup;
            case TdApi.ChatTypeSupergroup.CONSTRUCTOR -> {
                chatType = ChatType.Supergroup;
                isChannel = ((TdApi.ChatTypeSupergroup) chat.type).isChannel;
            }
        }

        long unreadCount = chat.unreadCount;
        //if chat is a forum and contains topics. not implemented
        if (chat.viewAsTopics) {
            unreadCount = 0;
        }

        return new ChatPreview(chat.id, chat.title, photoBase64, msgText, unreadCount, order, chatType, isChannel, chat.permissions.canSendBasicMessages);
    }

    private long mainChatListPositionOrder(TdApi.ChatPosition[] positions) {
        for (TdApi.ChatPosition position: positions) {
            if (position.list instanceof TdApi.ChatListMain) {
                return position.order;
            }
        }
        return -1;
    }

    private void cacheChatPhoto(TdApi.File file, long chatId) {
        if (file.local.isDownloadingCompleted) {
            Caches.photoIdToChatIdCache.put(file.id, chatId);
            notifyChatPhotoCached.accept(new ChatPhotoFile(chatId, file));
        } else {
            startDownloadFile.apply(file)
                    .thenAccept(response -> {
                        if (response.error() == null) {
                            Caches.photoIdToChatIdCache.put(response.object().id, chatId);
                        }
                    });
        }
    }

}
