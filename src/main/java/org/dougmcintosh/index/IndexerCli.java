package org.dougmcintosh.index;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Entry point for indexing files contained in a set of directories.
 */
public class IndexerCli {
    private static final Logger logger = LoggerFactory.getLogger(IndexerCli.class);
    private static final String OPT_INPUT_DIR = "i";
    private static final String OPT_OUTPUT_DIR = "o";
    private static final String OPT_STOP_WORDS_PATH = "s";
    private static final String OPT_RECURSE = "r";
    private static final String OPT_WORKERS = "w";

    public static void main(String... args) {
        final Options opts = setupOptions();
        final CommandLineParser parser = new DefaultParser();

        try {
            final CommandLine cli = parser.parse(opts, args);
            logger.debug("Parsed command line.");
            final String[] inputdirPaths = cli.getOptionValues(OPT_INPUT_DIR);
            final String outputdirPath = cli.getOptionValue(OPT_OUTPUT_DIR);
            final Optional<String> stopWordsPath = Optional.ofNullable(cli.getOptionValue(OPT_STOP_WORDS_PATH));
            final boolean isRecursive = cli.hasOption(OPT_RECURSE);
            final int workers = ((Long) cli.getParsedOptionValue(OPT_WORKERS)).intValue();

            final IndexerArgs indexerArgs = IndexerArgs.builder()
                    .inputdirPaths(inputdirPaths)
                    .outputdirPath(outputdirPath)
                    .stopwordsPath(stopWordsPath)
                    .workers(workers)
                    .recurse(isRecursive)
                    .build();
            Indexer.with(indexerArgs).index();
        }
        catch (ParseException e) {
            System.err.println("Failed to parse command line args: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    private static Options setupOptions() {
        Options opts = new Options();
        opts.addOption(Option.builder(OPT_INPUT_DIR)
                .desc("One or more input directories to scan for pdf files.")
                .longOpt("inputDir")
                .required()
                .hasArgs()
                .build());
        opts.addOption(Option.builder(OPT_OUTPUT_DIR)
                .desc("Output directory where index will be written.")
                .longOpt("outputDir")
                .required()
                .hasArg()
                .build());
        opts.addOption(Option.builder(OPT_STOP_WORDS_PATH)
                .desc("Path to file containing stop words, one per line.")
                .longOpt("stopwordsFile")
                .required(false)
                .hasArg()
                .build());
        opts.addOption(Option.builder(OPT_RECURSE)
                .desc("Recursively process provided directory.")
                .longOpt("recurse")
                .hasArg(false)
                .required(false)
                .build());
        opts.addOption(Option.builder(OPT_WORKERS)
                .desc("Number of worker threads that will consume the work queue.")
                .longOpt("workers")
                .required(false)
                .hasArg()
                .type(Number.class)
                .build());
        return opts;
    }
}