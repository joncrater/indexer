package org.dougmcintosh.index.extract;

import com.google.common.base.Preconditions;
import org.dougmcintosh.index.extract.tika.TikaExtractor;

import java.io.File;

public class StaticPatternExtractFilterTest {

//    @Test
//    public void testExclude() throws Exception {
    public static void main(String... args) {
        ExtractResult result = TikaExtractor.extract(new File(
                "/Users/joncrater/dev/projects/mcintosh/indexer/build/pdf/1John06.pdf"))
                .orElseThrow(() -> new IllegalStateException("Extract result not present."));

        String text = result.getText();
        System.out.println(text);
        Preconditions.checkState(!text.toLowerCase().contains("lilburn"));
    }
}
