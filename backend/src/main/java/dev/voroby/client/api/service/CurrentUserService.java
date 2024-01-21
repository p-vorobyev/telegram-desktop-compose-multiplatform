package dev.voroby.client.api.service;

import dev.voroby.client.api.GetCurrentUser;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class CurrentUserService {

    private final GetCurrentUser getCurrentUser;

    private final AtomicReference<TdApi.User> me = new AtomicReference<>();

    public CurrentUserService(GetCurrentUser getCurrentUser) {
        this.getCurrentUser = getCurrentUser;
    }

    private void checkAndLazyInit() {
        if (me.get() == null) {
            synchronized (CurrentUserService.class) {
                if (me.get() == null) {
                    me.set(getCurrentUser.get());
                }
            }
        }
    }

    public TdApi.User getMe() {
        checkAndLazyInit();
        return me.get();
    }

    public boolean isCurrentUserId(long userId) {
        checkAndLazyInit();
        return me.get().id == userId;
    }

}
