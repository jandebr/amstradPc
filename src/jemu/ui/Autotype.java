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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;

import jemu.system.cpc.CPCPrinter;

public class Autotype extends WindowAdapter implements WindowListener, ActionListener {
	JFileChooser saveFileChooser = new JFileChooser(System.getProperty("/"));
	static JFileChooser loadFileChooser = new JFileChooser(System.getProperty("/"));
	protected GridBagConstraints gbcConstraints = null;
	public static JFrame typeconsole;
	public static String autotext;
	public static JTextArea textArea;
	public JButton button = new JButton(" Send text ");
	public JButton button2 = new JButton(" Clear console ");
	public JButton button3 = new JButton(" Copy text ");
	public JButton button4 = new JButton(" Paste text ");
	public static JButton button5 = new JButton(" Load... ");
	public static JButton button6 = new JButton(" Save... ");
	protected Font font;
	protected Color CPC_BLUE = new Color(00, 00, 0x7f);
	protected Color CPC_GRAY = new Color(0x7f, 0x7f, 0x7f);

	public Autotype() {
		typeconsole = new JFrame("JavaCPC Autotype");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		int x = (frameSize.width / 2 + 110);
		int y = (frameSize.height / 2 + 60);
		typeconsole.setBounds(x, y, 340, 380);
		textArea = new JTextArea();
		typeconsole.setResizable(false);
		textArea.setLineWrap(true);
		textArea.setEditable(true);
		textArea.setAutoscrolls(true);
		textArea.setBackground(Color.WHITE);
		textArea.setForeground(Color.BLACK);
		textArea.setCaretColor(Color.BLACK);
		textArea.setCaret(new DefaultCaret());

		textArea.setSelectedTextColor(Color.DARK_GRAY);
		textArea.setSelectionColor(Color.LIGHT_GRAY);
		textArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
		InputStream in = getClass().getResourceAsStream("amstrad.ttf");
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(Font.PLAIN, 8);
		} catch (Exception e) {
		}
		textArea.setFont(this.font);
		button.setForeground(Color.GREEN);
		button.setBackground(CPC_BLUE);
		button.setFont(this.font);
		button.setBorder(new BevelBorder(BevelBorder.RAISED));
		button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		button2.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		button2.setForeground(Color.RED);
		button2.setBackground(CPC_BLUE);
		button2.setFont(this.font);
		button2.setBorder(new BevelBorder(BevelBorder.RAISED));
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
		typeconsole.add(button, getGridBagConstraints(1, 2, 1.0, 0.0, 1, GridBagConstraints.BOTH));
		typeconsole.add(button2, getGridBagConstraints(2, 2, 1.0, 0.0, 1, GridBagConstraints.BOTH));
		typeconsole.add(button3, getGridBagConstraints(3, 2, 1.0, 0.0, 1, GridBagConstraints.BOTH));
		typeconsole.add(button4, getGridBagConstraints(3, 3, 1.0, 0.0, 1, GridBagConstraints.BOTH));
		typeconsole.add(button5, getGridBagConstraints(1, 3, 1.0, 0.0, 1, GridBagConstraints.BOTH));
		typeconsole.add(button6, getGridBagConstraints(2, 3, 1.0, 0.0, 1, GridBagConstraints.BOTH));
		typeconsole.add(new JScrollPane(textArea), getGridBagConstraints(1, 1, 1.0, 1.0, 5, GridBagConstraints.BOTH));

		typeconsole.setAlwaysOnTop(true);
		typeconsole.setVisible(false);
		typeconsole.addWindowListener(this);
		button.addActionListener(this);
		button.setFocusable(false);
		button2.addActionListener(this);
		button2.setFocusable(false);
		button3.addActionListener(this);
		button3.setFocusable(false);
		button4.addActionListener(this);
		button4.setFocusable(false);
		button5.addActionListener(this);
		button5.setFocusable(false);
		button6.addActionListener(this);
		button6.setFocusable(false);
		open();
		if (textArea.getText().length() >= 1)
			Display.atmessage = 200;
	}

	private GridBagConstraints getGridBagConstraints(int x, int y, double weightx, double weighty, int width, int fill) {
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

	@Override
	public synchronized void windowClosing(WindowEvent evt) {
		save();
		typeconsole.setVisible(false);
	}

	public synchronized void actionPerformed(ActionEvent evt) {

		if (evt.getSource() == button) {
			typeconsole.setVisible(false);
			SendToCPC();
		}
		if (evt.getSource() == button2) {
			textArea.setText("");
			save();
		}
		if (evt.getSource() == button3) {
			if (!textArea.getText().equals("")) {
				textArea.selectAll();
				textArea.copy();
				textArea.paste();
			}
		}
		if (evt.getSource() == button4)
			textArea.paste();
		if (evt.getSource() == button5) {
			openFile();
		}
		if (evt.getSource() == button6) {
			saveFile();
		}
	}

	public static void typeText(CharSequence text) {
		save();
		textArea.setText(text.toString());
		SendToCPC();
		open();
	}

	public static void clearText() {
		textArea.setText("");
		save();
	}

	public static void PasteText() {
		// typeconsole.setVisible(false);
		save();
		textArea.setText("");
		textArea.paste();
		textArea.selectAll();
		autotext = textArea.getText();
		if (CPCPrinter.Processed) {
			autotext = "\n" + autotext;
			CPCPrinter.Processed = false;
		}
		// System.out.println("Autotext is:\n"+autotext);
		textArea.selectAll();
		Switches.getfromautotype = 1;
		open();
	}

	public static void SendToCPC() {
		textArea.selectAll();
		autotext = textArea.getText();
		if (CPCPrinter.Processed) {
			autotext = "\n" + autotext;
			CPCPrinter.Processed = false;
		}
		// System.out.println("Autotext is:\n"+autotext);
		Switches.getfromautotype = 1;
		textArea.selectAll();
		// typeconsole.setVisible(false);
	}

	public void openFile() {
		FileDialog filedia = new FileDialog((Frame) typeconsole, "Import ASCII...", FileDialog.LOAD);
		filedia.setFile("*.txt; *.asm; *.bas");
		filedia.setVisible(true);
		String filename = filedia.getFile();
		if (filename != null) {
			filename = filedia.getDirectory() + filedia.getFile();
			String loadname = filename;
			File file = new File(loadname);
			StringBuffer contents = new StringBuffer();
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(file));
				String text = null;
				while ((text = reader.readLine()) != null) {
					contents.append(text).append(System.getProperty("line.separator"));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			textArea.setText(contents.toString());
		}
		filedia.dispose();
	}

	public static void loadFile() {
		save();
		FileDialog filedia = new FileDialog((Frame) typeconsole, "Import ASCII...", FileDialog.LOAD);
		filedia.setFile("*.txt; *.asm; *.bas");
		filedia.setVisible(true);
		String filename = filedia.getFile();
		if (filename != null) {
			filename = filedia.getDirectory() + filedia.getFile();
			String loadname = filename;

			File file = new File(loadname);
			StringBuffer contents = new StringBuffer();
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(file));
				String text = null;
				while ((text = reader.readLine()) != null) {
					contents.append(text).append(System.getProperty("line.separator"));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			textArea.setText(contents.toString());
			SendToCPC();
		}
		filedia.dispose();
		open();
	}

	public void saveFile() {
		FileDialog filedia = new FileDialog((Frame) typeconsole, "Save ASCII...", FileDialog.SAVE);
		filedia.setFile("*.txt; *.asm; *.bas");
		filedia.setVisible(true);
		String filename = filedia.getFile();
		if (filename != null) {
			filename = filedia.getDirectory() + filedia.getFile();
			String savename = filename;
			if (!savename.toLowerCase().endsWith(".txt"))
				if (!savename.toLowerCase().endsWith(".asm"))
					if (!savename.toLowerCase().endsWith(".bas"))
						savename = savename + ".txt";
			File file = new File(savename);
			String exportedText = textArea.getText();
			try {
				FileWriter fw = new FileWriter(file);
				fw.write(exportedText);
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		filedia.dispose();
	}

	public static void open() {
		File file = new File(System.getProperty("user.home"), "autotype.dat");
		StringBuffer importedText = new StringBuffer();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			while ((text = reader.readLine()) != null) {
				importedText.append(text).append(System.getProperty("line.separator"));
			}
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {

			}
		}
		textArea.setText(importedText.toString());
	}

	public static void save() {
		String exportedText = textArea.getText();
		File file = new File(System.getProperty("user.home"), "autotype.dat");
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(exportedText);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}