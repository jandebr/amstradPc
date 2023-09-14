/*
 * Switches.java
 *
 * Used for switching some variables in JEMU
 *
 * @author Markus
 *
 * Created on 12. August 2007, 11:59
 */

package jemu.ui;

import java.awt.image.*;

public class Switches {

  public static boolean firstStart = true;
    public static int CRTC = 1;
    public static boolean uncompressed = false;
    public static boolean changePolarity = false;
    public static boolean lightGun = false;
    public static long availmem;

    public static boolean overrideP = false;
    public static boolean askDrive = false;
    public static boolean doIntack = true;
    public static boolean breakpoints, breakinsts = false;
    public static int computername = 6; // 0 = Isp, 1 = Triumph, 2 = Saisho
                                        // 3 = Solavox, 4 = Awa, 5 = Schneider
                                        // 6 = Orion, 7 = Amstrad
    public static boolean khz44 = false;
    public static boolean khz11 = false;
    public static boolean loaded = false;
    public static int numberOfTracks, track;
    public static int drive = 0;
    public static String choosenname="";
    public static boolean saveauto, neverOverwrite, checksave = false;
    public static boolean floppyturbo = false;
    public static int getfromautotype = 0;
    public static boolean autofire = false;
    public static String ROM="";
    public static boolean export = false;
    public static boolean write = false;
    public static boolean CNG = false;
    public static boolean blockKeyboard;
    public static String name ="";
    public static int Blastervolume = 100;
    public static boolean VSoftOutput, ayeffect, CPCE95, KAYOut, linear = false;
    public static boolean digiblaster = false;
    public static boolean poke = false;
    public static boolean scores = false;
    public static boolean saveScr = false;
    public static BufferedImage image;
    public static boolean dskcheck = false;
    public static boolean savealldsk = false;
    public static boolean Printer;
    public static boolean Expansion;
    public static boolean printEffect = false;
    
    public static boolean save64 = false;
    public static boolean save128 = false;
    public static boolean save256 = false;
    public static boolean save512 = false;
    public static boolean BINImport = false;
    public static boolean showPalette = false;
  public static boolean ScanLines = false;
  public static boolean unprotect = false;
  public static String    LOWER = "none";
  public static String    UPPER_0 = "none";
  public static String    UPPER_1 = "none";
  public static String    UPPER_2 = "none";
  public static String    UPPER_3 = "none";
  public static String    UPPER_4 = "none";
  public static String    UPPER_5 = "none";
  public static String    UPPER_6 = "none";
  public static String    UPPER_7 = "none";
  public static String    UPPER_8 = "none";
  public static String    UPPER_9 = "none";
  public static String    UPPER_A = "none";
  public static String    UPPER_B = "none";
  public static String    UPPER_C = "none";
  public static String    UPPER_D = "none";
  public static String    UPPER_E = "none";
  public static String    UPPER_F = "none";
        
  
  public static String romlow = "0";
  public static String rom0 = "0";
  public static String rom1 = "0";
  public static String rom2 = "0";
  public static String rom3 = "0";
  public static String rom4 = "0";
  public static String rom5 = "0";
  public static String rom6 = "0";
  public static String rom7 = "0";
  public static String rom8 = "0";
  public static String rom9 = "0";
  public static String rom10 = "0";
  public static String rom11 = "0";
  public static String rom12 = "0";
  public static String rom13 = "0";
  public static String rom14 = "0";
  public static String rom15 = "0";
  
  
  public static boolean executable;
  public static boolean devil, digi, digimc, digipg;
        public static boolean Maxam;
        public static String Memory = "TYPE_512K";
        public static String loadauto;
        public static String move;
        public static String directxL = "Stop";
        public static String directyD = "Stop";
        public static String directxR = "Stop";
        public static String directyU = "Stop";
        public static int directL,directR,directU,directD;
        public static boolean MouseJoy;

        public static boolean bilinear          = false;
        public static boolean stretch           = true;
        public static boolean doublesize        = false;
        public static boolean triplesize        = false;
        public static int     booter            = 0;
        public static int     top               = 0;
        public static int     turbo             = 1;
        public static boolean osddisplay        = true;
        public static boolean autosave          = true;
        public static boolean autoboot          = true;
        public static boolean FloppySound       = true;
        public static boolean notebook          = false;
        public static boolean showLogoAtLaunch	= false;
        public static String loadname           = "";                     // name of medium loaded
        public static String loaddrivea         = "Drive is empty.";  // Message for Drive 0
        public static String loaddriveb         = "Drive is empty.";  // Message for Drive 1
        public static String loaddrivec         = "Drive is empty.";  // Message for Drive 2
        public static String loaddrived         = "Drive is empty.";  // Message for Drive 3
        public static int computersys           = 0;                    // variable for drive selector
        public static int audioenabler          = 1;                    // 1 = audio on by start, 0 = off
        public static int joystick              = 1;                    // keyboardset 1 will be used
        public static int monitormode           = 0;                    // variable for emulated INKs (CPC)
        public static double volume                = 1.0;
        public static int luminance             = 255;
        public static int vhold                 = 45;
        public static int diagnose                 = 0;

        public static boolean stagedDisplayRendering = false;
        public static boolean autonomousDisplayRendering = true;
}