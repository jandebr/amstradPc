/*
 * M6526.java
 *
 * Created on 24 February 2007, 09:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jemu.core.device.io;

import jemu.core.*;
import jemu.core.device.*;

/**
 *
 * @author Richard
 */
public class M6526 extends Device {
  
  public static final int PORT_A = 0;
  public static final int PORT_B = 1;
  
  public static final int INT_TIMERA = 0x01;
  public static final int INT_TIMERB = 0x02;
  public static final int INT_ALARM  = 0x04;
  public static final int INT_SERIAL = 0x08;
  public static final int INT_FLAG   = 0x10;
  public static final int INT_ANY    = 0x80;
  public static final int INT_SET    = 0x80;
  
  public static final int CR_START   = 0x01;
  public static final int CR_PBON    = 0x02;
  public static final int CR_OUTMODE = 0x04;
  public static final int CR_RUNMODE = 0x08;
  public static final int CR_LOAD    = 0x10;
  public static final int CRA_INMODE  = 0x20;
  public static final int CRA_SPMODE  = 0x40;
  public static final int CRA_TODIN   = 0x80;
  public static final int CRB_INMODE  = 0x60;
  public static final int CRB_ALARM   = 0x80;
  
  protected IOPort[] ports = new IOPort[] { new IOPort(IOPort.READ), new IOPort(IOPort.READ) };
  protected boolean enableA, enableB;
  protected int latchA, latchB;
  protected int countA, countB;
  protected int tod, alarm, latchT;
  protected boolean todStop;
  protected int sdr;
  protected int icrMask;
  protected int icr;
  protected int cra, crb;
  protected int todTick;    // Count 5 or 6 for tod update
  
  protected Device interruptDevice;
  protected int interruptMask;
  
  /** Creates a new instance of M6526 */
  public M6526() {
    super("MOS 6526 CIA");
    reset();
  }
  
  public IOPort getPort(int port) {
    return ports[port];
  }
  
  public void reset() {
    ports[PORT_A].setPortMode(IOPort.READ);
    ports[PORT_B].setPortMode(IOPort.READ);
    // Set Interrupt Enable off
  }
  
  public void setInterruptDevice(Device device, int mask) {
    interruptDevice = device;
    interruptMask = mask;
  }
  
  public int readPort(int port) {
    // RS0 .. RS3 in port
    //System.out.println("CIA Read: " + Util.hex((byte)port));
    switch(port & 0x0f) {
      case 0x00: return ports[PORT_A].read();
      
      case 0x01: return ports[PORT_B].read();
      
      case 0x02: return ports[PORT_A].getPortMode();
      
      case 0x03: return ports[PORT_B].getPortMode();  // May have bit 7,6 forced as output
      
      case 0x04: return countA & 0xff;
      
      case 0x05: return countA >> 8;
      
      case 0x06: return countB & 0xff;
      
      case 0x07: return countB >> 8;
      
      case 0x08: int result = latchT == -1 ? tod & 0xff : latchT & 0xff; latchT = -1; return result;  // Tenths
      
      case 0x09: return ((latchT == -1 ? tod : latchT) >> 8) & 0xff;            // Seconds
      
      case 0x0a: return ((latchT == -1 ? tod : latchT) >> 16) & 0xff;          // Minutes
      
      case 0x0b: return ((latchT == -1 ? latchT = tod : latchT) >> 24) & 0xff; // Hours
      
      case 0x0c: return sdr;    // Serial Data
      
      case 0x0d: {
        result = icr;
        icr = 0x00;
        if (interruptDevice != null)
          interruptDevice.clearInterrupt(interruptMask);
        return result;
      }
      
      case 0x0e: return cra;
      
      default:   return crb;      
    }
  }
  
  public void writePort(int port, int value) {
    // RS0 .. RS3 in port
    //System.out.println("CIA Write: " + Util.hex((byte)port) + "=" + Util.hex((byte)value));
    switch(port & 0x0f) {
      case 0x00: ports[PORT_A].write(value); break;
      
      case 0x01: ports[PORT_B].write(value); break;
      
      case 0x02: ports[PORT_A].setPortMode(value); break;
      
      case 0x03: ports[PORT_B].setPortMode(value); break;
      
      case 0x04: latchA = latchA & 0xff00 | value; break;
      
      case 0x05: latchA = latchA & 0xff | (value << 8); break;
      
      case 0x06: latchB = latchB & 0xff00 | value; break;
      
      case 0x07: latchB = latchB & 0xff | (value << 8); break;
      
      case 0x08: {  // Milliseconds
        if ((crb & CRB_ALARM) != 0) alarm = alarm & 0x9f7f7f00 | (value & 0x0f);
        else {
          tod = tod & 0x9f7f7f00 | (value & 0x0f);
          todStop = false;
        }
        checkAlarm();
        break;
      }
      
      case 0x09: { // Seconds
        if ((crb & CRB_ALARM) != 0) alarm = alarm & 0x9f7f000f | ((value * 0x7f) << 8);
        else tod = tod & 0x9f7f000f | ((value & 0x7f) << 8);
        checkAlarm();
        break;
      }
      
      case 0x0a: { // Minutes
        if ((crb & CRB_ALARM) != 0) alarm = alarm & 0x9f007f0f | ((value & 0x7f) << 16);
        else tod = tod & 0x9f007f0f | ((value & 0x7f) << 8);
        checkAlarm();
        break;
      }
      
      case 0x0b: { // Hours
        if ((crb & CRB_ALARM) != 0) {
          alarm = alarm & 0x007f7f0f | ((value & 0x9f) << 24);
          checkAlarm();
        }
        else {
          tod = tod & 0x007f7f0f | ((((value & 0x1f) == 0x12 ? value ^ 0x80 : value) & 0x9f) << 24);
          todStop = true;
        }
        break;
      }
      
      case 0x0c: sdr = value; break;
      
      case 0x0d: {
        if ((value & INT_SET) != 0) {
          icrMask |= value & 0x1f;
          setInterrupt(icr & 0x1f);            // Allow unsent ints
        }
        else {
          icrMask &= ~value;
          // TODO: Does this clear the -INT line ???
          // if ((icr & icrMask & 0x1f) == 0) interruptDevice.clearInterrupt(interruptMask);
        }
        break;
      }
      
      case 0x0e: {
        if ((value & CR_LOAD) != 0) {
          countA = latchA;
          cra = value & ~CR_LOAD;
        }
        else
          cra = value;
        enableA = (cra & (CR_START | CRA_INMODE)) == CR_START;
        break;
      }
      
      default:   {
        crb = value & ~CR_LOAD;
        if ((value & CR_LOAD) != 0) {
          countB = latchB;
          crb = value & ~CR_LOAD;
        }
        else
          crb = value;
        enableB = (crb & (CR_START | CRB_INMODE)) == CR_START;
      }
      
    }
  }
  
  protected void checkAlarm() {
    if (tod == alarm && !todStop)
      setInterrupt(INT_ALARM);
  }
  
  public final void setInterrupt(int value) {
    if ((icr & INT_ANY) == 0 && (icrMask & value) != 0) {
      icr |= value | INT_ANY;
      if (interruptDevice != null)
        interruptDevice.setInterrupt(interruptMask);
    }
    else
      icr |= value;
  }
  
  public final void cycle() {
    // Count down timers
    if (enableA) {
      if (countA == 0) {
        setInterrupt(INT_TIMERA);
        countA = latchA;
        if ((cra & CR_RUNMODE) != 0) {  // One-Shot
          cra &= ~CR_START;
          enableA = false;
        }
      }
      else
        countA--;
    }
    if (enableB) {
      if (countB == 0) {
        setInterrupt(INT_TIMERB);
        countB = latchB;
        if ((crb & CR_RUNMODE) != 0) {
          crb &= ~CR_START;
          enableB = false;
        }
      }
      else
        countB--;
    }
  }
  
  protected static final int LSEC_INC = 0x00000100 - 0x00000009;
  protected static final int MSEC_INC = 0x00001000 - 0x00000909;
  protected static final int LMIN_INC = 0x00010000 - 0x00005909;
  protected static final int MMIN_INC = 0x00100000 - 0x00095909;
  protected static final int LHR_INC  = 0x01000000 - 0x00595909;
  protected static final int MHR_INC  = 0x10000000 - 0x09595909;
  protected static final int HDAY_INC = 0x81000000 - 0x12595909;
  
  public final void todCycle() {
    // Update Time of day
    if (!todStop) {
      if (todTick == 0) {
        todTick = (cra & CRA_TODIN) == 0 ? 6 : 5;
        // TODO: What happens if BCD value set is not valid??
        if ((tod & 0x0000000f) == 0x9) {
          if ((tod & 0x00000f00) == 0x900) {                // Seconds LSB
            if ((tod & 0x00007000) == 0x5000) {             // Seconds MSB
              if ((tod & 0x000f0000) == 0x90000) {          // Minutes LSB
                if ((tod & 0x00700000) == 0x500000) {       // Minutes MSB
                  switch(tod & 0x1f000000) {                // Hours
                    case 0x12000000: tod += HDAY_INC; break;
                    case 0x09000000: tod += MHR_INC;  break;
                    default:         tod += LHR_INC;  break;
                  }
                }
                else
                  tod += MMIN_INC;
              }
              else
                tod += LMIN_INC;
            }
            else
              tod += MSEC_INC;
          }
          else
            tod += LSEC_INC;
        }
        else
          tod++;
        checkAlarm();
      }
      else
        todTick--;
    }
  }
  
  public void setCountA(int value) {
    countA = value & 0xffff;
  }
  
  public void setLatchA(int value) {
    latchA = value & 0xffff;
  }
  
  public void setCountB(int value) {
    countB = value & 0xffff;
  }
  
  public void setLatchB(int value) {
    latchB = value & 0xffff;
  }
  
  protected int getTime(byte hours, byte minutes, byte seconds, byte tenths) {
    return ((hours & 0x9f) << 24) | ((minutes & 0x7f) << 16) |
      ((seconds & 0x7f) << 8) | (tenths & 0x0f);
  }
  
  public void setTimeOfDay(byte hours, byte minutes, byte seconds, byte tenths) {
    tod = getTime(hours, minutes, seconds, tenths);
  }
  
  public void setTimeOfDayTicks(int value) {
    todTick = value & 0xff;
  }
  
  public void setAlarm(byte hours, byte minutes, byte seconds, byte tenths) {
    alarm = getTime(hours, minutes, seconds, tenths);
  }
  
  public void setTimeLatch(byte hours, byte minutes, byte seconds, byte tenths) {
    latchT = getTime(hours, minutes, seconds, tenths);
  }
  
  public void clearTimeLatch() {
    latchT = -1;
  }

  public void setEnabledInterrupts(int value) {
    icrMask = value & 0xff;
  }
  
  public void setActiveInterrupts(int value) {
    icr = value & 0xff;
  }
  
  public void setCRA(int value) {
    cra = value & 0xff;
  }
  
  public void setCRB(int value) {
    crb = value & 0xff;
  }
  
  public void setTimeOfDayStopped(boolean value) {
    todStop = value;
  }
  
}