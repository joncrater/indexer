package org.dougmcintosh;

import java.io.File;

public interface CrawlFilter {
    boolean allows(File candidate);
}
