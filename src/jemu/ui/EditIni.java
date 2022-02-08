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

import jemu.system.cpc.CPCPrinter;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.border.*;
import jemu.system.cpc.CPC;
import java.io.*;
import javax.swing.text.*;

public class EditIni extends WindowAdapter implements WindowListener, ActionListener
{
        JFileChooser saveFileChooser = new JFileChooser(System.getProperty("/"));
        static JFileChooser loadFileChooser = new JFileChooser(System.getProperty("/"));
        protected GridBagConstraints gbcConstraints   = null;
	public static JFrame typeconsole;
        public static String autotext;
	public static JTextArea textArea;
	public JButton button3  =new JButton(" Copy text ");
	public JButton button4  =new JButton(" Paste text ");
	public static JButton button5  =new JButton(" Load... ");
	public static JButton button6  =new JButton(" Save... ");
        protected Font font;
        protected Color CPC_BLUE = new Color (00,00,0x7f);
        protected Color CPC_GRAY = new Color (0x7f,0x7f,0x7f);
	public EditIni()
	{
		typeconsole=new JFrame("JavaCPC.ini");
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize=new Dimension((int)(screenSize.width/2),(int)(screenSize.height/2));
		int x=(frameSize.width/2 + 110);
		int y=(frameSize.height/2 + 60);
		typeconsole.setBounds(x,y,340,380);
		textArea=new JTextArea();
                typeconsole.setResizable(false);
		textArea.setLineWrap(true);
		textArea.setEditable(true);
                textArea.setAutoscrolls(true);
                textArea.setBackground(Color.WHITE);
                textArea.setForeground(Color.BLACK);
                textArea.setCaretColor(Color.BLACK);
               // textArea.setCaret(new DefaultCaret());
                textArea.setSelectedTextColor(Color.DARK_GRAY);
                textArea.setSelectionColor(Color.LIGHT_GRAY);
                textArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
                textArea.setFont(new Font("Courier", 1, 12));
                button3.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                button3.setForeground(Color.YELLOW);
                button3.setBackground(CPC_BLUE);
                button3.setFont(this.font);
                button3.setBorder(new BevelBorder(BevelBorder.RAISED));             
                button4.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                button4.setForeground(Color.CYAN);
                button4.setBackground(CPC_BLUE);
                button4.setFont(this.font);
                button4.setBorder(new BevelBorder(BevelBorder.RAISED));
                button5.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                button5.setForeground(Color.LIGHT_GRAY);
                button5.setBackground(CPC_BLUE);
                button5.setFont(this.font);
                button5.setBorder(new BevelBorder(BevelBorder.RAISED));
                button6.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                button6.setForeground(Color.WHITE);
                button6.setBackground(CPC_BLUE);
                button6.setFont(this.font);
                button6.setBorder(new BevelBorder(BevelBorder.RAISED));
                typeconsole.getContentPane().setLayout(new GridBagLayout());
                typeconsole.add(button3,getGridBagConstraints(1, 2, 1.0, 0.0, 1, GridBagConstraints.BOTH));
                typeconsole.add(button4,getGridBagConstraints(2, 2, 1.0, 0.0, 1, GridBagConstraints.BOTH));
                typeconsole.add(button5,getGridBagConstraints(1, 3, 1.0, 0.0, 1, GridBagConstraints.BOTH));
                typeconsole.add(button6,getGridBagConstraints(2, 3, 1.0, 0.0, 1, GridBagConstraints.BOTH));
                typeconsole.add(new JScrollPane(textArea),getGridBagConstraints(1, 1, 1.0, 1.0, 5, GridBagConstraints.BOTH));

                typeconsole.setAlwaysOnTop(true);
		typeconsole.setVisible(true);
		typeconsole.addWindowListener(this);	
		button3.addActionListener(this);	
                button3.setFocusable(false);
		button4.addActionListener(this);	
                button4.setFocusable(false);
		button5.addActionListener(this);	
                button5.setFocusable(false);
		button6.addActionListener(this);	
                button6.setFocusable(false);
                open();
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
		typeconsole.dispose();
	}
	
	public synchronized void actionPerformed(ActionEvent evt)
	{

                if (evt.getSource() == button3){
                if (!textArea.getText().equals("")){
		textArea.selectAll();
                textArea.copy();
                textArea.paste();}
                }
                if (evt.getSource() == button4)
		textArea.paste();
                if (evt.getSource() == button5) {
                    open();
                }
                if (evt.getSource() == button6) {
                    save();
                }
               
                    //textArea.paste();
	}

    public static void open()
    {
          File file;
          if (Switches.executable)
              file = new File(System.getProperty("/"), "javacpc.ini");
          else
              file = new File(System.getProperty("user.home"), "javacpc.ini");
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
 
        try
        {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            while ((text = reader.readLine()) != null)
            {
                contents.append(text)
                    .append(System.getProperty(
                        "line.separator"));
            }
        } catch (FileNotFoundException e)
        {
            
        } catch (IOException e)
        {
            
        } finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            } catch (IOException e)
            {
                
            }
        }
        textArea.setText(contents.toString());
    }   

      
      public static void save(){
          File file;
          if (Switches.executable)
              file = new File(System.getProperty("/"), "javacpc.ini");
          else
              file = new File(System.getProperty("user.home"), "javacpc.ini");
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
}
