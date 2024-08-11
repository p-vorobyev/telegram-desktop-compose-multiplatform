package dev.voroby.client.files.application;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chatList.application.ChatListUpdatesQueue;
import dev.voroby.client.chatList.dto.ChatPhotoFile;
import dev.voroby.client.util.Utils;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class NotifyChatPhotoCached implements Consumer<ChatPhotoFile> {

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public NotifyChatPhotoCached(ChatListUpdatesQueue chatListUpdatesQueue) {
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }


    @Override
    public void accept(ChatPhotoFile chatPhotoFile) {
        long chatId = chatPhotoFile.chatId();
        TdApi.File file = chatPhotoFile.file();
        Caches.chatIdToPhotoCache.put(chatId, Utils.fileBase64Encode(file.local.path));
        var chatPhotoInfo = new TdApi.ChatPhotoInfo();
        chatPhotoInfo.small = file;
        var updateChatPhoto = new TdApi.UpdateChatPhoto(chatId, chatPhotoInfo);
        chatListUpdatesQueue.addIncomingUpdate(updateChatPhoto);
    }

}
