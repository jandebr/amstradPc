The *Amstrad PC* application requires the `javacpc.ini` configuration file from *JavaCPC* although it can be extended


## Syntax

The syntax of the `javacpc.ini` configuration file continues to follow the Java [Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-) `key = value` line format



## Specify the config file

By default, *Amstrad PC* will look for the file `javacpc.ini` in the *current user folder* of the Java VM. This can be overridden at startup by the system property `javacpc-ini-read` as follows
```
java -Djavacpc-ini-read="javacpc.ini" ...
```

By default, any *modifications* during the application run are in-memory only and not persisted back to the file. This can be overridden by explicitely specifying an *output configuration file* via the system property `javacpc-ini-write`. Typically, this will point to the same file so that changes during one application run are persisted and carried over to the next application run

```
java -Djavacpc-ini-read="javacpc.ini" -Djavacpc-ini-write="javacpc.ini" ...
```

> Note: The [distribution launcher](Distribute-using-getdown#run-a-distribution) follows this paradigm



## Startup overrides

One can override a configuration value in the startup command by setting a system property `javacpc.<key>=<value>`. For example to start the *Amstrad PC* in [kiosk mode](Run-modes#kiosk-mode) with audio muted, one could use

```
java -Djavacpc-ini-read="javacpc.ini" -Djavacpc.mode="KIOSK" -Djavacpc.audio="false" ...
```

> Note: Startup overrides also persist to the *output configuration file*, when defined



## Settings

Documented below are the settings (keys) that were added to support the extra [features](Features) of *Amstrad PC*. To remain backwards compatible with older versions of `javacpc.ini` (for instance older [client distributions](Distribute-using-getdown)), the application falls back on default values presented below between parenthesis after the key

> Note: Values that are file or folder paths can be either *absolute* or *relative*. When *relative* they are resolved against the *current user folder* of the Java VM



### General settings

A few general settings

- `mode` (*DEFAULT*) The [run mode](Run-modes) of the application as either *DEFAULT*, *KIOSK* or *ORIGINAL*. The value may also be specified in lowercase
- `current_dir` (*.*) The path of the most recently used folder for loading and saving files. When empty or missing, defaults to the *current user folder*


### Program repository

Settings related to the [program repository](Program-repository) and how it is accessible from the [program browser](Features#program-browser)

- `program_repo.file.dir` (*.*) The path of the root folder of the program repository. When empty or missing, defaults to the *current user folder*
- `program_repo.file.dir-managed` (*.*) The path of the root folder of the program repository that is managed via the [distribution](Distribute-using-getdown). The application prohibits users from saving or updating files inside this folder, as these would get overridden upon the next distribution update
- `program_repo.file.dir-managed.cleanup.enable` (*true*) When *true*, obsolete files and folders from previous [distributions](Distribute-using-getdown) will be removed at startup. When *false* this is skipped
- `program_repo.rename.hide_seqnr` (*true*) When *true*, the repository item display order can be controlled via sequence numbers in the item names that remain hidden from view. See [program repository](Program-repository#order-of-items) for details. When *false*, the literal file or folder names are displayed, ordered lexicographically
- `program_repo.search.by_name` (*false*) When *true*, the program browser will display only those programs whose name (partially) matches `program_repo.search.string`. When *false* no name filter is applied
- `program_repo.search.string` (*""*) The search string used when filtering programs
- `program_repo.facet.enable` (*false*) When *true*, the program browser will organize the programs by (nested) facets. When *false*, programs are organized as per the folder structure on the file system
- `program_repo.facet.facets` (*""*) Comma-separated list of facets, in the order of nesting. Supported facets refer to the [program metadata](Program-metadata) and are "*author*", "*year*", "*tape*", "*blocks*" and "*color*" (type of monitor)



### Program browser UI

Settings related to the *User Interface* of the [program browser](Features#program-browser)

- `program_browser.cover_images.cache_capacity` (*10*) The maximum number of cover images to keep in memory cache
- `program_browser.cover_images.show` (*true*) When *true*, cover images are shown inside the program browser
- `program_browser.mini_info.show` (*true*) When *true*, some information about the currently selected program is shown while browsing
- `program_browser.theme.*` A group of settings defining visual properties of the program browser including colors



### Basic program staging

Settings related to the [staging](Basic-program-staging) of Basic source code programs

- `basic_staging.enable` (*true*) When *true*, enables the emulation of file IO instructions in Basic source code programs. This is a global setting that can be overridden by individual programs via their [program metadata](Program-metadata)
- `basic_staging.delayFileOperations` (*true*) When *true*, file IO instructions are artifically delayed for a more original reproduction (for example, binary loading a screen image block by block into the graphics memory)
- `basic_staging.minify.level` (*2*) The minification level used to reduce the Basic source code after staging expansion. Higher values change the original source code more substantially. The value range is from 0 (no minification) to 10 (maximum minification)
- `basic_staging.minify.printStats` (*false*) When *true*, minification statistics (before vs after) are logged to the console
