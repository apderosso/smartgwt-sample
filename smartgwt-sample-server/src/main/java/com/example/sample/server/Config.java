package com.example.sample.server;

import java.util.Map;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Spring configuration.
 */
@Configuration
@ImportResource("classpath:applicationContext-security.xml")
public class Config {

    /**
     * Embedded HSQLDB data source.
     * 
     * @return {@link HikariDataSource}
     */
    @Bean
    public DataSource dataSource() {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setJdbcUrl("jdbc:hsqldb:hsql://localhost/isomorphic");
        dataSource.setUsername("sa");
        return dataSource;
    }

    /**
     * Message source for internationalization.
     * 
     * @return {@link MessageSource}
     */
    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("com/example/sample/shared/Messages");
        return messageSource;
    }

    /**
     * Task executor for asynchronous tasks.
     * 
     * @return {@link Executor}
     */
    @Bean
    public Executor taskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        executor.setTaskDecorator(taskDecorator());
        return executor;
    }

    /**
     * Task decorator that copies the {@link RequestAttributes}, {@link LocaleContext}, {@link SecurityContext} and MDC context map from the calling thread to
     * the task thread.
     * 
     * @return {@link TaskDecorator}
     */
    @Bean
    public TaskDecorator taskDecorator() {
        return runnable -> {
            final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            final LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
            final SecurityContext securityContext = SecurityContextHolder.getContext();
            final Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(requestAttributes, true);
                    LocaleContextHolder.setLocaleContext(localeContext, true);
                    SecurityContextHolder.setContext(securityContext);
                    if (mdcContextMap != null) {
                        MDC.setContextMap(mdcContextMap);
                    }
                    runnable.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                    LocaleContextHolder.resetLocaleContext();
                    SecurityContextHolder.clearContext();
                    MDC.clear();
                }
            };
        };
    }

}
