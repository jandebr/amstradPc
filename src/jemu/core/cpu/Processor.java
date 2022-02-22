package jemu.core.cpu;

import java.util.ArrayList;
import java.util.List;

import jemu.core.Util;
import jemu.core.device.Device;
import jemu.core.device.DeviceMapping;
import jemu.ui.Switches;

/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public abstract class Processor extends Device {

	// TODO: what is this?
    public int actualAddress;

  // Memory for processor
  protected Device memory;

  // Input Devices
  protected DeviceMapping[] inputDevice = new DeviceMapping[0];

  // Output Devices
  protected DeviceMapping[] outputDevice = new DeviceMapping[0];

  // Cycle devices
  protected Device cycleDevice = null;

  // Interrupt device
  protected Device interruptDevice = null;

  // Interrupt mask
  protected int interruptPending = 0;

  // Total number of cycles executed
  protected long cycles = 0;

  // Cycles per second of CPU
  protected long cyclesPerSecond;

  // Track value of processor program counter
  private long cyclePreviousProgramCounter = 0xfffff;

  // Processor stopped
  protected boolean stopped = false;

  public Processor(String type, long cyclesPerSecond) {
    super(type);
    this.cyclesPerSecond = cyclesPerSecond;
  }

  public final void cycle(int count) {
  	  // Perform cycles
	  for (; count > 0; count--) {
		  cycles++;
		  cycleDevice.cycle();
	  }

	  // If the program counter has changed, notify the observers
	  if (Switches.breakpoints) {
		  int cycleProgramCounter = getProgramCounter();
		  if (cycleProgramCounter != cyclePreviousProgramCounter) {
			  notifyProgramCounterObservers();
			  cyclePreviousProgramCounter = cycleProgramCounter;
		  }
	  }
  }

  public void cycle() {
	  cycle(1);
  }

  public void reset() {
    cycles = 0;
  }

  public long getCycles() {
    return cycles;
  }

  public abstract void step();

  public abstract void stepOver();

  public void run() {
    stopped = false;
    do {
      step();
    } while(!stopped);
  }

  public static long bigsteps = 0L;
  
  public void runTo(int address) {
    stopped = false;
    do {
      step();
    } while(!stopped && getProgramCounter() != address);
  }

  public synchronized void stop() {
    stopped = true;
  }

  public final int readWord(int addr) {
    return readByte(addr) + (readByte((addr + 1) & 0xffff) << 8);
  }

  public final void writeWord(int addr, int value) {
    writeByte(addr,value);
    writeByte((addr + 1) & 0xffff, value >> 8);
  }

  public int readByte(int address) {
    return memory.readByte(address);
  }

  public int writeByte(int address, int value) {
    return memory.writeByte(address,value);
  }

  public final int in(int port) {
    int result = 0xff;
    for (int i = 0; i < inputDevice.length; i++)
      result &= inputDevice[i].readPort(port);
    return result;
  }

  public final void out(int port, int value) {
    for (int i = 0; i < outputDevice.length; i++)
      outputDevice[i].writePort(port,value);
  }

  public final void setMemoryDevice(Device value) {
    memory = value;
  }

  public final Device getMemoryDevice() {
    return memory;
  }

  public final void addInputDeviceMapping(DeviceMapping value) {
    inputDevice = (DeviceMapping[])Util.arrayInsert(inputDevice,inputDevice.length,1,
      value);
  }

  public final void removeInputDeviceMapping(DeviceMapping value) {
    inputDevice = (DeviceMapping[])Util.arrayDeleteElement(inputDevice,value);
  }

  public final void addOutputDeviceMapping(DeviceMapping value) {
    outputDevice = (DeviceMapping[])Util.arrayInsert(outputDevice,outputDevice.length,1,
      value);
  }

  public final void removeOutputDeviceMapping(DeviceMapping value) {
    outputDevice = (DeviceMapping[])Util.arrayDeleteElement(outputDevice,value);
  }

  public final void setCycleDevice(Device value) {
    cycleDevice = value;
  }

  public final void setInterruptDevice(Device value) {
    interruptDevice = value;
  }

  public void setInterrupt(int mask) {
    interruptPending |= mask;
  }

  public void clearInterrupt(int mask) {
    interruptPending &= ~mask;
  }

  public abstract String getState();

  public abstract int getProgramCounter();

  public long getCyclesPerSecond() {
    return cyclesPerSecond;
  }

  public void setCyclesPerSecond(long value) {
    cyclesPerSecond = value;
  }

  	//
  	// ProgramCounterObserver MANAGEMENT
  	//

  	// List of objects observing this processor's program counter
  	private List<ProgramCounterObserver> programCounterObservers = new ArrayList<ProgramCounterObserver>();

  	/**
  	 * Add an object to the list observing this Processor's program counter.
  	 *
  	 * @param o - the object to add.
  	 */
  	public void attachProgramCounterObserver(ProgramCounterObserver o) {
  		programCounterObservers.add(o);
  	}

  	/**
  	 * Remove an object from the list observing this Processor's program counter.
  	 *
  	 * @param o - the object to remove.
  	 */
  	public void detachProgramCounterObserver(ProgramCounterObserver o) {
  		programCounterObservers.remove(o);
  	}

  	/**
  	 * Notify all objects observing this Processor's program counter that the
  	 * program counter value has changed. Observers are notified in the order
  	 * that they were attached.
  	 */
  	public void notifyProgramCounterObservers() {
  		// why -1?
        int address = getProgramCounter();
        actualAddress = address;

        // Notify all program counter observers
        if (!programCounterObservers.isEmpty()) {
        	for (ProgramCounterObserver o : programCounterObservers) {
        		o.update(address);
        	}
        }
  	}
}