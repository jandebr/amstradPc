package jemu.core.device.tape;

//********************************************************************
//  GraphicDisplay.java       Author: Devilmarkus
//
//  Draws tape-WAV to a small display
//********************************************************************

import java.awt.*;
import jemu.system.cpc.CPC;

import javax.swing.*;
public class GraphicDisplay extends Canvas
{
   private final int CANVAS_WIDTH = 246;
   private final int CANVAS_HEIGHT = 20;
   public String message="";
private int oldlevel=0;
private int oldi = 0;

   //-----------------------------------------------------------------
   //  Creates an initially empty canvas.
   //-----------------------------------------------------------------
   public GraphicDisplay ()
   {
      setBackground (Color.BLACK);
      setForeground (Color.GRAY);
      setSize (CANVAS_WIDTH, CANVAS_HEIGHT);
      setFont(new Font("Arial",1,9));
   }

   public void paintWAV(){
      Graphics page = getGraphics();
      page.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
      int divider = 10;
      oldi = 0;
		for (int i = 0; i < 2500; i = i + divider) {
            int level = 0;
            if (CPC.number+(i) < CPC.tapesample.length)
                level = (CPC.tapesample[CPC.number+(i)])&0xff;
            level = level /12;
            page.setColor(Color.GREEN);
            if (level < 255/24)
            page.setColor(Color.RED);
           page.drawLine(oldi, oldlevel, i/divider, (255/12)-level );

           oldlevel = (255/12)-level;
           oldi = i/divider;
        }

   }

   public void showText(String text){
      getGraphics().clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
      getGraphics().drawString(text, 1, 14);
   }
   public void showText(){
      getGraphics().clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
      getGraphics().drawString(message, 1, 14);
   }

}
