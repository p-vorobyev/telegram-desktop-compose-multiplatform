package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service @Slf4j
public class LoadChats implements Supplier<List<ChatPreview>> {

    @Autowired
    private TelegramClient telegramClient;

    @Override
    public List<ChatPreview> get() {
        List<ChatPreview> previews = new ArrayList<>();
        CompletableFuture<List<Long>> future = telegramClient.sendAsync(new TdApi.GetChats(new TdApi.ChatListMain(), 100))
                .thenApply(chats -> Arrays.stream(chats.chatIds).boxed().toList())
                .thenApply(ids -> {
                    for (Long chatId : ids) {
                        TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(chatId));
                        if (chat.title.isBlank()) continue;
                        TdApi.Message lastMessage = chat.lastMessage;
                        String msgText = Utils.getMessageText(lastMessage);
                        String photoBase64 = null;
                        TdApi.ChatPhotoInfo photo = chat.photo;
                        if (photo != null) {
                            photoBase64 = getPhotoBase64(photo);
                        }
                        long order = Utils.mainChatListPositionOrder(chat.positions);
                        var chatPreview = new ChatPreview(chatId, chat.title, photoBase64, msgText, chat.unreadCount, order);
                        previews.add(chatPreview);
                    }
                    return ids;
                });
        future.join();

        return previews;
    }

    @SneakyThrows(IOException.class)
    private String getPhotoBase64(TdApi.ChatPhotoInfo photo) {
        String photoBase64;
        TdApi.File small = photo.small;
        small = telegramClient.sendSync(new TdApi.DownloadFile(small.id, 10, 0, 0, true));
        byte[] bytes = Files.readAllBytes(Path.of(small.local.path));
        photoBase64 = Base64.getEncoder().encodeToString(bytes);
        return photoBase64;
    }

}
