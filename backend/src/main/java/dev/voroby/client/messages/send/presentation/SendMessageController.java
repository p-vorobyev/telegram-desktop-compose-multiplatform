package dev.voroby.client.messages.send.presentation;

import dev.voroby.client.messages.send.application.SendMessageService;
import dev.voroby.client.messages.send.dto.NewMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.voroby.client.messages.MessagesConstants.REQUEST_MAPPING_PATH;

@RestController
@RequestMapping(value = REQUEST_MAPPING_PATH)
public class SendMessageController {

    @Autowired
    private SendMessageService sendMessageService;

    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendMessage(@RequestBody NewMessage newMessage) {
        sendMessageService.accept(newMessage);
    }
}
