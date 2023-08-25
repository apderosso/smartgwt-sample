package com.example.sample.server.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * Gets the current {@link ApplicationContext}.
     *
     * @return {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * Static setter.
     *
     * @param applicationContext {@link ApplicationContext}
     */
    private static void setContext(final ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }

}
