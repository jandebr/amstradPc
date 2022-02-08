/*
 * Register.java
 * 
 * Created on 3/09/2007, 10:24:01
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jemu.core.device;

/**
 * This class provides a descriptor for registers of devices, including Processors.
 * Each device has a method getRegisters to get each register for the device.
 * It includes display formatting and positioning for UI representation of the register.
 * 
 * @author Richard
 */
public class Register {
  
  protected String name;    // The name of the Register
  protected int bits;       // The number of bits in the register (1, 8, 16, 24, 32, 48 or 64)
  protected String format;  // The format (used for flags registers)
  protected int column;     // The column in the debugger of this register - row is determined by index
    
  public Register(String name, int bits, String format, int column) {
    this.name = name;
    this.bits = bits;
    this.format = format;
    this.column = column;
  }
  
  public Register(String name) {
    this(name,8,null,0);
  }

  public Register(String name, int bits) {
    this(name,bits,null,0);
  }
  
  public Register(String name, int bits, String format) {
    this(name,bits,format,0);
  }
    
  public Register(String name, int bits, int column) {
    this(name,bits,null,column);
  }
    
  public String getName() {
    return name;
  }
  
  public int getBits() {
    return bits;
  }
  
  public String getFormat() {
    return format;
  }
  
  public int getColumn() {
    return column;
  }
}
