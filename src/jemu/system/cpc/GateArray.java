package jemu.system.cpc;

import java.awt.Dimension;

import jemu.core.device.crtc.*;
import jemu.core.renderer.MonitorRenderer;
import jemu.ui.Switches;

/**
* Title:        JavaCPC
* Description:  The Java CPC Emulator
* Copyright:    Copyright (c) 2002-2008
* Company:
* @author:      Markus, Richard (JEMU)m
*/

public class GateArray extends MonitorRenderer implements CRTCListener {

    int keepCRTC;

  protected static final int HOFFSET  = 0x0b0000 - 0x800;    // Offset of monitor (half pixel adjust) 0xa80000
  protected static final int HOFFSEND = HOFFSET + 0x300000;  // End of offset

  protected static Dimension HALF_DISPLAY_SIZE = new Dimension(384,272);
  protected static Dimension FULL_DISPLAY_SIZE = new Dimension(768,272);
  static int returnmode = 0;
  static int rambank = 0;

  public static boolean doRender = true;
  protected CPC cpc;
  protected Z80 z80;
  protected Basic6845 crtc;
  protected int r52;
  protected int vSyncInt		= 0;
  protected int interruptMask;
  protected int hSyncCount;
  protected int vSyncCount	= 0;
  protected int screenMode	= -1;
  protected int newMode		= 0;
  protected boolean inHSyncA	= false;
  protected boolean outHSync	= false;
  protected int[] inks		= new int[33];
  protected byte[] fullMap;
  protected byte[] halfMap;
  protected int offset		= 0;
  protected int scanStart		= 0;
  protected boolean scanStarted;
  protected Renderer borderRenderer;
  protected Renderer syncRenderer;
  protected Renderer defRenderer;
  protected Renderer startRenderer;
  protected Renderer endRenderer;
  protected Renderer renderer;
  protected int endPix;
  protected int selInk		= 0;
  protected boolean render	= true;
  protected boolean rendering	= true;
  protected byte[] memory;
 // protected int[] palTranslate	= new int[4096];
  protected boolean halfSize	= false;
  protected int Luminance = 255;

  protected static final int[] maTranslate = new int[65536];
  protected static int[] CPCInks = new int[33];
  protected static int[] CPCInksb = new int[33];
  protected int[] GAInks		= {
  13,27,19,25,1,7,10,16,28,29,24,26,6,8,15,17,30,31,18,20,0,2,9,11,4,22,21,23,3,5,12,14};
  protected static final int[] inkTranslateColor = {
                /*R G B */
                0x606060, /*13*/
                0x606060, /*27*/
                0x00FF60, /*19*/
                0xFFFF60, /*25*/
                0x000060, /* 1*/
                0xFF0060, /* 7*/
                0x006060, /*10*/
                0xFF6060, /*16*/
                0xFF0060, /*28*/
                0xFFFF60, /*29*/
                0xFFFF00, /*24*/
                0xFFFFFF, /*26*/
                0xFF0000, /* 6*/
                0xFF00FF, /* 8*/
                0xFF6000, /*15*/
                0xFF60FF, /*17*/
                0x000060, /*30*/
                0x00FF60, /*31*/
                0x00FF00, /*18*/
                0x00FFFF, /*20*/
                0x000000, /* 0*/
                0x0000FF, /* 2*/
                0x006000, /* 9*/
                0x0060FF, /*11*/
                0x600060, /* 4*/
                0x60FF60, /*22*/
                0x60FF00, /*21*/
                0x60FFFF, /*23*/
                0x600000, /* 3*/
                0x6000FF, /* 5*/
                0x606000, /*12*/
                0x6060FF  /*14*/
	};

/*	protected static final int[] inkTranslateLinear = {
                /*R G B */
      /*          0x7F7F7F, /*13*/
      /*          0x7F7F7F, /*27*/
      /*          0x00FF7F, /*19*/
      /*          0xFFFF7F, /*25*/
      /*          0x00007F, /* 1*/
      /*          0xFF007F, /* 7*/
      /*          0x007F7F, /*10*/
      /*          0xFF7F7F, /*16*/
      /*          0xFF007F, /*28*/
      /*          0xFFFF7F, /*29*/
      /*          0xFFFF00, /*24*/
      /*          0xFFFFFF, /*26*/
      /*          0xFF0000, /* 6*/
      /*          0xFF00FF, /* 8*/
      /*          0xFF7F00, /*15*/
      /*          0xFF7FFF, /*17*/
      /*          0x00007F, /*30*/
      /*          0x00FF7F, /*31*/
      /*          0x00FF00, /*18*/
      /*          0x00FFFF, /*20*/
      /*          0x000000, /* 0*/
      /*          0x0000FF, /* 2*/
      /*          0x007F00, /* 9*/
      /*          0x007FFF, /*11*/
      /*          0x7F007F, /* 4*/
      /*          0x7FFF7F, /*22*/
      /*          0x7FFF00, /*21*/
      /*          0x7FFFFF, /*23*/
      /*          0x7F0000, /* 3*/
      /*          0x7F00FF, /* 5*/
      /*          0x7F7F00, /*12*/
      /*          0x7F7FFF  /*14*/
	//};
      
	protected static final int[] inkTranslateLinear = {
                /*R G B */
                0x7F7F7F, /*13*/
                0x5F5F5F, /*27*/
                0x00FF7F, /*19*/
                0xFFFF7F, /*25*/
                0x00007F, /* 1*/
                0xFF007F, /* 7*/
                0x007F7F, /*10*/
                0xFF7F7F, /*16*/
                0x9F9F9F, /*28*/
                0xBFBFBF, /*29*/
                0xFFFF00, /*24*/
                0xFFFFFF, /*26*/
                0xFF0000, /* 6*/
                0xFF00FF, /* 8*/
                0xFF7F00, /*15*/
                0xFF7FFF, /*17*/
                0xDFDFDF, /*30*/
                0x0000AF, /*31*/
                0x00FF00, /*18*/
                0x00FFFF, /*20*/
                0x000000, /* 0*/
                0x0000FF, /* 2*/
                0x007F00, /* 9*/
                0x007FFF, /*11*/
                0x7F007F, /* 4*/
                0x7FFF7F, /*22*/
                0x7FFF00, /*21*/
                0x7FFFFF, /*23*/
                0x7F0000, /* 3*/
                0x7F00FF, /* 5*/
                0x7F7F00, /*12*/
                0x7F7FFF  /*14*/
	};   
               
       protected static final int[] inkTranslateGreen = {
                /*R G B */
                0x007D00, /*13*/
                0x007D00, /*27*/
                0x00B500, /*19*/
                0x00EF00, /*25*/
                0x000A00, /* 1*/
                0x004300, /* 7*/
                0x006000, /*10*/
                0x009900, /*16*/
                0x004300, /*28*/
                0x00EF00, /*29*/
                0x00E500, /*24*/
                0x00F800, /*26*/
                0x003900, /* 6*/
                0x004C00, /* 8*/
                0x009000, /*15*/
                0x00A300, /*17*/
                0x00A000, /*30*/
                0x00B500, /*31*/
                0x00AC00, /*18*/
                0x00BF00, /*20*/
                0x000000, /* 0*/
                0x001300, /* 2*/
                0x005700, /* 9*/
                0x006A00, /*11*/
                0x002600, /* 4*/
                0x00D200, /*22*/
                0x00C900, /*21*/
                0x00DC00, /*23*/
                0x001D00, /* 3*/
                0x003000, /* 5*/
                0x007300, /*12*/
                0x008600  /*14*/
	};     

        protected static final int[] inkTranslateBW = {
                /*R G B */
                0x686868, /*13*/
                0x686868, /*27*/
                0x989898, /*19*/
                0xC8C8C8, /*25*/
                0x080808, /* 1*/
                0x383838, /* 7*/
                0x505050, /*10*/
                0x808080, /*16*/
                0x707070, /*28*/
                0x909090, /*29*/
                0xC0C0C0, /*24*/
                0xD0D0D0, /*26*/
                0x303030, /* 6*/
                0x404040, /* 8*/
                0x787878, /*15*/
                0x888888, /*17*/
                0xB8B8B8, /*30*/
                0x989898, /*31*/
                0x909090, /*18*/
                0xA0A0A0, /*20*/
                0x000000, /* 0*/
                0x101010, /* 2*/
                0x484848, /* 9*/
                0x585858, /*11*/
                0x202020, /* 4*/
                0xB0B0B0, /*22*/
                0xA8A8A8, /*21*/
                0xB8B8B8, /*23*/
                0x181818, /* 3*/
                0x282828, /* 5*/
                0x606060, /*12*/
                0x707070  /*14*/
	};      
                
  protected static final byte[][] fullMaps = new byte[4][];
  protected static final byte[][] halfMaps = new byte[4][];
  static {
    for (int mode = 0; mode < 4; mode++) {
      byte[] full = new byte[65536 * 16];
      byte[] half = new byte[65536 * 8];
      fullMaps[mode] = full;
      halfMaps[mode] = half;
      for (int i = 0; i < 65536 * 8;) {
        int b1 = (i >> 3) & 0xff;
        int b2 = (i >> 11) & 0xff;
        decodeHalf(half,i,mode,b1);
        decodeFull(full,i * 2,mode,b1);
        i += 4;
        decodeHalf(half,i,mode,b2);
        decodeFull(full,i * 2,mode,b2);
        i += 4;
      }
    }
    for (int i = 0; i < maTranslate.length; i++) {
      int j = i << 1;
      maTranslate[i] = (j & 0x7fe) | ((j & 0x6000) << 1);
    }
  }

  protected static final void decodeHalf(byte[] map, int offs, int mode, int b) {
    switch(mode) {
      case 0:
        map[offs] = map[offs + 1] = (byte)
          (((b & 0x80) >> 7) | ((b & 0x08) >> 2) | ((b & 0x20) >> 3) | ((b & 0x02) << 2));
        map[offs + 2] = map[offs + 3] = (byte)
          (((b & 0x40) >> 6) | ((b & 0x04) >> 1) | ((b & 0x10) >> 2) | ((b & 0x01) << 3));
        break;

      case 1:
        map[offs++] = (byte)(((b & 0x80) >> 7) | ((b & 0x08) >> 2));
        map[offs++] = (byte)(((b & 0x40) >> 6) | ((b & 0x04) >> 1));
        map[offs++] = (byte)(((b & 0x20) >> 5) | (b & 0x02));
        map[offs]   = (byte)(((b & 0x10) >> 4) | ((b & 0x01) << 1));
        break;

      case 2:
        map[offs++] = (byte)((b & 0x80) >> 7);
        map[offs++] = (byte)((b & 0x20) >> 5);
        map[offs++] = (byte)((b & 0x08) >> 3);
        map[offs]   = (byte)((b & 0x02) >> 1);
        break;

      case 3:
        map[offs] = map[offs + 1] = (byte)
          (((b & 0x80) >> 7) | ((b & 0x08) >> 2) );
        map[offs + 2] = map[offs + 3] = (byte)
          (((b & 0x40) >> 6) | ((b & 0x04) >> 1) );
        break;
    }
  }

  protected static final void decodeFull(byte[] map, int offs, int mode, int b) {
    switch(mode) {
      case 0:
        map[offs] = map[offs + 1] = map[offs + 2] = map[offs + 3] = (byte)
          (((b & 0x80) >> 7) | ((b & 0x08) >> 2) | ((b & 0x20) >> 3) | ((b & 0x02) << 2));
        map[offs + 4] = map[offs + 5] = map[offs + 6] = map[offs + 7] = (byte)
          (((b & 0x40) >> 6) | ((b & 0x04) >> 1) | ((b & 0x10) >> 2) | ((b & 0x01) << 3));
        break;

      case 1:
        map[offs]     = map[offs + 1] = (byte)(((b & 0x80) >> 7) | ((b & 0x08) >> 2));
        map[offs + 2] = map[offs + 3] = (byte)(((b & 0x40) >> 6) | ((b & 0x04) >> 1));
        map[offs + 4] = map[offs + 5] = (byte)(((b & 0x20) >> 5) | (b & 0x02));
        map[offs + 6] = map[offs + 7] = (byte)(((b & 0x10) >> 4) | ((b & 0x01) << 1));
        break;

      case 2:
        map[offs++] = (byte)((b & 0x80) >> 7);
        map[offs++] = (byte)((b & 0x40) >> 6);
        map[offs++] = (byte)((b & 0x20) >> 5);
        map[offs++] = (byte)((b & 0x10) >> 4);
        map[offs++] = (byte)((b & 0x08) >> 3);
        map[offs++] = (byte)((b & 0x04) >> 2);
        map[offs++] = (byte)((b & 0x02) >> 1);
        map[offs]   = (byte)(b & 0x01);
        break;

      case 3:
        map[offs] = map[offs + 1] = map[offs + 2] = map[offs + 3] = (byte)
          (((b & 0x80) >> 7) | ((b & 0x08) >> 2) );
        map[offs + 4] = map[offs + 5] = map[offs + 6] = map[offs + 7] = (byte)
          (((b & 0x40) >> 6) | ((b & 0x04) >> 1) );
        break;
    }
  }

  public GateArray(CPC cpc) {
    super("Amstrad Gate Array");

    setCycleFrequency(1000000);  // 1MHz
    this.cpc = cpc;
    z80 = cpc.z80;
    crtc = cpc.crtc;
    reset();
		setHalfSize(false);
  }

  public void setHalfSize(boolean value) {
    if (halfSize != value || defRenderer == null) {
      halfSize = value;
      defRenderer = halfSize ? (Renderer)new HalfRenderer() : new FullRenderer();
      startRenderer = halfSize ? (Renderer)new HalfStartRenderer() : new FullStartRenderer();
      endRenderer = halfSize ? (Renderer)new HalfEndRenderer() : new FullEndRenderer();
      borderRenderer = new BorderRenderer(16,halfSize ? 8 : 16);
      syncRenderer = new BorderRenderer(32,halfSize ? 8 : 16);
      renderer = borderRenderer;
    }
  }

  @Override
  public void reset() {
    r52 = 0;
    setScreenMode(1);
    for (int i = 0; i < 33; i++)
      inks[i] = 0xff000000;
    inks[0x10] = 0xff808080;
  }

  public void setSelectedInk(int value) {
    selInk = (value & 0x1f) < 0x10 ? value & 0x0f : 0x10;
  }

  public int getSelectedInk(){
          return selInk;
  }
  
  public void setInk(int index, int value) {
      CPCInks[index] = value;
         CPCInksb[index] = value;  
    if      (Switches.monitormode == 1)
		  inks[index] = inkTranslateLinear[value & 0x1f];
    else if (Switches.monitormode == 2)
		  inks[index] = inkTranslateGreen[value & 0x1f];
    else if (Switches.monitormode == 3)
		  inks[index] = inkTranslateBW[value & 0x1f];
    else
                  inks[index] = inkTranslateColor[value & 0x1f];
  }

  public int getInk(int index){
      return CPCInks[index];
  }

  public int getInks(int index){
      return CPCInksb[index];
  }
  
  @Override
  public void writePort(int port, int value) {
    if ((value & 0x80) == 0) {
      if ((value & 0x40) == 0) {
        value &= 0x1f;
        selInk = value < 0x10 ? value : 0x10;	
      } else 
      {
         CPCInks[selInk] = GAInks[value & 0x1f]; 
        if      (Switches.monitormode == 1)
          inks[selInk] = inkTranslateLinear[value & 0x1f];
        else if (Switches.monitormode == 2)
          inks[selInk] = inkTranslateGreen[value & 0x1f];
        else if (Switches.monitormode == 3)
          inks[selInk] = inkTranslateBW[value & 0x1f];
        else
          inks[selInk] = inkTranslateColor[value & 0x1f];
         CPCInksb[selInk] = value & 0x01f;  
      }
		
    } 
    else 
    {
      CPCMemory cpcmemory = (CPCMemory)cpc.getMemory();
      if ((value & 0x40) == 0){
        setModeAndROMEnable(cpcmemory,value);
      
      }
      else {
        cpcmemory.setRAMBank(value);
        rambank=value;
      }
    }
  }

public int getRAMBank(){
    return rambank;
}

  public void setModeAndROMEnable(CPCMemory memory, int value) {
      returnmode = value;
    memory.setLowerEnabled((value & 0x04) == 0);
    memory.setUpperEnabled((value & 0x08) == 0);
    if ((value & 0x10) != 0) {
      r52 = 0;
      setInterruptMask(interruptMask & 0x70);
    }
    newMode = value & 0x03;
  }
  
  public int getMode(){
      return returnmode;
  }

  protected void setScreenMode(int mode) {
    screenMode = mode;
    fullMap = fullMaps[mode];
    halfMap = halfMaps[mode];
  }

  public int getScreenMode(){
      return screenMode;
  }
  
  public void setInterruptMask(int value) {
    interruptMask = value;
    if (interruptMask != 0)
      z80.setInterrupt(1);
    else
      z80.clearInterrupt(1);
  }

  @Override
  public void setInterrupt(int mask) {
    r52 &= 0x1f;
    setInterruptMask(interruptMask & 0x70);
  }

  @Override
  public void cycle() {
      // next 2 lines added
      if (hSyncCount == 3 || hSyncCount == 4 )
          modeCheck();
    if (scanStarted) {
      if (hPos < HOFFSEND && doRender)
        renderer.render();
      else {
        endRenderer.render();
        render = scanStarted = false;
      }
    }
    else if (render && hPos >= HOFFSET) {
      startRenderer.render();
      scanStarted = true;
    }

    crtc.cycle();
    if (inHSyncA) {
      hSyncCount++;
      if (hSyncCount == 2) {
        outHSync = true;
        super.hSyncStart();
      }
      if (hSyncCount == 6){
        endHSync();
      }
    }
    clock();
  }

  // TODO: WinAPE does some weird stuff here to suit Overflow Preview 3
  protected void endHSync() {
    if (outHSync)
      super.hSyncEnd();
    outHSync = false;
      modeCheck();
  }

  public void modeCheck(){
    if (screenMode != newMode)
      setScreenMode(newMode);
  }

  @Override
  public void hSyncStart() {
   // System.out.println(hSyncCount);
    hSyncCount = 0;
    inHSyncA = true;
    renderer = syncRenderer;
  }

  @Override
  public void hSync() {
    if (render = rendering && (monitorLine >= -4 && monitorLine < 268)) {
      offset = scanStart;
      scanStart += halfSize ? 384 : 768;
    }
    if (vSyncCount > 0) {
      if (--vSyncCount == 0)
        super.vSyncEnd();
    }
    scanStarted = false;
  }

  @Override
  public void hSyncEnd() {
    debug = cpc.getMode() != CPC.RUN;
    endHSync();
    if (++r52 == 52) {
      r52 = 0;
      setInterruptMask(interruptMask | 0x80);
    }
    if (vSyncInt > 0 && --vSyncInt == 0)
    {
      if (r52 >= 32)
        setInterruptMask(interruptMask | 0x80);
      r52 = 0;
    }
    renderer = vSyncCount > 0 ? syncRenderer :
      (crtc.isVDisp() && crtc.isHDisp() ? defRenderer : borderRenderer);
  }

  public void hDispEnd() {
    renderer = vSyncCount > 0 || crtc.isHSync() ? syncRenderer : borderRenderer;
  }

  public void hDispStart() {
    renderer = vSyncCount > 0 || crtc.isHSync() ? syncRenderer :
      (crtc.isVDisp() ? defRenderer : borderRenderer);
  }

  @Override
  public void vSyncStart() {
    super.vSyncStart();
    vSyncCount = 32;
    renderer = syncRenderer;
    vSyncInt = 2;
  }

  @Override
  public void vSyncEnd() {
  }

  @Override
  public void vSync(boolean interlace) {
    scanStart = offset = 0;
    cpc.vSync();
  }

  @Override
  public void vDispStart() {
  }

  public void setMemory(byte[] value) {
    memory = value;
  }

  public void setRendering(boolean value) {
    rendering = value;
    if (!rendering)
      render = scanStarted = false;
  }

  public Dimension getDisplaySize(boolean large) {
    return large ? FULL_DISPLAY_SIZE : HALF_DISPLAY_SIZE;
  }
  
  public void cursor() { }  // Not used on CPC

  protected abstract class Renderer {
    public void render() { }
  }

  protected class BorderRenderer extends Renderer {

    protected int ink;
    protected int width;

    public BorderRenderer(int ink, int width) {
      this.ink = ink;
      this.width = width;
    }

  @Override
    public void render() {
      int pix = inks[ink];
      for (int i = 0; i < width; i++)
        pixels[offset++] = pix;
    }
  }

  protected class HalfRenderer extends Renderer {

  @Override
    public void render() {
      int addr = maTranslate[crtc.getMA()] + ((crtc.getRA() & 0x07) << 11);
      int val = ((memory[addr] & 0xff) << 3) + ((memory[addr + 1] & 0xff) << 11);  // Base always even
      for (int i = 0; i < 8; i++)
        pixels[offset++] = inks[halfMap[val++]];
    }

  }

  protected class FullRenderer extends Renderer {

  @Override
    public void render() {
      int addr = maTranslate[crtc.getMA()] + ((crtc.getRA() & 0x07) << 11);
      int val = ((memory[addr] & 0xff) << 4) + ((memory[addr + 1] & 0xff) << 12);  // Base always even
      for (int i = 0; i < 16; i++)
        pixels[offset++] = inks[fullMap[val++]];
    }

  }
  
  protected class HalfStartRenderer extends Renderer {

  @Override
    public void render() {
      endPix = 8 - (((hPos - HOFFSET) >> 13) & 0x07);
      if (renderer == borderRenderer) {
        int pix = inks[16];
        for (int i = endPix; i < 8; i++)
            if (doRender)
          pixels[offset++] = pix;
      }
      else if (renderer == defRenderer) {
        int addr = maTranslate[crtc.getMA()] + ((crtc.getRA() & 0x07) << 11);
        int val = ((memory[addr] & 0xff) << 3) + ((memory[addr + 1] & 0xff) << 11) + endPix;  // Base always even
        for (int i = endPix; i < 8; i++)
          pixels[offset++] = inks[halfMap[val++]];
      }
      else {
        int pix = inks[32];
        for (int i = endPix; i < 8; i++)
          pixels[offset++] = pix;
      }
    }
    
  }
  
  protected class HalfEndRenderer extends Renderer {

  @Override
    public void render() {
      if (renderer == borderRenderer) {
        int pix = inks[16];
        for (int i = 0; i < endPix; i++)
            if (doRender)
          pixels[offset++] = pix;
      }
      else if (renderer == defRenderer) {
        int addr = maTranslate[crtc.getMA()] + ((crtc.getRA() & 0x07) << 11);
        int val = ((memory[addr] & 0xff) << 3) + ((memory[addr + 1] & 0xff) << 11);  // Base always even
        for (int i = 0; i < endPix; i++)
          pixels[offset++] = inks[halfMap[val++]];
      }
      else {
        int pix = inks[32];
        for (int i = 0; i < endPix; i++)
          pixels[offset++] = pix;
      }
    }
    
  }
  
  protected class FullStartRenderer extends Renderer {

  @Override
    public void render() {
      endPix = 16 - (((hPos - HOFFSET) >> 12) & 0x0f);
      if (renderer == borderRenderer) {
        int pix = inks[16];
        for (int i = endPix; i < 16; i++)
            if (doRender)
          pixels[offset++] = pix;
      }
      else if (renderer == defRenderer) {
        int addr = maTranslate[crtc.getMA()] + ((crtc.getRA() & 0x07) << 11);
        int val = ((memory[addr] & 0xff) << 4) + ((memory[addr + 1] & 0xff) << 12) + endPix;  // Base always even
        for (int i = endPix; i < 16; i++)
          pixels[offset++] = inks[fullMap[val++]];
      }
      else {
        int pix = inks[32];
        for (int i = endPix; i < 16; i++)
          pixels[offset++] = pix;
      }
    }
    
  }

  protected class FullEndRenderer extends Renderer {

  @Override
    public void render() {
      if (renderer == borderRenderer) {
        int pix = inks[16];
        for (int i = 0; i < endPix; i++)
            if (doRender)
          pixels[offset++] = pix;
      }
      else if (renderer == defRenderer) {
        int addr = maTranslate[crtc.getMA()] + ((crtc.getRA() & 0x07) << 11);
        int val = ((memory[addr] & 0xff) << 4) + ((memory[addr + 1] & 0xff) << 12);  // Base always even
        for (int i = 0; i < endPix; i++)
          pixels[offset++] = inks[fullMap[val++]];
      }
      else {
        int pix = inks[32];
        for (int i = 0; i < endPix; i++)
          pixels[offset++] = pix;
      }
    }
    
  }

  public void resetInks(){
      for (int i = 0; i < 17; i++)
          setInk(i, getInks(i));
  }

  public void init(){
      crtc.init();
  }

  public int PEEK(int address){
      return cpc.PEEK(address);
  }
  public void POKE(int address, int value){
      CPC.POKE(address, value);
  }

}