package dev.voroby.client.config.http;

import dev.voroby.client.cache.Caches;
import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationState;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ActivityCheckInterceptor implements HandlerInterceptor {

    private final ClientAuthorizationState authorizationState;

    public ActivityCheckInterceptor(ClientAuthorizationState authorizationState) {
        this.authorizationState = authorizationState;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (authorizationState.haveAuthorization()) {
            Caches.requestsCount.incrementAndGet();
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
