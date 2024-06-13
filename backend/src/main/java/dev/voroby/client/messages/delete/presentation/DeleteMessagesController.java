package dev.voroby.client.messages.delete.presentation;

import dev.voroby.client.messages.delete.application.api.DeleteMessages;
import dev.voroby.client.messages.delete.dto.DeleteMessagesDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.voroby.client.messages.MessagesConstants.REQUEST_MAPPING_PATH;

@RestController
@RequestMapping(value = REQUEST_MAPPING_PATH)
public class DeleteMessagesController {

    private final DeleteMessages deleteMessages;

    public DeleteMessagesController(DeleteMessages deleteMessages) {
        this.deleteMessages = deleteMessages;
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteMessages(@RequestBody DeleteMessagesDto deleteMessagesDto) {
        deleteMessages.accept(deleteMessagesDto);
    }
}
