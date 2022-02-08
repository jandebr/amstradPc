/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.core.device.sound;

import jemu.system.cpc.CPC;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 *
 * @author Markus
 */
public class AudioCapture extends JComponent{

    protected   boolean initialized     = false;
    protected   JButton selectB         = new JButton("Select file");
    protected   JButton startB          = new JButton("Start recording");
    protected   JButton stopB           = new JButton("Stop recording");
    protected   JButton pauseB          = new JButton("Pause");
    public      boolean stopCapture     = false;
    public      boolean doCapture       = false;
    public      boolean doInit          = false;
    public      boolean showCapture     = false;
    protected   int     counter         = 0;
    protected   int     Subchunk2Size   = 0;
	protected   int     ChunkSize       = 0;
    protected   RandomAccessFile file;
    protected   String filename;
    Frame Audiocapture;
    BufferDisplay gfx = new BufferDisplay();
    protected int flashCol = 0;
    protected boolean doStartCapture = false;
    Color DARK_GREEN = new Color(0x00, 0x40, 0x00);
    Color DARK_RED = new Color(0x40, 0x00, 0x00);

    protected byte[] WAV_HEADER = {
        (byte)0x52,(byte)0x49,(byte)0x46,(byte)0x46,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x57,(byte)0x41,
        (byte)0x56,(byte)0x45,(byte)0x66,(byte)0x6D,(byte)0x74,(byte)0x20,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,
        (byte)0x01,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x44,(byte)0xac,(byte)0x00,(byte)0x00,(byte)0x44,(byte)0xac,
        (byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x64,(byte)0x61,(byte)0x74,(byte)0x61,
        (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
    };

    public void select(){
                startB.setEnabled(false);
        Audiocapture.setAlwaysOnTop(false);
        FileDialog filedia = new FileDialog((Frame) Audiocapture, "Record WAV file", FileDialog.SAVE);
        filedia.setFile("*.wav");
        filedia.setVisible(true);
        filename = filedia.getDirectory() + filedia.getFile();
        if (filename != null){
            try{
                File check = new File(filename);
                check.delete();
            }
            catch (Exception e){}
            if (filename.toLowerCase().endsWith(".wav"))
               filename =  filename.substring(0, (filename.length() - 4));
                startB.setEnabled(true);
        }
        Audiocapture.setAlwaysOnTop(true);
    }

    public void init(){
        if (filename != null) {
            try{
                file = new RandomAccessFile(filename+".tmp", "rw");
                file.write(new byte[44]);
                doCapture = true;
            } catch (Exception e){}
        }
        doInit = false;

    }

    public void PaintBuffer(byte[] input){
            gfx.paintWAV(input);
    }

    public void Capture(byte[] input, int length){
        flashCol++;
        if ((input[1] != -128 || input[2] != -128) && !doStartCapture){
            System.out.println("recording started...");
            doStartCapture = true;
        }
        if (doStartCapture)
        try{
            if (flashCol == 50)
               startB.setBackground(Color.GREEN);
            if (flashCol == 100){
                startB.setBackground(DARK_GREEN);
                flashCol = 0;
            }
            file.write(input);
            counter+=length;
		Subchunk2Size = counter;
		ChunkSize = 36 + Subchunk2Size;
        putLong(WAV_HEADER , 28, 88200);
        putLong(WAV_HEADER , 40, Subchunk2Size);
        putLong(WAV_HEADER ,  4, ChunkSize);
        }
        catch (Exception e){}
        else {
            if (flashCol == 50)
               startB.setBackground(Color.RED);
            if (flashCol == 100){
                startB.setBackground(DARK_RED);
                flashCol = 0;
            }
        }
    }
    public void stopCapture(){
        stopCapture = doCapture = doInit = false;
                startB.setBackground(Color.LIGHT_GRAY);
        try{
            file.seek(0);
            file.write(WAV_HEADER,0,44);
            file.close();
            File file1 = new File(filename+".tmp");
            File file2 = new File(filename+".wav");
            file1.renameTo(file2);
        }
        catch (IOException e){System.err.println("error writing header!");}
    }

    public void showCapture(){

        this.setDoubleBuffered(true); 
    startB.setEnabled(false);
    stopB.setEnabled(false);
    pauseB.setEnabled(false);
    if (!initialized){
    startB.addActionListener(
      new ActionListener(){
        public void actionPerformed(
                                  ActionEvent e){
            startB.setForeground(Color.WHITE);
            doStartCapture = false;
          startB.setEnabled(false);
          selectB.setEnabled(false);
          stopB.setEnabled(true);
          pauseB.setEnabled(true);
          init();
        }
      }
    );
    stopB.addActionListener(
      new ActionListener(){
        public void actionPerformed(
                                  ActionEvent e){
                startB.setEnabled(false);
               startB.setBackground(Color.LIGHT_GRAY);
    startB.setForeground(Color.BLACK);
            doStartCapture = false;
          startB.setEnabled(true);
          selectB.setEnabled(true);
          stopB.setEnabled(false);
          pauseB.setEnabled(false);
          stopCapture();
        }
      }
    );

    selectB.addActionListener(
      new ActionListener(){
        public void actionPerformed(
                                  ActionEvent e){
          select();
        }
      }
    );

    pauseB.addActionListener(
      new ActionListener(){
        public void actionPerformed(
                                  ActionEvent e){
            doStartCapture = false;
               startB.setBackground(Color.LIGHT_GRAY);
    startB.setForeground(Color.BLACK);
            if (doCapture){
                doCapture = false;
                pauseB.setBackground(Color.RED);
            } else {
                doCapture = true;
                pauseB.setBackground(Color.GREEN);
            }
        }
      }
    );
    initialized = true;
    }
    Audiocapture = new Frame();
    Audiocapture.addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent arg0) {
                CPC.showAudioCapture = false;
    showCapture = true;
            Audiocapture.setVisible(false);
            }
			public void windowActivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
                CPC.showAudioCapture = false;
    showCapture = true;
            Audiocapture.setVisible(false);
            }
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
    });

    startB.setForeground(Color.BLACK);
    selectB.setBackground(Color.LIGHT_GRAY);
    startB.setBackground(Color.LIGHT_GRAY);
    stopB.setBackground(Color.LIGHT_GRAY);
    Audiocapture.add(gfx);
    Audiocapture.add(selectB);
    Audiocapture.add(startB);
    Audiocapture.add(pauseB);
    Audiocapture.add(stopB);
    selectB.setBorder(new BevelBorder(BevelBorder.RAISED));
    startB.setBorder(new BevelBorder(BevelBorder.RAISED));
    stopB.setBorder(new BevelBorder(BevelBorder.RAISED));
    pauseB.setBorder(new BevelBorder(BevelBorder.RAISED));
    pauseB.setBackground(Color.GREEN);
    Audiocapture.setBackground(Color.DARK_GRAY);
    Audiocapture.setForeground(Color.LIGHT_GRAY);
    Audiocapture.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
    Audiocapture.setTitle("JavaCPC AudioCapture");
    Audiocapture.setSize(420,190);
    
    Audiocapture.setResizable(false);
    Audiocapture.setVisible(true);
    Audiocapture.setAlwaysOnTop(true);
    showCapture = true;
    CPC.showAudioCapture = false;
    }

    public void putLong(byte[] buffer, int offs, int data) {
    buffer[offs]=(byte)(data& 0xff);
 	buffer[offs+1]=(byte)((data>>8)&0x0ff);
 	buffer[offs+2]=(byte)((data>>16)&0x0ff);
 	buffer[offs+3] =(byte)((data>>24)&0x0ff);
  }
}
