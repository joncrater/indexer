package org.dougmcintosh.index;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkManager implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(WorkManager.class);
    private ExecutorService threadPool;
    private WorkerFactory workerFactory;
    private AtomicBoolean failureDetected;

    public WorkManager(int workers, WorkerFactory workerFactory) {
        Preconditions.checkState(workers >= 1, "Workers must be >= 1.");
        this.failureDetected = new AtomicBoolean(false);
        this.workerFactory = Preconditions.checkNotNull(workerFactory, "WorkerFactory is null.");
        this.threadPool = Executors.newFixedThreadPool(workers, new ThreadFactory() {
            private AtomicInteger workerIdx = new AtomicInteger(0);
            private Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.error("Uncaught exception in worker {}", t.getName(), e);
                    failureDetected.set(true);
                }
            };

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, String.format("index-worker-%d", workerIdx.incrementAndGet()));
                t.setUncaughtExceptionHandler(exceptionHandler);
                return t;
            }
        });
    }

    public void queueWork(File work) {
        logger.debug("Queueing file {}", work.getAbsolutePath());
        threadPool.execute(workerFactory.newWorker(work));
    }

    @Override
    public void close() throws IOException {
        if (!threadPool.isShutdown()) {
            threadPool.shutdown();

            logger.info("Awaiting thread pool shutdown.");

            try {
                threadPool.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread pool interrupted while awaiting worker completion.", e);
                throw new IndexingException(e);
            } finally {
                this.workerFactory.close();
            }

            if (failureDetected.get()) {
                throw new IndexingException("One or more worker threads failed. Check logs for details.");
            }

            logger.info("Thread pool shutdown complete.");
        }
    }
}