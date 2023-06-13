package org.maia.amstrad.pc.audio;

import org.maia.amstrad.util.AmstradListener;

public interface AmstradAudioListener extends AmstradListener {

	void amstradAudioMuted(AmstradAudio audio);

	void amstradAudioUnmuted(AmstradAudio audio);

}