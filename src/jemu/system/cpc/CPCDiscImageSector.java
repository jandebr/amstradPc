package jemu.system.cpc;

/**
 * Sector of a CPC disc image.
 * 
 * @author Roland.Barthel
 */
public class CPCDiscImageSector {

  /** the track number. */
  private final int track;

  /** the disc side. */
  private int       side;

  /** the sector id. */
  private final int id;

  /** the sector size (UPD765A size). */
  private final int size;

  /** sector data. */
  private byte[]    data;

  /**
   * Create a new instance of a sector.
   * 
   * @param track track number
   * @param side side number (0/1)
   * @param id sector id
   * @param size sector size (UPD765A size)
   * @param data sector data
   */
  public CPCDiscImageSector(final int track, final int side, final int id, final int size, final byte[] data) {
    super();
    this.track = track;
    this.side = side;
    this.id = id;
    this.size = size;
    this.data = data;
  }

  /**
   * @return sector id
   */
  public int getId() {
    return this.id;
  }

  /**
   * @return track number
   */
  public int getTrack() {
    return this.track;
  }

  /**
   * @return side number
   */
  public int getSide() {
    return this.side;
  }

  /**
   * @return sector size (UPD765A size)
   */
  public int getSize() {
    return this.size;
  }

  /**
   * @return sector data
   */
  public byte[] getData() {
    return this.data;
  }

  /**
   * Set new sector data.
   * 
   * @param data new data
   */
  public void setData(final byte[] data) {
    this.data = data;
  }

  /**
   * Set side of sector.
   * 
   * @param side new side
   */
  public void setSide(final int side) {
    this.side = side;
  }

}
