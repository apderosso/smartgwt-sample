package com.example.sample.server.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.example.sample.server.spring.ApplicationContextHolder;

public class MessagesHandler implements InvocationHandler {

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        return ApplicationContextHolder.getContext().getBean(MessageSource.class).getMessage(method.getName(), args, LocaleContextHolder.getLocale());
    }

}
