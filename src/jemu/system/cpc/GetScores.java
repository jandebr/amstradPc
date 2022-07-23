/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.system.cpc;
import jemu.core.Util;
import javax.swing.*;

/**
 *
 * @author Markus
 */
public class GetScores {
  public static CPCMemory    memory   = CPC.memory;
  public static JFrame dummy = new JFrame();

    public static void getScores(){
        Object[] oscores = {"Address where scores are located:"};
             String scores = JOptionPane.showInputDialog(dummy, oscores , "88");
             if (scores == null) return;
             try {
             getScores(Util.hexValue(scores));}
             catch (final Exception iox) { }
    }

    public static void getScores(int address){
            byte value1 = (byte)memory.readByte(address);
            byte value2 = (byte)memory.readByte(address+1);
            byte value3 = (byte)memory.readByte(address+2);
            byte value4 = (byte)memory.readByte(address+3);
            String value = Util.hex(value4) + Util.hex(value3) + Util.hex(value2) + Util.hex(value1);

            try {
            int Value = Util.hexValue(value);
            JOptionPane.showMessageDialog(null, "Score is: "+ Value);}

                        catch (final Exception iox) { }
    }
      public static void getDW3Scores(){
        int address = 0x88;
        int checkoffset = 0x44cb;
        String checkstring = "DEATHWISH";
            byte[] mem = memory.getMemory();
            String stringcheck = new String(mem, checkoffset, 0x09);
            if (checkstring.equals(stringcheck)){
            byte value1 = (byte)memory.readByte(address);
            byte value2 = (byte)memory.readByte(address+1);
            byte value3 = (byte)memory.readByte(address+2);
            byte value4 = (byte)memory.readByte(address+3);
            String value = Util.hex(value4) + Util.hex(value3) + Util.hex(value2) + Util.hex(value1);

            try {
            int Value = Util.hexValue(value);
            JOptionPane.showMessageDialog(null, "Score is: "+ Value);}

                        catch (final Exception iox) { }
            } else
                JOptionPane.showMessageDialog(null, "Deathwish 3 is not running...");
    }
      public static String DW3Scores(){
        int address = 0x88;
        int checkoffset = 0x44cb;
        String checkstring = "DEATHWISH  3";
            byte[] mem = memory.getMemory();
            String stringcheck = new String(mem, checkoffset, 0x0c);
            if (checkstring.equals(stringcheck)){

            byte value1 = (byte)memory.readByte(address);
            byte value2 = (byte)memory.readByte(address+1);
            byte value3 = (byte)memory.readByte(address+2);
            byte value4 = (byte)memory.readByte(address+3);
            String value = Util.hex(value4) + Util.hex(value3) + Util.hex(value2) + Util.hex(value1);

            try {
            int Value = Util.hexValue(value);
            return "Scores: " + Value;
            }

                        catch (final Exception iox) { }
            }
            return "DW3 is not running...";
    }
      public static String getDWHigh(){
        int address = 0x461c;
        int checkoffset = 0x44cb;
        String checkstring = "DEATHWISH  3";
            byte[] mem = memory.getMemory();
            String stringcheck = new String(mem, checkoffset, 0x0c);
            if (checkstring.equals(stringcheck)){

            byte value1 = (byte)memory.readByte(address);
            byte value2 = (byte)memory.readByte(address+1);
            byte value3 = (byte)memory.readByte(address+2);
            String value = Util.hex(value3) + Util.hex(value2) + Util.hex(value1);

            try {
            int Value = Util.hexValue(value);
            return "Highscore: " + Value;
            }

                        catch (final Exception iox) { }
            }
            return "DW3 is not running...";
    }
}
