package dev.voroby.client.api;

import dev.voroby.client.dto.ChatPreview;
import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
                ChatPreview preview = getChatLastMessage.apply(upd);
                if (preview != null) previews.add(preview);
            } else if (update instanceof TdApi.UpdateChatTitle upd) {
                previews.add(getChatNewTitle.apply(upd));
            } else if (update instanceof TdApi.UpdateChatPosition upd) {
                previews.add(getChatNewOrder.apply(upd));
            } else if (update instanceof TdApi.UpdateChatReadInbox upd) {
                previews.add(getChatReadInbox.apply(upd));
            } else if (update instanceof TdApi.UpdateNewChat upd) {
                previews.add(getNewChat.apply(upd));
            } else if (update instanceof TdApi.UpdateChatPhoto upd) {
                previews.add(getNewChatPhoto.apply(upd));
            }

        }

        return previews;
    }

}
