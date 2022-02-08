package jemu.system.cpc;

import java.awt.event.*;
import jemu.core.*;
import jemu.core.device.keyboard.*;

/**
* Title:        JavaCPC
* Description:  The Java CPC Emulator
* Copyright:    Copyright (c) 2002-2008
* Company:
* @author:      Markus, Richard (JEMU)
*/

public abstract class Keyboard extends MatrixKeyboard {

  protected boolean     DEBUG   = false;
  protected int[]       bytes   = new int[16];
  protected int         row     = 0;

  public Keyboard() {
    super("CPC Keyboard",8,10);
    for (int i = 0; i < bytes.length; i++)
      bytes[i] = 0xff;
    setKeyMappings();
    reset();
  }
  
  protected abstract void setKeyMappings();

  protected void keyChanged(int col, int row, int oldValue, int newValue) {
    if (oldValue == 0) {
      if (newValue != 0)
        bytes[row] &= (0x01 << col) ^ 0xff;
    }
    else if (newValue == 0)
      bytes[row] |= (0x01 << col);
  }

  public void setSelectedRow(int value) {
    row = value;
  }

  public int readSelectedRow() {
      if (DEBUG){
      if (bytes[row]!=255)
	System.out.println("Row: "+row+" Data: "+bytes[row]);
      }
    return bytes[row];
  }
}
