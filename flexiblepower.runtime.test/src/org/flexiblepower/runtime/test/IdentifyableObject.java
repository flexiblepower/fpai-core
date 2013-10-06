package org.flexiblepower.runtime.test;

import java.util.concurrent.atomic.AtomicInteger;

public class IdentifyableObject {
    private static final AtomicInteger idGenerator = new AtomicInteger();

    private final int id = idGenerator.incrementAndGet();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + id + ")";
    }
}
