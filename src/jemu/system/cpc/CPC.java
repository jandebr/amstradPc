package jemu.system.cpc;

import java.applet.Applet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import jemu.core.samples.Samples;
import jemu.core.Util;
import jemu.core.cpu.Processor;
import jemu.core.device.Computer;
import jemu.core.device.DeviceMapping;
import jemu.ui.Switches;
import jemu.core.device.crtc.Basic6845;
import jemu.core.device.floppy.Drive;
import jemu.core.device.floppy.UPD765A;
import jemu.core.device.floppy.virtualDrive;
import jemu.core.device.io.PPI8255;
import jemu.core.device.memory.Memory;
import jemu.core.device.tape.*;
import jemu.ui.Display;
import jemu.util.diss.Disassembler;
import jemu.util.diss.DissZ80;
import jemu.ui.Autotype;
import jemu.core.device.sound.*;
import jemu.ui.UpdateInfo;
import jemu.settings.Settings;
import java.util.zip.*;

import javazoom.jl.converter.*;

/**
* Title:        JavaCPC
* Description:  The Java CPC Emulator
* Copyright:    Copyright (c) 2002-2008
* Company:
* @author:      Markus, Richard (JEMU)
*/

public class CPC extends Computer{


    protected int track;
    public static boolean showAudioCapture, fromCapture = false;
    public static int resetInk = 0;
    public static boolean changeBorder = false;
    int mp3count = 0;
    String mp3name="";
        Frame mp3 = new Frame();
        JButton conv = new JButton("Reading MP3...");
    public static int bitrate = 8;
    public Converter mp3c = new Converter();
protected long checkmem = 2147483647;
  public static  boolean doOptimize = false;
  public static boolean doRecord = false;
protected int tapestopper = 0;
public static boolean Bypass = false;
    public static boolean shouldquit = false;
    public static boolean trueaudio = false;
 //   protected boolean hidetape = false;voi
    public static boolean tape_stereo = false;
    protected boolean isCDT = false;
    public static String downloadname="";
    public static boolean download = false;
    public static String downstring="";
    public static int DoDownload = 0;
    public static String Oldpage="";
    public static boolean shouldBoot = false;

    // Tape variables
    protected int turbocount = 0;
    protected int tapedistort = 0x00;
    public static int recordcount = 0;
    protected byte TapeRecbyte = 0x00;
    public static boolean savetape = false;
    public static int     tape_delay                      = 22;
    public static int       tapestarttimer                  = 0;
    public static int       playcount , number;
    protected int           doLoad;
    public static boolean   inserttape , tapeloaded         = false;
    public static String    loadtape;
    protected int           portB                           = 0;
    public static byte[]    tapesample;
    protected boolean       tapeEnabled                     = true;
    public int              tapesound , tapesoundb                      = 0;
    public static boolean   playing, savecheck, stoptape,
                            relay, rec, rew, play, ffwd,
                            tapedeck                        = false;
    public static TapeDeck  TapeDrive                       = new TapeDeck();

    // YM Player/recorder variables

    protected boolean       st_mode, zx_mode    = false;
    public boolean          YM_Interleaved      = false;
    public static int       YM_registers        = 16;
    public static boolean   oldYM               = false;
    public int[]            YM_Data             = new int[1000000];
    public int[]            YM_DataInterleaved  = new int[1000000];
    public static int       ymcount             = 0;
    public static int       YM_RecCount         = 0;
    public static int       YM_vbl              = 0;
    public static boolean   YM_Rec              = false;
    public static boolean   YM_Play             = false;
    public static boolean   YM_Stop             = false;
    public static boolean   YM_Save             = false;
    public static boolean   YM_Load             = false;
    public static boolean   atari_st_mode, spectrum_mode= false;
    public boolean          shouldcount         = false;
    public int              begincount          = 0;
    public  static  int     vcount, YM_Minutes,
                            YM_Seconds, msecs = 0;
    public String           YMtitle             = "";
    public String           YMauthor            = "";
    public String           YMcreator           = "";
    JCheckBox               YMInterleaved       = new JCheckBox("Interleaved");

    public static int timefire = 2;

    public int previousPortValue = 0;

    public int launchcount, blastercount, launchcode,launchcaul = 0;
    public static int savetimer, saveOnExit, saveOn = 0;
    public static boolean df0mod, df1mod, df2mod, df3mod = false;
    public boolean fired = false;
    public int firetimer;
    JCheckBox AmHeader = new JCheckBox("Write AMSDOS header");
    public boolean soverscan = false;
    JCheckBox Overscan = new JCheckBox("Save overscan screen?");
    JTextField AddressA = new JTextField();
    JTextField AddressB = new JTextField();
    JTextField SetByte = new JTextField();
    private int startimageA = 0x4000;
    private int startimageB = 0xC000;
    boolean amheader = false;
    public String MP3_HEADER_A = "ÿûÄ";
    public String MP3_HEADER_B = "ID3";
    public String CNG_HEADER ="CNGSOFT's LZ";
    public boolean CNG = false;
    public boolean CNGBIN = false;
    protected String CSW_HEADER = "Compressed Square Wave";
    protected String WAV_HEADER = "RIFF";
    protected int[] WAV_HEADER_11KHz = {
        0x52,0x49,0x46,0x46,0xD9,0xFC,0xA4,0x00,0x57,0x41,
        0x56,0x45,0x66,0x6D,0x74,0x20,0x10,0x00,0x00,0x00,
        0x01,0x00,0x01,0x00,0x11,0x2b,0x00,0x00,0x11,0x2b,
        0x00,0x00,0x01,0x00,0x08,0x00,0x64,0x61,0x74,0x61,
        0xB5,0xFC,0xA4,0x00
    };
    protected int[] WAV_HEADER_22KHz = {
        0x52,0x49,0x46,0x46,0xD9,0xFC,0xA4,0x00,0x57,0x41,
        0x56,0x45,0x66,0x6D,0x74,0x20,0x10,0x00,0x00,0x00,
        0x01,0x00,0x01,0x00,0x22,0x56,0x00,0x00,0x22,0x56,
        0x00,0x00,0x01,0x00,0x08,0x00,0x64,0x61,0x74,0x61,
        0xB5,0xFC,0xA4,0x00
    };
    protected int[] WAV_HEADER_44KHz = {
        0x52,0x49,0x46,0x46,0xD9,0xFC,0xA4,0x00,0x57,0x41,
        0x56,0x45,0x66,0x6D,0x74,0x20,0x10,0x00,0x00,0x00,
        0x01,0x00,0x01,0x00,0x44,0xac,0x00,0x00,0x44,0xac,
        0x00,0x00,0x01,0x00,0x08,0x00,0x64,0x61,0x74,0x61,
        0xB5,0xFC,0xA4,0x00
    };

    public int[] SCR_HEADER = {
        0x00,0x4A,0x41,0x56,0x41,0x43,0x50,0x43,
        0x20,0x53,0x43,0x52,0x00,0x00,0x00,0x00,
        0x00,0x00,0x02,0x00,0x00,0x00,0xC0,0x00,
        0x00,0x40,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x40,0x00,0x42,0x04,0x00,0x00,0x4A,
        0x41,0x56,0x41,0x43,0x50,0x43,0x20,0x24,
        0x24,0x24,0xFF,0x00,0xFF,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00
    };

    public int[] PAL_HEADER = {
        0x00,0x11,0x16,0x0D,0x12,0x16,0x20,0x20,
        0x20,0x50,0x41,0x4C,0x00,0x00,0x00,0x00,
        0x00,0x00,0x02,0xEF,0x00,0x09,0x88,0x00,
        0xEF,0x00,0x09,0x88,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0xEF,0x00,0x00,0x8A,0x05,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00
    };

    public int[] PAL_INK = {
        0x54,0x44,0x55,0x5c,0x58,0x5d,0x4c,0x45,
        0x4d,0x56,0x46,0x57,0x5e,0x40,0x5f,0x4e,
        0x47,0x4f,0x52,0x42,0x53,0x5a,0x59,0x5b,
        0x4a,0x43,0x4b,0x54,0x54,0x54,0x54
    };

    public int[] SCR_CODE =  // Code to put to #C7D0
    {
        0x3A, 0xD0, 0xD7,               //      LD      A,  (#D7D0)
        0xCD, 0x1C, 0xBD,               //      CALL    #BD1C
        0x21, 0xD1, 0xD7,               //      LD      HL, #D7D1
        0x46,                           //      LD      B,  (HL)
        0x48,                           //      LD      C,  B
        0xCD, 0x38, 0xBC,               //      CALL    #BC38
        0xAF,                           //      XOR     A
        0x21, 0xD1, 0xD7,               //      LD      HL, #D7D1
        0x46,                           // BCL: LD      B,  (HL)
        0x48,                           //      LD      C,  B
        0xF5,                           //      PUSH    AF
        0xE5,                           //      PUSH    HL
        0xCD, 0x32, 0xBC,               //      CALL    #BC32
        0xE1,                           //      POP     HL
        0xF1,                           //      POP     AF
        0x23,                           //      INC     HL
        0x3C,                           //      INC     A
        0xFE, 0x10,                     //      CP      #10
        0x20, 0xF1,                     //      JR      NZ,BCL
        0xC3, 0x18, 0xBB                //      JP      #BB18
    };

    public static boolean FDCReset = false;
    public CPCDiscImage dskImage;
    public CPCDiscImage dskImageA;
    public CPCDiscImage dskImageB;
    public CPCDiscImage dskImageC;
    public CPCDiscImage dskImageD;

    JFrame dummyB = new JFrame();
    protected GridBagConstraints gbcConstraints   = null;

    String[] Palette = new String[33];
    Color[] Palcols = {
                  /*R G B */
        new Color(0x000000), /* 0*/
        new Color(0x000060), /* 1*/
        new Color(0x0000FF), /* 2*/
        new Color(0x600000), /* 3*/
        new Color(0x600060), /* 4*/
        new Color(0x6000FF), /* 5*/
        new Color(0xFF0000), /* 6*/
        new Color(0xFF0060), /* 7*/
        new Color(0xFF00FF), /* 8*/
        new Color(0x006000), /* 9*/
        new Color(0x006060), /*10*/
        new Color(0x0060FF), /*11*/
        new Color(0x606000), /*12*/
        new Color(0x606060), /*13*/
        new Color(0x6060FF), /*14*/
        new Color(0xFF6000), /*15*/
        new Color(0xFF6060), /*16*/
        new Color(0xFF60FF), /*17*/
        new Color(0x00FF00), /*18*/
        new Color(0x00FF60), /*19*/
        new Color(0x00FFFF), /*20*/
        new Color(0x60FF00), /*21*/
        new Color(0x60FF60), /*22*/
        new Color(0x60FFFF), /*23*/
        new Color(0xFFFF00), /*24*/
        new Color(0xFFFF60), /*25*/
        new Color(0xFFFFFF), /*26*/
        new Color(0x606060), /*27*/
		new Color(0xFF0060), /*28*/
        new Color(0xFFFF60), /*29*/
		new Color(0x000060), /*30*/
        new Color(0x00FF60)  /*31*/};

  public int joyreader;
  protected boolean turbo = false;
  protected int turbotimer;
  public int autotyper = 0;
  public int readkey  = 0;

  int[] eventArray;
  boolean[] shifter;
  public static boolean shift;


  // Port mappings
  protected static final int   PSG_PORT_A        = -1;
  protected static final int   PPI_PORT_B        = -2;
  protected static final int   PPI_PORT_C        = -3;

  public static int autotype = 4;
  protected static final int   CYCLES_PER_SECOND_CPC = 1000000;
  protected static final int   CYCLES_PER_SECOND_ST = 2000000;
  protected static final int   CYCLES_PER_SECOND_ZX = 1773400;
  protected static final int   AUDIO_TEST        = 0x40000000;

  protected String             lowerROM          = null;
  protected Hashtable          upperROMs         = new Hashtable();
  protected Z80                z80               = new Z80(CYCLES_PER_SECOND_CPC);
  public static CPCMemory          memory            = null;                                                      // new
  // CPCMemory(CPCMemory.TYPE_512K);
  protected Basic6845          crtc              = (Basic6845) addDevice(new Basic6845());
  protected GateArray          gateArray         = (GateArray) addDevice(new GateArray(this));
  protected PPI8255            ppi               = (PPI8255) addDevice(new PPI8255());
  protected UPD765A            fdc               = (UPD765A) addDevice(new UPD765A(4));  // 4 cycles per call to cycle()
  protected AY_3_8910          psg               = (AY_3_8910) addDevice(new AY_3_8910());
  protected Disassembler       disassembler      = new DissZ80();
  protected int                audioAdd          = psg.getSoundPlayer().getClockAdder(AUDIO_TEST,
                                                                                           CYCLES_PER_SECOND_CPC);
  protected int                audioCount        = 0;
  protected Drive[]            floppies          = new Drive[4];

  protected KeyboardB          keyboardb         = new KeyboardB();
  protected KeyboardA          keyboarda         = new KeyboardA();
  protected KeyboardN          keyboardn         = new KeyboardN();

  // Conversion of CPC BDIR & BC1 values 0..3 to PSG values

  protected final int[] PSG_VALUES = new int[] {
    psg.BC2_MASK,
    psg.BC2_MASK | psg.BC1_MASK,
    psg.BC2_MASK | psg.BDIR_MASK,
    psg.BC2_MASK | psg.BDIR_MASK | psg.BC1_MASK
  };

  protected String CPCname ="";
  public CPC(Applet applet, String name) {
    super(applet, name);
    final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        mp3.add(conv);
        conv.setFocusable(false);
        conv.setEnabled(false);
        mp3.setUndecorated(true);
        mp3.pack();
        mp3.setAlwaysOnTop(true);
        mp3.setLocation((d.width - mp3.getSize().width) / 2, (d.height - mp3.getSize().height) / 2);
        mp3.setVisible(false);
    initCPCType(name);
    z80.setMemoryDevice(memory);
    z80.addInputDeviceMapping(new DeviceMapping(memory, 0x0101, 0x0000));  // Emulator detection
    z80.addOutputDeviceMapping(new DeviceMapping(memory, 0x2000, 0x0000)); // ROM Select
    z80.setInterruptDevice(gateArray);
    z80.addOutputDeviceMapping(new DeviceMapping(gateArray, 0xc000, 0x4000)); // All GA functions
    z80.setCycleDevice(this);
    crtc.setRegisterSelectMask(0x0100, 0x0000);
    crtc.setCRTCListener(gateArray);
    z80.addOutputDeviceMapping(new DeviceMapping(crtc, 0x4000, 0x0000));
    ppi.setPortMasks(0x0100, 0x0100, 0x0200, 0x0200);
    ppi.setReadDevice(PPI8255.PORT_B, this, PPI_PORT_B);
    ppi.setWriteDevice(PPI8255.PORT_C, this, PPI_PORT_C);
    ppi.setReadDevice(PPI8255.PORT_A, psg, 0);
    ppi.setWriteDevice(PPI8255.PORT_A, psg, 0);
    psg.setReadDevice(psg.PORT_A, this, PSG_PORT_A);
    psg.setClockSpeed(CYCLES_PER_SECOND_CPC);
    z80.addOutputDeviceMapping(new DeviceMapping(ppi, 0x0800, 0x0000));
    z80.addInputDeviceMapping(new DeviceMapping(ppi, 0x0800, 0x0000));
    z80.addOutputDeviceMapping(new DeviceMapping(fdc, 0x0580, 0x0100));
    z80.addInputDeviceMapping(new DeviceMapping(fdc, 0x0580, 0x0100));
    z80.addOutputDeviceMapping(new DeviceMapping(this, 0x581, 0x0000));
    for (int i = 0; i < 4; i++){
        fdc.setDrive(i, floppies[i] = new Drive(i == 0 ? 1 : 2));
        fdc.setDrive(i, null);
    }
    setBasePath("cpc");
  }

  private void initCPCType(String name) {
    CPCname = name;
    // check for MAXAM assembler
    if (Switches.Maxam)
      upperROMs.put("14", "MAXAM.zip");

    // Set memorytype for all models
      if (Switches.Memory.equals("TYPE_512K"))
        memory = new CPCMemory(CPCMemory.TYPE_512K);
      else
      if (Switches.Memory.equals("TYPE_256K"))
        memory = new CPCMemory(CPCMemory.TYPE_256K);
      else
      if (Switches.Memory.equals("TYPE_128K"))
        memory = new CPCMemory(CPCMemory.TYPE_128K);
      else
      if (Switches.Memory.equals("TYPE_64K"))
        memory = new CPCMemory(CPCMemory.TYPE_64K);
      else
      if (Switches.Memory.equals("TYPE_SILICON_DISC"))
        memory = new CPCMemory(CPCMemory.TYPE_SILICON_DISC);
      else
      if (Switches.Memory.equals("TYPE_128_SILICON_DISC"))
        memory = new CPCMemory(CPCMemory.TYPE_128_SILICON_DISC);
      else
      memory = new CPCMemory(CPCMemory.TYPE_512K);
    //
      System.out.println("Memory choosen: " + Switches.Memory);
      if ("CPC464".equals(name)) {
      lowerROM = "OS464.zip";
      upperROMs.put("0", "BASIC1-0.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("CPC464T".equals(name)) {
      lowerROM = "OS464.zip";
      upperROMs.put("0", "BASIC1-0.zip");
    } else if ("CPC664".equals(name)) {
      lowerROM = "OS664.zip";
      upperROMs.put("0", "BASIC664.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("CPC464PARA".equals(name)) {
      lowerROM = "OS464.zip";
      upperROMs.put("0", "BASIC1-0.zip");
      upperROMs.put("7", "PARADOS.zip");
    } else if ("CPC6128".equals(name)) {
      lowerROM = "OS6128.zip";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("ST128".equals(name)) {
      lowerROM = "OS6128.zip";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("1", "STK1.zip");
      upperROMs.put("2", "STK2.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("ART".equals(name)) {
      lowerROM = "OS6128.zip";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("1", "ART0.zip");
      upperROMs.put("2", "ART1.zip");
      upperROMs.put("3", "ART2.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("MEGA".equals(name)) {
      lowerROM = "MOS6128.ZIP";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("1", "STK1.zip");
      upperROMs.put("2", "STK2.zip");
      upperROMs.put("3", "ART0.zip");
      upperROMs.put("4", "ART1.zip");
      upperROMs.put("5", "ART2.zip");
      upperROMs.put("14", "locksmit.zip");
      upperROMs.put("7", "AMSDOS.zip");
      upperROMs.put("8", "sym-romA.zip");
      upperROMs.put("9", "sym-romB.zip");
      upperROMs.put("10", "sym-romC.zip");
      upperROMs.put("11", "sym-romD.zip");
      upperROMs.put("12", "ROMPKP1.zip");
      upperROMs.put("13", "ROMPKP2.zip");
    } else if ("PARADOS".equals(name)) {
      lowerROM = "OS6128.zip";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("7", "PARADOS.zip");
    } else if ("ROMPACK".equals(name)) {
      lowerROM = "OS6128.zip";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("1", "ROMPKP1.zip");
      upperROMs.put("2", "ROMPKP2.zip");
      upperROMs.put("7", "PARADOS.zip");
    } else if ("ROMSYM".equals(name)) {
      lowerROM = "OS6128.zip";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("1", "sym-romA.zip");
      upperROMs.put("2", "sym-romB.zip");
      upperROMs.put("3", "sym-romC.zip");
      upperROMs.put("4", "sym-romD.zip");
      upperROMs.put("5", "ROMPKP1.zip");
      upperROMs.put("6", "ROMPKP2.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("CPC6128fr".equals(name)) {
      lowerROM = "OSFR.zip";
      upperROMs.put("0", "BASICFR.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("CPC6128es".equals(name)) {
      lowerROM = "OSES.zip";
      upperROMs.put("0", "BASICES.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("SymbOS".equals(name)) {
      lowerROM = "OS6128.zip";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("7", "AMSDOS.zip");
      upperROMs.put("1", "sym-romA.zip");
      upperROMs.put("2", "sym-romB.zip");
      upperROMs.put("3", "sym-romC.zip");
      upperROMs.put("4", "sym-romD.zip");
      } else if ("FutureOS".equals(name)) {
      lowerROM = "OS6128.zip";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("7", "AMSDOS.zip");
      upperROMs.put("10", "T-OG-E-A.zip");
      upperROMs.put("11", "T-OG-E-B.zip");
      upperROMs.put("12", "T-OG-E-C.zip");
      upperROMs.put("13", "T-OG-E-D.zip");
    } else if ("KCcomp".equals(name)) {
      lowerROM = "KCCOS.zip";
      upperROMs.put("0", "KCCBAS.zip");
    } else if ("COMCPC".equals(name)) {
      lowerROM = "-OS.zip";
      upperROMs.put("0", "-BASIC1-1.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("JEMCPC".equals(name)) {
      lowerROM = "jemuOS6128.zip";
      upperROMs.put("0", "BASIC1-1.zip");
      upperROMs.put("7", "AMSDOS.zip");
    } else if ("CPCPLUS".equals(name)) {
      lowerROM = "os-plus.zip";
      upperROMs.put("0", "basic-plus.zip");
      upperROMs.put("7", "romplus.zip");
    } else if ("ROMSETTER".equals(name)) {
      lowerROM = "OS6128.zip";
    }
    // Manual rom settings

    else if ("CUSTOM".equals(name)) {
      if (!Switches.LOWER.equals("none"))    lowerROM = Switches.LOWER;
      if (!Switches.UPPER_0.equals("none"))  upperROMs.put("0", Switches.UPPER_0);
      if (!Switches.UPPER_1.equals("none"))  upperROMs.put("1", Switches.UPPER_1);
      if (!Switches.UPPER_2.equals("none"))  upperROMs.put("2", Switches.UPPER_2);
      if (!Switches.UPPER_3.equals("none"))  upperROMs.put("3", Switches.UPPER_3);
      if (!Switches.UPPER_4.equals("none"))  upperROMs.put("4", Switches.UPPER_4);
      if (!Switches.UPPER_5.equals("none"))  upperROMs.put("5", Switches.UPPER_5);
      if (!Switches.UPPER_6.equals("none"))  upperROMs.put("6", Switches.UPPER_6);
      if (!Switches.UPPER_7.equals("none"))  upperROMs.put("7", Switches.UPPER_7);
      if (!Switches.UPPER_8.equals("none"))  upperROMs.put("8", Switches.UPPER_8);
      if (!Switches.UPPER_9.equals("none"))  upperROMs.put("9", Switches.UPPER_9);
      if (!Switches.UPPER_A.equals("none"))  upperROMs.put("10", Switches.UPPER_A);
      if (!Switches.UPPER_B.equals("none"))  upperROMs.put("11", Switches.UPPER_B);
      if (!Switches.UPPER_C.equals("none"))  upperROMs.put("12", Switches.UPPER_C);
      if (!Switches.UPPER_D.equals("none"))  upperROMs.put("13", Switches.UPPER_D);
      if (!Switches.UPPER_E.equals("none"))  upperROMs.put("14", Switches.UPPER_E);
      if (!Switches.UPPER_F.equals("none"))  upperROMs.put("15", Switches.UPPER_F);
    }
    Switches.computersys = 1;
    if (Switches.Expansion)
      upperROMs.put("6", "JAVACPC.zip");
  }

  @Override
  public void initialise() {
                  AddressA.setEnabled(false);
                  AddressB.setEnabled(false);
                  AddressA.setText("");
                  AddressB.setText("C000");
        AmHeader.addItemListener(new CheckBoxListener());
        AmHeader.setSelected(true);
        YMInterleaved.addItemListener(new CheckBoxListener());
        Overscan.addItemListener(new CheckBoxListener());
        YMInterleaved.setSelected(YM_Interleaved);
    ymcount = 0;
    YM_RecCount = 0;
    YM_vbl = 0;
    YM_Rec = false;
    YM_Play = false;
    YM_Stop = false;
    YM_Save = false;
    YM_Load = false;
    atari_st_mode = false;
    spectrum_mode = false;
    shouldcount = false;
    begincount = 0;
    YM_Minutes = 0;
    YM_Seconds = 0;
    YMControl.YM_Counter.setText(" 00:00\"00 ");
    String romNum;
    String romName;
    if (lowerROM != null){
    if (lowerROM.toLowerCase().startsWith("custom:")){
        lowerROM = lowerROM.substring(7, (lowerROM.length()));
        memory.setLowerROM(getFile(lowerROM));
    }
    else
        memory.setLowerROM(getFile(romPath + lowerROM));}
   // memory.setMultiROM(getFile(romPath + "MULTFACE.zip"));
    Set roms = upperROMs.keySet();
    Iterator romIt = roms.iterator();
    while (romIt.hasNext()) {
      romNum = (String) romIt.next();
      romName = (String) upperROMs.get(romNum);
    if (romName.toLowerCase().startsWith("custom:")){
        romName = romName.substring(7, (romName.length()));
        memory.setUpperROM((new Byte(romNum)).byteValue(), getFile(romName));
    }
      else
        memory.setUpperROM((new Byte(romNum)).byteValue(), getFile(romPath + romName));
    }
    upperROMs.clear();
    gateArray.setMemory(memory.getMemory());
    super.initialise();
    psg.getSoundPlayer().play();

  }

  @Override
  public void reset() {
      if (!recordKeys){
      playKeys = false;
      keyNumber = 0;
      recordKeys = false;
      }
      
             YMControl.displaycount1 = 0;
             YMControl.displaycount2 = 0;
             YMControl.DisplayStart = 0;
     // number = 0;
     // doLoad = 0;
      psg.changeClockSpeed(CYCLES_PER_SECOND_CPC);
      st_mode = false;
      zx_mode = false;
     if (YM_Play){
          YM_Play = false;
          YM_Stop = true;
          System.out.println("Playback stopped...");
     }
      
    turbo = false;
    Switches.turbo = 1;
    Display.autotype = 0;
      try {
          keyboarda.keyReleased(eventArray[readkey]);
      }
      catch (Exception e) {}
    Switches.blockKeyboard = false;
    autotyper = 0;
    stop();
    super.reset();
    fdc.reset();
    memory.reset();
    reSync();
    z80.reset();
      gateArray.init();
    start();
  }

  public void setInks(){
    for(int i = 0; i < 15; i++)
      gateArray.setInk(i, Util.random(30));
  }
  public void setBorder(int ink){
      gateArray.setInk(16, ink);
  }
  public void setBorderBlack(){
      gateArray.setInk(16, 20);
  }

  @Override
  public void eject() {
    int drive = getCurrentDrive();
    if (drive == 0){
        checkDF0();
        dskImageA = null;
      df0mod = false;}
    if (drive == 1){
        checkDF1();
        dskImageB = null;
      df1mod = false;}
    if (drive == 2){
        checkDF2();
        dskImageC = null;
      df2mod = false;}
    if (drive == 3){
        checkDF3();
        dskImageD = null;
      df3mod = false;}
    if (Switches.FloppySound && Switches.audioenabler == 1)
        Samples.EJECT.play();
    {
    fdc.setDrive(getCurrentDrive(), null);
      if (getCurrentDrive() == 0)
          Switches.loaddrivea = "Drive is empty.";
      if (getCurrentDrive() == 1)
          Switches.loaddriveb = "Drive is empty.";
      if (getCurrentDrive() == 2)
          Switches.loaddrivec = "Drive is empty.";
      if (getCurrentDrive() == 3)
          Switches.loaddrived = "Drive is empty.";
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    psg.getSoundPlayer().dispose();
  }

  @Override
  public void cycle() {
      if (Switches.breakinsts && z80.getPC() != 0 && (PEEK(z80.getPC()-1) == 0xED && PEEK(z80.getPC()) == 0xFF)){
          jemu.ui.JEMU.debugthis.setSelected(true);
          Samples.BREAKI.play();
          z80.stop();
      }
      if (Switches.overrideP)
          if (Switches.ROM.equals("CPC6128") || Switches.ROM.equals("CPC664"))
              POKE(0xAE2C,0);
          else
              POKE (0xAE45,0);

      if (shouldquit)
          start();

      if ((relay && play) || (trueaudio && play))
          tapeCycle();

      if (FDCReset){
          fdc.resetb();
          FDCReset = false;
      }
      gateArray.cycle();
      fdc.cycle();
      if ((audioCount += audioAdd / Switches.turbo) >= AUDIO_TEST) {
          psg.writeAudio();
          audioCount -= AUDIO_TEST;
      }
  }

  @Override
  public void setDisplay(Display value) {
    super.setDisplay(value);
    gateArray.setDisplay(value);
  }

  @Override
  public void setFrameSkip(int value) {
    super.setFrameSkip(value);
    gateArray.setRendering(value == 0);
  }

  public void checkDF0(){
      if (Switches.checksave){
           Frame dummy2 = new Frame();
              if (df0mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF0 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.YES_OPTION)
                                 AutoSave(0);
              }
           df0mod = false;
      }
  }
  public void checkDF1(){
      if (Switches.checksave){
           Frame dummy2 = new Frame();
              if (df1mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF1 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.YES_OPTION)
                                 AutoSave(1);
              }
           df1mod = false;
      }
  }
  public void checkDF2(){
      if (Switches.checksave){
           Frame dummy2 = new Frame();
              if (df2mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF2 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.YES_OPTION)
                                 AutoSave(2);
              }
           df2mod = false;
      }
  }
  public void checkDF3(){
      if (Switches.checksave){
           Frame dummy2 = new Frame();
              if (df3mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF3 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.YES_OPTION)
                                 AutoSave(3);
              }
           df3mod = false;
      }
  }

  public static void checkSave(){
            if (Switches.checksave){
           Frame dummy2 = new Frame();
              if (df0mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF0 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.NO_OPTION)
                                 df0mod=false;
              }
              if (df1mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF1 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.NO_OPTION)
                                 df1mod=false;
              }
              if (df2mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF2 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.NO_OPTION)
                                 df2mod=false;
              }
              if (df3mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF3 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.NO_OPTION)
                                 df3mod=false;
              }
      } else
          df0mod = df1mod = df2mod = df3mod = false;
      saveOn = 1;
  }
  public static void checkSaveOnExit(){
      shouldquit = true;
      if (Switches.checksave){
           Frame dummy2 = new Frame();
              if (df0mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF0 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.NO_OPTION)
                                 df0mod=false;
              }
              if (df1mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF1 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.NO_OPTION)
                                 df1mod=false;
              }
              if (df2mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF2 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.NO_OPTION)
                                 df2mod=false;
              }
              if (df3mod){
              int ok = JOptionPane.showConfirmDialog(dummy2,
                      "Your DSK in drive DF3 has been modified.\n" +
                      "Do you want to save it now?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.NO_OPTION)
                                 df3mod=false;
              }
      } else
          df0mod = df1mod = df2mod = df3mod = false;
      saveOnExit = 1;
  }

  public void TapeSound(int value){
      if ((z80.getPC() > 0x02800 && z80.getPC() < 0x03000 ) && !savecheck){
      System.out.println("Tape is saving...");
      savecheck = true;
      }

        if ((value & 0x020) == 0x020){
            Display.tape = 4;
            tapesound = 0xda;
            psg.digiblast = true;

        }
        else
        {
            tapesound = 0x26;
        }
            psg.blasterA =  tapesound;
            psg.blasterB =  tapesound;
  }

  public void TapeRelayCheck(int value){
        previousPortValue = value;
        if ((value) == 0x10 && !relay) {
            relay = true;
            System.out.println("Tape-relay on");
          savecheck = false;
            if (Switches.FloppySound && !Bypass){
            Samples.RELAIS.play();
            if (play && !trueaudio)
            Samples.TAPEMOTOR.loop();
            if (ffwd && number >= (tapesample.length -1000))
            Samples.WINDMOTOR.loop();
            if (rew && number <=1000)
            Samples.REWINDMOTOR.loop();
            }
        }
        if ((value & 0x10) == 0x00 && relay){
            playing = false;
            stoptape = true;
            tapestopper = 1;
        }else{
            stoptape = false;
            playing = true;
            tapestopper = 0;
        }
  }

  public void tapeCycle(){
      if (!TapeDrive.paused){
      if (tapeloaded){
          if (!rec){
            playcount++;
            playing = true;
            doLoad++;
            if (number >=10)
                doLoad = 2000001;
              if (doLoad >= 2000000 && playcount > tape_delay){
                  if (Switches.floppyturbo && Switches.turbo == 1)
                      Switches.turbo = 2;
                  if (number >= tapesample.length){
                      play = false;
                      Samples.TAPEMOTOR.stop();
                      Samples.TAPESTOP.play();
                      TapeDrive.btnPLAY.setBackground(Color.DARK_GRAY);
                      TapeDrive.btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));
                      number--;
                      number--;
                      playcount = 0;
                      return;
                  }
                  portB = tapesample[number];

                  // avoid crackling noise when sliding 16 bit samples
                  if ((number & 1) == 1 && bitrate > 8) {
                      number++;
                  }
                  //

                  number++;
                  if (bitrate > 8){
                      portB = tapesample[number];
                      number++;}
                  playcount = 0;
                  if (trueaudio){
                      if (bitrate >8)
                      tapesound = portB;
                      else
                          tapesound = portB^0x80;
                  }
                  else
                      tapesound = (((portB)*(Switches.Blastervolume / 5))/100);
                  if (tape_stereo){
                      portB = tapesample[number];
                      number++;
                      if (bitrate > 8){
                          portB = tapesample[number];
                          number++;
                      }
                      if (bitrate >8)
                          tapesoundb = portB;
                      else
                          tapesoundb = portB^0x80;
                  }
                  else
                      tapesoundb=tapesound;

                  if (!trueaudio)
                      tapesoundb = tapesound;
                  if (Switches.FloppySound){

                      if (Switches.turbo == 1){
                          psg.digicount = 10;
                          if (psg.readRegister(7)!=0x3f && (psg.readRegister(8)!=0 || psg.readRegister(9)!=0 || psg.readRegister(10)!=0))
                              psg.digiblast = false;
                          else
                              psg.digiblast = true;
                          psg.blasterA =  tapesound;
                          psg.blasterB =  tapesoundb;
                      } else {
                          turbocount++;
                          if (turbocount == 20){
                              turbocount = 0;
                              psg.digicount = 1;
                           if (psg.readRegister(7)!=0x3f && (psg.readRegister(8)!=0 || psg.readRegister(9)!=0 || psg.readRegister(10)!=0))
                              psg.digiblast = false;
                          else
                              psg.digiblast = true;
                              psg.blasterA =  tapesound;
                              psg.blasterB =  tapesound;
                              TapeDrive.WAVBYTE = tapesound;
                          }
                      }
                  }
                  if (Switches.changePolarity)
                  portB = ~portB^0x80;
                  else
                  portB = portB^0x80;
                  if (portB < 0x030)
                      Display.tape = 1;
                  if (changeBorder || trueaudio){
                      if (portB >0x020 && portB<=0x090)
                          setBorder(28);
                      if (portB >0x090 && portB<=0x0a0)
                          setBorder(12);
                      if (portB >0x0a0 && portB<=0x0b0)
                          setBorder(14);
                      if (portB >0x0b0)
                          setBorder(3);
                      if (portB <=0x020)
                          setBorderBlack();
                  }
              }
          }
      }


          if (rec){
            playcount++;
            playing = true;
              if (playcount >= tape_delay){
                  if (Switches.floppyturbo && Switches.turbo == 1)
                      Switches.turbo = 2;


                  tapesample[number] = TapeRecbyte;
                  number++;
                  recordcount++;
                  if (number >= tapesample.length){
                      rec = false;
                      Samples.TAPEMOTOR.stop();
                      Samples.TAPESTOP.play();
                      TapeDrive.btnPLAY.setBackground(Color.DARK_GRAY);
                      TapeDrive.btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));
                      TapeDrive.btnREC.setBackground(new Color(0xff,0x00,0x00));
                      TapeDrive.btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
                      number--;
                      playcount = 0;
                  }
                  playcount = 0;
              }
          }

      }
  }

  public void TapeCheck(){
    if (tapeloaded){
      if (rew){
          if (TapeDeck.isMem && (TapeDeck.memCount >= TapeDeck.counter)){
              System.out.println("Tape REW stopped");
              TapeDrive.btnREW.setBorder(new BevelBorder(BevelBorder.RAISED));
              TapeDrive.btnREW.setBackground(Color.DARK_GRAY);
              play = false;
              rew = false;
              Samples.TAPEMOTOR.stop();
              Samples.REWINDMOTOR.stop();
              Samples.WINDMOTOR.stop();
              if (Switches.FloppySound)
                  Samples.TAPESTOP.play();
          }
          if (number >=25000)
              number = number - 25000;
          if (number >=2500)
              number = number - 2500;
          if (number >=1000)
              number = number - 300;
          if (number <= 800){
              number = 0;
              play = false;
              rew = false;
              Samples.TAPEMOTOR.stop();
              Samples.REWINDMOTOR.stop();
              Samples.WINDMOTOR.stop();
          }
      }
      if (ffwd){
          if (TapeDeck.isMem && (TapeDeck.memCount <= TapeDeck.counter)){
              System.out.println("Tape FF stopped");
              TapeDrive.btnFF.setBorder(new BevelBorder(BevelBorder.RAISED));
              TapeDrive.btnFF.setBackground(Color.DARK_GRAY);
              play = false;
              ffwd = false;
              Samples.TAPEMOTOR.stop();
              Samples.REWINDMOTOR.stop();
              Samples.WINDMOTOR.stop();
              if (Switches.FloppySound)
                  Samples.TAPESTOP.play();
          }
          if (number <= (tapesample.length-25000))
              number = number + 25000;
          else
          if (number <= (tapesample.length-2500))
              number = number + 2500;
          else
          if (number <= (tapesample.length-1000))
              number = number + 300;
          if (number >= (tapesample.length - 800)){
              number = tapesample.length-4;
              play = false;
              ffwd = false;
              Samples.TAPEMOTOR.stop();
              Samples.REWINDMOTOR.stop();
              Samples.WINDMOTOR.stop();
          }
      }
    }

      TapeDeck.counter = number / (2080000 / (byte)tape_delay);
      if (TapeDeck.counter <=9999)
          TapeDeck.before = "";
      if (TapeDeck.counter <=999)
          TapeDeck.before = "0";
      if (TapeDeck.counter <=99)
          TapeDeck.before = "00";
      if (TapeDeck.counter <=9)
          TapeDeck.before = "000";
      TapeDeck.TapeCounter.setText(TapeDeck.before + TapeDeck.counter);
      TapeDeck.positionslider.setValue(number);
  }

  public void StopTape(){
            //number = number + 60000;
            relay = false;
            playing = false;
            stoptape = false;
            doLoad=0;
              if (Switches.floppyturbo){
                  Switches.turbo = 1;
              }
            System.out.println("Tape-relay off");
            TapeDrive.showText();
            if (Switches.FloppySound && !Bypass){
            Samples.RELAISOFF.play();
            Samples.TAPEMOTOR.stop();
            }
  }
public static void Download (String download){
    System.out.println("Downloading " + download +"...");
    String ending = download.substring((download.length()-4), download.length());
    System.out.println("Downloading to buffer" + ending);
          String savename="buffer" + ending;
          downloadname = download;
          Switches.booter = 1;
          downstring = savename;
          DoDownload=1;
    }

public void Download(){
          String[]arg= {downloadname, downstring};
          jemu.ui.copyURL.main(arg);
      //    download = true;

}

public int PEEK(int address){
    return memory.readByte(address);
}

public void reSync(){
    psg.getSoundPlayer().resync();
}
  public void vSync() {
      if (resetInk > 0){
          resetInk = 0;
          gateArray.resetInks();
      }
      if (mp3count >0){
          mp3count++;
          if (mp3count == 10){
              MP3Load();
          }
      }
      if (doOptimize){
          optimizeWAV();
          doOptimize = false;
      }
      if (DoDownload >= 1){
          DoDownload++;
          if (DoDownload >=100){
              DoDownload = 0;
              Download();
          }
      }
      if (download){
          download =false;
          try{
              Switches.loaded=false;
          loadFile(0, downstring);
          UpdateInfo.pane.setPage(Oldpage);
          if (shouldBoot)
              bootDisk();
          }
          catch (Exception error){}
      }
      if (savetape){
          savetape=false;
          tape_WAV_save();
      }
      if (inserttape){
          inserttape = false;
          try {
          loadFile(0, loadtape);}
          catch (Exception emil){}
          tapestarttimer = 1;
      }
      if (tapestarttimer >=1){
          Switches.blockKeyboard = true;
          TapeDrive.buttonpressed = true;
          tapestarttimer++;

          if (tapestarttimer >=56){
              tapestarttimer = 0;
             // hidetape = true;
              play = true;
              TapeDrive.btnPLAY.setBorder(new BevelBorder(BevelBorder.LOWERED));
              TapeDrive.btnPLAY.setBackground(Color.BLACK);
              if (CPCname.equals("CPC464T") || CPCname.equals("KCcomp"))
                  AutoType("RUN\"\n\n\n");
              else
                  AutoType("|TAPE\nRUN\"\n\n\n");
          }
      }
      if (tapeEnabled){
      if ((play && playing && relay)||(play && playing && trueaudio) || rew || ffwd){
      TapeCheck();
      } 
      if (stoptape && tapestopper >=1){
          tapestopper++;
          if (tapestopper == 0x08){
          StopTape();
          tapestopper = 0;
          }
      }
      }
      if (saveOnExit >=1)
          saveOnExit++;
      if (saveOnExit == 2){
              if (df0mod)
                AutoSave(0);
              if (df1mod)
                AutoSave(1);
              if (df2mod)
                AutoSave(2);
              if (df3mod)
                AutoSave(3);
      }
      if (saveOnExit == 10){
          if (relay){
              if (Switches.FloppySound && !Bypass){
                  Samples.RELAISOFF.play();
                  Samples.TAPEMOTOR.stop();}
              relay = false;
          }
          System.exit(0);
      }

      if (saveOn ==1 ){
          if (df0mod)
              AutoSave(0);
          if (df1mod)
              AutoSave(1);
          if (df2mod)
              AutoSave(2);
          if (df3mod)
              AutoSave(3);
          saveOn = 0;
          df0mod = df1mod = df2mod = df3mod = false;
      }

      if (savetimer >=1){
          savetimer++;
          if (savetimer == 100){
              savetimer = 0;
              if (df0mod)
                AutoSave(0);
              if (df1mod)
                AutoSave(1);
              if (df2mod)
                AutoSave(2);
              if (df3mod)
                AutoSave(3);
              df0mod = df1mod = df2mod = df3mod = false;
          }
      }
      if (blastercount >=1){
          Switches.blockKeyboard = true;
          blastercount++;
          if (blastercount == 170 || blastercount == 180)
              keyboarda.keyPressed(KeyEvent.VK_DECIMAL);
          if (blastercount == 175 || blastercount == 185)
              keyboarda.keyReleased(KeyEvent.VK_DECIMAL);
          if (blastercount == 186){
              blastercount=0;
          Switches.blockKeyboard = false;
          }
      }
      if (launchcount >=1){
          launchcount++;
          if (launchcount == 10)
               smartReset();
              if (launchcount == 30){
               smartReset();
                  launchcount = 0;
              // smartReset();
               if (launchcode == 1)
                  launchDigitracker();
               if (launchcode == 3)
                  launchDigitrackerMC();
               if (launchcode == 4)
                  launchDigitrackerPG();
               if (launchcode == 2)
                  launchCheat();
              }
      }
      if(Switches.autofire){
          firetimer++;
      if (firetimer >= timefire){
          if (fired && !Switches.blockKeyboard){
              keyboarda.keyPressed(KeyEvent.VK_NUMPAD5);
              fired = false;
          }
          else{
              keyboarda.keyReleased(KeyEvent.VK_NUMPAD5);
              fired = true;
          }
      firetimer = 0;
      }
      }
      else
          if (!fired){
              keyboarda.keyReleased(KeyEvent.VK_NUMPAD5);
              fired = true;
          }


      turbotimer++;
      if (turbotimer >=100){
          turbotimer = 0;
          if (Switches.turbo >=2)
          Switches.turbo++;
      }
    if (frameSkip == 0)
      display.updateImage(true);
    syncProcessor(psg.getSoundPlayer());
    if (!YM_Play){
    typeAuto();
    joyReader();
    Check();
    }
    if (YM_Rec || YM_Play || YM_Stop || YM_Load || YM_Save)
      YMCheck();
  }

  public void SaveDSK(){
      dskImage = null;
      int drive = getCurrentDrive();
      System.out.println("Saving image from drive DF" + drive);
      if (drive == 0){
          if (dskImageA != null)
                dskImage=dskImageA;
      }
      if (drive == 1){
          if (dskImageB != null)
                dskImage=dskImageB;
      }
      if (drive == 2){
          if (dskImageC != null)
                dskImage=dskImageC;
      }
      if (drive == 3){
          if (dskImageD != null)
                dskImage=dskImageD;
      }
      if (dskImage != null){
        floppies[drive].setSides(dskImage.getNumberOfSides());
        int heads = dskImage.getNumberOfSides() == 1 ? Drive.HEAD_0 : Drive.BOTH_HEADS;
      
        fdc.setDrive(drive, floppies[drive]);
        fdc.getDrive(drive).setDisc(heads, dskImage);
        dskImage.saveImage();
      }
      else
          System.out.println("Failed to save... Probably drive empty?");
      setCurrentDrive(0);
  }

  public void AutoSave(int drive){
      dskImage = null;
      System.out.println("Saving image from drive DF" + drive);
      if (drive == 0){
          if (dskImageA != null)
                dskImage=dskImageA;
      }
      if (drive == 1){
          if (dskImageB != null)
                dskImage=dskImageB;
      }
      if (drive == 2){
          if (dskImageC != null)
                dskImage=dskImageC;
      }
      if (drive == 3){
          if (dskImageD != null)
                dskImage=dskImageD;
      }
      if (dskImage != null){
        floppies[drive].setSides(dskImage.getNumberOfSides());
        int heads = dskImage.getNumberOfSides() == 1 ? Drive.HEAD_0 : Drive.BOTH_HEADS;
        fdc.setDrive(drive, floppies[drive]);
        fdc.getDrive(drive).setDisc(heads, dskImage);
        dskImage.saveImage();
      }
      else
          System.out.println("Failed to save... Probably drive empty?");
      setCurrentDrive(0);
  }

  public void joyReader(){
       if (!Switches.blockKeyboard){
      if (Switches.MouseJoy){
      if (Switches.directxR.equals("Right"))
          keyboarda.keyPressed(KeyEvent.VK_NUMPAD6);
      if (Switches.directxL.equals("Left"))
          keyboarda.keyPressed(KeyEvent.VK_NUMPAD4);
      if (Switches.directyU.equals("Up"))
          keyboarda.keyPressed(KeyEvent.VK_NUMPAD8);
      if (Switches.directyD.equals("Down"))
          keyboarda.keyPressed(KeyEvent.VK_NUMPAD2);
              Switches.directL++;
              if (Switches.directL >= 4){
                  keyboarda.keyReleased(KeyEvent.VK_NUMPAD4);
              }
              Switches.directR++;
              if (Switches.directR >= 4){
                  keyboarda.keyReleased(KeyEvent.VK_NUMPAD6);
              }
              Switches.directU++;
              if (Switches.directU >= 4){
                  keyboarda.keyReleased(KeyEvent.VK_NUMPAD8);
              }
              Switches.directD++;
              if (Switches.directD >= 4){
                  keyboarda.keyReleased(KeyEvent.VK_NUMPAD2);
              }
      }
       }
  }

  public void typeAuto(){
    if (Switches.getfromautotype != 0) {
      Switches.getfromautotype = 0;
      AutoType(Autotype.autotext);
    }
    if (autotyper != 0) {
      Display.autotype=2;
      autotyper++;
      if (autotyper == 3) {
        try {
         // System.out.println("KeyRelease: " + readkey);
          keyboarda.keyReleased(eventArray[readkey]);
          if (shifter[readkey + 1])
            keyboarda.keyPressed(KeyEvent.VK_SHIFT);
          else
            keyboarda.keyReleased(KeyEvent.VK_SHIFT);
        }  catch (Exception e) {
          keyboarda.keyReleased(KeyEvent.VK_SHIFT);
          autotyper = 0;
          Display.autotype=0;
          if (turbo)
              Switches.turbo = 4;
          else
              Switches.turbo = 1;
          Switches.blockKeyboard = false;
        }
      }

      if (autotyper == 5) {
        try {
          //System.out.println("KeyPress: " + readkey);
          readkey++;
          keyboarda.keyPressed(eventArray[readkey]);
          autotyper = 1;
        } catch (Exception e) {
          keyboarda.keyReleased(KeyEvent.VK_SHIFT);
          autotyper = 0;
          Display.autotype=0;
          if (turbo)
              Switches.turbo = 4;
          else
              Switches.turbo = 1;
          Switches.blockKeyboard = false;
        }
      }
    }
  }

  public Memory getMemory() {
    return memory;
  }

  public Processor getProcessor() {
    return z80;
  }

  public Dimension getDisplaySize(boolean large) {
    return gateArray.getDisplaySize(large);
  }

  @Override
  public Dimension getDisplayScale(boolean large) {
    return large ? Display.SCALE_1x2 : Display.SCALE_1;
  }

  @Override
  public void setLarge(boolean value) {
    gateArray.setHalfSize(!value);
  }

  @Override
  public Disassembler getDisassembler() {
    return disassembler;
  }

  protected Keyboard getKeyboard() {
    if (Switches.joystick == 0)
      return keyboardb;
    else if (Switches.notebook)
      return keyboardn;
    else
      return keyboarda;
  }

  public static boolean recordKeys, playKeys = false;
  protected int keyNumber, totalKeyNumber;
  protected byte keyStroke[] = new byte[11000000];


  public void recordKeys(){
      totalKeyNumber = 0;
      keyNumber = 0;
      playKeys = false;
      recordKeys = true;
  }

  public void playKeys(){
      playKeys = true;
      recordKeys = false;
      keyNumber = 0;
  }
  public void stopKeys(){
      if (recordKeys){
          totalKeyNumber = keyNumber;
          SNK_Save();
      }
      playKeys = false;
      recordKeys = false;
      keyNumber = 0;
  }

  @Override
  public int readPort(int port) {
      int result;
    switch(port) {
        case PPI_PORT_C:
            result = 1;
            System.out.println("Port read..." + Util.hex(port) + " result:" + result);
            break;
      case PPI_PORT_B:
          result = (((Switches.Printer ? 0x00 : 0x040) | 0x010) |
                  (Switches.computername*2) | (crtc.isVSync() ? 0x01 : 0 ) | portB);

          break;

      case PSG_PORT_A: {
          if (recordKeys){
              keyStroke[keyNumber] = (byte)keyboarda.readSelectedRow();
              keyNumber++;
              if (keyNumber>=10000000){
                  keyNumber=0;
              recordKeys = false;
              System.out.println("Recording stopped... Buffer full");
              }
          }
          if (playKeys){
              result = keyStroke[keyNumber];
              keyNumber++;
              if (keyNumber>=totalKeyNumber){
                  System.out.println("Playback finished");
                  keyNumber=0;
                  playKeys=false;
              }
              break;
          } else
          if (Switches.blockKeyboard) {
              result = keyboarda.readSelectedRow();
              break;
          }
          else {
              result = getKeyboard().readSelectedRow();
              break;
          }
      }
      default:
          throw new RuntimeException("Unexpected Port Read: " + Util.hex((short)port));
    }
    return result;
  }

  @Override
  public void writePort(int port, int value) {

    switch(port) {

      case PPI_PORT_C:
          psg.setBDIR_BC2_BC1(PSG_VALUES[value >> 6],ppi.readOutput(PPI8255.PORT_A));
          
          if (Switches.blockKeyboard){
              keyboarda.setSelectedRow(value & 0x0f);
          }
          else
              getKeyboard().setSelectedRow(value & 0x0f);

          if (tapeEnabled){
              TapeRecbyte = (byte)((value & 0x20) * 7);
              if (z80.getPC() < 0x02800 || z80.getPC() > 0x03000 )
                  TapeRecbyte = (byte)0x80;
        if ((value & 0x020) == 0x20){
            psg.digicount = 1;
            number = recordcount;
        }
        if (psg.digicount >=1 && relay)
            TapeSound(TapeRecbyte);
        if ((((value^previousPortValue) & 0x010) != 0) || ((value & 0x10) == 0x10))
            TapeRelayCheck(value);
          }
          break;


      default:
          // FDC Motor control
          if ((port & 0x0581) == 0) {
              if (port >= 0xfa00){
              System.out.println("Floppy-motor " + ((value & 0x01) == 0 ? "off" : "on"));
              if ((value & 0x01) == 0) {
                  if (Switches.FloppySound && Switches.audioenabler == 1 && !Bypass)
                      Samples.MOTOR.play();
                  Display.ledOn = false;
              }
              else
                  if (Switches.FloppySound && Switches.audioenabler == 1 && !Bypass){
                      Samples.MOTOR.loop();
                      Display.ledOn = true;
                  }
              }
              }
          else {
          throw new RuntimeException("Unexpected Port Write: " + Util.hex((short) port) + " with "
            + Util.hex((byte) value));
      }
    }
  }

  public void keyPressed(KeyEvent e) {
    if (!Switches.blockKeyboard){/*
        if (Switches.FloppySound){
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            Samples.SPACE.play();
            else
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            Samples.ENTER.play();
            else
            Samples.KEY.play();
        }*/
      getKeyboard().keyPressed(e.getKeyCode());
    }
  }

  public void keyReleased(KeyEvent e) {
    if (!Switches.blockKeyboard){/*
        if (Switches.FloppySound){
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            Samples.SPACE.stop();
            else
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            Samples.ENTER.stop();
            else
            Samples.KEY.stop();
        }*/
    /*    if (Switches.FloppySound){
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
            Samples.SPACE.play();
            else
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            Samples.ENTER.play();
            else
            Samples.KEY.play();
        }*/
      getKeyboard().keyReleased(e.getKeyCode());
    }
  }

  public void AtKey() {
    AutoType("@");
  }

  public void bootDisk() {
      Display.bootgames = 150;
    AutoType("mode 1:pen 0\n|A:CAT:FOR k=1 TO 8:n$=n$+chr$(peek(HIMEM-&7FF+k)):NEXT:" +
            "n$=n$+\".\":FOR k=1 TO 3:n$=n$+chr$(peek(HIMEM-&7FF+k+8)):NEXT:PEN 1:RUN n$\nccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc\n|cpm\n");
  }
  public void bootDiskb() {
      Display.bootgames = 150;
    AutoType("mode 1:pen 0\n|B:CAT:FOR k=1 TO 8:n$=n$+chr$(peek(HIMEM-&7FF+k)):NEXT:" +
            "n$=n$+\".\":FOR k=1 TO 3:n$=n$+chr$(peek(HIMEM-&7FF+k+8)):NEXT:PEN 1:RUN n$\n");
  }

  public void bootCPM() {
    AutoType("|CPM\n");
  }

  public void typeRun() {
    AutoType("RUN\"");
  }

  public void typeCat() {
    AutoType("CAT\n");
  }

  public void AutoType() {
    AutoType(Switches.loadauto);
  }

  public void MouseFire1(){
       if (!Switches.blockKeyboard)
      keyboarda.keyPressed(KeyEvent.VK_NUMPAD5);
  }
  public void MouseFire2(){
       if (!Switches.blockKeyboard)
      keyboarda.keyPressed(KeyEvent.VK_NUMPAD0);
  }
  public void MouseReleaseFire1(){
       if (!Switches.blockKeyboard)
      keyboarda.keyReleased(KeyEvent.VK_NUMPAD5);
  }
  public void MouseReleaseFire2(){
       if (!Switches.blockKeyboard)
      keyboarda.keyReleased(KeyEvent.VK_NUMPAD0);

  }
  public void AutoType(String textinput) {
          if (Switches.turbo >= 2)
        turbo = true;
    else
        turbo = false;
        Switches.turbo=4;
    Switches.blockKeyboard = true;
    readkey = 0;
    eventArray = new int[textinput.length()];
    shifter = new boolean[textinput.length()];
    for(int i = 0; i < textinput.length(); i++) {
      eventArray[i] = convertCharToVK(textinput.charAt(i));
      shifter[i] = shift;
    }
    if (shifter[0])
      keyboarda.keyPressed(KeyEvent.VK_SHIFT);
    keyboarda.keyPressed(eventArray[0]);
    autotyper = 1;
  }

  /**
   * This should really be done using CPC key codes and overriding the keepRelay returned in
   * readPort for the PSG port read.
   */
  protected static final int[] ASC_TO_KEY = {
    KeyEvent.VK_SPACE, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3,                       // SPACE !"#   FTTT
    KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7,                           // $%&'        TTTT
    KeyEvent.VK_8, KeyEvent.VK_9, KeyEvent.VK_SEMICOLON, KeyEvent.VK_QUOTE,               // ()*+        TTTT
    KeyEvent.VK_COMMA, KeyEvent.VK_MINUS, KeyEvent.VK_PERIOD, KeyEvent.VK_SLASH,          // ,-./        FFFF
    KeyEvent.VK_0, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3,                           // 0123        FFFF
    KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7,                           // 4567        FFFF
    KeyEvent.VK_8, KeyEvent.VK_9, KeyEvent.VK_SEMICOLON, KeyEvent.VK_QUOTE,               // 89:;        FFFF
    KeyEvent.VK_COMMA, KeyEvent.VK_MINUS, KeyEvent.VK_PERIOD, KeyEvent.VK_SLASH,          // <=>?        TTTT
    KeyEvent.VK_OPEN_BRACKET, KeyEvent.VK_A, KeyEvent.VK_B, KeyEvent.VK_C,                // @ABC        FTTT
    KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_F, KeyEvent.VK_G,                           // DEFG        TTTT
    KeyEvent.VK_H, KeyEvent.VK_I, KeyEvent.VK_J, KeyEvent.VK_K,                           // HIJK        TTTT
    KeyEvent.VK_L, KeyEvent.VK_M, KeyEvent.VK_N, KeyEvent.VK_O,                           // LMNO        TTTT
    KeyEvent.VK_P, KeyEvent.VK_Q, KeyEvent.VK_R, KeyEvent.VK_S,                           // PQRS        TTTT
    KeyEvent.VK_T, KeyEvent.VK_U, KeyEvent.VK_V, KeyEvent.VK_W,                           // TUVW        TTTT
    KeyEvent.VK_X, KeyEvent.VK_Y, KeyEvent.VK_Z, KeyEvent.VK_ALT_GRAPH,                   // XYZ[        TTTF
    KeyEvent.VK_BACK_SLASH, KeyEvent.VK_CLOSE_BRACKET, KeyEvent.VK_EQUALS, KeyEvent.VK_0, // \]^_        FFFT
    KeyEvent.VK_BACK_SLASH, KeyEvent.VK_A, KeyEvent.VK_B, KeyEvent.VK_C,                  // `abc        TFFF
    KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_F, KeyEvent.VK_G,                           // defg        FFFF
    KeyEvent.VK_H, KeyEvent.VK_I, KeyEvent.VK_J, KeyEvent.VK_K,                           // hijk        FFFF
    KeyEvent.VK_L, KeyEvent.VK_M, KeyEvent.VK_N, KeyEvent.VK_O,                           // lmno        FFFF
    KeyEvent.VK_P, KeyEvent.VK_Q, KeyEvent.VK_R, KeyEvent.VK_S,                           // pqrs        FFFF
    KeyEvent.VK_T, KeyEvent.VK_U, KeyEvent.VK_V, KeyEvent.VK_W,                           // tuvw        FFFF
    KeyEvent.VK_X, KeyEvent.VK_Y, KeyEvent.VK_Z, KeyEvent.VK_ALT_GRAPH,                   // xyz{        FFFT
    KeyEvent.VK_OPEN_BRACKET, KeyEvent.VK_CLOSE_BRACKET                                   // |}          TT
  };

  protected static final String ASC_TO_SHIFT =
    "FTTTTTTTTTTTFFFFFFFFFFFFFFFFTTTTFTTTTTTTTTTTTTTTTTTTTTTTTTTFFFFTTFFFFFFFFFFFFFFFFFFFFFFFFFFTTT";

  public static int convertCharToVK(char in) {
    int result;
    switch(in) {
      case '\n': shift = false; result = KeyEvent.VK_ENTER; break;
      case '\u00A3':  shift = true; result = KeyEvent.VK_EQUALS; break; // POUND SIGN
    default:
      if (in >= '\u0020' && in <= '\u007D') {
        in -= 32;
        result = ASC_TO_KEY[in];
        shift = ASC_TO_SHIFT.charAt(in) == 'T';
      }
      else {
        shift = false;
        result = KeyEvent.CHAR_UNDEFINED;
      }
  }
  return result;
}

  protected static final String SNA_HEADER                             = "MV - SNA";
  protected static final String SNK_HEADER                             = "MV - SNK";
  protected static final String SNK_EYECATCHER                         = "JavaCPC Keyboard" +
                                                                         "   record file  ";
  protected              String BIN_HEADER                             = "JAVACPC BIN";
  protected              String BIN_EYECATCHER                         = "File exported by" +
                                                                         "     JavaCPC    ";
  protected static       String SNA_EYECATCHER                         = "JavaCPC Snapshot";
  protected static final String k64                                    = "64k Snapshot    ";
  protected static final String k128                                   = "128k Snapshot   ";
  protected static final String k256                                   = "256k Snapshot   ";
  protected static final String k512                                   = "512k Snapshot   ";
  protected static final String DSK_HEADER                             = "MV - CPC";
  protected static final String DSK_HEADER_EXT                         = "EXTENDED";
  protected static final String CDT_HEADER                             = "ZXTAPE";

  protected static final int    CRTC_FLAG_VSYNC_ACTIVE                 = 0x01;
  protected static final int    CRTC_FLAG_HSYNC_ACTIVE                 = 0x02;
  protected static final int    CRTC_FLAG_HDISP_ACTIVE                 = 0x04;
  protected static final int    CRTC_FLAG_VDISP_ACTIVE                 = 0x08;
  protected static final int    CRTC_FLAG_HTOT_REACHED                 = 0x10;
  protected static final int    CRTC_FLAG_VTOT_REACHED                 = 0x20;
  protected static final int    CRTC_FLAG_MAXIMUM_RASTER_COUNT_REACHED = 0x40;

  protected static final int    SNAPSHOT_ID                            = 0x0000;
  protected static final int    VERSION                                = 0x0010;

  protected static final int    AF                                     = 0x0011;
  protected static final int    BC                                     = 0x0013;
  protected static final int    DE                                     = 0x0015;
  protected static final int    HL                                     = 0x0017;
  protected static final int    R                                      = 0x0019;
  protected static final int    I                                      = 0x001a;
  protected static final int    IFF1                                   = 0x001b;
  protected static final int    IFF2                                   = 0x001c;
  protected static final int    IX                                     = 0x001d;
  protected static final int    IY                                     = 0x001f;
  protected static final int    SP                                     = 0x0021;
  protected static final int    PC                                     = 0x0023;
  protected static final int    IM                                     = 0x0025;
  protected static final int    AF1                                    = 0x0026;
  protected static final int    BC1                                    = 0x0028;
  protected static final int    DE1                                    = 0x002a;
  protected static final int    HL1                                    = 0x002c;

  protected static final int    GA_PEN                                 = 0x002e;
  protected static final int    GA_INKS                                = 0x002f;
  protected static final int    GA_ROM                                 = 0x0040;
  protected static final int    GA_RAM                                 = 0x0041;

  protected static final int    CRTC_REG                               = 0x0042;
  protected static final int    CRTC_REGS                              = 0x0043;

  protected static final int    UPPER_ROM                              = 0x0055;

  protected static final int    PPI_A                                  = 0x0056;
  protected static final int    PPI_B                                  = 0x0057;
  protected static final int    PPI_C                                  = 0x0058;
  protected static final int    PPI_CONTROL                            = 0x0059;

  protected static final int    PSG_REG                                = 0x005a;
  protected static final int    PSG_REGS                               = 0x005b;

  protected static final int    MEM_SIZE                               = 0x006b;

  protected static final int    CPC_TYPE                               = 0x006d;

/*  protected static final int    VER_INT_BLOCK                          = 0x006d;
  protected static final int    VER_MODES                              = 0x006e;*/

  protected static final int    HEADER_SIZE                            = 0x0100;

  @Override
  public void loadFile(int type, String name) throws Exception {
      Switches.name = name;
      if (!Switches.loaded)
          System.out.println("opening: " + name);
    byte[] data = getFile(name);
    if (SNA_HEADER.equals(new String(data, 0, SNA_HEADER.length()).toUpperCase())){
        SNA_Load(name, data);
        System.out.println("Loading MV - SNA snapshot file...");
        jemu.ui.JEMU.isTape = true;
    }
    else
    if (SNK_HEADER.equals(new String(data, 0, SNK_HEADER.length()).toUpperCase())){
        SNK_Load(name, data);
        System.out.println("Loading MV - SNP snapshot record file...");
        jemu.ui.JEMU.isTape = true;
    }
    else
    if (DSK_HEADER.equals(new String(data, 0, DSK_HEADER.length()).toUpperCase())){
        DSK_Load(name, data);
        System.out.println("Loading MV - CPCEMU DSK file...");
    }
    else
    if (DSK_HEADER_EXT.equals(new String(data, 0, DSK_HEADER_EXT.length()).toUpperCase())){
        DSK_Load(name, data);
        System.out.println("Loading EXTENDED CPC DSK file...");
    }
    else
    if (CDT_HEADER.equals(new String(data, 0, CDT_HEADER.length()).toUpperCase())){
        System.out.println("Loading ZXTAPE file...");
        CDT_Load(name, data);
    }
    else
    if (WAV_HEADER.equals(new String(data, 0, WAV_HEADER.length()).toUpperCase())){
      Switches.booter = 0;
        System.out.println("Loading WAV-tape file...");
        TapeLoad(name, data);
        jemu.ui.JEMU.isTape = true;
    }
    else
    if (CSW_HEADER.equals(new String(data, 0, CSW_HEADER.length())) ||
            name.toUpperCase().endsWith(".CSW")){
      Switches.booter = 0;
        System.out.println("Loading CSW-compressed square wave tape file...");
        CSWLoad(name, data);
        jemu.ui.JEMU.isTape = true;
    }
    else
        if (MP3_HEADER_A.equals(new String(data, 0, MP3_HEADER_A.length()).toUpperCase())){
      Switches.booter = 1;
        System.out.println("Loading and converting MP3 file...");
        MP3Load(name);
        jemu.ui.JEMU.isTape = true;
        }
    else
        if (MP3_HEADER_B.equals(new String(data, 0, MP3_HEADER_B.length()).toUpperCase())){
      Switches.booter = 1;
        System.out.println("Loading and converting MP3 file...");
        MP3Load(name);
        jemu.ui.JEMU.isTape = true;

        }
    else
        if (name.toUpperCase().endsWith("MP3")){
      Switches.booter = 1;
        System.out.println("Loading and converting MP3 file...");
        MP3Load(name);
        jemu.ui.JEMU.isTape = true;
        }
    else
        if (name.toUpperCase().endsWith("YM")){
        jemu.ui.JEMU.isTape = true;
      Switches.booter = 1;
            loadYM(name);
            YM_Play = true;
        }
        else
        if (name.toUpperCase().endsWith("WAV") || name.toUpperCase().endsWith("JTP")){
        TapeLoad(name, data);
        jemu.ui.JEMU.isTape = true;
        }else
            if (!jemu.ui.JEMU.isTape)
                BIN_Load(name, data);
       // System.out.println("Unrecognized file format");
    reSync();
    }

  public void CDT_Load(String name, byte[] data) throws Exception {
            tape_stereo = false;
      bitrate = 8;
      tapeloaded = false;
        jemu.ui.JEMU.isTape = true;
      int freq = 44100;
      if (Switches.khz44)
          freq = 44100;
      if (Switches.khz11)
          freq = 11025;
      if (!Switches.khz11 && !Switches.khz44)
          freq = 22050;
      System.out.println("Converting CDT to " + freq + "hz WAV...");
      System.out.println("CDT size is:" + data.length);
      isCDT = true;
      Switches.booter = 0;

      tapesample = null;
      CDT2WAV cdt2wav = new CDT2WAV(data, freq);
      tapesample = cdt2wav.convert();
      if (tapesample == null) {
    	  tapesample = new byte[0]; // avoid tapesample being null
      }
      cdt2wav.dispose();

      tape_delay = 1050000/(freq);
      number = 0;
      play = true;
      if (Switches.FloppySound && !tapeloaded)
          Samples.TAPEINSERT.play();
      TapeDrive.btnPLAY.setBorder(new BevelBorder(BevelBorder.LOWERED));
      TapeDrive.btnPLAY.setBackground(Color.BLACK);
      TapeDrive.buttonpressed = true;
      tapeloaded = true;
      recordcount = tapesample.length;
      TapeDeck.positionslider.setMaximum(recordcount);
        TapeDeck.positionslider.setValue(0);
        System.out.println("Tape size is:" + tapesample.length+" bytes");

                 TapeDrive.showText(name.toUpperCase());
    reSync();
  }

  public void DSK_Load(String name, byte[] data) throws Exception {
      Switches.booter = 0;
      CPCDiscImage image = new CPCDiscImage(name, data);
      System.out.println("data length:" + data.length);
      int drive = getCurrentDrive();
      if (drive == 0) {
        checkDF0();
      dskImageA = image;
      df0mod = false;}
      if (drive == 1) {
        checkDF1();
      dskImageB = image;
      df1mod = false;}
      if (drive == 2) {
        checkDF2();
      dskImageC = image;
      df2mod = false;}
      if (drive == 3) {
        checkDF3();
      dskImageD = image;
      df3mod = false;}
     // fdc.setDrive(drive, floppies[drive] = new Drive(drive == 0 ? 1 : 2));
      floppies[drive].setSides(image.getNumberOfSides());
      int heads = image.getNumberOfSides() == 1 ? Drive.HEAD_0 : Drive.BOTH_HEADS;
      fdc.setDrive(drive, floppies[drive]);
      fdc.getDrive(drive).setDisc(heads, image);
      fdc.poll();
      if (getCurrentDrive() == 0)
        Switches.loaddrivea = name;
      if (getCurrentDrive() == 1)
        Switches.loaddriveb = name;
      if (getCurrentDrive() == 2)
        Switches.loaddrivec = name;
      if (getCurrentDrive() == 3)
        Switches.loaddrived = name;
      if (Switches.FloppySound  && Switches.audioenabler == 1){
        Samples.INSERT.play();
        Samples.MOTOR.play();
      }
    reSync();
  }


  public void SNK_Load(String name, byte[] data){
      Switches.booter = 1;
      totalKeyNumber = getDWord(data, 20);
      System.arraycopy(data, 0x100, keyStroke, 0, totalKeyNumber);
    System.out.println("Length is " + totalKeyNumber);
      playKeys();
  }

  public void SNA_Load(String name, byte[] data){
      Switches.booter = 1;
      z80.setAF(getWord(data, AF));
      z80.setBC(getWord(data, BC));
      z80.setDE(getWord(data, DE));
      z80.setHL(getWord(data, HL));
      z80.setR(data[R]);
      z80.setI(data[I]);
      z80.setIFF1(data[IFF1] != 0);
      z80.setIFF2(data[IFF2] != 0);
      z80.setIX(getWord(data, IX));
      z80.setIY(getWord(data, IY));
      z80.setSP(getWord(data, SP));
      z80.setPC(getWord(data, PC));
      z80.setIM(data[IM]);
      z80.setAF1(getWord(data, AF1));
      z80.setBC1(getWord(data, BC1));
      z80.setDE1(getWord(data, DE1));
      z80.setHL1(getWord(data, HL1));

      gateArray.setSelectedInk(data[GA_PEN]);
      System.out.print("INK READ: ");
      for (int i = 0; i < 0x11; i++){
        gateArray.setInk(i, data[GA_INKS + i]);
        System.out.print(data[GA_INKS + i]);
        if (i>=0x10)
            System.out.println("");
        else
            System.out.print(",");
      }

      gateArray.setModeAndROMEnable(memory, data[GA_ROM]);
      memory.setRAMBank(data[GA_RAM]);

      crtc.setSelectedRegister(data[CRTC_REG]);
      for (int i = 0; i < 18; i++)
        crtc.setRegister(i, data[CRTC_REGS + i]);

      ppi.setControl(data[PPI_CONTROL] & 0xff | 0x80);
      ppi.setOutputValue(PPI8255.PORT_A, data[PPI_A] & 0xff);
      ppi.setOutputValue(PPI8255.PORT_B, data[PPI_B] & 0xff);
      int portC = data[PPI_C] & 0xff;
      ppi.setOutputValue(PPI8255.PORT_C, portC);

      psg.setBDIR_BC2_BC1(PSG_VALUES[portC >> 6], ppi.readOutput(PPI8255.PORT_A));

      psg.setSelectedRegister(data[PSG_REG]);
      for (int i = 0; i < 14; i++)
        psg.setRegister(i, data[PSG_REGS + i] & 0xff);

      int memSize = (data[MEM_SIZE] & 0xff) * 1024;
      memSize = Math.min(memSize, memory.getRAMType() == CPCMemory.TYPE_512K ? 0x100000 : 0x200000);
      int length = data.length;
      System.out.println("Snapshot length is "+ length);
      if (length <= 65800)
          memSize = 0x10000;
      else if (length <= 131400)
          memSize = 0x20000;
      else if (length <= 262800)
          memSize = 0x40000;
      else if (length <= 524600)
          memSize = 0x80000;
      System.out.println("Calculated length is "+ memSize);

      byte[] mem = memory.getMemory();
      System.arraycopy(data, 0x100, mem, 0, memSize);
  }

  public void SNK_Save(){
      FileDialog filedia = new FileDialog((Frame) dummy, "Save SNK Snapshot File", FileDialog.SAVE);
      if (Switches.uncompressed)
        filedia.setFile("*.snk");
      else
        filedia.setFile("*.szk");
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename != null) {
           filename =  filedia.getDirectory() + filedia.getFile();
           String savename = filename;
        File file = new File(savename);

    byte[] data = new byte[0x0100+totalKeyNumber];
    try {
      System.arraycopy(SNK_HEADER.getBytes("UTF-8"), 0, data, 0, SNK_HEADER.length());
    }
        catch (final Exception iox) {}
    try {
      System.arraycopy(SNK_EYECATCHER.getBytes("UTF-8"), 0, data, 0xe0, SNK_EYECATCHER.length());
    }
        catch (final Exception iox) {}
    System.out.println("Length is " + totalKeyNumber);
      putDWord(data,20,totalKeyNumber);
      System.arraycopy(keyStroke,0,data,0x0100, totalKeyNumber);
      try{
          Thread.sleep(200);
      }
      catch (Exception cantsleep){}

if (Switches.uncompressed)
    try {
        savename="";
            if (!file.toString().toLowerCase().endsWith(".snk"))
                        savename=savename + ".snk";
      final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file + savename));
	   	bos.write(data);
      bos.close();
    } catch (final IOException iox) {
      System.out.println("can't write to file ");
    }
else {
        savename="";
            if (!file.toString().toLowerCase().endsWith(".szk"))
                        savename=savename + ".szk";
    File gzip_output = new File (file  + savename);
    GZIPOutputStream gzip_out_stream;
    try {
      FileOutputStream out = new FileOutputStream (gzip_output);
      gzip_out_stream =
        new GZIPOutputStream (new BufferedOutputStream (out));
      gzip_out_stream.write (data, 0, data.length);
      gzip_out_stream.close ();
    }
    catch (IOException e) {
    }
}
        }

  }

  public void SNA_Save(int sizemem){
      FileDialog filedia = new FileDialog((Frame) dummy, "Save "+sizemem +"k Snapshot File", FileDialog.SAVE);
      if (Switches.uncompressed)
        filedia.setFile("*.sna");
      else
        filedia.setFile("*.snz");
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename != null) {
           filename =  filedia.getDirectory() + filedia.getFile();
           String savename = filename;
        File file = new File(savename);

        // saveauto data to file
      int memSize = 0x10000;
      if (sizemem == 128)
         memSize = 0x20000;
      if (sizemem == 256)
         memSize = 0x40000;
      if (sizemem == 512)
         memSize = 0x80000;

    byte[] data = new byte[0x0100+memSize];
    try {
      System.arraycopy(SNA_HEADER.getBytes("UTF-8"), 0, data, 0, SNA_HEADER.length());
    }
        catch (final IOException iox) {}
    String snasize = k64;
    if (sizemem == 64)
        snasize = k64;
    if (sizemem == 128)
        snasize = k128;
    if (sizemem == 256)
        snasize = k256;
    if (sizemem == 512)
        snasize = k512;

    try {
        SNA_EYECATCHER = SNA_EYECATCHER + snasize;
      System.arraycopy(SNA_EYECATCHER.getBytes("UTF-8"), 0, data, 0xe0, SNA_EYECATCHER.length());
    }
        catch (final IOException iox) {}


      data[VERSION] = (byte)1;

      // z80 registers
      putWord(data,AF,z80.getRegisterValue(1));
      putWord(data,BC,z80.getRegisterValue(7));
      putWord(data,DE,z80.getRegisterValue(5));
      putWord(data,HL,z80.getRegisterValue(3));
      data[R] = (byte)z80.getRegisterValue(14);
      data[I] = (byte)z80.getRegisterValue(12);
      data[IFF1] = (byte)z80.getIFF1();
      data[IFF2] = (byte)z80.getIFF2();
      putWord(data,IX,z80.getRegisterValue(9));
      putWord(data,IY,z80.getRegisterValue(11));
      putWord(data,SP,z80.getRegisterValue(10));
      putWord(data,PC,z80.getRegisterValue(13));
          data[IM] = (byte)z80.getIM();
      putWord(data,AF1,z80.getRegisterValue(2));
      putWord(data,BC1,z80.getRegisterValue(8));
      putWord(data,DE1,z80.getRegisterValue(6));
      putWord(data,HL1,z80.getRegisterValue(4));


      // inks and pens
      data[GA_PEN] = (byte)gateArray.getSelectedInk();
      System.out.print("CPC Palette is: ");
      for (int i = 0; i < 0x11; i++){
        data[GA_INKS +i] = (byte)gateArray.getInks(i);
          System.out.print((byte)gateArray.getInks(i)+ " ");
      }
      System.out.println();
      // CRTC registers
      data[CRTC_REG] = (byte)crtc.getSelectedRegister();
      for (int i = 0; i < 18; i++)
          data[CRTC_REGS + i] = (byte)crtc.getReg(i);
      data[PPI_CONTROL] = (byte)ppi.getControl();
        data[PPI_A] = (byte)ppi.portInputA();
        data[PPI_B] = (byte)ppi.portInputB();
        data[PPI_C] = (byte)ppi.portInputC();

      data[PSG_REG] = (byte)psg.getSelectedRegister();
      for (int i = 0; i < 14; i++)
      data[PSG_REGS + i] = (byte)psg.getRegister(i);

      data[GA_ROM] = (byte)gateArray.getMode();
      data[GA_RAM] = (byte)(gateArray.getRAMBank());

      data[UPPER_ROM] = (byte)memory.getUpperROM();
      data[MEM_SIZE] = (byte)64;
      if (sizemem == 128)
      data[MEM_SIZE] = (byte)128;
      if (sizemem == 256)
      data[MEM_SIZE] = (byte)256;
      if (sizemem == 512)
      {
      int SizeInK = 512;
      data[MEM_SIZE] = (byte)(SizeInK & 0x0ff);
      data[MEM_SIZE+1] = (byte)((SizeInK>>8) & 0x0ff);
      }
      data[CPC_TYPE] = (byte)2;
      byte[] mem = memory.getMemory();
      System.arraycopy(mem,0,data,0x0100, memSize);


if (Switches.uncompressed)
    try {
        savename="";
            if (!file.toString().toLowerCase().endsWith(".sna"))
                        savename=savename + ".sna";
      final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file + savename));
	   	bos.write(data);
      bos.close();
    } catch (final IOException iox) {
      System.out.println("can't write to file ");
    }
else {
        savename="";
            if (!file.toString().toLowerCase().endsWith(".snz"))
                        savename=savename + ".snz";
    File gzip_output = new File (file  + savename);
    GZIPOutputStream gzip_out_stream;
    try {
      FileOutputStream out = new FileOutputStream (gzip_output);
      gzip_out_stream =
        new GZIPOutputStream (new BufferedOutputStream (out));
      gzip_out_stream.write (data, 0, data.length);
      gzip_out_stream.close ();
    }
    catch (IOException e) {
    }
}
        }

filedia.dispose();
    reSync();
  }

  public void tape_WAV_save(){
      if (recordcount >=100){
        FileDialog filedia = new FileDialog((Frame) dummy, "Export JavaCPC tape file...", FileDialog.SAVE);
        if (Switches.uncompressed)
        filedia.setFile("*.wav");
        else
            filedia.setFile("*.taz");
        filedia.setVisible(true);
        String filename = filedia.getDirectory() + filedia.getFile();
        if (filename != null) {
            tape_WAV_save(filename);
        }
      }
  }
  
  public void CDT2WAV(){
        FileDialog filedia = new FileDialog((Frame) dummy, "Open CDT to convert...", FileDialog.LOAD);
        filedia.setFile("*.cdt; *.tzx; *.zip");
        filedia.setVisible(true);
        String filename = filedia.getDirectory() + filedia.getFile();
        if (filedia.getFile() != null) {
            CDT2WAV(filename);
        }
  }

  public void CDT2WAV(String name){
      try{
          byte[] data = getFile(name);
          CDT_Load(name, data);
          tape_WAV_save();
      }
      catch (Exception error){}
  }


  public void tape_WAV_save(String filename){
      if (fromCapture){
          fromCapture = false;
          Switches.khz44 = true;
          Switches.khz11 = false;
          tape_stereo = true;
          bitrate = 8;
      }
      if (Switches.uncompressed){
            if (!filename.toUpperCase().endsWith(".WAV"))
                filename = filename + ".wav";
            System.out.println("Saving to " + filename);
      try {
      final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename));
      if (!WAV_HEADER.equals(new String(tapesample, 0, WAV_HEADER.length()).toUpperCase())){
          if (Switches.khz44){
              if (tape_stereo){
                  WAV_HEADER_44KHz[22] = 2;
                  WAV_HEADER_44KHz[32] = 2;
                  putLong(WAV_HEADER_44KHz, 28, 88200);
              
              }
              putLong(WAV_HEADER_44KHz , 40, recordcount);
              putLong(WAV_HEADER_44KHz , 4, 36+recordcount);
              for (int i = 0; i < 0x2c; i++)
                  bos.write((byte)WAV_HEADER_44KHz[i]);}
          if (Switches.khz11){
              putLong(WAV_HEADER_11KHz , 40, recordcount);
              putLong(WAV_HEADER_11KHz , 4, 36+recordcount);
              for (int i = 0; i < 0x2c; i++)
                  bos.write((byte)WAV_HEADER_11KHz[i]);}
          if (!Switches.khz11 && !Switches.khz44){
              putLong(WAV_HEADER_22KHz , 40, recordcount);
              putLong(WAV_HEADER_22KHz , 4, 36+recordcount);
              for (int i = 0; i < 0x2c; i++)
                  bos.write((byte)WAV_HEADER_22KHz[i]);}
      for (int i=0; i<recordcount; i++)
          bos.write(tapesample[i]);
      } else
          bos.write(tapesample);
      bos.close();
    } catch (final IOException iox) {
      System.out.println("can't write to file ");
    }
      try{
    loadFile(0, filename);}
          catch (Exception error){}
      }
      else {
          String savename="";
            if (!filename.toLowerCase().endsWith(".taz"))
                        savename=savename + ".taz";
            System.out.println("Saving to " + filename);
    File gzip_output = new File (filename  + savename);
    GZIPOutputStream gzip_out_stream;
    try {
      FileOutputStream out = new FileOutputStream (gzip_output);
      gzip_out_stream =
        new GZIPOutputStream (new BufferedOutputStream (out));
            if (!WAV_HEADER.equals(new String(tapesample, 0, WAV_HEADER.length()).toUpperCase())){
          if (Switches.khz44){
              putLong(WAV_HEADER_44KHz , 40, recordcount);
              for (int i = 0; i < 0x2c; i++)
                  gzip_out_stream.write((byte)WAV_HEADER_44KHz[i]);}
          if (Switches.khz11){
              putLong(WAV_HEADER_11KHz , 40, recordcount);
              for (int i = 0; i < 0x2c; i++)
                  gzip_out_stream.write((byte)WAV_HEADER_11KHz[i]);}
          if (!Switches.khz11 && !Switches.khz44){
              putLong(WAV_HEADER_22KHz , 40, recordcount);
              for (int i = 0; i < 0x2c; i++)
                  gzip_out_stream.write((byte)WAV_HEADER_22KHz[i]);}
      for (int i=0; i<recordcount; i++)
          gzip_out_stream.write(tapesample[i]);
      } else
          gzip_out_stream.write(tapesample);


      gzip_out_stream.close ();
    }
    catch (IOException e) {
    }
      }

  }
  public void BIN_Load(){
        FileDialog filedia = new FileDialog((Frame) dummy, "Import CPC file...", FileDialog.LOAD);
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename != null) {
           filename =  filedia.getDirectory() + filedia.getFile();
        BIN_Load(filename,getFile(filename));
        }
  }

  public void BIN_Load(String name, byte[] data){
      Switches.booter = 1;
      int header = 0;
      int start = 0;
      int execaddress = 0;
      int BasicEnd = 0;
      String Sexc="";
      byte filetype = 0;
            if (CheckAMSDOS(data)){
            int startaddress = getWord(data, 0x15);
                 execaddress = getWord(data , 0x1a);
            int filelength = getWord(data , 0x18);
            filetype = (data[0x12]);
            String ftype="";
            //if(filetype == 0)
            //    filetype = 2;
            if(filetype == 0)
                ftype="BASIC";
            if(filetype == 1)
                ftype="BASIC (protected)";
            if(filetype == 2)
                ftype="BINARY";
            if(filetype == 3)
                ftype="BINARY (protected)";
            if(filetype == 4)
                ftype="IMAGE";
            if(filetype == 5)
                ftype="IMAGE (protected)";
            if(filetype == 6)
                ftype="ASCII";
            if(filetype == 7)
                ftype="ASCII (protected)";

             start = startaddress;
             String Sadr = Util.hex(start).substring(4);
             String Slen = Util.hex(filelength).substring(4);
             Sexc = Util.hex(execaddress).substring(4);
             BasicEnd = start + filelength;
             Object[] oheader = {"AMSDOS header found...\n\n" +
                     "&" + Sadr +" - Load address\n"+
                     "&" + Slen +" - File length\n" +
                     "&" + Sexc +" - Exec. address\n\n" +
                     "Filetype is: " + ftype+"\n\n" +
                     "Enter load address:\n(Hexadecimal)"};

            String CNG_CHECK = new String(data, 0x8c, 0x0c);
            CNG = false;
            if (CNG_CHECK.equals(CNG_HEADER))
                CNG = true;
                    CNG_CHECK = null;
                    CNGBIN = false;
            CNG_CHECK = new String(data, 0x80, 0x0c);
            if (CNG_CHECK.equals(CNG_HEADER)){
                CNG = true;
            CNGBIN = true;}
            String selectedHeader ="";
            if (filetype == 0)
                selectedHeader="170";
            if (filetype >= 1 && !CNG)
             selectedHeader = JOptionPane.showInputDialog(dummy, oheader,Sadr);
             if (selectedHeader == null) return;
                    if (!selectedHeader.equals(""))
                        try{
                            start = Util.hexValue(selectedHeader);
                            if (start != Util.hexValue(Sadr))
                            execaddress = 0;
                            }
                        catch (final Exception iox) {return;}
             header = 0x80;
             }
            else {
                String result;
                String CNG_CHECK = new String(data, 0x0c, 0x0c);
                    CNG = false;
                if (CNG_CHECK.equals(CNG_HEADER))
                    CNG = true;
                    CNGBIN = false;
                    CNG_CHECK = null;
                CNG_CHECK = new String(data, 0x80, 0x0c);
                if (CNG_CHECK.equals(CNG_HEADER)){
                    CNG = true;
                    CNGBIN = true;
                }
                if (!CNG){
                   result = JOptionPane.showInputDialog(dummy, "No AMSDOS header found...\n\n" +
                            "Please enter start address:\n(Hexadecimal)\n");}
                else {
                    result = "100";
                }
                    if (result == null) return;
                    if (!result.equals(""))
                        try{
                            start = Util.hexValue(result);
                            }
                        catch (final Exception iox) {return;}
                    else
                        return;
                header = 0x00;
            }
            byte[] mem = memory.getMemory();
            int memSize = data.length;
            if ((start + (memSize-header)) >=0x10001)
                JOptionPane.showMessageDialog(null, "An error occured during importing file:\n" + "no or wrong start address entered...\n");

            else
                 {
             for (int i = header; i < data.length; i++){
                 POKE((start + i - header),data[i]);
             }
            }
             if (CheckAMSDOS(data)){
                 if ((execaddress !=0 && !CNG) || (filetype == 0 && !CNG)){
                    if (filetype == 2){
                      if (execaddress !=0 && (filetype >=2 && filetype <=3)){
                         int ok = JOptionPane.showConfirmDialog(dummy,"Execute binary?",
                             "Please choose", JOptionPane.YES_NO_OPTION);
                             if (ok == JOptionPane.YES_OPTION)
                                runBinary(execaddress);
                             else
                                 return;
                      }
                    }
                    else {
                        if (Switches.ROM.equals("CPC6128"))
                            importBasic1_1(BasicEnd);
                        if (Switches.ROM.equals("CPC664"))
                            importBasic664(BasicEnd);
                        if (Switches.ROM.equals("CPC464"))
                            importBasic1_0(BasicEnd);
                        int ok = JOptionPane.showConfirmDialog(dummy,"Execute basic?\n(at your own risk)",
                                "Please choose", JOptionPane.YES_NO_OPTION);
                        if (ok == JOptionPane.YES_OPTION)
                            executeBasic();
                        else
                            return;
                        //AutoType("list\n");
                    }
                 }
             }
            if (CNG){
                if (!CNGBIN){
                    if (execaddress == 0)
                        execaddress = 0x130;
                    if (execaddress == 0x170)
                        execaddress = 0x1a0;
                }
                else
                        execaddress = 0x120;
                runBinary(execaddress);
             }
  }

  public void runBinary(int execaddress){
      POKE(0x0bf00,0x0e);
      POKE(0x0bf01,0x0ff);
      POKE(0x0bf02,0x021);
      POKE(0x0bf03,(execaddress & 0x0ff));
      POKE(0x0bf04,(execaddress/256) & 0x0ff);
      POKE(0x0bf05,0x0c3);
      POKE(0x0bf06,0x016);
      POKE(0x0bf07,0x0bd);
      CALL(0x0bf00);
  }

  public void importBasic1_0(int BasicEnd){
      POKE(0x0ae83, BasicEnd & 0x0ff);
      POKE(0x0ae84,(BasicEnd>>8) & 0x0ff);
      POKE(0x0ae85, BasicEnd & 0x0ff);
      POKE(0x0ae86,(BasicEnd>>8) & 0x0ff);
      POKE(0x0ae87,BasicEnd & 0x0ff);
      POKE(0x0ae88,(BasicEnd>>8) & 0x0ff);

  }

  public void importBasic1_1(int BasicEnd){
      POKE(0x0ae66, BasicEnd & 0x0ff);
      POKE(0x0ae67,(BasicEnd>>8) & 0x0ff);
      POKE(0x0ae68, BasicEnd & 0x0ff);
      POKE(0x0ae69,(BasicEnd>>8) & 0x0ff);
      POKE(0x0ae6a,BasicEnd & 0x0ff);
      POKE(0x0ae6b,(BasicEnd>>8) & 0x0ff);
  }

  public void importBasic664(int BasicEnd){
      POKE(0x0ae66, BasicEnd & 0x0ff);
      POKE(0x0ae67,(BasicEnd>>8) & 0x0ff);
      POKE(0x0ae68, BasicEnd & 0x0ff);
      POKE(0x0ae69,(BasicEnd>>8) & 0x0ff);
      POKE(0x0ae6a,BasicEnd & 0x0ff);
      POKE(0x0ae6b,(BasicEnd>>8) & 0x0ff);
  }

  public void executeBasic(){
      POKE(0x0bf00,0x0e);
      POKE(0x0bf01,0x000);
      POKE(0x0bf02,0x021);
      POKE(0x0bf03,0x078);
      POKE(0x0bf04,0x0ea);
      POKE(0x0bf05,0x0c3);
      POKE(0x0bf06,0x016);
      POKE(0x0bf07,0x0bd);
      CALL(0x0bf00);
  }

  public void CALL(int address){
      z80.setPC(address);
  }

  public void POKE(){
            int address = 0;
            int value = 0;

            JTextField Address = new JTextField();
            JTextField Value = new JTextField();
                Object[] message = {"Address", Address,
        		"Value", Value};

                JOptionPane pane = new JOptionPane( message,
                                                JOptionPane.WARNING_MESSAGE,
                                                JOptionPane.OK_CANCEL_OPTION);

                pane.createDialog(null, "Poke Memory").setVisible(true);

             if (Address.getText().equals("")){
                 JOptionPane.showMessageDialog(null, "nothing poked...");
                 return;
             }
             String Address1 = Address.getText();
             String Value1 = Value.getText();
             try {
             address = Util.hexValue(Address1);
             value = Util.hexValue(Value1);
             POKE (address, value);
             }
                        catch (final Exception iox) {
                 JOptionPane.showMessageDialog(null, "nothing poked...");
                 return;
                        }
  }

  public static void POKE(int address, int value){
      memory.writeByte(address, value);
  }
    public void BIN_Export(){
        FileDialog filedia = new FileDialog((Frame) dummy, "Export CPC file...", FileDialog.SAVE);
        filedia.setFile("*.bin");
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename != null) {
           String ss = filename.toUpperCase();
           if (ss.endsWith(".BIN"))
             ss = ss.substring(0, (ss.length() - 4));
           ss = ss+"             ";
           String newname = "";
          for (int i=0; i<8; i++){
              if (ss.charAt(i) != '.' && ss.charAt(i) != '_' && ss.charAt(i) != '-')
          newname = newname + ss.charAt(i);
              else
                  newname = newname + " ";
          }
           BIN_HEADER = newname + "BIN";

           filename =  filedia.getDirectory() + filedia.getFile();
            if (!filename.toLowerCase().endsWith(".bin"))
                filename = filename + ".bin";

            int address = 0;
            int length = 0;
            int exec = 0;

            JTextField Address = new JTextField();
            JTextField Startaddress = new JTextField();
            JTextField Exec = new JTextField();
            JTextField InternalName = new JTextField();
            InternalName.setText(BIN_HEADER);
            InternalName.setEditable(false);
                Object[] message = {"Internal name", InternalName, "Start address", Address,
        		"Length", Startaddress, "Exec. address (optional)", Exec, AmHeader};

                JOptionPane pane = new JOptionPane( message,
                                                JOptionPane.QUESTION_MESSAGE,
                                                JOptionPane.OK_CANCEL_OPTION);

                pane.createDialog(null, "Export binary").setVisible(true);

             if (Address.getText().equals("") || Startaddress.getText().equals("")){
                 JOptionPane.showMessageDialog(null, "An error occurred...");
                 return;
             }
             String Address1 = Address.getText();
             String Startaddress1 = Startaddress.getText();
             String Exec1 = Exec.getText();
             String intname ="";
             if (InternalName != null)
                 intname = InternalName.getText();
             if (intname.length() == 11)
                 BIN_HEADER = intname;

             if (Exec1 == null)
                 Exec1 = "0";
             try {
             address = Util.hexValue(Address1);
             length = Util.hexValue(Startaddress1);
             exec = Util.hexValue(Exec1);
                BIN_Export(filename, address, length, exec, amheader);
             }
                 catch (final Exception iox) {
                 JOptionPane.showMessageDialog(null, "An error occurred...");
                 return;
                        }
        }

  }

    public void BIN_Export (String filename, int fileaddress, int filelength, int exaddress, boolean writeheader){
        byte[] header = new byte[0x80];
        byte[] data = new byte[filelength];
            putWord(header,0x15,fileaddress);
            putWord(header,0x1a,exaddress);
            putWord(header,0x18,filelength);
            putWord(header,0x40,filelength);
            header[0x12] = 2;
            byte[] mem = memory.getMemory();
            try {
                System.arraycopy(BIN_HEADER.getBytes("UTF-8"), 0, header, 1, BIN_HEADER.length());
                System.arraycopy(BIN_EYECATCHER.getBytes("UTF-8"), 0, header, 0x60, BIN_EYECATCHER.length());
            }
            catch (final IOException iox) {}
            putWord(header,67,ChecksumAMSDOS(header));
            System.arraycopy(mem,fileaddress,data,0, filelength - 1);

try{
     final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename));
     if (writeheader)
      for (int i=0; i<0x080; i++)
	   	bos.write(header[i]);
      for (int i=0; i<filelength; i++)
	   	bos.write(data[i]);
      bos.close();
        }
        catch (final IOException iox) {}
  }

  public  class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
              if (e.getSource() == AmHeader)
                  if (e.getStateChange() == ItemEvent.DESELECTED)
                      amheader = false;
                  else
                      amheader = true;
              if (e.getSource() == YMInterleaved)
                  if (e.getStateChange() == ItemEvent.DESELECTED)
                      YM_Interleaved = false;
                  else
                      YM_Interleaved = true;
              if (e.getSource() == Overscan)
                  if (e.getStateChange() == ItemEvent.DESELECTED){
                      soverscan = false;
                  AddressA.setEnabled(false);
                  AddressB.setEnabled(false);
                  AddressA.setText("");
                  AddressB.setText("C000");
                  }
                  else{
                      soverscan = true;
                  AddressA.setEnabled(true);
                  AddressB.setEnabled(true);
                  AddressA.setText("4000");
                  AddressB.setText("C000");
                  }
        }
    }

  @Override
  public void displayLostFocus() {
        keyboardb.reset();
        keyboarda.reset();
        keyboardn.reset();
  }

  @Override
  public Drive[] getFloppyDrives() {
    return floppies;
  }

  private GridBagConstraints getGridBagConstraints(int x, int y, double weightx,double weighty,
            int width, int fill)
                {
                if (this.gbcConstraints == null) {
                    this.gbcConstraints = new GridBagConstraints();
                }
                this.gbcConstraints.gridx = x;
                this.gbcConstraints.gridy = y;
                this.gbcConstraints.weightx = weightx;
                this.gbcConstraints.weighty = weighty;
                this.gbcConstraints.gridwidth = width;
                this.gbcConstraints.fill = fill;
            return this.gbcConstraints;
  }


  public void showPalette(){
      JFrame Output = new JFrame("CPC Palette");
      Output.setAlwaysOnTop(true);
      Output.getContentPane().setLayout(new GridBagLayout());
      for (int i = 0; i < 17; i++){
          Palette[i] = ""+gateArray.getInk(i);
          String pals;
          pals = Palette[i];
          int cols;
          cols = gateArray.getInk(i);
          String gcols = ""+(byte)gateArray.getInks(i);

          if (gateArray.getInk(i) <= 9)
              pals = "0" + pals;
          int x = i;
          int y = 1;
          int w = 1;
          if (i >= 16){
              pals = "BORDER " + pals;
              y = 4;
              x = 0;
              w = 5;
          }
          JButton Palettes = new JButton(pals);
          JButton Paletteg = new JButton(gcols);
          JButton Paletten = new JButton(" ");
          Paletten.setBackground(Palcols[cols]);
          Paletten.setFocusable(false);
          Paletten.setBorder(new BevelBorder(BevelBorder.RAISED));
          Palettes.setBackground(Color.DARK_GRAY);
          Palettes.setForeground(Color.LIGHT_GRAY);
          Palettes.setFont(new Font("Courier",1,16));
          Palettes.setBorder(new BevelBorder(BevelBorder.LOWERED));
          Palettes.setFocusable(false);
          Paletteg.setBackground(Color.WHITE);
          Paletteg.setForeground(Color.BLACK);
          Paletteg.setFont(new Font("Courier",1,16));
          Paletteg.setBorder(new BevelBorder(BevelBorder.LOWERED));
          Paletteg.setFocusable(false);

                Output.add(Paletten,getGridBagConstraints(x, y, 1.0, 1.0, w, GridBagConstraints.BOTH));
                Output.add(Palettes,getGridBagConstraints(x, y+1, 1.0, 1.0, w, GridBagConstraints.BOTH));
                Output.add(Paletteg,getGridBagConstraints(x, y+2, 1.0, 1.0, w, GridBagConstraints.BOTH));
      }

            BufferedImage images = new BufferedImage(288, 203,  BufferedImage.SCALE_SMOOTH);
            images.getGraphics().drawImage(Switches.image, 0,0, 288, 203,null);
            JLabel Display     =   new JLabel(new ImageIcon(images));
                Output.add(Display,getGridBagConstraints(5, 4, 1.0, 1.0, 12, GridBagConstraints.BOTH));
      Output.pack();
      Output.setResizable(false);
      Output.setVisible(true);
  }

  public void exportScreen(){
      boolean RETURN = false;
            AddressA.setText("4000");
            AddressB.setText("C000");
      Object[] object = { "You can set the screen-mode and inks\n" +
              "with a CALL &C7D0 after you loaded\n" +
              "an exported screen.\n" +
              "Please choose now:\n" +
              "YES:\nWait for a key before return to BASIC", "NO:\nReturn to BASIC after a CALL &C7D0\n\n", Overscan, "Start part A",AddressA, "Start part B", AddressB };
      int selectedValue = JOptionPane.showOptionDialog(dummy, object, "Please choose:",
              JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null,null,null);
      if(selectedValue == JOptionPane.NO_OPTION) {
          RETURN = true;
      }
      if(selectedValue == JOptionPane.CANCEL_OPTION) {
          return;
      }
      try {
      startimageA = Util.hexValue(AddressA.getText());
      startimageB = Util.hexValue(AddressB.getText());
      }
      catch (final Exception error){}
      FileDialog filedia = new FileDialog((Frame) dummy, "Save 16k CPC Screen File + PAL", FileDialog.SAVE);
      filedia.setFile("*.scr");
      filedia.setVisible(true);
      String filename = filedia.getFile();
      if (filename != null) {
           filename =  filedia.getDirectory() + filedia.getFile();
           String savename = filename;
           savename = savename.toUpperCase();
           String palname = filename;

                   int length = savename.length();
                   String temp = savename;
            if (savename.toLowerCase().endsWith(".scr"))
             temp = savename.substring(0, (length - 4));
            if (savename.toLowerCase().endsWith(".bin"))
             temp = savename.substring(0, (length - 4));
                        savename=temp + ".SCR";
                        palname=temp + ".PAL";
           if (soverscan){
             String savename2 = temp+".SC1";
           savename = temp+".SC2";
               File fileb = new File(savename2);

       try {
      int memSize = 0x4000;
        byte[] data = new byte[0x080+memSize];
        for (int i = 0; i < 0x80; i++)
            data[i] = (byte)SCR_HEADER[i];

      byte[] mem = memory.getMemory();
      System.arraycopy(mem,startimageA,data,0x80, memSize - 1);


       final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileb));

      for (int i=0; i<0x080+memSize; i++)
	   	bos.write(data[i]);
      bos.close();
       }
             catch (final IOException iox) {}
           }

            File file = new File(savename);
        // saveauto data to file
    try {
      int memSize = 0x4000;
        byte[] data = new byte[0x080+memSize];
        for (int i = 0; i < 0x80; i++)
            data[i] = (byte)SCR_HEADER[i];

      byte[] mem = memory.getMemory();
      System.arraycopy(mem,startimageB,data,0x80, memSize - 1);

      if (!soverscan){
      data[0x17d0 + 0x80] = (byte)gateArray.getScreenMode();

      int adr = 0x17d1 + 0x80;
      for (int i = 0; i< 16; i++){
          data[adr+i] = (byte)gateArray.getInk(i);
      }
          adr = 0x07d0 + 0x80;
          for (int i = 0; i < 36; i++){
              data[adr+i] = (byte)SCR_CODE[i];
          }
          if (RETURN){
              data[adr+33] = (byte)0xc9; data[adr+34] = (byte)0x00; data[adr+35] = 0x00;
          }
      }

      final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

      for (int i=0; i<0x080+memSize; i++)
	   	bos.write(data[i]);
      bos.close();
      if (!soverscan)
      exportPal(palname);
    }

        catch (final IOException iox) {}
  }
}
  public void exportPal(String name){

        File file = new File(name);

        // saveauto data to file
    try {
      int memSize = 0xef;
        byte[] data = new byte[0x080+memSize];
        for (int i = 0; i < 0x80; i++)
            data[i] = (byte)PAL_HEADER[i];
        data[0x80] = (byte)gateArray.getScreenMode();
        int adr = 0x83;
        for (int i = 0; i < 17; i++)
            for (int g = 0; g < 12; g++){
                int numb = gateArray.getInk(i);
                data[adr] = (byte)PAL_INK[numb];
                adr++;
            }

      final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

      for (int i=0; i<0x080+memSize; i++)
	   	bos.write(data[i]);
      bos.close();
    }

        catch (final IOException iox) {}
  }

/* calculate checksum as AMSDOS would for the first 66 bytes of a datablock */
/* this is used to determine if a file has a AMSDOS header */
    public int ChecksumAMSDOS(byte pHeader[])
    {
        int Checksum;
        int i;
        Checksum = 0;
        for (i=0; i<67; i++)
        {
                int CheckSumByte;
                CheckSumByte = pHeader[i] & 0x0ff;
                Checksum+=CheckSumByte;
        }
        return Checksum;
    }
    public boolean CheckAMSDOS(byte pHeader[]){
         int CalculatedChecksum;
         int ChecksumFromHeader;
        CalculatedChecksum = ChecksumAMSDOS(pHeader);
        ChecksumFromHeader = (pHeader[67] & 0x0ff) | (pHeader[68] & 0x0ff)<<8;
        if (ChecksumFromHeader == CalculatedChecksum){
            System.out.println("With header");
            return true;}
            System.out.println("Without header");
        return false;
}

    public void doMultiface(){
      z80.nmi();
    }


    public void Check(){
      if (Switches.save64){
          Switches.save64=false;
          SNA_Save(64);
      }
      if (Switches.save128){
          Switches.save128=false;
          SNA_Save(128);
      }
      if (Switches.save256){
          Switches.save256=false;
          SNA_Save(256);
      }
      if (Switches.save512){
          Switches.save512=false;
          SNA_Save(512);
      }
      if (Switches.scores) {
          Switches.scores = false;
          //getScores();
          getScores.getDW3Scores();
      }
      if (Switches.poke){
          Switches.poke = false;
          POKE();
      }

      if (Switches.devil){
          Cheat();
          Switches.devil = false;
      }

      if (Switches.export){
          Switches.export = false;
          BIN_Export();
      }
      if (Switches.saveScr){
          Switches.saveScr = false;
          exportScreen();
      }

      if (Switches.showPalette){
          Switches.showPalette = false;
          showPalette();
      }
      if (Switches.dskcheck == true){
          Switches.dskcheck = false;
          SaveDSK();
      }
      if (Switches.BINImport){
        Switches.BINImport = false;
        BIN_Load();
      }
      if (Switches.digi){
          Switches.digi = false;
          Digitracker();
      }
      if (Switches.digimc){
          Switches.digimc = false;
          DigitrackerMC();
      }
      if (Switches.digipg){
          Switches.digipg = false;
          DigitrackerPG();
      }
    }

 public void smartReset(){
             YMControl.displaycount1 = 0;
             YMControl.displaycount2 = 0;
             YMControl.DisplayStart = 0;
      psg.changeClockSpeed(CYCLES_PER_SECOND_CPC);
      st_mode = false;
      zx_mode = false;
     if (YM_Play){
          YM_Play = false;
          YM_Stop = true;
          System.out.println("Playback stopped...");
     }
     Switches.turbo=1;
    super.reset();
    fdc.reset();
    memory.reset();
    reSync();
    z80.reset();
 }

  public void Digitracker(){
     launchcount=1;
     launchcode = 1;
     // launchDigitracker();
  }
  public void DigitrackerMC(){
     launchcount=1;
     launchcode = 3;
  }
  public void DigitrackerPG(){
     launchcount=1;
     launchcode = 4;
  }

 public void launchDigitracker(){

      Digitracker.Digitracker();
      runBinary(0x5d73);
      //AutoType("call &5d37\n");
      blastercount = 1;
 }
 public void launchDigitrackerMC(){
      digitrakmc.digitrakmc();
      runBinary(0x6080);
 }
 public void launchDigitrackerPG(){
      digitrakpg.digitrakpg();
      runBinary(0x5c80);
 }

  public void Cheat() {
     launchcode = 2;
     launchcount = 1;
  }

  public void launchCheat() {
      Devil.Devil();
      runBinary(0x9000);
  }
  
  public void storeYM(){
      if ((psg.readRegister(8)!=0 || psg.readRegister(9)!=0 || psg.readRegister(10)!=0) && !doRecord)
          doRecord = true;
      if (doRecord){
          vcount++;
          if (vcount == 50){
              vcount = 0;
              YM_Seconds++;
              if (YM_Seconds == 60){
                  YM_Seconds = 0;
                  YM_Minutes++;
              }
          }
          String minutes = "" + YM_Minutes;
          if (YM_Minutes <=9)
              minutes = "0" + minutes;
          String seconds = "" + YM_Seconds;
          if (YM_Seconds <=9)
              seconds = "0" + seconds;
          String milliseconds = "" + (vcount*2);
          if (vcount <=4)
              milliseconds = "0" + milliseconds;
          
          YMControl.doYMDisplay(minutes, seconds, milliseconds,
                  "recording...", minutes, seconds);
          
          for (int i = 0; i < 16; i++){
              if (i == 13){
                  if (psg.registerUpdated())
                      YM_Data[ymcount]=psg.readRegister(i);
                  else
                      YM_Data[ymcount]=0x0ff;
              }
              else {
                  YM_Data[ymcount]=psg.readRegister(i);
              }
              if (i == 14 || i == 15)
                  YM_Data[ymcount]=0x00;
              ymcount++;
              YM_RecCount++;
          }

          if (ymcount >= 999980){
              System.out.println("Recording stopped... Buffer full!");
              YM_Rec = false;
              ymcount=0;
          }
          YM_vbl++;
          psg.resetUpdated();
      }
  }

  public void playYM(){
            if (atari_st_mode && !st_mode){
                psg.changeClockSpeed(CYCLES_PER_SECOND_ST);
            st_mode = true;
            }
            if (spectrum_mode && !zx_mode){
                psg.changeClockSpeed(CYCLES_PER_SECOND_ZX);
            zx_mode = true;
            }
      //int vcount, YM_Minutes, YM_Seconds, msecs
      vcount++;
      if (vcount == 50){
          vcount = 0;
          YM_Seconds++;
      if (YM_Seconds == 60){
          YM_Seconds = 0;
        YM_Minutes++;
      }
      //System.out.println("playing..." + minutes + ":" + seconds);
      }
          String minutes = "" + YM_Minutes;
          if (YM_Minutes <=9)
              minutes = "0" + minutes;
          String seconds = "" + YM_Seconds;
          if (YM_Seconds <=9)
              seconds = "0" + seconds;
          String milliseconds = "" + (vcount*2);
          if (vcount <=4)
              milliseconds = "0" + milliseconds;
          YMControl.doYMDisplay(minutes, seconds, milliseconds, YMtitle, YMauthor, YMcreator);

      if (YM_RecCount >=1){
          for (int i = 0; i < YM_registers; i++){
              if (i == 13){
                  if (YM_Data[ymcount] != 0x0ff)
                      psg.setRegister(i, YM_Data[ymcount]);
              }
              else {
                  psg.setRegister(i, YM_Data[ymcount]);
              }
              ymcount++;

          }
            /*  if (oldYM){
                      psg.setRegister(14, 0x00);
                      psg.setRegister(15, 0x00);
              }*/
          if (ymcount >= YM_RecCount){
              YM_Minutes = 0;
              YM_Seconds = 0;
              ymcount = 0;
              YMControl.YM_Counter.setText(" 00:00\"00 ");
          }
      }
      else{
      psg.changeClockSpeed(CYCLES_PER_SECOND_CPC);
      st_mode = false;
          YM_Play = false;
          System.out.println("Sorry, no playback-data in buffer");
      }
  }

  public void loadYM(){
      jemu.ui.JEMU.ymControl.setVisible(false);
  FileDialog filedia = new FileDialog((Frame) dummy, "Load YM audio File", FileDialog.LOAD);
        filedia.setFile("*.ym; *.bin");
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename != null) {
           filename =  filedia.getDirectory() + filedia.getFile();
           String loadname = filename;
           loadYM(loadname);
        }
      jemu.ui.JEMU.ymControl.setVisible(true);
  }

  public void loadYM(String loadname){
             YMControl.displaycount1 = 0;
             YMControl.displaycount2 = 0;
             YMControl.DisplayStart = 0;
      YM_Minutes = 0;
      YM_Seconds = 0;
      ymcount = 0;
      YMControl.YM_Counter.setText(" 00:00\"00 ");
      if (loadname.startsWith("http://") || loadname.startsWith("www.")){
          if (loadname.startsWith("www."))
              loadname = "http://" + loadname;
          String savename="buffer.ym";
          String[]arg= {loadname, savename};
          jemu.ui.copyURL.main(arg);
          loadname=savename;
      }
      psg.changeClockSpeed(CYCLES_PER_SECOND_CPC);
      st_mode = false;
      YMtitle="";
      YMauthor="";
      YMcreator="";
      shouldcount = false;
      begincount = 0;
      YM_RecCount = 0;
      atari_st_mode = false;
      spectrum_mode = false;
      YM_Interleaved = false;
        File file = new File(loadname);
        int ym_read_byte = 0;
        try{
            final BufferedInputStream bos = new BufferedInputStream(new FileInputStream(file));
            long len = file.length();


      for (int ym_filepos=0; ym_filepos<=len; ym_filepos++){
            ym_read_byte = bos.read();
            if (ym_filepos == 2)
                if (ym_read_byte == 0x035 || ym_read_byte == 0x036 || ym_read_byte == 0x033){
                    if (ym_read_byte == 0x033){
                        YM_registers = 14;
                        YM_Interleaved = true;
                        oldYM = true;
                        atari_st_mode = true;
                        spectrum_mode = false;
                        System.out.println("opening YM3! file...");
                    }
                    if (ym_read_byte == 0x035){
                        YM_registers = 16;
                        oldYM = false;
                        System.out.println("opening YM5! file...");
                    }
                    if (ym_read_byte == 0x036){
                        YM_registers = 16;
                        oldYM = false;
                        System.out.println("opening YM6! file...");
                    }
                } else {

                    System.out.println("Wrong format... Cannot playback!");
                    YM_Interleaved = false;
                    atari_st_mode = false;
                    spectrum_mode = false;
                    YM_RecCount = 0;
                    bos.close();
                    return;
                }
            if (ym_filepos == 19 && ym_read_byte == 1)
                YM_Interleaved = true;
            if (ym_filepos == 25){
                if (ym_read_byte == 0x080){
                    atari_st_mode = true;
                    spectrum_mode = false;
                }
                if (ym_read_byte == 0x058){
                    atari_st_mode = false;
                    spectrum_mode = true;
                }
            }

            if (!oldYM && ym_filepos >=34 && !shouldcount){

                if (begincount == 3)
                    shouldcount = true;

                if (ym_read_byte == 0 && begincount <=5){
                    begincount++;
                }
                    if (begincount == 0){
                        if (ym_read_byte!=0){
                            char c = (char)ym_read_byte;
                            YMtitle = YMtitle + c;
                        }
                    }
                    if (begincount == 1){
                        if (ym_read_byte!=0){
                            char c = (char)(ym_read_byte);
                            YMauthor = YMauthor + c;
                        }
                    }
                    if (begincount == 2){
                        if (ym_read_byte!=0){
                            char c = (char)(ym_read_byte);
                            YMcreator = YMcreator + c;
                        }
                    }
            }

            if (oldYM && ym_filepos >=4 && !shouldcount){
                YMtitle = "none";
                YMauthor ="none";
                YMcreator = "none";
                shouldcount = true;
            }

            if (shouldcount){
                if (!YM_Interleaved){
                    YM_Data[YM_RecCount] = ym_read_byte;
                    YM_RecCount++;
                } else {
                    YM_DataInterleaved[YM_RecCount] = ym_read_byte;
                    YM_RecCount++;
                }
            }

    }
            bos.close();

            if (!oldYM)
            YM_RecCount = YM_RecCount -4;
            YM_vbl = YM_RecCount / YM_registers;
          //  YM_Play=true;

          YMControl.YM_Counter.setText(" 00:00\"00 ");
            ymcount = 0;
            if (YM_Interleaved){
                int counted = 0;
                for (int jk=0; jk<YM_registers; jk++){
                    for (int ik=0; ik<YM_RecCount/YM_registers; ik++)
                    {
                        YM_Data[(ik*YM_registers)+jk] = YM_DataInterleaved[counted];
                        counted++;
                    }
                }
            }
            System.out.println("Interleaved is " + YM_Interleaved);
            Display.title = YMtitle;
            Display.author = YMauthor;
            Display.creator = YMcreator;
                    System.out.println("Title: " + YMtitle);
                    System.out.println("Author: " + YMauthor);
                    System.out.println("Creator: " + YMcreator);
            if (atari_st_mode)
                System.out.println("AY speed is 2000000 hz!");
            else
                if (spectrum_mode)
                System.out.println("AY speed is 1773400 hz!");
                else
                System.out.println("AY speed is 1000000 hz!");
        }
        catch (final IOException iox) {
            System.out.println("can't read file ");
        }

  }

  public void saveYM(){
      jemu.ui.JEMU.ymControl.setVisible(false);
      if (YM_RecCount >=1){
            FileDialog filedia = new FileDialog((Frame) dummy, "Save YM audio File", FileDialog.SAVE);
        filedia.setFile("*.ym");
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename != null) {
           filename =  filedia.getDirectory() + filedia.getFile();
           String savename = filename;
            if (!savename.toLowerCase().endsWith(".ym"))
                        savename=savename + ".ym";
           saveYM(savename);
        }
      }
      else
          System.out.println("Sorry, no data in buffer, cannot save!");
      jemu.ui.JEMU.ymControl.setVisible(true);
  }

  public void saveYM(String savename){
    /*
    Header YM5 : (format BIG ENDIAN)

    Offset  Size    Type        Comment
    0       4       DWORD       ID of YM5 format. ('YM5!')
    4       8       string[8]   Check String ('LeOnArD!')
    12      4       DWORD       Nb of valid VBL of the file
    16      4       DWORD       Song attributes (see bellow)
    20      2       WORD        Nb of digi-drum sample (can be 0)
    22      4       DWORD       YM2149 External frequency in Hz (1000000)
    26      2       WORD        Player frequency in Hz (50Hz for almost player)
    28      4       DWORD       VBL number to loop the song. (0 is default)
    32      2       WORD        Must be 0 for the moment
    ?       ?       NT-String   Name of the song
    ?       ?       NT-String   Name of the author
    ?       ?       NT-String   Comments (Name of the YM converter !)
    ?       ?                   All YM2149 registers
    ?       4       DWORD       End-File check ('End!')

    */
        atari_st_mode = false;
        spectrum_mode = false;
        psg.changeClockSpeed(CYCLES_PER_SECOND_CPC);
        st_mode = false;
        YM_Interleaved = true;
        File file = new File(savename);
      String HeaderYM = "YM5!LeOnArD!";
      String title = "";
      String author = "";
      if (!oldYM){
            JTextField YMTitle = new JTextField();
            JTextField YMAuthor = new JTextField();
            YMAuthor.setText(author);
            YMTitle.setText(title);
                Object[] message = {"Song name", YMTitle, "Authors name", YMAuthor
                        //, YMInterleaved
                };

                JOptionPane pane = new JOptionPane( message,
                                                JOptionPane.QUESTION_MESSAGE,
                                                JOptionPane.DEFAULT_OPTION);

                pane.createDialog(null, "Create YM file").setVisible(true);


                title = YMTitle.getText();
                author = YMAuthor.getText();
      }
try {
      final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
if (!oldYM){
        if(title == null)
            title="none";
      if (author == null)
          author = "none";
      String recorded = "recorded with JavaCPC";
      // write header
      byte[] header = new byte[34];
      byte[]authorbyte = new byte[1000];
      byte[]titlebyte = new byte[1000];
      byte[]recbyte = new byte[40];
      System.arraycopy(HeaderYM.getBytes("UTF-8"), 0, header, 0, HeaderYM.length());
      System.arraycopy(title.getBytes("UTF-8"),0,titlebyte,0,title.length());
      System.arraycopy(author.getBytes("UTF-8"),0,authorbyte,0,author.length());
      System.arraycopy(recorded.getBytes("UTF-8"),0,recbyte,0,recorded.length());
      putLongBigEndian(header,12,YM_vbl);
      putWordBigEndian(header, 16,0);
      if (YM_Interleaved)
          header[19] = 1;
      else
          header[19] = 0;
      putLongBigEndian(header,22,1000000);
      putWordBigEndian(header,26,50);
      for (int i=0; i<34; i++)
          bos.write(header[i]);
      for (int i=0; i<title.length()+1; i++)
          bos.write(titlebyte[i]);
      for (int i=0; i<author.length()+1; i++)
          bos.write(authorbyte[i]);
      for (int i=0; i<recorded.length()+1; i++)
          bos.write(recbyte[i]);
}
else {
          String YMHeader = "YM3!";
          byte[] header = new byte[14];
          System.arraycopy(YMHeader.getBytes("UTF-8"), 0, header, 0, YMHeader.length());
          for (int i=0; i<4; i++)
              bos.write(header[i]);
          YM_Interleaved = true;

}
      // write data (YM_Interleaved)
      if (YM_Interleaved)
     for (int j=0; j<YM_registers; j++){
          for (int i=0; i<YM_RecCount/YM_registers; i++)
          {
              bos.write(YM_Data[(i*YM_registers)+j]);
          }
      }
      else
          for (int j=0; j<=YM_RecCount; j++){
              bos.write(YM_Data[j]);
          }
      // write endheader
      if (!oldYM){
          String EndYM = "End!";
          byte[] headerend = new byte[14];
          System.arraycopy(EndYM.getBytes("UTF-8"), 0, headerend, 0, EndYM.length());
          for (int i=0; i<4; i++)
              bos.write(headerend[i]);
      }

      bos.close();
}
 catch (final IOException iox) {
      System.out.println("can't write to file ");
    }
        }

  private void YMCheck() {

      if (YM_Rec){
          storeYM();
      }
      if (YM_Play && !YM_Stop){
          Switches.blockKeyboard = true;
          playYM();
      }
      if (YM_Stop && !YM_Rec){
          Switches.blockKeyboard = false;
      psg.changeClockSpeed(CYCLES_PER_SECOND_CPC);
      st_mode = false;
          YM_Stop = false;
          psg.setRegister(7,0x3f);
      }
      if (YM_Stop && YM_Rec){
          Switches.blockKeyboard = false;
      psg.changeClockSpeed(CYCLES_PER_SECOND_CPC);
      st_mode = false;
          YM_Stop = false;
      }
      if (YM_Save){
          YM_Save = false;
          saveYM();
      }

      if (YM_Load){
          YM_Load = false;
          loadYM();
      }
  }
  
    protected void TapeLoad(String loadname, byte[] data){
            tape_stereo = false;
        bitrate = 8;
      tapeloaded = false;
      TapeDrive.showText(loadname.toUpperCase());
        isCDT = false;
        number = 0;
        doLoad = 0;
        jemu.ui.JEMU.isTape = true;
        tapesample = null;
        tapesample = data;
        int tapelength = getDWord(tapesample, 40);
        int frequency = getDWord(tapesample, 24);
        int channels = tapesample[22];
        int bits = tapesample[34];
        if (channels >=2)
            tape_stereo = true;
        else
            tape_stereo = false;
        tape_delay = 1010000/(frequency);
        System.out.println("Tape delay is: "+ tape_delay);

        bitrate = bits;
        if (tapelength > (int)Switches.availmem)
        {
            System.out.println("Sorry, the file is too large");
            tapesample = new byte[0];
            return;
        }
        tapesample = getFile(loadname, tapelength);
        System.out.println("Stream has " + frequency + " hz, "
                 + tapelength + " bytes, " + channels + " channels, "
                  + bits + " bits");
        play = true;
        TapeDrive.btnPLAY.setBorder(new BevelBorder(BevelBorder.LOWERED));
        TapeDrive.btnPLAY.setBackground(Color.BLACK);
        TapeDrive.buttonpressed = true;
        TapeDeck.positionslider.setValue(0);
      if (Switches.FloppySound && !tapeloaded)
          Samples.TAPEINSERT.play();
        tapeloaded = true;
        recordcount = tapesample.length;
      TapeDeck.positionslider.setMaximum(recordcount);
    reSync();
}

    public void tapeEject(){
       TapeDrive.showText("No tape inserted...");
        tapesample = new byte[0];
        Settings.set(Settings.TAPE_FILE, "~none~");
        Settings.setBoolean(Settings.LOADTAPE , false);
        TapeDeck.positionslider.setValue(0);
        TapeDeck.positionslider.setMaximum(0);
      if (Switches.FloppySound)
          Samples.TAPEEJECT.play();
    }

  protected void optimizeWAV(){
  FileDialog filedia = new FileDialog((Frame) dummy, "Load WAV file", FileDialog.LOAD);
        filedia.setFile("*.wav");
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename != null) {
           filename =  filedia.getDirectory() + filedia.getFile();
           String loadname = filename;
           SetByte.setText(("6F"));
      Object[] object = { "Enter togglebyte-value" , SetByte};
      int selectedValue = JOptionPane.showOptionDialog(dummy, object, "Please enter:",
              JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,null,null);
      if(selectedValue == JOptionPane.NO_OPTION) {
          return;
      }
      if(selectedValue == JOptionPane.CANCEL_OPTION) {
          return;
      }
           optimizeWAV(loadname, SetByte.getText());
        }
  }

  protected void optimizeWAV(String name, String setbyte){
      try {
      int setByte = Util.hexValue(setbyte);
         try{
             int length;
             BufferedInputStream bos = new BufferedInputStream(new FileInputStream(name));
             tapesample = null;
             tapesample = new byte[bos.available()];
             bos.read(tapesample);
             bos.close();
             length = tapesample.length -44;
             for (int i = 0; i < length; i++){
                 int value = tapesample[i+44]^0x80;
                 if (value <= setByte)
                     tapesample[i+44] = (byte)0x026;
                 else
                     tapesample[i+44] = (byte)0x0da;
             }
             BufferedOutputStream boss = new BufferedOutputStream(new FileOutputStream(name+"_optimized.wav"));
             boss.write(tapesample);
             boss.close();
             System.out.println("Successfully optimized");
         }

        catch (final IOException iox) {
            System.out.println("can't read/write file ");
        }
      }
      catch (final Exception err){}
  }

    public void CSWLoad(String name, byte[] data){
        tapeloaded = false;
       tape_stereo = false;
        bitrate = 8;
      tapesample = null;

      int length = data.length;
      int frequency = getWord(data, 25);
      if (data[27]!=1){
          System.err.println("Wrong compression format!");
          return;
      }

      int polarity = 0x7F + data[28] % 2;
      boolean odd = ((data[28]+2) % 2 == 0);
      String pol = "positive";
      if (odd)
          pol ="negative";
      /*
      Object[] options = {"Polarity +",
                    "Polarity-"};
      int n = JOptionPane.showOptionDialog(dummy,
    "Please select positive or negative polarity\nSuggested polarity is " + pol,
    "CSW import",
    JOptionPane.YES_NO_OPTION,
    JOptionPane.QUESTION_MESSAGE,
    null,
    options,
    options[0]);

      if (n != 0)
          odd = true;
      else
          odd = false;*/
      tape_delay = 1010000/(frequency);
      System.out.println("CSW loaded.");
      System.out.println("polarity  = "+ pol);
      System.out.println("frequency = " + frequency);
      int size = 0;
      //loop1
      int i = 32;
      while (i < length){
          int a = data[i++] & 0xff;
          if (a == 0){
              a=data[i++]+(data[i++]<<8) +(data[i++]<<16) +(data[i++]<<24);
          }
          while (a-- > 0){
              size++;
          }
      }
      //size +=300000;
      tapesample = new byte[size];
      //loop2
      int tapecount = 0;
      i = 32;
      while (i < length){
          int a = data[i++] & 0xff;
          if (a == 0){
              a=data[i++] +(data[i++]<<8) +(data[i++]<<16) +(data[i++]<<24);
          }
          if (!odd)
          polarity = 0xff - polarity;
          while (a-- > 0){
              tapesample[tapecount] = (byte)(polarity^0x80);
              if (tapesample[tapecount] == 0)
                  tapesample[tapecount] = (byte)0xda;
              else
                  tapesample[tapecount] = (byte)0x26;
              tapecount++;
          }
          if(odd)
          polarity = 0xff - polarity;
      }
      play = true;
        TapeDrive.btnPLAY.setBorder(new BevelBorder(BevelBorder.LOWERED));
        TapeDrive.btnPLAY.setBackground(Color.BLACK);
        TapeDrive.buttonpressed = true;
        TapeDeck.positionslider.setValue(0);
      if (Switches.FloppySound && !tapeloaded)
          Samples.TAPEINSERT.play();
        tapeloaded = true;
        recordcount = tapesample.length;
      TapeDeck.positionslider.setMaximum(recordcount);
      TapeDeck.positionslider.setValue(0);
  }

public void MP3Load(String name){
    mp3name = name;
        mp3.setVisible(true);
        mp3count = 1;
}
public void MP3Load(){
    mp3count = 0;
    try{
        bitrate = 8;
        mp3c.convert(mp3name, "buffer.wav");
        loadFile(0,"buffer.wav");
        mp3.setVisible(false);
    }
    catch (Exception error){
        System.out.println(error.getMessage());
    }
    reSync();
}

}