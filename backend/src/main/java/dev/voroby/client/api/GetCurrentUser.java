package dev.voroby.client.api;

import dev.voroby.client.tdlib.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class GetCurrentUser extends AbstractUpdates implements Supplier<TdApi.User> {

    protected GetCurrentUser(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public TdApi.User get() {
        return telegramClient.sendSync(new TdApi.GetMe());
    }

}
