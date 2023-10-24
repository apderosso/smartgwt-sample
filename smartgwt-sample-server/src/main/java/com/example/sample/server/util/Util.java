package com.example.sample.server.util;

import java.lang.reflect.Proxy;
import java.util.Collection;

import com.example.sample.shared.Messages;

/**
 * Server side utility methods.
 */
public final class Util {

    private static final Messages MESSAGES = (Messages) Proxy.newProxyInstance(Messages.class.getClassLoader(), new Class[] { Messages.class },
            new MessagesHandler());

    private Util() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns the first element of the collection or null if the collection is null or empty.
     * 
     * @param collection the collection
     * @return the first element of the collection or null if the collection is null or empty
     * @param <T> the type of elements in the collection
     */
    public static <T> T getFirst(final Collection<T> collection) {
        return (collection == null || collection.isEmpty()) ? null : collection.iterator().next();
    }

    /**
     * Returns the {@link Messages} instance.
     * 
     * @return {@link Messages}
     */
    public static Messages messages() {
        return MESSAGES;
    }

}
