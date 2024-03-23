package dev.voroby.client.api;

import dev.voroby.client.api.util.Utils;
import dev.voroby.client.cache.Caches;
import dev.voroby.client.dto.ChatPhotoFile;
import dev.voroby.client.tdlib.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class NotifyChatPhotoCached extends AbstractUpdates implements Consumer<ChatPhotoFile> {

    protected NotifyChatPhotoCached(UpdatesQueues updatesQueues, @Lazy TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public void accept(ChatPhotoFile chatPhotoFile) {
        long chatId = chatPhotoFile.chatId();
        TdApi.File file = chatPhotoFile.file();
        Caches.chatIdToPhotoCache.put(chatId, Utils.fileBase64Encode(file.local.path));
        var chatPhotoInfo = new TdApi.ChatPhotoInfo();
        chatPhotoInfo.small = file;
        var updateChatPhoto = new TdApi.UpdateChatPhoto(chatId, chatPhotoInfo);
        updatesQueues.addIncomingSidebarUpdate(updateChatPhoto);
    }

}
