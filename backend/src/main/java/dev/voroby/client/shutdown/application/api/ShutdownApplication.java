package dev.voroby.client.shutdown.application.api;

import dev.voroby.client.shutdown.dto.ExitCode;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
public class ShutdownApplication implements Consumer<ExitCode> {

    private final ApplicationContext ctx;

    public ShutdownApplication(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void accept(ExitCode exitCode) {
        CompletableFuture.runAsync(() -> SpringApplication.exit(ctx, exitCode::value));
    }
}
