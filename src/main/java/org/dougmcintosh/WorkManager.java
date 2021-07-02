package org.dougmcintosh;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WorkManager implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(WorkManager.class);
    private ExecutorService threadPool;
    private int workers;

    public WorkManager(int workers) {
        Preconditions.checkState(workers >= 1, "Workers must be >= 1.");

        this.workers = workers;
        this.threadPool = Executors.newFixedThreadPool(workers, new ThreadFactory() {
            private int workerIdx;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("index-worker-%d", ++workerIdx));
            }
        });
    }

    public void queueWork(File work) {
        logger.debug("Queueing file {}", work.getAbsolutePath());
    }

    @Override
    public void close() {
        if (!threadPool.isShutdown()) {
            threadPool.shutdown();

            logger.info("Awaiting thread pool shutdown.");

            try {
                threadPool.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread pool interrupted while awaiting worker completion.", e);
                throw new IndexingException(e);
            }
        }
    }
}
