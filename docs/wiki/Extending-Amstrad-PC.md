The *Amstrad PC* application code and [API](Features#api) were designed with custom extensions in mind

This page highlights a few probable extensions



## Menu actions

To add a new menu action, follow these steps

1. Implement the action as a subclass of [AmstradPcAction](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/action/AmstradPcAction.java)
2. Provide a reference to the action from within [AmstradPcActions](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/action/AmstradPcActions.java)
3. Add the action to a menu from within the [AmstradPcMenuMaker](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/action/AmstradPcMenuMaker.java)

Learn by example
- [QuitAction](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/action/QuitAction.java)
- [AmstradSystemColorsDisplayAction](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/gui/colors/AmstradSystemColorsDisplayAction.java)



## Custom displays

![Amstrad Colors display](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Colors.png)

To add a custom display rendered in Java code, follow these steps

1. Implement the display as a subclass of [AmstradWindowDisplaySource](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/monitor/display/source/AmstradWindowDisplaySource.java)
    - An instance of [AmstradDisplayCanvas](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/monitor/display/AmstradDisplayCanvas.java) is passed for example in the inherited method `renderWindowContent(AmstradDisplayCanvas canvas)`. It features several emulated drawing operations in the traditional Basic graphics language
    - By extending `AmstradWindowDisplaySource`, some common UI elements are available by inheritance for example a title bar and a *close* button. The superclass also provides some convenience rendering methods, including modal windows
2. Show (open) the display using the method `swapDisplaySource(AmstradAlternativeDisplaySource displaySource)` from [AmstradMonitor](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/monitor/AmstradMonitor.java), passing an instance of the display as the argument
    - This could be triggered from a [custom menu action](#menu-actions) for example
3. Hide (close) the display using the method `resetDisplaySource()` from [AmstradMonitor](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/monitor/AmstradMonitor.java)
    - This could be triggered from the same [custom menu action](#menu-actions)
    - Clicking the display's *close* button will by default invoke `resetDisplaySource()`

Learn by example
- [AmstradSystemColorsDisplaySource](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/gui/colors/AmstradSystemColorsDisplaySource.java)
- [BasicMemoryDisplaySource](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/gui/memory/BasicMemoryDisplaySource.java)




## Program repositories

The [program browser](Features#program-browser) integrated in the *Amstrad PC* references an instance of [AmstradProgramRepository](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/repo/AmstradProgramRepository.java) that provides the browser with a collection of programs organized in some hierarchy (see [program repository](Program-repository))

The default repository implementation [BasicProgramFileRepository](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/repo/file/BasicProgramFileRepository.java) returns a collection of Basic source code files (`.bas`) or Basic byte code files (`.bin`) that are stored as files on the local file system

> Note that the *Amstrad Pc* also supports loading and saving of [snapshot files](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/AmstradPcSnapshotFile.java) (`.sna` or `.snz`) via the menu but not via the browser

Implementing a different type of repository and/or supporting a different type of programs is a more fundamental work

These are the high-level steps

1. Add an enum value for the new *program type* to [AmstradProgramType](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/AmstradProgramType.java)
2. Implement the new *repository type* as a subclass of [AmstradProgramRepository](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/repo/AmstradProgramRepository.java)
    - One essential method to implement is the `AmstradProgramRepository.FolderNode.listChildNodes()` as it defines the hierarchy and listing of programs
    - Another essential method to implement is the `AmstradProgramRepository.ProgramNode.readProgram()` which returns an instance of [AmstradProgram](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/AmstradProgram.java) for the new [AmstradProgramType](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/AmstradProgramType.java)
    - For file-based repositories, it will be easiest to extend from [FileBasedAmstradProgramRepository](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/repo/file/FileBasedAmstradProgramRepository.java) and implement the method `createProgram(...)` to return an instance of [AmstradProgramStoredInFile](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/AmstradProgramStoredInFile.java). To allow a proper recognition of the program file's *payload* (as either text or binary), add an enum value to [AmstradFileType](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/AmstradFileType.java) matching the programs' file extension
3. To support the `load` and `run` operations in the *program browser*, implement a new *program loader* as a subclass of [AmstradProgramLoader](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/load/AmstradProgramLoader.java)
    - The responsibility of the `AmstradProgramLoader` is to load an instance of `AmstradProgram` (of the new *program type*) into the `AmstradPc` via the method `loadProgramIntoAmstradPc(...)`
    - This may likely require an extension to the [AmstradPc](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/AmstradPc.java) as well
4. To support the `run` operation in the *program browser*, the new *program loader* creates and returns an instance of a subclass of [AmstradProgramRuntime](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/load/AmstradProgramRuntime.java)
    - The responsibility of the `AmstradProgramRuntime` is to run an already loaded `AmstradProgram` (of the new *program type*) via the method `doRun(...)`
5. Return an instance of the new *program loader* in the method `createLoaderFor(AmstradProgram program, AmstradPc amstradPc)` of the [AmstradProgramLoaderFactory](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/load/AmstradProgramLoaderFactory.java) as per the *program type* of the argument `program`

Learn by example
- [BasicProgramFileRepository](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/repo/file/BasicProgramFileRepository.java)
- [AmstradBasicProgramFile](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/program/AmstradBasicProgramFile.java)
- [BasicProgramLoader](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/load/basic/BasicProgramLoader.java)
- [BasicProgramRuntime](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/load/basic/BasicProgramRuntime.java)
