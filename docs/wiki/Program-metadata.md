An *Amstrad program* can be described and documented by a companion file called *Amstrad metadata* file

The location and name of the metadata file is as follows (see also [program repository](Program-repository))

- If every program has its own folder, the filename can be chosen but must end with `.amd`
- If multiple programs are in one folder, the filename must equal the program's filename but end with `.amd`. For example, for a program `pac-man.bas` the corresponding metadata file is `pac-man.amd`




## File format

The file is a plain text file that follows the Java [Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-) `key: value` line format

Inside a *value* there can be a few special characters

- A newline character `\n` to force a new line of text
- An Amstrad *symbol* represented as Unicode character, for example `\u00E9` ("\u00" followed by a hexadecimal value between 32 and 255)




## Example

Below is the example contents of a metadata file named `pac-man.amd`

An explanation of the metadata is given [below](#list-of-metadata)

```
TYPE: LOCOMOTIVE BASIC PROGRAM
NAME: < PAC-MAN >
AUTHOR: Jan De Beer
YEAR: 1994
TAPE: TDK-1 (A)
BLOCKS: 6
MONITOR: GREEN
DESCRIPTION: The legendary PAC-MAN game\nEarn points by eating. Be the quickest to reach 2000 points
AUTHORING: Original game\nFeatures a single maze\nContains some minor bugs
CONTROLS[1]HEADING: Main menu
CONTROLS[1]KEY: \u00F1 (numpad 2)
CONTROLS[1]DESCRIPTION: Next option
CONTROLS[2]KEY: \u00E9 (numpad 5)
CONTROLS[2]DESCRIPTION: Enter option
CONTROLS[3]HEADING: Change name
CONTROLS[3]KEY: \u00F3 (numpad 6)
CONTROLS[3]DESCRIPTION: Scroll next letter
CONTROLS[4]KEY: \u00F2 (numpad 4)
CONTROLS[4]DESCRIPTION: Scroll previous letter
CONTROLS[5]KEY: \u00E9 (numpad 5)
CONTROLS[5]DESCRIPTION: Confirm letter\nPad with spaces to fill the entire name field
CONTROLS[6]KEY: \u00F0 (numpad 8)
CONTROLS[6]DESCRIPTION: Go back one letter
CONTROLS[7]HEADING: Pac-man
CONTROLS[7]KEY: \u00F0 (numpad 8)
CONTROLS[7]DESCRIPTION: Move up
CONTROLS[8]KEY: \u00F1 (numpad 2)
CONTROLS[8]DESCRIPTION: Move down
CONTROLS[9]KEY: \u00F2 (numpad 4)
CONTROLS[9]DESCRIPTION: Move left
CONTROLS[10]KEY: \u00F3 (numpad 6)
CONTROLS[10]DESCRIPTION: Move right
CONTROLS[11]KEY: \u00E9 (numpad 5)
CONTROLS[11]DESCRIPTION: Pause/resume game
IMAGES[1]FILEREF: screenshot1.png
IMAGES[1]CAPTION: Title screen
IMAGES[2]FILEREF: screenshot2.png
IMAGES[2]CAPTION: Main menu
IMAGES[3]FILEREF: screenshot3.png
IMAGES[3]CAPTION: Maze
COVERIMAGE: cover.png
FILEREFS[highscores]: highscores.txt
FILEREFS[]: ../PAC-MAN2/pac-man2.bas DESCRIBED BY ../PAC-MAN2/pac-man2.amd
FLAGS: 
```

For illustration, a visual representation of (most parts of) this file in the [program browser](Features#program-browser)

![Program info](https://github.com/jandebr/amstradPc/blob/main/docs/wiki/Program-metadata.png)




## List of metadata

All metadata is informational and optional, except the `FILEREFS` may be required to properly run the program

- `TYPE` The type of program. For now only a single value is supported "*LOCOMOTIVE BASIC PROGRAM*"
- `NAME` The original name of the program. May be different than the filename, for example as the file system may not allow certain characters
- `AUTHOR` The author(s) of the program
- `YEAR` The year in which the program was made
- `TAPE` The tape on which the program used to be saved
- `BLOCKS` The number of blocks when saved to tape
- `MONITOR` The preferred monitor type to run the program in. Possible values are
    - "*COLOR*" for a color monitor (CTM644)
    - "*GREEN*" for a green monitor (GT65)
    - "*GRAY*" for a gray monitor
- `DESCRIPTION` A description of the program
- `AUTHORING` Any information about the program that goes separate from its description
- `CONTROLS` Any user controls (indexed) with the following properties
    - `CONTROLS[i%]HEADING` Starts grouping the next user controls in the file under this heading
    - `CONTROLS[i%]KEY` A user control e.g., keypress or joystick command
    - `CONTROLS[i%]DESCRIPTION` Explains what the user control is for
- `IMAGES` Any program images, for example screenshots (indexed) with the following properties
    - `IMAGES[i%]FILEREF` Path to the image file, relative to the location of the metadata file. Supported image formats are PNG and JPG
    - `IMAGES[i%]CAPTION` A short caption that describes the image
- `COVERIMAGE` Path to the cover image file, relative to the location of the metadata file. Supported image formats are PNG and JPG
- `FILEREFS` Maps a source filename as mentioned in the program's code to a target file path on the native file system. The target file path (as well as its own metadata file) must be relative to the location of the metadata file. The source filename is case sensitive. The character '!' as sometimes used in the source code must not be included and spaces should be escaped e.g. `FILEREFS[DINOSAURS\ 1]`. The filename can be empty to denote the "next program on tape". Some example variations to `FILEREFS` lines
    - `FILEREFS[source$]: target` to support for example `OPENOUT "source$"`
    - `FILEREFS[source$]: target.bas DESCRIBED BY target.amd` to support for example `RUN "source$"`
    - `FILEREFS[]: target.bas DESCRIBED BY target.amd` to support for example `RUN ""`
    - `FILEREFS[]: target.bas` to support for example `CHAIN MERGE ""`
- `FLAGS` A comma-separated list of flags. The following flags are supported
    - "*NO-LAUNCH*" to disable `load` or `run` menu options in the [program browser](Features#program-browser) (e.g., when the code is still being worked on)
    - "*NO-STAGE*" to disable [staging](Basic-program-staging) for this program




## Usages

The *program metadata* is used on several places in the *Amstrad PC*

- Most noticeably in the [program browser](Features#program-browser)
    - Shows the original program name in all panels
    - Shows the program cover image (if any) while browsing
    - Makes all metadata information accessible behind the menu options `Info`, `Images` and `File refs`
- When a program is run, pressing `F1` pauses the game and opens up an information panel where for example the controls are listed for quick reference
- When a program is [staged](Basic-program-staging) for running, the `FILEREFS` are used to support any [Basic IO](Basic-program-staging#basic-io) instructions in the program's code


Example *cover image* in the program browser

![Program cover image](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Program-Browser.png)


Example *info panel* in the program browser

![Program info](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Program-Browser-Info.png)


Example *images panel* in the program browser

![Program images](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Program-Browser-Images.png)


Example *file references panel* in the program browser

![Program file references](https://github.com/jandebr/amstradPc/blob/main/screenshots/AmstradPC-Program-Browser-Filerefs.png)
