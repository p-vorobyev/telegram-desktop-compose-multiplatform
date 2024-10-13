package dev.voroby.client.users.application;

import dev.voroby.client.users.application.api.GetCurrentUser;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class CurrentUserService {

    private final GetCurrentUser getCurrentUser;

    private final AtomicReference<TdApi.User> me = new AtomicReference<>();

    public CurrentUserService(GetCurrentUser getCurrentUser) {
        this.getCurrentUser = getCurrentUser;
    }

    private void lazyInit() {
        if (me.get() == null) {
            synchronized (CurrentUserService.class) {
                if (me.get() == null) {
                    me.set(getCurrentUser.get());
                }
            }
        }
    }

    public TdApi.User getMe() {
        lazyInit();
        return me.get();
    }

    public boolean isCurrentUserId(long userId) {
        lazyInit();
        return me.get().id == userId;
    }

}
