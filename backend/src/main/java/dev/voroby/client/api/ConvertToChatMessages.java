package dev.voroby.client.api;

import dev.voroby.client.api.service.CurrentUserService;
import dev.voroby.client.api.util.Utils;
import dev.voroby.client.cache.Caches;
import dev.voroby.client.dto.ChatMessage;
import dev.voroby.client.updates.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class ConvertToChatMessages extends AbstractUpdates implements Function<List<TdApi.Message>, List<ChatMessage>> {

    private final CurrentUserService currentUserService;

    private final GetProfilePhoto getProfilePhoto;

    protected ConvertToChatMessages(UpdatesQueues updatesQueues,
                                    TelegramClient telegramClient,
                                    CurrentUserService currentUserService,
                                    GetProfilePhoto getProfilePhoto) {
        super(updatesQueues, telegramClient);
        this.currentUserService = currentUserService;
        this.getProfilePhoto = getProfilePhoto;
    }

    @Override
    public List<ChatMessage> apply(List<TdApi.Message> messages) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        messages.forEach(message -> chatMessages.add(convertToChatMessage(message)));
        return chatMessages;
    }

    private ChatMessage convertToChatMessage(TdApi.Message message) {
        boolean isPrivate = !Caches.chatIdToGroupIdCache.containsKey(message.chatId);
        String messageText = Utils.getMessageText(message);
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.date * 1000L), ZoneId.systemDefault());
        String dateStr = date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT));
        String editDateStr = "";
        if (message.editDate > 0) {
            LocalDateTime editDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.editDate * 1000L), ZoneId.systemDefault());
            editDateStr = editDate.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT));
        }
        TdApi.MessageSender senderId = message.senderId;
        String senderInfo = "";
        boolean isCurrentUser = false;
        String senderPhoto = "";
        if (senderId instanceof TdApi.MessageSenderUser senderUser) {
            TdApi.User user = Caches.userIdToUserCache.get(senderUser.userId);
            senderInfo = String.join(" ", user.firstName, user.lastName).trim();
            isCurrentUser = currentUserService.isCurrentUserId(user.id);
            senderPhoto = getProfilePhoto.apply(user.id);
        }
        if (senderId instanceof TdApi.MessageSenderChat senderChat) {
            senderInfo = Caches.initialChatCache.get(senderChat.chatId).title;
            senderPhoto = Caches.chatIdToPhotoCache.getOrDefault(senderChat.chatId, "");
        }

        return new ChatMessage(
                message.id,
                message.chatId,
                isPrivate,
                messageText,
                dateStr,
                editDateStr,
                senderInfo,
                senderPhoto,
                isCurrentUser,
                message.canBeDeletedForAllUsers,
                message.canBeDeletedOnlyForSelf
        );
    }

}
