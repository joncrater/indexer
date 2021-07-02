package org.dougmcintosh;

public final class IndexingException extends RuntimeException {
    public IndexingException(String message) {
        super(message);
    }
    public IndexingException(Throwable cause) {
        super(cause);
    }

    public IndexingException(String message, Throwable cause) {
        super(message, cause);
    }
}
