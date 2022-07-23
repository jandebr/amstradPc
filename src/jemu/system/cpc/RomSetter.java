/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.system.cpc;

/**
* Title:        JavaCPC
* Description:  The Java CPC Emulator
* Copyright:    Copyright (c) 2002-2009
* Company:
* @author:      Markus
*/

import jemu.ui.Switches;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import jemu.settings.*;

@SuppressWarnings("unchecked")
public class RomSetter implements ActionListener{

 protected GridBagConstraints Constraints   = null;

    //
    // Settings for romsetter
    //

    JLabel spacer= new JLabel("     ");
    protected final String[] LowerROMs = {
      "none", "OS464.zip", "OS664.zip", "OS6128.zip", "KCCOS.zip", "-OS.zip",
      "jemuOS6128.zip", "custom"
    };
    protected final String[] UpperROMs = {
      "none", "AMSDOS.zip", "ART0.zip", "ART1.zip", "ART2.zip",
      "BASIC1-0.zip", "BASIC1-1.zip", "BASIC664.zip",
      "PARADOS.zip", "MAXAM.zip", "sym-romA.zip", "sym-romB.zip",
      "sym-romC.zip", "sym-romD.zip",
      "Stk12-1.zip", "Stk12-2.zip", "Stk12Gen.zip",
      "T-OG-E-A.zip", "T-OG-E-B.zip",
      "T-OG-E-C.zip", "T-OG-E-D.zip", "ROMPKP1.zip", "ROMPKP2.zip",
      "STK1.zip", "STK2.zip", "locksmit.zip", "KCCBAS.zip", "-BASIC1-1.zip",
      "custom"
    };

   protected final Frame romsetter = new Frame();

  protected final JLabel lorom = new JLabel("Lower Rom");
  protected final JLabel uprom0 = new JLabel("Upper 00");
  protected final JLabel uprom1 = new JLabel("Upper 01");
  protected final JLabel uprom2 = new JLabel("Upper 02");
  protected final JLabel uprom3 = new JLabel("Upper 03");
  protected final JLabel uprom4 = new JLabel("Upper 04");
  protected final JLabel uprom5 = new JLabel("Upper 05");
  protected final JLabel uprom6 = new JLabel("Upper 06");
  protected final JLabel uprom7 = new JLabel("Upper 07");
  protected final JLabel uprom8 = new JLabel("Upper 08");
  protected final JLabel uprom9 = new JLabel("Upper 09");
  protected final JLabel uproma = new JLabel("Upper 10");
  protected final JLabel upromb = new JLabel("Upper 11");
  protected final JLabel upromc = new JLabel("Upper 12");
  protected final JLabel upromd = new JLabel("Upper 13");
  protected final JLabel uprome = new JLabel("Upper 14");
  protected final JLabel upromf = new JLabel("Upper 15");
  protected final JLabel romblank = new JLabel("          ");
  protected final JComboBox LOWER_R = new JComboBox(LowerROMs);
  protected final JComboBox UPPER_0 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_1 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_2 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_3 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_4 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_5 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_6 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_7 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_8 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_9 = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_A = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_B = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_C = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_D = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_E = new JComboBox(UpperROMs);
  protected final JComboBox UPPER_F = new JComboBox(UpperROMs);

  protected final JButton applyroms = new JButton("Apply");
  protected final JButton okroms = new JButton("Close");
  protected final JButton lload = new JButton(" ... ");
  protected final JButton u0load = new JButton(" ... ");
  protected final JButton u1load = new JButton(" ... ");
  protected final JButton u2load = new JButton(" ... ");
  protected final JButton u3load = new JButton(" ... ");
  protected final JButton u4load = new JButton(" ... ");
  protected final JButton u5load = new JButton(" ... ");
  protected final JButton u6load = new JButton(" ... ");
  protected final JButton u7load = new JButton(" ... ");
  protected final JButton u8load = new JButton(" ... ");
  protected final JButton u9load = new JButton(" ... ");
  protected final JButton uAload = new JButton(" ... ");
  protected final JButton uBload = new JButton(" ... ");
  protected final JButton uCload = new JButton(" ... ");
  protected final JButton uDload = new JButton(" ... ");
  protected final JButton uEload = new JButton(" ... ");
  protected final JButton uFload = new JButton(" ... ");


  public void StoreRoms(){

        Settings.set(Settings.UPPER_ROM_F, Switches.UPPER_F);
        Settings.set(Settings.UPPER_ROM_E, Switches.UPPER_E);
        Settings.set(Settings.UPPER_ROM_D, Switches.UPPER_D);
        Settings.set(Settings.UPPER_ROM_C, Switches.UPPER_C);
        Settings.set(Settings.UPPER_ROM_B, Switches.UPPER_B);
        Settings.set(Settings.UPPER_ROM_A, Switches.UPPER_A);
        Settings.set(Settings.UPPER_ROM_9, Switches.UPPER_9);
        Settings.set(Settings.UPPER_ROM_8, Switches.UPPER_8);
        Settings.set(Settings.UPPER_ROM_7, Switches.UPPER_7);
        Settings.set(Settings.UPPER_ROM_6, Switches.UPPER_6);
        Settings.set(Settings.UPPER_ROM_5, Switches.UPPER_5);
        Settings.set(Settings.UPPER_ROM_4, Switches.UPPER_4);
        Settings.set(Settings.UPPER_ROM_3, Switches.UPPER_3);
        Settings.set(Settings.UPPER_ROM_2, Switches.UPPER_2);
        Settings.set(Settings.UPPER_ROM_1, Switches.UPPER_1);
        Settings.set(Settings.UPPER_ROM_0, Switches.UPPER_0);
        Settings.set(Settings.LOWER_ROM, Switches.LOWER);

        }

  public void setRoms(){

                romsetter.setVisible(true);
  }

  public void prepareRomsetter(){

      //  manual ROM settings
      // in progress

      Switches.LOWER = Settings.get(Settings.LOWER_ROM, "OS464.zip");
      Switches.UPPER_0 = Settings.get(Settings.UPPER_ROM_0, "BASIC1-0.zip");
      Switches.UPPER_1 = Settings.get(Settings.UPPER_ROM_1, "none");
      Switches.UPPER_2 = Settings.get(Settings.UPPER_ROM_2, "none");
      Switches.UPPER_3 = Settings.get(Settings.UPPER_ROM_3, "none");
      Switches.UPPER_4 = Settings.get(Settings.UPPER_ROM_4, "none");
      Switches.UPPER_5 = Settings.get(Settings.UPPER_ROM_5, "none");
      Switches.UPPER_6 = Settings.get(Settings.UPPER_ROM_6, "none");
      Switches.UPPER_7 = Settings.get(Settings.UPPER_ROM_7, "none");
      Switches.UPPER_8 = Settings.get(Settings.UPPER_ROM_8, "none");
      Switches.UPPER_9 = Settings.get(Settings.UPPER_ROM_9, "none");
      Switches.UPPER_A = Settings.get(Settings.UPPER_ROM_A, "none");
      Switches.UPPER_B = Settings.get(Settings.UPPER_ROM_B, "none");
      Switches.UPPER_C = Settings.get(Settings.UPPER_ROM_C, "none");
      Switches.UPPER_D = Settings.get(Settings.UPPER_ROM_D, "none");
      Switches.UPPER_E = Settings.get(Settings.UPPER_ROM_E, "none");
      Switches.UPPER_F = Settings.get(Settings.UPPER_ROM_F, "none");

      StoreRoms();

  Switches.romlow = Settings.get(Settings.romlow, "1");
  Switches.rom0 = Settings.get(Settings.rom0, "5");
  Switches.rom1 = Settings.get(Settings.rom1, "0");
  Switches.rom2 = Settings.get(Settings.rom2, "0");
  Switches.rom3 = Settings.get(Settings.rom3, "0");
  Switches.rom4 = Settings.get(Settings.rom4, "0");
  Switches.rom5 = Settings.get(Settings.rom5, "0");
  Switches.rom6 = Settings.get(Settings.rom6, "0");
  Switches.rom7 = Settings.get(Settings.rom7, "0");
  Switches.rom8 = Settings.get(Settings.rom8, "0");
  Switches.rom9 = Settings.get(Settings.rom9, "0");
  Switches.rom10 = Settings.get(Settings.rom10, "0");
  Switches.rom11 = Settings.get(Settings.rom11, "0");
  Switches.rom12 = Settings.get(Settings.rom12, "0");
  Switches.rom13 = Settings.get(Settings.rom13, "0");
  Switches.rom14 = Settings.get(Settings.rom14, "0");
  Switches.rom15 = Settings.get(Settings.rom15, "0");

  LOWER_R.setSelectedIndex(Integer.parseInt(Switches.romlow));
  UPPER_0.setSelectedIndex(Integer.parseInt(Switches.rom0));
  UPPER_1.setSelectedIndex(Integer.parseInt(Switches.rom1));
  UPPER_2.setSelectedIndex(Integer.parseInt(Switches.rom2));
  UPPER_3.setSelectedIndex(Integer.parseInt(Switches.rom3));
  UPPER_4.setSelectedIndex(Integer.parseInt(Switches.rom4));
  UPPER_5.setSelectedIndex(Integer.parseInt(Switches.rom5));
  UPPER_6.setSelectedIndex(Integer.parseInt(Switches.rom6));
  UPPER_7.setSelectedIndex(Integer.parseInt(Switches.rom7));
  UPPER_8.setSelectedIndex(Integer.parseInt(Switches.rom8));
  UPPER_9.setSelectedIndex(Integer.parseInt(Switches.rom9));
  UPPER_A.setSelectedIndex(Integer.parseInt(Switches.rom10));
  UPPER_B.setSelectedIndex(Integer.parseInt(Switches.rom11));
  UPPER_C.setSelectedIndex(Integer.parseInt(Switches.rom12));
  UPPER_D.setSelectedIndex(Integer.parseInt(Switches.rom13));
  UPPER_E.setSelectedIndex(Integer.parseInt(Switches.rom14));
  UPPER_F.setSelectedIndex(Integer.parseInt(Switches.rom15));
  romsetter.setBackground(Color.DARK_GRAY);
    applyroms.addActionListener(this);
    okroms.addActionListener(this);
    lload.addActionListener(this);
    u0load.addActionListener(this);
    u1load.addActionListener(this);
    u2load.addActionListener(this);
    u3load.addActionListener(this);
    u4load.addActionListener(this);
    u5load.addActionListener(this);
    u6load.addActionListener(this);
    u7load.addActionListener(this);
    u8load.addActionListener(this);
    u9load.addActionListener(this);
    uAload.addActionListener(this);
    uBload.addActionListener(this);
    uCload.addActionListener(this);
    uDload.addActionListener(this);
    uEload.addActionListener(this);
    uFload.addActionListener(this);

    okroms.setBackground(Color.DARK_GRAY);
    okroms.setForeground(Color.LIGHT_GRAY);
    okroms.setFocusable(false);
    okroms.setBorder(new BevelBorder(BevelBorder.RAISED));
    applyroms.setBackground(Color.DARK_GRAY);
    applyroms.setForeground(Color.LIGHT_GRAY);
    applyroms.setFocusable(false);
    applyroms.setBorder(new BevelBorder(BevelBorder.RAISED));
    lload.setBackground(Color.DARK_GRAY);
    lload.setForeground(Color.LIGHT_GRAY);
    lload.setFocusable(false);
    lload.setBorder(new BevelBorder(BevelBorder.RAISED));
    u0load.setBackground(Color.DARK_GRAY);
    u0load.setForeground(Color.LIGHT_GRAY);
    u0load.setFocusable(false);
    u0load.setBorder(new BevelBorder(BevelBorder.RAISED));
    u1load.setBackground(Color.DARK_GRAY);
    u1load.setForeground(Color.LIGHT_GRAY);
    u1load.setFocusable(false);
    u1load.setBorder(new BevelBorder(BevelBorder.RAISED));
    u2load.setBackground(Color.DARK_GRAY);
    u2load.setForeground(Color.LIGHT_GRAY);
    u2load.setFocusable(false);
    u2load.setBorder(new BevelBorder(BevelBorder.RAISED));
    u3load.setBackground(Color.DARK_GRAY);
    u3load.setForeground(Color.LIGHT_GRAY);
    u3load.setFocusable(false);
    u3load.setBorder(new BevelBorder(BevelBorder.RAISED));
    u4load.setBackground(Color.DARK_GRAY);
    u4load.setForeground(Color.LIGHT_GRAY);
    u4load.setFocusable(false);
    u4load.setBorder(new BevelBorder(BevelBorder.RAISED));
    u5load.setBackground(Color.DARK_GRAY);
    u5load.setForeground(Color.LIGHT_GRAY);
    u5load.setFocusable(false);
    u5load.setBorder(new BevelBorder(BevelBorder.RAISED));
    u6load.setBackground(Color.DARK_GRAY);
    u6load.setForeground(Color.LIGHT_GRAY);
    u6load.setFocusable(false);
    u6load.setBorder(new BevelBorder(BevelBorder.RAISED));
    u7load.setBackground(Color.DARK_GRAY);
    u7load.setForeground(Color.LIGHT_GRAY);
    u7load.setFocusable(false);
    u7load.setBorder(new BevelBorder(BevelBorder.RAISED));
    u8load.setBackground(Color.DARK_GRAY);
    u8load.setForeground(Color.LIGHT_GRAY);
    u8load.setFocusable(false);
    u8load.setBorder(new BevelBorder(BevelBorder.RAISED));
    u9load.setBackground(Color.DARK_GRAY);
    u9load.setForeground(Color.LIGHT_GRAY);
    u9load.setFocusable(false);
    u9load.setBorder(new BevelBorder(BevelBorder.RAISED));
    uAload.setBackground(Color.DARK_GRAY);
    uAload.setForeground(Color.LIGHT_GRAY);
    uAload.setFocusable(false);
    uAload.setBorder(new BevelBorder(BevelBorder.RAISED));
    uBload.setBackground(Color.DARK_GRAY);
    uBload.setForeground(Color.LIGHT_GRAY);
    uBload.setFocusable(false);
    uBload.setBorder(new BevelBorder(BevelBorder.RAISED));
    uCload.setBackground(Color.DARK_GRAY);
    uCload.setForeground(Color.LIGHT_GRAY);
    uCload.setFocusable(false);
    uCload.setBorder(new BevelBorder(BevelBorder.RAISED));
    uDload.setBackground(Color.DARK_GRAY);
    uDload.setForeground(Color.LIGHT_GRAY);
    uDload.setFocusable(false);
    uDload.setBorder(new BevelBorder(BevelBorder.RAISED));
    uEload.setBackground(Color.DARK_GRAY);
    uEload.setForeground(Color.LIGHT_GRAY);
    uEload.setFocusable(false);
    uEload.setBorder(new BevelBorder(BevelBorder.RAISED));
    uFload.setBackground(Color.DARK_GRAY);
    uFload.setForeground(Color.LIGHT_GRAY);
    uFload.setFocusable(false);
    uFload.setBorder(new BevelBorder(BevelBorder.RAISED));
    lorom.setBackground(Color.DARK_GRAY);
    lorom.setForeground(Color.LIGHT_GRAY);
    lorom.setFocusable(false);
    uprom0.setBackground(Color.DARK_GRAY);
    uprom0.setForeground(Color.LIGHT_GRAY);
    uprom0.setFocusable(false);
    uprom1.setBackground(Color.DARK_GRAY);
    uprom1.setForeground(Color.LIGHT_GRAY);
    uprom1.setFocusable(false);
    uprom2.setBackground(Color.DARK_GRAY);
    uprom2.setForeground(Color.LIGHT_GRAY);
    uprom2.setFocusable(false);
    uprom3.setBackground(Color.DARK_GRAY);
    uprom3.setForeground(Color.LIGHT_GRAY);
    uprom3.setFocusable(false);
    uprom4.setBackground(Color.DARK_GRAY);
    uprom4.setForeground(Color.LIGHT_GRAY);
    uprom4.setFocusable(false);
    uprom5.setBackground(Color.DARK_GRAY);
    uprom5.setForeground(Color.LIGHT_GRAY);
    uprom5.setFocusable(false);
    uprom6.setBackground(Color.DARK_GRAY);
    uprom6.setForeground(Color.LIGHT_GRAY);
    uprom6.setFocusable(false);
    uprom7.setBackground(Color.DARK_GRAY);
    uprom7.setForeground(Color.LIGHT_GRAY);
    uprom7.setFocusable(false);
    uprom8.setBackground(Color.DARK_GRAY);
    uprom8.setForeground(Color.LIGHT_GRAY);
    uprom8.setFocusable(false);
    uprom9.setBackground(Color.DARK_GRAY);
    uprom9.setForeground(Color.LIGHT_GRAY);
    uprom9.setFocusable(false);
    uproma.setBackground(Color.DARK_GRAY);
    uproma.setForeground(Color.LIGHT_GRAY);
    uproma.setFocusable(false);
    upromb.setBackground(Color.DARK_GRAY);
    upromb.setForeground(Color.LIGHT_GRAY);
    upromb.setFocusable(false);
    upromc.setBackground(Color.DARK_GRAY);
    upromc.setForeground(Color.LIGHT_GRAY);
    upromc.setFocusable(false);
    upromd.setBackground(Color.DARK_GRAY);
    upromd.setForeground(Color.LIGHT_GRAY);
    upromd.setFocusable(false);
    uprome.setBackground(Color.DARK_GRAY);
    uprome.setForeground(Color.LIGHT_GRAY);
    uprome.setFocusable(false);
    upromf.setBackground(Color.DARK_GRAY);
    upromf.setForeground(Color.LIGHT_GRAY);
    upromf.setFocusable(false);
    LOWER_R.setBackground(Color.DARK_GRAY);
    LOWER_R.setForeground(Color.LIGHT_GRAY);
    LOWER_R.setFocusable(false);
    LOWER_R.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_0.setBackground(Color.DARK_GRAY);
    UPPER_0.setForeground(Color.LIGHT_GRAY);
    UPPER_0.setFocusable(false);
    UPPER_0.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_1.setBackground(Color.DARK_GRAY);
    UPPER_1.setForeground(Color.LIGHT_GRAY);
    UPPER_1.setFocusable(false);
    UPPER_1.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_2.setBackground(Color.DARK_GRAY);
    UPPER_2.setForeground(Color.LIGHT_GRAY);
    UPPER_2.setFocusable(false);
    UPPER_2.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_3.setBackground(Color.DARK_GRAY);
    UPPER_3.setForeground(Color.LIGHT_GRAY);
    UPPER_3.setFocusable(false);
    UPPER_3.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_4.setBackground(Color.DARK_GRAY);
    UPPER_4.setForeground(Color.LIGHT_GRAY);
    UPPER_4.setFocusable(false);
    UPPER_4.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_5.setBackground(Color.DARK_GRAY);
    UPPER_5.setForeground(Color.LIGHT_GRAY);
    UPPER_5.setFocusable(false);
    UPPER_5.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_6.setBackground(Color.DARK_GRAY);
    UPPER_6.setForeground(Color.LIGHT_GRAY);
    UPPER_6.setFocusable(false);
    UPPER_6.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_7.setBackground(Color.DARK_GRAY);
    UPPER_7.setForeground(Color.LIGHT_GRAY);
    UPPER_7.setFocusable(false);
    UPPER_7.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_8.setBackground(Color.DARK_GRAY);
    UPPER_8.setForeground(Color.LIGHT_GRAY);
    UPPER_8.setFocusable(false);
    UPPER_8.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_9.setBackground(Color.DARK_GRAY);
    UPPER_9.setForeground(Color.LIGHT_GRAY);
    UPPER_9.setFocusable(false);
    UPPER_9.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_A.setBackground(Color.DARK_GRAY);
    UPPER_A.setForeground(Color.LIGHT_GRAY);
    UPPER_A.setFocusable(false);
    UPPER_A.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_B.setBackground(Color.DARK_GRAY);
    UPPER_B.setForeground(Color.LIGHT_GRAY);
    UPPER_B.setFocusable(false);
    UPPER_B.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_C.setBackground(Color.DARK_GRAY);
    UPPER_C.setForeground(Color.LIGHT_GRAY);
    UPPER_C.setFocusable(false);
    UPPER_C.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_D.setBackground(Color.DARK_GRAY);
    UPPER_D.setForeground(Color.LIGHT_GRAY);
    UPPER_D.setFocusable(false);
    UPPER_D.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_E.setBackground(Color.DARK_GRAY);
    UPPER_E.setForeground(Color.LIGHT_GRAY);
    UPPER_E.setFocusable(false);
    UPPER_E.setBorder(new BevelBorder(BevelBorder.LOWERED));
    UPPER_F.setBackground(Color.DARK_GRAY);
    UPPER_F.setForeground(Color.LIGHT_GRAY);
    UPPER_F.setFocusable(false);
    UPPER_F.setBorder(new BevelBorder(BevelBorder.LOWERED));
            romsetter.setLayout(new GridBagLayout());
      romsetter.add(lorom,getGridBagConstraints(1, 1, 0.0, 0.0, 1, GridBagConstraints.CENTER));
      romsetter.add(LOWER_R,getGridBagConstraints(1, 2, 0.0, 0.0, 1, GridBagConstraints.CENTER));
      romsetter.add(uprom0,getGridBagConstraints(1, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_0,getGridBagConstraints(1, 4, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uprom1,getGridBagConstraints(1, 5, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_1,getGridBagConstraints(1, 6, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uprom2,getGridBagConstraints(1, 7, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_2,getGridBagConstraints(1, 8, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uprom3,getGridBagConstraints(1, 9, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_3,getGridBagConstraints(1, 10, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uprom4,getGridBagConstraints(1, 11, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_4,getGridBagConstraints(1, 12, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uprom5,getGridBagConstraints(1, 13, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_5,getGridBagConstraints(1, 14, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uprom6,getGridBagConstraints(1, 15, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_6,getGridBagConstraints(1, 16, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uprom7,getGridBagConstraints(1, 17, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_7,getGridBagConstraints(1, 18, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(romblank,getGridBagConstraints(1, 19, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(lload,getGridBagConstraints(2, 2, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u0load,getGridBagConstraints(2, 4, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u1load,getGridBagConstraints(2, 6, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u2load,getGridBagConstraints(2, 8, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u3load,getGridBagConstraints(2, 10, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u4load,getGridBagConstraints(2, 12, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u5load,getGridBagConstraints(2, 14, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u6load,getGridBagConstraints(2, 16, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u7load,getGridBagConstraints(2, 18, 0.0, 0.0, 1, GridBagConstraints.BOTH));

      romsetter.add(spacer,getGridBagConstraints(3, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));

      romsetter.add(uprom8,getGridBagConstraints(4, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_8,getGridBagConstraints(4, 4, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uprom9,getGridBagConstraints(4, 5, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_9,getGridBagConstraints(4, 6, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uproma,getGridBagConstraints(4, 7, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_A,getGridBagConstraints(4, 8, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(upromb,getGridBagConstraints(4, 9, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_B,getGridBagConstraints(4, 10, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(upromc,getGridBagConstraints(4, 11, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_C,getGridBagConstraints(4, 12, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(upromd,getGridBagConstraints(4, 13, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_D,getGridBagConstraints(4, 14, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uprome,getGridBagConstraints(4, 15, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_E,getGridBagConstraints(4, 16, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(upromf,getGridBagConstraints(4, 17, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(UPPER_F,getGridBagConstraints(4, 18, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u8load,getGridBagConstraints(5, 4, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(u9load,getGridBagConstraints(5, 6, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uAload,getGridBagConstraints(5, 8, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uBload,getGridBagConstraints(5, 10, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uCload,getGridBagConstraints(5, 12, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uDload,getGridBagConstraints(5, 14, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uEload,getGridBagConstraints(5, 16, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      romsetter.add(uFload,getGridBagConstraints(5, 18, 0.0, 0.0, 1, GridBagConstraints.BOTH));

      romsetter.add(applyroms,getGridBagConstraints(1, 20, 0.0, 0.0, 2, GridBagConstraints.BOTH));
      romsetter.add(okroms,getGridBagConstraints(4, 20, 0.0, 0.0, 2, GridBagConstraints.BOTH));


                romsetter.setTitle("JavaCPC ROM settings");
                romsetter.setResizable(false);
                romsetter.setAlwaysOnTop(true);

                romsetter.pack();
    final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    romsetter.setLocation((d.width - romsetter.getSize().width) / 2, (d.height - romsetter.getSize().height) / 2);
  }

  public void loadLow(){
      Frame framer = new Frame();
      final FileDialog dlg = new FileDialog((Frame) framer, "Choose Lower ROM", FileDialog.LOAD);
      dlg.setFile("*.rom; *.zip");
      dlg.setVisible(true);
      if (dlg.getFile() != null) {
        Switches.LOWER = "custom:" + dlg.getDirectory() + dlg.getFile();
        Settings.set(Settings.LOWER_ROM, Switches.LOWER);
        Switches.romlow = "7";
        Settings.set(Settings.romlow, Switches.romlow);
        LOWER_R.setSelectedItem(LowerROMs[7]);
      }
          dlg.dispose();
      romsetter.setVisible(true);
  }

  public void loadUpper(int slot){
      Frame framer = new Frame();
      final FileDialog dlg = new FileDialog((Frame) framer, "Choose Upper ROM", FileDialog.LOAD);
      dlg.setFile("*.rom; *.zip");
      dlg.setVisible(true);
      if (dlg.getFile() != null) {
          if (slot == 0){
            Switches.UPPER_0 = "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_0, Switches.UPPER_0);
            Switches.rom0 = "28";
            Settings.set(Settings.rom0, Switches.rom0);
            UPPER_0.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 1){
            Switches.UPPER_1 =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_1, Switches.UPPER_1);
            Switches.rom1 = "28";
            Settings.set(Settings.rom1, Switches.rom1);
            UPPER_1.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 2){
            Switches.UPPER_2 =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_2, Switches.UPPER_2);
            Switches.rom2 = "28";
            Settings.set(Settings.rom2, Switches.rom2);
            UPPER_2.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 3){
            Switches.UPPER_3 =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_3, Switches.UPPER_3);
            Switches.rom3 = "28";
            Settings.set(Settings.rom3, Switches.rom3);
            UPPER_3.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 4){
            Switches.UPPER_4 =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_4, Switches.UPPER_4);
            Switches.rom4 = "28";
            Settings.set(Settings.rom4, Switches.rom4);
            UPPER_4.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 5){
            Switches.UPPER_5 =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_5, Switches.UPPER_5);
            Switches.rom5 = "28";
            Settings.set(Settings.rom5, Switches.rom5);
            UPPER_5.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 6){
            Switches.UPPER_6 =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_6, Switches.UPPER_6);
            Switches.rom6 = "28";
            Settings.set(Settings.rom6, Switches.rom6);
            UPPER_6.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 7){
            Switches.UPPER_7 =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_7, Switches.UPPER_7);
            Switches.rom7 = "28";
            Settings.set(Settings.rom7, Switches.rom7);
            UPPER_7.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 8){
            Switches.UPPER_8 =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_8, Switches.UPPER_8);
            Switches.rom8 = "28";
            Settings.set(Settings.rom8, Switches.rom8);
            UPPER_8.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 9){
            Switches.UPPER_9 =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_9, Switches.UPPER_9);
            Switches.rom9 = "28";
            Settings.set(Settings.rom9, Switches.rom9);
            UPPER_9.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 10){
            Switches.UPPER_A =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_A, Switches.UPPER_A);
            Switches.rom10 = "28";
            Settings.set(Settings.rom10, Switches.rom10);
            UPPER_A.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 11){
            Switches.UPPER_B =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_B, Switches.UPPER_B);
            Switches.rom11 = "28";
            Settings.set(Settings.rom11, Switches.rom11);
            UPPER_B.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 12){
            Switches.UPPER_C =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_C, Switches.UPPER_C);
            Switches.rom12 = "28";
            Settings.set(Settings.rom12, Switches.rom12);
            UPPER_C.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 13){
            Switches.UPPER_D =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_D, Switches.UPPER_D);
            Switches.rom13 = "28";
            Settings.set(Settings.rom13, Switches.rom13);
            UPPER_D.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 14){
            Switches.UPPER_E =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_E, Switches.UPPER_E);
            Switches.rom14 = "28";
            Settings.set(Settings.rom14, Switches.rom14);
            UPPER_E.setSelectedItem(UpperROMs[28]);
          }
          if (slot == 15){
            Switches.UPPER_F =  "custom:" + dlg.getDirectory() + dlg.getFile();
            Settings.set(Settings.UPPER_ROM_F, Switches.UPPER_F);
            Switches.rom15 = "28";
            Settings.set(Settings.rom15, Switches.rom15);
            UPPER_F.setSelectedItem(UpperROMs[28]);
          }
      }
          dlg.dispose();
      romsetter.setVisible(true);
  }
  public void putRoms(){
                    if (!LOWER_R.getSelectedItem().toString().equals("custom"))
                    Switches.LOWER = LOWER_R.getSelectedItem().toString();
                    if (!UPPER_0.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_0 = UPPER_0.getSelectedItem().toString();
                    if (!UPPER_1.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_1 = UPPER_1.getSelectedItem().toString();
                    if (!UPPER_2.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_2 = UPPER_2.getSelectedItem().toString();
                    if (!UPPER_3.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_3 = UPPER_3.getSelectedItem().toString();
                    if (!UPPER_4.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_4 = UPPER_4.getSelectedItem().toString();
                    if (!UPPER_5.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_5 = UPPER_5.getSelectedItem().toString();
                    if (!UPPER_6.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_6 = UPPER_6.getSelectedItem().toString();
                    if (!UPPER_7.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_7 = UPPER_7.getSelectedItem().toString();
                    if (!UPPER_8.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_8 = UPPER_8.getSelectedItem().toString();
                    if (!UPPER_9.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_9 = UPPER_9.getSelectedItem().toString();
                    if (!UPPER_A.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_A = UPPER_A.getSelectedItem().toString();
                    if (!UPPER_B.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_B = UPPER_B.getSelectedItem().toString();
                    if (!UPPER_C.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_C = UPPER_C.getSelectedItem().toString();
                    if (!UPPER_D.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_D = UPPER_D.getSelectedItem().toString();
                    if (!UPPER_E.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_E = UPPER_E.getSelectedItem().toString();
                    if (!UPPER_F.getSelectedItem().toString().equals("custom"))
                    Switches.UPPER_F = UPPER_F.getSelectedItem().toString();
                    Switches.romlow = Integer.toString(LOWER_R.getSelectedIndex());
                    Switches.rom0 = Integer.toString(UPPER_0.getSelectedIndex());
                    Switches.rom1 = Integer.toString(UPPER_1.getSelectedIndex());
                    Switches.rom2 = Integer.toString(UPPER_2.getSelectedIndex());
                    Switches.rom3 = Integer.toString(UPPER_3.getSelectedIndex());
                    Switches.rom4 = Integer.toString(UPPER_4.getSelectedIndex());
                    Switches.rom5 = Integer.toString(UPPER_5.getSelectedIndex());
                    Switches.rom6 = Integer.toString(UPPER_6.getSelectedIndex());
                    Switches.rom7 = Integer.toString(UPPER_7.getSelectedIndex());
                    Switches.rom8 = Integer.toString(UPPER_8.getSelectedIndex());
                    Switches.rom9 = Integer.toString(UPPER_9.getSelectedIndex());
                    Switches.rom10 = Integer.toString(UPPER_A.getSelectedIndex());
                    Switches.rom11 = Integer.toString(UPPER_B.getSelectedIndex());
                    Switches.rom12 = Integer.toString(UPPER_C.getSelectedIndex());
                    Switches.rom13 = Integer.toString(UPPER_D.getSelectedIndex());
                    Switches.rom14 = Integer.toString(UPPER_E.getSelectedIndex());
                    Switches.rom15 = Integer.toString(UPPER_F.getSelectedIndex());

                    Settings.set(Settings.romlow, Switches.romlow);
                    Settings.set(Settings.rom0, Switches.rom0);
                    Settings.set(Settings.rom1, Switches.rom1);
                    Settings.set(Settings.rom2, Switches.rom2);
                    Settings.set(Settings.rom3, Switches.rom3);
                    Settings.set(Settings.rom4, Switches.rom4);
                    Settings.set(Settings.rom5, Switches.rom5);
                    Settings.set(Settings.rom6, Switches.rom6);
                    Settings.set(Settings.rom7, Switches.rom7);
                    Settings.set(Settings.rom8, Switches.rom8);
                    Settings.set(Settings.rom9, Switches.rom9);
                    Settings.set(Settings.rom10, Switches.rom10);
                    Settings.set(Settings.rom11, Switches.rom11);
                    Settings.set(Settings.rom12, Switches.rom12);
                    Settings.set(Settings.rom13, Switches.rom13);
                    Settings.set(Settings.rom14, Switches.rom14);
                    Settings.set(Settings.rom15, Switches.rom15);

                StoreRoms();

      jemu.ui.JEMU.setRoms = true;
  }


  public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyroms){
            romsetter.dispose();
            putRoms();
        }

        if (e.getSource() == okroms){
          //  putRoms();
            romsetter.dispose();
        }

        if (e.getSource() == lload){
            romsetter.dispose();
            loadLow();
        }
        if (e.getSource() == u0load){
            romsetter.dispose();
            loadUpper(0);
        }
        if (e.getSource() == u1load){
            romsetter.dispose();
            loadUpper(1);
        }
        if (e.getSource() == u2load){
            romsetter.dispose();
            loadUpper(2);
        }
        if (e.getSource() == u3load){
            romsetter.dispose();
            loadUpper(3);
        }
        if (e.getSource() == u4load){
            romsetter.dispose();
            loadUpper(4);
        }
        if (e.getSource() == u5load){
            romsetter.dispose();
            loadUpper(5);
        }
        if (e.getSource() == u6load){
            romsetter.dispose();
            loadUpper(6);
        }
        if (e.getSource() == u7load){
            romsetter.dispose();
            loadUpper(7);
        }
        if (e.getSource() == u8load){
            romsetter.dispose();
            loadUpper(8);
        }
        if (e.getSource() == u9load){
            romsetter.dispose();
            loadUpper(9);
        }
        if (e.getSource() == uAload){
            romsetter.dispose();
            loadUpper(10);
        }
        if (e.getSource() == uBload){
            romsetter.dispose();
            loadUpper(11);
        }
        if (e.getSource() == uCload){
            romsetter.dispose();
            loadUpper(12);
        }
        if (e.getSource() == uDload){
            romsetter.dispose();
            loadUpper(13);
        }
        if (e.getSource() == uEload){
            romsetter.dispose();
            loadUpper(14);
        }
        if (e.getSource() == uFload){
            romsetter.dispose();
            loadUpper(15);
        }
  }

  private GridBagConstraints getGridBagConstraints(int x, int y, double weightx,double weighty,
    int width, int fill)
  {
    if (this.Constraints == null) {
      this.Constraints = new GridBagConstraints();
    }
    this.Constraints.gridx = x;
    this.Constraints.gridy = y;
    this.Constraints.weightx = weightx;
    this.Constraints.weighty = weighty;
    this.Constraints.gridwidth = width;
    this.Constraints.fill = fill;
    return this.Constraints;
  }

}
