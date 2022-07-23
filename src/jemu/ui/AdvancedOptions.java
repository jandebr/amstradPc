/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.ui;

/**
* Title:        JavaCPC
* Description:  The Java CPC Emulator
* Copyright:    Copyright (c) 2004-2009
* Company:
* @author:      Markus
*/

import javax.swing.*;
import javax.swing.border.*;

import jemu.settings.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import jemu.core.Util;

import javax.swing.JSlider;
import javax.swing.event.*;

/**
 *
 * @author Markus
 */
public class AdvancedOptions implements ActionListener{
    protected boolean DIAGNOSE = true
            ;
  final Hashtable labels = new Hashtable ();
  final Hashtable blabels = new Hashtable ();
  final Hashtable leftlabels = new Hashtable ();
  final Hashtable rightlabels = new Hashtable ();
  protected GridBagConstraints Constraints   = null;
  protected DKnob volumeslider    = new DKnob("AY-Volume");
  protected DKnob digiblastervolumeslider    = new DKnob("Digiblaster");
  protected ButtonGroup group = new ButtonGroup();
  protected JLabel amps = new JLabel(" AY amplitude tables: ");
  protected JLabel vum = new JLabel("      VU - Level ");
  protected JRadioButton KAYOut    = new JRadioButton("Hacker KAY");
  protected JRadioButton VSoftOut  = new JRadioButton("V_Soft");
  protected JRadioButton CPCE95Out = new JRadioButton("CPCe95");
  protected JCheckBox ayeffect = new JCheckBox("Method B");
  protected JCheckBox linear = new JCheckBox("Linear sound");
  public static JSlider leftvumeter         = new JSlider();
  public static JSlider rightvumeter         = new JSlider();
  protected JSlider vholdslider         = new JSlider();
  protected JSlider diagnoseslider         = new JSlider();
  protected JSlider brightslider         = new JSlider();
  protected JSlider fireslider         = new JSlider();
    Frame options;
    public static boolean Options = false;
    JButton ok = new JButton("OK");
    JButton cheat = new JButton("Devil");

     public void OptionPanel(){
         Switches.Blastervolume = Integer.parseInt(Settings.get(Settings.DBVOLUME, "9"))*10;
        Switches.volume = (double)Integer.parseInt(Settings.get(Settings.VOLUME, "1000"))/1000;
      Options = true;
      options = new Frame(){

     protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            Options =  false;
            options.dispose();
        }
      }

      public synchronized void setTitle(String title) {
        super.setTitle(title);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      }

 };
    //options.setResizable(false);
    options.setTitle("Options");
    options.setLayout(new GridBagLayout());
    options.setBackground(Color.DARK_GRAY);
    options.setAlwaysOnTop(true);
    JLabel info1 = new JLabel("  V-hold  ");
    JLabel info2 = new JLabel(" AY-Chip Volume ");
    JLabel info3 = new JLabel("  Left  ");
    JLabel info4 = new JLabel("  Right  ");
    JLabel info5 = new JLabel(" Blaster Volume ");
    JLabel info6 = new JLabel(" Brightness  ");
    JLabel info7 = new JLabel(" Autofire  ");
    ok.addActionListener(this);
    cheat.addActionListener(this);
    info1.setBackground(Color.DARK_GRAY);
    info2.setBackground(Color.DARK_GRAY);
    info3.setBackground(Color.DARK_GRAY);
    info4.setBackground(Color.DARK_GRAY);
    info5.setBackground(Color.DARK_GRAY);
    info6.setBackground(Color.DARK_GRAY);
    info7.setBackground(Color.DARK_GRAY);
    ayeffect.setBackground(Color.DARK_GRAY);
    linear.setBackground(Color.DARK_GRAY);
    info1.setForeground(Color.LIGHT_GRAY);
    info2.setForeground(Color.LIGHT_GRAY);
    info3.setForeground(Color.LIGHT_GRAY);
    info4.setForeground(Color.LIGHT_GRAY);
    info5.setForeground(Color.LIGHT_GRAY);
    info6.setForeground(Color.LIGHT_GRAY);
    info7.setForeground(Color.LIGHT_GRAY);
    VSoftOut.setBackground(Color.DARK_GRAY);
    VSoftOut.setForeground(Color.LIGHT_GRAY);
    VSoftOut.setFocusable(false);
    CPCE95Out.setBackground(Color.DARK_GRAY);
    CPCE95Out.setForeground(Color.LIGHT_GRAY);
    CPCE95Out.setFocusable(false);
    KAYOut.setBackground(Color.DARK_GRAY);
    KAYOut.setForeground(Color.LIGHT_GRAY);
    vum.setForeground(Color.LIGHT_GRAY);
    amps.setForeground(Color.LIGHT_GRAY);
    KAYOut.setFocusable(false);
    ayeffect.setForeground(Color.LIGHT_GRAY);
    ayeffect.setFocusable(false);
    linear.setForeground(Color.LIGHT_GRAY);
    linear.setFocusable(false);
    ok.setBackground(Color.LIGHT_GRAY);
    ok.setForeground(Color.DARK_GRAY);
    ok.setBorder(new BevelBorder(BevelBorder.RAISED));
    cheat.setBackground(Color.DARK_GRAY);
    cheat.setForeground(Color.DARK_GRAY);
    cheat.setBorder(null);
    cheat.setFocusable(false);

      options.add(info1, getGridBagConstraints(0, 0, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      options.add(vholdslider, getGridBagConstraints(0, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      if (DIAGNOSE)
      options.add(diagnoseslider, getGridBagConstraints(5, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      options.add(info6, getGridBagConstraints(1, 0, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      options.add(brightslider, getGridBagConstraints(1, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      options.add(info2, getGridBagConstraints(0, 4, 0.0, 0.0, 2, GridBagConstraints.BOTH));
      options.add(volumeslider, getGridBagConstraints(0, 5, 0.0, 0.0, 2, GridBagConstraints.BOTH));
      options.add(info5, getGridBagConstraints(2, 4, 0.0, 0.0, 2, GridBagConstraints.BOTH));
      options.add(digiblastervolumeslider, getGridBagConstraints(2, 5, 0.0, 0.0, 2, GridBagConstraints.BOTH));
      options.add(info7, getGridBagConstraints(2, 0, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      options.add(fireslider, getGridBagConstraints(2, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));

      options.add(amps, getGridBagConstraints(0, 6, 1.0, 1.0, 2, GridBagConstraints.BOTH));
      options.add(CPCE95Out, getGridBagConstraints(2, 6, 1.0, 1.0, 2, GridBagConstraints.BOTH));
      options.add(VSoftOut, getGridBagConstraints(0, 7, 1.0, 1.0, 2, GridBagConstraints.BOTH));
      options.add(KAYOut, getGridBagConstraints(2, 7, 1.0, 1.0, 2, GridBagConstraints.BOTH));
      options.add(ayeffect, getGridBagConstraints(0, 10, 0.0, 0.0, 2, GridBagConstraints.BOTH));
      options.add(linear, getGridBagConstraints(2, 10, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      options.add(vum, getGridBagConstraints(3, 0, 0.0, 0.0, 2, GridBagConstraints.BOTH));
      options.add(leftvumeter, getGridBagConstraints(3, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      options.add(rightvumeter, getGridBagConstraints(4, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      options.add(ok, getGridBagConstraints(3, 10, 1.0, 1.0, 2, GridBagConstraints.BOTH));
      options.add(cheat, getGridBagConstraints(5, 10, 1.0, 1.0, 1, GridBagConstraints.BOTH));
      options.pack();
      options.setVisible(true);
      options.setResizable(false);
      }

   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == ok){
          options.dispose();
          Options=false;
      }
      if (e.getSource() == cheat){
          Switches.devil = true;
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
   @SuppressWarnings("unchecked")
public void init(){
    try {
        group.add(CPCE95Out);
        group.add(VSoftOut);
        group.add(KAYOut);
        Switches.volume = (double)Integer.parseInt(Settings.get(Settings.VOLUME, "1000"))/1000;
        volumeslider.setValue((float)Switches.volume);
        System.out.println("Volume is set to:" + volumeslider.getValue());
        volumeslider.setPreferredSize(new Dimension(110, 110));
        volumeslider.setBackground(Color.DARK_GRAY);
        volumeslider.setForeground(new Color(255, 255, 255));
        volumeslider.setBorder(new EtchedBorder());
        volumeslider.setDoubleBuffered(false);
        volumeslider.addChangeListener (new ChangeListener () {

          public void stateChanged (ChangeEvent e) {
              Switches.volume = volumeslider.getValue ();
              Settings.set(Settings.VOLUME, ""+(int)(Switches.volume*1000));
             }
          });

          VSoftOut.addItemListener (new ItemListener () {
          public void itemStateChanged (ItemEvent e){
              if (e.getSource() == VSoftOut)
                  if (e.getStateChange() == ItemEvent.DESELECTED){
                      Switches.VSoftOutput = false;
                  Settings.setBoolean(Settings.VSOFT, false);}
                  else{
                      Switches.VSoftOutput = true;
                  Settings.setBoolean(Settings.VSOFT, true);
                  }
          }
          });
          CPCE95Out.addItemListener (new ItemListener () {
          public void itemStateChanged (ItemEvent e){
              if (e.getSource() == CPCE95Out)
                  if (e.getStateChange() == ItemEvent.DESELECTED){
                      Switches.CPCE95 = false;
                  Settings.setBoolean(Settings.CPCE95, false);}
                  else{
                      Switches.CPCE95 = true;
                  Settings.setBoolean(Settings.CPCE95, true);
                  }
          }
          });

          KAYOut.addItemListener (new ItemListener () {
          public void itemStateChanged (ItemEvent e){
              if (e.getSource() == KAYOut)
                  if (e.getStateChange() == ItemEvent.DESELECTED){
                      Switches.KAYOut = false;
                  Settings.setBoolean(Settings.KAYOut, false);}
                  else{
                      Switches.KAYOut = true;
                  Settings.setBoolean(Settings.KAYOut, true);
                  }
          }
          });

          ayeffect.addItemListener (new ItemListener () {
          public void itemStateChanged (ItemEvent e){
              if (e.getSource() == ayeffect)
                  if (e.getStateChange() == ItemEvent.DESELECTED){
                      Switches.ayeffect = false;
                  Settings.setBoolean(Settings.AYEFFECT, false);}
                  else{
                      Switches.ayeffect = true;
                  Settings.setBoolean(Settings.AYEFFECT, true);
                  }
          }
          });
          linear.addItemListener (new ItemListener () {
          public void itemStateChanged (ItemEvent e){
              if (e.getSource() == linear)
                  if (e.getStateChange() == ItemEvent.DESELECTED){
                      Switches.linear = false;
                      KAYOut.setEnabled(true);
                      VSoftOut.setEnabled(true);
                      CPCE95Out.setEnabled(true);
                  Settings.setBoolean(Settings.LINEAR, false);}
                  else{
                      Switches.linear = true;
                      KAYOut.setEnabled(false);
                      VSoftOut.setEnabled(false);
                      CPCE95Out.setEnabled(false);
                  Settings.setBoolean(Settings.LINEAR, true);
                  }
          }
          });

          Switches.Blastervolume = Integer.parseInt(Settings.get(Settings.DBVOLUME, "9"))*10;
        digiblastervolumeslider.setValue((float)Integer.parseInt(Settings.get(Settings.DBVOLUME, "9"))/9);
        digiblastervolumeslider.setPreferredSize(new Dimension(110, 110));
        digiblastervolumeslider.setBackground(Color.DARK_GRAY);
        digiblastervolumeslider.setForeground(new Color(255, 255, 255));
        digiblastervolumeslider.setBorder(new EtchedBorder());
        digiblastervolumeslider.setDoubleBuffered(false);
       digiblastervolumeslider.addChangeListener (new ChangeListener () {

          public void stateChanged (ChangeEvent e) {
              int dbvolume = (int)(digiblastervolumeslider.getValue()*9);
              Switches.Blastervolume = dbvolume*10;
              Settings.set(Settings.DBVOLUME, ""+dbvolume);
          }
       });

        vholdslider.setMinimum(0);
        vholdslider.setMaximum(70);
        vholdslider.setValue(Util.hexValue(Settings.get(Settings.VHOLD, "23")));
        Switches.vhold = vholdslider.getValue();
        vholdslider.setOrientation(JSlider.VERTICAL);
        vholdslider.setPaintTicks(true);
        vholdslider.setPaintLabels(false);
        vholdslider.setPaintTrack(true);
        vholdslider.setSnapToTicks(true);
        vholdslider.setMajorTickSpacing(5);
        vholdslider.setPreferredSize(new Dimension(12, 130));
        vholdslider.setBackground(Color.DARK_GRAY);
        vholdslider.setForeground(new Color(255, 255, 255));
        vholdslider.setBorder(new EtchedBorder());
              vholdslider.addChangeListener (new ChangeListener () {
          private int lastValue = 0;

          public void stateChanged (ChangeEvent e) {
            lastValue = vholdslider.getValue ();
             Switches.vhold = lastValue;
             Settings.set(Settings.VHOLD, Util.hex(lastValue));
            // System.out.println(lastValue);
             vholdslider.repaint ();
             }
          });

        diagnoseslider.setMinimum(0);
        diagnoseslider.setMaximum(255);
        diagnoseslider.setValue(Util.hexValue(Settings.get(Settings.DIAGNOSE, "0")));
        Switches.diagnose = diagnoseslider.getValue();
        diagnoseslider.setOrientation(JSlider.VERTICAL);
        diagnoseslider.setPaintTicks(true);
        diagnoseslider.setPaintLabels(false);
        diagnoseslider.setPaintTrack(true);
        diagnoseslider.setSnapToTicks(true);
        diagnoseslider.setMajorTickSpacing(1);
        diagnoseslider.setPreferredSize(new Dimension(12, 130));
        diagnoseslider.setBackground(Color.DARK_GRAY);
        diagnoseslider.setForeground(new Color(255, 255, 255));
        diagnoseslider.setBorder(new EtchedBorder());
              diagnoseslider.addChangeListener (new ChangeListener () {
          private int lastValue = 0;

          public void stateChanged (ChangeEvent e) {
            lastValue = diagnoseslider.getValue ();
             Switches.diagnose = lastValue;
             System.out.println("diagnostic = " + Util.hex(lastValue));
             Settings.set(Settings.DIAGNOSE, Util.hex(lastValue));
             diagnoseslider.repaint ();
             }
          });


        fireslider.setMinimum(2);
        fireslider.setMaximum(12);
        fireslider.setValue(Util.hexValue(Settings.get(Settings.FIRETIMER, "02")));
        jemu.system.cpc.CPC.timefire = fireslider.getValue();
        fireslider.setOrientation(JSlider.VERTICAL);
        fireslider.setPaintTicks(true);
        fireslider.setPaintLabels(false);
        fireslider.setPaintTrack(true);
        fireslider.setSnapToTicks(true);
        fireslider.setMajorTickSpacing(1);
        fireslider.setPreferredSize(new Dimension(12, 130));
        fireslider.setBackground(Color.DARK_GRAY);
        fireslider.setForeground(new Color(255, 255, 255));
        fireslider.setBorder(new EtchedBorder());
              fireslider.addChangeListener (new ChangeListener () {
          private int lastValue = 0;

          public void stateChanged (ChangeEvent e) {
            lastValue = fireslider.getValue ();
             jemu.system.cpc.CPC.timefire = lastValue;
             Settings.set(Settings.FIRETIMER, Util.hex(lastValue));
             fireslider.repaint ();
             }
          });



        brightslider.setMinimum(-250);
        brightslider.setMaximum(250);
        brightslider.setValue(Util.hexValue(Settings.get(Settings.BRIGHTNESS, "0")));
        Display.setFade(brightslider.getValue());
        brightslider.setOrientation(JSlider.VERTICAL);    //stellt den Schieberegler horizontal auf
        brightslider.setInverted(true);
        brightslider.setPaintTicks(true);
        brightslider.setPaintLabels(false);
        brightslider.setPaintTrack(true);
        brightslider.setSnapToTicks(true);
        brightslider.setMajorTickSpacing(25);
        brightslider.setPreferredSize(new Dimension(12, 130));
        brightslider.setBackground(Color.DARK_GRAY);
        brightslider.setForeground(new Color(255, 255, 255));
        brightslider.setBorder(new EtchedBorder());
        brightslider.addChangeListener (new ChangeListener () {
          private int lastValue = 0;

          public void stateChanged (ChangeEvent e) {
            lastValue = brightslider.getValue ();
             Display.setFade(lastValue);
             Settings.set(Settings.BRIGHTNESS, Util.hex(lastValue));
             brightslider.repaint ();
             }
          });

        leftvumeter.setMinimum(0);
        leftvumeter.setMaximum(128);
        leftvumeter.setValue(0);
        leftvumeter.setOrientation(JSlider.VERTICAL);
        leftvumeter.setPaintTicks(false);
        leftvumeter.setPaintLabels(true);
        leftvumeter.setPaintTrack(false);
        leftvumeter.setMajorTickSpacing(15);
       // leftvumeter.setPreferredSize(new Dimension(250, 26));
        leftvumeter.setBackground(Color.DARK_GRAY);
        leftvumeter.setForeground(new Color(255, 255, 255));
        leftvumeter.setBorder(null);
        leftvumeter.setDoubleBuffered(true);
        leftvumeter.setEnabled(false);
                for (int i = 0; i < leftvumeter.getMaximum (); i++)
            leftlabels.put (new Integer (i), new ColoredComponent (i < leftvumeter.getMaximum () / 2 ? Color.green : i < leftvumeter.getMaximum () * 3/4 ? Color.yellow : Color.red));
            leftvumeter.setLabelTable (leftlabels);

        leftvumeter.addChangeListener (new ChangeListener () {
          private int lastValue = 0;

          public void stateChanged (ChangeEvent e) {
             if (leftvumeter.getValue () < lastValue)
                for (int i = leftvumeter.getValue (); i < lastValue; i++)
                    ((JComponent) leftlabels.get (new Integer (i))).setOpaque (false);
                else for (int i = lastValue; i < leftvumeter.getValue (); i++)
                         ((JComponent) leftlabels.get (new Integer (i))).setOpaque (true);
             lastValue = leftvumeter.getValue ();
             leftvumeter.repaint ();
             }
          });

        rightvumeter.setMinimum(0);
        rightvumeter.setMaximum(128);
        rightvumeter.setValue(0);
        rightvumeter.setOrientation(JSlider.VERTICAL);
        rightvumeter.setPaintTicks(false);
        rightvumeter.setPaintLabels(true);
        rightvumeter.setPaintTrack(false);
        rightvumeter.setMajorTickSpacing(15);
      //  rightvumeter.setPreferredSize(new Dimension(250, 26));
        rightvumeter.setBackground(Color.DARK_GRAY);
        rightvumeter.setForeground(new Color(255, 255, 255));
        rightvumeter.setBorder(null);
        rightvumeter.setDoubleBuffered(true);
        rightvumeter.setEnabled(false);
                for (int i = 0; i < rightvumeter.getMaximum (); i++)
            rightlabels.put (new Integer (i), new ColoredComponent (i < rightvumeter.getMaximum () / 2 ? Color.green : i < rightvumeter.getMaximum () * 3/4 ? Color.yellow : Color.red));
            rightvumeter.setLabelTable (rightlabels);

        rightvumeter.addChangeListener (new ChangeListener () {
          private int lastValue = 0;

          public void stateChanged (ChangeEvent e) {
             if (rightvumeter.getValue () < lastValue)
                for (int i = rightvumeter.getValue (); i < lastValue; i++)
                    ((JComponent) rightlabels.get (new Integer (i))).setOpaque (false);
                else for (int i = lastValue; i < rightvumeter.getValue (); i++)
                         ((JComponent) rightlabels.get (new Integer (i))).setOpaque (true);

             lastValue = rightvumeter.getValue ();
             rightvumeter.repaint ();
             }
          });

      Switches.KAYOut = Settings.getBoolean(Settings.KAYOut, true);
      KAYOut.setSelected(Switches.KAYOut);
      Switches.VSoftOutput = Settings.getBoolean(Settings.VSOFT, false);
      VSoftOut.setSelected(Switches.VSoftOutput);
      Switches.CPCE95 = Settings.getBoolean(Settings.CPCE95, false);
      CPCE95Out.setSelected(Switches.CPCE95);
      Switches.ayeffect = Settings.getBoolean(Settings.AYEFFECT, true);
      ayeffect.setSelected(Switches.ayeffect);
      Switches.linear = Settings.getBoolean(Settings.LINEAR, false);
      linear.setSelected(Switches.linear);
    } 
    catch (Exception e){}
   }
      class ColoredComponent extends JLabel {

     ColoredComponent (Color color) {
        setOpaque (false);
        setBackground (color);
        Dimension d = new Dimension (10, 6);
        setPreferredSize (d);
        setMaximumSize (d);
        setMinimumSize (d);
        setBorder (new LineBorder (Color.BLACK));
        setBorder (null);
        setDoubleBuffered (false);
        }
     }
}
