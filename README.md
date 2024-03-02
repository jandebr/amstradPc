# Amstrad PC

*Amstrad PC* -- A fork of the *JavaCPC* emulator with enhancements for playing a collection of Basic program files

The software is created using the Java Development Kit (JDK) 9 and is compatible with JRE/JDK 9 or higher

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
- Onscreen keyboard
- Onscreen popup menu
- Joystick support including popular gamepads (Xbox, PS)
- Instant loading and saving of Basic programs
- Integrated program browser
- Quick access to program info
- Multiple run modes
- Basic IO against the host filesystem
- Insights into Basic memory allocation
- Extend and integrate
	- A clean API
	- Customizable Java AWT displays
	- Locomotive Basic parser, compiler and decompiler written in Java
- Optimized fullscreen rendering
- Stability fixes

For more information see the [Features](https://github.com/jandebr/amstradPc/wiki/Features) documentation

![Integrated program browser](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Program-Browser.png)



## Getting started

To get started

1. Download the project sources from [github](https://github.com/jandebr/amstradPc)
2. From the parent directory, run `java -Djava.library.path=system/jinput -jar dist/amstradPc.jar`

Alternatively, one can distribute *Amstrad PC* to end user computers using [getdown](https://github.com/threerings/getdown) as detailed in the [distribution](https://github.com/jandebr/amstradPc/wiki/Distribute-using-getdown) documentation

A *getdown distribution* may ship with a (managed) collection of Basic program files. It is *managed* so not intended to make local changes as these will get overridden upon update

To setup your own (local) program collection, see the [repository](https://github.com/jandebr/amstradPc/wiki/Program-repository) documentation



[1]: <http://cpc-live.com> "JavaCPC"
[2]: <http://jemu.winape.net> "JEMU"
