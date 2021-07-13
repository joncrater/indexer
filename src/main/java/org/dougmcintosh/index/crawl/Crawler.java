package org.dougmcintosh.index.crawl;

import com.google.common.base.Preconditions;
import org.dougmcintosh.index.WorkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

/**
 * Crawl a provided set of directories for files matching a filter.
 * Hand those files to the {@link WorkManager} to be added to the
 * work queue.
 */
public class Crawler {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);
    private Set<File> directories;
    private WorkManager workManager;
    private boolean recurse;
    private CrawlFilter filter;

    private Crawler(Set<File> directories, WorkManager workManager, CrawlFilter filter, boolean recurse) {
        this.directories = Preconditions.checkNotNull(directories, "Directories is null.");
        Preconditions.checkState(!this.directories.isEmpty(), "No crawl directories provided.");
        for (File dir : this.directories) {
            Preconditions.checkState(dir.isDirectory(),
                "Provided directory does not exist or is not a directories: " + dir.getAbsolutePath());
        }
        this.workManager = Preconditions.checkNotNull(workManager, "WorkManager is null.");
        this.filter = Preconditions.checkNotNull(filter);
        this.recurse = recurse;
    }

    /**
     * Crawl the provided directory and hand each matching file to the work manager.
     */
    public void crawl() {
        this.directories.forEach(dir -> {
            logger.trace("Starting crawl of {}", dir.getAbsolutePath());
            File[] candidates = dir.listFiles(f -> filter.allows(f));
            crawlInternal(candidates);
            logger.trace("Completed crawl of {}", dir.getAbsolutePath());
        });
    }

    private void crawlInternal(File[] candidates) {
        for (File candidate : candidates) {
            if (candidate.isDirectory() && recurse) {
                logger.trace("Starting crawl of {}", candidate.getAbsolutePath());
                crawlInternal(candidate.listFiles(f -> filter.allows(f)));
                logger.trace("Completed crawl of {}", candidate.getAbsolutePath());
            } else if (filter.allows(candidate)) {
                workManager.queueWork(candidate);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Set<File> directories;
        private WorkManager workManager;
        // default to no-op filter
        private CrawlFilter filter = f -> true;
        private boolean recurse = true;

        private Builder() {
        }

        public Builder directories(Set<File> directories) {
            this.directories = directories;
            return this;
        }

        public Builder workManager(WorkManager workManager) {
            this.workManager = Preconditions.checkNotNull(workManager, "WorkManager is null.");
            return this;
        }

        public Builder recurse(boolean flag) {
            this.recurse = flag;
            return this;
        }

        public Builder filter(CrawlFilter filter) {
            this.filter = Preconditions.checkNotNull(filter, "CrawlFilter is null.");
            return this;
        }

        public Crawler build() {
            return new Crawler(directories, workManager, filter, recurse);
        }
    }
}
