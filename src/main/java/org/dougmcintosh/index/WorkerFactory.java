package org.dougmcintosh.index;

import com.google.common.base.Preconditions;
import org.dougmcintosh.util.SynchronizedOutputWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class WorkerFactory implements Closeable {
    private final SynchronizedOutputWriter writer;

    public WorkerFactory(File outputFile) throws IOException {
        Preconditions.checkState(!outputFile.exists(),
                "Output file already exists: " + outputFile.getAbsolutePath());
        this.writer = new SynchronizedOutputWriter(outputFile);
    }

    public Worker newWorker(File sourceFile) {
        return new Worker(writer, sourceFile);
    }

    @Override
    public void close() throws IOException {
        if (this.writer != null) {
            this.writer.close();
        }
    }

    static class Worker implements Callable<Void> {
        private static final Logger logger = LoggerFactory.getLogger(Worker.class);
        private SynchronizedOutputWriter writer;
        private File sourceFile;

        Worker(SynchronizedOutputWriter writer, File sourceFile) {
            this.writer = Preconditions.checkNotNull(writer, "Writer is null.");
            this.sourceFile = Preconditions.checkNotNull(sourceFile, "Source file is null.");
        }

        @Override
        public Void call() throws Exception {
            logger.info("Processing source file {}", sourceFile.getAbsolutePath());
            writer.write(String.format("%s-%s", Thread.currentThread().getName(), sourceFile.getAbsolutePath()));
            return null;
        }
    }
}
