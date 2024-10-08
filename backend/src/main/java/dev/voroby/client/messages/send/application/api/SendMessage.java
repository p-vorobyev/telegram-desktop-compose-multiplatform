package dev.voroby.client.messages.send.application.api;

import dev.voroby.client.messages.send.dto.TdApiMessageContent;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.templates.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component @Slf4j
public class SendMessage implements Consumer<TdApiMessageContent> {

    private final TelegramClient telegramClient;

    public SendMessage(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void accept(TdApiMessageContent tdApiMessageContent) {
        var sendMessage = new TdApi.SendMessage();
        sendMessage.chatId = tdApiMessageContent.chatId();
        sendMessage.inputMessageContent = tdApiMessageContent.content();
        log.info("Sending a message to chat: [chatId: {}, queryIdentifier: {}]",
                tdApiMessageContent.chatId(), tdApiMessageContent.content().getConstructor());
        telegramClient.sendAsync(sendMessage)
                .thenAccept(response -> debugLog(tdApiMessageContent, response));
    }

    private static void debugLog(TdApiMessageContent tdApiMessageContent, Response<TdApi.Message> response) {
        if (response.error() == null) {
            log.debug("Sent message to chat: [chatId: {}, msgId: {}, queryIdentifier: {}]",
                    tdApiMessageContent.chatId(), response.object().id, tdApiMessageContent.content().getConstructor());
        }
    }
}
