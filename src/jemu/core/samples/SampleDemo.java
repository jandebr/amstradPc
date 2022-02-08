/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.core.samples;

/**
 *
 * @author Markus
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Testing the Samples enum in a Swing application
public class SampleDemo extends JFrame {

    protected boolean playing = false;
   // Constructor
   public SampleDemo() {
      // Pre-load all the sound files
      Samples.init();
      Samples.volume = Samples.Volume.HIGH;  // un-mute

      // Set up UI components
      Container cp = this.getContentPane();
      cp.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
      JButton btnSound1 = new JButton("Eject");
      btnSound1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Samples.EJECT.play();
         }
      });
      cp.add(btnSound1);
      JButton btnSound2 = new JButton("Insert");
      btnSound2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Samples.INSERT.play();
         }
      });
      cp.add(btnSound2);
      JButton btnSound3 = new JButton("Motor");
      btnSound3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (playing){
                 Samples.MOTOR.stop();
                 playing = false;
             } else {
            Samples.MOTOR.loop();
             playing = true;
             }
         }
      });
      cp.add(btnSound3);
      JButton btnSound4 = new JButton("Seek");
      btnSound4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Samples.SEEK.play();
         }
      });
      cp.add(btnSound4);
      JButton btnSound5 = new JButton("Seekback");
      btnSound5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Samples.SEEKBACK.play();
         }
      });
      cp.add(btnSound5);
      JButton btnSound6 = new JButton("Track");
      btnSound6.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Samples.TRACK.play();
         }
      });
      cp.add(btnSound6);
      JButton btnSound7 = new JButton("Trackback");
      btnSound7.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Samples.TRACKBACK.play();
         }
      });
      cp.add(btnSound7);
      JButton btnSound8 = new JButton("Degauss");
      btnSound8.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Samples.DEGAUSS.play();
         }
      });
      cp.add(btnSound8);
      JButton btnSound9 = new JButton("RelOn");
      btnSound9.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Samples.RELAIS.play();
         }
      });
      cp.add(btnSound9);
      JButton btnSound10 = new JButton("RelOff");
      btnSound10.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Samples.RELAISOFF.play();
         }
      });
      cp.add(btnSound10);
      JButton btnSound11 = new JButton("TaMotor");
      btnSound11.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (playing){
                 Samples.TAPEMOTOR.stop();
                 playing = false;
             } else {
            Samples.TAPEMOTOR.loop();
             playing = true;
             }
         }
      });
      cp.add(btnSound11);
      JButton btnSound12 = new JButton("TaRew");
      btnSound12.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (playing){
                 Samples.REWINDMOTOR.stop();
                 playing = false;
             } else {
            Samples.REWINDMOTOR.loop();
             playing = true;
             }
         }
      });
      cp.add(btnSound12);
      JButton btnSound13 = new JButton("TaFF");
      btnSound13.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             if (playing){
                 Samples.WINDMOTOR.stop();
                 playing = false;
             } else {
            Samples.WINDMOTOR.loop();
             playing = true;
             }
         }
      });
      cp.add(btnSound13);
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      this.setTitle("Test Samples");
      this.pack();
      this.setVisible(true);
   }

   public static void main(String[] args) {
      new SampleDemo();
   }
}

