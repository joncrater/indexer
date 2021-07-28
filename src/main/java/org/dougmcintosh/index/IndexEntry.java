package org.dougmcintosh.index;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public class IndexEntry {
    private String pdf;
    private String audio;
    private String keywords;

    private IndexEntry(String pdf, String audio, String keywords) {
        Preconditions.checkState(StringUtils.isNotBlank(pdf), "PDF is null/empty.");
        this.pdf = pdf;
        this.audio = audio;
        this.keywords = keywords;
    }

    public String getPdf() {
        return pdf;
    }

    public String getAudio() {
        return audio;
    }

    public String getKeywords() {
        return keywords;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return pdf;
    }

    public static class Builder {
        private String pdf;
        private String audio;
        private String keywords;

        public Builder pdf(String pdf) {
            this.pdf = pdf;
            return this;
        }

        public Builder audio(String audio) {
            this.audio = audio;
            return this;
        }

        public Builder keywords(String keywords) {
            this.keywords = keywords;
            return this;
        }

        public IndexEntry build() {
            return new IndexEntry(pdf, audio, keywords);
        }
    }
}
