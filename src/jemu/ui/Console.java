package jemu.ui;

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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.maia.amstrad.AmstradFactory;
import org.maia.util.io.MultiplexOutputStream;

import jemu.settings.Settings;

/**
 * Title: JEMU by cpc.devilmarkus.de / jemu.winape.net Description: JAVA emulator for Amstrad Homecomputers Copyright:
 * Copyright (c) 2002-2008 Company:
 * 
 * @author Devilmarkus
 * @version 4.0
 */
public class Console extends WindowAdapter implements WindowListener, ActionListener {

	private JFileChooser saveFileChooser = new JFileChooser();
	private GridBagConstraints gbcConstraints = null;
	private JFrame frameconsole;
	private JTextArea textArea;
	private Font font;

	private JButton button = new JButton("Clear");
	private JButton button2 = new JButton("Copy");
	private JButton button3 = new JButton("Save");

	private ByteArrayOutputStream outputSink = new ByteArrayOutputStream(10000);

	private static Console instance;

	private Console() {
		// create all components and add them
		frameconsole = new JFrame("System logs");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		int x = (int) (frameSize.width / 2);
		int y = (int) (frameSize.height / 2);
		frameconsole.setBounds(x, y, frameSize.width, frameSize.height);

		textArea = new JTextArea();
		InputStream in = getClass().getResourceAsStream("amstrad.ttf");
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(Font.PLAIN, 8);
		} catch (Exception e) {
		}
		textArea.setFont(this.font);
		textArea.setEditable(false);
		textArea.setAutoscrolls(true);
		textArea.setBackground(new Color(0, 10, 0));
		textArea.setForeground(new Color(0, 229, 0));
		textArea.setCaretColor(textArea.getForeground());
		textArea.setSelectedTextColor(textArea.getBackground());
		textArea.setSelectionColor(Color.WHITE);
		textArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
		// textArea.setFont(new Font("", 1, 10));
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
		frameconsole.add(scrollpane, getGridBagConstraints(0, 0, 1.0, 1.0, 4, GridBagConstraints.BOTH));
		frameconsole.add(button, getGridBagConstraints(0, 1, 1.0, 0.0, 1, GridBagConstraints.BOTH));
		frameconsole.add(button2, getGridBagConstraints(1, 1, 1.0, 0.0, 1, GridBagConstraints.BOTH));
		frameconsole.add(button3, getGridBagConstraints(2, 1, 1.0, 0.0, 1, GridBagConstraints.BOTH));
		frameconsole.setAlwaysOnTop(true);
		frameconsole.setVisible(false);

		frameconsole.addWindowListener(this);
		button.addActionListener(this);
		button2.addActionListener(this);
		button.setFocusable(false);
		button2.setFocusable(false);
		button3.setFocusable(false);
		button3.addActionListener(this);

		// Stdout
		try {
			MultiplexOutputStream multiplexOut = new MultiplexOutputStream();
			OutputStream cos = AmstradFactory.getInstance().getAmstradContext().getConsoleOutputStream();
			if (cos != null)
				multiplexOut.addOutputStream(cos);
			if (Settings.getBoolean(Settings.CONSOLE, false)) {
				multiplexOut.addOutputStream(outputSink);
			}
			System.setOut(new PrintStream(multiplexOut, true));
		} catch (SecurityException se) {
			textArea.append("Couldn't redirect STDOUT to this console\n" + se.getMessage());
		}

		// Stderr
		try {
			MultiplexOutputStream multiplexOut = new MultiplexOutputStream();
			OutputStream ces = AmstradFactory.getInstance().getAmstradContext().getConsoleErrorStream();
			if (ces != null)
				multiplexOut.addOutputStream(ces);
			if (Settings.getBoolean(Settings.CONSOLE, false)) {
				multiplexOut.addOutputStream(outputSink);
			}
			System.setErr(new PrintStream(multiplexOut, true));
		} catch (SecurityException se) {
			textArea.append("Couldn't redirect STDERR to this console\n" + se.getMessage());
		}
	}

	private GridBagConstraints getGridBagConstraints(int x, int y, double weightx, double weighty, int width,
			int fill) {
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

	public static synchronized void init() {
		getInstance();
	}

	public static synchronized Console getInstance() {
		if (instance == null) {
			instance = new Console();
		}
		return instance;
	}

	public void showInFrame() {
		textArea.setText(outputSink.toString());
		frameconsole.setVisible(true);
	}

	public String getText() {
		return textArea.getText();
	}

	public synchronized void windowClosing(WindowEvent evt) {
		frameconsole.setVisible(false); // default behaviour of JFrame
		frameconsole.dispose();
	}

	public synchronized void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == button) {
			textArea.setText("");
			outputSink.reset();
		}
		if (evt.getSource() == button2) {
			if (textArea.getSelectedText() == null)
				textArea.selectAll();
			textArea.copy();
			System.out.println("Console content copied to clipboard.");
		}
		if (evt.getSource() == button3) {
			saveFile();
		}
	}

	public void saveFile() {
		FileDialog filedia = new FileDialog((Frame) frameconsole, "Save ASCII...", FileDialog.SAVE);
		filedia.setFile("*.txt");
		filedia.setVisible(true);
		String filename = filedia.getFile();
		if (filename != null) {
			filename = filedia.getDirectory() + filedia.getFile();
			String savename = filename;
			if (!savename.toLowerCase().endsWith(".txt"))
				savename = savename + ".txt";
			File file = new File(savename);
			String gettext = getText();
			try {
				FileWriter fw = new FileWriter(file);
				fw.write(gettext);
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		filedia.dispose();
	}

}