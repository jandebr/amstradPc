package jemu.ui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Markus
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class File2CDT {
    public  String CDTname;
    public  JLabel head = new JLabel("2CDT - GUI by Devilmarkus - Creates CDT from CPC files.                                         ");
    public  JLabel files = new JLabel("Add files to your CDT: (minimum is 1 file)");
    public  JTextArea output = new JTextArea("2CDT-GUI\n");
    public  JTextField filename;
    public  JTextField file1;
    public  JTextField file2;
    public  JTextField file3;
    public  JTextField file4;
    public  JTextField file5;
    public  JTextField file6;
    public JTextField file7;
    public JTextField file8;
    public JTextField file9;
    public JTextField file10;
    public JLabel name1;
    public JTextField name2;
    public JTextField name3;
    public JTextField name4;
    public JTextField name5;
    public JTextField name6;
    public JTextField name7;
    public JTextField name8;
    public JTextField name9;
    public JTextField name10;
    public JTextField name11;
    public JLabel headtext = new JLabel("   Choose fileformat:   ");
    public JButton speedwrite = new JButton("Speed write 1");
    public int speed = 1;
    public JButton header1 = new JButton("      Header      ");
    public JButton header2 = new JButton("      Header      ");
    public JButton header3 = new JButton("      Header      ");
    public JButton header4 = new JButton("      Header      ");
    public JButton header5 = new JButton("      Header      ");
    public JButton header6 = new JButton("      Header      ");
    public JButton header7 = new JButton("      Header      ");
    public JButton header8 = new JButton("      Header      ");
    public JButton header9 = new JButton("      Header      ");
    public JButton turbo = new JButton("");
    public int m1 = 0;
    public int m2 = 0;
    public int m3 = 0;
    public int m4 = 0;
    public int m5 = 0;
    public int m6 = 0;
    public int m7 = 0;
    public int m8 = 0;
    public int m9 = 0;
    public JFrame dummy = new JFrame();
      protected static GridBagConstraints gbcConstraints   = null;

  public  GridBagConstraints getGridBagConstraints(int x, int y, double weightx,double weighty,
    int width, int fill)
  {
    if (File2CDT.gbcConstraints == null) {
      File2CDT.gbcConstraints = new GridBagConstraints();
    }
    File2CDT.gbcConstraints.gridx = x;
    File2CDT.gbcConstraints.gridy = y;
    File2CDT.gbcConstraints.weightx = weightx;
    File2CDT.gbcConstraints.weighty = weighty;
    File2CDT.gbcConstraints.gridwidth = width;
    File2CDT.gbcConstraints.fill = fill;
    return File2CDT.gbcConstraints;
  }

public void makeCDT (){
    final JFrame frame = new JFrame (){
        protected void processWindowEvent(WindowEvent we) {
            if (we.getID() == WindowEvent.WINDOW_CLOSING)
                this.dispose();
        }
    };
    frame.setBackground(Color.DARK_GRAY);
    frame.setTitle("2CDT GUI");
    frame.setLayout(new GridBagLayout());
    filename = new JTextField("                                                                        ");
    file1 = new JTextField("                                                                        ");
    file2 = new JTextField("                                                                        ");
    file3 = new JTextField("                                                                        ");
    file4 = new JTextField("                                                                        ");
    file5 = new JTextField("                                                                        ");
    file6 = new JTextField("                                                                        ");
    file7 = new JTextField("                                                                        ");
    file8 = new JTextField("                                                                        ");
    file9 = new JTextField("                                                                        ");
    file10 = new JTextField("                                                                        ");
    name1 = new JLabel("Filename: (16 chars)      ");
    name2 = new JTextField("unnamed file");
    name3 = new JTextField("unnamed file");
    name4 = new JTextField("unnamed file");
    name5 = new JTextField("unnamed file");
    name6 = new JTextField("unnamed file");
    name7 = new JTextField("unnamed file");
    name8 = new JTextField("unnamed file");
    name9 = new JTextField("unnamed file");
    name10 = new JTextField("unnamed file");
    name11 = new JTextField("unnamed file");

    JButton create = new JButton("Create");
    JButton load1 = new JButton("Add");
    JButton load2 = new JButton("Add");
    JButton load3 = new JButton("Add");
    JButton load4 = new JButton("Add");
    JButton load5 = new JButton("Add");
    JButton load6 = new JButton("Add");
    JButton load7 = new JButton("Add");
    JButton load8 = new JButton("Add");
    JButton load9 = new JButton("Add");
    JButton load10 = new JButton("Add");
    JButton build = new JButton("Build");
    JButton quit = new JButton("Quit");
    output.setRows(10);
    output.setAutoscrolls(true);
    output.setLineWrap(true);
    output.setEditable(false);
    output.setFont(new Font("Courier" , 1,11));
    output.setBorder (new BevelBorder(BevelBorder.LOWERED));
                JScrollPane scrollpane = new JScrollPane(output);
    create.setBorder (new BevelBorder(BevelBorder.RAISED));
    load1.setBorder (new BevelBorder(BevelBorder.RAISED));
    load2.setBorder (new BevelBorder(BevelBorder.RAISED));
    load3.setBorder (new BevelBorder(BevelBorder.RAISED));
    load4.setBorder (new BevelBorder(BevelBorder.RAISED));
    load5.setBorder (new BevelBorder(BevelBorder.RAISED));
    load6.setBorder (new BevelBorder(BevelBorder.RAISED));
    load7.setBorder (new BevelBorder(BevelBorder.RAISED));
    load8.setBorder (new BevelBorder(BevelBorder.RAISED));
    load9.setBorder (new BevelBorder(BevelBorder.RAISED));
    load10.setBorder (new BevelBorder(BevelBorder.RAISED));
    quit.setBorder (new BevelBorder(BevelBorder.RAISED));
    header1.setBorder (new BevelBorder(BevelBorder.RAISED));
    header2.setBorder (new BevelBorder(BevelBorder.RAISED));
    header3.setBorder (new BevelBorder(BevelBorder.RAISED));
    header4.setBorder (new BevelBorder(BevelBorder.RAISED));
    header5.setBorder (new BevelBorder(BevelBorder.RAISED));
    header6.setBorder (new BevelBorder(BevelBorder.RAISED));
    header7.setBorder (new BevelBorder(BevelBorder.RAISED));
    header8.setBorder (new BevelBorder(BevelBorder.RAISED));
    header9.setBorder (new BevelBorder(BevelBorder.RAISED));
    speedwrite.setBorder (new BevelBorder(BevelBorder.RAISED));
    turbo.setBorder (new BevelBorder(BevelBorder.RAISED));
    turbo.setEnabled(false);
   // output.setRows(11);
    build.setBorder (new BevelBorder(BevelBorder.RAISED));
    frame.add(head,getGridBagConstraints(1, 0, 1.0, 1.0, 3, GridBagConstraints.BOTH));
    frame.add(name1,getGridBagConstraints(1, 2, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(filename,getGridBagConstraints(2, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(create,getGridBagConstraints(3, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(files,getGridBagConstraints(2, 2, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name2,getGridBagConstraints(1, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file1,getGridBagConstraints(2, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load1,getGridBagConstraints(3, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name3,getGridBagConstraints(1, 4, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file2,getGridBagConstraints(2, 4, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load2,getGridBagConstraints(3, 4, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name4,getGridBagConstraints(1, 5, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file3,getGridBagConstraints(2, 5, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load3,getGridBagConstraints(3, 5, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name5,getGridBagConstraints(1, 6, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file4,getGridBagConstraints(2, 6, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load4,getGridBagConstraints(3, 6, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name6,getGridBagConstraints(1, 7, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file5,getGridBagConstraints(2, 7, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load5,getGridBagConstraints(3, 7, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name7,getGridBagConstraints(1, 8, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file6,getGridBagConstraints(2, 8, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load6,getGridBagConstraints(3, 8, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name8,getGridBagConstraints(1, 9, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file7,getGridBagConstraints(2, 9, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load7,getGridBagConstraints(3, 9, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name9,getGridBagConstraints(1, 10, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file8,getGridBagConstraints(2, 10, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load8,getGridBagConstraints(3, 10, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name10,getGridBagConstraints(1, 11, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file9,getGridBagConstraints(2, 11, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load9,getGridBagConstraints(3, 11, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(name11,getGridBagConstraints(1, 12, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file10,getGridBagConstraints(2, 12, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(load10,getGridBagConstraints(3, 12, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(speedwrite,getGridBagConstraints(1, 13, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(build,getGridBagConstraints(2, 13, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(quit,getGridBagConstraints(4, 13, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(headtext,getGridBagConstraints(4, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(header1,getGridBagConstraints(4, 4, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(header2,getGridBagConstraints(4, 5, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(header3,getGridBagConstraints(4, 6, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(header4,getGridBagConstraints(4, 7, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(header5,getGridBagConstraints(4, 8, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(header6,getGridBagConstraints(4, 9, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(header7,getGridBagConstraints(4, 10, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(header8,getGridBagConstraints(4, 11, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(header9,getGridBagConstraints(4, 12, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(turbo,getGridBagConstraints(3, 13, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(scrollpane,getGridBagConstraints(1, 14, 1.0, 1.0, 4, GridBagConstraints.BOTH));
    frame.pack();
    final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
    frame.setResizable(false);
          speedwrite.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (speedwrite.getText().equals("Speed write 1")){
              speedwrite.setText("Speed write 0");
          speed = 0;
          }
          else {
              speedwrite.setText("Speed write 1");
          speed = 1;
          }
          }
          });
          header1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (header1.getText().equals("      Header      ")){
              header1.setText("Headerless");
              m1=1;
          }
          else {
              header1.setText("      Header      ");
              m1=0;
          }
          }
          });
          header2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (header2.getText().equals("      Header      ")){
              header2.setText("Headerless");
              m2=1;
          }
          else {
              header2.setText("      Header      ");
              m2=0;
          }
          }
          });
          header3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (header3.getText().equals("      Header      ")){
              header3.setText("Headerless");
              m3=1;
          }
          else {
              header3.setText("      Header      ");
              m3=0;
          }
          }
          });
          header4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (header4.getText().equals("      Header      ")){
              header4.setText("Headerless");
              m4=1;
          }
          else {
              header4.setText("      Header      ");
              m4=0;
          }
          }
          });
          header5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (header5.getText().equals("      Header      ")){
              header5.setText("Headerless");
              m5=1;
          }
          else {
              header5.setText("      Header      ");
              m5=0;
          }
          }
          });
          header6.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (header6.getText().equals("      Header      ")){
              header6.setText("Headerless");
              m6=1;
          }
          else {
              header6.setText("      Header      ");
              m6=0;
          }
          }
          });
          header7.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (header7.getText().equals("      Header      ")){
              header7.setText("Headerless");
              m7=1;
          }
          else {
              header7.setText("      Header      ");
              m7=0;
          }
          }
          });
          header8.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (header8.getText().equals("      Header      ")){
              header8.setText("Headerless");
              m8=1;
          }
          else {
              header8.setText("      Header      ");
              m8=0;
          }
          }
          });
          header9.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          if (header9.getText().equals("      Header      ")){
              header9.setText("Headerless");
              m9=1;
          }
          else {
              header9.setText("      Header      ");
              m9=0;
          }
          }
          });
          create.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Create CDT file", FileDialog.SAVE);
          filedia.setFile("*.cdt");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              if (!filenamed.toLowerCase().endsWith(".cdt"))
                  filenamed = filenamed + ".cdt";
              String loadname = filenamed;
              filename.setText(loadname);
              CDTname = filedia.getFile();
          }
         }
          });
          load1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file1.setText(loadname);
              name2.setText(filedia.getFile());
          }
         }
          });
          load2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file2.setText(loadname);
              name3.setText(filedia.getFile());
          }
         }
          });
          load3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file3.setText(loadname);
              name4.setText(filedia.getFile());
          }
         }
          });
          load4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file4.setText(loadname);
              name5.setText(filedia.getFile());
          }
         }
          });
          load5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file5.setText(loadname);
              name6.setText(filedia.getFile());
          }
         }
          });
          load6.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file6.setText(loadname);
              name7.setText(filedia.getFile());
          }
         }
          });
          load7.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file7.setText(loadname);
              name8.setText(filedia.getFile());
          }
         }
          });
          load8.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file8.setText(loadname);
              name9.setText(filedia.getFile());
          }
         }
          });
          load9.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file9.setText(loadname);
              name10.setText(filedia.getFile());
          }
         }
          });
          load10.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Add CPC file", FileDialog.LOAD);
          filedia.setFile("*.*");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file10.setText(loadname);
              name11.setText(filedia.getFile());
          }
         }
          });

          quit.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {frame.dispose();}
      });
          build.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {output.setText("processing...\n");
         output.updateUI();
         buildCDT();}
      });

}

public void buildCDT(){
    String loadname = filename.getText();
    if (loadname.equalsIgnoreCase("                                                                        "))
        loadname = new String();
    String firstname = file1.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;
    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name2.getText();
                       String command = "tools/2cdt.exe -s " + speed + " -n -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                        AddSecond();
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
    else
        JOptionPane.showMessageDialog(null, "An error occured");
}

public void AddSecond(){
    String loadname = filename.getText();
    String firstname = file2.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;


    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name3.getText();
                       String command = "tools/2cdt.exe -m " + m1 + " -s " + speed + " -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
                        AddThird();
}
public void AddThird(){
    String loadname = filename.getText();
    String firstname = file3.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;


    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name4.getText();
                       String command = "tools/2cdt.exe -m " + m2 + " -s " + speed + " -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
                        AddFourth();
}
public void AddFourth(){
    String loadname = filename.getText();
    String firstname = file4.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;


    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name5.getText();
                       String command = "tools/2cdt.exe -m " + m3 + " -s " + speed + " -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
                        AddFifth();
}
public void AddFifth(){
    String loadname = filename.getText();
    String firstname = file5.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;


    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name6.getText();
                       String command = "tools/2cdt.exe -m " + m4 + " -s " + speed + " -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
                        AddSixth();
}
public void AddSixth(){
    String loadname = filename.getText();
    String firstname = file6.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;


    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name7.getText();
                       String command = "tools/2cdt.exe -m " + m5 + " -s " + speed + " -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
                        AddSeventh();
}
public void AddSeventh(){
    String loadname = filename.getText();
    String firstname = file7.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;


    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name8.getText();
                       String command = "tools/2cdt.exe -m " + m6 + " -s " + speed + " -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
                        AddEigth();
}
public void AddEigth(){
    String loadname = filename.getText();
    String firstname = file8.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;


    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name9.getText();
                       String command = "tools/2cdt.exe -m " + m7 + " -s " + speed + " -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
                        AddNineth();
}
public void AddNineth(){
    String loadname = filename.getText();
    String firstname = file9.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;


    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name10.getText();
                       String command = "tools/2cdt.exe -m " + m8 + " -s " + speed + " -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
                        AddTenth();
}
public void AddTenth(){
    String loadname = filename.getText();
    String firstname = file10.getText();
    if (firstname.equalsIgnoreCase("                                                                        "))
        firstname = null;


    if (loadname != null && firstname != null)
                   try {
                       String dat1 = name11.getText();
                       String command = "tools/2cdt.exe -m " + m9 + " -s " + speed + " -r \"" + dat1 + "\" \"" + firstname + "\" \"" + loadname+"\"";
                        Runtime.getRuntime().exec(command);
                        output.append(command+"\n");
                        try {Thread.sleep(500);} catch (Exception e){}
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }

                    JOptionPane.showMessageDialog(null, CDTname + "\nsuccessfully created...");
}
}