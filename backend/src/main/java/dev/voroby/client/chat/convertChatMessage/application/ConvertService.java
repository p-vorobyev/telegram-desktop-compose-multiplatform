package dev.voroby.client.chat.convertChatMessage.application;

import dev.voroby.client.chat.common.dto.ChatMessage;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class ConvertService implements Function<List<TdApi.Message>, List<ChatMessage>> {

    private final ChatMessageConverter chatMessageConverter;

    protected ConvertService(ChatMessageConverter chatMessageConverter) {
        this.chatMessageConverter = chatMessageConverter;
    }

    @Override
    public List<ChatMessage> apply(List<TdApi.Message> messages) {
        return messages.stream().map(chatMessageConverter).toList();
    }

}
