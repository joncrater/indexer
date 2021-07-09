package org.dougmcintosh.index.crawl;

import java.io.File;

public interface CrawlFilter {
    boolean allows(File candidate);
}
