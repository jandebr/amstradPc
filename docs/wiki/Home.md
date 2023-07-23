Welcome to the *Amstrad PC* wiki


## Introduction

The *Amstrad PC* is a fork of the *JavaCPC* emulator, primarily designed for managing and playing a collection of Basic program files

This software was created using the Java Development Kit (JDK) 9 and should be compatible with JRE/JDK 9 or higher

![AmstradPC GT65 emulator](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-GT65.png)

![AmstradPC CTM644 emulator](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-CTM644.png)




## Copyright

*Amstrad PC* is a modified work based on [JavaCPC][1], by Markus Hohmann

*JavaCPC* is based on the original [JEMU][2], by Richard Wilson
 
*JavaCPC* is distributed under the [GNU GENERAL PUBLIC LICENSE](LICENSE.txt) and so is this work




## Features

*Amstrad PC* provides several improvements and features over *JavaCPC* / *JEMU*

- Modern keyboard key mapping
- Instant loading of Basic source code
- Instant saving of Basic source code
- Integrated program browser
- Quick access to program info (e.g. game controls) depending on available metadata
- Support for Basic IO instructions using the host filesystem
- Insights into Basic memory allocation
- Context menu in fullscreen mode
- *Kiosk mode* for playing games
- A clean API to integrate and extend *Amstrad PC*
- Locomotive Basic parser, compiler and decompiler written in Java
- Customizable displays rendered with Java code
- Stability and bug fixes

For more information see the [Features](Features) documentation

![Integrated program browser](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Program-Browser-Menu.png)




## Getting started

Getting started with *Amstrad PC* is a simple three-step process

1. Obtain source code from [github](https://github.com/jandebr/amstradPc)
2. Build with Ant `ant -buildfile ant.xml package`, which produces `dist/amstradPc.jar`
3. Run with Java `java -jar dist/amstradPc.jar`, which uses the provided [config file](Config-javacpc.ini) `javacpc.ini`

Alternatively, one can distribute *Amstrad PC* to end user computers using [getdown](https://github.com/threerings/getdown) as detailed in the [distribution](Distribute-using-getdown) documentation

A *getdown distribution* may ship with a (managed) collection of Basic program files. It is *managed* so not intended to make local changes as these will get overridden upon update

To setup your own (local) program collection, see the [repository](Program-repository) documentation



[1]: <http://cpc-live.com> "JavaCPC"
[2]: <http://jemu.winape.net> "JEMU"
