package dev.voroby.client.chat.common.application;

import dev.voroby.client.util.Utils;
import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.common.application.api.GetProfilePhoto;
import dev.voroby.client.chat.common.dto.MessagePhotoInfo;
import dev.voroby.client.chat.open.dto.ChatMessage;
import dev.voroby.springframework.telegram.client.TdApi;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class ConvertToChatMessagesService implements Function<List<TdApi.Message>, List<ChatMessage>> {

    private final CurrentUserService currentUserService;

    private final GetProfilePhoto getProfilePhoto;

    private final GetPhotoPreviewService getPhotoPreviewService;

    protected ConvertToChatMessagesService(CurrentUserService currentUserService,
                                           GetProfilePhoto getProfilePhoto,
                                           GetPhotoPreviewService getPhotoPreviewService) {
        this.currentUserService = currentUserService;
        this.getProfilePhoto = getProfilePhoto;
        this.getPhotoPreviewService = getPhotoPreviewService;
    }

    @Override
    public List<ChatMessage> apply(List<TdApi.Message> messages) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        messages.forEach(message -> chatMessages.add(convertToChatMessage(message)));
        return chatMessages;
    }

    private record SenderInfo(String senderDescription, String senderPhoto, boolean isCurrentUser) {}

    @SneakyThrows
    private ChatMessage convertToChatMessage(TdApi.Message message) {
        boolean isPrivate = !Caches.chatIdToGroupIdCache.containsKey(message.chatId);
        String messageText = Utils.getMessageText(message);
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.date * 1000L), ZoneId.systemDefault());
        String dateStr = date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT));
        String editDateStr = getMessageEditDateStr(message);
        var senderInfo = getSenderInfo(message);
        String photoPreview = null;
        if (message.content instanceof TdApi.MessagePhoto messagePhoto) {
            var messagePhotoInfo = new MessagePhotoInfo(message.id, message.chatId, messagePhoto);
            photoPreview = getPhotoPreviewService.apply(messagePhotoInfo);
        }
        return new ChatMessage(
                message.id,
                message.chatId,
                isPrivate,
                messageText,
                photoPreview,
                dateStr,
                editDateStr,
                senderInfo.senderDescription,
                senderInfo.senderPhoto,
                senderInfo.isCurrentUser,
                message.canBeDeletedForAllUsers,
                message.canBeDeletedOnlyForSelf
        );
    }

    private static String getMessageEditDateStr(TdApi.Message message) {
        String editDateStr = "";
        if (message.editDate > 0) {
            LocalDateTime editDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.editDate * 1000L), ZoneId.systemDefault());
            editDateStr = editDate.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT));
        }
        return editDateStr;
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
