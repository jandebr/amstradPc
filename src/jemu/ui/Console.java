package jemu.ui;

/**
 * Title:        JEMU by cpc.devilmarkus.de / jemu.winape.net
 * Description:  JAVA emulator for Amstrad Homecomputers
 * Copyright:    Copyright (c) 2002-2008
 * Company:
 * @author       Devilmarkus
 * @version 4.0
 * 
 * 
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import org.maia.amstrad.io.MultiplexOutputStream;
import org.maia.amstrad.pc.AmstradFactory;

public class Console extends WindowAdapter implements WindowListener, ActionListener, Runnable
{
        JFileChooser saveFileChooser = new JFileChooser();
        protected GridBagConstraints gbcConstraints   = null;
	public static JFrame frameconsole;
	public static JTextArea textArea;
	private Thread reader;
	private Thread reader2;
	private boolean quit;
		JButton button=new JButton("Clear");
		JButton button2=new JButton("Copy");
		JButton button3=new JButton("Save");
					
	private final PipedInputStream pin=new PipedInputStream(); 
	private final PipedInputStream pin2=new PipedInputStream(); 
        protected Font font;
	
	public Console()
	{
		// create all components and add them
		frameconsole=new JFrame("JavaCPC Console");
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize=new Dimension((int)(screenSize.width/2),(int)(screenSize.height/2));
		int x=(int)(frameSize.width/2);
		int y=(int)(frameSize.height/2);
		frameconsole.setBounds(x,y,350,400);
		
		textArea=new JTextArea();
                InputStream in = getClass().getResourceAsStream("amstrad.ttf");
                try {
                font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(Font.PLAIN, 8);
                }
                catch (Exception e) {}
                textArea.setFont(this.font);
                textArea.setEditable(true);
                textArea.setAutoscrolls(true);
                textArea.setBackground(Color.BLACK);
                textArea.setForeground(Color.GREEN);
                textArea.setCaretColor(Color.GREEN);
                textArea.setSelectedTextColor(Color.RED);
                textArea.setSelectionColor(Color.ORANGE);
                textArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
                //textArea.setFont(new Font("", 1, 10));
                textArea.setLineWrap(true);
                button.setForeground(Color.RED);
                button.setBackground(Color.DARK_GRAY);
                button.setBorder(new BevelBorder(BevelBorder.RAISED));
                button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                button2.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                button2.setForeground(Color.YELLOW);
                button2.setBackground(Color.DARK_GRAY);
                button2.setBorder(new BevelBorder(BevelBorder.RAISED));
                button3.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                button3.setForeground(Color.GREEN);
                button3.setBackground(Color.DARK_GRAY);
                button3.setBorder(new BevelBorder(BevelBorder.RAISED));
		frameconsole.getContentPane().setLayout(new GridBagLayout());
                JScrollPane scrollpane = new JScrollPane(textArea);
                frameconsole.add(scrollpane,getGridBagConstraints(0, 0, 1.0, 1.0, 4, GridBagConstraints.BOTH));
                frameconsole.add(button,getGridBagConstraints(0, 1, 1.0, 0.0, 1, GridBagConstraints.BOTH));
                frameconsole.add(button2,getGridBagConstraints(1, 1, 1.0, 0.0, 1, GridBagConstraints.BOTH));
                frameconsole.add(button3,getGridBagConstraints(2, 1, 1.0, 0.0, 1, GridBagConstraints.BOTH));
                frameconsole.setAlwaysOnTop(true);
		frameconsole.setVisible(false);		
		
		frameconsole.addWindowListener(this);		
		button.addActionListener(this);		
		button2.addActionListener(this);
                button.setFocusable(false);
                button2.setFocusable(false);
                button3.setFocusable(false);	
		button3.addActionListener(this);
		
		try
		{
			PipedOutputStream pout=new PipedOutputStream(this.pin);
			OutputStream cos = AmstradFactory.getInstance().getAmstradContext().getConsoleOutputStream();
			MultiplexOutputStream multiplexOut = new MultiplexOutputStream();
			multiplexOut.addOutputStream(pout);
			if (cos != null) multiplexOut.addOutputStream(cos);
			System.setOut(new PrintStream(multiplexOut,true)); 
		} 
		catch (java.io.IOException io)
		{
			textArea.append("Couldn't redirect STDOUT to this console\n"+io.getMessage());
		}
		catch (SecurityException se)
		{
			textArea.append("Couldn't redirect STDOUT to this console\n"+se.getMessage());
	    } 
		
		try 
		{
			PipedOutputStream pout2=new PipedOutputStream(this.pin2);
			OutputStream ces = AmstradFactory.getInstance().getAmstradContext().getConsoleErrorStream();
			MultiplexOutputStream multiplexOut = new MultiplexOutputStream();
			multiplexOut.addOutputStream(pout2);
			if (ces != null) multiplexOut.addOutputStream(ces);
			System.setErr(new PrintStream(multiplexOut,true));
		} 
		catch (java.io.IOException io)
		{
			textArea.append("Couldn't redirect STDERR to this console\n"+io.getMessage());
		}
		catch (SecurityException se)
		{
			textArea.append("Couldn't redirect STDERR to this console\n"+se.getMessage());
	    } 		
			
		quit=false; // signals the Threads that they should exit
				
		// Starting two seperate threads to read from the PipedInputStreams				
		//
		reader=new Thread(this);
		reader.setDaemon(true);	
		reader.start();	
		//
		reader2=new Thread(this);	
		reader2.setDaemon(true);	
		reader2.start();
				
	}
	
        
	private GridBagConstraints getGridBagConstraints(int x, int y, double weightx,double weighty,
            int width, int fill)
                {
                if (this.gbcConstraints == null) {
                    this.gbcConstraints = new GridBagConstraints();
                }
                this.gbcConstraints.gridx = x;
                this.gbcConstraints.gridy = y;
                this.gbcConstraints.weightx = weightx;
                this.gbcConstraints.weighty = weighty;
                this.gbcConstraints.gridwidth = width;
                this.gbcConstraints.fill = fill;
            return this.gbcConstraints;
  }	
		
	public synchronized void windowClosing(WindowEvent evt)
	{
		frameconsole.setVisible(false); // default behaviour of JFrame	
		frameconsole.dispose();
	}
	
	public synchronized void actionPerformed(ActionEvent evt)
	{
                if (evt.getSource() == button) {
		textArea.setText("");
                } 
                if (evt.getSource() == button2) {
		textArea.selectAll();
                textArea.copy();
                System.out.println("Console content copied to clipboard.");
                } 
                if (evt.getSource() == button3) {
		saveFile();
                } 
	}

	public synchronized void run()
	{
		try
		{			
			while (Thread.currentThread()==reader)
			{
				try { this.wait(100);}catch(InterruptedException ie) {}
				if (pin.available()!=0)
				{
					String input=this.readLine(pin);
					textArea.append(input);
                                        textArea.select(2000000000, 2000000000);
				}
				if (quit) return;
			}
		
			while (Thread.currentThread()==reader2)
			{
				try { this.wait(100);}catch(InterruptedException ie) {}
				if (pin2.available()!=0)
				{
					String input=this.readLine(pin2);
					textArea.append(input);
                                        textArea.select(2000000000, 2000000000);
				}
				if (quit) return;
			}			
		} catch (Exception e)
		{
			textArea.append("\nConsole reports an Internal error.");
			textArea.append("The error is: "+e);			
		}


	}
	
	public synchronized String readLine(PipedInputStream in) throws IOException
	{
		String input="";
		do
		{
			int available=in.available();
			if (available==0) break;
			byte b[]=new byte[available];
			in.read(b);
			input=input+new String(b,0,b.length);														
		}while( !input.endsWith("\n") &&  !input.endsWith("\r\n") && !quit);
		return input;
	}
        

      public void saveFile(){
        FileDialog filedia = new FileDialog((Frame) frameconsole, "Save ASCII...", FileDialog.SAVE);
        filedia.setFile("*.txt");
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename != null) { 
           filename =  filedia.getDirectory() + filedia.getFile();
            String savename=filename;  
            if (!savename.toLowerCase().endsWith(".txt"))
                        savename=savename + ".txt";          
        File file = new File(savename);
        String gettext = textArea.getText();
        try{
            FileWriter fw = new FileWriter(file);
            fw.write(gettext);
            fw.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
               }
        }
filedia.dispose();
      }
}