package org.dougmcintosh.index;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.dougmcintosh.index.extract.ExtractResult;
import org.dougmcintosh.index.extract.lucene.LuceneWrapper;
import org.dougmcintosh.index.extract.tika.TikaExtractor;
import org.dougmcintosh.util.SynchronizedOutputWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WorkerFactory implements Closeable {
    private final SynchronizedOutputWriter writer;
    private final int minTokenLength;

    public WorkerFactory(SynchronizedOutputWriter writer, int minTokenLength) throws IOException {
        this.writer = Preconditions.checkNotNull(writer, "Writer is null.");
        this.minTokenLength = minTokenLength;
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

    private class Worker implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(Worker.class);
        private final SynchronizedOutputWriter writer;
        private final File sourceFile;
        private final Stopwatch stopwatch;

        Worker(SynchronizedOutputWriter writer, File sourceFile) {
            this.writer = Preconditions.checkNotNull(writer, "Writer is null.");
            this.sourceFile = Preconditions.checkNotNull(sourceFile, "Source file is null.");
            this.stopwatch = Stopwatch.createUnstarted();
        }

        @Override
        public void run() {
            logger.info("Processing source file {}", sourceFile.getAbsolutePath());
            Optional<ExtractResult> extractOpt = TikaExtractor.extract(sourceFile);
            if (extractOpt.isPresent()) {
                stopwatch.start();
                final ExtractResult extraction = extractOpt.get();
                LuceneWrapper.tokenize(extraction, minTokenLength);
                writer.write(
                    IndexEntry.builder()
                        .audio(sourceFile.getName().replaceAll("(?i)\\.pdf$", ".mp3"))
                        .pdf(sourceFile.getName())
                        .keywords(extraction.tokenString())
                        .build());

                if (logger.isTraceEnabled()) {
                    logger.trace("Indexed {} in {} ms.", sourceFile.getAbsolutePath(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
                }
            }
        }
    }
}
