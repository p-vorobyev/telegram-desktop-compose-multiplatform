package dev.voroby.client.chatList.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.dto.ChatPhotoFile;
import dev.voroby.client.chatList.dto.ChatPreview;
import dev.voroby.client.chatList.dto.ChatType;
import dev.voroby.client.files.application.NotifyChatPhotoCached;
import dev.voroby.client.files.application.api.StartDownloadFile;
import dev.voroby.client.util.Utils;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.templates.response.Response;
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
        Response<TdApi.Chat> chatResponse = telegramClient.send(new TdApi.GetChat(chatId));
        TdApi.Chat chat = chatResponse.getObject()
                .orElseThrow(() -> new RuntimeException(chatResponse.getError().orElse(new TdApi.Error()).message));
        if (!hasChatList(chat)) return null; // chat is not listed
        initialChatCache.put(chat.id, chat);
        return getCurrentChatPreview(chat);
    }

    private boolean hasChatList(TdApi.Chat chat) {
        if (chat.chatLists.length == 0) {
            removeFromCache(chat.id);
            return false;
        }
        return true;
    }

    private void removeFromCache(long chatId) {
        Caches.mainListChatIds.remove(chatId);
        initialChatCache.remove(chatId);
    }

    private ChatPreview getCurrentChatPreview(TdApi.Chat chat) {
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

        return new ChatPreview(
                chat.id,
                chat.title,
                photoBase64,
                msgText,
                unreadCount,
                order,
                chatType,
                isChannel,
                chat.permissions.canSendBasicMessages
        );
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
                    .thenAccept(response ->
                            response.onSuccess(f -> Caches.photoIdToChatIdCache.put(f.id, chatId))
                    );
        }
    }

}
