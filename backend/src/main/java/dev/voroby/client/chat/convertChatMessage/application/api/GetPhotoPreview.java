package dev.voroby.client.chat.convertChatMessage.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.common.application.api.GetBase64FileContent;
import dev.voroby.client.chat.common.dto.EncodedContent.Photo;
import dev.voroby.client.chat.common.dto.MessagePhotoInfo;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class GetPhotoPreview implements Function<MessagePhotoInfo, Photo> {

    private final GetBase64FileContent getBase64FileContent;

    public GetPhotoPreview(GetBase64FileContent getBase64FileContent) {
        this.getBase64FileContent = getBase64FileContent;
    }

    @Override
    public Photo apply(MessagePhotoInfo messagePhotoInfo) {
        String encodedPhoto = "";
        Integer photoPreviewId = Caches.messageIdToPhotoPreviewIdCache.get(messagePhotoInfo.messageId());
        if (photoPreviewId != null) {
            encodedPhoto = getBase64FileContent.apply(photoPreviewId);
        }
        return new Photo(encodedPhoto);
    }
}
