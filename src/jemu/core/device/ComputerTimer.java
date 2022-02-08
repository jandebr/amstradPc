/*
 * ComputerTimer.java
 *
 * Created on 08 March 2007, 11:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jemu.core.device;

/**
 *
 * @author Richard
 */
public interface ComputerTimer {
  
  public long getCount();
  
  public long getUpdates();
  
  public long getDeviation();
  
  public void resync();
  
}
