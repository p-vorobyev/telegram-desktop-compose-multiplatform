package dev.voroby.client.schedule;

import dev.voroby.client.api.LoadChats;
import dev.voroby.client.cache.Caches;
import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationState;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TerminationCheckScheduler {

    private final ClientAuthorizationState authorizationState;

    private final ApplicationContext ctx;

    private final LoadChats loadChats;

    private final AtomicLong requestCountCheckPoint = new AtomicLong();

    public TerminationCheckScheduler(ClientAuthorizationState authorizationState,
                                     ApplicationContext ctx,
                                     LoadChats loadChats) {
        this.authorizationState = authorizationState;
        this.ctx = ctx;
        this.loadChats = loadChats;
    }

    @SneakyThrows
    @Scheduled(initialDelay = 10, fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    void checkForTermination() {
        if (authorizationState.haveAuthorization() && loadChats.chatsLoaded()) {
            if (Caches.requestsCount.get() == requestCountCheckPoint.get()) {
                CompletableFuture.runAsync(() -> SpringApplication.exit(ctx, () -> 0));
            } else {
                requestCountCheckPoint.set(Caches.requestsCount.get());
            }
        }
    }

}
