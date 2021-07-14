package org.dougmcintosh.index.extract.lucene;

import com.google.common.base.Preconditions;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.IOException;
import java.io.Reader;

public class CustomAnalyzer extends StopwordAnalyzerBase {

    /**
     * Default maximum allowed token length
     */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

    /**
     * Default minimum allowed token length
     */
    public static final int DEFAULT_MIN_TOKEN_LENGTH = 5;

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

    private int minTokenLength = DEFAULT_MIN_TOKEN_LENGTH;

    /**
     * Builds an analyzer with the given stop words.
     *
     * @param stopWords stop words
     */
    public CustomAnalyzer(CharArraySet stopWords) {
        super(stopWords);
    }

    /**
     * Builds an analyzer with no stop words.
     */
    public CustomAnalyzer() {
        this(CharArraySet.EMPTY_SET);
    }

    /**
     * Builds an analyzer with the stop words from the given reader.
     *
     * @param stopwords Reader to read stop words from
     * @see WordlistLoader#getWordSet(Reader)
     */
    public CustomAnalyzer(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }

    public CustomAnalyzer(CharArraySet stopWords, int minTokenLength) {
        this(stopWords);
        Preconditions.checkState(minTokenLength >= 1, "Min token length must be >= 1.");
        setMinTokenLength(minTokenLength);
    }

    /**
     * Set the max allowed token length.  Tokens larger than this will be chopped
     * up at this token length and emitted as multiple tokens.  If you need to
     * skip such large tokens, you could increase this max length, and then
     * use {@code LengthFilter} to remove long tokens.  The default is
     * {@link CustomAnalyzer#DEFAULT_MAX_TOKEN_LENGTH}.
     */
    public void setMaxTokenLength(int length) {
        maxTokenLength = length;
    }

    /**
     * Set the minimum allowed token length.  Tokens shorter than this will be
     * ignored. The default is
     * {@link CustomAnalyzer#DEFAULT_MIN_TOKEN_LENGTH}.
     */
    public void setMinTokenLength(int length) {
        minTokenLength = length;
    }

    /**
     * Returns the current maximum token length
     *
     * @see #setMaxTokenLength
     */
    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    @Override
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        src.setMaxTokenLength(maxTokenLength);
        TokenStream tokenStream = new LowerCaseFilter(src);
        tokenStream = new StopFilter(tokenStream, stopwords);
        tokenStream = new LengthFilter(tokenStream, minTokenLength, maxTokenLength);
        return new TokenStreamComponents(r -> {
            src.setMaxTokenLength(CustomAnalyzer.this.maxTokenLength);
            src.setReader(r);
        }, tokenStream);
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }
}
