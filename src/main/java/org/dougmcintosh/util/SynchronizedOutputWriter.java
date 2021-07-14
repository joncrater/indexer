package org.dougmcintosh.util;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.dougmcintosh.index.IndexingException;
import org.dougmcintosh.index.extract.ExtractResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility class providing for proper locking of a single writer via a {@link ReentrantLock}.
 */
public class SynchronizedOutputWriter implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedOutputWriter.class);
    private final File outputFile;
    private final BufferedWriter writer;
    private final ReentrantLock writeLock;

    public SynchronizedOutputWriter(File outputFile) throws IOException {
        Preconditions.checkNotNull(outputFile, "Output file is null.");
        Preconditions.checkState(!outputFile.exists(),
            "Output file already exists: " + outputFile.getAbsolutePath());
        this.outputFile = outputFile;
        this.writer = new BufferedWriter(new FileWriter(outputFile));
        this.writeLock = new ReentrantLock();
        logger.info("Initialized output writer on {}", outputFile.getAbsolutePath());
    }

    public void write(final String str) throws IndexingException {
        if (StringUtils.isNotBlank(str)) {
            try {
                writeLock.lock();
                logger.debug("Attempting to write \"{}\"", str);
                writer.write(str);
                writer.newLine();
            } catch (Exception e) {
                logger.error("Worker threw exception.", e);
            } finally {
                writeLock.unlock();
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
