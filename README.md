# Amstrad PC

Amstrad PC -- A fork of the *JavaCPC* emulator  
It was primarily designed for managing and playing a collection of Basic source code programs



## Copyright

This is a modified work based on *JavaCPC* (based on JEMU)
*JavaCPC* is distributed under the [GNU GENERAL PUBLIC LICENSE](https://github.com/jandebr/amstradPc/blob/main/LICENSE) and so is this work

[JEMU][1] originally by Richard Wilson
[JavaCPC][2] by Markus Hohmann



## Features

*Amstrad PC* is a pure Java software that builds on *JavaCPC (JEMU)* and provides several improvements and features

- Modern keyboard key mapping
- Instant loading and saving of Basic source code
- Integrated program browser
- Quick access to program info (e.g. game controls) depending on available metadata
- Support for Basic IO instructions using the host filesystem
- Insights into Basic memory allocation
- Context menu in fullscreen mode
- *Kiosk mode* for playing games
- A clean API for integrators
- A Basic parser, compiler and decompiler written in Java
- Customizable alternative display sources in Java
- Stability and bug fixes



## Getting started




An instance of `AmstradPc` can be obtained from [AmstradFactory](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/AmstradFactory.java) 

See also the main class [AmstradMain](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/AmstradMain.java)

You can launch the software using these VM arguments :

- `-splash:resources/images/amstrad1.png` for a splash startup image
- `-Djavacpc.ini="javacpc.ini"` for locating the *JavaCPC* configuration file (default ./javacpc.ini)
- `-Djavacpc.ini.out="javacpc.ini"` for writing the modified *JavaCPC* configuration file (default read-only)



## Screenshots

Emulated AmstradPc
![screenshot](https://github.com/jandebr/amstradPc/blob/main/screenshots/amstradPc.png)

Integrated program browser
![screenshot](https://github.com/jandebr/amstradPc/blob/main/screenshots/program-browser-menu.png)

Program info
![screenshot](https://github.com/jandebr/amstradPc/blob/main/screenshots/program-browser-info.png)



## References

[1]: <http://jemu.winape.net> "JEMU"
[2]: <http://cpc-live.com> "JavaCPC"
