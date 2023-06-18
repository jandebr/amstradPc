# Amstrad PC

*Amstrad PC* -- A fork of the *JavaCPC* emulator  
Primarily designed for managing and playing a collection of Basic source code programs

This software was created using the Java Development Kit (JDK) 9 and should be compatible with JRE/JDK 9 or higher



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
- A Basic parser, compiler and decompiler written in Java
- Customizable alternative display sources rendered with Java code
- Stability and bug fixes

For more information see the [Features](docs/wiki/Features) documentation



## Getting started

Getting started with *Amstrad PC* is a simple three-step process

1. Obtain source code from [github](https://github.com/jandebr/amstradPc)
2. Build using `ant -buildfile ant.xml package`, which produces `dist/amstradPc.jar`
3. Run using `java -jar dist/amstradPc.jar`, which uses the config file `./javacpc.ini`

Alternatively, one can distribute *Amstrad PC* to end user computers, as detailed in the [Distribution](docs/wiki/Distribute-Using-Getdown) documentation



[1]: <http://cpc-live.com> "JavaCPC"
[2]: <http://jemu.winape.net> "JEMU"
