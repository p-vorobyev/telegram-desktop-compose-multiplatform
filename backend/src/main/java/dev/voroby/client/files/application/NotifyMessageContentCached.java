package dev.voroby.client.files.application;

import dev.voroby.client.chat.open.application.OpenChatService;
import dev.voroby.client.chat.common.dto.MessageId;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.templates.response.Response;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class NotifyMessageContentCached implements Consumer<MessageId> {

    private final TelegramClient telegramClient;

    private final OpenChatService openChatService;

    public NotifyMessageContentCached(@Lazy TelegramClient telegramClient, OpenChatService openChatService) {
        this.telegramClient = telegramClient;
        this.openChatService = openChatService;
    }

    @Override
    public void accept(MessageId messageId) {
        var getMessage = new TdApi.GetMessage(messageId.chatId(), messageId.messageId());
        telegramClient.sendAsync(getMessage)
                .thenApply(Response::object)
                .thenAccept(openChatService::addUpdatedMessage);
    }

}
