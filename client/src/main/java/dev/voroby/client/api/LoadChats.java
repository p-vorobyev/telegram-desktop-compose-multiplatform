package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Component
public class LoadChats extends AbstractUpdates implements Supplier<List<ChatPreview>> {

    protected LoadChats(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public List<ChatPreview> get() {
        List<ChatPreview> previews = new ArrayList<>();
        CompletableFuture<List<Long>> future = telegramClient.sendAsync(new TdApi.GetChats(new TdApi.ChatListMain(), 100))
                .thenApply(chats -> Arrays.stream(chats.chatIds).boxed().toList())
                .thenApply(ids -> {
                    for (Long chatId : ids) {
                        TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(chatId));
                        if (chat.title.isBlank()) continue;
                        previews.add(getCurrentChatPreview(chatId));
                    }
                    return ids;
                });
        future.join();

        return previews;
    }

}
