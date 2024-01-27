package dev.voroby.client.api;

import dev.voroby.client.dto.ChatHistoryRequest;
import dev.voroby.client.updates.queue.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component @Slf4j
public class GetChatHistory extends AbstractUpdates implements Function<ChatHistoryRequest, List<TdApi.Message>> {

    protected GetChatHistory(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        super(updatesQueues, telegramClient);
    }

    @Override
    public List<TdApi.Message> apply(ChatHistoryRequest chatHistoryRequest) {
        List<TdApi.Message> messageList = new ArrayList<>();
        long fromMsgId = chatHistoryRequest.fromMessageId();
        do {
            int limit = 100 - messageList.size();
            if (!messageList.isEmpty()) {
                fromMsgId = messageList.get(0).id;
            }
            var getChatHistory = new TdApi.GetChatHistory(chatHistoryRequest.chatId(), fromMsgId, chatHistoryRequest.offset(), limit, false);
            TdApi.Messages messages = telegramClient.sendSync(getChatHistory);
            log.info("Load chat history [chatId: {}, fromMsgId: {}, offset: {}]",
                    chatHistoryRequest.chatId(), fromMsgId, chatHistoryRequest.offset());
            if (messages.messages == null || messages.messages.length == 0) {
                return messageList;
            }
            List<TdApi.Message> messagesBatch = new ArrayList<>();
            for (int i = messages.messages.length - 1; i >= 0; i--) {
                messagesBatch.add(messages.messages[i]);
            }
            messageList.addAll(0, messagesBatch);
        } while (messageList.size() < 100);

        return messageList;
    }

}
