package jemu.core.cpu;

import jemu.core.*;
import jemu.core.device.*;
import jemu.ui.Switches;

import javax.swing.*;
import java.awt.*;
import jemu.core.Util;
/**
* Title:        JavaCPC
* Description:  The Java CPC Emulator
* Copyright:    Copyright (c) 2002-2008
* Company:
* @author:      Markus, Richard (JEMU)
*/

public class Z80 extends Processor {
    protected boolean DEBUG_EXTRA = false;
    protected boolean DEBUG_PRE = false;
    protected boolean DEBUG_POST = false;
          byte[] preEDD = new byte[80];

    JFrame timesetter;
    JFrame timepresetter;
    JTextField timepre0 = new JTextField();
    JTextField timepre1 = new JTextField();
    JTextField timepre2 = new JTextField();
    JTextField timepre3 = new JTextField();
    JTextField timepre4 = new JTextField();
    JTextField timepre5 = new JTextField();
    JTextField timepre6 = new JTextField();
    JTextField timepre7 = new JTextField();
    JTextField timepre8 = new JTextField();
    JTextField timepre9 = new JTextField();
    JTextField timepre10 = new JTextField();
    JTextField timepre11 = new JTextField();
    JTextField timepre12 = new JTextField();
    JTextField timepre13 = new JTextField();
    JTextField timepre14 = new JTextField();
    JTextField timepre15 = new JTextField();
    JTextField timepre16 = new JTextField();
    JTextField timepre17 = new JTextField();
    JTextField timepre18 = new JTextField();
    JTextField timepre19 = new JTextField();
    JTextField timepre20 = new JTextField();
    JTextField timepre21 = new JTextField();
    JTextField timepre22 = new JTextField();
    JTextField timepre23 = new JTextField();
    JTextField timepre24 = new JTextField();
    JTextField timepre25 = new JTextField();
    JTextField timepre26 = new JTextField();
    JTextField timepre27 = new JTextField();
    JTextField timepre28 = new JTextField();
    JTextField timepre29 = new JTextField();
    JTextField timepre30 = new JTextField();
    JTextField timepre31 = new JTextField();
    JTextField timepre32 = new JTextField();
    JTextField timepre33 = new JTextField();
    JTextField timepre34 = new JTextField();
    JTextField timepre35 = new JTextField();
    JTextField timepre36 = new JTextField();
    JTextField timepre37 = new JTextField();
    JTextField timepre38 = new JTextField();
    JTextField timepre39 = new JTextField();
    JTextField timepre40 = new JTextField();
    JTextField timepre41 = new JTextField();
    JTextField timepre42 = new JTextField();
    JTextField timepre43 = new JTextField();
    JTextField timepre44 = new JTextField();
    JTextField timepre45 = new JTextField();
    JTextField timepre46 = new JTextField();
    JTextField timepre47 = new JTextField();
    JTextField timepre48 = new JTextField();
    JTextField timepre49 = new JTextField();
    JTextField timepre50 = new JTextField();
    JTextField timepre51 = new JTextField();
    JTextField timepre52 = new JTextField();
    JTextField timepre53 = new JTextField();
    JTextField timepre54 = new JTextField();
    JTextField timepre55 = new JTextField();
    JTextField timepre56 = new JTextField();
    JTextField timepre57 = new JTextField();
    JTextField timepre58 = new JTextField();
    JTextField timepre59 = new JTextField();
    JTextField timepre60 = new JTextField();
    JTextField timepre61 = new JTextField();
    JTextField timepre62 = new JTextField();
    JTextField timepre63 = new JTextField();
    JTextField timepre64 = new JTextField();
    JTextField timepre65 = new JTextField();
    JTextField timepre66 = new JTextField();
    JTextField timepre67 = new JTextField();
    JTextField timepre68 = new JTextField();
    JTextField timepre69 = new JTextField();
    JTextField timepre70 = new JTextField();
    JTextField timepre71 = new JTextField();
    JTextField timepre72 = new JTextField();
    JTextField timepre73 = new JTextField();
    JTextField timepre74 = new JTextField();
    JTextField timepre75 = new JTextField();
    JTextField timepre76 = new JTextField();
    JTextField timepre77 = new JTextField();
    JTextField timepre78 = new JTextField();
    JTextField timepre79 = new JTextField();
    JTextField timepre80 = new JTextField();

    JTextField time0 = new JTextField();
    JTextField time1 = new JTextField();
    JTextField time2 = new JTextField();
    JTextField time3 = new JTextField();
    JTextField time4 = new JTextField();
    JTextField time5 = new JTextField();
    JTextField time6 = new JTextField();
    JTextField time7 = new JTextField();
    JTextField time8 = new JTextField();
    JTextField time9 = new JTextField();
    JTextField time10 = new JTextField();
    JTextField time11 = new JTextField();
    JTextField time12 = new JTextField();
    JTextField time13 = new JTextField();
    JTextField time14 = new JTextField();


    protected int checktimes = 0;
    protected int checkpretimes = 0;

  // =============================================================
  // Timings for instructions. This is standard Z80 T-States.
  // =============================================================

  protected static final byte[] Z80_TIME_PRE = {
     4, 10,  7,  6,  4,  4,  7,  4,  4, 11,  7,  6,  4,  4,  7,  4,    // 00 .. 0F
     8, 10,  7,  6,  4,  4,  7,  4, 12, 11,  7,  6,  4,  4,  7,  4,    // 10 .. 1F
     7, 10, 16,  6,  4,  4,  7,  4,  7, 11, 16,  6,  4,  4,  7,  4,    // 20 .. 2F
     7, 10, 13,  6, 11, 11, 10,  4,  7, 11, 13,  6,  4,  4,  7,  4,    // 30 .. 3F
     4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4,    // 40 .. 4F
     4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4,    // 50 .. 5F
     4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4,    // 60 .. 6F
     7,  7,  7,  7,  7,  7,  4,  7,  4,  4,  4,  4,  4,  4,  7,  4,    // 70 .. 7F
     4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4,    // 80 .. 8F
     4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4,    // 90 .. 9F
     4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4,    // A0 .. AF
     4,  4,  4,  4,  4,  4,  7,  4,  4,  4,  4,  4,  4,  4,  7,  4,    // B0 .. BF
     5, 10, 10, 10, 10, 11,  8, 11,  5, 10, 10,  4, 10, 17,  8, 11,    // C0 .. CF
     5, 10, 10,  8, 10, 11,  8, 11,  5,  4, 10,  8, 10,  4,  8, 11,    // D0 .. DF
     5, 10, 10, 19, 10, 11,  8, 11,  5,  4, 10,  4, 10,  4,  8, 11,    // E0 .. EF
     5, 10, 10,  4, 10, 11,  8, 11,  5,  6, 10,  4, 10,  4,  8, 11     // F0 .. FF
  };

  // This table contains timings for instructions ED 40 through ED 7F,
  // followed ED A0 through ED A3, ED A8 through ED AB, ED B0 through ED B3
  // and ED B8 through ED BB
  // Each time is adjusted by 4 T-States since the first 4 T-States are the time
  // for the initial ED opcode fetch/execute
  public static final byte[] Z80_TIME_PRE_ED = {
     4,  4, 11, 16,  4, 10,  4,  5,  4,  4, 11, 16,  4, 10,  4,  5,    // 40 .. 4F
     4,  4, 11, 16,  4, 10,  4,  5,  4,  4, 11, 16,  4, 10,  4,  5,    // 50 .. 5F
     4,  4, 11, 16,  4, 10,  4, 14,  4,  4, 11, 16,  4, 10,  4, 14,    // 60 .. 6F
     4,  4, 11, 16,  4, 10,  4,  4,  4,  4, 11, 16,  4, 10,  4,  4,    // 70 .. 7F

    12, 12, 12, 12,                                                    // A0 .. A3
    12, 12, 12, 12,                                                    // A8 .. AB
    12, 12, 12, 12,                                                    // B0 .. B3
    12, 12, 12, 12                                                     // B8 .. BB
  };

  protected static final int CYCLES_EXTRA_JRCC    = 0;
  protected static final int CYCLES_EXTRA_DJNZ    = 1;
  protected static final int CYCLES_EXTRA_CALLCC  = 2;
  protected static final int CYCLES_EXTRA_RETCC   = 3;
  protected static final int CYCLES_EXTRA_LDIR    = 4;
  protected static final int CYCLES_EXTRA_CPIR    = 5;
  protected static final int CYCLES_EXTRA_INIR    = 6;
  protected static final int CYCLES_EXTRA_OTIR    = 7;
  protected static final int CYCLES_EXTRA_IDXNORM = 8;
  protected static final int CYCLES_EXTRA_IDXLDIN = 9;
  protected static final int CYCLES_EXTRA_IDXCB   = 10;
  protected static final int CYCLES_EXTRA_IM0     = 11;
  protected static final int CYCLES_EXTRA_IM1     = 12;
  protected static final int CYCLES_EXTRA_IM2     = 13;
  protected static final int CYCLES_EXTRA_INTACK  = 14;

  protected static final byte[] Z80_TIME_EXTRA = {
     5,  5,  7,  6,  5,  5,  5,  5,  8,  5,  4,  0,  0,  17,  2
  };

  // Standard registers
  public static final int B = 0;
  public static final int C = 1;
  public static final int D = 2;
  public static final int E = 3;
  public static final int H = 4;
  public static final int L = 5;
  public static final int F = 6;
  public static final int A = 7;

  // Alternate register set
  public static final int B1 = 8;
  public static final int C1 = 9;
  public static final int D1 = 10;
  public static final int E1 = 11;
  public static final int H1 = 12;
  public static final int L1 = 13;
  public static final int F1 = 14;
  public static final int A1 = 15;

  // Declarations for get/setWordReg function
  public static final int BC = 0;
  public static final int DE = 2;
  public static final int HL = 4;
  public static final int AF = 6;

  // Flags
  public static final int FS  = 0x80;
  public static final int FZ  = 0x40;
  public static final int F5  = 0x20;
  public static final int FH  = 0x10;
  public static final int F3  = 0x08;
  public static final int FPV = 0x04;
  public static final int FN  = 0x02;
  public static final int FC  = 0x01;

  protected static final int FLAG_MASK_LDIR  = 0xe9;
  protected static final int FLAG_MASK_LDDR  = 0xc1;
  protected static final int FLAG_MASK_CPIR  = 0xfa;
  protected static final int FLAG_MASK_CPL   = 0xc5;
  protected static final int FLAG_MASK_CCF   = 0xc5;
  protected static final int FLAG_MASK_SCF   = 0xc5;
  protected static final int FLAG_MASK_ADDHL = 0xc4;
  protected static final int FLAG_MASK_RLCA  = 0xc4;
  protected static final int FLAG_MASK_RLD   = 0x01;
  protected static final int FLAG_MASK_BIT   = 0x01;
  protected static final int FLAG_MASK_IN    = 0x01;
  protected static final int FLAG_MASK_INI   = 0xe8;

  // Register set
  protected int[] reg = new int[16];

  // 16-bit only registers
  protected int SP, PC, IX, IY;

  // 8-bit special registers
  protected int I, R, R7, IM;

  // Interrupt flip-flops
  protected boolean IFF1, IFF2;

  // No-Extra wait for CPC Interrupt?
  protected boolean noWait = false;

  // Currently executing a HALT instruction?
  protected boolean inHalt = false;

  // Flag to cause an interrupt to execute
  protected boolean interruptExecute = false;

  // Interrupt Vector
  protected int interruptVector = 0xff;

  // Pre-execute times
  protected byte[] timePre = new byte[256];

  // Post-execute times
  protected byte[] timePost = new byte[256];

  // CB code Pre-execute times
  protected byte[] timePreCB = new byte[256];

  // CB code Post-execute times
  protected byte[] timePostCB = new byte[256];

  // ED code Pre-execute times
  protected byte[] timePreED = new byte[128];

  // ED code Post-execute times
  protected byte[] timePostED = new byte[128];

  // Extra execute time for special or non-constant execution times
  protected byte[] timeExtra = new byte[2];

  // Parity values
  protected static int[] PARITY = new int[256];
  static {
    for (int i = 0; i < 256; i++) {
      int p = (i & 0x01) == 0 ? FPV : 0;
      if ((i & 0x02) != 0) p ^= FPV;
      if ((i & 0x04) != 0) p ^= FPV;
      if ((i & 0x08) != 0) p ^= FPV;
      if ((i & 0x10) != 0) p ^= FPV;
      if ((i & 0x20) != 0) p ^= FPV;
      if ((i & 0x40) != 0) p ^= FPV;
      if ((i & 0x80) != 0) p ^= FPV;
      PARITY[i] = p;
    }
  };

  public Z80(long cyclesPerSecond) {
    super("Zilog Z80", cyclesPerSecond);
    setTimes();
      if (DEBUG_EXTRA){
      timesetter = new JFrame("Z80_TIMES_EXTRA editor");
      timesetter.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
      timesetter.add(time0);
      timesetter.add(time1);
      timesetter.add(time2);
      timesetter.add(time3);
      timesetter.add(time4);
      timesetter.add(time5);
      timesetter.add(time6);
      timesetter.add(time7);
      timesetter.add(time8);
      timesetter.add(time9);
      timesetter.add(time10);
      timesetter.add(time11);
      timesetter.add(time12);
      timesetter.add(time13);
      timesetter.add(time14);
      time0.setText(""+timeExtra[0]+"  ");
      time1.setText(""+timeExtra[1]+"  ");
      time2.setText(""+timeExtra[2]+"  ");
      time3.setText(""+timeExtra[3]+"  ");
      time4.setText(""+timeExtra[4]+"  ");
      time5.setText(""+timeExtra[5]+"  ");
      time6.setText(""+timeExtra[6]+"  ");
      time7.setText(""+timeExtra[7]+"  ");
      time8.setText(""+timeExtra[8]+"  ");
      time9.setText(""+timeExtra[9]+"  ");
      time10.setText(""+timeExtra[10]+"  ");
      time11.setText(""+timeExtra[11]+"  ");
      time12.setText(""+timeExtra[12]+"  ");
      time13.setText(""+timeExtra[13]+"  ");
      time14.setText(""+timeExtra[14]+"  ");
      timesetter.pack();
      timesetter.setAlwaysOnTop(true);
      timesetter.setVisible(true);
      }


  }

  // Override this method to set different execution times
  protected void setTimes() {
    byte[] zeros = new byte[256];
    byte[] timesCB = new byte[256];
    for (int i = 0; i < 256; i++)
      if ((i & 0x07) != 6)              // Rotates, Shifts etc on registers
        timesCB[i] = 4;
      else if ((i & 0xc0) == 0x40)      // BIT n,(HL)
        timesCB[i] = 8;
      else
        timesCB[i] = 11;                // Rotates, Shifts and SET/RES n,(HL)
    setTimes(Z80_TIME_PRE,zeros,timesCB,zeros,Z80_TIME_PRE_ED,new byte[80],
      Z80_TIME_EXTRA);
    timePost[0xdb] = 3;                  // IN A,(n)
    timePost[0xd3] = 3;                  // OUT (n),A
    for (int i = 0; i < 64; i += 8) {
      timePostED[0x40 + i] = 4;          // IN r,(C)
      timePostED[0x41 + i] = 4;          // OUT (C),r
    }
  }

  protected void setTimes(byte[] pre, byte[] post, byte[] preCB, byte[] postCB,
    byte[] preED, byte[] postED, byte[] extra)
  {
    timePre = checkByteArraySize(pre,256);
    timePost = checkByteArraySize(post,256);
    timePreCB = checkByteArraySize(preCB,256);
    timePostCB = checkByteArraySize(postCB,256);
    checkByteArraySize(preED,80);
    checkByteArraySize(postED,80);
    timePreED = new byte[256];
    timePostED = new byte[256];
    for (int i = 0; i < 256; i++) {
      timePreED[i] = timePre[0];
      timePostED[i] = timePost[0];
    }
          if (DEBUG_PRE){
      timepresetter = new JFrame("Z80_TIMES_PRE editor");
      timepresetter.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
      timepresetter.add(timepre0);
      timepresetter.add(timepre1);
      timepresetter.add(timepre2);
      timepresetter.add(timepre3);
      timepresetter.add(timepre4);
      timepresetter.add(timepre5);
      timepresetter.add(timepre6);
      timepresetter.add(timepre7);
      timepresetter.add(timepre8);
      timepresetter.add(timepre9);
      timepresetter.add(timepre10);
      timepresetter.add(timepre11);
      timepresetter.add(timepre12);
      timepresetter.add(timepre13);
      timepresetter.add(timepre14);
      timepresetter.add(timepre15);
      timepresetter.add(timepre16);
      timepresetter.add(timepre17);
      timepresetter.add(timepre18);
      timepresetter.add(timepre19);
      timepresetter.add(timepre20);
      timepresetter.add(timepre21);
      timepresetter.add(timepre22);
      timepresetter.add(timepre23);
      timepresetter.add(timepre24);
      timepresetter.add(timepre25);
      timepresetter.add(timepre26);
      timepresetter.add(timepre27);
      timepresetter.add(timepre28);
      timepresetter.add(timepre29);
      timepresetter.add(timepre30);
      timepresetter.add(timepre31);
      timepresetter.add(timepre32);
      timepresetter.add(timepre33);
      timepresetter.add(timepre34);
      timepresetter.add(timepre35);
      timepresetter.add(timepre36);
      timepresetter.add(timepre37);
      timepresetter.add(timepre38);
      timepresetter.add(timepre39);
      timepresetter.add(timepre40);
      timepresetter.add(timepre41);
      timepresetter.add(timepre42);
      timepresetter.add(timepre43);
      timepresetter.add(timepre44);
      timepresetter.add(timepre45);
      timepresetter.add(timepre46);
      timepresetter.add(timepre47);
      timepresetter.add(timepre48);
      timepresetter.add(timepre49);
      timepresetter.add(timepre50);
      timepresetter.add(timepre51);
      timepresetter.add(timepre52);
      timepresetter.add(timepre53);
      timepresetter.add(timepre54);
      timepresetter.add(timepre55);
      timepresetter.add(timepre56);
      timepresetter.add(timepre57);
      timepresetter.add(timepre58);
      timepresetter.add(timepre59);
      timepresetter.add(timepre60);
      timepresetter.add(timepre61);
      timepresetter.add(timepre62);
      timepresetter.add(timepre63);
      timepresetter.add(timepre64);
      timepresetter.add(timepre65);
      timepresetter.add(timepre66);
      timepresetter.add(timepre67);
      timepresetter.add(timepre68);
      timepresetter.add(timepre69);
      timepresetter.add(timepre70);
      timepresetter.add(timepre71);
      timepresetter.add(timepre72);
      timepresetter.add(timepre73);
      timepresetter.add(timepre74);
      timepresetter.add(timepre75);
      timepresetter.add(timepre76);
      timepresetter.add(timepre77);
      timepresetter.add(timepre78);
      timepresetter.add(timepre79);
      timepre0.setText(""+preED[0]+"  ");
      timepre1.setText(""+preED[1]+"  ");
      timepre2.setText(""+preED[2]+"  ");
      timepre3.setText(""+preED[3]+"  ");
      timepre4.setText(""+preED[4]+"  ");
      timepre5.setText(""+preED[5]+"  ");
      timepre6.setText(""+preED[6]+"  ");
      timepre7.setText(""+preED[7]+"  ");
      timepre8.setText(""+preED[8]+"  ");
      timepre9.setText(""+preED[9]+"  ");
      timepre10.setText(""+preED[10]+"  ");
      timepre11.setText(""+preED[11]+"  ");
      timepre12.setText(""+preED[12]+"  ");
      timepre13.setText(""+preED[13]+"  ");
      timepre14.setText(""+preED[14]+"  ");
      timepre15.setText(""+preED[15]+"  ");
      timepre16.setText(""+preED[16]+"  ");
      timepre17.setText(""+preED[17]+"  ");
      timepre18.setText(""+preED[18]+"  ");
      timepre19.setText(""+preED[19]+"  ");
      timepre20.setText(""+preED[20]+"  ");
      timepre21.setText(""+preED[21]+"  ");
      timepre22.setText(""+preED[22]+"  ");
      timepre23.setText(""+preED[23]+"  ");
      timepre24.setText(""+preED[24]+"  ");
      timepre25.setText(""+preED[25]+"  ");
      timepre26.setText(""+preED[26]+"  ");
      timepre27.setText(""+preED[27]+"  ");
      timepre28.setText(""+preED[28]+"  ");
      timepre29.setText(""+preED[29]+"  ");
      timepre30.setText(""+preED[30]+"  ");
      timepre31.setText(""+preED[31]+"  ");
      timepre32.setText(""+preED[32]+"  ");
      timepre33.setText(""+preED[33]+"  ");
      timepre34.setText(""+preED[34]+"  ");
      timepre35.setText(""+preED[35]+"  ");
      timepre36.setText(""+preED[36]+"  ");
      timepre37.setText(""+preED[37]+"  ");
      timepre38.setText(""+preED[38]+"  ");
      timepre39.setText(""+preED[39]+"  ");
      timepre40.setText(""+preED[40]+"  ");
      timepre41.setText(""+preED[41]+"  ");
      timepre42.setText(""+preED[42]+"  ");
      timepre43.setText(""+preED[43]+"  ");
      timepre44.setText(""+preED[44]+"  ");
      timepre45.setText(""+preED[45]+"  ");
      timepre46.setText(""+preED[46]+"  ");
      timepre47.setText(""+preED[47]+"  ");
      timepre48.setText(""+preED[48]+"  ");
      timepre49.setText(""+preED[49]+"  ");
      timepre50.setText(""+preED[50]+"  ");
      timepre51.setText(""+preED[51]+"  ");
      timepre52.setText(""+preED[52]+"  ");
      timepre53.setText(""+preED[53]+"  ");
      timepre54.setText(""+preED[54]+"  ");
      timepre55.setText(""+preED[55]+"  ");
      timepre56.setText(""+preED[56]+"  ");
      timepre57.setText(""+preED[57]+"  ");
      timepre58.setText(""+preED[58]+"  ");
      timepre59.setText(""+preED[59]+"  ");
      timepre60.setText(""+preED[60]+"  ");
      timepre61.setText(""+preED[61]+"  ");
      timepre62.setText(""+preED[62]+"  ");
      timepre63.setText(""+preED[63]+"  ");
      timepre64.setText(""+preED[64]+"  ");
      timepre65.setText(""+preED[65]+"  ");
      timepre66.setText(""+preED[66]+"  ");
      timepre67.setText(""+preED[67]+"  ");
      timepre68.setText(""+preED[68]+"  ");
      timepre69.setText(""+preED[69]+"  ");
      timepre70.setText(""+preED[70]+"  ");
      timepre71.setText(""+preED[71]+"  ");
      timepre72.setText(""+preED[72]+"  ");
      timepre73.setText(""+preED[73]+"  ");
      timepre74.setText(""+preED[74]+"  ");
      timepre75.setText(""+preED[75]+"  ");
      timepre76.setText(""+preED[76]+"  ");
      timepre77.setText(""+preED[77]+"  ");
      timepre78.setText(""+preED[78]+"  ");
      timepre79.setText(""+preED[79]+"  ");
      timepresetter.setSize(520,250);
      timepresetter.setAlwaysOnTop(true);
      timepresetter.setVisible(true);
      }
              if (DEBUG_POST){
      timepresetter = new JFrame("Z80_TIMES_PRE editor");
      timepresetter.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
      timepresetter.add(timepre0);
      timepresetter.add(timepre1);
      timepresetter.add(timepre2);
      timepresetter.add(timepre3);
      timepresetter.add(timepre4);
      timepresetter.add(timepre5);
      timepresetter.add(timepre6);
      timepresetter.add(timepre7);
      timepresetter.add(timepre8);
      timepresetter.add(timepre9);
      timepresetter.add(timepre10);
      timepresetter.add(timepre11);
      timepresetter.add(timepre12);
      timepresetter.add(timepre13);
      timepresetter.add(timepre14);
      timepresetter.add(timepre15);
      timepresetter.add(timepre16);
      timepresetter.add(timepre17);
      timepresetter.add(timepre18);
      timepresetter.add(timepre19);
      timepresetter.add(timepre20);
      timepresetter.add(timepre21);
      timepresetter.add(timepre22);
      timepresetter.add(timepre23);
      timepresetter.add(timepre24);
      timepresetter.add(timepre25);
      timepresetter.add(timepre26);
      timepresetter.add(timepre27);
      timepresetter.add(timepre28);
      timepresetter.add(timepre29);
      timepresetter.add(timepre30);
      timepresetter.add(timepre31);
      timepresetter.add(timepre32);
      timepresetter.add(timepre33);
      timepresetter.add(timepre34);
      timepresetter.add(timepre35);
      timepresetter.add(timepre36);
      timepresetter.add(timepre37);
      timepresetter.add(timepre38);
      timepresetter.add(timepre39);
      timepresetter.add(timepre40);
      timepresetter.add(timepre41);
      timepresetter.add(timepre42);
      timepresetter.add(timepre43);
      timepresetter.add(timepre44);
      timepresetter.add(timepre45);
      timepresetter.add(timepre46);
      timepresetter.add(timepre47);
      timepresetter.add(timepre48);
      timepresetter.add(timepre49);
      timepresetter.add(timepre50);
      timepresetter.add(timepre51);
      timepresetter.add(timepre52);
      timepresetter.add(timepre53);
      timepresetter.add(timepre54);
      timepresetter.add(timepre55);
      timepresetter.add(timepre56);
      timepresetter.add(timepre57);
      timepresetter.add(timepre58);
      timepresetter.add(timepre59);
      timepresetter.add(timepre60);
      timepresetter.add(timepre61);
      timepresetter.add(timepre62);
      timepresetter.add(timepre63);
      timepresetter.add(timepre64);
      timepresetter.add(timepre65);
      timepresetter.add(timepre66);
      timepresetter.add(timepre67);
      timepresetter.add(timepre68);
      timepresetter.add(timepre69);
      timepresetter.add(timepre70);
      timepresetter.add(timepre71);
      timepresetter.add(timepre72);
      timepresetter.add(timepre73);
      timepresetter.add(timepre74);
      timepresetter.add(timepre75);
      timepresetter.add(timepre76);
      timepresetter.add(timepre77);
      timepresetter.add(timepre78);
      timepresetter.add(timepre79);
      timepre0.setText(""+postED[0]+"  ");
      timepre1.setText(""+postED[1]+"  ");
      timepre2.setText(""+postED[2]+"  ");
      timepre3.setText(""+postED[3]+"  ");
      timepre4.setText(""+postED[4]+"  ");
      timepre5.setText(""+postED[5]+"  ");
      timepre6.setText(""+postED[6]+"  ");
      timepre7.setText(""+postED[7]+"  ");
      timepre8.setText(""+postED[8]+"  ");
      timepre9.setText(""+postED[9]+"  ");
      timepre10.setText(""+postED[10]+"  ");
      timepre11.setText(""+postED[11]+"  ");
      timepre12.setText(""+postED[12]+"  ");
      timepre13.setText(""+postED[13]+"  ");
      timepre14.setText(""+postED[14]+"  ");
      timepre15.setText(""+postED[15]+"  ");
      timepre16.setText(""+postED[16]+"  ");
      timepre17.setText(""+postED[17]+"  ");
      timepre18.setText(""+postED[18]+"  ");
      timepre19.setText(""+postED[19]+"  ");
      timepre20.setText(""+postED[20]+"  ");
      timepre21.setText(""+postED[21]+"  ");
      timepre22.setText(""+postED[22]+"  ");
      timepre23.setText(""+postED[23]+"  ");
      timepre24.setText(""+postED[24]+"  ");
      timepre25.setText(""+postED[25]+"  ");
      timepre26.setText(""+postED[26]+"  ");
      timepre27.setText(""+postED[27]+"  ");
      timepre28.setText(""+postED[28]+"  ");
      timepre29.setText(""+postED[29]+"  ");
      timepre30.setText(""+postED[30]+"  ");
      timepre31.setText(""+postED[31]+"  ");
      timepre32.setText(""+postED[32]+"  ");
      timepre33.setText(""+postED[33]+"  ");
      timepre34.setText(""+postED[34]+"  ");
      timepre35.setText(""+postED[35]+"  ");
      timepre36.setText(""+postED[36]+"  ");
      timepre37.setText(""+postED[37]+"  ");
      timepre38.setText(""+postED[38]+"  ");
      timepre39.setText(""+postED[39]+"  ");
      timepre40.setText(""+postED[40]+"  ");
      timepre41.setText(""+postED[41]+"  ");
      timepre42.setText(""+postED[42]+"  ");
      timepre43.setText(""+postED[43]+"  ");
      timepre44.setText(""+postED[44]+"  ");
      timepre45.setText(""+postED[45]+"  ");
      timepre46.setText(""+postED[46]+"  ");
      timepre47.setText(""+postED[47]+"  ");
      timepre48.setText(""+postED[48]+"  ");
      timepre49.setText(""+postED[49]+"  ");
      timepre50.setText(""+postED[50]+"  ");
      timepre51.setText(""+postED[51]+"  ");
      timepre52.setText(""+postED[52]+"  ");
      timepre53.setText(""+postED[53]+"  ");
      timepre54.setText(""+postED[54]+"  ");
      timepre55.setText(""+postED[55]+"  ");
      timepre56.setText(""+postED[56]+"  ");
      timepre57.setText(""+postED[57]+"  ");
      timepre58.setText(""+postED[58]+"  ");
      timepre59.setText(""+postED[59]+"  ");
      timepre60.setText(""+postED[60]+"  ");
      timepre61.setText(""+postED[61]+"  ");
      timepre62.setText(""+postED[62]+"  ");
      timepre63.setText(""+postED[63]+"  ");
      timepre64.setText(""+postED[64]+"  ");
      timepre65.setText(""+postED[65]+"  ");
      timepre66.setText(""+postED[66]+"  ");
      timepre67.setText(""+postED[67]+"  ");
      timepre68.setText(""+postED[68]+"  ");
      timepre69.setText(""+postED[69]+"  ");
      timepre70.setText(""+postED[70]+"  ");
      timepre71.setText(""+postED[71]+"  ");
      timepre72.setText(""+postED[72]+"  ");
      timepre73.setText(""+postED[73]+"  ");
      timepre74.setText(""+postED[74]+"  ");
      timepre75.setText(""+postED[75]+"  ");
      timepre76.setText(""+postED[76]+"  ");
      timepre77.setText(""+postED[77]+"  ");
      timepre78.setText(""+postED[78]+"  ");
      timepre79.setText(""+postED[79]+"  ");
      timepresetter.setSize(520,250);
      timepresetter.setAlwaysOnTop(true);
      timepresetter.setVisible(true);
      }
    System.arraycopy(preED,0,timePreED,0x40,0x40);
    System.arraycopy(postED,0,timePostED,0x40,0x40);
    for (int i = 0; i < 4; i++) {
      System.arraycopy(preED,0x40 + i * 4,timePreED,0xa0 + i * 8,4);
      System.arraycopy(postED,0x40 + i * 4,timePostED,0xa0 + i * 8,4);
    }
    timeExtra = checkByteArraySize(extra,15);
  }

  protected static byte[] checkByteArraySize(byte[] value, int length) {
    int len = value == null ? 0 : value.length;
    if (len != length)
      throw new RuntimeException("Invalid Length for byte array: " + len +
        ". Should be " + length);
    return value;
  }

  @Override
  public void reset() {
    super.reset();
    for (int i = 0; i < reg.length; i++)
      reg[i] = 0;
    SP = PC = IX = IY = 0;
    I = R = R7 = IM = 0;
    IFF1 = IFF2 = false;
    interruptPending = 0;
    interruptExecute = inHalt = noWait = false;
  }

  public void stepOver() {
    int opcode = memory.readByte(PC);
    switch(opcode) {
      case 0x76: canRun(); runTo((PC + 1) & 0xffff); break;

      case 0xed: stepOverED();                    break;

      case 0xc4:
      case 0xcc:
      case 0xcd:
      case 0xd4:
      case 0xdc:
      case 0xe4:
      case 0xf4:
      case 0xfc: canRun(); runTo((PC + 3) & 0xffff);        break;

      default:   step();                          break;
    }
  }

  protected void stepOverED() {
    int opcode = memory.readByte((PC + 1) & 0xffff);
    switch(opcode) {
      case 0xb0:
      case 0xb1:
      case 0xb2:
      case 0xb3:
      case 0xb8:
      case 0xb9:
      case 0xba:
      case 0xbb: canRun(); runTo((PC + 2) & 0xffff);        break;

      default:   step();                          break;
    }
  }

  public final void step() {
    if (interruptExecute)
      doInterrupt();
    else if (inHalt)
	{
		step(0);
	}
	else
	      step(fetchOpCode());
  }

  protected final void step(int opcode) {
    boolean oldIFF = IFF1;
    noWait = false;
    executeNormal(opcode);
    interruptExecute = (interruptPending != 0) && oldIFF && IFF1;
  }

  protected void executeNormal(int opcode) {
      if (DEBUG_EXTRA){
          checktimes++;
          if (checktimes > 1000){
              checktimes = 0;
      try{
          timeExtra[0] = (byte)Util.hexValue(time0.getText());
          timeExtra[1] = (byte)Util.hexValue(time1.getText());
          timeExtra[2] = (byte)Util.hexValue(time2.getText());
          timeExtra[3] = (byte)Util.hexValue(time3.getText());
          timeExtra[4] = (byte)Util.hexValue(time4.getText());
          timeExtra[5] = (byte)Util.hexValue(time5.getText());
          timeExtra[6] = (byte)Util.hexValue(time6.getText());
          timeExtra[7] = (byte)Util.hexValue(time7.getText());
          timeExtra[8] = (byte)Util.hexValue(time8.getText());
          timeExtra[9] = (byte)Util.hexValue(time9.getText());
          timeExtra[10] = (byte)Util.hexValue(time10.getText());
          timeExtra[11] = (byte)Util.hexValue(time11.getText());
          timeExtra[12] = (byte)Util.hexValue(time12.getText());
          timeExtra[13] = (byte)Util.hexValue(time13.getText());
          timeExtra[14] = (byte)Util.hexValue(time14.getText());
      }
      catch (Exception e){}
      }
      }

      if (DEBUG_PRE){
          checkpretimes++;
          if (checkpretimes > 5000){
              checkpretimes = 0;
      try{
          preEDD[0] = (byte)Util.hexValue(timepre0.getText());
          preEDD[1] = (byte)Util.hexValue(timepre1.getText());
          preEDD[2] = (byte)Util.hexValue(timepre2.getText());
          preEDD[3] = (byte)Util.hexValue(timepre3.getText());
          preEDD[4] = (byte)Util.hexValue(timepre4.getText());
          preEDD[5] = (byte)Util.hexValue(timepre5.getText());
          preEDD[6] = (byte)Util.hexValue(timepre6.getText());
          preEDD[7] = (byte)Util.hexValue(timepre7.getText());
          preEDD[8] = (byte)Util.hexValue(timepre8.getText());
          preEDD[9] = (byte)Util.hexValue(timepre9.getText());
          preEDD[10] = (byte)Util.hexValue(timepre10.getText());
          preEDD[11] = (byte)Util.hexValue(timepre11.getText());
          preEDD[12] = (byte)Util.hexValue(timepre12.getText());
          preEDD[13] = (byte)Util.hexValue(timepre13.getText());
          preEDD[14] = (byte)Util.hexValue(timepre14.getText());
          preEDD[15] = (byte)Util.hexValue(timepre15.getText());
          preEDD[16] = (byte)Util.hexValue(timepre16.getText());
          preEDD[17] = (byte)Util.hexValue(timepre17.getText());
          preEDD[18] = (byte)Util.hexValue(timepre18.getText());
          preEDD[19] = (byte)Util.hexValue(timepre19.getText());
          preEDD[20] = (byte)Util.hexValue(timepre20.getText());
          preEDD[21] = (byte)Util.hexValue(timepre21.getText());
          preEDD[22] = (byte)Util.hexValue(timepre22.getText());
          preEDD[23] = (byte)Util.hexValue(timepre23.getText());
          preEDD[24] = (byte)Util.hexValue(timepre24.getText());
          preEDD[25] = (byte)Util.hexValue(timepre25.getText());
          preEDD[26] = (byte)Util.hexValue(timepre26.getText());
          preEDD[27] = (byte)Util.hexValue(timepre27.getText());
          preEDD[28] = (byte)Util.hexValue(timepre28.getText());
          preEDD[29] = (byte)Util.hexValue(timepre29.getText());
          preEDD[30] = (byte)Util.hexValue(timepre30.getText());
          preEDD[31] = (byte)Util.hexValue(timepre31.getText());
          preEDD[32] = (byte)Util.hexValue(timepre32.getText());
          preEDD[33] = (byte)Util.hexValue(timepre33.getText());
          preEDD[34] = (byte)Util.hexValue(timepre34.getText());
          preEDD[35] = (byte)Util.hexValue(timepre35.getText());
          preEDD[36] = (byte)Util.hexValue(timepre36.getText());
          preEDD[37] = (byte)Util.hexValue(timepre37.getText());
          preEDD[38] = (byte)Util.hexValue(timepre38.getText());
          preEDD[39] = (byte)Util.hexValue(timepre39.getText());
          preEDD[40] = (byte)Util.hexValue(timepre40.getText());
          preEDD[41] = (byte)Util.hexValue(timepre41.getText());
          preEDD[42] = (byte)Util.hexValue(timepre42.getText());
          preEDD[43] = (byte)Util.hexValue(timepre43.getText());
          preEDD[44] = (byte)Util.hexValue(timepre44.getText());
          preEDD[45] = (byte)Util.hexValue(timepre45.getText());
          preEDD[46] = (byte)Util.hexValue(timepre46.getText());
          preEDD[47] = (byte)Util.hexValue(timepre47.getText());
          preEDD[48] = (byte)Util.hexValue(timepre48.getText());
          preEDD[49] = (byte)Util.hexValue(timepre49.getText());
          preEDD[50] = (byte)Util.hexValue(timepre50.getText());
          preEDD[51] = (byte)Util.hexValue(timepre51.getText());
          preEDD[52] = (byte)Util.hexValue(timepre52.getText());
          preEDD[53] = (byte)Util.hexValue(timepre53.getText());
          preEDD[54] = (byte)Util.hexValue(timepre54.getText());
          preEDD[55] = (byte)Util.hexValue(timepre55.getText());
          preEDD[56] = (byte)Util.hexValue(timepre56.getText());
          preEDD[57] = (byte)Util.hexValue(timepre57.getText());
          preEDD[58] = (byte)Util.hexValue(timepre58.getText());
          preEDD[59] = (byte)Util.hexValue(timepre59.getText());
          preEDD[60] = (byte)Util.hexValue(timepre60.getText());
          preEDD[61] = (byte)Util.hexValue(timepre61.getText());
          preEDD[62] = (byte)Util.hexValue(timepre62.getText());
          preEDD[63] = (byte)Util.hexValue(timepre63.getText());
          preEDD[64] = (byte)Util.hexValue(timepre64.getText());
          preEDD[65] = (byte)Util.hexValue(timepre65.getText());
          preEDD[66] = (byte)Util.hexValue(timepre66.getText());
          preEDD[67] = (byte)Util.hexValue(timepre67.getText());
          preEDD[68] = (byte)Util.hexValue(timepre68.getText());
          preEDD[69] = (byte)Util.hexValue(timepre69.getText());
          preEDD[70] = (byte)Util.hexValue(timepre70.getText());
          preEDD[71] = (byte)Util.hexValue(timepre71.getText());
          preEDD[72] = (byte)Util.hexValue(timepre72.getText());
          preEDD[73] = (byte)Util.hexValue(timepre73.getText());
          preEDD[74] = (byte)Util.hexValue(timepre74.getText());
          preEDD[75] = (byte)Util.hexValue(timepre75.getText());
          preEDD[76] = (byte)Util.hexValue(timepre76.getText());
          preEDD[77] = (byte)Util.hexValue(timepre77.getText());
          preEDD[78] = (byte)Util.hexValue(timepre78.getText());
          preEDD[79] = (byte)Util.hexValue(timepre79.getText());

    System.arraycopy(preEDD,0,timePreED,0x40,0x40);
    for (int i = 0; i < 4; i++) {
      System.arraycopy(preEDD,0x40 + i * 4,timePreED,0xa0 + i * 8,4);
    }
      }
      catch (Exception e){}
      }
      }

            if (DEBUG_POST){
          checkpretimes++;
          if (checkpretimes > 5000){
              checkpretimes = 0;
      try{
          preEDD[0] = (byte)Util.hexValue(timepre0.getText());
          preEDD[1] = (byte)Util.hexValue(timepre1.getText());
          preEDD[2] = (byte)Util.hexValue(timepre2.getText());
          preEDD[3] = (byte)Util.hexValue(timepre3.getText());
          preEDD[4] = (byte)Util.hexValue(timepre4.getText());
          preEDD[5] = (byte)Util.hexValue(timepre5.getText());
          preEDD[6] = (byte)Util.hexValue(timepre6.getText());
          preEDD[7] = (byte)Util.hexValue(timepre7.getText());
          preEDD[8] = (byte)Util.hexValue(timepre8.getText());
          preEDD[9] = (byte)Util.hexValue(timepre9.getText());
          preEDD[10] = (byte)Util.hexValue(timepre10.getText());
          preEDD[11] = (byte)Util.hexValue(timepre11.getText());
          preEDD[12] = (byte)Util.hexValue(timepre12.getText());
          preEDD[13] = (byte)Util.hexValue(timepre13.getText());
          preEDD[14] = (byte)Util.hexValue(timepre14.getText());
          preEDD[15] = (byte)Util.hexValue(timepre15.getText());
          preEDD[16] = (byte)Util.hexValue(timepre16.getText());
          preEDD[17] = (byte)Util.hexValue(timepre17.getText());
          preEDD[18] = (byte)Util.hexValue(timepre18.getText());
          preEDD[19] = (byte)Util.hexValue(timepre19.getText());
          preEDD[20] = (byte)Util.hexValue(timepre20.getText());
          preEDD[21] = (byte)Util.hexValue(timepre21.getText());
          preEDD[22] = (byte)Util.hexValue(timepre22.getText());
          preEDD[23] = (byte)Util.hexValue(timepre23.getText());
          preEDD[24] = (byte)Util.hexValue(timepre24.getText());
          preEDD[25] = (byte)Util.hexValue(timepre25.getText());
          preEDD[26] = (byte)Util.hexValue(timepre26.getText());
          preEDD[27] = (byte)Util.hexValue(timepre27.getText());
          preEDD[28] = (byte)Util.hexValue(timepre28.getText());
          preEDD[29] = (byte)Util.hexValue(timepre29.getText());
          preEDD[30] = (byte)Util.hexValue(timepre30.getText());
          preEDD[31] = (byte)Util.hexValue(timepre31.getText());
          preEDD[32] = (byte)Util.hexValue(timepre32.getText());
          preEDD[33] = (byte)Util.hexValue(timepre33.getText());
          preEDD[34] = (byte)Util.hexValue(timepre34.getText());
          preEDD[35] = (byte)Util.hexValue(timepre35.getText());
          preEDD[36] = (byte)Util.hexValue(timepre36.getText());
          preEDD[37] = (byte)Util.hexValue(timepre37.getText());
          preEDD[38] = (byte)Util.hexValue(timepre38.getText());
          preEDD[39] = (byte)Util.hexValue(timepre39.getText());
          preEDD[40] = (byte)Util.hexValue(timepre40.getText());
          preEDD[41] = (byte)Util.hexValue(timepre41.getText());
          preEDD[42] = (byte)Util.hexValue(timepre42.getText());
          preEDD[43] = (byte)Util.hexValue(timepre43.getText());
          preEDD[44] = (byte)Util.hexValue(timepre44.getText());
          preEDD[45] = (byte)Util.hexValue(timepre45.getText());
          preEDD[46] = (byte)Util.hexValue(timepre46.getText());
          preEDD[47] = (byte)Util.hexValue(timepre47.getText());
          preEDD[48] = (byte)Util.hexValue(timepre48.getText());
          preEDD[49] = (byte)Util.hexValue(timepre49.getText());
          preEDD[50] = (byte)Util.hexValue(timepre50.getText());
          preEDD[51] = (byte)Util.hexValue(timepre51.getText());
          preEDD[52] = (byte)Util.hexValue(timepre52.getText());
          preEDD[53] = (byte)Util.hexValue(timepre53.getText());
          preEDD[54] = (byte)Util.hexValue(timepre54.getText());
          preEDD[55] = (byte)Util.hexValue(timepre55.getText());
          preEDD[56] = (byte)Util.hexValue(timepre56.getText());
          preEDD[57] = (byte)Util.hexValue(timepre57.getText());
          preEDD[58] = (byte)Util.hexValue(timepre58.getText());
          preEDD[59] = (byte)Util.hexValue(timepre59.getText());
          preEDD[60] = (byte)Util.hexValue(timepre60.getText());
          preEDD[61] = (byte)Util.hexValue(timepre61.getText());
          preEDD[62] = (byte)Util.hexValue(timepre62.getText());
          preEDD[63] = (byte)Util.hexValue(timepre63.getText());
          preEDD[64] = (byte)Util.hexValue(timepre64.getText());
          preEDD[65] = (byte)Util.hexValue(timepre65.getText());
          preEDD[66] = (byte)Util.hexValue(timepre66.getText());
          preEDD[67] = (byte)Util.hexValue(timepre67.getText());
          preEDD[68] = (byte)Util.hexValue(timepre68.getText());
          preEDD[69] = (byte)Util.hexValue(timepre69.getText());
          preEDD[70] = (byte)Util.hexValue(timepre70.getText());
          preEDD[71] = (byte)Util.hexValue(timepre71.getText());
          preEDD[72] = (byte)Util.hexValue(timepre72.getText());
          preEDD[73] = (byte)Util.hexValue(timepre73.getText());
          preEDD[74] = (byte)Util.hexValue(timepre74.getText());
          preEDD[75] = (byte)Util.hexValue(timepre75.getText());
          preEDD[76] = (byte)Util.hexValue(timepre76.getText());
          preEDD[77] = (byte)Util.hexValue(timepre77.getText());
          preEDD[78] = (byte)Util.hexValue(timepre78.getText());
          preEDD[79] = (byte)Util.hexValue(timepre79.getText());

    System.arraycopy(preEDD,0,timePostED,0x40,0x40);
    for (int i = 0; i < 4; i++) {
      System.arraycopy(preEDD,0x40 + i * 4,timePostED,0xa0 + i * 8,4);
    }
      }
      catch (Exception e){}
      }
      }

    cycle(timePre[opcode]);
    R++;
    switch(opcode) {
      case 0x00: nop();                           break;

      case 0x01:
      case 0x11:
      case 0x21:
      case 0x31: ldddnn(opcode, fetchWord());     break;

      case 0x02: ldbca();                         break;

      case 0x03:
      case 0x13:
      case 0x23:
      case 0x33: incss(opcode);                   break;

      case 0x04:
      case 0x0c:
      case 0x14:
      case 0x1c:
      case 0x24:
      case 0x2c:
      case 0x3c: incr(opcode);                    break;

      case 0x05:
      case 0x0d:
      case 0x15:
      case 0x1d:
      case 0x25:
      case 0x2d:
      case 0x3d: decr(opcode);                    break;

      case 0x06:
      case 0x0e:
      case 0x16:
      case 0x1e:
      case 0x26:
      case 0x2e:
      case 0x3e: ldrn(opcode,fetch());            break;

      case 0x07: rlca();                          break;

      case 0x08: exafaf1();                       break;

      case 0x09:
      case 0x19:
      case 0x29:
      case 0x39: addhlss(opcode);                 break;

      case 0x0a: ldabc();                         break;

      case 0x0b:
      case 0x1b:
      case 0x2b:
      case 0x3b: decss(opcode);                   break;

      case 0x0f: rrca();                          break;

      case 0x10: djnze((byte)fetch());            break;

      case 0x12: lddea();                         break;

      case 0x17: rla();                           break;

      case 0x18: jre((byte)fetch());              break;

      case 0x1a: ldade();                         break;

      case 0x1f: rra();                           break;

      case 0x20: jrnze((byte)fetch());            break;

      case 0x22: ldxxhl(fetchWord());             break;

      case 0x27: daa();                           break;

      case 0x28: jrze((byte)fetch());             break;

      case 0x2a: ldhlxx(fetchWord());             break;

      case 0x2f: cpl();                           break;

      case 0x30: jrnce((byte)fetch());            break;

      case 0x32: ldxxa(fetchWord());              break;

      case 0x34: incchl();                        break;

      case 0x35: decchl();                        break;

      case 0x36: ldhln(fetch());                  break;

      case 0x37: scf();                           break;

      case 0x38: jrce((byte)fetch());             break;

      case 0x3a: ldaxx(fetchWord());              break;

      case 0x3f: ccf();                           break;

      case 0x40:
      case 0x41:
      case 0x42:
      case 0x43:
      case 0x44:
      case 0x45:
      case 0x47:
      case 0x48:
      case 0x49:
      case 0x4a:
      case 0x4b:
      case 0x4c:
      case 0x4d:
      case 0x4f:
      case 0x50:
      case 0x51:
      case 0x52:
      case 0x53:
      case 0x54:
      case 0x55:
      case 0x57:
      case 0x58:
      case 0x59:
      case 0x5a:
      case 0x5b:
      case 0x5c:
      case 0x5d:
      case 0x5f:
      case 0x60:
      case 0x61:
      case 0x62:
      case 0x63:
      case 0x64:
      case 0x65:
      case 0x67:
      case 0x68:
      case 0x69:
      case 0x6a:
      case 0x6b:
      case 0x6c:
      case 0x6d:
      case 0x6f:
      case 0x78:
      case 0x79:
      case 0x7a:
      case 0x7b:
      case 0x7c:
      case 0x7d:
      case 0x7f: ldrr(opcode);                    break;

      case 0x46:
      case 0x4e:
      case 0x56:
      case 0x5e:
      case 0x66:
      case 0x6e:
      case 0x7e: ldrhl(opcode);                   break;

      case 0x70:
      case 0x71:
      case 0x72:
      case 0x73:
      case 0x74:
      case 0x75:
      case 0x77: ldhlr(opcode);                   break;

      case 0x76: halt();                          break;

      case 0x80:
      case 0x81:
      case 0x82:
      case 0x83:
      case 0x84:
      case 0x85:
      case 0x87: addar(opcode);                   break;

      case 0x86: addahl();                        break;

      case 0x88:
      case 0x89:
      case 0x8a:
      case 0x8b:
      case 0x8c:
      case 0x8d:
      case 0x8f: adcar(opcode);                   break;

      case 0x8e: adcahl();                        break;

      case 0x90:
      case 0x91:
      case 0x92:
      case 0x93:
      case 0x94:
      case 0x95:
      case 0x97: subar(opcode);                   break;

      case 0x96: subahl();                        break;

      case 0x98:
      case 0x99:
      case 0x9a:
      case 0x9b:
      case 0x9c:
      case 0x9d:
      case 0x9f: sbcar(opcode);                   break;

      case 0x9e: sbcahl();                        break;

      case 0xa0:
      case 0xa1:
      case 0xa2:
      case 0xa3:
      case 0xa4:
      case 0xa5:
      case 0xa7: andar(opcode);                   break;

      case 0xa6: andahl();                        break;

      case 0xa8:
      case 0xa9:
      case 0xaa:
      case 0xab:
      case 0xac:
      case 0xad:
      case 0xaf: xorar(opcode);                   break;

      case 0xae: xorahl();                        break;

      case 0xb0:
      case 0xb1:
      case 0xb2:
      case 0xb3:
      case 0xb4:
      case 0xb5:
      case 0xb7: orar(opcode);                    break;

      case 0xb6: orahl();                         break;

      case 0xb8:
      case 0xb9:
      case 0xba:
      case 0xbb:
      case 0xbc:
      case 0xbd:
      case 0xbf: cpar(opcode);                    break;

      case 0xbe: cpahl();                         break;

      case 0xc0:
      case 0xc8:
      case 0xd0:
      case 0xd8:
      case 0xe0:
      case 0xe8:
      case 0xf0:
      case 0xf8: retcc(opcode);                   break;

      case 0xc1:
      case 0xd1:
      case 0xe1:
      case 0xf1: popqq(opcode);                   break;

      case 0xc2:
      case 0xca:
      case 0xd2:
      case 0xda:
      case 0xe2:
      case 0xea:
      case 0xf2:
      case 0xfa: jpccnn(opcode,fetchWord());      break;

      case 0xc3: jpnn(fetchWord());               break;

      case 0xc4:
      case 0xcc:
      case 0xd4:
      case 0xdc:
      case 0xe4:
      case 0xec:
      case 0xf4:
      case 0xfc: callccnn(opcode,fetchWord());    break;

      case 0xc5:
      case 0xd5:
      case 0xe5:
      case 0xf5: pushqq(opcode);                  break;

      case 0xc6: addan(fetch());                  break;

      case 0xc7:
      case 0xcf:
      case 0xd7:
      case 0xdf:
      case 0xe7:
      case 0xef:
      case 0xf7:
      case 0xff: rstp(opcode);                    break;

      case 0xc9: ret();                           break;

      case 0xcb: executeCB(fetch(),false);        break;

      case 0xcd: callnn(fetchWord());             break;

      case 0xce: adcan(fetch());                  break;

      case 0xd3: outna(fetch());                  break;

      case 0xd6: suban(fetch());                  break;

      case 0xd9: exx();                           break;

      case 0xdb: inan(fetch());                   break;

      case 0xdd: IX = executeDDFD(IX,fetch());    break;

      case 0xde: sbcan(fetch());                  break;

      case 0xe3: exsphl();                        break;

      case 0xe6: andan(fetch());                  break;

      case 0xe9: jphl();                          break;

      case 0xeb: exdehl();                        break;

      case 0xed: executeED(fetch());              break;

      case 0xee: xoran(fetch());                  break;

      case 0xf3: di();                            break;

      case 0xf6: oran(fetch());                   break;

      case 0xf9: ldsphl();                        break;

      case 0xfb: ei();                            break;

      case 0xfd: IY = executeDDFD(IY,fetch());    break;

      case 0xfe: cpan(fetch());                   break;

      default:
        throw new RuntimeException("Invalid Opcode: " + Util.hex((byte)opcode));
    }
    cycle(timePost[opcode]);
  }

  protected void executeCB(int opcode, boolean ixiy) {
    cycle(timePreCB[opcode]);
    R++;
    int result = -1;
    switch(ixiy ? (opcode & 0xf8) | 0x06 : opcode) {
      case 0x00:
      case 0x01:
      case 0x02:
      case 0x03:
      case 0x04:
      case 0x05:
      case 0x07: rlcr(opcode);                    break;

      case 0x06: result = rlchl();                break;

      case 0x08:
      case 0x09:
      case 0x0a:
      case 0x0b:
      case 0x0c:
      case 0x0d:
      case 0x0f: rrcr(opcode);                    break;

      case 0x0e: result = rrchl();                break;

      case 0x10:
      case 0x11:
      case 0x12:
      case 0x13:
      case 0x14:
      case 0x15:
      case 0x17: rlr(opcode);                     break;

      case 0x16: result = rlhl();                 break;

      case 0x18:
      case 0x19:
      case 0x1a:
      case 0x1b:
      case 0x1c:
      case 0x1d:
      case 0x1f: rrr(opcode);                     break;

      case 0x1e: result = rrhl();                 break;

      case 0x20:
      case 0x21:
      case 0x22:
      case 0x23:
      case 0x24:
      case 0x25:
      case 0x27: slar(opcode);                    break;

      case 0x26: result = slahl();                break;

      case 0x28:
      case 0x29:
      case 0x2a:
      case 0x2b:
      case 0x2c:
      case 0x2d:
      case 0x2f: srar(opcode);                    break;

      case 0x2e: result = srahl();                break;

      case 0x30:
      case 0x31:
      case 0x32:
      case 0x33:
      case 0x34:
      case 0x35:
      case 0x37: sllr(opcode);                    break;

      case 0x36: result = sllhl();                break;

      case 0x38:
      case 0x39:
      case 0x3a:
      case 0x3b:
      case 0x3c:
      case 0x3d:
      case 0x3f: srlr(opcode);                    break;

      case 0x3e: result = srlhl();                break;

      case 0x40:
      case 0x41:
      case 0x42:
      case 0x43:
      case 0x44:
      case 0x45:
      case 0x47:
      case 0x48:
      case 0x49:
      case 0x4a:
      case 0x4b:
      case 0x4c:
      case 0x4d:
      case 0x4f:
      case 0x50:
      case 0x51:
      case 0x52:
      case 0x53:
      case 0x54:
      case 0x55:
      case 0x57:
      case 0x58:
      case 0x59:
      case 0x5a:
      case 0x5b:
      case 0x5c:
      case 0x5d:
      case 0x5f:
      case 0x60:
      case 0x61:
      case 0x62:
      case 0x63:
      case 0x64:
      case 0x65:
      case 0x67:
      case 0x68:
      case 0x69:
      case 0x6a:
      case 0x6b:
      case 0x6c:
      case 0x6d:
      case 0x6f:
      case 0x70:
      case 0x71:
      case 0x72:
      case 0x73:
      case 0x74:
      case 0x75:
      case 0x77:
      case 0x78:
      case 0x79:
      case 0x7a:
      case 0x7b:
      case 0x7c:
      case 0x7d:
      case 0x7f: bitbr(opcode);                   break;

      case 0x46:
      case 0x4e:
      case 0x56:
      case 0x5e:
      case 0x66:
      case 0x6e:
      case 0x76:
      case 0x7e: bitbhl(opcode);                  break;

      case 0x80:
      case 0x81:
      case 0x82:
      case 0x83:
      case 0x84:
      case 0x85:
      case 0x87:
      case 0x88:
      case 0x89:
      case 0x8a:
      case 0x8b:
      case 0x8c:
      case 0x8d:
      case 0x8f:
      case 0x90:
      case 0x91:
      case 0x92:
      case 0x93:
      case 0x94:
      case 0x95:
      case 0x97:
      case 0x98:
      case 0x99:
      case 0x9a:
      case 0x9b:
      case 0x9c:
      case 0x9d:
      case 0x9f:
      case 0xa0:
      case 0xa1:
      case 0xa2:
      case 0xa3:
      case 0xa4:
      case 0xa5:
      case 0xa7:
      case 0xa8:
      case 0xa9:
      case 0xaa:
      case 0xab:
      case 0xac:
      case 0xad:
      case 0xaf:
      case 0xb0:
      case 0xb1:
      case 0xb2:
      case 0xb3:
      case 0xb4:
      case 0xb5:
      case 0xb7:
      case 0xb8:
      case 0xb9:
      case 0xba:
      case 0xbb:
      case 0xbc:
      case 0xbd:
      case 0xbf: resbr(opcode);                   break;

      case 0x86:
      case 0x8e:
      case 0x96:
      case 0x9e:
      case 0xa6:
      case 0xae:
      case 0xb6:
      case 0xbe: result = resbhl(opcode);         break;

      case 0xc0:
      case 0xc1:
      case 0xc2:
      case 0xc3:
      case 0xc4:
      case 0xc5:
      case 0xc7:
      case 0xc8:
      case 0xc9:
      case 0xca:
      case 0xcb:
      case 0xcc:
      case 0xcd:
      case 0xcf:
      case 0xd0:
      case 0xd1:
      case 0xd2:
      case 0xd3:
      case 0xd4:
      case 0xd5:
      case 0xd7:
      case 0xd8:
      case 0xd9:
      case 0xda:
      case 0xdb:
      case 0xdc:
      case 0xdd:
      case 0xdf:
      case 0xe0:
      case 0xe1:
      case 0xe2:
      case 0xe3:
      case 0xe4:
      case 0xe5:
      case 0xe7:
      case 0xe8:
      case 0xe9:
      case 0xea:
      case 0xeb:
      case 0xec:
      case 0xed:
      case 0xef:
      case 0xf0:
      case 0xf1:
      case 0xf2:
      case 0xf3:
      case 0xf4:
      case 0xf5:
      case 0xf7:
      case 0xf8:
      case 0xf9:
      case 0xfa:
      case 0xfb:
      case 0xfc:
      case 0xfd:
      case 0xff: setbr(opcode);                   break;

      case 0xc6:
      case 0xce:
      case 0xd6:
      case 0xde:
      case 0xe6:
      case 0xee:
      case 0xf6:
      case 0xfe: result = setbhl(opcode);         break;

      default:
        throw new RuntimeException("Invalid Opcode: CB " + opcode);
    }
    if (ixiy && ((opcode & 0x07) != 0x06) && ((opcode & 0xc0) != 0x40)) {
      // Undocumented DD CB and FD CB opcodes LD r,....
      int r = opcode & 0x07;
      reg[r] = result;
    }
    cycle(timePostCB[opcode]);
  }

  protected void executeED(int opcode) {
    cycle(timePreED[opcode]);
    R++;
    switch(opcode) {
      case 0x40:
      case 0x48:
      case 0x50:
      case 0x58:
      case 0x60:
      case 0x68:
      case 0x78: inrc(opcode);                    break;

      case 0x41:
      case 0x49:
      case 0x51:
      case 0x59:
      case 0x61:
      case 0x69:
      case 0x79: outcr(opcode);                   break;

      case 0x42:
      case 0x52:
      case 0x62:
      case 0x72: sbchlss(opcode);                 break;

      // TODO: Is this dd or ss?
      case 0x43:
      case 0x53:
      case 0x63:
      case 0x73: ldxxdd(opcode,fetchWord());      break;

      case 0x44:
      case 0x4c:
      case 0x54:
      case 0x5c:
      case 0x64:
      case 0x6c:
      case 0x74:
      case 0x7c: neg();                           break;

      case 0x45:
      case 0x55:
      case 0x65:
      case 0x75: retn();                          break;

      case 0x46:
      case 0x4e:
      case 0x66:
      case 0x6e: imn(0);                          break;

      case 0x47: ldia();                          break;

      case 0x4a:
      case 0x5a:
      case 0x6a:
      case 0x7a: adchlss(opcode);                 break;

      case 0x4b:
      case 0x5b:
      case 0x6b:
      case 0x7b: ldddxx(opcode,fetchWord());      break;

      case 0x4d:
      // TODO: Check these could be RETN
      case 0x5d:
      case 0x6d:
      case 0x7d: reti();                          break;

      case 0x4f: ldra();                          break;

      case 0x56:
      case 0x76: imn(1);                          break;

      case 0x57: ldai();                          break;

      case 0x5e:
      case 0x7e: imn(2);                          break;

      case 0x5f: ldar();                          break;

      case 0x67: rrd();                           break;

      case 0x6f: rld();                           break;

      case 0x70: inc();                           break;

      case 0x71: outc0();                         break;

      case 0xa0: ldi();                           break;

      case 0xa1: cpi();                           break;

      case 0xa2: ini();                           break;

      case 0xa3: outi();                          break;

      case 0xa8: ldd();                           break;

      case 0xa9: cpd();                           break;

      case 0xaa: ind();                           break;

      case 0xab: outd();                          break;

      case 0xb0: ldir();                          break;

      case 0xb1: cpir();                          break;

      case 0xb2: inir();                          break;

      case 0xb3: otir();                          break;

      case 0xb8: lddr();                          break;

      case 0xb9: cpdr();                          break;

      case 0xba: indr();                          break;

      case 0xbb: otdr();                          break;

      default:   nop();                           break;
    }
    cycle(timePostED[opcode]);
  }

  protected int executeDDFD(int ixiy, int opcode) {
    switch(opcode) {
      case 0x09:
      case 0x19:
      case 0x21:
      case 0x22:
      case 0x23:
      case 0x24:
      case 0x25:
      case 0x26:
      case 0x29:
      case 0x2a:
      case 0x2b:
      case 0x2c:
      case 0x2d:
      case 0x2e:
      case 0x39:
      case 0x44:
      case 0x45:
      case 0x4c:
      case 0x4d:
      case 0x54:
      case 0x55:
      case 0x5c:
      case 0x5d:
      case 0x60:
      case 0x61:
      case 0x62:
      case 0x63:
      case 0x65:
      case 0x67:
      case 0x68:
      case 0x69:
      case 0x6a:
      case 0x6b:
      case 0x6c:
      case 0x6f:
      case 0x7c:
      case 0x7d:
      case 0x84:
      case 0x85:
      case 0x8c:
      case 0x8d:
      case 0x94:
      case 0x95:
      case 0x9c:
      case 0x9d:
      case 0xa4:
      case 0xa5:
      case 0xac:
      case 0xad:
      case 0xb4:
      case 0xb5:
      case 0xbc:
      case 0xbd:
      case 0xe1:
      case 0xe3:
      case 0xe5:
      case 0xe9:
      case 0xf9: ixiy = swapDDFD(ixiy,opcode);                break;

      case 0x34:
      case 0x35:
      case 0x46:
      case 0x4e:
      case 0x56:
      case 0x5e:
      case 0x70:
      case 0x71:
      case 0x72:
      case 0x73:
      case 0x77:
      case 0x7e:
      case 0x86:
      case 0x8e:
      case 0x96:
      case 0x9e:
      case 0xa6:
      case 0xae:
      case 0xb6:
      case 0xbe: indexDDFD(ixiy,opcode,CYCLES_EXTRA_IDXNORM); break;

      case 0x36: indexDDFD(ixiy,opcode,CYCLES_EXTRA_IDXLDIN); break;

      case 0xcb: indexDDFD(ixiy,opcode,CYCLES_EXTRA_IDXCB);   break;

      case 0x66:
      case 0x6e: ldrixiyd(ixiy,opcode);                       break;

      case 0x74:
      case 0x75: ldixiydr(ixiy,opcode);                       break;

      default: executeNormal(opcode);                         break;

    }
    return ixiy;
  }

  protected int swapDDFD(int ixiy, int opcode) {
    int hl = getqq(HL);
    setqq(HL,ixiy);
    executeNormal(opcode);
    ixiy = getqq(HL);
    setqq(HL,hl);
    return ixiy;
  }

  protected void indexDDFD(int ixiy, int opcode, int extra) {
    cycle(timeExtra[extra]);
    int hl = getqq(HL);
    setqq(HL,(ixiy + (byte)fetch()) & 0xffff);
    executeNormal(opcode);
    setqq(HL,hl);
  }

  protected void ldrixiyd(int ixiy, int opcode) {
    cycle(timePre[opcode] + timeExtra[CYCLES_EXTRA_IDXNORM]);
    R++;
    int r = (opcode & 0x38) >> 3;
    reg[r] = readByte((ixiy + (byte)fetch()) & 0xffff);
    cycle(timePost[opcode]);
  }

  protected void ldixiydr(int ixiy, int opcode) {
    cycle(timePre[opcode] + timeExtra[CYCLES_EXTRA_IDXNORM]);
    R++;
    int r = opcode & 0x07;
    writeByte((ixiy + (byte)fetch()) & 0xffff,reg[r]);
    cycle(timePost[opcode]);
  }

  protected void stopHalt() {
    if (inHalt) {
      inHalt = false;
      PC = (PC + 1) & 0xffff;
    }
  }

  public void nmi() {
    IFF1 = false;   // RETN will copy value back from IFF2
    stopHalt();
    //cycle(timeExtra[CYCLES_EXTRA_NMI]);
    cycle(timePre[0xcd]);  // CALL nn
    callnn(0x0066);
    cycle(timePost[0xcd]);
  }

  protected void doInterrupt() {
    interruptExecute = false;
    stopHalt();
    if (!noWait && Switches.doIntack)
      cycle(timeExtra[CYCLES_EXTRA_INTACK]);
    if (interruptDevice != null)
      interruptDevice.setInterrupt(1);
    interruptNotify();
    IFF1 = IFF2 = false;
    switch(IM) {
      case 0:
        cycle(timeExtra[CYCLES_EXTRA_IM0]);
        step(interruptVector);
        break;

      case 1:
        cycle(timeExtra[CYCLES_EXTRA_IM1]);
        step(0xff);
        break;

      case 2:
        cycle(timeExtra[CYCLES_EXTRA_IM2]);
        push(PC);
        PC = readWord((I << 8) | interruptVector);
        break;
    }
  }

  protected void interruptNotify() { }

  protected int fetchWord() {
    int lsb = fetch();
    return lsb | (fetch() << 8);
  }

  protected int fetch() {
    // TODO: Modify this for Mode 0 interrupt check
    int result = readByte(PC);
    PC = (PC + 1) & 0xffff;
    return result;
  }

  protected int fetchOpCode() {
    int result = readByte(PC);
    PC = (PC + 1) & 0xffff;
    return result;
  }

  // =============================================================
  // Op-Code functions.
  // =============================================================

  // -------------------------------------------------------------
  // 8-Bit Load Group.
  // -------------------------------------------------------------

  protected void ldrr(int opcode) {
    int r = (opcode & 0x38) >> 3;
    int r1 = opcode & 0x07;
    reg[r] = reg[r1];
  }

  protected void ldrn(int opcode, int n) {
    int r = (opcode & 0x38) >> 3;
    reg[r] = n;
  }

  protected void ldrhl(int opcode) {
    int r = (opcode & 0x38) >> 3;
    reg[r] = readByte(getqq(HL));
  }

  protected void ldhlr(int opcode) {
    int r = opcode & 0x07;
    writeByte(getqq(HL),reg[r]);
  }

  protected void ldhln(int n) {
    writeByte(getqq(HL),n);
  }

  protected void ldabc() {
    reg[A] = readByte(getqq(BC));
  }

  protected void ldade() {
    reg[A] = readByte(getqq(DE));
  }

  protected void ldaxx(int xx) {
    reg[A] = readByte(xx);
  }

  protected void ldbca() {
    writeByte(getqq(BC),reg[A]);
  }

  protected void lddea() {
    writeByte(getqq(DE),reg[A]);
  }

  protected void ldxxa(int xx) {
    writeByte(xx,reg[A]);
  }

  protected void ldai() {
    ldair(I);
  }

  protected void ldar() {
    ldair(R & 0x7f | R7);
  }

  protected void ldia() {
    I = reg[A];
    noWait = true;
  }

  protected void ldra() {
    R = reg[A];
    R7 = reg[A] & 0x80;
    noWait = true;
  }

  // -------------------------------------------------------------
  // 16-Bit Load Group.
  // -------------------------------------------------------------

  protected void ldddnn(int opcode, int nn) {
    int dd = (opcode & 0x30) >> 3;
    setdd(dd,nn);
  }

  protected void ldhlxx(int xx) {
    setqq(HL,readWord(xx));
  }

  protected void ldddxx(int opcode, int xx) {
    int dd = (opcode & 0x30) >> 3;
    setdd(dd,readWord(xx));
  }

  protected void ldxxhl(int xx) {
    writeWord(xx,getdd(HL));
  }

  protected void ldxxdd(int opcode, int xx) {
    int dd = (opcode & 0x30) >> 3;
    writeWord(xx,getdd(dd));
  }

  protected void ldsphl() {
    SP = getqq(HL);
  }

  protected void pushqq(int opcode) {
    int qq = (opcode & 0x30) >> 3;
    push(getqq(qq));
  }

  protected void popqq(int opcode) {
    int qq = (opcode & 0x30) >> 3;
    setqq(qq,pop());
  }

  // -------------------------------------------------------------
  // Exchange, Block Transfer, Block Search Group.
  // -------------------------------------------------------------

  protected void exdehl() {
    int temp = reg[D];
    reg[D] = reg[H];
    reg[H] = temp;
    temp = reg[E];
    reg[E] = reg[L];
    reg[L] = temp;
  }

  protected void exafaf1() {
    int temp = reg[A];
    reg[A] = reg[A1];
    reg[A1] = temp;
    temp = reg[F];
    reg[F] = reg[F1];
    reg[F1] = temp;
  }

  protected void exx() {
    for (int i = B; i <= L; i++) {
      int temp = reg[i];
      reg[i] = reg[i + 8];
      reg[i + 8] = temp;
    }
  }

  protected void exsphl() {
    int data = readWord(SP);
    writeWord(SP,getqq(HL));
    setqq(HL,data);
    noWait = true;
  }

  protected void ldi() {
    int de = getqq(DE);
    int hl = getqq(HL);
    writeByte(de++,readByte(hl++));
    endldi(de,hl);
  }

  protected void ldir() {
    ldi();
    if ((reg[F] & FPV) != 0) {
      PC = (PC - 2) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_LDIR]);
    }
  }

  protected void ldd() {
    int de = getqq(DE);
    int hl = getqq(HL);
    writeByte(de--,readByte(hl--));
    endldd(de,hl);
  }

  protected void lddr() {
    ldd();
    if ((reg[F] & FPV) != 0) {
      PC = (PC - 2) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_LDIR]);
    }
  }

  protected void cpi() {
    cpid(1);
  }

  protected void cpir() {
    cpid(1);
    if ((reg[F] & (FPV | FZ)) == FPV) {
      PC = (PC - 2) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_CPIR]);
      noWait = true;
    }
  }

  protected void cpd() {
    cpid(-1);
  }

  protected void cpdr() {
    cpid(-1);
    if ((reg[F] & (FPV | FZ)) == FPV) {
      PC = (PC - 2)  & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_CPIR]);
      noWait = true;
    }
  }

  // -------------------------------------------------------------
  // 8-Bit Arithmetic and Logical Group.
  // -------------------------------------------------------------

  protected void addar(int opcode) {
    int r = opcode & 0x07;
    addan(reg[r],0);
  }

  protected void addan(int n) {
    addan(n,0);
  }

  protected void addahl() {
    addan(readByte(getqq(HL)),0);
  }

  protected void adcar(int opcode) {
    int r = opcode & 0x07;
    addan(reg[r],reg[F] & FC);
  }

  protected void adcan(int n) {
    addan(n,reg[F] & FC);
  }

  protected void adcahl() {
    addan(readByte(getqq(HL)),reg[F] & FC);
  }

  protected void subar(int opcode) {
    int r = opcode & 0x07;
    suba(reg[r],0);
  }

  protected void suban(int n) {
    suba(n,0);
  }

  protected void subahl() {
    suba(readByte(getqq(HL)),0);
  }

  protected void sbcar(int opcode) {
    int r = opcode & 0x07;
    suba(reg[r],reg[F] & FC);
  }

  protected void sbcan(int n) {
    suba(n,reg[F] & FC);
  }

  protected void sbcahl() {
    suba(readByte(getqq(HL)),reg[F] & FC);
  }

  protected void andar(int opcode) {
    int r = opcode & 0x07;
    andan(reg[r]);
  }

  protected void andan(int n) {
    int a = reg[A] = reg[A] & n;
    int f = a & 0xa8 | FH | PARITY[a];
    reg[F] = a == 0 ? f | FZ : f;
  }

  protected void andahl() {
    andan(readByte(getqq(HL)));
  }

  protected void orar(int opcode) {
    int r = opcode & 0x07;
    oran(reg[r]);
  }

  protected void oran(int n) {
    int a = reg[A] = reg[A] | n;
    int f = a & 0xa8 | PARITY[a];
    reg[F] = a == 0 ? f | FZ : f;
  }

  protected void orahl() {
    oran(readByte(getqq(HL)));
  }

  protected void xorar(int opcode) {
    int r = opcode & 0x07;
    xoran(reg[r]);
  }

  protected void xoran(int n) {
    int a = reg[A] = reg[A] ^ n;
    int f = a & 0xa8 | PARITY[a];
    reg[F] = a == 0 ? f | FZ : f;
  }

  protected void xorahl() {
    xoran(readByte(getqq(HL)));
  }

  protected void cpar(int opcode) {
    int r = opcode & 0x07;
    cpan(reg[r]);
  }

  protected void cpan(int n) {
    int a = reg[A];
    int result = a - n;
    int f = (result & FS) | FN | (n & (F5 | F3));
    if (result < 0)
      f |= FC;
    result &= 0xff;
    if (((a ^ n) & 0x80) != 0 && ((result ^ a) & 0x80) != 0)
      f |= FPV;
    if ((a & 0x0f) - (n & 0x0f) < 0)
      f |= FH;
    reg[F] = result == 0 ? f | FZ : f;
  }

  protected void cpahl() {
    cpan(readByte(getqq(HL)));
  }

  protected void incr(int opcode) {
    int r = (opcode & 0x38) >> 3;
    reg[r] = incn(reg[r]);
  }

  protected void incchl() {
    int hl = getqq(HL);
    writeByte(hl,incn(readByte(hl)));
  }

  protected void decr(int opcode) {
    int r = (opcode & 0x038) >> 3;
    reg[r] = decn(reg[r]);
  }

  protected void decchl() {
    int hl = getqq(HL);
    writeByte(hl,decn(readByte(hl)));
  }

  // -------------------------------------------------------------
  // General Purpose Arithmetic and CPU Control Groups.
  // -------------------------------------------------------------

  // TODO: Extensive testing on this instruction
  protected void daa() {
    int a = reg[A];
    int f = reg[F];
    int lsn = a & 0x0f;
    int add;
    add = (lsn > 9) || ((f & FH) != 0) ? 0x06 : 0;
    if ((f & FC) != 0 || a > 0x99) {
      f |= FC;
      add |= 0x60;
    }
    if ((f & FN) != 0)
      suba(add,0);
    else
      addan(add,0);
    reg[F] = reg[F] & (~(FC | FPV)) | (f & FC) | PARITY[reg[A]];
  }

  protected void cpl() {
    int a = reg[A] = ~reg[A] & 0xff;
    reg[F] = reg[F] & FLAG_MASK_CPL | (a & (F5 | F3)) | FH | FN;
  }

  protected void neg() {
    int a = reg[A];
    reg[A] = 0;
    suba(a,0);
  }

  protected void ccf() {
    int f = reg[F];
    reg[F] = ((f & FLAG_MASK_CCF) ^ FC) | (reg[A] & (F5 | F3)) | ((f & FC) == 0 ? 0 : FH);
  }

  protected void scf() {
    reg[F] = (reg[F] & FLAG_MASK_SCF) | (reg[A] & (F5 | F3)) | FC;
  }

  protected void nop() { }

  protected void halt() {
    inHalt = true;
    PC = (PC - 1) & 0xffff;
  }

  public void di() {
    IFF1 = IFF2 = false;
  }

  public void ei() {
    IFF1 = IFF2 = true;
  }

  protected void imn(int n) {
    IM = n;
  }

  // -------------------------------------------------------------
  // 16-Bit Arithmetic Group.
  // -------------------------------------------------------------

  protected void addhlss(int opcode) {
    int ss = (opcode & 0x30) >> 3;
    int hl = getqq(HL);
    int n = getdd(ss);
    int result = hl + n;
    int f = (reg[F] & FLAG_MASK_ADDHL) | ((result >> 8) & (F5 | F3));
    if ((result & 0x10000) != 0)
      f |= FC;
    if ((hl & 0xfff) + (n & 0xfff) > 0xfff)
      f |= FH;
    setqq(HL,result);
    reg[F] = f;
  }

  protected void adchlss(int opcode) {
    int ss = (opcode & 0x30) >> 3;
    int hl = getqq(HL);
    int n = getdd(ss);
    int c = reg[F] & FC;
    int result = hl + n + c;
    int f = (result >> 8) & (FS | F5 | F3);
    if ((result & 0x10000) != 0)
      f |= FC;
    if ((hl & 0xfff) + (n & 0xfff) + c > 0xfff)
      f |= FH;
    if (((hl ^ n) & 0x8000) == 0 && ((result ^ hl) & 0x8000) != 0)
      f |= FPV;
    setqq(HL,result &= 0xffff);
    reg[F] = result == 0 ? f | FZ : f;
  }

  protected void sbchlss(int opcode) {
    int ss = (opcode & 0x30) >> 3;
    int hl = getqq(HL);
    int n = getdd(ss);
    int c = reg[F] & FC;
    int result = hl - n - c;
    int f = (result >> 8) & (FS | F5 | F3) | FN;
    if (result < 0)
      f |= FC;
    if ((hl & 0xfff) - (n & 0xfff) - c < 0)
      f |= FH;
    if (((hl ^ n) & 0x8000) != 0 && ((result ^ hl) & 0x8000) != 0)
      f |= FPV;
    setqq(HL,result &= 0xffff);
    reg[F] = result == 0 ? f | FZ : f;
  }

  protected void incss(int opcode) {
    int ss = (opcode & 0x30) >> 3;
    setdd(ss,(getdd(ss) + 1) & 0xffff);
  }

  protected void decss(int opcode) {
    int ss = (opcode & 0x30) >> 3;
    setdd(ss,(getdd(ss) - 1) & 0xffff);
  }

  // -------------------------------------------------------------
  // Rotate and Shift Group.
  // -------------------------------------------------------------

  protected void rlca() {
    int a = reg[A];
    int c = (a & 0x80) == 0 ? 0 : 1;
    reg[A] = a = ((a << 1) | c) & 0xff;
    reg[F] = (reg[F] & FLAG_MASK_RLCA) | (a & (F5 | F3)) | c;
  }

  protected void rla() {
    int a = reg[A];
    int f = reg[F];
    a = (a << 1) | (f & FC);
    f = (f & FLAG_MASK_RLCA) | (a & (F5 | F3));
    reg[A] = a & 0xff;
    reg[F] = (a & 0x100) != 0 ? f | FC : f;
  }

  protected void rrca() {
    int a = reg[A];
    int c = a & 0x01;
    reg[A] = a = ((a >> 1) | (c << 7)) & 0xff;
    reg[F] = (reg[F] & FLAG_MASK_RLCA) | (a & (F5 | F3)) | c;
  }

  protected void rra() {
    int a = reg[A];
    int f = reg[F];
    int c = a & 0x01;
    reg[A] = a = ((f & FC) == 0 ? a >> 1 : (a >> 1) | 0x80) & 0xff;
    reg[F] = (reg[F] & FLAG_MASK_RLCA) | (a & (F5 | F3)) | c;
  }

  protected void rlcr(int opcode) {
    int r = opcode & 0x07;
    reg[r] = rlcn(reg[r]);
  }

  protected int rlchl() {
    int hl = getqq(HL);
    return writeByte(hl,rlcn(readByte(hl)));
  }

  protected void rlr(int opcode) {
    int r = opcode & 0x07;
    reg[r] = rln(reg[r]);
  }

  protected int rlhl() {
    int hl = getqq(HL);
    return writeByte(hl,rln(readByte(hl)));
  }

  protected void rrcr(int opcode) {
    int r = opcode & 0x07;
    reg[r] = rrcn(reg[r]);
  }

  protected int rrchl() {
    int hl = getqq(HL);
    return writeByte(hl,rrcn(readByte(hl)));
  }

  protected void rrr(int opcode) {
    int r = opcode & 0x07;
    reg[r] = rrn(reg[r]);
  }

  protected int rrhl() {
    int hl = getqq(HL);
    return writeByte(hl,rrn(readByte(hl)));
  }

  protected void slar(int opcode) {
    int r = opcode & 0x07;
    reg[r] = slan(reg[r]);
  }

  protected int slahl() {
    int hl = getqq(HL);
    return writeByte(hl,slan(readByte(hl)));
  }

  protected void sllr(int opcode) {
    int r = opcode & 0x07;
    reg[r] = slln(reg[r]);
  }

  protected int sllhl() {
    int hl = getqq(HL);
    return writeByte(hl,slln(readByte(hl)));
  }

  protected void srar(int opcode) {
    int r = opcode & 0x07;
    reg[r] = sran(reg[r]);
  }

  protected int srahl() {
    int hl = getqq(HL);
    return writeByte(hl,sran(readByte(hl)));
  }

  protected void srlr(int opcode) {
    int r = opcode & 0x07;
    reg[r] = srln(reg[r]);
  }

  protected int srlhl() {
    int hl = getqq(HL);
    return writeByte(hl,srln(readByte(hl)));
  }

  // TODO: Extensive testing on this instruction
  protected void rld() {
    int a = reg[A];
    int hl = getqq(HL);
    int b = readByte(hl);
    writeByte(hl,((b << 4) | (a & 0x0f)) & 0xff);
    reg[A] = a = (a & 0xf0) | ((b >> 4) & 0x0f);
    int f = (reg[F] & FLAG_MASK_RLD) | (a & (FS | F5 | F3)) | PARITY[a];
    reg[F] = a == 0 ? f | FZ : f;
  }

  // TODO: Extensive testing on this instruction
  protected void rrd() {
    int a = reg[A];
    int hl = getqq(HL);
    int b = readByte(hl);
    writeByte(hl,((a << 4) & 0xf0) | ((b >> 4) & 0x0f));
    reg[A] = a = (a & 0xf0) | (b & 0x0f);
    int f = (reg[F] & FLAG_MASK_RLD) | (a & (FS | F5 | F3)) | PARITY[a];
    reg[F] = a == 0 ? f | FZ : f;
  }

  // -------------------------------------------------------------
  // Bit Set, Reset and Test Group.
  // -------------------------------------------------------------

  protected void bitbr(int opcode) {
    int r = opcode & 0x07;
    bitbn(reg[r],opcode);
  }

  protected void bitbhl(int opcode) {
    int hl = getqq(HL);
    bitbn(readByte(hl),opcode);
  }

  protected void setbr(int opcode) {
    int r = opcode & 0x07;
    reg[r] = setbn(reg[r],opcode);
  }

  protected int setbhl(int opcode) {
    int hl = getqq(HL);
    return writeByte(hl,setbn(readByte(hl),opcode));
  }

  protected void resbr(int opcode) {
    int r = opcode & 0x07;
    reg[r] = resbn(reg[r],opcode);
  }

  protected int resbhl(int opcode) {
    int hl = getqq(HL);
    return writeByte(hl,resbn(readByte(hl),opcode));
  }

  // -------------------------------------------------------------
  // Jump Group.
  // -------------------------------------------------------------

  protected void jpnn(int nn) {
    PC = nn;
  }

  protected static final int[] CC_MASK = { FZ, FZ, FC, FC, FPV, FPV, FS, FS };
  protected static final int[] CC_TEST = { 0, FZ, 0, FC, 0, FPV, 0, FS };

  protected void jpccnn(int opcode, int nn) {
    int cc = (opcode & 0x38) >> 3;
    if ((reg[F] & CC_MASK[cc]) == CC_TEST[cc])
      PC = nn;
  }

  protected void jre(byte e) {
    PC = (PC + e) & 0xffff;
  }

  protected void jrce(byte e) {
    if ((reg[F] & FC) != 0) {
      PC = (PC + e) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_JRCC]);
    }
  }

  protected void jrnce(byte e) {
    if ((reg[F] & FC) == 0) {
      PC = (PC + e) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_JRCC]);
    }
  }

  protected void jrze(byte e) {
    if ((reg[F] & FZ) != 0) {
      PC = (PC + e) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_JRCC]);
    }
  }

  protected void jrnze(byte e) {
    if ((reg[F] & FZ) == 0) {
      PC = (PC + e) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_JRCC]);
    }
  }

  protected void jphl() {
    PC = getqq(HL);
  }

  protected void djnze(byte e) {
    int b = reg[B] = (reg[B] - 1) & 0xff;
    if (b != 0) {
      PC = (PC + e) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_DJNZ]);
    }
  }

  // -------------------------------------------------------------
  // Call and Return Group.
  // -------------------------------------------------------------

  protected void callnn(int nn) {
    push(PC);
    PC = nn;
  }

  protected void callccnn(int opcode, int nn) {
    int cc = (opcode & 0x38) >> 3;
    if ((reg[F] & CC_MASK[cc]) == CC_TEST[cc]) {
      push(PC);
      PC = nn;
      cycle(timeExtra[CYCLES_EXTRA_CALLCC]);
    }
  }

  protected void ret() {
    PC = pop();
  }

  protected void retcc(int opcode) {
    int cc = (opcode & 0x38) >> 3;
    if ((reg[F] & CC_MASK[cc]) == CC_TEST[cc]) {
      PC = pop();
      cycle(timeExtra[CYCLES_EXTRA_RETCC]);
    }
  }

  protected void reti() {
    PC = pop();
  }

  protected void retn() {
    PC = pop();
    IFF1 = IFF2;
  }

  protected void rstp(int opcode) {
    push(PC);
    PC = opcode & 0x38;
  }

  // -------------------------------------------------------------
  // Input and Output Groups.
  // -------------------------------------------------------------

  protected void inan(int n) {
    reg[A] = in((reg[A] << 8) | n);
  }

  protected void inrc(int opcode) {
    int r = (opcode & 0x38) >> 3;
    int result = reg[r] = in(getqq(BC));
    int f = (reg[F] & FLAG_MASK_IN) | (result & (FS | F5 | F3)) | PARITY[result];
    reg[F] = result == 0 ? f | FZ : f;
  }

  protected void inc() {
    int result = in(getqq(BC));
    int f = (reg[F] & FLAG_MASK_IN) | (result & (FS | F5 | F3)) | PARITY[result];
    reg[F] = result == 0 ? f | FZ : f;
  }

  protected void ini() {
    inid(1);
  }

  protected void inir() {
    inid(1);
    if ((reg[F] & FZ) == 0) {
      PC = (PC - 2) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_INIR]);
    }
  }

  protected void ind() {
    inid(-1);
  }

  protected void indr() {
    if ((reg[F] & FZ) == 0) {
      PC = (PC - 2) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_INIR]);
    }
  }

  protected void outna(int n) {
    out((reg[A] << 8) | n,reg[A]);
  }

  protected void outcr(int opcode) {
    int r = (opcode & 0x38) >> 3;
    out(getqq(BC),reg[r]);
  }

  protected void outc0() {
    out(getqq(BC),0);
  }

  protected void outi() {
    outid(1);
  }

  protected void otir() {
    outid(1);
    if ((reg[F] & FZ) == 0) {
      PC = (PC - 2) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_OTIR]);
    }
  }

  protected void outd() {
    outid(-1);
  }

  protected void otdr() {
    outid(-1);
    if ((reg[F] & FZ) == 0) {
      PC = (PC - 2) & 0xffff;
      cycle(timeExtra[CYCLES_EXTRA_OTIR]);
    }
  }

  // =============================================================
  // Extra Op-Code Utility functions.
  // =============================================================

  protected void endldi(int de, int hl) {
    setqq(DE,de & 0xffff);
    setqq(HL,hl & 0xffff);
    int bc = (getqq(BC) - 1) & 0xffff;
    setqq(BC,bc);
    int f = reg[F] & FLAG_MASK_LDIR;
    reg[F] = bc != 0 ? f | FPV : f;
    noWait = true;
  }

  protected void endldd(int de, int hl) {
    setqq(DE,de & 0xffff);
    setqq(HL,hl & 0xffff);
    int bc = (getqq(BC) - 1) & 0xffff;
    setqq(BC,bc);
    int f = reg[F] & FLAG_MASK_LDDR;
    reg[F] = bc != 0 ? f | FPV : f;
    noWait = true;
  }

  protected void cpid(int add) {
    int f = reg[F] & FC;
    int hl = getqq(HL);
    cpan(readByte(hl));
    hl = (hl + add) & 0xffff;
    setqq(HL,hl);
    int bc = (getqq(BC) - 1) & 0xffff;
    setqq(BC,bc);
    f = reg[F] & FLAG_MASK_CPIR | f;
    reg[F] = bc != 0 ? f | FPV : f;
  }

  protected void ldair(int a) {
    reg[A] = a;
    int f = (a & 0xa8) | (reg[F] & FC);
    if (IFF2)
      f |= FPV;
    reg[F] = a == 0 ? f | FZ : f;
    noWait = true;
  }

  // TODO: Extensive testing of the PV flag
  protected void addan(int n, int c) {
    int a = reg[A];
    int result = a + n + c;
    int f = result & (FS | F3 | F5);
    if ((result & 0x100) != 0)
      f |= FC;
    reg[A] = (result &= 0xff);
    // TODO: Is carry included in the first part of this test?
    if (((a ^ n) & 0x80) == 0 && ((result ^ a) & 0x80) != 0)
      f |= FPV;
    if ((a & 0x0f) + (n & 0x0f) + c > 0x0f)
      f |= FH;
    reg[F] = result == 0 ? f | FZ : f;
  }

  // TODO: Extensive testing of the PV flag
  protected void suba(int n, int c) {
    int a = reg[A];
    int result = a - n - c;
    int f = result & (FS | F3 | F5) | FN;
    if (result < 0)
      f |= FC;
    reg[A] = (result &= 0xff);
    // TODO: Is carry included in the first part of this test?
    if (((a ^ n) & 0x80) != 0 && ((result ^ a) & 0x80) != 0)
      f |= FPV;
    if ((a & 0x0f) - (n & 0x0f) - c < 0)
      f |= FH;
    reg[F] = result == 0 ? f | FZ : f;
  }

  protected int incn(int n) {
    n = (n + 1) & 0xff;
    int f = n & 0xa8 | (reg[F] & FC);
    if ((n & 0x0f) == 0)
      f |= FH;
    if (n == 0x80)
      f |= FPV;
    else if (n == 0)
      f |= FZ;
    reg[F] = f;
    return n;
  }

  protected int decn(int n) {
    n = (n - 1) & 0xff;
    int f = n & 0xa8 | (reg[F] & FC) | FN;
    if ((n & 0x0f) == 0x0f)
      f |= FH;
    if (n == 0x7f)
      f |= FPV;
    else if (n == 0)
      f |= FZ;
    reg[F] = f;
    return n;
  }

  protected int rlcn(int n) {
    int c = (n & 0x80) == 0 ? 0 : 1;
    n = ((n << 1) | c) & 0xff;
    int f = (n & (FS | F5 | F3)) | c | PARITY[n];
    reg[F] = n == 0 ? f | FZ : f;
    return n;
  }

  protected int rln(int n) {
    int f = reg[F];
    n = (n << 1) | (f & FC);
    f = (n & (FS | F5 | F3));
    if ((n & 0x100) != 0)
      f |= FC;
    n &= 0xff;
    reg[F] = (n == 0 ? f | FZ : f) | PARITY[n];
    return n;
  }

  protected int rrcn(int n) {
    int c = n & 0x01;
    n = ((n >> 1) | (c << 7)) & 0xff;
    int f = (n & (FS | F5 | F3)) | c | PARITY[n];
    reg[F] = n == 0 ? f | FZ : f;
    return n;
  }

  protected int rrn(int n) {
    int f = reg[F];
    int c = n & 0x01;
    n = ((f & FC) == 0 ? n >> 1 : (n >> 1) | 0x80) & 0xff;
    f = (n & (FS | F5 | F3)) | c | PARITY[n];
    reg[F] = n == 0 ? f | FZ : f;
    return n;
  }

  protected int slan(int n) {
    n <<= 1;
    int f = n & (FS | F5 | F3);
    if ((n & 0x100) != 0)
      f |= FC;
    n &= 0xff;
    reg[F] = (n == 0 ? f | FZ : f) | PARITY[n];
    return n;
  }

  protected int slln(int n) {
    n = (n << 1) | 1;
    int f = n & (FS | F5 | F3);
    if ((n & 0x100) != 0)
      f |= FC;
    n &= 0xff;
    reg[F] = (n == 0 ? f | FZ : f) | PARITY[n];
    return n;
  }

  protected int sran(int n) {
    int c = n & 0x01;
    n = (n >> 1) | (n & 0x80);
    int f = (n & (FS | F5 | F3)) | c | PARITY[n];
    reg[F] = n == 0 ? f | FZ : f;
    return n;
  }

  protected int srln(int n) {
    int c = n & 0x01;
    n >>= 1;
    int f = (n & (FS | F5 | F3)) | c | PARITY[n];
    reg[F] = n == 0 ? f | FZ : f;
    return n;
  }

  protected void bitbn(int n, int opcode) {
    int b = (opcode & 0x38) >> 3;
    int f = (reg[F] & FLAG_MASK_BIT) | (n & (F5 | F3)) | FH;
    if (((n >> b) & 0x01) != 0)
      reg[F] = b == 7 ? f | FS : f;
    else
      reg[F] = f | (FZ | FPV);
  }

  protected int setbn(int n, int opcode) {
    int b = (opcode & 0x38) >> 3;
    return n | (0x01 << b);
  }

  protected int resbn(int n, int opcode) {
    int b = (opcode & 0x38) >> 3;
    return n & ~(0x01 << b);
  }

  protected void inid(int add) {
    int hl = getqq(HL);
    int b = reg[B];
    int c = reg[C];
    int result = in((b << 8) | c);
    writeByte(hl,result);
    setqq(HL,(hl + add) & 0xffff);
    // TODO: This is from Z80 CPU Specifications by Sean Young
    c = ((((c + add) & 0xff) + result) & 0x100) != 0 ? FC | FH : 0;
    reg[B] = b = decn(b);
    // TODO: The documentation is not clear about the parity result
    reg[F] = (reg[F] & FLAG_MASK_INI) | c | PARITY[result] | ((result & 0x80) >> 6);
  }

  protected void outid(int add) {
    int hl = getqq(HL);
    int b = reg[B] = decn(reg[B]);
    out((b << 8) | reg[C],readByte(hl));
    setqq(HL, (hl + add) & 0xffff);
    // TODO: Check other flags in these instructions
  }

  // =============================================================
  // Utility functions.
  // =============================================================

  protected int getqq(int index) {
    return index == AF ? reg[index] | (reg[index + 1] << 8) :
      reg[index + 1] | (reg[index] << 8);
  }

  protected void setqq(int index, int value) {
    if (index == AF) {
      reg[index] = value & 0xff;
      reg[index + 1] = (value >> 8) & 0xff;
    }
    else {
      reg[index + 1] = value & 0xff;
      reg[index] = (value >> 8) & 0xff;
    }
  }

  protected int getdd(int index) {
    return index == AF ? SP : reg[index + 1] | (reg[index] << 8);
  }

  protected void setdd(int index, int value) {
    if (index == AF)
      SP = value & 0xffff;
    else {
      reg[index + 1] = value & 0xff;
      reg[index] = (value >> 8) & 0xff;
    }
  }

  protected int pop() {
    int result = readWord(SP);
    SP = (SP + 2) & 0xffff;
    return result;
  }

  protected void push(int data) {
    SP = (SP - 2) & 0xffff;
    writeWord(SP,data);
  }

  public String getState() {
    String result =
      "AF :" + Util.hex((short)getqq(AF)) + " HL :" + Util.hex((short)getqq(HL)) +
      " DE :" + Util.hex((short)getqq(DE)) + " BC :" + Util.hex((short)getqq(BC)) +
      " IX :" + Util.hex((short)IX) + " IY :" + Util.hex((short)IY) + "\n" +
      "AF':" + Util.hex((byte)reg[A1]) + Util.hex((byte)reg[F1]) + " HL':" + Util.hex((short)getqq(H1)) +
      " DE':" + Util.hex((short)getqq(D1)) + " BC':" + Util.hex((short)getqq(B1)) +
      " Cycles: " + Util.hex((int)cycles);
    return result;
  }

  protected static final Register[] REGISTERS = {
    new Register("Flags",8,"SZ-H-VNC"),
    new Register("AF",16), new Register("AF'",16,1),
    new Register("HL",16), new Register("HL'",16,1),
    new Register("DE",16), new Register("DE'",16,1),
    new Register("BC",16), new Register("BC'",16,1),
    new Register("IX",16), new Register("SP",16,1),
    new Register("IY",16), new Register("I",8,1),
    new Register("PC",16), new Register("R",8,1)
  };

  @Override
  public Register[] getRegisters() {
    return REGISTERS;
  }

  @Override
  public int getRegisterValue(int index) {
    int result;
    switch(index) {
      case 0:  result = reg[F];                   break;
      case 1:  result = getqq(AF);                break;
      case 2:  result = reg[F1] | (reg[A1] << 8); break;
      case 3:  result = getqq(HL);                break;
      case 4:  result = getqq(H1);                break;
      case 5:  result = getqq(DE);                break;
      case 6:  result = getqq(D1);                break;
      case 7:  result = getqq(BC);                break;
      case 8:  result = getqq(B1);                break;
      case 9:  result = IX;                       break;
      case 10: result = SP;                       break;
      case 11: result = IY;                       break;
      case 12: result = I;                        break;
      case 13: result = PC;                       break;
      case 14: result = (R & 0x7f) | R7;          break;

      default: result = 0;
    }
    return result;
  }

  public int getProgramCounter() {
    return PC;
  }

  public void setAF(int value) {
    setqq(AF,value);
  }
  public void setB(int value) {
    setqq(B,value);
  }

  public void setBC(int value) {
    setqq(BC,value);
  }

  public void setDE(int value) {
    setqq(DE,value);
  }

  public void setHL(int value) {
    setqq(HL,value);
  }

  public void setR(int value) {
    R = value & 0xff;
    R7 = value & 0x80;
  }

  public void setI(int value) {
    I = value & 0xff;
  }

  public void setIFF1(boolean value) {
    IFF1 = value;
  }

  public void setIFF2(boolean value) {
    IFF2 = value;
  }

  public int getIFF1() {
    if (IFF1)
        return 0xff;
    return 0;
  }

  public int getIFF2() {
    if (IFF2)
        return 0xff;
    return 0;
  }

  public void setIX(int value) {
    IX = value & 0xffff;
  }

  public void setIY(int value) {
    IY = value & 0xffff;
  }

  public void setSP(int value) {
    SP = value & 0xffff;
  }

  public void setPC(int value) {
    PC = value & 0xffff;
  }

  public void setIM(int value) {
    IM = value & 0xff;
  }
  public int getIM() {
    return IM;
  }

  public void setAF1(int value) {
    reg[F1] = value & 0xff;
    reg[A1] = (value >> 8) & 0xff;
  }

  public void setBC1(int value) {
    setqq(B1,value);
  }

  public void setDE1(int value) {
    setqq(D1,value);
  }

  public void setHL1(int value) {
    setqq(H1,value);
  }

  public boolean isInHalt() {
    return inHalt;
  }

  //
  // Snapshot stuff here
  //

  public int getPC() {
    return PC & 0xffff;
  }
  public int getAF(){
      return AF & 0xffff;
  }
  public int getBC(){
      return BC & 0xffff;
  }
  public int getDE(){
      return DE;
  }
  public int getHL(){
      return HL & 0xffff;
  }
  public int getR(){
      return R & 0xff;
  }
  public int getI(){
      return I & 0xff;
  }
  public int getIX(){
      return IX & 0xffff;
  }
  public int getIY(){
      return IY & 0xffff;
  }
  public int getSP(){
      return SP & 0xffff;
  }
  public int getAF1(){
      return reg[F1] | (reg[A1] >> 8) & 0xffff;
  }
  public int getBC1(){
      return getqq(B1);
  }
  public int getDE1(){
      return getqq(D1);
  }
  public int getHL1(){
      return getqq(H1);
  }
}