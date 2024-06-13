package dev.voroby.client.chat.common.application;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.common.application.api.GetPhotoPreview;
import dev.voroby.client.chat.common.dto.MessagePhotoInfo;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class GetPhotoPreviewService implements Function<MessagePhotoInfo, String> {

    private final GetPhotoPreview getPhotoPreview;

    private final CacheMessagePhotoPreviewService cacheMessagePhotoPreviewService;

    public GetPhotoPreviewService(GetPhotoPreview getPhotoPreview,
                                  CacheMessagePhotoPreviewService cacheMessagePhotoPreviewService) {
        this.getPhotoPreview = getPhotoPreview;
        this.cacheMessagePhotoPreviewService = cacheMessagePhotoPreviewService;
    }

    @Override
    public String apply(MessagePhotoInfo messagePhotoInfo) {
        String photoPreview = "";
        Integer photoPreviewId = Caches.messageIdToPhotoPreviewIdCache.get(messagePhotoInfo.messageId());
        if (photoPreviewId != null) {
            photoPreview = getPhotoPreview.apply(photoPreviewId);
        } else {
            cacheMessagePhotoPreviewService.accept(messagePhotoInfo);
        }
        return photoPreview;
    }
}
