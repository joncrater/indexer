package org.dougmcintosh.index.extract.tika;

import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.dougmcintosh.index.IndexingException;
import org.dougmcintosh.index.extract.ExtractResult;
import org.dougmcintosh.index.extract.lucene.CustomAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TikaExtractor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private CharArraySet stopWords;

    public TikaExtractor(Optional<File> stopwordsFile) {
        Preconditions.checkNotNull(stopwordsFile, "Stop words file is null.");
        this.stopWords = initStopWords(stopwordsFile);
    }

    public Optional<ExtractResult> extract(File sourceFile) throws IndexingException {
        Optional<ExtractResult> optResult = Optional.empty();

        try (final InputStream stream = new FileInputStream(sourceFile)) {
            String rawText = extractRawText(stream);

            if (StringUtils.isNotBlank(rawText)) {
                ExtractResult result = ExtractResult.of(sourceFile, rawText);
                optResult = Optional.of(result);

                try (final CustomAnalyzer analyzer = new CustomAnalyzer(stopWords);
                     final TokenStream tokenStream = analyzer.tokenStream("text",
                             new InputStreamReader(
                                     new ByteArrayInputStream(rawText.getBytes(StandardCharsets.UTF_8))))) {

                    tokenStream.reset();
                    while (tokenStream.incrementToken()) {
                        result.addToken(tokenStream.getAttribute(CharTermAttribute.class).toString());
                    }
                    tokenStream.end();
                }
            }
            else {
                logger.warn("No text extracted from file {}", sourceFile.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new IndexingException(e);
        }

        return optResult;
    }

    private String extractRawText(InputStream stream) throws TikaException, IOException, SAXException {
        final BodyContentHandler handler = new BodyContentHandler(-1 /* disable write limit */);
        final AutoDetectParser parser = new AutoDetectParser();
        parser.parse(stream, handler, new Metadata());
        return handler.toString().replaceAll("[\\n\\r\\t]", " ");
    }

    private CharArraySet initStopWords(Optional<File> stopwordsFile) {
        final CharArraySet stopWords = new CharArraySet(16, true);
        stopWords.addAll(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);

        if (stopwordsFile.isPresent()) {
            final List<String> stopWordsList;
            try {
                stopWordsList = CharStreams.readLines(
                        new InputStreamReader(
                                new FileInputStream(stopwordsFile.get()), StandardCharsets.UTF_8));
                stopWords.addAll(stopWordsList);
            } catch (IOException e) {
                throw new IndexingException("Error reading stopwords file: " + stopwordsFile.get().getAbsolutePath());
            }
        }
        return stopWords;
    }

}
