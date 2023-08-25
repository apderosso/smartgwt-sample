package com.example.sample.server.spring;

import javax.servlet.ServletContext;

import com.isomorphic.interfaces.ISpringBeanFactory;

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
        return ApplicationContextProvider.getContext();
    }

    @Override
    public Object getBean(final ServletContext servletContext, final String s) {
        return ApplicationContextProvider.getContext().getBean(s);
    }

}
