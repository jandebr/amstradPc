/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.core.device.floppy;

import java.awt.*;
import java.net.URL;
import java.awt.event.*;
import javax.swing.*;
import jemu.settings.Settings;

/**
 *
 * @author Markus
 */
  public class virtualDrive extends JFrame implements MouseListener, MouseMotionListener {
    int x, y;  // Textposition
    public int oldtrackpos, trackpos;
    protected boolean isClicked;
    protected boolean init = true;
    protected int oldY = 10;
    // Angezeigter Text
    final String text = "a little graphic demo in Java";
  final URL dhead = getClass().getResource("drive_head.png");
   Image drivehead = getToolkit().getImage(dhead);
  final URL driv = getClass().getResource("drive.png");
   Image drivebase = getToolkit().getImage(driv);
   static boolean showIt, hideIt;
  public static boolean ledOn                   = false;
  int multipler = 2;
  public static  Color LED_OFF         = new Color(0x90, 0x00, 0x00);


 public void update() {
     if (init){
         int x = Integer.parseInt(Settings.get(Settings.FLOPPYX, "0"));
         int y = Integer.parseInt(Settings.get(Settings.FLOPPYY, "0"));
         multipler = Integer.parseInt(Settings.get(Settings.FLOPPYZOOM, "1"));
         this.setLocation(x, y);
     this.addMouseMotionListener(this);
     this.addMouseListener(this);
                  rePaint();
     init=false;
     }
     if (showIt && !this.isVisible())
         this.setVisible(true);
     if (hideIt && this.isVisible()){
         this.setVisible(false);
     }
     repaint();
 }

 public static void Show(){
     showIt = true;
     hideIt = false;
 }
 public static void Hide(){
     hideIt = true;
     showIt = false;
 }
    public void paint(Graphics g) {
        if (oldtrackpos != trackpos){
        trackpos = (trackpos % 42)/2;
        trackpos = trackpos * multipler;}
        oldtrackpos = trackpos;
        g.drawImage(drivebase,0,0,drivebase.getWidth(this)*multipler,drivebase.getHeight(this)*multipler, this);
        g.drawImage(drivehead,35*multipler,22*multipler+trackpos,drivehead.getWidth(this)*multipler,drivehead.getHeight(this)*multipler, this);
        if (jemu.ui.Display.ledOn)
            g.setColor(Color.RED);
        else
            g.setColor(LED_OFF);
        g.fillRect(6*multipler, 158*multipler, 8*multipler, 2*multipler);
    }

 public void mouseMoved(MouseEvent me)
     {

     }
   public void mouseDragged(MouseEvent me)
     {
       int x = me.getXOnScreen(); int y = me.getYOnScreen();
       int xf = this.getWidth()/2; int yf= this.getHeight()/2;
       this.setLocation(x-xf,y-yf);
       Settings.set(Settings.FLOPPYX, ""+(x-xf));
       Settings.set(Settings.FLOPPYY, ""+(y-yf));
        }

   protected void rePaint(){
             System.out.println("Zoomfactor is: " + this.multipler);
             this.setSize(98*this.multipler,160*this.multipler);
             this.oldY = this.getHeight();
   }
  public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
          if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == 0) {
              if (multipler > 1){
                  multipler--;
                  Settings.set(Settings.FLOPPYZOOM, ""+(multipler));
                  rePaint();
              }

          } else
          {
              if (multipler < 4){
                  multipler++;
                  Settings.set(Settings.FLOPPYZOOM, ""+(multipler));
                  rePaint();
              }
          }
      }
  }
  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) { }
  }