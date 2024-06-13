package dev.voroby.client.files.application;

import dev.voroby.client.chat.open.application.OpenChatService;
import dev.voroby.client.chat.common.dto.MessageId;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class NotifyMessagePhotoCached implements Consumer<MessageId> {

    private final TelegramClient telegramClient;

    private final OpenChatService openChatService;

    public NotifyMessagePhotoCached(@Lazy TelegramClient telegramClient, OpenChatService openChatService) {
        this.telegramClient = telegramClient;
        this.openChatService = openChatService;
    }

    @Override
    public void accept(MessageId messageId) {
        telegramClient.sendWithCallback(new TdApi.GetMessage(messageId.chatId(), messageId.messageId()),
                (obj, error) -> openChatService.addUpdatedMessage(obj));
    }

}
