package org.dougmcintosh.index;

import java.util.concurrent.atomic.AtomicLong;

public final class Metrics {
    private static final AtomicLong fileCounter = new AtomicLong(0);
    private static final AtomicLong failureCounter = new AtomicLong(0);

    public static void fileSeen() {
        fileCounter.incrementAndGet();
    }

    public static void failure() {
        failureCounter.incrementAndGet();
    }

    public static long getFilesSeen() {
        return fileCounter.get();
    }

    public static long getFailures() {
        return failureCounter.get();
    }

    private Metrics() {}
}
