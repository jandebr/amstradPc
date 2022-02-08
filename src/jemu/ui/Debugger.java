/*
 * Debugger.java
 *
 * Created on 18 January 2007, 15:07
 */

package jemu.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;

import jemu.core.Util;
import jemu.core.breakpoint.Breakpoint;
import jemu.core.breakpoint.StopBreakpoint;
import jemu.core.cpu.Processor;
import jemu.core.device.Computer;
import jemu.core.device.memory.Memory;
import jemu.util.diss.Disassembler;
import java.util.Calendar;

import jemu.settings.Settings;

/**
 * Debugger form.
 *
 * @author Richard
 * @author John Girvin
 */
public class Debugger extends JFrame implements MouseListener, ItemListener, ActionListener {
	Calendar cal = Calendar.getInstance();

	// List of breakpoints, keyed by address
	private HashMap<Integer,Breakpoint> breakpoints = new HashMap<Integer,Breakpoint>();
	
  public static final Color navy = new Color(0,0,127);
  protected Computer computer;
  protected long startCycles = 0;
  protected int breakaddress;
  protected int breakindex = 0;
  
  protected JFileChooser fileDlg;
  
  /** Creates new form Debugger. */
  public Debugger() {
    initComponents();
    bRun.setBorder(new BevelBorder(BevelBorder.RAISED));
    bRun.setBackground(new Color (70,255,70));
    bRun.setForeground(new Color (0,0,0));
    bRun.addActionListener(this);
    bStop.addActionListener(this);
    bStop.setBackground(new Color (255,70,70));
    bStop.setForeground(new Color (0,0,0));
    bStop.setBorder(new BevelBorder(BevelBorder.RAISED));
    bPoints.addActionListener(this);
    bPoints.setBackground(new Color (170,170,170));
    bPoints.setForeground(new Color (0,0,0));
    bPoints.setFocusable(false);
    bPoints.addItemListener(this);
    bPoints.setSelected(Settings.getBoolean(Settings.BREAKPOINTS, false));
    bInst.addActionListener(this);
    bInst.setBackground(new Color (170,170,170));
    bInst.setForeground(new Color (0,0,0));
    bInst.addItemListener(this);
    bInst.setSelected(Settings.getBoolean(Settings.BREAKINST, false));
    bInst.setFocusable(false);
    bStep.setBackground(new Color (255,255,70));
    bStep.setForeground(new Color (0,0,0));
    bStep.setBorder(new BevelBorder(BevelBorder.RAISED));
    bStepOver.addActionListener(this);
    bStepOver.setBackground(new Color (70,255,255));
    bStepOver.setForeground(new Color (0,0,0));
    bStepOver.setBorder(new BevelBorder(BevelBorder.RAISED));
    mSave.addActionListener(this);
    mGoto.addActionListener(this);
    mBreak.addActionListener(this);
    mRemove.addActionListener(this);
    mRemoveAll.addActionListener(this);
    jScrollPane1.getVerticalScrollBar().setUnitIncrement(getFontMetrics(eMemory.getFont()).getHeight());
    jScrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED));
  }
  
  public void setComputer(Computer value) {
    if (computer != null)
      computer.removeActionListener(this);
    computer = value;
    eDisassembler.setComputer(computer);
    eMemory.setComputer(computer);
    if (computer != null) {
      computer.addActionListener(this);
      eRegisters.setDevice(computer.getProcessor());
      updateDisplay();
    }
    else
      eRegisters.setDevice(null);
  }
  
  public void updateDisplay() {
    eDisassembler.setPC(computer.getProcessor().actualAddress);
    lCycleCount.setText(Long.toString(computer.getProcessor().getCycles() - startCycles));
    eRegisters.setValues();
    repaint();
  }
  
  /**
   * Determine if the debugger has set a breakpoint at an address
   * 
   * @param address - the address to check
   * 
   * @return true if the debugger has a breakpoint at address,
   * false otherwise
   */
  public boolean isBreakpoint(int address) {
	  return (breakpoints.get(address) != null);
  }
  
  protected long getGotoAddress() {
    String address = JOptionPane.showInputDialog("Address: ", "#");
    if (address == null)
      return -1;
    address = address.trim();
    if (address.length() == 0)
      return -1;
    
    switch(address.charAt(0)) {
      case '#':
      case '&':
      case '$': return Long.parseLong(address.substring(1), 16);

      default:  return Long.parseLong(address);
    }
  }

  public static void setDisass(int address){
      eDisassembler.setPC(address);
  };
    public void itemStateChanged(final ItemEvent e) {
        if (e.getSource() == bPoints){
            Switches.breakpoints = bPoints.isSelected();
            Settings.setBoolean(Settings.BREAKPOINTS, bPoints.isSelected());
        }
        if (e.getSource() == bInst){
            Switches.breakinsts = bInst.isSelected();
            Settings.setBoolean(Settings.BREAKINST, bInst.isSelected());
        }
    }
  public void actionPerformed(ActionEvent e) {
    computer.clearRunToAddress();
    if      (e.getSource() == bRun)      computer.start();
    else if (e.getSource() == bStop)     computer.stop();
    else if (e.getSource() == bStep)     computer.step();
    else if (e.getSource() == bStepOver) computer.stepOver();
    else if (e.getSource() == computer)  updateDisplay();
    else if (e.getSource() == mGoto) {
      long address = getGotoAddress();
      if (address != -1) {
        if (popupMenu.getInvoker() == eDisassembler) eDisassembler.setAddress((int)address);
        else                                         eMemory.setAddress((int)address);
      }
    }
    else if (e.getSource() == mBreak) {
    	// Add a breakpoint
    	if (Switches.breakpoints && breakaddress != 0) {
    		Breakpoint bp = breakpoints.get(breakaddress);
    		if (bp == null) {
    			bp = new StopBreakpoint(computer.getProcessor(), breakaddress);
                computer.getProcessor().attachProgramCounterObserver(bp);
    			breakpoints.put(breakaddress, bp);
				System.out.println("StopBreakpoint added at &" + Util.hex(breakaddress));
    		}
    		this.repaint();
    	}
    }
    else if (e.getSource() == mRemove) {
    	// Remove a breakpoint
    	if (Switches.breakpoints) {
    		Breakpoint bp = breakpoints.get(breakaddress);
    		if (bp != null) {
        		computer.getProcessor().detachProgramCounterObserver(bp);
        		breakpoints.remove(breakaddress);
				System.out.println("StopBreakpoint removed at &" + Util.hex(breakaddress));
    		}
    		this.repaint();
    	}
    }
    else if (e.getSource() == mRemoveAll) {
    	// Remove all breakpoints
        removeAllBreakpoints();
    		this.repaint();
    }
    else if (e.getSource() == mSave) {
      if (popupMenu.getInvoker() == eDisassembler) saveDisassembly();
      else                                         saveMemory();
    }
    computer.setFrameSkip(0);
    computer.updateDisplay(false);
  }
  
  protected File showSaveDialog(String title) {
    if (fileDlg == null)
      fileDlg = new JFileChooser();
    fileDlg.setDialogTitle(title);
    return fileDlg.showSaveDialog(bRun) == JFileChooser.APPROVE_OPTION ? fileDlg.getSelectedFile() : null;
  }
  
  public void saveMemory() {
    saveMemory(eMemory.selStart, eMemory.selEnd);
  }
  
  public void saveMemory(int start, int end) {
    File file = showSaveDialog("Save Memory");
    if (file != null) {
      try {
        FileOutputStream io = new FileOutputStream(file);
        try {
          Memory mem = computer.getMemory();
          for (int addr = start; addr <= end; addr++)
            io.write(mem.readByte(addr));
        } finally {
          io.close();
        }
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public void saveDisassembly() {
    saveDisassembly(eDisassembler.selStart, eDisassembler.selEnd);
  }
  
  public void saveDisassembly(int start, int end) {
    File file = showSaveDialog("Save Disassembly");
    if (file != null) {
      int[] addr = new int[] { start };
      try {
        FileOutputStream io = new FileOutputStream(file);
        try {
          Disassembler diss = computer.getDisassembler();
          Memory mem = computer.getMemory();
            io.write(("  ;; JavaCPC disassembled binary\r\n" +
                    "  ;; disassembled from "+ Util.hex((short)start)+ " to "
                    + Util.hex((short)end)+ "\r\n" +
                    "  ;; " + cal.getTime()+"\r\n\r\n").getBytes());
            io.write(("    ORG #" + Util.hex((short)start)+ "\r\n\r\n").getBytes());
          while (addr[0] <= end) {
            String s = "    ";//+Util.hex((short)addr[0]) + ": ";
            io.write((s + diss.disassemble(mem, addr) + "\r\n").getBytes());
          }
        } finally {
          io.close();
        }
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {

    popupMenu = new javax.swing.JPopupMenu();
    mGoto = new javax.swing.JMenuItem();
    mSave = new javax.swing.JMenuItem();
    mBreak = new javax.swing.JMenuItem();
    mRemove = new javax.swing.JMenuItem();
    mRemoveAll = new javax.swing.JMenuItem();
    jPanel1 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    jPanel2.setBackground(new Color (170,170,170));
    jPanel2.setBorder(new BevelBorder(BevelBorder.LOWERED));
    bRun = new javax.swing.JButton();
    bStop = new javax.swing.JButton();
    bPoints = new javax.swing.JCheckBox();
    bInst = new javax.swing.JCheckBox();
    bStep = new jemu.ui.EButton();
    bStepOver = new jemu.ui.EButton();
    jPanel3 = new javax.swing.JPanel();
    jPanel3.setBackground(new Color (170,170,170));
    jPanel3.setBorder(new BevelBorder(BevelBorder.LOWERED));
    lCycles = new javax.swing.JLabel();
    lCycleCount = new javax.swing.JLabel();
    eRegisters = new jemu.ui.ERegisters();
    jSplitPane1 = new javax.swing.JSplitPane();
    eDisassembler = new jemu.ui.EDisassembler();
    eDisassembler.setBackground(new Color (190,190,220));
    jScrollPane1 = new javax.swing.JScrollPane();
    eMemory = new jemu.ui.EMemory();
    eMemory.setBackground(new Color (190,220,190));
    eRegisters.setBackground(new Color (170,170,170));

    mGoto.setText("Goto...");
    popupMenu.add(mGoto);

    mBreak.setText("Set breakpoint...");
    popupMenu.add(mBreak);
    mRemove.setText("Remove breakpoint...");
    popupMenu.add(mRemove);
    mRemoveAll.setText("Remove all breakpoints...");
    popupMenu.add(mRemoveAll);

    mSave.setText("Save...");
    popupMenu.add(mSave);

    setTitle("JavaCPC Debugger");

    jPanel1.setLayout(new java.awt.BorderLayout());

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    bRun.setText("Run");
    jPanel2.add(bRun);

    bStop.setText("Stop");
    jPanel2.add(bStop);

    bStep.setText("Step");
    jPanel2.add(bStep);

    bStepOver.setText("Step Over");
    jPanel2.add(bStepOver);
    bPoints.setText("Breakpoints");
    jPanel2.add(bPoints);
    bInst.setText("Break Instructions");
    jPanel2.add(bInst);

    jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

    lCycles.setForeground(new java.awt.Color(70, 255, 70));
    lCycles.setText("Cycles:");
    jPanel3.add(lCycles);

    lCycleCount.setText("0");
    lCycleCount.addMouseListener(this);
    lCycleCount.setForeground(new java.awt.Color(70, 70, 255));

    jPanel3.add(lCycleCount);

    jPanel1.add(jPanel3, java.awt.BorderLayout.EAST);

    getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

    eRegisters.setLayout(null);
    getContentPane().add(eRegisters, java.awt.BorderLayout.LINE_END);

    jSplitPane1.setDividerLocation(200);
    jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.setContinuousLayout(true);

    eDisassembler.setComponentPopupMenu(popupMenu);
    eDisassembler.addMouseListener(this);

    jSplitPane1.setTopComponent(eDisassembler);

    eMemory.setComponentPopupMenu(popupMenu);
    jScrollPane1.setViewportView(eMemory);

    jSplitPane1.setRightComponent(jScrollPane1);

    getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

    pack();
  }

  // Code for dispatching events from components to event handlers.

  public void mouseClicked(java.awt.event.MouseEvent evt) {
    if (evt.getSource() == lCycleCount) {
      Debugger.this.lCycleCountMouseClicked(evt);
    }
    else if (evt.getSource() == eDisassembler) {
    breakaddress = eDisassembler.getAddress(evt.getY());
    System.out.println("Breakaddress = "+ Util.hex(breakaddress));
      Debugger.this.eDisassemblerMouseClicked(evt);
    }
  }

  public void mouseEntered(java.awt.event.MouseEvent evt) {
  }

  public void mouseExited(java.awt.event.MouseEvent evt) {
  }

  public void mousePressed(java.awt.event.MouseEvent evt) {
  }

  public void mouseReleased(java.awt.event.MouseEvent evt) {
  }// </editor-fold>//GEN-END:initComponents

  private void eDisassemblerMouseClicked(java.awt.event.MouseEvent e) {//GEN-FIRST:event_eDisassemblerMouseClicked
    if (e.getClickCount() == 2) {
      int addr = eDisassembler.getAddress(e.getY());
      if (addr != -1) {
        computer.setRunToAddress(addr);
        computer.start();
      }
    }
  }//GEN-LAST:event_eDisassemblerMouseClicked

  public void removeAllBreakpoints(){
    	if (Switches.breakpoints && !breakpoints.isEmpty()) {
    		Processor p = computer.getProcessor();
    		for (Breakpoint bp : breakpoints.values()) {
                p.detachProgramCounterObserver(bp);
    		}
    		breakpoints.clear();
    		System.out.println("StopBreakpoint all removed");
    	}
  }

  public void continueAndReset(){
            computer.start();
            computer.reset();
  }

  private void lCycleCountMouseClicked(java.awt.event.MouseEvent e) {//GEN-FIRST:event_lCycleCountMouseClicked
    if (e.getClickCount() == 2) {
      startCycles = computer.getProcessor().getCycles();
      lCycleCount.setText("0");
    }
  }//GEN-LAST:event_lCycleCountMouseClicked
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  protected javax.swing.JButton bRun;
  protected jemu.ui.EButton bStep;
  protected jemu.ui.EButton bStepOver;
  protected javax.swing.JButton bStop;
  protected javax.swing.JCheckBox bPoints;
  protected javax.swing.JCheckBox bInst;
  public static jemu.ui.EDisassembler eDisassembler;
  protected jemu.ui.EMemory eMemory;
  protected jemu.ui.ERegisters eRegisters;
  protected javax.swing.JPanel jPanel1;
  protected javax.swing.JPanel jPanel2;
  protected javax.swing.JPanel jPanel3;
  protected javax.swing.JScrollPane jScrollPane1;
  protected javax.swing.JSplitPane jSplitPane1;
  protected javax.swing.JLabel lCycleCount;
  protected javax.swing.JLabel lCycles;
  protected javax.swing.JMenuItem mGoto;
  protected javax.swing.JMenuItem mSave;
  protected javax.swing.JMenuItem mBreak;
  protected javax.swing.JMenuItem mRemove;
  protected javax.swing.JMenuItem mRemoveAll;
  protected javax.swing.JPopupMenu popupMenu;
  // End of variables declaration//GEN-END:variables
  
}
