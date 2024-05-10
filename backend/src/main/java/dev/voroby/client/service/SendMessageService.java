package dev.voroby.client.service;

import dev.voroby.client.api.SendMessage;
import dev.voroby.client.dto.NewMessage;
import dev.voroby.client.dto.TdApiMessageContent;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class SendMessageService implements Consumer<NewMessage> {

    private final SendMessage sendMessage;

    public SendMessageService(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Override
    public void accept(NewMessage newMessage) {
        var tdApiMessageContent = switch (newMessage) {
            case NewMessage.TextMessage textMessage ->
                    new TdApiMessageContent(textMessage.chatId(), textMessageContent(textMessage));
        };
        sendMessage.accept(tdApiMessageContent);
    }

    private TdApi.InputMessageContent textMessageContent(NewMessage.TextMessage textMessage) {
        var inputMessageText = new TdApi.InputMessageText();
        var formattedText = new TdApi.FormattedText();
        formattedText.text = textMessage.text();
        inputMessageText.text = formattedText;
        return inputMessageText;
    }
}
