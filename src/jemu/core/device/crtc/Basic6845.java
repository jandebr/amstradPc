package jemu.core.device.crtc;

import jemu.core.*;
import jemu.core.device.*;
import jemu.ui.Switches;
import jemu.system.cpc.CPC;

/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class Basic6845 extends CRTC {

    boolean ignore;

    boolean hitech, camemb4, skoh, tomate = false;

  protected  final Register[] REGISTERS = {
  /* REG  0 */  new Register("Horizontal Total"),
  /* REG  1 */  new Register("Horizontal Displayed"),
  /* REG  2 */  new Register("HSync Position"),
  /* REG  3 */  new Register("Sync Width"),
  /* REG  4 */  new Register("Vertical Total"),
  /* REG  5 */  new Register("Vertical Total Adjust"),
  /* REG  6 */  new Register("Vertical Displayed"),
  /* REG  7 */  new Register("VSync Position"),
  /* REG  8 */  new Register("Interlace and Delay"),
  /* REG  9 */  new Register("Max Scan Line"),
  /* REG 10 */  new Register("Cursor Start"),
  /* REG 11 */  new Register("Cursor End"),
  /* REG 12 */  new Register("Screen Address",16),
  /* REG 13 */  new Register("Cursor Address",16)
    // Light Pen register!
  };

  protected CPC cpc;
  protected final int EVENT_HSYNC_START = 0x01;
  protected final int EVENT_HDISP_END   = 0x02;
  protected final int EVENT_HDISP_START = 0x04;
  protected final int EVENT_VSYNC_START = 0x08;
  protected final int EVENT_VSYNC_END   = 0x10;

  protected int statusRegister = 0;

  public boolean Scratch, Scratchdemo, Scratchinit = false;

  protected static final int[] CURSOR_FLASH_MASKS = { 0x40, 0x00, 0x10, 0x20 };

  public int ma;
  public int ra;
  protected int hCC;
  public int vCC;
  protected int hCCMask = 0x7f;

  protected int[] reg = new int[32];
  protected int[] orig = new int[32];
  protected int[] rdMask;
  protected int[] wrMask;

  protected int[] eventMask = new int[256];

  protected int registerSelectMask = 0x01;
  protected int registerSelectTest = 0x00;

  protected int selReg;

  protected int hChars = 1;
  protected int hSyncStart, hDispEnd, hSyncWidth, hSyncCount, vSyncCount;
  protected int vSyncWidth;
  protected boolean inHSync = false;
  protected boolean inVSync = false;
  public boolean hDisp = true;
  public boolean vDisp = true;
  protected boolean interlace = false;
  protected int interlaceVideo = 0;  // 0 for normal, 1 for interlace sync & video
  protected int scanAdd = 1;         // 2 for interlace sync & video
  protected int maxRaster = 0;       // Register 9 | interlaceVideo
  protected int frame = 0;           // Toggles between 0 and 1 for interlace mode
  public int maBase = 0;          // Base address for current character line
  public int maScreen = 0;        // Base address at start of screen
  protected int vtAdj = 0;           // Vertical total adjust
  protected int halfR0 = 0;          // Used for VSync in interlace odd frames
  protected int hDispDelay = 0;      // HDISP delay
  protected int cursorMA = 0;        // Cursor position
  protected int cursorStart = 0;     // Cursor start
  protected int cursorEnd   = 0;     // Cursor end
  protected boolean cursor = false;  // Is cursor on?
  protected int cursorCount = 0;     // Cursor flash counter
  protected int cursorFlash = 0;     // Cursor flash mask
  protected int cursorDelay = 0;     // Cursor delay programmed in register 8
  protected int cursorWait  = 0;     // Delay counter

  public Basic6845() {
    super("Basic 6845");
    rdMask = wrMask = new int[32];
    for (int i = 0; i < 32; i++)
      rdMask[i] = wrMask[i] = 0xff;
    reset();
  }

  public void init(){
      Scratch = false;
      Scratchdemo = false;
      Scratchinit = false;
      hitech = camemb4 = skoh = tomate = false;
      setReg3(142);
  }

  public void reset() {
    selReg = hCC = 0;
    ma = maBase;
    hSyncWidth = hSyncCount = 0;
    inHSync = false;
    for (int i = 0; i < eventMask.length; i++)
      eventMask[i] = 0;
    reg[0] = 0x3f & wrMask[0];
    setEvents();
  }

  public void setWriteMask(int reg, int mask) {
    wrMask[reg] = mask;
  }

  public void cycle() {
    if (hCC == reg[0]) {
      hCC = 0;
      scanStart();
      ma = maBase;
    }
    else {
      hCC = (hCC + 1) & hCCMask;
      ma = (maBase + hCC) & 0x3fff;
    }
    if (inHSync) {
      hSyncCount = (hSyncCount + 1) & 0x0f;
      if (hSyncCount == hSyncWidth) {
        inHSync = false;
        listener.hSyncEnd();
      }
    }
    int mask = eventMask[hCC];
    if (mask != 0) {
      if ((mask & EVENT_VSYNC_START) != 0) {
        eventMask[hCC] &= ~EVENT_VSYNC_START;
        inVSync = true;
        listener.vSyncStart();
      }
      else if ((mask & EVENT_VSYNC_END) != 0) {
        eventMask[hCC] &= ~EVENT_VSYNC_END;
        inVSync = false;
        listener.vSyncEnd();
      }
      if ((mask & EVENT_HSYNC_START) != 0) {
        inHSync = true;
        hSyncCount = 0;
        listener.hSyncStart();
      }
      if (vDisp || (Switches.CRTC == 1 && Scratchdemo)) {
        if ((mask & EVENT_HDISP_START) != 0) {
          hDisp = true;
          listener.hDispStart();
              if (vCC==0 && Switches.CRTC == 1 && !camemb4)
                  ma = maBase = maScreen;
        }
        if ((mask & EVENT_HDISP_END) != 0) {
          hDisp = false;
          listener.hDispEnd();
          if ((ra | interlaceVideo) == maxRaster){
              maBase = (maBase + reg[1]) & 0x3fff;
          }
        } 
      }
    }
    if (cursor) {
      if (cursorWait > 0) {
        if (--cursorWait == 0)
          listener.cursor();
      }
      else if (ma == cursorMA && ra >= cursorStart && ra <= cursorEnd && hDisp) {
        if ((cursorWait = cursorDelay) == 0)
          listener.cursor();
      }
    }
  }

  protected void newFrame() {
    vCC = 0;
    frame = interlace ? frame ^ 0x01 : 0;
    ra = frame & interlaceVideo;
    vDisp = reg[6] != 0;
    ma = maBase = maScreen;
    listener.vDispStart();
    checkVSync();
    cursorCount = (cursorCount + 1) | 0x40;       // 0x40 is for always on
    cursor = (cursorCount & cursorFlash) != 0 && cursorDelay != 0x03;
  }

  protected void checkVSync() {
    if (vCC == reg[7] && !inVSync) {
      vSyncCount = 0;
      if (interlace && (frame == 0))
        eventMask[halfR0] |= EVENT_VSYNC_START;
      else {
        inVSync = true;
        listener.vSyncStart();
      }
      //  System.out.println("vSync Start: reg7=" + reg[7]);
    }
  }

  protected void scanStart() {
      if (reg[9] == 0 && reg[4] == 0)
          vtAdj  =1;
    demoDetect();
   // System.out.println(Integer.toString(vCC) + ":" + ra + " ");
    if (inVSync && (vSyncCount = (vSyncCount + 1) & 0x0f) == vSyncWidth) {
      if (interlace && (frame == 0))
        eventMask[halfR0] |= EVENT_VSYNC_END;
      else {
        inVSync = false;
        listener.vSyncEnd();
      }
    }
    if (vtAdj > 0 && --vtAdj == 0)
      newFrame();
    else if ((ra | interlaceVideo) == maxRaster) {
      if (vCC == reg[4] && vtAdj == 0) {
        vtAdj = reg[5];
        if (interlace && frame == 0)
          vtAdj++;
        if (vtAdj == 0) {
          newFrame();
          return;
        }
      }
      vCC = (vCC + 1) & 0x7f;
      checkVSync();
      if (vCC == reg[6])
        vDisp = false;
      ra = frame & interlaceVideo;
    }
    else
      ra = (ra + scanAdd) & 0x1f;
  }

  public void writePort(int port, int value) {
    if ((port & registerSelectMask) == registerSelectTest){
      selReg = value & 0x1f;
    }
    else
      setRegister(selReg,value);
    if (port == 0xbf00)
        statusRegister = value;
  }

  public void setRegister(int index, int value) {
    //  if (index != 6 && index != 0)
   // System.out.println("Reg " + index + " = " + Util.hex((byte)value));
    orig[index] = value & 0xff;
    value &= wrMask[index];
    if (reg[index] != value) {
      reg[index] = value;
      switch(index) {
        case 0:
        case 1:
        case 2: setEvents(); break;
        case 3: setReg3(value); setEvents(); break;
          case 6: setReg6(); break;
        case 8: setReg8(value); break;
        case 9: maxRaster = value | interlaceVideo; break;
        case 10: cursorStart = value & 0x1f; cursorFlash = CURSOR_FLASH_MASKS[(value >> 5) & 0x03]; break;
        case 11: cursorEnd = value & 0x1f; break;
        case 12:
        case 13: maScreen = (reg[13] + (reg[12] << 8)) & 0x3fff; break;
        case 14:
        case 15: cursorMA = (reg[15] + (reg[14] << 8)) & 0x3fff; break;
      }
    }
    if (reg[7] == 0)
          vSyncCount = 0;
    

    if (Scratchdemo && !Scratchinit && Switches.CRTC == 1){
        Scratchinit = true;
        listener.POKE(0xc89,0xc9);
    }
  }

  protected void setReg6(){
      if (reg[6] == 0xff)
          Scratchdemo = true;
      if (Scratchdemo)
          if ((reg[6] == 0 || reg[6] == 0xff) && Switches.CRTC == 1){
              vDisp = reg[6] != 0x0;
              listener.hDispStart();
          }
  }

  protected void demoDetect(){
    if (listener.PEEK(0x8800) == 0x94
     && listener.PEEK(0x8801) == 0x4c
     && listener.PEEK(0x8802) == 0x94)
        camemb4 = true;
    else
        camemb4 = false;
    if (listener.PEEK(0x34d7) == 0x48
     && listener.PEEK(0x34d8) == 0x61
     && listener.PEEK(0x34db) == 0x7a){
        tomate = true;
        setReg3(142);}
    else
        tomate = false;
    if (listener.PEEK(0x1a74) == 0x40
     && listener.PEEK(0x1a79) == 0x46
     && listener.PEEK(0x0a60) == 0x78)
        hitech = true;
    else
        hitech = false;
    if (!Scratch && listener.PEEK(0x01ec) == 0x32
            && listener.PEEK(0x01ed) == 0x030
            && listener.PEEK(0x01ef) == 0x039){
        Scratchdemo = true;
        setReg3(143);
        Scratch = true;
    }
    if (listener.PEEK(0xadf0) == 0x6c
     && listener.PEEK(0x3f90) == 0x4d
     && listener.PEEK(0x3f91) == 0x4f
     && listener.PEEK(0x3f92) == 0x4b)
        skoh = true;
    else
        skoh = false;
  }

  protected void demoPatch(){
    if (hitech){
        if (reg[2] != 49 && reg[2] != 0)
            reg[2] = 0xff;
    }
    if (camemb4){
        if (reg[1] == 24){
         reg[1] = 23;
         reg[0] = 255;
        }
    }
    if (skoh)
        hSyncCount = 0x0c;
    if (!Scratchdemo)
      Scratchinit = false;
  }
  protected void setEvents() {
    eventMask[hSyncStart] &= ~EVENT_HSYNC_START;
    eventMask[hDispDelay] &= ~EVENT_HDISP_START;
    eventMask[hDispEnd] &= ~EVENT_HDISP_END;
    demoPatch();
    hChars = reg[0] + 1;
    halfR0 = hChars >> 1;
    hSyncStart = reg[2];
    hDispDelay = ((reg[8] >> 4) & 0x03);
    hDispEnd = reg[1] + hDispDelay;
    eventMask[hSyncStart] |= EVENT_HSYNC_START;
    eventMask[hDispDelay] |= EVENT_HDISP_START;
    eventMask[hDispEnd] |= EVENT_HDISP_END;
  }

  protected void setReg3(int value) {
      boolean debug = false;
      if ((Scratchdemo || tomate) && !Scratch)
          if (value==142) value = 141;
      if (value != 13  && value >3) value-=1;
      if (value == 139) value-=1;
    hSyncWidth = value & 0x0f;
    if (Switches.CRTC == 1)
    vSyncWidth =  15;
    else
    vSyncWidth = (value >> 5) & 0x0f;
    if (debug)
        System.err.println("setReg3: value is:" + value +
                " hSyncWidth is:" + hSyncWidth + " vSyncWidth is:" +vSyncWidth);
  }

  protected void setReg8(int value) {
    interlace = (value & 0x01) != 0;
    interlaceVideo = (value & 0x03) == 3 ? 1 : 0;
    scanAdd = interlaceVideo + 1;
    maxRaster = reg[9] | interlaceVideo;
    cursorDelay = (value >> 6) & 0x03;
    setEvents();
  }

  public void setRegisterSelectMask(int mask, int test) {
    registerSelectMask = mask;
    registerSelectTest = test;
  }

  public int getHCC() {
    return hCC;
  }

  public boolean isVDisp() {
    return vDisp;
  }

  public boolean isVSync() {
    return inVSync;
  }

  public boolean isHSync() {
    return inHSync;
  }

  public boolean isHDisp() {
    return hDisp;
  }

  public int getMA() {
    return ma;
  }

  public int getRA() {
    return ra;
  }

  public int getScreenMA() {
    return maScreen;
  }

  public int getVCC() {
    return vCC;
  }

  public int getVLC() {
    return ra;
  }

  public int getReg(int index) {
    return reg[index];
  }

  public int getSelectedRegister(){
      return selReg;
  }

  public void setSelectedRegister(int value) {
    selReg = value & 0x1f;
  }

  public boolean isInterlace() {
    return interlace;
  }

  public boolean isInterlaceVideo() {
    return interlaceVideo == 1;
  }

  public int getFrame() {
    return frame;
  }

  public int getRegisterValue(int index) {
    int result;
    result = reg[index];
    if (index < 14  && index > 9 && Switches.CRTC == 1)
        result = 0xff;
    else{
        if (index == 12) result = (reg[12] << 8) | reg[13];
        else if (index == 13) result = (reg[14] << 8) | reg[15];
    }
    return result;
  }

}