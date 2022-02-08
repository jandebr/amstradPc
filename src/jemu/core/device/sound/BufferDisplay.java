package jemu.core.device.sound;

//********************************************************************
//  GraphicDisplay.java       Author: Devilmarkus
//
//  Draws tape-WAV to a small display
//********************************************************************

import java.awt.*;
public class BufferDisplay extends Canvas
{
   private final int CANVAS_WIDTH = 400;
   private final int CANVAS_HEIGHT = 108;
   public String left="Left Channel";
   public String right="Right Channel";
   private int OldlevelR, OldlevelL = 0;
   private int OldposR, OldposL = 0;
      Graphics page = getGraphics();

   //-----------------------------------------------------------------
   //  Creates an initially empty canvas.
   //-----------------------------------------------------------------
   public BufferDisplay ()
   {
      setBackground (Color.DARK_GRAY);
      setForeground (Color.GREEN);
      setSize (CANVAS_WIDTH, CANVAS_HEIGHT);
   }

   public void paintWAV(byte[] data){
       page = getGraphics();
      int driftL = 0x0a;
      int driftR = 0x26;
      OldposR = 0;
      OldposL = 0;
      OldlevelR = 64+driftR;
      OldlevelL = 64-driftL;
		for (int i = 1; i < data.length; i+=2) {
            page.setColor(Color.BLACK);
            page.drawLine(i, 0, i, 108);
            page.drawLine(i-1, 0, i-1, 108);
            page.setColor(Color.GREEN);
            int levelL = (int)java.lang.Math.sqrt(data[i]*data[i])/2;
            int levelR = (int)java.lang.Math.sqrt(data[i-1]*data[i-1])/2;
            page.setColor(Color.RED);//new Color((levelR*2+20)&0xff, 0x00, 0x00));
            page.drawLine(OldposR, OldlevelR, i, levelR + driftR );
            page.setColor(Color.GREEN);//new Color(0x00, (levelL*2+20)&0xff, 0x00));
           page.drawLine(OldposL, OldlevelL, i, levelL - driftL );
           OldlevelR = levelR + driftR;
           OldposR = i;
           OldlevelL = levelL - driftL;
           OldposL = i;
        }

   }
}
