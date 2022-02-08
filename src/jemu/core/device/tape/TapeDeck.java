/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.core.device.tape;

/**
 *
 * @author Markus
 */
import jemu.system.cpc.*;
import jemu.core.samples.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import jemu.ui.Switches;
import java.net.URL;
import jemu.settings.Settings;

public class TapeDeck extends Frame implements WindowListener{

    public int WAVBYTE = 0;
    private int update=0;

    GraphicDisplay gfx = new GraphicDisplay();

  final URL tapeemp = getClass().getResource("image/tapea.png");
  final ImageIcon tapeempty = new ImageIcon(tapeemp);
  final URL tapef = getClass().getResource("image/tapeb.png");
  final ImageIcon tape = new ImageIcon(tapef);
  final URL recb = getClass().getResource("image/but_rec.png");
  final ImageIcon recbu = new ImageIcon(recb);
  final URL rewb = getClass().getResource("image/but_rew.png");
  final ImageIcon rewbu = new ImageIcon(rewb);
  final URL ffb = getClass().getResource("image/but_ff.png");
  final ImageIcon ffbu = new ImageIcon(ffb);
  final URL playb = getClass().getResource("image/but_play.png");
  final ImageIcon playbu = new ImageIcon(playb);
  final URL stopb = getClass().getResource("image/but_stop.png");
  final ImageIcon stopbu = new ImageIcon(stopb);
  final URL pauseb = getClass().getResource("image/but_pause.png");
  final ImageIcon pausebu = new ImageIcon(pauseb);
  final JLabel tapeDeck = new JLabel(tape);
      public static Color CPCGRAY = new Color(0x37,0x37,0x37);
      public boolean buttonpressed;
      public static boolean isMem;

      public boolean paused, played, recorded = false;

    protected boolean playing = false;
     public  JButton btnREC = new JButton(recbu);
     public JButton btnREW = new JButton(rewbu);
     public JButton btnPLAY = new JButton(playbu);
     public JButton btnFF = new JButton(ffbu);
      JButton btnSTOP = new JButton(stopbu);
      JButton btnPAUSE = new JButton(pausebu);
      JButton reset = new JButton(" ");
      JLabel blanker = new JLabel("              ");
      JLabel blanker2 = new JLabel("         ");
      JButton trueaudio = new JButton("▄");
      public static int counter = 0;
      public static String before = "000";
      public static JTextField TapeCounter = new JTextField(before + counter);
      public static JTextField TapeMemory = new JTextField("0000");
      public JButton memory = new JButton("Mem");
      public static int memCount = 0;
      public static JSlider positionslider = new JSlider();

public void windowClosing(WindowEvent e) {
    CPC.tapedeck = false;
    this.setVisible(false);
}
public void windowClosed(WindowEvent e) {
    CPC.tapedeck = false;
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
   public TapeDeck() {

       positionslider.setMinimum(0);    //stellt den Minimalwert auf 0 ein
        positionslider.setMaximum(0);  //stellt den Maximalwert auf 4 ein
        positionslider.setValue(0);     //selektiert den Wert von outvolume
        positionslider.setOrientation(JSlider.HORIZONTAL);    //stellt den Schieberegler horizontal auf
        positionslider.setPaintTicks(false);    //Striche werden angezeigt
        positionslider.setPaintLabels(false);  //Zahlen werden nicht angezeigt
        positionslider.setPaintTrack(false);    //Balken wird angezeigt
        positionslider.setPreferredSize(new Dimension(247, 7));
        positionslider.setBackground(Color.DARK_GRAY);
        positionslider.setBorder(new EtchedBorder());
        positionslider.setFocusable(false);
        positionslider.setMajorTickSpacing(2);
        positionslider.setSnapToTicks(false);
        positionslider.setDoubleBuffered(true);

        positionslider.addChangeListener (new ChangeListener () {
          private int lastValue = 0;

          public void stateChanged (ChangeEvent e) {
             lastValue = positionslider.getValue ();
             CPC.number = lastValue;
             update++;
             if (update >=3){
                 gfx.paintWAV();
                 update = 0;
             }
             counter = CPC.number / (2080000 / (byte)CPC.tape_delay);
      if (TapeDeck.counter <=9999)
          TapeDeck.before = "";
      if (TapeDeck.counter <=999)
          TapeDeck.before = "0";
      if (TapeDeck.counter <=99)
          TapeDeck.before = "00";
      if (TapeDeck.counter <=9)
          TapeDeck.before = "000";
             TapeCounter.setText(before + counter);
             positionslider.repaint ();
             }
          });
      Samples.volume = Samples.Volume.HIGH;  // un-mute

      this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 4));
      btnREW.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
       showText();
             buttonpressed = true;
             if (!CPC.rec){
             if (Switches.FloppySound){
                 if (CPC.ffwd)
                Samples.TAPESTOP.play();
             }
             CPC.rec = false;
             CPC.rew = true;
             CPC.ffwd = false;
             playing = false;
      btnREC.setForeground(Color.WHITE);
      btnREW.setForeground(Color.LIGHT_GRAY);
      btnPLAY.setForeground(Color.WHITE);
      btnFF.setForeground(Color.WHITE);
      btnREW.setBorder(new BevelBorder(BevelBorder.LOWERED));
      if (!CPC.play){
      btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnPLAY.setBackground(Color.DARK_GRAY);}
      btnFF.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREW.setBackground(Color.BLACK);
      btnFF.setBackground(Color.DARK_GRAY);
          //   if (CPC.relay){
            if (Switches.FloppySound){
                Samples.TAPEKEY.play();
             Samples.WINDMOTOR.stop();
             Samples.REWINDMOTOR.loop();
             Samples.TAPEMOTOR.stop();}
           //  }
         }}
      });
      btnPLAY.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
       showText();
             buttonpressed = true;
             if (!CPC.rec){
             if (Switches.FloppySound){
                 if (CPC.ffwd || CPC.rew)
                Samples.TAPESTOP.play();
             }
             playing = true;
             CPC.rec = false;
             CPC.play = true;
             CPC.rew = false;
             CPC.ffwd = false;
            if (Switches.FloppySound){
                Samples.TAPEKEY.play();
             Samples.WINDMOTOR.stop();
             Samples.REWINDMOTOR.stop();
             if (CPC.relay && !CPC.trueaudio){
             Samples.TAPEMOTOR.loop();
             }
            }
      btnREW.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnPLAY.setBorder(new BevelBorder(BevelBorder.LOWERED));
      btnPLAY.setBackground(Color.BLACK);
      btnFF.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBackground(new Color(0xff,0x00,0x00));
      btnREW.setBackground(Color.DARK_GRAY);
      btnFF.setBackground(Color.DARK_GRAY);

      btnREC.setForeground(Color.WHITE);
      btnREW.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.LIGHT_GRAY);
      btnFF.setForeground(Color.WHITE);
         }}
      });
      btnREC.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
       showText();
             buttonpressed = true;
             if (!playing){
                 CPC.number = 0;
                 TapeCounter.setText("000");
             CPC.tapesample = new byte[0];
             try {
             CPC.tapesample = new byte[(int)Switches.availmem];
             positionslider.setMaximum((int)Switches.availmem);
             positionslider.setValue(0);
             } catch (Exception tooLarge){
             }
             if (Switches.khz44)
                 CPC.tape_delay = 1000000/44100;
             if (Switches.khz11)
                 CPC.tape_delay = 1000000/11025;
             if (!Switches.khz11 && !Switches.khz44)
                 CPC.tape_delay = 1000000/22050;
             CPC.recordcount = 0;
             CPC.rec = true;
             CPC.play = true;
             CPC.rew = false;
             CPC.ffwd = false;
             playing = false;
             //audiocapture.captureAudio();
            if (Switches.FloppySound){
                Samples.TAPEKEY.play();
             Samples.WINDMOTOR.stop();
             Samples.REWINDMOTOR.stop();
             if (CPC.relay && !CPC.trueaudio)
             Samples.TAPEMOTOR.loop();}
      btnREW.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnPLAY.setBorder(new BevelBorder(BevelBorder.LOWERED));
      btnFF.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.LOWERED));
      btnREC.setBackground(Color.BLACK);
      btnREW.setBackground(Color.DARK_GRAY);
      btnPLAY.setBackground(Color.BLACK);
      btnFF.setBackground(Color.DARK_GRAY);

      btnREC.setForeground(Color.LIGHT_GRAY);
      btnREW.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.LIGHT_GRAY);
      btnFF.setForeground(Color.WHITE);
         }}
      });
      btnFF.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
       showText();
             buttonpressed = true;
             if (!CPC.rec){
             if (Switches.FloppySound){
                 if (CPC.rew)
                Samples.TAPESTOP.play();
             }
             CPC.rec = false;
             CPC.rew = false;
             CPC.ffwd = true;
             playing = false;
            // CPC.play = false;
            if (Switches.FloppySound){
                Samples.TAPEKEY.play();
             Samples.WINDMOTOR.loop();
             Samples.REWINDMOTOR.stop();
             Samples.TAPEMOTOR.stop();}
      btnREW.setBorder(new BevelBorder(BevelBorder.RAISED));
      if (!CPC.play){
      btnPLAY.setBackground(Color.DARK_GRAY);
      btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));}
      btnFF.setBorder(new BevelBorder(BevelBorder.LOWERED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBackground(new Color(0xff,0x00,0x00));
      btnREW.setBackground(Color.DARK_GRAY);
      btnFF.setBackground(Color.BLACK);

      btnREC.setForeground(Color.WHITE);
      btnREW.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.WHITE);
      btnFF.setForeground(Color.LIGHT_GRAY);
         }}
      });
      btnSTOP.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             showText();
             if (!buttonpressed){
                 CPC.tapesample = new byte[0];
               //  jemu.ui.JEMU.tapeload = true;
                 Settings.set(Settings.TAPE_FILE, "~none~");
                 Settings.setBoolean(Settings.LOADTAPE , false);
                 showText("No tape inserted...");
                 CPC.tapeloaded = false;
                 positionslider.setValue(0);
                 positionslider.setMaximum(0);
            if (Switches.FloppySound){
                Samples.TAPEEJECT.play();
            }
             }
             if (CPC.rec)
                 CPC.savetape = true;
             CPC.rec = false;
             CPC.play = false;
             CPC.rew = false;
             CPC.ffwd = false;
             buttonpressed = false;
             playing = false;
            if (Switches.FloppySound){
                Samples.TAPESTOP.play();
             Samples.WINDMOTOR.stop();
             Samples.REWINDMOTOR.stop();
             Samples.TAPEMOTOR.stop();}
      btnREW.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnFF.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBackground(new Color(0xff,0x00,0x00));
      btnREW.setBackground(Color.DARK_GRAY);
      btnPLAY.setBackground(Color.DARK_GRAY);
      btnFF.setBackground(Color.DARK_GRAY);
      btnREC.setForeground(Color.WHITE);
      btnREW.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.WHITE);
      btnFF.setForeground(Color.WHITE);
         }
      });
      reset.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
       showText();
             counter = 0;
             before = "000";
             TapeCounter.setText(before + counter);
             CPC.number = 0;
             positionslider.setValue(0);
         }
      });
      trueaudio.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (CPC.trueaudio){
                 CPC.trueaudio = false;
                 trueaudio.setText("▄");
                 trueaudio.setForeground(Color.RED);
             }
             else{
                 CPC.trueaudio = true;
                 trueaudio.setText("▀");
                 trueaudio.setForeground(Color.GREEN);
             }
         }
      });
      btnPAUSE.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
       showText();
             if (Switches.FloppySound)
                Samples.TAPEKEY.play();
         if (paused){
                 btnPAUSE.setBackground(Color.DARK_GRAY);
                 paused = false;
         }
         else{
                 btnPAUSE.setBackground(Color.BLACK);
                 paused = true;}
         }
      });
      memory.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             String first = "";
             if (!isMem){
                 memory.setBorder(new BevelBorder(BevelBorder.LOWERED));
                 TapeMemory.setForeground(Color.WHITE);
                 TapeMemory.setBackground(Color.GRAY);
                 isMem = true;
                 memCount = counter;

                 if (memCount<10)
                     first="000";
                 else
                 if (memCount<100)
                     first ="00";
                 else
                 if (memCount<1000)
                     first ="0";
                 TapeMemory.setText(first + memCount);
             }
             else{
                 isMem = false;
                 memory.setBorder(new BevelBorder(BevelBorder.RAISED));
                 TapeMemory.setBackground(Color.BLACK);
                 TapeMemory.setForeground(Color.BLACK);
                 TapeMemory.setText("000");
             }

         }
      });

      
      this.add(TapeCounter);
      this.add(reset);
      this.add(blanker);
      this.add(trueaudio);
      this.add(blanker2);
      this.add(memory);
      this.add(TapeMemory);
      this.add(tapeDeck);
      this.add(gfx);
      this.add(positionslider);
      this.add(btnREC);
      this.add(btnPLAY);
      this.add(btnREW);
      this.add(btnFF);
      this.add(btnSTOP);
      this.add(btnPAUSE);

      btnREW.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREW.setFocusable(false);
      btnREW.setBackground(Color.DARK_GRAY);
      btnREW.setForeground(Color.WHITE);
      memory.setBorder(new BevelBorder(BevelBorder.RAISED));
      memory.setFocusable(false);
      memory.setBackground(Color.DARK_GRAY);
      memory.setForeground(Color.WHITE);
      TapeMemory.setBorder(new BevelBorder(BevelBorder.LOWERED));
      TapeMemory.setFocusable(false);
      TapeMemory.setBackground(Color.BLACK);
      TapeMemory.setForeground(Color.BLACK);
      TapeMemory.setEditable(false);
      btnPLAY.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnFF.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnSTOP.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnPAUSE.setBorder(new BevelBorder(BevelBorder.RAISED));
      TapeCounter.setBorder(new BevelBorder(BevelBorder.LOWERED));
      reset.setBorder(new BevelBorder(BevelBorder.RAISED));
      btnREC.setBorder(new BevelBorder(BevelBorder.RAISED));
      trueaudio.setBorder(null);
      trueaudio.setForeground(Color.RED);
      trueaudio.setBackground(Color.BLACK);
      btnPLAY.setFocusable(false);
      btnFF.setFocusable(false);
      btnSTOP.setFocusable(false);
      btnPAUSE.setFocusable(false);
      reset.setFocusable(false);
     // TapeCounter.setFocusable(false);
      TapeCounter.setFont(new Font ("Courier",0,12));
      TapeCounter.setDoubleBuffered(true);
      btnREC.setBackground(new Color(0xff,0x00,0x00));
      btnPLAY.setBackground(Color.DARK_GRAY);
      btnFF.setBackground(Color.DARK_GRAY);
      btnSTOP.setBackground(Color.DARK_GRAY);
      btnPAUSE.setBackground(Color.DARK_GRAY);
      reset.setBackground(Color.DARK_GRAY);
      btnREC.setForeground(Color.WHITE);
      btnPLAY.setForeground(Color.WHITE);
      btnFF.setForeground(Color.WHITE);
      btnSTOP.setForeground(Color.WHITE);
      btnPAUSE.setForeground(Color.WHITE);
      TapeCounter.setBackground(Color.DARK_GRAY);
      TapeCounter.setForeground(Color.WHITE);
      reset.setForeground(Color.LIGHT_GRAY);
      this.setForeground(Color.LIGHT_GRAY);
      this.setBackground(Color.BLACK);
     // this.setBackground(new Color(0x34,0x37,0x39));
      this.setTitle("TapeDeck");
      this.pack();
      this.setSize(272,354);
      this.setResizable(false);
      this.setAlwaysOnTop(true);
      this.addWindowListener(this);
      //gfx.showText("JavaCPC Tapedrive");
   }

   public void showText(String text){
       gfx.message = text;
       gfx.showText();
   }

   public void showText(){
       gfx.showText();
   }
}

