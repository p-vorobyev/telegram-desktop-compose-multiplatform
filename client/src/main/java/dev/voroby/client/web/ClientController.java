package dev.voroby.client.web;

import dev.voroby.client.api.*;
import dev.voroby.client.dto.ChatPreview;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/client")
public class ClientController {

    @Autowired
    private LoadChats loadChats;

    @Autowired
    private TelegramClient telegramClient;

    @Autowired
    private GetChatLastMessage getChatLastMessage;

    @Autowired
    private GetChatNewTitle getChatNewTitle;

    @Autowired
    private GetChatNewOrder getChatNewOrder;

    @Autowired
    private GetChatReadInbox getChatReadInbox;

    @GetMapping(value = "/loadChats", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatPreview> loadChats() {
        return loadChats.get();
    }

    @GetMapping(value = "/updateSidebar", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatPreview> updateSidebar() {
        Map<Long, ChatPreview> updatedPreviews = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            ChatPreview lastMsgChanged = getChatLastMessage.get();
            ChatPreview newChatTitle = getChatNewTitle.get();
            ChatPreview chatNewOrder = getChatNewOrder.get();
            ChatPreview chatReadInbox = getChatReadInbox.get();
            if (lastMsgChanged == null && newChatTitle == null && chatNewOrder == null && chatReadInbox != null) break;
            if (lastMsgChanged != null) computeUpdatedPreviews(updatedPreviews, lastMsgChanged);
            if (newChatTitle != null) computeUpdatedPreviews(updatedPreviews, newChatTitle);
            if (chatNewOrder != null) computeUpdatedPreviews(updatedPreviews, chatNewOrder);
            if (chatReadInbox != null) computeUpdatedPreviews(updatedPreviews, chatReadInbox);
        }

        return new ArrayList<>(updatedPreviews.values());
    }

    private void computeUpdatedPreviews(Map<Long, ChatPreview> updatedPreviews, ChatPreview chatPreview) {
        updatedPreviews.compute(chatPreview.id(), (key, value) -> {
            if (value == null) {
                value = chatPreview;
            }
            return value;
        });
    }

    @GetMapping("/sendHello")
    public void helloToYourself() {
        telegramClient.sendAsync(new TdApi.GetMe())
                .thenApply(user -> user.usernames.activeUsernames[0])
                .thenApply(username -> telegramClient.sendAsync(new TdApi.SearchChats(username, 1)))
                .thenCompose(chatsFuture ->
                        chatsFuture.thenApply(chats -> chats.chatIds[0]))
                .thenApply(chatId -> telegramClient.sendAsync(sendMessageQuery(chatId)));
    }

    private TdApi.SendMessage sendMessageQuery(Long chatId) {
        var content = new TdApi.InputMessageText();
        var formattedText = new TdApi.FormattedText();
        formattedText.text = "Hello!";
        content.text = formattedText;
        return new TdApi.SendMessage(chatId, 0, null, null, null, content);
    }

}
