/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.core.device.sound;

/**
 *
 * @author Markus
 */
public class DigiBlaster extends SoundDevice {
    public DigiBlaster() {
    super("DigiBlaster printer port emulator");
    player = SoundUtil.getSoundPlayer(100,true);
    player.setFormat(SoundUtil.UPCM8);
  }

    public void writeBlaster(int a, int b){
        player.writeStereo(a, b);
    }

}
