package org.dougmcintosh.index;

import com.google.common.base.Preconditions;
import org.dougmcintosh.index.crawl.Crawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LunrIndexer {
    private static final Logger logger = LoggerFactory.getLogger(LunrIndexer.class);
    private static final Pattern FILE_PATTERN = Pattern.compile("(?i).+\\.pdf$");
    private static final String TIME_PATTERN = "YYYYMMDDHHmmss";
    private IndexerArgs args;

    private LunrIndexer(IndexerArgs args) {
        this.args = Preconditions.checkNotNull(args);
    }

    public static LunrIndexer with(IndexerArgs args) {
        return new LunrIndexer(args);
    }

    public void index() throws IOException {
        final File outputFile = new File(args.getOutputdir(), timestampedFileName());
        final WorkerFactory workerFactory = new WorkerFactory(outputFile);

        try (final WorkManager workMgr = new WorkManager(args.getWorkers(), workerFactory)) {
            Crawler.builder()
                    .directories(args.getInputdirs())
                    .filter(file -> file.isDirectory() || file.isFile() && FILE_PATTERN.matcher(file.getName()).matches())
                    .workManager(workMgr)
                    .recurse(args.isRecurse())
                    .build()
                    .crawl();
        }
    }

    private String timestampedFileName() {
        return String.format(
                "lunr-%s.js",
                DateTimeFormatter.ofPattern(TIME_PATTERN).format(LocalDateTime.now()));
    }
}
