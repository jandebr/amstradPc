/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.core.samples;

/**
 *
 * @author Markus
 */

import java.io.*;
import java.net.URL;

import javax.sound.sampled.*;



/**
 * This enum encapsulates all the sound effects
 */

public enum Samples {
   BREAK("jemu/core/samples/wav/breakpoint.wav"),
   BREAKI("jemu/core/samples/wav/instruction.wav"),
   EJECT("jemu/core/samples/wav/eject.wav"),
   INSERT("jemu/core/samples/wav/insert.wav"),
   MOTOR("jemu/core/samples/wav/motor.wav"),
   SEEK("jemu/core/samples/wav/seek.wav"),
   SEEKBACK("jemu/core/samples/wav/seekback.wav"),
   TRACK("jemu/core/samples/wav/track.wav"),
   TRACKBACK("jemu/core/samples/wav/trackback.wav"),
   DEGAUSS("jemu/core/samples/wav/monitoron.wav"),
   RELAIS("jemu/core/samples/wav/relon.wav"),
   RELAISOFF("jemu/core/samples/wav/reloff.wav"),
   TAPEMOTOR("jemu/core/samples/wav/tapmotor.wav"),
   WINDMOTOR("jemu/core/samples/wav/wind.wav"),
   REWINDMOTOR("jemu/core/samples/wav/rewind.wav"),
   TAPEKEY("jemu/core/samples/wav/tapekey.wav"),
   TAPESTOP("jemu/core/samples/wav/tapestop.wav"),
   TAPEINSERT("jemu/core/samples/wav/tape_insert.wav"),
   PRINTER("jemu/core/samples/wav/printer.wav"),
   TAPEEJECT("jemu/core/samples/wav/tape_eject.wav"),
   KEY("jemu/core/samples/wav/cpckey.wav"),
   ENTER("jemu/core/samples/wav/cpcenter.wav"),
   SPACE("jemu/core/samples/wav/cpcspace.wav");

   // Nested class for specifying volume
   public static enum Volume {
      MUTE, LOW, MEDIUM, HIGH
   }

   public static Volume volume = Volume.HIGH;

   // Each sound effect has its own clip, loaded with its own sound file.
   private Clip clip;

   // Constructor to construct each element of the enum with its own sound file.
   Samples(String soundFileName) {
      try {
         // Use URL (instead of File) to read from disk and JAR.
         URL url = this.getClass().getClassLoader().getResource(soundFileName);
         // Set up an audio input stream piped from the sound file.
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
         // Get a clip resource.
         clip = AudioSystem.getClip();
         // Open audio clip and load samples from the audio input stream.
         clip.open(audioInputStream);
      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
   }

   // Play or Re-play the sound effect from the beginning, by rewinding.
   public void play() {
      if (volume != Volume.MUTE) {
         if (clip.isRunning())
            clip.stop();   // Stop the player if it is still running
         clip.setFramePosition(0); // rewind to the beginning
         clip.start();     // Start playing
      }
   }
   public void loop() {
      if (volume != Volume.MUTE) {
         if (clip.isRunning())
            clip.stop();   // Stop the player if it is still running
         clip.setFramePosition(0); // rewind to the beginning
         clip.loop(Clip.LOOP_CONTINUOUSLY);     // Start playing
      }
   }
   
   public void loop2() {
      if (volume != Volume.MUTE) {
         if (!clip.isRunning()){
         clip.setFramePosition(0); // rewind to the beginning
         clip.loop(Clip.LOOP_CONTINUOUSLY);     // Start playing
      }
      }
   }
   public void stop() {
         if (clip.isRunning())
            clip.stop();   // Stop the player if it is still running
         clip.setFramePosition(0); // rewind to the beginning
         clip.stop();     // Start playing
   }

   // Optional static method to pre-load all the sound files.
   static void init() {
      values(); // calls the constructor for all the elements
   }
}

