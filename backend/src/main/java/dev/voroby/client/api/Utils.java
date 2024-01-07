package dev.voroby.client.api;

import dev.voroby.springframework.telegram.client.TdApi;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {

    private static final String MESSAGE_DISCLAIMER = "Unsupported message type.";

    static String getMessageText(TdApi.Message message) {
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
