package dev.voroby.client.shutdown.application;

import dev.voroby.client.cache.Caches;
import dev.voroby.client.shutdown.application.api.ShutdownApplication;
import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationState;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TerminationCheckScheduler {

    private final ClientAuthorizationState authorizationState;

    private final ShutdownApplication shutdownApplication;

    private final AtomicLong requestCountCheckPoint = new AtomicLong();

    private final AtomicLong awaitCount = new AtomicLong();

    public TerminationCheckScheduler(ClientAuthorizationState authorizationState,
                                     ShutdownApplication shutdownApplication) {
        this.authorizationState = authorizationState;
        this.shutdownApplication = shutdownApplication;
    }

    @SneakyThrows
    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    void checkForTermination() {
        if (authorizationState.haveAuthorization() && Caches.initialApplicationLoadDone.get()) {
            if (Caches.requestsCount.get() == requestCountCheckPoint.get()) {
                if (awaitCount.incrementAndGet() > 3) {
                    shutdownApplication.get();
                }
            } else {
                requestCountCheckPoint.set(Caches.requestsCount.get());
                awaitCount.set(0);
            }
        }
    }

}
