package dev.voroby.client.chat.markAsRead.presentation;

import dev.voroby.client.chat.common.presentation.CommonChatController;
import dev.voroby.client.chat.markAsRead.application.api.MarkMessagesAsRead;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarkAsReadController extends CommonChatController {

    private final MarkMessagesAsRead markMessagesAsRead;

    public MarkAsReadController(MarkMessagesAsRead markMessagesAsRead) {
        this.markMessagesAsRead = markMessagesAsRead;
    }

    @PostMapping(value = "/markasread/{chatId}")
    public void markMessagesAsRead(@PathVariable("chatId") long chatId) {
        markMessagesAsRead.accept(chatId);
    }
}
