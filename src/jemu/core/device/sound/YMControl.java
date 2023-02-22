/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.core.device.sound;

/**
 *
 * @author Markus
 */
import jemu.system.cpc.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import jemu.core.Util;

public class YMControl extends Frame implements WindowListener{
public static int displaycount1 = 0;
public static int displaycount2 = 0;
protected static int displaycount3 = 0;
public static int DisplayStart , DisplayEnd = 0;
public static String Monitor = "";
static String dot = ":";

        protected Font font;
    protected boolean playing = false;
      public JButton btnREC = new JButton(" Rec ");
      public JButton btnPLAY = new JButton(" Play ");
      public JButton btnLOAD = new JButton(" Open ");
      public JButton btnSTOP = new JButton(" Stop ");
      public JButton btnSAVE = new JButton(" Save ");
      public static int counter = 0;
      public static JButton YM_Counter = new JButton(" 00:00\"00 ");
      
      
public void windowClosing(WindowEvent e) {
      this.setVisible(false);
}
public void windowClosed(WindowEvent e) {
      this.setVisible(false);
}
public void windowDeactivated(WindowEvent e) {
}
public void windowActivated(WindowEvent e) {
}
public void windowDeiconified(WindowEvent e) {
    
}
public void windowOpened(WindowEvent e) {
    
}
public void windowIconified(WindowEvent e) {
    
}

   public YMControl() {


      this.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));

      btnPLAY.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (!CPC.YM_Rec){
                 //jemu.ui.Display.skin = Util.random(1) + 1;
                 if (jemu.ui.Display.skin == 1)
                     jemu.ui.Display.skin = 2;
                 else
                 if (jemu.ui.Display.skin == 2)
                     jemu.ui.Display.skin = 1;
             CPC.YM_Minutes = 0;
             CPC.YM_Seconds = 0;
             displaycount1 = 0;
             displaycount2 = 0;
             DisplayStart = 0;
             CPC.ymcount = 0;
             CPC.YM_Play = true;
             CPC.YM_Rec = false;
             YM_Counter.setText(" 00:00\"00 ");
      btnPLAY.setBorder(new BevelBorder(BevelBorder.LOWERED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBackground(new Color(0xff,0x00,0x00));
      btnPLAY.setBackground(Color.BLACK);
      YM_Counter.setBackground(new Color (0x68,0x7F,0x85));
      YM_Counter.setForeground(new Color (0x18,0x2F,0x35));

      btnREC.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.LIGHT_GRAY);
             }
         }
      });
      btnREC.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (!CPC.YM_Play){
                 CPC.doRecord = false;
             displaycount1 = 0;
             displaycount2 = 0;
             DisplayStart = 0;
                 CPC.YM_registers = 16;
                 CPC.atari_st_mode = false;
                 CPC.oldYM = false;
                 CPC.YM_Minutes = 0;
                 CPC.YM_Seconds = 0;
                 CPC.ymcount = 0;
                 CPC.YM_RecCount = 0;
                 CPC.YM_vbl = 0;
                 CPC.YM_Play = false;
                 CPC.YM_Rec = true;
                 YM_Counter.setText("PAUSED");

      btnPLAY.setBorder(new BevelBorder(BevelBorder.LOWERED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.LOWERED));
      btnREC.setBackground(new Color(0x80,0x00,0x00));
      btnPLAY.setBackground(Color.BLACK);
      YM_Counter.setBackground(new Color (0x88,0x00,0x00));
      YM_Counter.setForeground(new Color (0xff,0x00,0x00));

      btnREC.setForeground(Color.LIGHT_GRAY);
      btnPLAY.setForeground(Color.LIGHT_GRAY);
             }
         }
      });

      btnSTOP.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             CPC.YM_Minutes = 0;
             CPC.YM_Seconds = 0;
             CPC.ymcount = 0;
             CPC.YM_Play = false;
             CPC.YM_Rec = false;
             CPC.YM_Stop = true;
             displaycount1 = 0;
             displaycount2 = 0;
             DisplayStart = 0;
             YM_Counter.setText(" 00:00\"00 ");
      btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBackground(new Color(0xff,0x00,0x00));
      btnPLAY.setBackground(Color.DARK_GRAY);
      btnREC.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.WHITE);
      YM_Counter.setBackground(new Color (0x68,0x7F,0x85));
      YM_Counter.setForeground(new Color (0x58,0x6F,0x75));
         }
      });
      btnLOAD.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (!CPC.YM_Rec && !CPC.YM_Play){
             CPC.YM_Load = true;
      btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBackground(new Color(0xff,0x00,0x00));
      btnPLAY.setBackground(Color.DARK_GRAY);
      btnREC.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.WHITE);
             }
         }
      });
      btnSAVE.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (!CPC.YM_Rec && !CPC.YM_Play){
             CPC.YM_Save = true;
      btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBackground(new Color(0xff,0x00,0x00));
      btnPLAY.setBackground(Color.DARK_GRAY);
      btnREC.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.WHITE);
             }
         }
      });

      this.add(YM_Counter);

      this.add(btnREC);
      this.add(btnPLAY);
      this.add(btnSTOP);
      this.add(btnLOAD);
      this.add(btnSAVE);
      btnLOAD.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      YM_Counter.setBorder(new BevelBorder(BevelBorder.LOWERED));
      btnSAVE.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnLOAD.setFocusable(false);
      btnPLAY.setFocusable(false);
      btnSTOP.setFocusable(false);
      btnSAVE.setFocusable(false);
      YM_Counter.setFocusable(false);
      InputStream in = getClass().getResourceAsStream("tran.ttf");
                try {
                font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(0, 54);
                YM_Counter.setFont(this.font);
                }
                catch (Exception e) {
                    YM_Counter.setFont(new Font ("Courier",1,40));
                }
      YM_Counter.setDoubleBuffered(false);
      btnREC.setBackground(new Color(0xff,0x00,0x00));
      btnLOAD.setBackground(Color.DARK_GRAY);
      btnPLAY.setBackground(Color.DARK_GRAY);
      btnSTOP.setBackground(Color.DARK_GRAY);
      btnSAVE.setBackground(Color.DARK_GRAY);
      btnREC.setForeground(Color.WHITE);
      btnLOAD.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.WHITE);
      btnSTOP.setForeground(Color.WHITE);
      YM_Counter.setBackground(new Color (0x68,0x7F,0x85));
      YM_Counter.setForeground(new Color (0x58,0x6F,0x75));
      btnSAVE.setForeground(Color.WHITE);
      this.setForeground(Color.LIGHT_GRAY);
      this.setBackground(Color.DARK_GRAY);
      this.setTitle("YM-control");
      this.pack();
      this.setSize(224,186);
      this.setResizable(false);
      this.setAlwaysOnTop(true);
      this.setVisible(false);
        this.addWindowListener(this);
   }

  public static void doYMDisplay(String minutes, String seconds, String milliseconds, String YMtitle, String YMauthor, String YMcreator){
      String dispText = "        " + YMtitle + " - " + YMauthor + " - " + YMcreator + "        ";
      displaycount1++;
      if (displaycount1 >= 500){
          displaycount2++;
          if (displaycount2 >= 10){
              DisplayEnd = dispText.length();
              YM_Counter.setText(dispText.substring( DisplayStart, DisplayStart+7 ));
              DisplayStart++;
              if (DisplayStart > (DisplayEnd - 7))
                  DisplayStart = 0;
              displaycount2=0;
              if (displaycount1 >= (500 + ((DisplayEnd-7)*10))){
                  displaycount1=0;
              }
          }
      }
      else
          YM_Counter.setText(" " + minutes + dot + seconds + "\"" + milliseconds + " ");
          Monitor = " " + minutes + ":" + seconds + "\"" + milliseconds + " ";
          displaycount3++;
          if (displaycount3 >= 50)
              dot=".";
          if (displaycount3 >=100){
              dot=":";
              displaycount3 = 0;
          }
  }
}