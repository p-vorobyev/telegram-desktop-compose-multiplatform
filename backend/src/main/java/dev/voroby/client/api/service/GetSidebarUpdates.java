package dev.voroby.client.api.service;

import dev.voroby.client.api.*;
import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class GetSidebarUpdates implements Supplier<List<ChatPreview>> {

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
    private UpdatesQueues updatesQueues;

    @Override
    public List<ChatPreview> get() {
        List<ChatPreview> previews = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            TdApi.Update update = updatesQueues.pollIncomingSidebarUpdate();
            if (update == null) break;

            if (update instanceof TdApi.UpdateChatLastMessage upd) {
                addPreviewFromUpdate(getChatLastMessage, upd, previews);
            } else if (update instanceof TdApi.UpdateChatTitle upd) {
                addPreviewFromUpdate(getChatNewTitle, upd, previews);
            } else if (update instanceof TdApi.UpdateChatPosition upd) {
                addPreviewFromUpdate(getChatNewOrder, upd, previews);
            } else if (update instanceof TdApi.UpdateChatReadInbox upd) {
                addPreviewFromUpdate(getChatReadInbox, upd, previews);
            } else if (update instanceof TdApi.UpdateNewChat upd) {
                addPreviewFromUpdate(getNewChat, upd, previews);
            } else if (update instanceof TdApi.UpdateChatPhoto upd) {
                addPreviewFromUpdate(getNewChatPhoto, upd, previews);
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
