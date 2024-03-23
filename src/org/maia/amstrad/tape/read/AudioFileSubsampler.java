package org.maia.amstrad.tape.read;

import java.io.IOException;

import org.maia.util.KeyedCacheLRU;

public class AudioFileSubsampler {

	private KeyedCacheLRU<SubsamplesCacheKey, short[]> cache;

	private static AudioFileSubsampler instance;

	private static final long MAX_SUBSAMPLE_SEQUENCE_LENGTH = 200L;

	private AudioFileSubsampler() {
		this.cache = new KeyedCacheLRU<SubsamplesCacheKey, short[]>(4);
	}

	public short[] subsampleUnsigned(AudioFile audioFile, int length) throws IOException {
		short[] subsamples;
		SubsamplesCacheKey cacheKey = new SubsamplesCacheKey(audioFile, length);
		if (getCache().containsKey(cacheKey)) {
			subsamples = getCache().fetchFromCache(cacheKey);
		} else {
			subsamples = new short[length];
			long ns = audioFile.getNumberOfSamples();
			double f = ns / (double) length;
			for (int i = 0; i < length; i++) {
				long s0 = (long) Math.floor(i * f);
				long s1 = (long) Math.floor((i + 1) * f) - 1L;
				s1 = Math.min(s0 + MAX_SUBSAMPLE_SEQUENCE_LENGTH - 1L, Math.max(s1, s0));
				for (long si = s0; si <= s1; si++) {
					short s = audioFile.getAbsoluteSample(si);
					subsamples[i] = (short) Math.max(subsamples[i], s);
				}
			}
			getCache().storeInCache(cacheKey, subsamples);
		}
		return subsamples;
	}

	private KeyedCacheLRU<SubsamplesCacheKey, short[]> getCache() {
		return cache;
	}

	public static AudioFileSubsampler getInstance() {
		if (instance == null) {
			setInstance(new AudioFileSubsampler());
		}
		return instance;
	}

	private static synchronized void setInstance(AudioFileSubsampler subsampler) {
		if (instance == null) {
			instance = subsampler;
		}
	}

	private static class SubsamplesCacheKey {

		private AudioFile audioFile;

		private int length;

		public SubsamplesCacheKey(AudioFile audioFile, int length) {
			this.audioFile = audioFile;
			this.length = length;
		}

		@Override
		public int hashCode() {
			return 31 * getAudioFile().getSourceFile().hashCode() + getLength();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SubsamplesCacheKey other = (SubsamplesCacheKey) obj;
			if (getLength() != other.getLength())
				return false;
			String myPath = getAudioFile().getSourceFile().getPath();
			String otherPath = other.getAudioFile().getSourceFile().getPath();
			if (!myPath.equals(otherPath))
				return false;
			return true;
		}

		public AudioFile getAudioFile() {
			return audioFile;
		}

		public int getLength() {
			return length;
		}

	}

}