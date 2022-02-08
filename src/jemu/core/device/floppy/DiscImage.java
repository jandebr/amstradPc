/*
 * DiscImage.java
 *
 * Created on 29 July 2006, 11:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jemu.core.device.floppy;

import java.io.File;

/**
 *
 * @author Richard
 */
public abstract class DiscImage {

  protected String name;

  public DiscImage(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public abstract byte[] readSector(int cylinder, int head, int c, int h, int r, int n);

  public abstract int getSectorCount(int cylinder, int head);

  public abstract int[] getSectorID(int cylinder, int head, int index);

  public abstract void writeSector(int cylinder, int head, int c, int h, int r, int n, byte[] data);


  /**
   * Save the disc image.
   */
  public abstract void saveImage(File saveFile);

}