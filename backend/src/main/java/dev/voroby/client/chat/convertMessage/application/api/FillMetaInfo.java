package dev.voroby.client.chat.convertMessage.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.convertMessage.dto.ConvertChatMessageContext;
import dev.voroby.client.chat.common.dto.ChatMessage;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Function;

@Component
public class FillMetaInfo implements Function<ConvertChatMessageContext, ConvertChatMessageContext> {

    @Override
    public ConvertChatMessageContext apply(ConvertChatMessageContext convertChatMessageContext) {
        TdApi.Message message = convertChatMessageContext.message();
        boolean isPrivate = !Caches.chatIdToGroupIdCache.containsKey(message.chatId);
        String date = getMessageDate(message);
        String editDate = getMessageEditDate(message);
        TdApi.Chat chat = Caches.initialChatCache.get(message.chatId);
        ChatMessage chatMessage = convertChatMessageContext.chatMessage()
                .withId(message.id)
                .withChatId(message.chatId)
                .withPrivateChat(isPrivate)
                .withDate(date)
                .withEditDate(editDate)
                .withCanBeDeletedForAllUsers(chat != null && chat.canBeDeletedForAllUsers)
                .withCanBeDeletedOnlyForSelf(chat != null && chat.canBeDeletedOnlyForSelf);

        return new ConvertChatMessageContext(message, chatMessage);
    }

    private String getMessageEditDate(TdApi.Message message) {
        String editDateStr = "";
        if (message.editDate > 0) {
            LocalDateTime editDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.editDate * 1000L), ZoneId.systemDefault());
            editDateStr = editDate.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT));
        }
        return editDateStr;
    }

    private String getMessageDate(TdApi.Message message) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.date * 1000L), ZoneId.systemDefault());
        return date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT));
    }
}
