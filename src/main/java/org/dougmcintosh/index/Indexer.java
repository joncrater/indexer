package org.dougmcintosh.index;

import com.google.common.base.Preconditions;
import org.dougmcintosh.Crawler;
import org.dougmcintosh.IndexingException;
import org.dougmcintosh.WorkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class Indexer {
    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);
    private static final Pattern FILE_PATTERN = Pattern.compile("(?i).+\\.pdf$");
    private IndexerArgs args;

    private Indexer(IndexerArgs args) {
        this.args = Preconditions.checkNotNull(args);
    }

    public static Indexer with(IndexerArgs args) {
        return new Indexer(args);
    }

    public void index() throws IndexingException {
        try (final WorkManager workMgr = new WorkManager(args.getWorkers())) {
            final Crawler crawler = Crawler.builder()
                    .directories(args.getInputdirs())
                    .filter(file -> file.isDirectory() || file.isFile() && FILE_PATTERN.matcher(file.getName()).matches())
                    .workManager(workMgr)
                    .recurse(args.isRecurse())
                    .build();
            crawler.crawl();
        }
    }
}