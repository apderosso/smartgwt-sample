package com.example.sample.server.spring;

import com.isomorphic.interfaces.ISpringBeanFactory;

import jakarta.servlet.ServletContext;

/**
 * Custom implementation of {@link ISpringBeanFactory} since SmartGWT doesn't correctly look up the Spring context.
 *
 * <br>
 * <br>
 * Requires this setup in server.properties:
 *
 * {@code interfaces.ISpringBeanFactory.implementer:com.example.sample.server.spring.SpringBeanFactory}
 */
public class SpringBeanFactory implements ISpringBeanFactory {

    @Override
    public Object getBeanFactory(final ServletContext servletContext) {
        return ApplicationContextHolder.getContext();
    }

    @Override
    public Object getBean(final ServletContext servletContext, final String s) {
        return ApplicationContextHolder.getContext().getBean(s);
    }

}
