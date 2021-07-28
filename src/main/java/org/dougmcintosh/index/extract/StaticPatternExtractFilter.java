package org.dougmcintosh.index.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StaticPatternExtractFilter {
    private static final List<Pattern> patterns = new ArrayList<>();

    static {
        patterns.add(Pattern.compile("COPYRIGHT © \\d.*"));
    }

    public static String filter(String input) {
        String result = input;
        for (Pattern p : patterns) {
            result = p.matcher(result).replaceAll("");
        }
        return result;
    }

}
