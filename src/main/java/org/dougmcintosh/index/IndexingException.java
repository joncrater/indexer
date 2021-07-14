package org.dougmcintosh.index;

import java.io.File;

public final class IndexingException extends RuntimeException {
    private File target;

    public IndexingException(String message) {
        super(message);
    }

    public IndexingException(Throwable cause, File target) {
        this(cause);
        this.target = target;
    }

    public IndexingException(Throwable cause) {
        super(cause);
    }

    public IndexingException(String message, Throwable cause) {
        super(message, cause);
    }

    public File getTarget() {
        return this.target;
    }
}
