package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.dto.ChatType;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract public class AbstractUpdates {

    final UpdatesQueues updatesQueues;

    final TelegramClient telegramClient;

    private final Map<Long, String> chatBase64IconCache = new ConcurrentHashMap<>();

    protected AbstractUpdates(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        this.updatesQueues = updatesQueues;
        this.telegramClient = telegramClient;
    }

    ChatPreview getCurrentChatPreview(long chatId) {
        TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(chatId));
        return getCurrentChatPreview(chat);
    }

    ChatPreview getCurrentChatPreview(TdApi.Chat chat) {
        String msgText = Utils.getMessageText(chat.lastMessage);
        String photoBase64 = null;
        if (chatBase64IconCache.containsKey(chat.id)) {
            photoBase64 = chatBase64IconCache.get(chat.id);
        } else {
            TdApi.ChatPhotoInfo photo = chat.photo;
            if (photo != null) {
                photoBase64 = getPhotoBase64(photo);
                chatBase64IconCache.put(chat.id, photoBase64);
            }
        }
        long order = Utils.mainChatListPositionOrder(chat.positions);
        ChatType chatType = null;
        switch (chat.type.getConstructor()) {
            case TdApi.ChatTypePrivate.CONSTRUCTOR -> chatType = ChatType.Private;
            case TdApi.ChatTypeSecret.CONSTRUCTOR -> chatType = ChatType.Secret;
            case TdApi.ChatTypeBasicGroup.CONSTRUCTOR -> chatType = ChatType.BasicGroup;
            case TdApi.ChatTypeSupergroup.CONSTRUCTOR -> chatType = ChatType.Supergroup;
        }
        return new ChatPreview(chat.id, chat.title, photoBase64, msgText, chat.unreadCount, order, chatType);
    }

    @SneakyThrows(IOException.class)
    String getPhotoBase64(TdApi.ChatPhotoInfo photo) {
        String photoBase64;
        TdApi.File small = photo.small;
        small = telegramClient.sendSync(new TdApi.DownloadFile(small.id, 32, 0, 0, true));
        byte[] bytes = Files.readAllBytes(Path.of(small.local.path));
        photoBase64 = Base64.getEncoder().encodeToString(bytes);
        return photoBase64;
    }

}
