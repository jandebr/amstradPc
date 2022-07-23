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

@SuppressWarnings("unchecked")
public class samp2cdt {

    public String[] Info = {
        "Autodetect",
        "Use Alternative way to recognise 0 or 1 bit!",
        "Use 'Appleby' loader algorithm",
"Use 'Cassys' loader algorithm",
"Use 'Standard' loader algorithm",
"Use 'Micro-Key' loader algorithm",
"Use 'Spectrum variant 3' loader algorithm",
"Use 'Speedlock Data' loader algorithm",
"Use 'Speedlock Data 2' loader algorithm",
"Use 'Speedlock Data 3' loader algorithm",
"Use 'Bleepload V1' loader algorithm",
"Use 'Codemasters' loader algorithm",
"Use 'Ricochet' loader algorithm",
"Use 'Bleepload V2' loader algorithm",
"Use 'Spectrum ROM' loader algorithm",
"Use 'US Gold' loader algorithm",
"Ignore Last Byte if it has less than 8 bits",
"DON'T make Bit1 h-p == 2*Bit0 h-p",
"Skip n blocks before forcing decoder",
"n;    In HPs - Min. Number of Pilot HPs for a block",
"n;    In %% - Max. Treshold between Two HPs of Pilot",
"Use Middle Value of bit 0 and 1 !",
"n;    In %% - Min. Treshold between 0 and 1 bit HPs",
"n;    In %% - Min. Treshold between Two HPs to END block",
"n; block number that Speedlock V2 style block starts",
"n; block number to stop processing defined loader",
"Auto detect Speedlock V1 blocks",
"Use 'Speedlock V0' loader algorithm",
"Use 'Speedlock V1' loader algorithm",
"Use 'Speedlock V2' loader algorithm",
"Use 'Speedlock V3' loader algorithm",
"Use 'Speedlock V4' loader algorithm"
    };
    public String[] Switches = {
        "Autodetect",
        "/alter" ,      //1 Use Alternative way to recognise 0 or 1 bit!
        "/appleby" ,    //2 Use 'Appleby' loader algorithm
        "/cassys" ,     //3 Use 'Cassys' loader algorithm
        "/standard" ,   //4 Use 'Standard' loader algorithm
        "/microkey" ,   //5 Use 'Micro-Key' loader algorithm
        "/specvar3" ,   //6 Use 'Spectrum variant 3' loader algorithm
        "/sldata" ,     //7 Use 'Speedlock Data' loader algorithm
        "/sldata2" ,    //8 Use 'Speedlock Data 2' loader algorithm
        "/sldata3" ,    //9 Use 'Speedlock Data 3' loader algorithm
        "/bleepv1" ,    //10 Use 'Bleepload V1' loader algorithm
        "/codem" ,      //11 Use 'Codemasters' loader algorithm
        "/ricochet" ,   //12 Use 'Ricochet' loader algorithm
        "/bleepv2" ,    //13 Use 'Bleepload V2' loader algorithm
        "/spec" ,       //14 Use 'Spectrum ROM' loader algorithm
        "/usgold" ,     //15 Use 'US Gold' loader algorithm
        "/ignore" ,     //16 Ignore Last Byte if it has less than 8 bits
        "/noaprox" ,    //17 DON'T make Bit1 h-p == 2*Bit0 h-p
        "/ldrskip" ,    //18 Skip n blocks before forcing decoder
        "/pilot" ,      //19 n;    In HPs - Min. Number of Pilot HPs for a block
        "/maxp" ,       //20 n;    In %% - Max. Treshold between Two HPs of Pilot
        "/middle" ,     //21 Use Middle Value of bit 0 and 1 !
        "/diff" ,       //22 n;    In %% - Min. Treshold between 0 and 1 bit HPs
        "/end" ,        //23 n;    In %% - Min. Treshold between Two HPs to END block
        "/slmain" ,     //24 n; block number that Speedlock V2 style block starts
        "/ldrend" ,     //25 n; block number to stop processing defined loader
        "/slauto" ,     //26 Auto detect Speedlock V1 blocks
        "/slock0" ,     //27 Use 'Speedlock V0' loader algorithm
        "/slock1" ,     //28 Use 'Speedlock V1' loader algorithm
        "/slock2" ,     //29 Use 'Speedlock V2' loader algorithm
        "/slock3" ,     //30 Use 'Speedlock V3' loader algorithm
        "/slock4"       //31 Use 'Speedlock V4' loader algorithm

    };
	protected final JComboBox switches = new JComboBox(Switches);
    protected final JComboBox switchesA = new JComboBox(Switches);
    protected final JComboBox switchesB = new JComboBox(Switches);
    protected final JComboBox switchesC = new JComboBox(Switches);
    protected final JComboBox switchesD = new JComboBox(Switches);
    protected final JComboBox switchesE = new JComboBox(Switches);
    protected JLabel switchlabel = new JLabel("Switches:");
    public  String CDTname;
    public  JLabel head = new JLabel("samp2cdt - GUI by Devilmarkus - Creates CDT from audiofiles.");
    public  JTextArea output = new JTextArea("samp2cdt-GUI\n");
    public  JTextField filename, file1, switchinfo, switchinfoA,switchinfoB,
            switchinfoC,switchinfoD,switchinfoE, value, valueA, valueB
            , valueC, valueD, valueE;
    public JFrame dummy = new JFrame();
      protected static GridBagConstraints gbcConstraints   = null;

  public  GridBagConstraints getGridBagConstraints(int x, int y, double weightx,double weighty,
    int width, int fill)
  {
    if (samp2cdt.gbcConstraints == null) {
      samp2cdt.gbcConstraints = new GridBagConstraints();
    }
    samp2cdt.gbcConstraints.gridx = x;
    samp2cdt.gbcConstraints.gridy = y;
    samp2cdt.gbcConstraints.weightx = weightx;
    samp2cdt.gbcConstraints.weighty = weighty;
    samp2cdt.gbcConstraints.gridwidth = width;
    samp2cdt.gbcConstraints.fill = fill;
    return samp2cdt.gbcConstraints;
  }

public void samp2CDT (){
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
    switchinfo = new JTextField("                                                                        ");
    switchinfoA = new JTextField("                                                                        ");
    switchinfoB = new JTextField("                                                                        ");
    switchinfoC = new JTextField("                                                                        ");
    switchinfoD = new JTextField("                                                                        ");
    switchinfoE = new JTextField("                                                                        ");

    value = new JTextField("");
    valueA = new JTextField("");
    valueB = new JTextField("");
    valueC = new JTextField("");
    valueD = new JTextField("");
    valueE = new JTextField("");
    
    JButton create = new JButton("Create");
    JButton sample = new JButton("Select sample file");
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
    quit.setBorder (new BevelBorder(BevelBorder.RAISED));
   // output.setRows(11);
    build.setBorder (new BevelBorder(BevelBorder.RAISED));
    frame.add(head,getGridBagConstraints(1, 0, 1.0, 1.0, 3, GridBagConstraints.BOTH));
    frame.add(filename,getGridBagConstraints(2, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(create,getGridBagConstraints(3, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(file1,getGridBagConstraints(2, 2, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(sample,getGridBagConstraints(3, 2, 0.0, 0.0, 1, GridBagConstraints.BOTH));

    frame.add(switchlabel,getGridBagConstraints(2, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(switches,getGridBagConstraints(3, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(value,getGridBagConstraints(4, 3, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(valueA,getGridBagConstraints(4, 5, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(valueB,getGridBagConstraints(4, 7, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(valueC,getGridBagConstraints(4, 9, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(valueD,getGridBagConstraints(4, 11, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(valueE,getGridBagConstraints(4, 13, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(switchinfo,getGridBagConstraints(1, 4, 0.0, 0.0, 3, GridBagConstraints.BOTH));
    frame.add(switchesA,getGridBagConstraints(3, 5, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(switchinfoA,getGridBagConstraints(1, 6, 0.0, 0.0, 3, GridBagConstraints.BOTH));
    frame.add(switchesB,getGridBagConstraints(3, 7, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(switchinfoB,getGridBagConstraints(1, 8, 0.0, 0.0, 3, GridBagConstraints.BOTH));
    frame.add(switchesC,getGridBagConstraints(3, 9, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(switchinfoC,getGridBagConstraints(1, 10, 0.0, 0.0, 3, GridBagConstraints.BOTH));
    frame.add(switchesD,getGridBagConstraints(3, 11, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(switchinfoD,getGridBagConstraints(1, 12, 0.0, 0.0, 3, GridBagConstraints.BOTH));
    frame.add(switchesE,getGridBagConstraints(3, 13, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(switchinfoE,getGridBagConstraints(1, 14, 0.0, 0.0, 3, GridBagConstraints.BOTH));


    value.setEnabled(false);
    valueA.setEnabled(false);
    valueB.setEnabled(false);
    valueC.setEnabled(false);
    valueD.setEnabled(false);
    valueE.setEnabled(false);

    frame.add(scrollpane,getGridBagConstraints(1, 15, 0.0, 0.0, 3, GridBagConstraints.BOTH));
    frame.add(build,getGridBagConstraints(2, 16, 0.0, 0.0, 1, GridBagConstraints.BOTH));
    frame.add(quit,getGridBagConstraints(4, 16, 0.0, 0.0, 1, GridBagConstraints.BOTH));

    frame.pack();
    final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
    frame.setResizable(false);
          switches.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             int choosen = switches.getSelectedIndex();
             if (choosen == 19 || choosen == 20 || choosen == 22 ||
                     choosen == 23 || choosen == 24 || choosen == 25){
                 value.setEnabled(true);
             } else {
                 value.setEnabled(false);
                 value.setText("");
             }
             String SwitchInfos = Info[choosen];
             switchinfo.setText(SwitchInfos);
         }
          });
          switchesA.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             int choosen = switchesA.getSelectedIndex();
             if (choosen == 19 || choosen == 20 || choosen == 22 ||
                     choosen == 23 || choosen == 24 || choosen == 25){
                 valueA.setEnabled(true);
             } else {
                 valueA.setEnabled(false);
                 valueA.setText("");
             }
             String switchinfos = Info[choosen];
             switchinfoA.setText(switchinfos);
         }
          });
          switchesB.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             int choosen = switchesB.getSelectedIndex();
             if (choosen == 19 || choosen == 20 || choosen == 22 ||
                     choosen == 23 || choosen == 24 || choosen == 25){
                 valueB.setEnabled(true);
             } else {
                 valueB.setEnabled(false);
                 valueB.setText("");
             }
             String switchinfos = Info[choosen];
             switchinfoB.setText(switchinfos);
         }
          });
          switchesC.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             int choosen = switchesC.getSelectedIndex();
             if (choosen == 19 || choosen == 20 || choosen == 22 ||
                     choosen == 23 || choosen == 24 || choosen == 25){
                 valueC.setEnabled(true);
             } else {
                 valueC.setEnabled(false);
                 valueC.setText("");
             }
             String switchinfos = Info[choosen];
             switchinfoC.setText(switchinfos);
         }
          });
          switchesD.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             int choosen = switchesD.getSelectedIndex();
             if (choosen == 19 || choosen == 20 || choosen == 22 ||
                     choosen == 23 || choosen == 24 || choosen == 25){
                 valueD.setEnabled(true);
             } else {
                 valueD.setEnabled(false);
                 valueD.setText("");
             }
             String switchinfos = Info[choosen];
             switchinfoD.setText(switchinfos);
         }
          });
          switchesE.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             int choosen = switchesE.getSelectedIndex();
             if (choosen == 19 || choosen == 20 || choosen == 22 ||
                     choosen == 23 || choosen == 24 || choosen == 25){
                 valueE.setEnabled(true);
             } else {
                 valueE.setEnabled(false);
                 valueE.setText("");
             }
             String switchinfos = Info[choosen];
             switchinfoE.setText(switchinfos);
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
          sample.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
          FileDialog filedia = new FileDialog((Frame) dummy, "Create CDT file", FileDialog.LOAD);
          filedia.setFile("*.wav; *.voc; *.csw; *.iff; *.aiff");
          filedia.setVisible(true);
          String filenamed = filedia.getFile();
          if (filenamed != null) {
              filenamed =  filedia.getDirectory() + filedia.getFile();
              String loadname = filenamed;
              file1.setText(loadname);
              CDTname = filedia.getFile();
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
         SAMP2CDT();}
      });

}

public void SAMP2CDT(){
    int choosen = switches.getSelectedIndex();
    String Switch = Switches[choosen];
    if (Switch.equals("Autodetect"))
        Switch ="";
    else
        Switch +=" ";
    choosen = switchesA.getSelectedIndex();
    String SwitchA = Switches[choosen];
    if (SwitchA.equals("Autodetect"))
        SwitchA ="";
    else
        SwitchA +=" ";
    choosen = switchesB.getSelectedIndex();
    String SwitchB = Switches[choosen];
    if (SwitchB.equals("Autodetect"))
        SwitchB ="";
    else
        SwitchB +=" ";
    choosen = switchesC.getSelectedIndex();
    String SwitchC = Switches[choosen];
    if (SwitchC.equals("Autodetect"))
        SwitchC ="";
    else
        SwitchC +=" ";
    choosen = switchesD.getSelectedIndex();
    String SwitchD = Switches[choosen];
    if (SwitchD.equals("Autodetect"))
        SwitchD ="";
    else
        SwitchD +=" ";
    choosen = switchesE.getSelectedIndex();
    String SwitchE = Switches[choosen];
    if (SwitchE.equals("Autodetect"))
        SwitchE ="";
    else
        SwitchE +=" ";
    if (!value.getText().equals(""))
        Switch += value.getText() + " ";
    if (!valueA.getText().equals(""))
        SwitchA += valueA.getText() + " ";
    if (!valueB.getText().equals(""))
        SwitchB += valueB.getText() + " ";
    if (!valueC.getText().equals(""))
        SwitchC += valueC.getText() + " ";
    if (!valueD.getText().equals(""))
        SwitchD += valueD.getText() + " ";
    if (!valueE.getText().equals(""))
        SwitchE += valueE.getText() + " ";
    String cdtname = filename.getText();
    if (cdtname.equalsIgnoreCase("                                                                        "))
        cdtname = new String();
    String sampname = file1.getText();
    if (sampname.equalsIgnoreCase("                                                                        "))
        sampname = null;
    if (cdtname != null && sampname != null)
                   try {
                       String command = "tools/samp2cdt.exe " + Switch +  SwitchA +
                               SwitchB + SwitchC + SwitchD + SwitchE + "\"" +
                               sampname + "\" " + "\"" + cdtname + "\"";
                        output.append(command+"\n");
                        Runtime.getRuntime().exec(command);
                        try {Thread.sleep(5500);} catch (Exception e){}
                        JOptionPane.showMessageDialog(null, "Successfully created.");
                   } catch( IOException ex) {

                    JOptionPane.showMessageDialog(null, "An error occured");
                   }
    else
        JOptionPane.showMessageDialog(null, "An error occured");
}

}