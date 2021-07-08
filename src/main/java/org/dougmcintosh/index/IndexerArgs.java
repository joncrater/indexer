package org.dougmcintosh.index;

import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IndexerArgs {
    private Set<File> inputdirs;
    private File outputdir;
    private File stopwordsFile;
    private boolean recurse;
    private int workers;

    private IndexerArgs(Set<String> inputDirPaths,
                        String outputdirPath,
                        Optional<String> stopwordsPath,
                        boolean recurse,
                        int workers) {
        Preconditions.checkState(CollectionUtils.isNotEmpty(inputDirPaths), "Input dir paths is null/empty.");
        Preconditions.checkState(StringUtils.isNotBlank(outputdirPath), "outputdirPath is null/blank.");

        initInputDirs(inputDirPaths);
        initOutputDir(outputdirPath);
        initStopWordsFile(stopwordsPath);
        Preconditions.checkState(workers >= 1, "Workers must be >= 1.");
        this.recurse = recurse;
        this.workers = workers;
    }

    private void initInputDirs(Set<String> inputDirPaths) {
        this.inputdirs = inputDirPaths.stream().map(
                s -> new File(s)).collect(Collectors.toCollection(LinkedHashSet::new));

        this.inputdirs.stream().forEach(d ->
            Preconditions.checkState(d.isDirectory(),
                    "Input directory does not exist or isn't a directory: " + d.getAbsolutePath()));
    }

    private void initOutputDir(String outputdirPath) {
        this.outputdir = new File(outputdirPath);
        Preconditions.checkState(outputdir.isDirectory(),
                "Output directory does not exist or isn't a directory: " + outputdir.getAbsolutePath());
    }

    private void initStopWordsFile(Optional<String> stopwordsPath) {
        if (stopwordsPath.isPresent()) {
            this.stopwordsFile = new File(stopwordsPath.get());
            Preconditions.checkState(stopwordsFile.isFile(), "Stopwords path doesn't exist or isn't a file.;");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<File> getInputdirs() {
        return inputdirs;
    }

    public File getOutputdir() {
        return outputdir;
    }

    public File getStopwordsFile() {
        return stopwordsFile;
    }

    public boolean isRecurse() {
        return recurse;
    }

    public int getWorkers() {
        return workers;
    }

    public static class Builder {
        private Set<String> inputdirPaths;
        private String outputdirPath;
        private Optional<String> stopwordsPath;
        private boolean recurse = true;
        private Integer workers = 10;

        public Builder inputdirPaths(String[] inputdirPaths) {
            if (ArrayUtils.isNotEmpty(inputdirPaths)) {
                this.inputdirPaths = new LinkedHashSet<>(Arrays.asList(inputdirPaths));
            }
            return this;
        }

        public Builder outputdirPath(String outputdirPath) {
            this.outputdirPath = outputdirPath;
            return this;
        }

        public Builder stopwordsPath(Optional<String> stopwordsPath) {
            this.stopwordsPath = stopwordsPath;
            return this;
        }

        public Builder recurse(boolean recurse) {
            this.recurse = recurse;
            return this;
        }

        public Builder workers(int workers) {
            this.workers = workers;
            return this;
        }

        public IndexerArgs build() {
            return new IndexerArgs(inputdirPaths, outputdirPath, stopwordsPath, recurse, workers);
        }
    }
}
