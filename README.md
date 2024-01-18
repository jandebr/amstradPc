# Amstrad PC

*Amstrad PC* -- A fork of the *JavaCPC* emulator with enhancements for playing a collection of Basic program files

This software is 100% pure Java. It is created using the Java Development Kit (JDK) 9 and should be compatible with JRE/JDK 9 or higher

There is a [Wiki](https://github.com/jandebr/amstradPc/wiki) that documents the software

![AmstradPC GT65 emulator](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-GT65.png)

![AmstradPC CTM644 emulator](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-CTM644.png)



## Copyright

*Amstrad PC* is a modified work of [JavaCPC][1] by Markus Hohmann

*JavaCPC* is based on the original [JEMU][2] by Richard Wilson
 
*JavaCPC* is distributed under the [GNU GENERAL PUBLIC LICENSE](LICENSE.txt) and so is this work



## Features

*Amstrad PC* provides several enhancements over *JavaCPC* / *JEMU*

- Complete keyboard mapping
- Optimized fullscreen rendering
- Integrated Basic program browser
- Instant loading and saving of Basic programs
- Quick access to program info depending on available metadata
- Basic IO instructions ported to the host filesystem
- Insights into Basic memory allocation
- Onscreen popup menu
- Onscreen keyboard
- Joystick support including popular gamepads (Xbox, PS)
- Run modes to accomodate different uses
- Extensible and easily integrated using pure Java code
	- A clean API to *Amstrad PC*
	- Parser, compiler and decompiler for Locomotive Basic
	- Customizable displays rendered with Java AWT
- Many stability fixes

For more information see the [Features](https://github.com/jandebr/amstradPc/wiki/Features) documentation

![Integrated program browser](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Program-Browser.png)



## Getting started

Getting started with *Amstrad PC* is a simple three-step process

1. Obtain source code from [github](https://github.com/jandebr/amstradPc)
2. Build with Ant `ant -buildfile ant.xml package`, which produces `dist/amstradPc.jar`
3. Run with Java `java -Djava.library.path=system/jinput -jar dist/amstradPc.jar`, which uses the provided [config file](https://github.com/jandebr/amstradPc/wiki/Config-javacpc.ini) `javacpc.ini`

Alternatively, one can distribute *Amstrad PC* to end user computers using [getdown](https://github.com/threerings/getdown) as detailed in the [distribution](https://github.com/jandebr/amstradPc/wiki/Distribute-using-getdown) documentation

A *getdown distribution* may ship with a (managed) collection of Basic program files. It is *managed* so not intended to make local changes as these will get overridden upon update

To setup your own (local) program collection, see the [repository](https://github.com/jandebr/amstradPc/wiki/Program-repository) documentation



[1]: <http://cpc-live.com> "JavaCPC"
[2]: <http://jemu.winape.net> "JEMU"
