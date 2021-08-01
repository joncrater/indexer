### Intro

This cli generates a lunr (lunrjs.org) index of the PDF files contained in one or more directories provided on the command line. See usage options below.

```
usage: java -cp <classpath> org.dougmcintosh.index.IndexerCli <options>
 -c,--compress               gzip compress generated index.
 -h,--help                   Print usage help.
 -i,--inputdir <arg>         One or more input directories to scan for pdf files.
 -l,--mintokenlength <arg>   Minimum number of characters required for a keyword to be indexed.
 -o,--outputdir <arg>        Output directory where index will be written.
 -p,--pretty                 Pretty print generated json index.
 -r,--recurse                Recursively process provided directory.
 -s,--stopwordsfile <arg>    Path to file containing stop words, one per line.
 -w,--workers <arg>          Number of worker threads that will consume the work queue.
 ```