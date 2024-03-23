package dev.voroby.client.config;

import dev.voroby.client.config.http.ActivityCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final ActivityCheckInterceptor activityCheckInterceptor;

    public WebConfiguration(ActivityCheckInterceptor activityCheckInterceptor) {
        this.activityCheckInterceptor = activityCheckInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(activityCheckInterceptor);
    }

}
