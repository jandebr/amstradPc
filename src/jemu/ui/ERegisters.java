package jemu.ui;

import java.awt.*;
import jemu.core.*;
import jemu.core.device.*;
import javax.swing.*;

/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class ERegisters extends JPanel {

  protected Font fixed;
  
  protected Device device;

  public ERegisters() { }

  public void setDevice(Device value) {
    device = value;
    removeAll();
    setLayout(new GridBagLayout());
    setFont(getFont());
    if (device != null) {
      Register[] regs = device.getRegisters();
      int y = -1;
      int cols = 1;
      for (int i = 0; i < regs.length; i++)
        cols = Math.max(cols,regs[i].getColumn() + 1);
      for (int i = 0; i < regs.length; i++) {
        Register reg = regs[i];
        String fmt = reg.getFormat();
        int col = reg.getColumn() * 2;
        if (col == 0) y++;
        int w = 1;
        if (fmt != null) {
          JLabel label = new JLabel(fmt);
          label.setFont(fixed);
          label.setForeground(Debugger.navy);
          w = cols * 2 - 1;
          GridBagConstraints gbc = new GridBagConstraints(1,y++,w,1,0,0,GridBagConstraints.WEST,0,
            new Insets(1,4,1,1),0,0);
          add(label,gbc);
        }
        JLabel label = new JLabel(reg.getName());
        label.setForeground(Debugger.navy);
        GridBagConstraints gbc = new GridBagConstraints(col,y,1,1,0,0,GridBagConstraints.EAST,0,
          new Insets(1,1,2,1),0,0);
        add(label,gbc);
        JTextField tf = new JTextField();
        tf.setFont(fixed);
        gbc = new GridBagConstraints(col + 1,y,w,1,0,0,GridBagConstraints.WEST,0,
          new Insets(1,1,1,1),0,0);
        add(tf,gbc);
      }
      GridBagConstraints gbc = new GridBagConstraints(0,y + 1,2,1,0,1,GridBagConstraints.EAST,0,
        new Insets(0,0,0,0),0,0);
      add(new JLabel(),gbc);
    }
    setValues();
  }
  
  public void setFont(Font font) {
    super.setFont(font);
    fixed = new Font("Courier",Font.PLAIN,font.getSize());
  }
  
  public void setValues() {
    Component[] comp = getComponents();
    Register[] regs = device.getRegisters();
    int index = 0;
    for (int i = 0; i < comp.length; i++) {
      if (comp[i] instanceof JTextField) {
        Register reg = regs[index];
        String fmt = reg.getFormat();
        String str = "";
        int w = 0;
        if (fmt != null) {
          str = "0000000000000000" + Integer.toBinaryString(device.getRegisterValue(index));
          w = reg.getBits();
        }
        else {
          str = "000000000000000" + Util.hex(device.getRegisterValue(index));
          w = (reg.getBits() + 3) / 4;
        }
        ((JTextField)comp[i]).setText(str.substring(str.length() - w));
        index++;
      }
    }
  }
  
}