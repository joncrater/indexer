package org.dougmcintosh.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.google.common.base.Preconditions;
import org.dougmcintosh.index.IndexEntry;
import org.dougmcintosh.index.IndexingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;

/**
 * Utility class providing for proper locking of a single writer via a {@link ReentrantLock}.
 */
public class SynchronizedOutputWriter implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedOutputWriter.class);
    private final File outputFile;
    private final SequenceWriter sequenceWriter;
    private final ReentrantLock writeLock;

    public SynchronizedOutputWriter(File outputFile, boolean compress) throws IOException {
        Preconditions.checkNotNull(outputFile, "Output file is null.");
        Preconditions.checkState(!outputFile.exists(),
            "Output file already exists: " + outputFile.getAbsolutePath());
        this.outputFile = outputFile;
        this.writeLock = new ReentrantLock();
        ObjectMapper mapper = new ObjectMapper();
//        ObjectWriter jsonWriter = mapper.writer().withDefaultPrettyPrinter();
        ObjectWriter jsonWriter = mapper.writer();
        OutputStream outStream = compress ?
            new GZIPOutputStream(new FileOutputStream(outputFile)) :
            new FileOutputStream(outputFile);
        sequenceWriter = jsonWriter.writeValues(outStream);
        sequenceWriter.init(true /* wrap in array */);
        logger.info("Initialized output writer on {}", outputFile.getAbsolutePath());
    }

    public void write(final IndexEntry entry) throws IndexingException {
        try {
            writeLock.lock();
            logger.debug("Attempting to write \"{}\"", entry);
            sequenceWriter.write(entry);
        } catch (Exception e) {
            logger.error("Worker threw exception.", e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.sequenceWriter.close();
    }
}
