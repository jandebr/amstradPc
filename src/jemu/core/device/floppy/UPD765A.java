/*
 * UPD765A.java
 *
 * Created on 09 March 2007, 11:17
 * CMD_PARAMS_SEEK has been removed for let demo 'Midline Process' work
 * Only CMD_PARAMS are used.
 * Please regard lines 90-95 & 292-294
 */

package jemu.core.device.floppy;

import jemu.core.samples.Samples;
import jemu.core.*;
import jemu.core.device.*;
import jemu.ui.Switches;
import java.net.URL;
import java.applet.*;
import jemu.ui.JEMU;

/**
 *
 * @author Richard, Markus
 */
public class UPD765A extends Device {
  public static virtualDrive floppy = new virtualDrive();


  protected int counter;
  protected boolean readDel = false;

  /** DEBUG on/off. */
  private static final boolean DEBUG         = false;

  /** DEBUG sense drive on/off. */
  private static final boolean DEBUG_SENSE   = false;
  
  /** DEBUG show read buffer. */
  private static final boolean DEBUG_BUFFER   = false;

  /** DEBUG read id on/off. */
  private static final boolean DEBUG_READ_ID = false;
  protected boolean readtrack = false;



  /** Sector sizes. */
  private static final int[]   SECTOR_SIZES  = { 0x80, 0x100, 0x200,  0x400, 0x800, 0x1000, 0x1800 };

  /**
   * @param commandSize command size
   * @return real size
   */
  public static int getSectorSize(int commandSize) {
    return SECTOR_SIZES[Math.min(commandSize, 6)];
  }

  /**
   * @param realSize sector size
   * @return NEC 765 command sector size (0-5)
   */
  public static int getCommandSize(int realSize) {
    for (int i = 0; i < SECTOR_SIZES.length; i++) {
      if (SECTOR_SIZES[i] == realSize) {
        return i;
      }
    }
    // default size
    return 2;
  }

  /**
   * For efficiency, the cycles can be 1, 2, 4 or 8 per cycle call, allowing an 8MHz clock frequency to be emulated with
   * only one call to cycle() at 1MHz, or as used in the CPC, 4MHz with clocksPerCycle = 4, called at 1MHz.
   */

  // The following values are from the documentation for 8MHz operation

  public int formatid[] = {0,0,0,0};
  protected int                actualDrive      = 0;
 /* protected static final int   READ_TIME_FM     = 27;
  protected static final int   READ_TIME_MFM    = 13;
  protected static final int   POLL_TIME        = 1024;*/

  protected static final int READ_TIME_FM  = 24 * 8;
  protected static final int READ_TIME_MFM = 16 * 8;
  protected static final int POLL_TIME     = 1024 * 8;

  protected static final int   POLL             = 0;
  protected static final int   SEEK             = 1;
  protected static final int   READ_ID          = 2;
  protected static final int   MATCH_SECTOR     = 3;
  protected static final int   READ             = 4;
  protected static final int   WRITE            = 5;
  protected static final int   FORMAT           = 6;
  protected static final int[] CMD_PARAMS = {
    0, 0, 8, 2, 1, 8, 8, 1, 0, 8, 1, 0, 8, 5, 0, 2,
    0, 8, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 8, 0, 0
  };

  // Valid commands during a seek???
 /* protected static final int[] CMD_PARAMS_SEEK = {
    0, 0, 0, 2, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
  };*/

  // Main Status Register bits
  protected static final int   D0_BUSY          = 0x01;      // Drive 0 in Seek Mode
  protected static final int   D1_BUSY          = 0x02;
  protected static final int   D2_BUSY          = 0x04;
  protected static final int   D3_BUSY          = 0x08;
  protected static final int   COMMAND_BUSY     = 0x10;      // Command Busy
  protected static final int   EXEC_MODE        = 0x20;      // Execution Phase in non-DMA mode
  protected static final int   DATA_IN_OUT      = 0x40;      // Data Input/Output
  protected static final int   REQ_MASTER       = 0x80;      // Request for Master

  //protected static final int   SEEK_MASK        = 0x0f;

  protected static final int   ST0_NORMAL       = 0x00;
  protected static final int   ST0_ABNORMAL     = 0x40;
  protected static final int   ST0_INVALID      = 0x80;
  protected static final int   ST0_READY_CHANGE = 0xc0;
  protected static final int   ST0_NOT_READY    = 0x08;
  protected static final int   ST0_HEAD_ADDR    = 0x04;
  protected static final int   ST0_EQUIP_CHECK  = 0x10;
  protected static final int   ST0_SEEK_END     = 0x20;

  protected static final int   ST1_MISSING_ADDR = 0x01;
  protected static final int   ST1_NOT_WRITABLE = 0x02;
  protected static final int   ST1_NO_DATA      = 0x04;
  protected static final int   ST1_OVERRUN      = 0x10;
  protected static final int   ST1_DATA_ERROR   = 0x20;
  protected static final int   ST1_END_CYL      = 0x80;

  protected static final int   ST2_MISSING_ADDR = 0x01;
  protected static final int   ST2_BAD_CYLINDER = 0x02;
  protected static final int   ST2_SCAN_NOT_SAT = 0x04;
  protected static final int   ST2_SCAN_EQU_HIT = 0x08;
  protected static final int   ST2_WRONG_CYL    = 0x10;
  protected static final int   ST2_DATA_ERROR   = 0x20;
  protected static final int   ST2_CONTROL_MARK = 0x40;      // Deleted data address mark

  protected static final int   ST3_HEAD_ADDR    = 0x04;
  protected static final int   ST3_TWO_SIDE     = 0x08;
  protected static final int   ST3_TRACK_0      = 0x10;
  protected static final int   ST3_READY        = 0x20;
  protected static final int   ST3_WRITE_PROT   = 0x40;
  protected static final int   ST3_FAULT        = 0x80;

  protected int                command;                      // Current command
  protected int                action;                       // Current action
  protected int[]              params           = new int[8]; // Command parameters
  protected int                pcount           = 0;
  protected int                pindex           = 0;
  protected int[]              result           = new int[7]; // Command Result
  protected int                rindex           = 0;
  protected int                rcount           = 0;
  protected Drive              activeDrive;
  protected int                c, h, r, n;                   // Sector ID
  protected int                offset;                       // Offset in sector
  protected int                size;                         // Size of sector
  protected byte[]             buffer;                       // Sector Buffer
  protected int                count;                        // Total cycle count
  protected int                next;                         // Cycle count for next event
  protected int                status;                       // Main Status Register
  protected int                st1;                          // Status Register 1
  protected int                st2;                          // Status Register 2
  protected int                st3;                          // Status Register 3
  protected byte                data;                         // Current data byte
protected int 			sectorcount;				// sector count for format
  protected int                cycleRate;                    // Number of cycles per cycle() call, should be 1, 2, 4 or
  // 8
  protected int                countPoll, countStep, countFM, countMFM;

  protected Drive[]            drives           = new Drive[4];
  protected int[]              pcn              = new int[4];
  protected int[]              ncn              = new int[4];
  protected int[]              dir              = new int[4];
  protected int[]              max              = new int[4];
  protected int[]              st0              = new int[4];          // Separate Status register 0 for each drive
  protected boolean[]          ready            = new boolean[4];

  protected Device             interruptDevice;
  protected int                interruptMask;
  private boolean              driveChanged;

  /**
   * Creates a new instance of UPD765A
   *
   * @param clocksPerCycle clocks per cycle (1,2,4,8)
   */
  public UPD765A(int clocksPerCycle) {
    super("NEC uPD765AC-2 Floppy Controller");
    switch(clocksPerCycle) {
      case 1:
      case 2:
      case 4:
      case 8: cycleRate = clocksPerCycle; break;
      default: cycleRate = 4; break;
    }
     floppy.setTitle("NEC uPD765AC-2");
     floppy.setUndecorated(true);
     floppy.setAlwaysOnTop(true);
 /*   countPoll = POLL_TIME * cycleRate;
    countFM   = READ_TIME_FM * cycleRate;
    countMFM  = READ_TIME_MFM * cycleRate;*/

    countPoll = POLL_TIME / cycleRate;
    countFM   = READ_TIME_FM / cycleRate;
    countMFM  = READ_TIME_MFM / cycleRate;
    reset();
  }

  public void setInterruptDevice(Device device, int mask) {
    interruptDevice = device;
    interruptMask = mask;
  }

  /**
   * Set or reset a drive.
   *
   * @param index drive index
   * @param drive the drive or <code>null</code> to reset
   */
  public void setDrive(int index, Drive drive) {
    drives[index] = drive;
    driveChanged = true;
  }

  /**
   * Return indexed drive.
   *
   * @param index drive index
   * @return drive or <code>null</code> when not set
   */
  public Drive getDrive(int index) {
    return drives[index];
  }

  @Override
  public final int readPort(int port) {   // Port 0 is main status, 1 is data
    if ((port & 0x01) == 0) {
       // if (DEBUG)
      //System.out.println("FDC Status Read: " + Util.hex((byte)status));
      return status;
    }

    else {
      if ((status & (EXEC_MODE | REQ_MASTER)) == REQ_MASTER && rcount > 0) {
        data = (byte)result[rindex++];
        if (--rcount == 0)
          status &= ~(COMMAND_BUSY | DATA_IN_OUT);
      }
      else if (action == READ && (status & REQ_MASTER) != 0) {
        status &= ~REQ_MASTER;
        //System.out.println("FDC Data read: " + Util.hex((byte)data));
        //System.out.print(" " + Util.hex((byte)data));
      }
      return data;
    }
  }

  @Override
  public final void writePort(int port, int value) {  // Port 0 is main status, 1 is data
    if ((port & 0x01) != 0) {
      data = (byte)value;
      //System.out.println("FDC write: " + Util.hex((short)port) + "," + Util.hex((byte)value));
      if ((status & (REQ_MASTER | DATA_IN_OUT)) == REQ_MASTER) {
        if ((status & EXEC_MODE) != 0)
          status &= ~REQ_MASTER;
        else if ((status & COMMAND_BUSY) == 0) {
          // Can start a command
          command = value;
          value &= 0x1f;
          status |= COMMAND_BUSY;
          pindex = 0;
         // if ((pcount = (status & SEEK_MASK) == 0 ? CMD_PARAMS[value] : CMD_PARAMS_SEEK[value]) == 0) {
          if ((pcount = CMD_PARAMS[value]) == 0){
            // Invalid command or Sense Interrupt status
            status |= DATA_IN_OUT;
            result[rindex = 0] = ST0_INVALID;
            rcount = 1;
            if (value == 0x08) {  // Sense Interrupt Status
              for (int drive = 0; drive < 4; drive++)
                if (st0[drive] != ST0_INVALID) {
                  result[0] = st0[drive];
                  st0[drive] = ST0_INVALID;
                  result[1] = pcn[drive];
                  status &= ~(1 << drive);
                  rcount = 2;
                  if (DEBUG_SENSE)
                  System.out.println("Sense Interrupt: " + Util.hex((byte)result[0]) + "," +  Util.hex((byte)result[1]));
                  break;
                }
            }
          }
        }
        else {
          params[pindex++] = value;
          if (--pcount == 0) {
              if (command == 0x66) command = 0x66;
             String msg = "FDC Command " + Util.hex((byte) command) + ": " + Util.hex((byte) params[1])
             + "/" + Util.hex((byte) params[2]) + "/" + Util.hex((byte) params[3]) + "/"
             + Util.hex((byte) params[4]) + " ";
            switch(command & 0x1f) {
            case 0x02:
              if (DEBUG) {
                System.out.println(msg + " (read track)");
              }
              readTrack();
              break;
            case 0x03:
              // specify
              if (DEBUG) {
                System.out.println(msg + " (specify)");
              }
              specify();
              break;
            case 0x04:
              // sense actualDrive
              if (DEBUG) {
                System.out.println(msg + " (senseDrive)");
              }
              senseDrive();
              break;
            case 0x05:
              // write sector
              if (DEBUG) {
                System.out.println(msg + " (writeSector)");
              }
              writeSector();
              break;
            case 0x06:
              // read sector
              if (DEBUG) {
                System.out.println(msg + " (readSector)");
              }
                readDel = false;
              readSector();
              break;
            case 0x07:
              // re-calibrate
              if (DEBUG) {
                System.out.println(msg + " (re-calibrate)");
              }
              seek(0, 77);
              break;
            case 0x08:
              // get status register 0
              if (DEBUG) {
                System.out.println(msg + " (get status register 0)");
              }
              break;
            case 0x09:
              // TODO: write deleted data
              if (DEBUG) {
                System.out.println(msg + " (write deleted data)");
              }
              //writeSector();
              break;
            case 0x0a:
              // read id
              if (DEBUG) {
                System.out.println(msg + " (read id)");
              }
              readID();
              break;
            case 0x0c:
              // TODO: read deleted data
              if (DEBUG) {
                System.out.println(msg + " (read deleted data)");
              }
              readDel = true;
              readSector();
              break;
            case 0x0d:
              // TODO: format track
              if (DEBUG) {
                System.out.println(msg + " (format track)");
              }
		sectorcount = 0;
              formatTrack();

              break;
            case 0x0f:
              // seek track
              if (DEBUG) {
                System.out.println(msg + " (seek track)");
              }
              seek(params[1], -1);
              break;
            case 0x11:
            case 0x19:
            case 0x1d:
              // verify
              if (DEBUG) {
                System.out.println(msg + " (verify)");
              }
              break;

              default: throw new RuntimeException("Invalid command: " + Util.hex((byte)command));
            }
          }
        }
      }
    }
  }

  @Override
  public final void reset() {
    Samples.SEEK.stop();
    Samples.SEEKBACK.stop();
    pindex = pcount = count = 0;
    status = REQ_MASTER;
    countStep = 16 * 8000 / cycleRate;
    action = POLL;
    next = countPoll;
  }
  public final void resetb() {
    pindex = pcount = count = 0;
    status = REQ_MASTER;
    action = POLL;
  }

  public final void initialise(){
    pindex = pcount = count = 0;
    action = POLL;
  }

  public final void specify() {
    countStep = (16 - (params[0] >> 4)) * 8000 / cycleRate;
    status &= ~(COMMAND_BUSY | DATA_IN_OUT);
  }

  public final void senseDrive() {
    int select = params[0] & 0x07;
    int drv = select & 0x03;
    if (DEBUG_SENSE) {
      System.out.println("senseDrive(" + Integer.toString(select, 2) + ") " + activeDrive.getName() + " / "
          + activeDrive.getType());
    }
    activeDrive = drives[drv];
    if (activeDrive != null) {
      if (ready[drv] && !driveChanged) {
        select |= ST3_READY;
      }
      if (activeDrive.getCylinder() == 0) {
        select |= ST3_TRACK_0;
      }
      if (activeDrive.getSides() == 2) {
        select |= ST3_TWO_SIDE;
      }
      if (activeDrive.isWriteProtected()) {
        select |= ST3_WRITE_PROT;
      }
    }
    driveChanged = false;
    result[rindex = 0] = select;
    rcount = 1;
    status |= DATA_IN_OUT;
    if (DEBUG_SENSE) {
      System.out.println(" -> " + Integer.toString(result[0], 2));
    }
  }

  public final void seek(int cyl, int steps) {
    if (Switches.FloppySound && Switches.audioenabler == 1)
    Samples.MOTOR.loop2();
    actualDrive = params[0] & 0x03;
        jemu.ui.Display.drive = ""+actualDrive;
    Switches.drive = actualDrive;
    max[actualDrive] = steps;
    ncn[actualDrive] = cyl;
    status &= ~COMMAND_BUSY;
    if (pcn[actualDrive] == cyl) {
      seekEnd(actualDrive, ST0_NORMAL);
    } else {
        status |= (1 << actualDrive);
        if (pcn[actualDrive] < cyl) {
            dir[actualDrive] = 1;
            if (Switches.FloppySound && Switches.audioenabler == 1)
                Samples.TRACK.play();
        } else if (pcn[actualDrive] > cyl) {
            if (Switches.FloppySound && Switches.audioenabler == 1)
                Samples.TRACKBACK.play();
            dir[actualDrive] = -1;
        }
        if (action != SEEK) {
            action = SEEK;
            next = count + countStep;   // TODO: Real FDC might use counters for each drive
        }
    }
  }
  
  protected final void seekStep() {
    for (int drive = 0; drive < 4; drive++) {
      if (pcn[drive] != ncn[drive]) {
        int step = dir[drive];
        pcn[drive] += step;
        if (drives[drive] != null)
          if (drives[drive].step(step)) pcn[drive] = 0;
        if (pcn[drive] == ncn[drive]){
          seekEnd(drive, ST0_NORMAL);
        }
        else if (--max[drive] == 0) { // Recal 77 steps complete
          ncn[drive] = pcn[drive];
          seekEnd(drive, ST0_ABNORMAL | ST0_EQUIP_CHECK);
        } else {
            next = count + countStep;
            String track;// = Util.hex(params[1]).substring(6);
            if (pcn[drive] <=9)
                track = "0";
            else
                track = "";
            jemu.ui.Display.track = track+pcn[drive];
            floppy.trackpos = pcn[drive];
            jemu.ui.Display.sector = Util.hex(params[3]).substring(6);
            jemu.ui.Display.drive = ""+actualDrive;
            if (activeDrive != null)
                activeDrive.setActive(false);
            if (Switches.FloppySound && Switches.audioenabler == 1){
                if (step >0)
                    Samples.SEEK.loop2();
                else
                    Samples.SEEKBACK.loop2();
            }
        }
      }
    }
  }

  protected final void seekEnd(int drive, int status) {
    st0[drive] = status | ST0_SEEK_END | drive;
    dir[drive] = 0;
    Samples.SEEK.stop();
    Samples.SEEKBACK.stop();
    // Set interrupt. If no drives still seeking
    if ((dir[0] | dir[1] | dir[2] | dir[3]) == 0) {
      action = POLL;
      next = count + countPoll;
        if (activeDrive != null)
            activeDrive.setActive(true);
    }
  }

  public final void poll() {
    Switches.write = false;
    actualDrive = params[0] & 0x03;
        jemu.ui.Display.drive = ""+actualDrive;
    Switches.drive = actualDrive;
    for (int drive = 0; drive < 4; drive++) {
      boolean rdy = drives[drive] != null && drives[drive].isReady();
      if (rdy != ready[drive]) {
        ready[drive] = rdy;
        st0[drive] = ST0_READY_CHANGE | (rdy ? 0 : ST0_NOT_READY) | drive;
      }
    }
  }

  protected final boolean setupResult() {
    int select = params[0] & 0x07;
    driveChanged = false;
    activeDrive = drives[select & 0x03];
    result[rindex = 0] = select | ST0_ABNORMAL;
    result[1] = ST1_NO_DATA | ST1_MISSING_ADDR;
    rcount = 2;
    if (activeDrive != null && activeDrive.isReady()) {
      activeDrive.setHead(select >> 2);
      activeDrive.setActive(true);
      return true;
    }
    result[0] |= ST0_NOT_READY;
    status |= DATA_IN_OUT;
    return false;
  }

  protected final void readID() {
    Switches.write = false;
    if (setupResult()) {
      action = READ_ID;
      status ^= REQ_MASTER | DATA_IN_OUT;
     // next = count + (countPoll);
      next = count + (1200 / cycleRate);  // TODO: Accurate timing!
    }
  }

  protected final void getNextID() {
      if (DEBUG)
          System.out.println("this is getNextID");

    int[] id = activeDrive.getNextSectorID();
    if (id != null) {
      result[0] &= ~ST0_ABNORMAL; // status 0
      result[1] = result[2] = 0; // status 1 + 2
      result[3] = id[0]; // track
      result[4] = id[1]; // head
      result[5] = id[2]; // sector id
      result[6] = id[3]; // sector size
      rcount = 7;
    }
    else
      activeDrive.setActive(false);
    status |= REQ_MASTER;
    action = POLL;
    next = count + countPoll;
  }

  protected final void readSectorByte() {
    if (offset == buffer.length)
     endBuffer(READ);
    else {
      data = buffer[offset++];
      //System.out.print("(" + Util.hex((byte)data) + ")");
      next = count + countMFM;
      status |= REQ_MASTER;
    }
  }

  protected final void readSector() {
    if (setupResult()){
      getNextSector(READ);
    }
  }
/*  protected final void readDeletedData() {
// TODO: Read deleted data
      readDel = true;
      readSector();
  }*/

  protected final void readTrack() {
    if (setupResult()) {
        readtrack = true;
      activeDrive.resetSector();
      getNextSector(READ);
    }
  }


  protected final void formatTrack() {

if (setupResult()) {
	getNextFormatID();
    }
  }


  protected void addSector(){

  }

  protected final void getNextFormatID()
  {
        action = FORMAT;
		offset = 0;
		status = (status & ~DATA_IN_OUT) | REQ_MASTER | EXEC_MODE;  // ??? Is RQM high immediately?
	      Switches.write = true;
		next = count + (countPoll);
		data = -1;
  }

protected final void endFormatID() {
    if (sectorcount == params[3]) {
      status &= ~EXEC_MODE;
      status |= REQ_MASTER | DATA_IN_OUT;
      result[0] &= ~ST0_ABNORMAL;
      result[1] = result[2] = 0;
      result[3] = params[1];
      result[4] = params[2];
      result[5] = 0x01;
      result[6] = params[4];
      rcount = 7;
      action = POLL;
      next = count + countPoll;
      activeDrive.setActive(false);
    }
    else {
	sectorcount = (sectorcount+1) & 0x0ff;
      getNextFormatID();
    }
  }


  protected final void getNextSector(int direction) {
      if (DEBUG) {
          System.out.println("This is getNextSector");
          System.out.println(params[1] + params[2]+ params[3]+ params[4]);
      }
   //   if (!readtrack)
    buffer = activeDrive.getSector(params[1], params[2], params[3], params[4]);
      //else
    if(readtrack){
          buffer = null;
    System.out.println("Buffer cleared...");
    }
      readtrack = false;
    if (DEBUG_BUFFER){
        System.out.println("Buffer is:");
        System.out.println(Util.dumpBytes(buffer));
    }
    if (DEBUG)
    System.out.println("Got sector " + Util.hex((byte)params[3]) + " " + (buffer != null) + " " +
      (buffer == null ? "" : Integer.toString(buffer.length)));
    if (buffer != null) {
        //String sector = Util.hex(params[3]).substring(6);
        String track;// = Util.hex(params[1]).substring(6);
        if (params[1] <=9)
            track = "0";
        else
            track = "";
       // JEMU.menue7.setLabel("  T:" + track+params[1] + " | S:" + sector + " DF" + actualDrive);
        jemu.ui.Display.track = track+params[1];
        floppy.trackpos = params[1];
        jemu.ui.Display.sector = Util.hex(params[3]).substring(6);
        jemu.ui.Display.drive = ""+actualDrive;
      offset = 0;
      action = direction;  // TODO: Accurate matching/timing
      if (direction == READ){
          if (DEBUG)
              System.out.println("status:"+status+" REQ_MASTER:"+REQ_MASTER+" DATA_IN_OUT:"+DATA_IN_OUT+" EXEC_MODE:"+EXEC_MODE);
          status = (status & ~REQ_MASTER) | DATA_IN_OUT | EXEC_MODE;
        Switches.write = false;
      }
      else{
        status = (status & ~DATA_IN_OUT) | REQ_MASTER | EXEC_MODE;  // ??? Is RQM high immediately?
        Switches.write = true;
      }
      next = count + (countPoll);
       // next = count + (800 / cycleRate);
      data = -1;
    }
    else {
      status |= DATA_IN_OUT;
      action = POLL;
      next = count + countPoll;
      activeDrive.setActive(false);
    }
  }

  protected final void endBuffer(int direction) {
      int[] id = activeDrive.getReadID();
      if (params[3] == params[5]) {
          status &= ~EXEC_MODE;
          status |= REQ_MASTER | DATA_IN_OUT;
          result[0] &= ~ST0_ABNORMAL; // status 0
          result[1] = result[2] = 0; // status 1 + 2
          result[3] = params[1] = id[0]; // track
          result[4] = params[2] = id[1]; // head
          result[5] = id[2]; // sector id
          result[6] = params[4] = id[3]; // sector size
          rcount = 7;
          action = POLL;
          next = count + countPoll;
          activeDrive.setActive(false);
      }
      else {
          params[3] = (params[3] + 1) & 0xff;
          getNextSector(direction);
      }
  }


  protected final void writeSector() {
    if (setupResult()){
      getNextSector(WRITE);
    }
  }
  protected final void writeDeletedData() {
    if (setupResult()){
      getNextSector(WRITE);
    }
  }

  protected final void writeSectorByte() {
    if (data == -1) ; // TODO: Overrun error, no data supplied yet
    buffer[offset++] = (byte)data;
    data = -1;
    if (offset == buffer.length){
      endBuffer(WRITE);
      saveCheck();
    }
    else {
      next = count + countMFM;
      status |= REQ_MASTER;
    }

  }

  protected final void writeFormatByte() {
    formatid[offset++] = (byte)data;
    data = -1;
    if (offset == 4){
        endFormatID();
        saveCheck();
    }
    else {
      next = count + countMFM;
      status |= REQ_MASTER;
    }
  }

  public final void saveCheck(){
      switch (actualDrive) {
          case 0:
              jemu.system.cpc.CPC.df0mod = true; break;
          case 1:
              jemu.system.cpc.CPC.df1mod = true; break;
          case 2:
              jemu.system.cpc.CPC.df2mod = true; break;
          case 3:
              jemu.system.cpc.CPC.df3mod = true; break;
      }
      if (Switches.autosave)
        jemu.system.cpc.CPC.savetimer = 1;
  }
int oldtrack;
  @Override
  public final void cycle() {
      oldtrack = params[1];
      counter++;
      if (counter == 50000){
      floppy.update();
      counter= 0;
      }
    if (++count == next) {
              if (Switches.floppyturbo)
              jemu.ui.Display.turbotimer = 10;
      // System.out.println("FDC Count ended");
      switch (action) {
      case SEEK:
        seekStep();
        break;
      case POLL:
        poll();
        break;
      case READ_ID:
        getNextID();
        break;
      case READ:
        readSectorByte();
        break;
      case WRITE:
        writeSectorByte();
        break;
      case FORMAT:
        writeFormatByte();
        break;
       //default: setResult(action < 0 ? -action : 0,IRQ); break; // Ok
        default:
            break;
      }
    }
  }

  /**
   * Set forced head to use for given drive.
   *
   * @param head head 0/1
   * @param drive set head for drive 0/1
   */
  public void setForcedHead(int head, int drive) {
    if (drives[drive] != null) {
      drives[drive].setForcedHead(head);
      driveChanged = true;
    }
  }

  /**
   * Return forced head.
   *
   * @param Drive set head for Drive 0/1
   * @return 0/1
   */
  public int getForcedHead(int drive) {
    if (drives[drive] != null) {
      return drives[drive].getForcedHead();
    }
    return 0;
  }

}