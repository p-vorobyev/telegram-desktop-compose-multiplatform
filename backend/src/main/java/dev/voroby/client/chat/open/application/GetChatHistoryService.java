package dev.voroby.client.chat.open.application;

import dev.voroby.client.chat.common.dto.MessageGifAnimationInfo;
import dev.voroby.client.chat.common.dto.MessagePhotoInfo;
import dev.voroby.client.chat.open.application.api.GetChatHistory;
import dev.voroby.client.chat.open.dto.ChatHistoryRequest;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
public class GetChatHistoryService implements Function<ChatHistoryRequest, List<TdApi.Message>> {

    private final GetChatHistory getChatHistory;

    private final CacheMessagePhotoPreviewService cacheMessagePhotoPreviewService;

    private final CacheGifAnimationService cacheGifAnimationService;

    public GetChatHistoryService(GetChatHistory getChatHistory,
                                 CacheMessagePhotoPreviewService cacheMessagePhotoPreviewService,
                                 CacheGifAnimationService cacheGifAnimationService) {
        this.getChatHistory = getChatHistory;
        this.cacheMessagePhotoPreviewService = cacheMessagePhotoPreviewService;
        this.cacheGifAnimationService = cacheGifAnimationService;
    }

    @Override
    public List<TdApi.Message> apply(ChatHistoryRequest chatHistoryRequest) {
        List<TdApi.Message> messages = getChatHistory.apply(chatHistoryRequest);
        checkAndLoadContentToCache(messages);
        return messages;
    }

    private void checkAndLoadContentToCache(List<TdApi.Message> messages) {
        messages.stream()
                .filter(GetChatHistoryService::hasContentToCache)
                .map(this::getContentRecord)
                .filter(Objects::nonNull)
                .forEach(this::cacheContent);
    }

    private static boolean hasContentToCache(TdApi.Message message) {
        return message.content instanceof TdApi.MessagePhoto ||
                message.content instanceof TdApi.MessageAnimation;
    }

    private Object getContentRecord(TdApi.Message message) {
        return switch (message.content) {
            case TdApi.MessagePhoto messagePhoto -> new MessagePhotoInfo(message.id, message.chatId, messagePhoto);
            case TdApi.MessageAnimation messageAnimation ->
                    new MessageGifAnimationInfo(message.id, message.chatId, messageAnimation);
            default -> null;
        };
    }

    private void cacheContent(Object messageContentInfo) {
        switch (messageContentInfo) {
            case MessagePhotoInfo messagePhotoInfo ->
                    cacheMessagePhotoPreviewService.accept(messagePhotoInfo);
            case MessageGifAnimationInfo messageGifAnimationInfo ->
                    cacheGifAnimationService.accept(messageGifAnimationInfo);
            default -> {}
        }
    }
}
