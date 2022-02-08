package jemu.core.device;
import jemu.system.cpc.CPCPrinter;
import jemu.ui.JEMU;
import jemu.system.cpc.CPC;
import jemu.core.device.sound.*;
import jemu.ui.Switches;
import jemu.ui.Display;
import jemu.core.Util;
import jemu.core.samples.Samples;

/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class DeviceMapping {
  protected Device device;
  protected int mask;
  protected int test;
  protected int printer;
  int digicount = 0;

  public DeviceMapping(Device device, int mask, int test) {
    this.device = device;
    this.mask = mask;
    this.test = test;
  }

  public int readPort(int port) {
      if (port == 0x0000efff)
        if (Switches.Expansion)
              return 0xa0;
     return (port & mask) == test ? device.readPort(port) : -1;
  }

  public void writePort(int port, int value) {
    if ((port & mask) == test){
      device.writePort(port,value);
    }

    if (Switches.digiblaster){
    if ((port & 0x1000) == 0){
        if (port == 0xef00){
            Display.blaster = 10;
            AY_3_8910.digicount = 1;
            AY_3_8910.digiblast = true;}
            AY_3_8910.blasterA =  (((value^0x080)*Switches.Blastervolume)/100)^0x080;
            AY_3_8910.blasterB =  AY_3_8910.blasterA;
    }
    }
    else {
     if (Switches.Expansion)
        if (port == 0x0ef00){
            if (value == 0xdb)
                JEMU.screentimer = 1;
            if (value == 0xd5)
                JEMU.screenshottimer = 1;
            if (value == 0xd6)
                JEMU.dsksavetimer = 1;
            if (value == 0xd7)
                Switches.ScanLines = true;
            if (value == 0xd8)
                Switches.ScanLines = false;
            if (value == 0xd9)
                JEMU.dskmaketimer = 1;
            if (value == 0xff)
                CPCPrinter.MakeInvisible();
            if (value == 0xd0)
                Switches.monitormode = 0;
            if (value == 0xd1)
                Switches.monitormode = 1;
            if (value == 0xd2)
                Switches.monitormode = 0;
            if (value == 0xd2)
                Switches.monitormode = 2;
            if (value == 0xd3)
                Switches.monitormode = 3;
            if (value == 0xde)
                CPCPrinter.Smaller();
            if (value == 0xdf)
                CPCPrinter.Bigger();
            if (value == 0xef)
                System.exit(0);
            if (value == 0xfe)
                CPCPrinter.MakeVisible();
            if (value == 0xfc)
                CPCPrinter.ClearPrinter();
            if (value == 0xfd)
                CPCPrinter.Copy();
            if (value == 0xfa){
                Switches.Printer = false;
                CPCPrinter.OnlineButton();
            }
            if (value == 0xfb){
                Switches.Printer = true;
                CPCPrinter.OnlineButton();
            }
            if (value == 0xE0){
                CPCPrinter.FontName = CPCPrinter.FontName1;
            CPCPrinter.setFont();
            }
            if (value == 0xE1){
                CPCPrinter.FontName = CPCPrinter.FontName2;
            CPCPrinter.setFont();
            }
            if (value == 0xE2){
                CPCPrinter.FontName = CPCPrinter.FontName3;
            CPCPrinter.setFont();
            }
            if (value == 0xE3){
                CPCPrinter.FontName = CPCPrinter.FontName4;
            CPCPrinter.setFont();
            }
            if (value == 0xE4){
                CPCPrinter.FontName = CPCPrinter.FontName5;
            CPCPrinter.setFont();
            }
            if (value == 0xE5){
                CPCPrinter.FontName = CPCPrinter.FontName6;
            CPCPrinter.setFont();
            }
            if (value == 0xE6){
                CPCPrinter.FontName = CPCPrinter.FontName7;
            CPCPrinter.setFont();
            }
            if (value == 0xE7){
                CPCPrinter.FontName = CPCPrinter.FontName8;
            CPCPrinter.setFont();
            }
            if (value == 0xE8){
                CPCPrinter.FontName = CPCPrinter.FontName9;
            CPCPrinter.setFont();
            }
        }
        if (Switches.Expansion)
            if (port == 0x0ef01 && value !=0)
            {
                CPCPrinter.textSize = value;
                CPCPrinter.setFont();
                }
     if (Switches.Printer)
         if (port > 0xef30 && port < 0xef3f)
         if (((port & 0x1000) == 0) && ((value & 0x80) != 0)){
             value = value & 0x7f;
            Display.printer = 10;
             if (value != 0x0a)
                 CPCPrinter.printer = CPCPrinter.printer + CPCPrinter.asciitxt[value];
             else{
                 CPCPrinter.DumpText();
             }
        }
    }
  }
}