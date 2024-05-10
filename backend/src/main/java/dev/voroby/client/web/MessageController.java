package dev.voroby.client.web;

import dev.voroby.client.dto.NewMessage;
import dev.voroby.client.service.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/message")
public class MessageController {

    @Autowired
    private SendMessageService sendMessageService;

    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendMessage(@RequestBody NewMessage newMessage) {
        sendMessageService.accept(newMessage);
    }
}
