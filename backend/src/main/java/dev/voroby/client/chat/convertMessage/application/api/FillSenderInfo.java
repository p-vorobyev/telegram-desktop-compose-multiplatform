package dev.voroby.client.chat.convertMessage.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.users.application.CurrentUserService;
import dev.voroby.client.chat.common.application.api.GetProfilePhoto;
import dev.voroby.client.chat.convertMessage.dto.ConvertChatMessageContext;
import dev.voroby.client.chat.common.dto.ChatMessage;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class FillSenderInfo implements Function<ConvertChatMessageContext, ConvertChatMessageContext> {

    private final CurrentUserService currentUserService;

    private final GetProfilePhoto getProfilePhoto;

    public FillSenderInfo(CurrentUserService currentUserService, GetProfilePhoto getProfilePhoto) {
        this.currentUserService = currentUserService;
        this.getProfilePhoto = getProfilePhoto;
    }

    private record SenderInfo(String senderDescription, String senderPhoto, boolean isCurrentUser) {}

    @Override
    public ConvertChatMessageContext apply(ConvertChatMessageContext convertChatMessageContext) {
        TdApi.Message message = convertChatMessageContext.message();
        SenderInfo senderInfo = getSenderInfo(message);
        ChatMessage chatMessage = convertChatMessageContext.chatMessage()
                .withSenderInfo(senderInfo.senderDescription)
                .withSenderPhoto(senderInfo.senderPhoto)
                .withIsCurrentUser(senderInfo.isCurrentUser);

        return new ConvertChatMessageContext(message, chatMessage);
    }

    private SenderInfo getSenderInfo(TdApi.Message message) {
        TdApi.MessageSender senderId = message.senderId;
        return switch (senderId) {
            case TdApi.MessageSenderUser senderUser -> {
                TdApi.User user = Caches.userIdToUserCache.get(senderUser.userId);
                var senderDescription = String.join(" ", user.firstName, user.lastName).trim();
                var senderPhoto = getProfilePhoto.apply(user.id);
                boolean isCurrentUser = currentUserService.isCurrentUserId(user.id);
                yield new SenderInfo(senderDescription, senderPhoto, isCurrentUser);
            }
            case TdApi.MessageSenderChat senderChat -> {
                var senderDescription = Caches.initialChatCache.get(senderChat.chatId).title;
                var senderPhoto = Caches.chatIdToPhotoCache.getOrDefault(senderChat.chatId, "");
                yield new SenderInfo(senderDescription, senderPhoto, false);
            }
            default -> throw new IllegalStateException("Unexpected message sender type: " + senderId.getClass().getSimpleName());
        };
    }
}
