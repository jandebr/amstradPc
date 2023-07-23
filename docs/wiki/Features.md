This page highlights a selection of *Amstrad PC* features


## Program Browser

![Integrated program browser](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Program-Browser-Menu.png)

The integrated *program browser* is designed to navigate through a collection (*repository*) of programs, inspect programs before loading or running. For more details see [program repository](Program-repository) and [program metadata](Program-metadata)

The browser can be accessed at any time by pressing `Ctrl-B` (also available from the `File` or popup menu). When starting *Amstrad PC* in [kiosk mode](Run-modes#kiosk-mode), the browser shows up as the home screen

To configure the browser, enter the *browser setup* panel by pressing `Ctrl+Shift-B` (also available from the `File` or popup menu). Most importantly this is where you can set the *home folder* for the program collection. You can also choose an alternate navigation by nested facets (as opposed to nested folders) and filter by search

![Program browser setup](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Program-Browser-Setup.png)

To navigate in the browser, use the keyboard
- `Arrow keys` to go up, down, left and right
- `Page Up` to quickly go up a list
- `Page Down` to quickly go down a list
- `Home` to jump to the start of a list
- `End` to jump to the end of a list
- `Enter` to go inside or confirm
- `Esc` to go back or close a modal window

There is a top-left *home* button that takes you back to the *home folder*
> As a side effect, any program caches are cleared so edits to the [program metadata](Program-metadata) or underlying file system will be effective

To exit the browser, click its top-right *close* button or press `Esc`
> In [kiosk mode](Run-modes#kiosk-mode), one cannot (accidentally) `Esc` the browser but clicking its *close* button terminates the *Amstrad PC*

When navigating to a program and pressing `Enter`, a *program menu* pops up that lists a number of options. Options may be either disabled or not shown when not available

![Program menu](https://github.com/jandebr/amstradPc/blob/main/docs/wiki/Program-menu.png)

- `Return` Takes you back to the *Basic prompt* following an ended program run. Not available in [kiosk mode](Run-modes#kiosk-mode)
- `Run` Loads the program and immediately runs it
- `Load` Loads the program and takes you to the *Basic prompt* where you can inspect and modify the source code, save it and run it (without [staging](Basic-program-staging)). Not available in [kiosk mode](Run-modes#kiosk-mode)
- `Info` Shows the program information as per the available [program metadata](Program-metadata)
- `Images` Shows the program images as per the available [program metadata](Program-metadata)
- `File refs` Opens a utility that lists all file references detected in the program's source code and indicates which of those are (not) linked in the [program metadata](Program-metadata). Not available in [kiosk mode](Run-modes#kiosk-mode)
- `Close` Closes the program menu




## Run modes

The *Amstrad PC* can be launched in so-called *run modes*. A *run mode* fits a specific purpose and comes with a corresponding look & feel

For example, the [kiosk mode](Run-modes#kiosk-mode) is designed for playing a collection of programs

For more information, see the [run modes](Run-modes) documentation




## API

The *Amstrad PC* comes with an API that allows programmatic control and operation, makes it [extensible](Extending-Amstrad-PC) and easy to integrate in other Java applications
> Every *Amstrad PC* menu action is also supported via the API and both are kept in sync with the internal state of the *Amstrad PC*. In future versions, more menu options from *JavaCPC* may become available in the *Amstrad PC* [default mode](Run-modes#default-mode) and the API

A good starting point for the *Amstrad PC* API is the (abstract) class [AmstradPc](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/AmstradPc.java). Instances of `AmstradPc` can be obtained from the (singleton) class [AmstradFactory](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/AmstradFactory.java). The `AmstradFactory` also provides a reference to the (singleton) [AmstradContext](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/AmstradContext.java) which provides global information such as the [run mode](Run-modes) (as [AmstradMode](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/AmstradMode.java)) and the [configuration settings](Config-javacpc.ini) (as [AmstradSettings](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/AmstradSettings.java))

The API is modular in that an instance of `AmstradPc` references several device instances, having their own API and `Listener` interface. See for example [AmstradKeyboard](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/keyboard/AmstradKeyboard.java), [AmstradMonitor](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/monitor/AmstradMonitor.java), [AmstradMemory](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/memory/AmstradMemory.java), [AmstradAudio](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/audio/AmstradAudio.java) and [AmstradTape](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/tape/AmstradTape.java)

To generate the (public) Javadoc for *Amstrad PC*, follow these steps

1. Obtain source code from [github](https://github.com/jandebr/amstradPc)
2. Generate Javadoc with Ant `ant -buildfile ant-javadoc.xml `, which produces `docs/javadoc`
3. Open `docs/javadoc/index.html` in a browser




## Locomotive Basic

An instance of `AmstradPc` references an (abstract) [BasicRuntime](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/basic/BasicRuntime.java) that is the API interface to the *Basic runtime environment*. Via this interface, Basic programs can be fast-loaded into Basic memory or exported from Basic memory. It also provides a reference to a [BasicCompiler](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/basic/BasicCompiler.java) and [BasicDecompiler](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/basic/BasicDecompiler.java) for the [BasicLanguage](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/basic/BasicLanguage.java) of the `BasicRuntime`

For the time being, there is a single implementation [LocomotiveBasicRuntime](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/basic/locomotive/LocomotiveBasicRuntime.java) supporting *Locomotive Basic 1.0*

The Basic support in *Amstrad PC* can be used "standalone" by other Java applications, so outside the context of an `AmstradPc` instance. Below are some code examples featuring the use of a Basic compiler, decompiler and parser


### Basic compiler example

```java
BasicCompiler compiler = new LocomotiveBasicCompiler();
try {
    CharSequence source = AmstradIO.readTextFileContents(new File("test.bas"));
    BasicSourceCode sourceCode = new LocomotiveBasicSourceCode(source);
    BasicByteCode byteCode = compiler.compile(sourceCode);
} catch (IOException e) {
    // failed to read file
} catch (BasicException e) {
    // failed to parse or compile source code
}
```


### Basic decompiler example

```java
BasicDecompiler decompiler = new LocomotiveBasicDecompiler();
try {
    byte[] bytes = AmstradIO.readBinaryFileContents(new File("test.bin"));
    BasicByteCode byteCode = new LocomotiveBasicByteCode(bytes);
    BasicSourceCode sourceCode = decompiler.decompile(byteCode);
} catch (IOException e) {
    // failed to read file
} catch (BasicException e) {
    // failed to decompile
}
```


### Basic parser example

```java
LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
try {
    BasicKeywordToken END = stf.createBasicKeyword("END");
    BasicKeywordToken GOTO = stf.createBasicKeyword("GOTO");
    CharSequence source = AmstradIO.readTextFileContents(new File("test.bas"));
    BasicSourceCode sourceCode = new LocomotiveBasicSourceCode(source);
    for (BasicSourceCodeLine line : sourceCode) {
        BasicSourceTokenSequence sequence = line.parse();
        // Replace END with GOTO 250
        int i = sequence.getFirstIndexOf(END);
        while (i >= 0) {
            sequence.replace(i, GOTO, stf.createLiteral(" "), stf.createLineNumberReference(250));
            i = sequence.getNextIndexOf(END, i + 3);
        }
        // Replace line in source code with updated sequence
        if (sequence.isModified()) {
            String sequenceCode = sequence.getSourceCode();
            sourceCode.addLine(new LocomotiveBasicSourceCodeLine(sequenceCode));
        }
    }
} catch (IOException e) {
    // failed to read file
} catch (BasicException e) {
    // failed to parse
}
```




## Basic file IO

An important objective of *Amstrad PC* was to eliminate the need for making disc images (for example `.dsc` or `.dsk`). Instead, Basic programs are hosted as individual files on the *native file system* and disc IO operations are implemented against that same file system using the *staging* mechanism

For example, consider the following Basic code snippet

```basic
100 OPENOUT "!highscores"
110 FOR a%=1 TO NPLAYERS
120 PRINT #9, PLAYER$(a%), HIGHSCORE(a%)
130 NEXT a%
140 CLOSEOUT
```

When run, it results in a file `highscores.txt` on the *native file system* that has the following example contents

```
Jan
 140.0
Lucas
 120.0
Amber
 45.0
```

To find out how *staging* works and how it can be controlled, see [staging programs](Basic-program-staging)




## Java console

For troubleshooting purposes, the logs from the *Amstrad PC* can be consulted in the *Java Console*

The *Java Console* can be accessed from the `Emulator` menu
> The console is not available in [kiosk mode](Run-modes#kiosk-mode) as it exposes internal details

![Java console](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Java-Console.png)
