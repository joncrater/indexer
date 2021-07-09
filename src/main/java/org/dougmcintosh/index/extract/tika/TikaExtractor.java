package org.dougmcintosh.index.extract.tika;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.dougmcintosh.index.IndexingException;
import org.dougmcintosh.index.extract.ExtractResult;
import org.dougmcintosh.index.extract.lucene.CustomAnalyzer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TikaExtractor {

    public static ExtractResult extract(File sourceFile) throws IndexingException {
        final BodyContentHandler handler = new BodyContentHandler();
        final AutoDetectParser parser = new AutoDetectParser();

        ExtractResult result = null;
        try (final InputStream stream = new FileInputStream(sourceFile)) {
            parser.parse(stream, handler, new Metadata());
            final String rawText = handler.toString().replaceAll("[\\n\\r\\t]", " ");
            result = ExtractResult.of(sourceFile, rawText);
            final CharArraySet stopWords = new CharArraySet(16, true);
            stopWords.addAll(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
            stopWords.add("Lilburn");
            final CustomAnalyzer analyzer = new CustomAnalyzer(stopWords);
            final TokenStream tokenStream = analyzer.tokenStream("text", new InputStreamReader(
                    new ByteArrayInputStream(rawText.getBytes(StandardCharsets.UTF_8))));
            analyzer.close();

            try {
                tokenStream.reset();
                while (tokenStream.incrementToken()) {
                    System.out.println("token: " + tokenStream.getAttribute(CharTermAttribute.class).toString());
                }
                tokenStream.end();
            } finally {
                tokenStream.close();
            }
        } catch (Exception e) {
            throw new IndexingException(e);
        }

        return result;
    }

    private TikaExtractor() {}

}
