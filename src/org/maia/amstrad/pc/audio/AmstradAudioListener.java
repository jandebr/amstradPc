package org.maia.amstrad.pc.audio;

import org.maia.util.GenericListener;

public interface AmstradAudioListener extends GenericListener {

	void amstradAudioMuted(AmstradAudio audio);

	void amstradAudioUnmuted(AmstradAudio audio);

}