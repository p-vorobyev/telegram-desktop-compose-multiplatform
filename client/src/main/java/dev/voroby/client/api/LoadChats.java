package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class LoadChats implements Supplier<List<ChatPreview>> {

    @Autowired
    private TelegramClient telegramClient;

    @Override
    public List<ChatPreview> get() {
        List<ChatPreview> previews = new ArrayList<>();
        CompletableFuture<List<Long>> future = telegramClient.sendAsync(new TdApi.GetChats(new TdApi.ChatListMain(), 100), TdApi.Chats.class)
                .thenApply(chats -> Arrays.stream(chats.chatIds).boxed().toList())
                .thenApply(ids -> {
                    for (Long chatId : ids) {
                        TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(chatId), TdApi.Chat.class);
                        if (chat.title.isBlank()) continue;
                        TdApi.Message lastMessage = chat.lastMessage;
                        TdApi.MessageContent content = lastMessage.content;
                        String msgText = "Unsupported message type.";
                        switch (content.getConstructor()) {
                            case TdApi.MessageText.CONSTRUCTOR -> msgText = ((TdApi.MessageText) content).text.text;
                            case TdApi.MessagePhoto.CONSTRUCTOR -> msgText = ((TdApi.MessagePhoto) content).caption.text;
                            case TdApi.MessageVideo.CONSTRUCTOR -> msgText = ((TdApi.MessageVideo) content).caption.text;
                            default -> {/*do nothing*/}
                        }
                        String photoBase64 = null;
                        TdApi.ChatPhotoInfo photo = chat.photo;
                        if (photo != null) {
                            photoBase64 = getPhotoBase64(photo);
                        }
                        var chatPreview = new ChatPreview(chatId, chat.title, photoBase64, msgText, chat.unreadCount);
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
        small = telegramClient.sendSync(new TdApi.DownloadFile(small.id, 10, 0, 0, true), TdApi.File.class);
        byte[] bytes = Files.readAllBytes(Path.of(small.local.path));
        photoBase64 = Base64.getEncoder().encodeToString(bytes);
        return photoBase64;
    }

}
