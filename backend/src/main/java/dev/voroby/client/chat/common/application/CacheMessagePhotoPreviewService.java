package dev.voroby.client.chat.common.application;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.common.application.api.DownloadPhotoPreview;
import dev.voroby.client.chat.common.dto.MessagePhotoInfo;
import dev.voroby.client.chat.common.dto.MessageId;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class CacheMessagePhotoPreviewService implements Consumer<MessagePhotoInfo> {

    private final DownloadPhotoPreview downloadPhotoPreview;

    public CacheMessagePhotoPreviewService(DownloadPhotoPreview downloadPhotoPreview) {
        this.downloadPhotoPreview = downloadPhotoPreview;
    }

    @Override
    public void accept(MessagePhotoInfo messagePhotoInfo) {
        Integer photoPreviewId = downloadPhotoPreview.apply(messagePhotoInfo.content());
        if (photoPreviewId != null) {
            cacheMessagePhotoIdentifiers(messagePhotoInfo, photoPreviewId);
        }
    }

    private static void cacheMessagePhotoIdentifiers(MessagePhotoInfo messagePhotoInfo, Integer photoPreviewId) {
        Caches.messageIdToPhotoPreviewIdCache.put(messagePhotoInfo.messageId(), photoPreviewId);
        Caches.photoPreviewIdToMessageIdCache.put(
                photoPreviewId,
                new MessageId(messagePhotoInfo.chatId(), messagePhotoInfo.messageId())
        );
    }
}
