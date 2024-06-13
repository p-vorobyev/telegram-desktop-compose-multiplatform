package dev.voroby.client.shutdown.application.api;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Component
public class ShutdownApplication implements Supplier<CompletableFuture<Void>> {

    private final ApplicationContext ctx;

    public ShutdownApplication(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public CompletableFuture<Void> get() {
        return CompletableFuture.runAsync(() -> SpringApplication.exit(ctx, () -> 0));
    }
}
