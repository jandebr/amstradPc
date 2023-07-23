When a Basic program is `run` from the [program browser](Features#program-browser), it is *staged* by default (see [disabling staging](#disabling-staging) below)

*Staging* allows for a more seamless experience

- When a program ends, instead of entering the Basic prompt, one returns to the program browser
    > In the program browser, one can still go back to the Basic prompt using the `Return` menu option, except in [kiosk mode](Run-modes#kiosk-mode) where this option is not available
- When a program contains IO instructions, they are implemented against the native file system. No disc images or disc setups are needed (see [Basic IO](#basic-io) below)



## How it works

*Staging* works by source code manipulation

For this reason, it only works on Locomotive Basic 1.0 source code programs (`.bas` or their binary equivalents `.bin` that can be decompiled to source code)

A starting point for the implementation of the mechanism is the Java class [StagedBasicProgramLoader](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/load/basic/staged/StagedBasicProgramLoader.java). The implementation is based on a communication between the Java runtime and the Basic runtime using [memory traps](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/memory/AmstradMemoryTrap.java) (involving `POKE` and `PEEK`) and direct access to [Basic variables](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/basic/locomotive/LocomotiveBasicVariableSpace.java)

Staging extends to other programs over `CHAIN MERGE`, `CHAIN` or `RUN` instructions

All this works under the hood. An end user never gets to see the manipulated source code. This is realized by

- Disabling `save` menu actions while the program is run
- Automatically restoring the original source code when the program ends (including `Break` interrupts by the user and errors)




## Basic IO

An important objective of *Amstrad PC* was to eliminate the need for making disc images (for example `.dsc` or `.dsk`). Instead, Basic programs are hosted as individual files on the *native file system* and disc IO operations are implemented against that same file system using the *staging* mechanism


### Resolving filenames

Basic programs can reference filenames in *IO instructions*

For example

```basic
100 OPENOUT "!highscores"
...
200 RUN "!"
```

The filename `highscores` then is resolved to a *file* on the *native file system* in the following order

1. By consulting the [program metadata](Program-metadata), if any, looking for a `FILEREFS` property having the exact (case-sensitive) filename between brackets. The exclamation mark ('!') must not be included. The filename can be empty to denote the "next program on tape". The property value tells the target file on the file system, relative against the location of the metadata file (absolute paths are not supported)
    ```
    FILEREFS[highscores]: highscores.txt
    FILEREFS[]: level2.bas DESCRIBED BY level2.amd
    ```
2. *(fallback method)* By assuming the target file has the filename as mentioned in the source code (without the exclamation mark), relative against the location of the program file


### Supported IO instructions

The following IO instructions are supported by *staging*

#### Binary files
```basic
SAVE "filename", B, startAddress, lengthInBytes
```

```basic
LOAD "filename", startAddress
```


#### Ascii files
```basic
OPENOUT "filename"
```

```basic
PRINT #9, "Hi"
PRINT #9, 123
PRINT #9, A$
PRINT #9, A%
PRINT #9, A!
PRINT #9, A$, A%, A!
PRINT #9, A$(1), A%(I%,J%), "Hi", 123
```

```basic
CLOSEOUT
```

```basic
OPENIN "filename"
```

```basic
[LINE] INPUT #9, A$
[LINE] INPUT #9, A%
[LINE] INPUT #9, A!
[LINE] INPUT #9, A$, A%, A!
[LINE] INPUT #9, A$(1), A%(I%,J%)
```

```basic
EOF
```

```basic
CLOSEIN
```


#### Program files
```basic
RUN "filename"
```

```basic
CHAIN "filename"
CHAIN "filename", lineNumber
```

```basic
CHAIN MERGE "filename"
CHAIN MERGE "filename", resumeLineNumber
CHAIN MERGE "filename", resumeLineNumber, DELETE lineNumberFrom[-lineNumberTo]
```

> Beware that in the *staging* implementation, `CHAIN MERGE` renumbers the referenced program such that it always comes at the end of the merged program


### Unsupported IO instructions

The following IO instructions are currently not supported by *staging*

```basic
SAVE "filename", A
```

```basic
SAVE "filename", P
```

```basic
MERGE "filename"
```


### IO errors

When a *staged* program is run, IO instructions may run into error. The program then ends and the error code is available via the Basic `ERR` keyword

```basic
PRINT ERR
```

The table below lists the (custom) error codes from *staged* IO instructions and what resolutions are possible

|Error code|Error description|Resolution|
|:---:|:---|:---|
|7|Basic memory is full (e.g., with `CHAIN MERGE`)|Try increasing code minification via the setting `basic_staging.minify.level` in [javacpc.ini](Config-javacpc.ini#basic-program-staging)|
|32|File not found|Check for missing or incorrect file references, see [resolving filenames](#resolving-filenames)|
|33|Failure with `CHAIN MERGE`|Check the [Java console](Features#java-console) for error details|
|34|Failure with `RUN` or `CHAIN`|Check the [Java console](Features#java-console) for error details|
|35|Failure with `LOAD`|Check the [Java console](Features#java-console) for error details|
|36|Failure with `SAVE`|Check the [Java console](Features#java-console) for error details|
|37|Failure with `OPENIN`, `[LINE] INPUT #9` or `CLOSEIN`|Check the [Java console](Features#java-console) for error details|
|38|Failure with `CLOSEOUT`, `PRINT #9` or `CLOSEOUT`|Check the [Java console](Features#java-console) for error details|




## Disabling staging

*Staging* can be disabled in several ways

- The `load` menu option in the [program browser](Features#program-browser) always loads the original source code
    > Beware that when the program is loaded and then `RUN` from the Basic prompt, there is no *staging* taking place and hence any IO instructions will likely fail

- By including the `NO-STAGE` flag in the [program metadata](Program-metadata)
    ```
    FLAGS: NO-STAGE
    ```

- By setting `basic_staging.enable` to *false* in [javacpc.ini](Config-javacpc.ini#basic-program-staging). This is a global setting that affects all programs
