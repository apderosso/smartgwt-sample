package com.example.sample.server.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores the Spring {@link ApplicationContext} in a static variables for use by other classes.
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }

}
