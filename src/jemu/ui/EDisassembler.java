package jemu.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.border.BevelBorder;

import jemu.core.device.Computer;
import jemu.util.diss.Disassembler;

/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class EDisassembler extends JComponent implements AdjustmentListener, TimerListener {

  protected static final Color selBackground = new Color(0,0,200);
  protected static final Color selBreakPoint = new Color(200,0,0);
  protected static final Color selForeground = new Color(0xffffff);
  protected static final Color pcBackground  = new Color(0x00007f);
  protected static final Color pcBreakPoint  = new Color(0x7f0000);
  protected static final Color pcForeground  = Color.white;
  protected String Breaker = "";
  protected JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
  protected int address = 0;
  protected int maxAddress = 0xffff;
  protected int pc = 0;
  protected Computer computer;
  protected int lineHeight = 0;
  protected int[] addresses = new int[0];
  protected int selAnchor = 0;
  protected int selStart = 0;
  protected int selEnd = 0;
  protected int timerY = 0;
  protected Counter counter = null;
  protected boolean inSet = false;

  public EDisassembler() {
    enableEvents(MouseEvent.MOUSE_EVENT_MASK | MouseEvent.MOUSE_MOTION_EVENT_MASK);
    setBackground(Color.white);
    setForeground(Color.black);
    setBorder(new BevelBorder(BevelBorder.LOWERED));
    setFont(new Font("Courier",0,12));
    setLayout(new BorderLayout());
    scrollBar.addAdjustmentListener(this);
    scrollBar.setBlockIncrement(0x10);
    add(scrollBar,BorderLayout.EAST);
    setFocusable(true);
    setDoubleBuffered(true);
  }

  public void setComputer(Computer value) {
    computer = value;
    scrollBar.setMinimum(0);
    scrollBar.setMaximum(0x10010);
    scrollBar.setVisibleAmount(0x10);
    maxAddress = computer.getMemory().getAddressSize() - 1;
    repaint();
  }

  public Dimension getPreferredSize() {
    return new Dimension(200,200);
  }
  protected void paintComponent(Graphics g) {
    Insets insets = getInsets();
    Rectangle rect = new Rectangle(insets.left + 1,insets.top + 1,getWidth() - scrollBar.getWidth() -
      insets.left - insets.right - 2,getHeight() - insets.top - insets.bottom - 2);
    g.setColor(Color.black);
    g.drawRect(rect.x - 1,rect.y - 1,rect.width + 1,rect.height + 1);
    g.setColor(getBackground());
    g.fillRect(rect.x,rect.y,rect.width,rect.height);
    g.setFont(getFont());
    FontMetrics fm = g.getFontMetrics();
    if (computer != null) {
      Disassembler diss = computer.getDisassembler();
      if (diss != null) {
        int a = fm.getAscent();
        int[] addr = new int[] { address };
        int h = lineHeight = fm.getHeight();
        addresses = new int[(rect.height  + h - 1) / h];
        int n = 0;
        for (int y = rect.y; y < rect.y + rect.height; y += fm.getHeight()) {
              Breaker = "";
          addresses[n++] = addr[0];
          if (addr[0] == pc) {
            g.setColor(pcBackground);
            g.fillRect(0,y,rect.width,fm.getHeight());
            g.setColor(pcForeground == null ? getForeground() : pcForeground);
          }
          else if (addr[0] >= selStart && addr[0] <= selEnd) {
            g.setColor(selBackground);
            g.fillRect(0,y,rect.width,fm.getHeight());
            g.setColor(selForeground == null ? getForeground() : selForeground);
          }
          if (JEMU.debugger.isBreakpoint(addr[0])) {
              Breaker = "[Breakpoint]";
            g.setColor(selBreakPoint);
            g.fillRect(0,y,rect.width,fm.getHeight());
            g.setColor(selForeground == null ? getForeground() : selForeground);
          }
          else
            g.setColor(getForeground());
          String line = diss.disassemble(computer.getMemory(),addr,true,30,50);
              line = line + Breaker;
          g.drawString(line,rect.x,y + a);
        }
        scrollBar.setVisibleAmount(rect.height / h);
      }
    }
  }

  public void setPC(int value) {
    setAddress(pc = value,true);
  }
  
  public void setAddress(int value, boolean scroll) {
    address = Math.min(0xffff,value);
    repaint();
    if (scroll) {
      inSet = true;
      scrollBar.setValue(address);
      inSet = false;
    }
  }

  public void setAddress(int value) {
    setAddress(value,true);
  }

  public int getAddress(int y) {
    Insets insets = getInsets();
    y = (y - insets.top + 1) / lineHeight;
    return y < 0 || y >= addresses.length ? -1 : addresses[y];
  }

  public void adjustmentValueChanged(AdjustmentEvent e) {
    if (!inSet) {
      // Bloody Swing Still Sux (AdjustmentEvent is ALWAYS TRACK)
      switch(e.getValue() - address) {
        case 1 /*AdjustmentEvent.UNIT_INCREMENT*/: nextAddress(); break;
        case -1 /*AdjustmentEvent.UNIT_DECREMENT*/: prevAddress(); break;
        case 0x10 /*AdjustmentEvent.BLOCK_INCREMENT*/:
          for (int i = Math.max(1,addresses.length - 1); i > 0; i--)
            nextAddress();
          break;
        case -0x10 /*AdjustmentEvent.BLOCK_DECREMENT*/:
          for (int i = Math.max(1,addresses.length - 1); i > 0; i--)
            prevAddress();
          break;
        default: /*case AdjustmentEvent.TRACK:*/ setAddress(scrollBar.getValue()); break;
      }
      scrollBar.setValue(address);
    }
  }

  protected void nextAddress() {
    Disassembler diss = computer.getDisassembler();
    diss.disassemble(computer.getMemory(),addresses,false,0,0);
    setAddress(addresses[0],false);
  }

  protected void prevAddress() {
    int end = addresses[0];
    int addr = (end - 6) & 0xffff;
    int result;
    Disassembler diss = computer.getDisassembler();
    do {
      result = addr;
      addresses[0] = addr;
      diss.disassemble(computer.getMemory(),addresses,false,0,0);
      addr = addresses[0];
      if (addr != end) addr = (result + 1) & 0xffff;
    } while (addr != end);
    setAddress(addresses[0] = result,false);
  }
  
  protected void setSelection(int addr, boolean range) {
    if (!range)
      selAnchor = selStart = selEnd = addr;
    else if (addr < selAnchor) {
      selStart = addr;
      selEnd = selAnchor;
    }
    else {
      selStart = selAnchor;
      selEnd = addr;
    }
  }
  
  protected void startTimer(int y) {
    if (counter == null)
      counter = new Counter(this, 50, null);
    timerY = y;
  }
  
  protected void stopTimer() {
    if (counter != null) {
      counter.stop();
      counter = null;
    }
  }
  
  public void timerTick(Counter counter) {
    if (this.counter == counter) {
      if (timerY < 0) {
        prevAddress();
        setSelection(address, true);
      }
      else {
        nextAddress();
        setSelection(addresses[addresses.length - 1], true);
      }
      scrollBar.setValue(address);
      repaint();
    }
  }
  
  protected void processMouseEvent(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) {
      if (e.getID() == MouseEvent.MOUSE_PRESSED) {
        setSelection(getAddress(e.getY()), (e.getModifiers() & MouseEvent.SHIFT_MASK) != 0);
        repaint();
      }
      else if (e.getID() == MouseEvent.MOUSE_RELEASED)
        stopTimer();
    }
    super.processMouseEvent(e);
  }

  protected void processMouseMotionEvent(MouseEvent e) {
    if (e.getID() == MouseEvent.MOUSE_DRAGGED && (e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
      Dimension size = getSize();
      int y = e.getY();
      if (y >= size.height) {
        setSelection(addresses[addresses.length - 1], true);
        startTimer(y);
      }
      else if (y < 0) {
        setSelection(addresses[0], true);
        startTimer(y);
      }
      else {
        setSelection(getAddress(e.getY()), true);
        stopTimer();
      }
      repaint();
    }
    super.processMouseMotionEvent(e);
  }

  //@Override
  protected void processKeyEvent(KeyEvent e) {
    if (e.getID() == KeyEvent.KEY_PRESSED) {
      boolean shift = (e.getModifiers() & KeyEvent.SHIFT_MASK) != 0;
      switch(e.getKeyCode()) {
        case KeyEvent.VK_HOME:  setSelection(0, shift); break;
        case KeyEvent.VK_END:   setSelection(maxAddress - 1, shift); break;
        case KeyEvent.VK_UP:    setSelection(address - 1, shift); break;
        case KeyEvent.VK_DOWN:  setSelection(address + 1, shift); break;
        case KeyEvent.VK_PAGE_DOWN: setSelection(address + 10, shift); break;
        case KeyEvent.VK_PAGE_UP: setSelection(address - 10, shift); break;
      }
    }
    super.processKeyEvent(e);
  }
  
}