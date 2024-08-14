package dev.voroby.client.chat.open.application;

import dev.voroby.client.chat.common.application.CacheMessagePhotoPreviewService;
import dev.voroby.client.chat.common.dto.MessagePhotoInfo;
import dev.voroby.client.chat.open.application.api.GetChatHistory;
import dev.voroby.client.chat.open.dto.ChatHistoryRequest;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class GetChatHistoryService implements Function<ChatHistoryRequest, List<TdApi.Message>> {

    private final GetChatHistory getChatHistory;

    private final CacheMessagePhotoPreviewService cacheMessagePhotoPreviewService;

    public GetChatHistoryService(GetChatHistory getChatHistory,
                                 CacheMessagePhotoPreviewService cacheMessagePhotoPreviewService) {
        this.getChatHistory = getChatHistory;
        this.cacheMessagePhotoPreviewService = cacheMessagePhotoPreviewService;
    }

    @Override
    public List<TdApi.Message> apply(ChatHistoryRequest chatHistoryRequest) {
        List<TdApi.Message> messages = getChatHistory.apply(chatHistoryRequest);
        checkAndCacheMessagePhotos(messages);
        return messages;
    }

    private void checkAndCacheMessagePhotos(List<TdApi.Message> messages) {
        messages.stream()
                .filter(message -> message.content instanceof TdApi.MessagePhoto)
                .map(message -> new MessagePhotoInfo(message.id, message.chatId, (TdApi.MessagePhoto) message.content))
                .forEach(cacheMessagePhotoPreviewService);
    }
}
