package dev.voroby.client.messages.delete.application;

import dev.voroby.client.messages.delete.application.api.DeleteMessages;
import dev.voroby.client.messages.delete.application.api.NotifyDeleted;
import dev.voroby.client.messages.delete.dto.DeleteMessagesDto;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class DeleteMessagesService implements Consumer<DeleteMessagesDto> {

    private final DeleteMessages deleteMessages;

    private final NotifyDeleted notifyDeleted;

    public DeleteMessagesService(DeleteMessages deleteMessages, NotifyDeleted notifyDeleted) {
        this.deleteMessages = deleteMessages;
        this.notifyDeleted = notifyDeleted;
    }

    @Override
    public void accept(DeleteMessagesDto deleteMessagesDto) {
        deleteMessages
                .andThen(notifyDeleted)
                .accept(deleteMessagesDto);
    }
}
