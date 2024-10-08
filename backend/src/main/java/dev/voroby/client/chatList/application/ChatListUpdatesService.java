package dev.voroby.client.chatList.application;

import dev.voroby.client.chatList.application.api.*;
import dev.voroby.client.chatList.dto.ChatPreview;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class ChatListUpdatesService implements Supplier<List<ChatPreview>> {

    private final GetChatLastMessage getChatLastMessage;

    private final GetChatNewTitle getChatNewTitle;

    private final GetChatNewOrder getChatNewOrder;

    private final GetChatReadInbox getChatReadInbox;

    private final GetNewChat getNewChat;

    private final GetNewChatPhoto getNewChatPhoto;

    private final ChatListUpdatesQueue chatListUpdatesQueue;

    public ChatListUpdatesService(GetChatLastMessage getChatLastMessage, GetChatNewTitle getChatNewTitle, GetChatNewOrder getChatNewOrder, GetChatReadInbox getChatReadInbox, GetNewChat getNewChat, GetNewChatPhoto getNewChatPhoto, ChatListUpdatesQueue chatListUpdatesQueue) {
        this.getChatLastMessage = getChatLastMessage;
        this.getChatNewTitle = getChatNewTitle;
        this.getChatNewOrder = getChatNewOrder;
        this.getChatReadInbox = getChatReadInbox;
        this.getNewChat = getNewChat;
        this.getNewChatPhoto = getNewChatPhoto;
        this.chatListUpdatesQueue = chatListUpdatesQueue;
    }

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
