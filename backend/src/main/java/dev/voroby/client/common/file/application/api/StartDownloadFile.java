package dev.voroby.client.common.file.application.api;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.templates.response.Response;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Component
public class StartDownloadFile implements Function<TdApi.File, CompletableFuture<Response<TdApi.File>>> {

    private final TelegramClient telegramClient;

    public StartDownloadFile(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public CompletableFuture<Response<TdApi.File>> apply(TdApi.File file) {
        TdApi.LocalFile localFile = file.local;
        if (!localFile.isDownloadingCompleted && !localFile.isDownloadingActive && localFile.canBeDownloaded) {
            var downloadFile = new TdApi.DownloadFile(file.id, 32, 0, 0, false);
            return telegramClient.sendAsync(downloadFile);
        }
        return CompletableFuture.completedFuture(new Response<>(file, null));
    }
}
