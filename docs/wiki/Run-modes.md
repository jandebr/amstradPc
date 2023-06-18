The *Amstrad PC* can be launched in so-called *run modes*

A *run mode* fits a specific purpose and comes with a corresponding look & feel

The run mode is defined by the property `mode` in the [javacpc.ini file](Config-javacpc.ini). As such, it can be overridden at launch time using the system property `javacpc.mode`, for example `-Djavacpc.mode="DEFAULT"`


## Default mode

`mode=DEFAULT`

The *default* run mode starts windowed with the BASIC start prompt. From there, new programs can be created and existing programs can be loaded either via the menu or the integrated [program browser](Features#program-browser)

This run mode has full [Amstrad PC API](Features#api) support

![Default run mode](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Mode-Default.png)



## Kiosk mode

`mode=KIOSK`

The *kiosk* run mode exclusively operates in fullscreen and starts in the [program browser](Features#program-browser). It is meant to run programs and play games, not to create or edit programs. Because it operates in fullscreen, there is no menu bar but a context menu that can be invoked either by right mouse click or by pressing "F2"

This run mode has full [Amstrad PC API](Features#api) support

![Kiosk run mode](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Mode-Kiosk.png)



## Original mode

`mode=ORIGINAL`

The *original* run mode starts windowed with the BASIC start prompt, much like the *default* mode. The look & feel however is true to the original *JavaCPC*. There is no context menu but there is the full-featured menu bar from *JavaCPC*

In this run mode, only partial [Amstrad PC API](Features#api) support is available

![Original run mode](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Mode-Original.png)
