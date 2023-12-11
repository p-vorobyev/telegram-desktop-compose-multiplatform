package dev.voroby.client.api;

import dev.voroby.springframework.telegram.client.TdApi;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Utils {

    public static AtomicBoolean updatesConsumerLocked = new AtomicBoolean(false);

    static String getMessageText(TdApi.Message message) {
        if (message == null) return "";
        TdApi.MessageContent content = message.content;
        String msgText = "Unsupported message type.";
        switch (content.getConstructor()) {
            case TdApi.MessageText.CONSTRUCTOR -> msgText = ((TdApi.MessageText) content).text.text;
            case TdApi.MessagePhoto.CONSTRUCTOR -> msgText = ((TdApi.MessagePhoto) content).caption.text;
            case TdApi.MessageVideo.CONSTRUCTOR -> msgText = ((TdApi.MessageVideo) content).caption.text;
            default -> {}
        }

        return msgText;
    }

    static long mainChatListPositionOrder(TdApi.ChatPosition[] positions) {
        for (TdApi.ChatPosition position: positions) {
            if (position.list instanceof TdApi.ChatListMain) {
                return position.order;
            }
        }
        return -1;
    }

}
