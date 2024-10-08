package dev.voroby.client.chat.common.application.api;

import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class GetCurrentUser implements Supplier<TdApi.User> {

    private final TelegramClient telegramClient;

    public GetCurrentUser(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public TdApi.User get() {
        return telegramClient.send(new TdApi.GetMe()).object();
    }

}
