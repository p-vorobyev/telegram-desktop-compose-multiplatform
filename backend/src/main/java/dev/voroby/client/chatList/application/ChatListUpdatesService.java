package dev.voroby.client.chatList.application;

import dev.voroby.client.chatList.application.api.*;
import dev.voroby.client.chatList.dto.ChatPreview;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class ChatListUpdatesService implements Supplier<List<ChatPreview>> {

    @Autowired
    private GetChatLastMessage getChatLastMessage;

    @Autowired
    private GetChatNewTitle getChatNewTitle;

    @Autowired
    private GetChatNewOrder getChatNewOrder;

    @Autowired
    private GetChatReadInbox getChatReadInbox;

    @Autowired
    private GetNewChat getNewChat;

    @Autowired
    private GetNewChatPhoto getNewChatPhoto;

    @Autowired
    private ChatListUpdatesQueue chatListUpdatesQueue;

    @Override
    public List<ChatPreview> get() {
        List<ChatPreview> previews = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            TdApi.Update update = chatListUpdatesQueue.pollIncomingUpdate();
            if (update == null) break;
            switch (update) {
                case TdApi.UpdateChatLastMessage upd -> addPreviewFromUpdate(getChatLastMessage, upd, previews);
                case TdApi.UpdateChatTitle upd -> addPreviewFromUpdate(getChatNewTitle, upd, previews);
                case TdApi.UpdateChatPosition upd -> addPreviewFromUpdate(getChatNewOrder, upd, previews);
                case TdApi.UpdateChatReadInbox upd -> addPreviewFromUpdate(getChatReadInbox, upd, previews);
                case TdApi.UpdateNewChat upd -> addPreviewFromUpdate(getNewChat, upd, previews);
                case TdApi.UpdateChatPhoto upd -> addPreviewFromUpdate(getNewChatPhoto, upd, previews);
                default -> {}
            }
        }

        return previews;
    }

    private <T extends TdApi.Update> void addPreviewFromUpdate(Function<T, ChatPreview> func,
                                                               T update,
                                                               List<ChatPreview> previews) {
        ChatPreview preview = func.apply(update);
        if (preview != null) previews.add(preview);
    }

}
