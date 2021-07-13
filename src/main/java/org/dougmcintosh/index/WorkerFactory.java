package org.dougmcintosh.index;

import com.google.common.base.Preconditions;
import org.dougmcintosh.index.extract.ExtractResult;
import org.dougmcintosh.index.extract.tika.TikaExtractor;
import org.dougmcintosh.util.SynchronizedOutputWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class WorkerFactory implements Closeable {
    private final SynchronizedOutputWriter writer;
    private final Optional<File> stopwordsFile;

    public WorkerFactory(File outputFile, Optional<File> stopwordsFile) throws IOException {
        Preconditions.checkState(!outputFile.exists(),
            "Output file already exists: " + outputFile.getAbsolutePath());
        this.writer = new SynchronizedOutputWriter(outputFile);
        this.stopwordsFile = stopwordsFile;
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

    class Worker implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(Worker.class);
        private SynchronizedOutputWriter writer;
        private File sourceFile;

        Worker(SynchronizedOutputWriter writer, File sourceFile) {
            this.writer = Preconditions.checkNotNull(writer, "Writer is null.");
            this.sourceFile = Preconditions.checkNotNull(sourceFile, "Source file is null.");
        }

        @Override
        public void run() {
            logger.info("Processing source file {}", sourceFile.getAbsolutePath());
            Optional<ExtractResult> extraction = new TikaExtractor(stopwordsFile).extract(sourceFile);
            if (extraction.isPresent()) {
                writer.write(extraction.get().tokenString());
//            writer.write(String.format("%s-%s", Thread.currentThread().getName(), sourceFile.getAbsolutePath()));
            }
        }
    }
}
