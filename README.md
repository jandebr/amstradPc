# Amstrad PC
Amstrad PC -- A fork of the *JavaCPC* emulator


## Copyright
This is a modified work based on *JavaCPC* (Based on Jemu)

JEMU originally by Richard Wilson (http://jemu.winape.net)

Enhanced Emulator Version 6.5 by Markus Hohmann (http://cpc-live.com)

*JavaCPC* is distributed under the [GNU GENERAL PUBLIC LICENSE](https://github.com/jandebr/amstradPc/blob/main/LICENSE) and so is this work


## Modifications
This work is a 100% Java software that builds on *JavaCPC (JEMU)* and provides features including

- An integrated program browser

- Instant loading and saving of Basic source code

- Quick access to program info (e.g. game controls) depending on available metadata

- Text-based program metadata files

- A much simplified menu bar, in sync with the API

- Minor fixes and improvements to *JavaCPC* (e.g., fullscreen, pause mode, keyboard mapping)

- An easy API for integrators via the abstract class [AmstradPc](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/AmstradPc.java)

- A Basic compiler and decompiler developed in Java


## Getting started
An instance of `AmstradPc` can be obtained from [AmstradFactory](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/pc/AmstradFactory.java) 

See also the main class [AmstradMain](https://github.com/jandebr/amstradPc/blob/main/src/org/maia/amstrad/AmstradMain.java)

You can launch the software using these VM arguments :

- `-splash:resources/images/amstrad1.png` for a splash startup image
- `-Djavacpc.ini="javacpc.ini"` for locating the *JavaCPC* configuration file (default ./javacpc.ini)
- `-Djavacpc.ini.out="javacpc.ini"` for writing the modified *JavaCPC* configuration file (default read-only)


## Screenshots
Emulated Amstrad PC
![screenshot](https://github.com/jandebr/amstradPc/blob/main/screenshots/amstradPc.png)

Integrated program browser
![screenshot](https://github.com/jandebr/amstradPc/blob/main/screenshots/program-browser-menu.png)
![screenshot](https://github.com/jandebr/amstradPc/blob/main/screenshots/program-browser-info.png)
