package jemu.core.device.sound;

import jemu.core.device.*;
import jemu.ui.Switches;
import jemu.ui.Display;
import jemu.ui.AdvancedOptions;
/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class AY_3_8910 extends SoundDevice {

public final double[] LINEAR_VOLUME = {
    0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
};
    //
    // AY Amplitude values taken from CPCe95
    // and re-calculated for JavaCPC
    //
    // To calculate:
    // FOR n=0 TO 15: PRINT 15*(2^((1+n)/2))/256: NEXT n

public final double[] LOG_VOLUME = {
    0.08286408  ,0.1171875  ,0.1657282  ,0.234375,
    0.3314563   ,0.46875    ,0.6629126  ,0.9375,
    1.325825    ,1.875      ,2.65165    ,3.75,
    5.303301    ,7.5       ,10.6066    ,15
};

    //
    // AY Amplitude values taken from Ay_Emul
    // and re-calculated for JavaCPC (divided with 4369)
    //

    // { (c)Hacker KAY }
    //  Amplitudes_AY:array[0..15]of Word=
    //   (0, 836, 1212, 1773, 2619, 3875, 5397, 8823, 10392, 16706, 23339,
    //   29292, 36969, 46421, 55195, 65535);
    public final double[] LOG_VOLUME_A = {
        0 , 0.117418174 , 0.189517052 , 0.283588922 , 0.440146487 ,
        0.741130694 , 1.12748913 , 2.08514534 , 2.36758984 , 4.09155413 ,
        5.64934768 , 6.96772717 , 8.8908217 , 10.8194095 , 12.9095903 , 15
    };

    //{ (c)V_Soft }
    // Amplitudes_AY:array[0..15]of Word=
    //   (0, 513, 828, 1239, 1923, 3238, 4926, 9110, 10344, 17876, 24682,
    //   30442, 38844, 47270, 56402, 65535);}
    public final double[] LOG_VOLUME_B = {
        0 , 0.191348135 , 0.277409018 , 0.405813687 , 0.599450675 ,
        0.886930648 , 1.23529412 , 2.01945525 , 2.37857633 , 3.8237583 ,
        5.34195468 , 6.70450904 , 8.46166171 , 10.6250858 , 12.6333257 , 15
    };


protected int vuL, vuR;
    protected int leftchange, rightchange;
    public boolean register13Updated = false;
  public static boolean     digiblast = false;
  public static int         leftChannel, rightChannel, blasterA, blasterB;
  protected int             volumen     = 0;
  int                       volcount, vucount;
  protected int             digibuffer  = 20000;
  public static int         digicount   = 0;
  public  final int   BDIR_MASK   = 0x04;
  public  final int   BC2_MASK    = 0x02;
  public  final int   BC1_MASK    = 0x01;
  public  final int   PORT_A      = 0;
  public  final int   PORT_B      = 1;

  // Possible states
  protected static final int INACTIVE = 0;
  protected static final int LATCH    = 1;
  protected static final int READ     = 2;
  protected static final int WRITE    = 3;

  protected static final int[] STATES = {
     INACTIVE, LATCH, INACTIVE, READ, LATCH, INACTIVE, WRITE, LATCH
     
  };
  
  // Registers
  protected static final int AFINE       = 0;
  protected static final int ACOARSE     = 1;
  protected static final int BFINE       = 2;
  protected static final int BCOARSE     = 3;
  protected static final int CFINE       = 4;
  protected static final int CCOARSE     = 5;
  protected static final int NOISEPERIOD = 6;
  protected static final int ENABLE      = 7;
  protected static final int AVOL        = 8;
  protected static final int BVOL        = 9;
  protected static final int CVOL        = 10;
  protected static final int EFINE       = 11;
  protected static final int ECOARSE     = 12;
  protected static final int ESHAPE      = 13;
  protected static final int REG_PORTA   = 14;
  protected static final int REG_PORTB   = 15;
  
  // Bits of ENABLE register
  protected static final int ENABLE_A    = 0x01;
  protected static final int ENABLE_B    = 0x02;
  protected static final int ENABLE_C    = 0x04;
  protected static final int NOISE_A     = 0x08;
  protected static final int NOISE_B     = 0x10;
  protected static final int NOISE_C     = 0x20;
  protected static final int PORT_A_OUT  = 0x40;
  protected static final int PORT_B_OUT  = 0x80;
  
  protected static final int NOISE_ALL = NOISE_A | NOISE_B | NOISE_C;
  
  // Sound Channels (inc Noise and Envelope)
  protected static final int A        = 0;
  protected static final int B        = 1;
  protected static final int C        = 2;
  protected static final int NOISE    = 3;
  protected static final int ENVELOPE = 4;

  protected int step = 0x8000;
  protected int[] regs = new int[16];
  protected int selReg = 0;
  protected int bdirBC2BC1 = 0;
  protected int state = INACTIVE;
  protected int clockSpeed = 1000000;
  protected IOPort[] ports = new IOPort[] {
    new IOPort(IOPort.READ), new IOPort(IOPort.READ)
  };
  protected int[] envelope = new int[3];  // Channels A, B and C
  protected int[] output   = new int[4];  // A, B, C and Noise
  protected int[] count    = new int[5];  // A, B, C, Noise and Envelope counters
  protected int[] period   = new int[5];  // A, B, C, Noise and Envelope
  protected int[] volume   = new int[5];  // A, B, C, Noise and Envelope (Vol[3] not used)
  protected int outN, random = 1;
  protected int countEnv, hold, alternate, attack, holding;
  
  protected int updateStep;

  public AY_3_8910() {
    super("AY-3-8910/2/3 Programmable Sound Generator");
    setClockSpeed(clockSpeed);
    player = SoundUtil.getSoundPlayer(200,true);
    player.setFormat(SoundUtil.UPCM8);
  }

  public void setClockSpeed(int value) {
    clockSpeed = value;
    updateStep = (int)(((long)step * 8L * (long)JavaSound.SAMPLE_RATE) / (long)clockSpeed);
    //updateStep = 5780; //(int)(((long)step * 8L * (long)audio.getSampleRate()) / (long)clockSpeed); //
    output[NOISE] = 0xff;
    for (int i = A; i <= ENVELOPE; i++)
        period[i] = count[i] = updateStep;
    period[ENVELOPE] = 0;
    count[NOISE] = 0x7fff;
  }

  public void changeClockSpeed(int value) {
    clockSpeed = value;
    updateStep = (int)(((long)step * 8L * (long)JavaSound.SAMPLE_RATE) / (long)clockSpeed);
  }

  public void setSelectedRegister(int value) {
    selReg = value & 0x0f;
  }

  public int getSelectedRegister(){
      return selReg;
  }
  
  public void setBDIR_BC2_BC1(int value, int dataValue) {
    if (bdirBC2BC1 != value) {
      bdirBC2BC1 = value;
      state = STATES[bdirBC2BC1];
      writePort(0,dataValue);
    }
  }
  public int getBDIR_BC2_BC1(){
      return bdirBC2BC1; 
  }

  public int readPort(int port) {
    if (selReg != 0)
        return state == READ ? readRegister(selReg) : 0xff;
    return 0xff;
  }

  public void writePort(int port, int value) {
        switch(state) {
            case LATCH: selReg = value & 0x0f;             break;
            case WRITE: setRegister(selReg,value);         break;    
        }
  }
    
    public int getRegister(int index) {
    return regs[index];
  }

  public int readRegister(int index) {
    return index < REG_PORTA ? regs[index] : ports[index - REG_PORTA].read();
  }

  public boolean registerUpdated(){
      return register13Updated;
  }

  public void resetUpdated(){
      register13Updated = false;
  }

  public void setRegister(int index, int value) {
      if (index == 13){
          register13Updated = true;
      }

      if (index < REG_PORTA) {
        if (index == ESHAPE || regs[index] != value) {
          regs[index] = value;
          switch(index) {
            case ACOARSE:
            case BCOARSE:
            case CCOARSE:
            case AFINE:
            case BFINE:
            case CFINE: {
              index >>= 1;
              int val = (((regs[(index << 1) + 1] &0x0f) << 8) | regs[index << 1]) * updateStep;
              int last = period[index];
              period[index] = val = val < 0x8000 ? 0x8000 : val;
              int newCount = count[index] - (val - last);
              count[index] = newCount < 1 ? 1 : newCount;
              break;
            }
            
            case NOISEPERIOD: {
              int val  = 2*((value & 0x1f) * updateStep);
              int last = period[NOISE];
              period[NOISE] = val = val == 0 ? updateStep : val;
              int newCount = count[NOISE] - (val - last);
              count[NOISE] = newCount < 1 ? 1 : newCount;
              break;
            }
            
            case ENABLE: break;
            
            case AVOL:
            case BVOL:
            case CVOL: {
                volume[index - AVOL] = (value & 0x10) == 0 ? value & 0x0f : volume[ENVELOPE];
              break;
            }
            
            case EFINE:
            case ECOARSE: {
              int val = (((regs[ECOARSE] << 8) | regs[EFINE]) * updateStep) << 1;
              int last = period[ENVELOPE];
              period[ENVELOPE] = val;
              int newCount = count[ENVELOPE] - (val - last);
              count[ENVELOPE] = newCount < 1 ? 1 : newCount;
              break;
            }
            
            case ESHAPE: {
              attack = (value & 0x04) == 0 ? 0 : 0x0f;
              if ((value & 0x08) == 0) {
                hold = 1;
                alternate = attack;
              }
              else {
                hold = value & 0x01;
                alternate = value & 0x02;
              }
              count[ENVELOPE] = period[ENVELOPE];
              countEnv = 0x0f;
              holding = 0;
              int vol = volume[ENVELOPE] = attack ^ 0x0f;
              if ((regs[AVOL] & 0x10) != 0) volume[A] = vol;
              if ((regs[BVOL] & 0x10) != 0) volume[B] = vol;
              if ((regs[CVOL] & 0x10) != 0) volume[C] = vol;
              break;
            }
          }
        }
      }
      else
          ports[index - REG_PORTA].write(value);
  }
  
  public void writeAudio() {
    int enable = regs[ENABLE];
    if ((enable & ENABLE_A) != 0) {
      if (count[A] <= step) count[A] += step;
      output[A] = 1;
    }
    if ((enable & ENABLE_B) != 0) {
      if (count[B] <= step) count[B] += step;
      output[B] = 1;
    }
    if ((enable & ENABLE_C) != 0) {
      if (count[C] <= step) count[C] += step;
      output[C] = 1;
    }
    outN = output[NOISE] | enable;
    if ((enable & NOISE_ALL) == NOISE_ALL) { // false if All disabled
      if (count[NOISE] <= step)
        count[NOISE] += step;
    }
    // output Sound bytes
    int[] cnt = new int[3];
    int left = step;
    do {
      int add = count[NOISE] < left ? count[NOISE] : left;

      for (int chan = A; chan <= C; chan++) {
        int chcnt = count[chan];
        if ((outN & (NOISE_A << chan)) != 0) {
          int val = output[chan] == 0 ? cnt[chan] : cnt[chan] + chcnt;
          if ((chcnt -= add) <= 0) {
            int p = period[chan];
            while (true) {
              if ((chcnt += p) > 0) {
                if ((output[chan] ^= 0x01) != 0)
                  val += p - chcnt;
                break;
              }
              val += p;
              if ((chcnt += p) > 0) {
                if (output[chan] == 0)
                  val -= chcnt;
                break;
              }
            }
          }
          else if (output[chan] != 0)
            val -= chcnt;
          cnt[chan] = val;
        }
        else {
          if ((chcnt -= add) <= 0) {
            int p = period[chan];
            while (true) {
              if ((chcnt += p) > 0) {
                output[chan] ^= 0x01;
                break;
              }
              if ((chcnt += p) > 0)
                break;
            }
          }
        }
        count[chan] = chcnt;
      }

      if ((count[NOISE] -= add) <= 0) {
        int val = random + 1;
        if ((val & 0x02) != 0)
          outN = (output[NOISE] ^= 0xff) | enable;
        random = (random & 0x01) == 0 ? random >> 1 : (random ^ 0x28000) >> 1;
        count[NOISE] += period[NOISE];
      }

      left -= add;
    } while (left > 0);
    
    if (holding == 0 && period[ENVELOPE] != 0) {
      if ((count[ENVELOPE] -= step) <= 0) {
        int ce = countEnv;
        int p = period[ENVELOPE];
        do {
          ce--;
        } while((count[ENVELOPE] += p) <= 0);
        
        if (ce < 0) {
          if (hold != 0) {
            if (alternate != 0)
              attack ^= 0x0f;
            holding = 1;
            ce = 0;
          }
          else {
            if (alternate != 0 && (ce & 0x10) != 0)
              attack ^= 0x0f;
            ce &= 0x0f;
          }
        }
        countEnv = ce;
        int vol = volume[ENVELOPE] = ce ^ attack;
        if ((regs[AVOL] & 0x10) != 0) volume[A] = vol;
        if ((regs[BVOL] & 0x10) != 0) volume[B] = vol;
        if ((regs[CVOL] & 0x10) != 0) volume[C] = vol;
      }
    }
    
    int a = (int)(LOG_VOLUME_A[volume[A]]) * cnt[A] >> 13;
    int b = (int)(LOG_VOLUME_A[volume[B]]) * cnt[B] >> 13;
    int c = (int)(LOG_VOLUME_A[volume[C]]) * cnt[C] >> 13;
    if (Switches.VSoftOutput){
        a = (int)(LOG_VOLUME_B[volume[A]]) * cnt[A] >> 13;
        b = (int)(LOG_VOLUME_B[volume[B]]) * cnt[B] >> 13;
        c = (int)(LOG_VOLUME_B[volume[C]]) * cnt[C] >> 13;
    }
    if (Switches.CPCE95){
        a = (int)(LOG_VOLUME[volume[A]]) * cnt[A] >> 13;
        b = (int)(LOG_VOLUME[volume[B]]) * cnt[B] >> 13;
        c = (int)(LOG_VOLUME[volume[C]]) * cnt[C] >> 13;
    }

    if (Switches.linear){
        a = (int)(LINEAR_VOLUME[volume[A]]) * cnt[A] >> 13;
        b = (int)(LINEAR_VOLUME[volume[B]]) * cnt[B] >> 13;
        c = (int)(LINEAR_VOLUME[volume[C]]) * cnt[C] >> 13;

    }
    a *= Switches.volume;
    b *= Switches.volume;
    c *= Switches.volume;
    if (Switches.ayeffect)
        b =  (int)((long)b*0.55);
    leftChannel     = (a + b);
    rightChannel    = (b + c);

    soundOutput(leftChannel, rightChannel);

  }

  public void setReadDevice(int port, Device device, int readPort) {
    ports[port].setInputDevice(device,readPort);
  }

  public void soundOutput(int left, int right){
    if(digiblast){
        leftChannel = blasterA;
        rightChannel= blasterB;
    if (Switches.audioenabler != 1)
        leftChannel = blasterA = rightChannel = blasterB = 0;
    }
    else
    {
    if (Switches.audioenabler != 1)
        leftChannel = rightChannel = 0;
    }

    if (digicount != 0)
        digicount++;
    if (digicount >= digibuffer){
        digiblast = false;
        digicount = 0;
    }
    if (digiblast)
        BvuMeter();
    else
        vuMeter(leftChannel, rightChannel);
    player.writeStereo(leftChannel , rightChannel);
  }

  public void setWriteDevice(int port, Device device, int writePort) {
    ports[port].setOutputDevice(device,writePort);
  }
  public void vuMeter(int left, int right){
    volcount++;
    if (volcount >= 0x100){
        volcount = 0;
        //left = left*3/4;
       // right = right*3/4;
        if (left > leftchange){
            leftchange = left;
        }
        else
            if (leftchange > 0){
                leftchange--;
            }
        if (right > rightchange){
            rightchange = right;
        }
        else
            if (rightchange > 0)
                rightchange--;
        AdvancedOptions.leftvumeter.setValue   (leftchange);
        AdvancedOptions.rightvumeter.setValue  (rightchange);
        Display.left = leftchange;
        Display.right = rightchange;
    }
  }

  public void BvuMeter(){
    volcount++;
    if (volcount >= 0x100){
        volcount = 0;
        if (AdvancedOptions.Options){
            int left = blasterA - 128;
            int right = blasterB - 128;
            if (left > leftchange){
                leftchange = left;
            }
            else
                if (leftchange >0)
                leftchange--;
            if (right > rightchange){
                rightchange = right;
            }
            else
                if (rightchange >0)
                rightchange--;
            AdvancedOptions.leftvumeter.setValue   (leftchange);
            AdvancedOptions.rightvumeter.setValue  (rightchange);
        }
    }
  }
}