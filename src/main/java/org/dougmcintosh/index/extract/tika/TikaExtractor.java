package org.dougmcintosh.index.extract.tika;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.dougmcintosh.index.IndexingException;
import org.dougmcintosh.index.extract.ExtractResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TikaExtractor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public TikaExtractor() {
    }

    public Optional<ExtractResult> extract(File sourceFile) throws IndexingException {
        Optional<ExtractResult> optResult = Optional.empty();

        try (final InputStream stream = new FileInputStream(sourceFile)) {
            String rawText = extractRawText(stream);

            if (StringUtils.isNotBlank(rawText)) {
                ExtractResult result = ExtractResult.of(sourceFile, rawText);
                optResult = Optional.of(result);
            } else {
                logger.warn("No text extracted from file {}", sourceFile.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new IndexingException(e, sourceFile);
        }

        return optResult;
    }

    private String extractRawText(InputStream stream) throws TikaException, IOException, SAXException {
        final BodyContentHandler handler = new BodyContentHandler(-1 /* disable write limit */);
        final AutoDetectParser parser = new AutoDetectParser();
        parser.parse(stream, handler, new Metadata());
        return handler.toString().replaceAll("[\\n\\r\\t]", " ");
    }

}
