/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.ui;

import java.io.*;
import java.net.*;
import java.util.Calendar;

/**
* Title:        JavaCPC
* Description:  The Java CPC Emulator
* Copyright:    Copyright (c) 2004-2009
* Company:
* @author:      Markus
*/

public class BugReport {
   static Calendar cal = Calendar.getInstance();
  public static void reportBug() {
    try {
        System.getProperties().put("mail.host", "mail.cpc-live.com");

      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      String from = "webmaster@cpc-live.com";
      String to = "webmaster@cpc-live.com";
      String subject = "JavaCPC bug / contact";

      URL u = new URL("mailto:" + to);
      URLConnection c = u.openConnection();
      c.setDoInput(false); 
      c.setDoOutput(true);
      System.out.println("Connecting...");
      System.out.flush();
      c.connect();
      PrintWriter out =
        new PrintWriter(new OutputStreamWriter(c.getOutputStream()));

      out.println("From: \"" + from + "\" <" +
                  System.getProperty("user.name") + "@" +
                  InetAddress.getLocalHost().getHostName() + ">");
      out.println("To: " + to);
      out.println("Subject: " + subject);
      out.println();

      String line;
      line = "JavaCPC bug reported by   : " + JEMU.username;
      out.println(line);
      line = "email address: " + JEMU.usermail;
      out.println(line);
      out.println("[JavaCPC version " + jemu.ui.JEMU.version + "]\n["+cal.getTime()+
              "]\n\n------------------------------------\n\n");
      out.println(JEMU.usertext);

      line="";
      out.println(line);
      line="------------------------------------";
      out.println(line);
      line="";
      out.println(line);
      try{
          out.println(Console.textArea.getText());
      } catch (Exception b) {
          out.println("Console is disabled / empty?!?\nUsing BETA?\n\n");
      }

      out.close();
      System.out.println("Bug report sent.");
      System.out.flush();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      System.err.println("Mailserver does not respond");
    }
  }
}
