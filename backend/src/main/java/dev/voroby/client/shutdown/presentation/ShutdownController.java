package dev.voroby.client.shutdown.presentation;

import dev.voroby.client.shutdown.application.api.ShutdownApplication;
import dev.voroby.client.shutdown.dto.ExitCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ShutdownController {

    private final ShutdownApplication shutdownApplication;

    public ShutdownController(ShutdownApplication shutdownApplication) {
        this.shutdownApplication = shutdownApplication;
    }

    @PostMapping(value = "/shutdown")
    public void shutdownApp() {
        shutdownApplication.accept(new ExitCode(0));
    }
}
