package org.dougmcintosh.index;

public class IndexEntry {
    private String pdf;
    private String audio;
    private String keywords;

    private IndexEntry(String pdf, String audio, String keywords) {
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
