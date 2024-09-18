package dev.voroby.client.chat.common.application;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.common.dto.MessageGifAnimationInfo;
import dev.voroby.client.chat.common.dto.MessageId;
import dev.voroby.client.files.application.api.StartDownloadFile;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class CacheGifAnimationService implements Consumer<MessageGifAnimationInfo> {

    private final StartDownloadFile startDownloadFile;

    public CacheGifAnimationService(StartDownloadFile startDownloadFile) {
        this.startDownloadFile = startDownloadFile;
    }

    @Override
    public void accept(MessageGifAnimationInfo messageGifAnimationInfo) {
        TdApi.Animation animation = messageGifAnimationInfo.content().animation;
        TdApi.File file = animation.animation;
        startDownloadFile.apply(file);
        cacheGifAnimationIdentifiers(messageGifAnimationInfo, file.id);
    }

    private static void cacheGifAnimationIdentifiers(MessageGifAnimationInfo messageGifAnimationInfo, int fileId) {
        Caches.messageIdToGifAnimationIdCache.put(messageGifAnimationInfo.messageId(), fileId);
        var messageId = new MessageId(messageGifAnimationInfo.chatId(), messageGifAnimationInfo.messageId());
        Caches.gifAnimationIdToMessageIdCache.put(fileId, messageId);
    }
}
