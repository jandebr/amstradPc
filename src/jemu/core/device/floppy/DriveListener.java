/*
 * DriveListener.java
 * 
 * Created on 3/09/2007, 11:24:32
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jemu.core.device.floppy;

/**
 *
 * @author Richard
 */
public interface DriveListener {

  public void driveActiveChanged(Drive drive, boolean active);
  
}
