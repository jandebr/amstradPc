/*
 * ComputerDescriptor.java
 *
 * Created on 5 August 2006, 21:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jemu.core.device;

/**
 *
 * @author Richard
 */
public class ComputerDescriptor {
  
  public String key, name, className;
  
  /** Creates a new instance of ComputerDescriptor */
  public ComputerDescriptor(String key, String name, String className) {
    this.key = key;
    this.name = name;
    this.className = className;
  }
  
  public String toString() {
    return name;
  }
  
}
