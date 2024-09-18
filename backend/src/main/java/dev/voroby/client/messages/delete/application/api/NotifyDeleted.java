package dev.voroby.client.messages.delete.application.api;

import dev.voroby.client.chat.open.application.OpenChatService;
import dev.voroby.client.messages.delete.dto.DeleteMessagesDto;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Component
public class NotifyDeleted implements Consumer<DeleteMessagesDto> {

    private final OpenChatService openChatService;

    public NotifyDeleted(OpenChatService openChatService) {
        this.openChatService = openChatService;
    }

    @Override
    public void accept(DeleteMessagesDto deleteMessagesDto) {
        List<Long> ids = Arrays.stream(deleteMessagesDto.ids()).boxed().toList();
        openChatService.addDeletedMsgIds(deleteMessagesDto.chatId(), ids);
    }
}
