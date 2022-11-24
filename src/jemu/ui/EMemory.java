package jemu.ui;

import java.awt.*;
import java.awt.event.*;

import jemu.core.*;
import jemu.core.device.*;
import jemu.core.device.memory.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class EMemory extends JComponent implements TimerListener {

  protected static final int INVALID = 0;
  protected static final int HEX1    = 1;   // First char of hex
  protected static final int HEX2    = 2;
  protected static final int TEXT    = 3;

  protected Memory mem;
  protected Color selBackground = new Color(0,127,0);
  protected Color selForeground = Color.white;
  protected boolean textMode = false;
  protected boolean right = false;
  protected int selAnchor, selStart, selEnd, selected;
  protected int addressDigits;
  protected int cw, ch;
  protected int left;
  protected Counter counter;
  protected boolean cursor;

  public EMemory() {
    enableEvents(MouseEvent.MOUSE_EVENT_MASK | MouseEvent.MOUSE_MOTION_EVENT_MASK |
      FocusEvent.FOCUS_EVENT_MASK);
    setBackground(Color.white);
    setLayout(new BorderLayout());
    setFont(new Font("Courier",0,12));
    setAutoscrolls(true);
    setFocusable(true);
    setDoubleBuffered(true);
  }

  public void setMemory(Memory value) {
    mem = value;
    addressDigits = Math.max(4,Integer.toHexString(mem.getAddressSize() - 1).length());
  }

  public void setComputer(Computer value) {
    setMemory(value == null ? null : value.getMemory());
  }

  protected void paintComponent(Graphics g) {
    byte[] buff = new byte[16];
    g.setColor(getBackground());
    g.fillRect(0,0,getWidth(),getHeight());
    if (mem != null) {
      g.setFont(getFont());
      FontMetrics fm = g.getFontMetrics();
      g.setColor(getForeground());
      int a = fm.getAscent();
      ch = fm.getHeight();
      Rectangle rect = g.getClipBounds();
      Insets insets = getInsets();
      int row = (rect.y - insets.top) / ch;
      int y = insets.top + row * ch;
      int address = row * 0x10;
      for (; y < rect.y + rect.height; y += fm.getHeight()) {
        String line;
        if (addressDigits > 4) {
          line = Util.hex(address);
          if (addressDigits < 8)
            line = line.substring(8 - addressDigits);
        }
        else
          line = Util.hex((short)address);
        line += ": ";
        int w = fm.stringWidth(line);
        cw = w / line.length();
        left = w += insets.left;
        g.setColor(getForeground());
        int ya = y + a;
        g.drawString(line, insets.left, ya);
        for (int i = 0; i < 16; i++)
          buff[i] = (byte)mem.readByte(address + i,null);
        line = Util.dumpBytes(buff, 0, 16, false, true, false);       
        int start = 0;
        if (selStart < address + 16 && selEnd >= address) {
          if (right) {
            int index = (selected - address) * 3 + 1;
            line = line.substring(0, index - 1) + line.charAt(index) + ' ' + line.substring(index + 1);
          }
          else {
            int end;
            if (textMode) {
              int textStart = line.length() - 16;
              start = Math.max(textStart,textStart + selStart - address);
              g.drawString(line.substring(0,start),w,ya);
              w += fm.stringWidth(line.substring(0,start));
              end = Math.min(line.length(),textStart + selEnd - address + 1);
            }
            else {
              start = Math.max(0,(selStart - address) * 3);            
              if (start > 0) {
                g.drawString(line.substring(0,start),w,ya);
                w += fm.stringWidth(line.substring(0,start));
              }
              end = Math.min(47, (selEnd - address + 1) * 3 - 1);
            }
            int ww = fm.stringWidth(line.substring(start, end));
            g.setColor(selBackground);
            g.fillRect(w,y,ww,ch);
            g.setColor(selForeground);
            g.drawString(line.substring(start,end),w,ya);
            w += ww;
            start = end;
            g.setColor(getForeground());
          }
        }
        if (start != line.length())
          g.drawString(line.substring(start),w,ya);
        address += 16;
        if (address >= mem.getAddressSize()) break;
      }
      if (cursor) {
        Rectangle cursor = getRect(selected);
        if (right)
          cursor.x += cw;
        g.setXORMode(Color.white);
        g.fillRect(cursor.x, cursor.y, 2, cursor.height);
      }
    }
  }

  //@Override
public Dimension getPreferredSize() {
    FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
    return new Dimension(fm.charWidth('0') * (66 + addressDigits),
      fm.getHeight() * (mem == null ? 0x1000 : mem.getAddressSize() >> 4));
  }
  
  protected Point getCharacterCell(MouseEvent e) {
    Insets insets = getInsets();
    return new Point(e.getX() < left ? -1 : (e.getX() - left) / cw,(e.getY() - insets.top) / ch);
  }
  
  protected Rectangle getRect(int addr) {
    int y = (addr >> 4) * ch + getInsets().top;
    int x = addr & 0x0f;
    x = textMode ? left + (x + 48) * cw : left + x * cw * 3;
    return new Rectangle(x,y,textMode ? cw : cw * 2,ch);
  }
  
  protected int getClickSection(Point ch) {
    int result = INVALID;
    if (ch.x >= 48) {
      ch.x -= 48;
      result = TEXT;
    }
    else if (ch.x >= 0) {
      switch (ch.x % 3) {
        case 0: result = HEX1; break;
        case 1: result = HEX2; break;
      }
      ch.x /= 3;
    }
    ch.x = Math.max(0,Math.min(ch.x,15));
    return result;
  }
  
  //@Override
  protected void processMouseEvent(MouseEvent e) {
    if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
      if (e.getID() == MouseEvent.MOUSE_PRESSED) {
        requestFocus();
        Point p = getCharacterCell(e);
        int section = getClickSection(p);
        setTextMode(section == TEXT);
        setSelection((p.y << 4) + p.x, false);
      }
    }
    super.processMouseEvent(e);
  }

  //@Override
  protected void processMouseMotionEvent(MouseEvent e) {
    if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0 && e.getID() == MouseEvent.MOUSE_DRAGGED) {
      Point p = getCharacterCell(e);
      int section = getClickSection(p);
      if (textMode && section != TEXT) p.x = 0;
      else if (!textMode && section == TEXT) p.x = 15;
      int addr = (p.y << 4) + p.x;
      setSelection(addr, true);
    }
    super.processMouseMotionEvent(e);
  }
  
  public void setTextMode(boolean value) {
    if (textMode != value) {
      textMode = value;
      right = false;
      repaint();
    }
  }
  
  public int getPageSize() {
    return Math.max(1, getSize().height / ch);
  }

  //@Override
  protected void processKeyEvent(KeyEvent e) {
    if (e.getID() == KeyEvent.KEY_PRESSED) {
      boolean shift = (e.getModifiers() & KeyEvent.SHIFT_MASK) != 0;
      switch(e.getKeyCode()) {
        case KeyEvent.VK_HOME:  setSelection(0, shift); break;
        case KeyEvent.VK_END:   setSelection(mem.getAddressSize() - 1, shift); break;
        case KeyEvent.VK_LEFT:  setSelection(selected - 1, shift); break;
        case KeyEvent.VK_RIGHT: setSelection(selected + 1, shift); break;
        case KeyEvent.VK_UP:    if (selected > 15) setSelection(selected - 16, shift); break;
        case KeyEvent.VK_DOWN:  if (selected < mem.getAddressSize() - 16) setSelection(selected + 16, shift); break;
        case KeyEvent.VK_PAGE_DOWN: setSelection(selected + getPageSize(), shift); break;
        case KeyEvent.VK_PAGE_UP: setSelection(selected - getPageSize(), shift); break;
        case KeyEvent.VK_TAB:   if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) setTextMode(!textMode); break;
      }
    }
    else if (e.getID() == KeyEvent.KEY_TYPED) {
      char ch = e.getKeyChar();
      repaint(getRect(selected));
      if (textMode) {
        if (ch >= ' ' && ch < (char)127) ;
        mem.writeByte(selected, (int)ch);
        selStart--;
        setAddress(Math.min(mem.getAddressSize() - 1, selected + 1));
      }
      else {
        int hex = "0123456789ABCDEFabcdef".indexOf(ch);
        if (hex != -1) {
          if (hex > 15) hex -= 6;
          if (right = !right)
            mem.writeByte(selected, hex);
          else {
            mem.writeByte(selected, mem.readByte(selected) << 4 | hex);
            selStart--;
            setAddress(Math.min(mem.getAddressSize() - 1, selected + 1));
          }
        }
      }
      repaint(getRect(selected));
    }
    cursor = counter != null;
    super.processKeyEvent(e);
  }
  
  protected void setSelection(int addr) {
    setSelection(addr, true);
  }
  
  protected void setSelection(int addr, boolean range) {
    int start, end;
    selected = addr = Math.max(0, Math.min(mem.getAddressSize() - 1, addr));
    if (!range)
      start = end = selAnchor = addr;
    else if (addr < selAnchor) {
      start = addr;
      end = selAnchor;
    }
    else {
      end = addr;
      start = selAnchor;
    }
    if (right || start != selStart || end != selEnd) {
      right = false;
      selStart = start;
      selEnd = end;
      scrollRectToVisible(getRect(addr));
      repaint();
    }
  }
  
  public void setAddress(int addr) {
    setSelection(addr, false);
  }

  //@Override
  protected void processFocusEvent(FocusEvent e) {
    if (e.getID() == FocusEvent.FOCUS_GAINED) {
      if (counter == null)
        counter = new Counter(this, 500, null);
      cursor = true;
    }
    else if (e.getID() == FocusEvent.FOCUS_LOST && counter != null) {
      if (right) {
        right = false;
        repaint(getRect(selected));
      }
      counter.stop();
      timerTick(counter = null);
    }
    super.processFocusEvent(e);
  }

  public void timerTick(Counter counter) {
    cursor = !cursor && counter != null;
    repaint(getRect(selected));
  }
  
}