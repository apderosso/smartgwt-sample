package com.example.sample.server.util;

import java.lang.reflect.Proxy;
import java.util.Collection;

import com.example.sample.shared.Messages;

public final class Util {

    private static final Messages MESSAGES = (Messages) Proxy.newProxyInstance(Messages.class.getClassLoader(), new Class[] { Messages.class },
            new MessagesHandler());

    private Util() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> T getFirst(final Collection<T> collection) {
        return (collection == null || collection.isEmpty()) ? null : collection.iterator().next();
    }

    public static Messages messages() {
        return MESSAGES;
    }

}
