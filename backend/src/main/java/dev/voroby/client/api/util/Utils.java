package dev.voroby.client.api.util;

import dev.voroby.springframework.telegram.client.TdApi;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Slf4j
public class Utils {

    private static final String MESSAGE_DISCLAIMER = "Unsupported message type.";

    public static String getMessageText(TdApi.Message message) {
        if (message == null) return "";
        TdApi.MessageContent content = message.content;
        String msgText = MESSAGE_DISCLAIMER;
        switch (content.getConstructor()) {
            case TdApi.MessageText.CONSTRUCTOR -> msgText = ((TdApi.MessageText) content).text.text;
            case TdApi.MessagePhoto.CONSTRUCTOR -> msgText = ((TdApi.MessagePhoto) content).caption.text;
            case TdApi.MessageVideo.CONSTRUCTOR -> msgText = ((TdApi.MessageVideo) content).caption.text;
            case TdApi.MessageAnimation.CONSTRUCTOR -> msgText = ((TdApi.MessageAnimation) content).caption.text;
            case TdApi.MessageDocument.CONSTRUCTOR -> msgText = ((TdApi.MessageDocument) content).caption.text;
            default -> {}
        }
        if (msgText.isBlank()) {
            msgText = MESSAGE_DISCLAIMER;
        }

        return msgText.trim();
    }

    public static long mainChatListPositionOrder(TdApi.ChatPosition[] positions) {
        for (TdApi.ChatPosition position: positions) {
            if (position.list instanceof TdApi.ChatListMain) {
                return position.order;
            }
        }
        return -1;
    }

    public static void logError(TdApi.Error error) {
        String errorLogString = String.format("TDLib error:\n[\ncode: %d,\nmessage: %s\n]\n", error.code, error.message);
        log.error(errorLogString);
    }

    @SneakyThrows()
    public static String fileBase64Encode(String path) {
        byte[] bytes = Files.readAllBytes(Path.of(path));
        return Base64.getEncoder().encodeToString(bytes);
    }

}
