package jemu.core.device.memory;

import jemu.core.device.Device;

/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public abstract class Memory extends Device {
  
  protected int size;

  public Memory(String type, int size) {
    super(type);
    this.size = size;
  }
  
  public int getAddressSize() {
    return size;
  }

  public int readByte(int address, Object config) {
    return readByte(address);
  }

  public void writeByte(int address, int value, Object config) {
    writeByte(address,value);
  }

}