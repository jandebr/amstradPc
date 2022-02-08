/*
 * JavaSound.java
 *
 * Created on 12 June 2006, 20:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
*/
package jemu.core.device.sound;

import javax.sound.sampled.*;

import jemu.ui.Switches;

import jemu.system.cpc.CPC;

/**
 *
 * @author Richard
 */
public class JavaSound extends SunAudio {

    AudioCapture capture = new AudioCapture();

  public static int SAMPLE_RATE = 44100;
  
  protected static AudioFormat MONO_FORMAT = new AudioFormat(SAMPLE_RATE, 8, 1, false, true);
  protected static AudioFormat STEREO_FORMAT = new AudioFormat(SAMPLE_RATE, 8, 2, false, false);
  
  protected SourceDataLine line;
  protected byte[] data;
  protected int offset = 0;
  protected int channels;
  protected long startCount;
  
  /** Creates a new instance of JavaSound.
   *
   * @samples Number of samples written to DataLine at a time. Keep low ~32
   * @stereo  true for Stereo, false for Mono
   */
  public JavaSound(int samples, boolean stereo) {
    super(samples, stereo);
  }
  
  public int getSampleRate() {
    return SAMPLE_RATE;
  }
  
  protected void init() {
    format = SoundUtil.UPCM8;
    channels = stereo ? 2 : 1;
    data = new byte[samples * channels];
    AudioFormat format = stereo ? STEREO_FORMAT : MONO_FORMAT;
    try {
      line = (SourceDataLine)AudioSystem.getLine(
        new DataLine.Info(SourceDataLine.class, format, SAMPLE_RATE * channels));
      line.open();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  public void resync() {
    line.flush();
    startCount = line.getLongFramePosition();
    int samples = SAMPLE_RATE / 10 * channels;   // 1/10 sec (100 ms) delay
    while (samples > 0) {
      int len = Math.min(data.length,samples);
      line.write(data,0,len);
      samples -= len;
    }
    if (Switches.turbo >=2){
        Switches.turbo--;
    }
    else{
        System.out.println("resync: start=" + startCount);
        if (jemu.ui.Display.showfps <= 0)
        jemu.ui.Display.showfps = -200;
    }
  }
  
  public long getCount() {
    return line.getLongFramePosition() - startCount - 100;
  }
  
  public long getDeviation() {
    return SAMPLE_RATE / 10;    // 100 ms
  }
  
  public void play() {
    resync();
    line.start();
  }
  
  public void stop() {
    line.stop();
  }
  
  public void dispose()  {
    line.close();
  }
  
  public void writeMono(int value) {
    switch(format) {
      case SoundUtil.ULAW:  data[offset] = SoundUtil.ulawToUPCM8((byte)value); break;
      case SoundUtil.UPCM8: data[offset] = (byte)value; break;
    }
    //line.write(data,offset,1);
    if (++offset == data.length) {
      line.write(data,0,data.length);
      offset = 0;
    }
    updates++;
  }
  
  public void writeStereo(int a, int b) {
      a = a^0x80;
      b = b^0x80;
    switch(format) {
      case SoundUtil.ULAW:
        data[offset] = SoundUtil.ulawToUPCM8((byte)a);
        data[offset + 1] = SoundUtil.ulawToUPCM8((byte)b);
        break;
      case SoundUtil.PCM8:
        data[offset] = SoundUtil.ulawToPCM8((byte)a);
        data[offset + 1] = SoundUtil.ulawToPCM8((byte)b);
        break;
      case SoundUtil.UPCM8:
        data[offset] = (byte)a;
        data[offset + 1] = (byte)b;
        break;
    }

    if ((offset += 2) == data.length) {
      if (CPC.showAudioCapture && !capture.showCapture){
        capture.showCapture();
      }
      if (!CPC.showAudioCapture && capture.showCapture)
          capture.PaintBuffer(data);
      if (CPC.showAudioCapture && capture.showCapture){
        capture.Audiocapture.setVisible(true);
        CPC.showAudioCapture = false;
      }
      line.write(data,0,data.length);
        if (capture.doCapture)
            capture.Capture(data, data.length);
      offset = 0;
    }
    updates++;
  }


}
