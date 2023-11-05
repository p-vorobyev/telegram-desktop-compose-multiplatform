package dev.voroby.client.web;

import dev.voroby.client.api.LoadChats;
import dev.voroby.client.dto.ChatPreview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/client")
public class ClientController {

    @Autowired
    private LoadChats loadChats;

    @GetMapping(value = "/loadCahts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatPreview> loadChats() {
        return loadChats.get();
    }

}
