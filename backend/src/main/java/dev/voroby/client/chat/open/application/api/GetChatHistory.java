package dev.voroby.client.chat.open.application.api;

import dev.voroby.client.chat.open.dto.ChatHistoryRequest;
import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component @Slf4j
public class GetChatHistory implements Function<ChatHistoryRequest, List<TdApi.Message>> {

    private final TelegramClient telegramClient;

    public GetChatHistory(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public List<TdApi.Message> apply(ChatHistoryRequest chatHistoryRequest) {
        int pageSizeLimit = chatHistoryRequest.limit();
        List<TdApi.Message> messageList = new ArrayList<>();
        long fromMsgId = chatHistoryRequest.fromMessageId();
        do {
            int queryLimit = pageSizeLimit - messageList.size();
            if (!messageList.isEmpty()) {
                fromMsgId = messageList.getFirst().id;
            }
            var getChatHistory = new TdApi.GetChatHistory(
                    chatHistoryRequest.chatId(),
                    fromMsgId,
                    0,
                    queryLimit,
                    false
            );
            TdApi.Messages messages = telegramClient.send(getChatHistory).getObject().orElseThrow();
            log.debug("Load chat history [chatId: {}, fromMsgId: {}, limit: {}]",
                    chatHistoryRequest.chatId(), fromMsgId, chatHistoryRequest.limit());
            if (messages.messages == null || messages.messages.length == 0) {
                return messageList;
            }
            List<TdApi.Message> messagesBatch = new ArrayList<>();
            for (int i = messages.messages.length - 1; i >= 0; i--) {
                TdApi.Message message = messages.messages[i];
                messagesBatch.add(message);
            }
            messageList.addAll(0, messagesBatch);
        } while (messageList.size() < pageSizeLimit);

        return messageList;
    }

}
