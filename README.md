## Intro

This cli crawls a given set of input directories (recursively if `-r|--recurse` is provided), extracts the text from any PDF files found and generates a lunrjs (https://lunrjs.com/) compatible index containing an entry for each PDF file encounted. The index entry will contain any keywords not found in the optional stopwords file provided by the `-s|--stopwords` option. If no stopwords file is provided, the default Lucene stopwords will be used. See full list of options below.

## Options

```
 -c,--compress               gzip compress generated index.
 -h,--help                   Print usage help.
 -i,--inputdir <arg>         One or more input directories to scan for pdf files.
 -l,--mintokenlength <arg>   Minimum number of characters required for a keyword to be indexed.
 -o,--outputdir <arg>        Output directory (must exist) where index will be written.
 -p,--pretty                 Pretty print generated json index.
 -r,--recurse                Recursively process provided directory.
 -s,--stopwordsfile <arg>    Path to file containing stop words, one per line. Any word in this file will be ignored for indexing.
 -w,--workers <arg>          Number of worker threads that will consume files from the work queue.
 ```
 
## Logging

Logging is handled with log4j2 and configured with the `./src/main/resources/log4j2.yaml` file. By default logging is written to `var/log/indexer.log` relative to the project root.
 
## Build
 
 The project is built with gradle. Build a distribution tarball with `./gradlew distTar`. This will generate a tarball of the project in `build/distributions`.
 
## Run

Java 16 is required to build and run this project. https://openjdk.java.net/projects/jdk/16/

### In Place With Gradle

Run in place without building a tarball by executing `./gradlew run --args='[args]'` from the project root. For example:

```
./gradlew run --args='--pretty --recurse --compress --workers 4 --inputdir /Users/jon/dev/projects/mcintosh/www/pdf --outputdir /Users/jon/dev/projects/mcintosh/indexer/build --stopwordsfile /Users/jon/dev/projects/mcintosh/indexer/var/conf/stopwords.txt'
```

### From Extracted Distribution

For more control, run from an extracted tarball distribution.

* Build a distribution with `./gradlew clean distTar`. The tarball will be written to `build/distributions/mcintosh-indexer.tgz`.
* Extract the distribution to a deployment directory of your choice with `tar xvfz ./build/distributions/mcintosh-indexer.tgz -C /path/to/deploydir`
* `cd /path/to/deploydir/mcintosh-indexer`
* `./bin/indexer --pretty --recurse --compress --workers 4 --inputdir /Users/jon/dev/projects/mcintosh/www/pdf --outputdir /Users/jon/dev/projects/mcintosh/indexer/build --stopwordsfile /Users/jon/dev/projects/mcintosh/indexer/var/conf/stopwords.txt`