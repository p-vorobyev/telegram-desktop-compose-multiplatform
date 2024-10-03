package dev.voroby.client.chat.convertChatMessage.application.api;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.chat.common.dto.MessageGifAnimationInfo;
import dev.voroby.client.chat.common.dto.UrlContent;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.templates.response.Response;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.function.Function;

@Service
public class GetGifAnimationUrl implements Function<MessageGifAnimationInfo, UrlContent.GifFile> {

    private final TelegramClient telegramClient;

    public GetGifAnimationUrl(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    record GifUrlInfo(String gifAnimationUrl, String fileName) {}

    @Override
    public UrlContent.GifFile apply(MessageGifAnimationInfo messageGifAnimationInfo) {
        var gifUrlInfo = new GifUrlInfo("", "");
        Integer gifAnimationId = Caches.messageIdToGifAnimationIdCache.get(messageGifAnimationInfo.messageId());
        if (gifAnimationId != null) {
            Response<TdApi.File> fileResponse = telegramClient.send(new TdApi.GetFile(gifAnimationId));
            if (fileResponse.object() != null) {
                TdApi.File file = fileResponse.object();
                if (file.local.isDownloadingCompleted) {
                    gifUrlInfo = getGifUrlInfo(file);
                }
            }
        }
        return new UrlContent.GifFile(gifUrlInfo.gifAnimationUrl, gifUrlInfo.fileName);
    }

    private GifUrlInfo getGifUrlInfo(TdApi.File file) {
        String path = getPathWithWindowsOsCheck(file);
        String fileName = new File(path).getName();
        return new GifUrlInfo("file://" + path, fileName);
    }

    private String getPathWithWindowsOsCheck(TdApi.File file) {
        return file.local.path.startsWith("/") ?
                file.local.path :
                "/" + file.local.path.replaceAll("\\\\", "/");
    }
}
