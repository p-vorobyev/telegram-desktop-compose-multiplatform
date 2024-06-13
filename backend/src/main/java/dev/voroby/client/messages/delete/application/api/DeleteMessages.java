package dev.voroby.client.messages.delete.application.api;

import dev.voroby.client.messages.delete.dto.DeleteMessagesDto;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component @Slf4j
public class DeleteMessages implements Consumer<DeleteMessagesDto> {

    private final TelegramClient telegramClient;

    public DeleteMessages(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void accept(DeleteMessagesDto deleteMessagesDto) {
        telegramClient.sendAsync(new TdApi.DeleteMessages(deleteMessagesDto.chatId(), deleteMessagesDto.ids(), true));
        log.info("Messages deleted: [chatId: {}, count: {}]", deleteMessagesDto.chatId(), deleteMessagesDto.ids().length);
    }
}
