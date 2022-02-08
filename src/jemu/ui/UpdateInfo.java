package jemu.ui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import jemu.core.samples.*;

import jemu.system.cpc.CPC;
/**
 *
 * @author Markus
 */
public class UpdateInfo extends Frame{
    JFrame browser = new JFrame();
    String website = "";
    public static JEditorPane pane;
protected GridBagConstraints gbcConstraints   = null;
JButton home = new JButton("Reload");
JButton refresh = new JButton("Reload");
JButton goTo = new JButton("Goto");
JTextField address = new JTextField();
JEditorPane  browsercontent = new JEditorPane ();
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
	/**
	 * Standard Constructor
	 */
	public UpdateInfo(final String weburl, final String name) {
        website = weburl;
        home.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
                   try {
      URL url = new URL(website);
      address.setText(url.toString());
        browsercontent.setPage(url);

      }catch (Exception error){}
         }
      });
        goTo.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
                   try {
                       String adr = address.getText();
                       if (!adr.startsWith("http://"))
                           if (!adr.startsWith("ftp://"))
                           adr = "http://" + adr;
                       if (adr.startsWith("http://tacgr"))
                           return;
      URL url = new URL(adr);
      address.setText(url.toString());
        browsercontent.setPage(url);

      }catch (Exception error){}
         }
      });
        refresh.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
                   try {
                       String adr = address.getText();
                       if (!adr.startsWith("http://"))
                           if (!adr.startsWith("ftp://"))
                           adr = "http://" + adr;
      URL url = new URL(adr);
      address.setText(url.toString());
        browsercontent.setPage(url);

      }catch (Exception error){}
         }
      });

        browser = new JFrame(name){
            protected void processWindowEvent(WindowEvent we) {
                if (we.getID() == WindowEvent.WINDOW_CLOSING) {
                   this.dispose();
                }
            }
        };

            browser.setLayout(new GridBagLayout());

      try {
      URL url = new URL(website);
      address.setText(url.toString());
        browsercontent.setPage(url);
        browsercontent.setAutoscrolls(true);
        browsercontent.setEditable(false);
        browsercontent.addHyperlinkListener(new Hyperactive());

      }catch (Exception e){}
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        d = new Dimension(800,600);
       // address.setEditable(false);
      browser.add(home,getGridBagConstraints(1, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
   //   browser.add(refresh,getGridBagConstraints(2, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      browser.add(goTo,getGridBagConstraints(3, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      browser.add(address,getGridBagConstraints(4, 1, 0.0, 0.0, 1, GridBagConstraints.BOTH));
      browser.add(new JScrollPane(browsercontent),getGridBagConstraints(1, 2, 1.0, 1.0, 4, GridBagConstraints.BOTH));
        home.setFocusable(false);
        browser.setFocusable(false);
        browser.setSize(d);
        browser.setVisible(true);
        browsercontent.setFocusable(false);
	}
    class Hyperactive implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                pane = (JEditorPane) e.getSource();
                if (e instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                    HTMLDocument doc = (HTMLDocument)pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
      } else {
        try {
            if (e.getURL().toString().toLowerCase().endsWith(".jpg") ||
                   e.getURL().toString().toLowerCase().endsWith(".gif") ||
                   e.getURL().toString().toLowerCase().endsWith(".pdf") ||
                   e.getURL().toString().toLowerCase().endsWith(".zip") ||
                   e.getURL().toString().toLowerCase().endsWith(".jar") ||
                   e.getURL().toString().toLowerCase().endsWith(".dsk") ||
                   e.getURL().toString().toLowerCase().endsWith(".bin") ||
                   e.getURL().toString().toLowerCase().endsWith(".bas") ||
                   e.getURL().toString().toLowerCase().endsWith(".sna") ||
                   e.getURL().toString().toLowerCase().endsWith(".cdt") ||
                   e.getURL().toString().toLowerCase().endsWith(".mpg") ||
                   e.getURL().toString().toLowerCase().endsWith(".mp3") ||
                   e.getURL().toString().toLowerCase().endsWith(".mpeg") ||
                   e.getURL().toString().toLowerCase().endsWith(".wmv") ||
                   e.getURL().toString().toLowerCase().endsWith(".png") ||
                   e.getURL().toString().toLowerCase().endsWith(".rar") ||
                   e.getURL().toString().toLowerCase().endsWith(".avi") ||
                   e.getURL().toString().toLowerCase().endsWith(".bmp")
                    ) {
                pane.setPage("http://cpc-live.com/loading.html");

                String download = e.getURL().toString();
                String old = address.getText();
                CPC.Oldpage = old;
                CPC.Download(download);
                //pane.setPage(old);
                return;

            }
            else
                       if (e.getURL().toString().startsWith("http://tacgr") || e.getURL().toString().equals("http://www.cpcgamereviews.com/index.html"))
                           return;
          pane.setPage(e.getURL());
        pane.setFocusable(false);
      address.setText(e.getURL().toString());
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }
  }
}

/*    public void Download(String download){
        String extension = download;
        System.out.println(extension + " - " + extension.length());
        extension = download.substring(extension.length()-4);
      FileDialog filedia = new FileDialog((Frame) this, "Download file...", FileDialog.SAVE);
        filedia.setFile("*"+extension);
        filedia.setVisible(true);
        String filename = filedia.getDirectory() + filedia.getFile();
        if (filename != null) {
            if (!filename.toLowerCase().endsWith(extension))
                filename = filename + extension;
            System.out.println("Saving to " + filename);

          String[]arg= {download, filename};
          copyURL.main(arg);
        }
    }*/

/*    public void Download (String download){
          String savename="buffer.zip";
          String[]arg= {download, savename};
          copyURL.main(arg);
          Switches.booter = 1;
        CPC.downstring = savename;
    }*/


  /* public static void main (String[] args){
        new UpdateInfo();
    }*/
}
