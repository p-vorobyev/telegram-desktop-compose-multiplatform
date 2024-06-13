package dev.voroby.client.chat.delete.presentation;

import dev.voroby.client.chat.common.presentation.CommonChatController;
import dev.voroby.client.chat.delete.application.api.DeleteChat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteChatController extends CommonChatController {

    private final DeleteChat deleteChat;

    public DeleteChatController(DeleteChat deleteChat) {
        this.deleteChat = deleteChat;
    }

    @PostMapping(value = "/delete/{chatId}")
    public void deleteChat(@PathVariable("chatId") long chatId) {
        deleteChat.accept(chatId);
    }
}
