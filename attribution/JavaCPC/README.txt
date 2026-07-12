JavaCPC (Based on Jemu)
JEMU originally by Richard Wilson
           http://jemu.winape.net
Enhanced Emulator by Markus Hohmann
	   http://cpc-live.com

Standalone Emulator Version 6.5



System requirements:
Windows PC > 2 Ghz & 128 Mb Ram
JAVA Virtual Machine
(visit http://java.com)



Thanks to César Nicolás González - http://cpce.emuunlim.com -
Thanks to Kev Thacker for great help!
Also thanks to John Girvin - www.girv.net - 
for his C++ conversation of:
"TZX to WAV Converter v0.2" 
"(C) 2006 Francisco Javier Crespo"



***************************
For web-useage please read:
     parameters.txt
***************************

---------------------------------------------------------------------

Changes from ver. 6.5 to 6.6

- Debugger improvements (Breakpoints, Break Instructions)
- Tape polarity changeable (Needed for some strange tapes)
- New displaying method allows DoubleBuffered in actual JAVA-update
- A virtual floppy drive as anchor window, moveable with mouse and
  zoomable (Double click with left/right mousebutton into the drive)
- Simple GIF-Recorder (Not perfect, but funny)
- New screenshot routine (Allows to catch also the filter/monitor mask)
- New greenscreen emulation
- Improved display filter (needs high performance PC's)
- JavaCPC's window position is stored now on exit
- Added Prodatron's Digitracker tools as internal applications
  (MOD converter and Player generator)
- CRTC improvements allow the demo "From Scratch" to be viewed fully
- Small bugfix in FDC emulation (allows to watch raster images
  from ConvImgCPC without read error)

---------------------------------------------------------------------

Changes from ver. 6.4 to 6.5

- New feature: Record audio (You can save JavaCPC's soundchip output
  as WAV file now, 44khz, 8 bit, stereo)
- CSW file support
- Completely overhauled AY emulation with logarithmic volume output
  (You can choose between 3 different volume-tables) and with better
  noise output
- New features for Windows users: You can create CDT or convert WAV
  to CDT now using JavaCPC's GUI's for 2CDT / SAMP2CDT
  (Thanks to Kevin Thacker who gave me permission to use his apps)
- Improved CRTC emulation

---------------------------------------------------------------------

Changes from ver. 6.3 to 6.4
- Improved tape emulation, supports WAV, CDT and MP3!
- Tapedeck can be used as MP3/WAV music player
- Bugfix in Z80 emulation, makes Speedlock and similar protections
  work
- Bugfix in FDC emulation, makes games "Prehistorik II" and "Super
  cauldron" work
- Chooseable CRTC type 0 / 1 and CRTC improvements
- CRTC Register 1 fix
- Added GZip compression for WAV, DSK, SNA
- Added keyboard-recording function (Check edit-menu)
- Added STarKos 1.21 roms (Chooseable in JavaCPC's romsetter)
- some several bugfixes
- Better resynchronisation for sound
- New "knobs" to change volume / digiblaster volume
- New feature: "Report a bug" which allows the user to send a bug-
  report directly to me, includes JAVA-console output
  (See: Help -> Report a bug)
- Improved version checking
- You can assign now DSK, SNA and other cpc-related files to Win32 
  executable

---------------------------------------------------------------------

Changes from ver. 6.2 to 6.3
- CDT support!
- Tape can save to WAV
- YM-Recorder changed: - Waits now until AY chip reacts, before start
  recording
- Better scan-effect and scanlines when bilinear filter is set
- Drag & drop function - You can now drag & drop all supported files,
  like DSK, CDT, WAV, SNA etc... directly into emulator window
- Better memory useage (You can still use JavaCPC as applet, but there
  with memory limitations. As application, it can now use up to 256mb
  RAM.) - Larger memory is used for on-the-fly CDT converting.
- Convert CDT to WAV function to create WAV files from tape images
- Some bugfixes


---------------------------------------------------------------------

Changes from ver. 6.1 to 6.2
- Bilinear display filtering
- Tape-support (PCM WAV files 8 bit, 44khz, mono supported)
- YM recorder / player plays uncrunched YM3, YM5 and YM6 files
  (1 mhz CPC and also 2 mhz Atari ST possible)
- Digitracker included! Thanks to Prodatron.
- Memory selectable
- Computername chooseable (Amstrad, Orion, Schneider, Awa, Solavox,
  Triumph, Saisho, Isp
- You can enable DSK-check (Emulator will ask you to save changes,
  when a DSK have been changed)
- Automatic update check can be enabled/disabled
- Manual update check
- URLs added to homepage / downloadpage / Sourceforge projectpage
- Improvements in ROM setup: You can use your own ROMs now!
- Improved autofire function with variable speed (Advanced settings)
- Performance settings for older PCs (When enabled, some Display-
  features are disabled to increase performance)
- New display icons
- Content of autotype console is saved now automatically for better
  useage
- Dummy-format command (not formatting yet, but skips the tracks)
- Some bugfixes

---------------------------------------------------------------------

Changes from ver. 6.0 to 6.1
- DigiBlaster emulation implemented
- Keyboard translation for german and spanish keyboards (Forceable to
  other languages)
- Some smaller bugfixes
- Value for VHold is stored now in javacpc.ini
- Merry Christmas to all JavaCPC users!

---------------------------------------------------------------------
Changes from ver. 5.8 to 6.0
- Added 'Show actual palette'
- Added 'import CPC file' you can import now
  BIN files with or without header!
- Added 'Catch CPC screen (16k)' you can now
  catch the actual CPC screen content from
  &C000 - &FFFF and save it with a .PAL file
  and a internal palette. (Palette can be
  called using CALL &C7D0 in BASIC)
- Added 'Poke Memory' you can now poke
  values to given memory addresses
- Improved 'Save snapshot' now you can save
  64k, 128k, 256k and 512k snapshots and load
  them back!
- Improved 'Save Screenshot' function
- Added 'Floppystatus' to menu bar
- And much more...

---------------------------------------------------------------------
Changes from ver. 5.7 to 5.8
- Added a JavaCPC expansion rom
  (Please read info from menu or type
  |INFO when ROM is enabled)
- Saving of 64k and 128k snapshots
- Internal hex-editor (as tool)
- Import of ASCII files direct to emulator
- Import of binary files (without AMSDOS header)
  directly to RAM
- New method to save screenshots
  (Now the real screen-content is saved)
- Changed all file-choosers to faster ones
- JavaCPC can show now the actual INKs palette
- VU-meter added to advanced options & on the 
  display if OSD is enabled
- Autotype console content is stored now.
  (Useful if you want to use it for coding)
- Autotype console content can be saved/ loaded now
- Printer console content can be saved now
- Java console content can be saved now
  (Useful for finding bugs)
- some other improvements

---------------------------------------------------------------------
Changes from ver. 5.6 to 5.7
- Advanced options (VHold & volume)
- Printer output (Very simple, needs improvements)
- Using CPC-Truetype font for autotype-console
- Some improvements

---------------------------------------------------------------------
Changes from ver. 5.5 to 5.6
- AY_3_8910 emulation bugfix (Now also Cauldron 1 music & FX should
  be played correctly)
- Better FDC-emulation
- FDC-command "Read Track" added
- Some minor bugfixes

---------------------------------------------------------------------
Changes from ver. 5.3 to 5.5
- Automatic update check
- You can save screenshots now!
- major bugfix in Z80 emulation!!! Many games and demos will work now!!!
- Prepared JavaCPC to handle CDT's (Reading not yet implemented)

---------------------------------------------------------------------

Changes from ver. 5.1 to 5.3
- Added "Save" option for all emulated drives
  You can now save all modified DSK manually back to your PC without
  enabled "Autosave" option!
- Some minor bugfixes
---------------------------------------------------------------------

Changes from ver. 5.0 to 5.1
- Removed bugs in Z80 emulation
- Fixed FDC emulation for demos like "Midline Process" from Arkos
- Some minor bugfixes
---------------------------------------------------------------------

Changes from ver. 4.1 to 5.0
- Added 'Scanlines'
- New RGB-emulation
- Some bugfixes
- New routines for Z80 emulation
- Better green-/grey monitor emulation
---------------------------------------------------------------------

Changes from ver. 3.8 to 4.1
- Added 'autotype' console
- improved features for web-usage (new parameters)
- new 'splash-image'
- better size-switch
- included a few buttons for typing commands (for cpc-newbies)

Changes from ver. 3.6t/r to 3.8
- Added internal Java-Console (also useable as simple notepad)
- Fixed some bug's in FDC-emulation
- Changed 'outfit'
- Improved OSD
- "Create blank data disc" implemented
- DSK's will be reloaded after changing system
-----------------------------


Changes from ver. 3.5d to 3.6t
- Added fullscreen feature
- Added "hide interface" function
- Added detailled keyboard info
-----------------------------


Changes from ver. 3.5b to 3.5d
- Some fixes in FDC-emulation
- Added 2 disc drives (Now You can access 4 disc drives using Future-OS or other 4-drives applications)
- Some fixes in change size button

-----------------------------
Changes from ver. 3.2c to 3.5b
- Included Future-OS 8
- FDC changes for using Future-OS
- "Stay on Top"-button included
- Disc insert / eject sounds included
- Fixed missing ROMs and CPC-Rom-selection

-----------------------------
Changes from ver. 3.2a to 3.2c
- Added "Turbo"-Button (This button speeds-up to 300%)

-----------------------------
Changes from ver. 3.2b to 3.3a
- Removed a bug in Debugger
- Colorized debugger
- If you open a snapshot, this will not be stored as filename for drive A/B. Only DSK are stored in JavaCPC.ini

-----------------------------
Changes from ver. 3.0 to 3.2b
- Added floppy-sound
- Notebook feature
- removed some bug's in floppy-sound

-----------------------------

Changes from ver. 1.3 to 3.0

- Vhold regulator
- Brightness regulator
- Volume regulator (Value is stored)
- Some minor BUG-fixes
- Animated JavaCPC-logo is shown in the application ;-)
- Cauldron 1 sound-patch button (Tihs button fixes the bad music and sfx output for the game Cauldron 1 / Hexenkueche 1, Stored to .ini file)
- Settings are stored to JavaCPC.ini now
- Enhancements for usage with CPCloader
- Autoboot-function will not erase last DSK from drive A/B in JavaCPC.ini
- New name: JavaCPC
- Now avaiable as Console-application (runs faster) and GUI-application, too
- Joystick settings will be stored to .ini file now

-----------------------------
Changes from ver. 1.2 to 1.3

- Better OSD with more information
- Green and Greyscale monitor emulation added
- Only 1 File for double and normal size emulation
- Pause Button added

-----------------------------
Changes from ver. 1.1 to 1.2

- OSD (On screen display) now shows the most important values like content for disk-drives.
  (You can press CTRL to show them)
- Pause Button - If clicked, emulation will be paused
- Keyboard/Joystick switch: You can now turn joystick emulation off/on.
  If switched on, german keyboard translation is active. Default is on.
- New CPC-models: Added CPC 6128 with french and spanish roms.

-----------------------------
Changes from ver. 1.0 to 1.1

- New GUI: The GUI looks now more userfriendly. Better useage.
- Audio button: You can turn audio output on or off now.

-----------------------------
Changes from ver. 0.9 to 1.0:

- Drive selector: now you can choose the Drive (A or B) where the files will be loaded to.

-----------------------------
Changes from ver. 0.8 to 0.9:

- Enhanced Floppy with dummy-save function
- Eject-Button (ejects the actual DSK-image from the diskdrive)
- New Style
- Optimized source-code (thanks to MaV)
