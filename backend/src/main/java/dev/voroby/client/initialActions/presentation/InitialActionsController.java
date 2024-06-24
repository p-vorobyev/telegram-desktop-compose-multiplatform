package dev.voroby.client.initialActions.presentation;

import dev.voroby.client.cache.Caches;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class InitialActionsController {

    @GetMapping(value = "/isChatsLoaded")
    public boolean isChatsLoaded() {
        return Caches.initialApplicationLoadDone.get();
    }
}
