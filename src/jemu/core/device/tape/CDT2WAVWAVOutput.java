package jemu.core.device.tape;

/**
 * WAV file format output manager
 * Converted from "TZX to WAV Converter v0.2 (C) 2006 Francisco Javier Crespo <tzx2wav@ya.com>"
 * 
 * Nice WAV format info in this web site:
 * http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
 * 
 * @author John Girvin
 *
 */
public class CDT2WAVWAVOutput extends CDT2WAVBaseOutput {
	
	//
	// INSTANCE VARIABLES
	//
	
	private WAVHeader wavHeader;

	//
	// Inner class to represent WAV file header data
	//
	
	public class WAVHeader {
		// header data members
		public int   ChunkID;
		public int   ChunkSize;
		public int   Format;
		public int   fmtChunkID;
		public int   fmtChunkSize;
		public short AudioFormat;
		public short NumChannels;
		public int   SampleRate;
		public int   ByteRate;
		public short BlockAlign;
		public short BitsPerSample;
		public int   Subchunk2ID;
		public int   Subchunk2Size;

		/**
		 * Write a 32 bit value to the output
		 * @param data
		 */
		private void writeInt(int data) {
			outputByte((byte) ( data        & 0xff));
			outputByte((byte) ((data >>  8) & 0xff));
			outputByte((byte) ((data >> 16) & 0xff));
			outputByte((byte) ((data >> 24) & 0xff));
		}
		
		/**
		 * Write a 16 bit value to the output
		 * @param data
		 */
		private void writeShort(short data) {
			outputByte((byte) ( data        & 0xff));
			outputByte((byte) ((data >>  8) & 0xff));
		}
		
		/**
		 * Write header content to the output
		 */
		public void write() {
			// header goes at start of output
			int oldPos = outputTell();
			outputSeek(0);
			
			// output the header fields
			writeInt(ChunkID);
			writeInt(ChunkSize);
			writeInt(Format);
			writeInt(fmtChunkID);
			writeInt(fmtChunkSize);
			writeShort(AudioFormat);
			writeShort(NumChannels);
			writeInt(SampleRate);
			writeInt(ByteRate);
			writeShort(BlockAlign);
			writeShort(BitsPerSample);
			writeInt(Subchunk2ID);
			writeInt(Subchunk2Size);
			
			// reset back to previous position
			outputSeek(oldPos);
		}
	}
	
	/**
	 * @see CDT2WAVBaseOutput
	 */
	public CDT2WAVWAVOutput(int freq) {
		super(freq);
	}
	
	/**
	 * @see CDT2WAVBaseOutput.dispose
	 */
	public void dispose() {
		super.dispose();
		wavHeader = null;
	}
	
	/**
	 * Prepare for output
	 */
	protected void init() {
		// Initialise WAV file header for 8-bit mono output 95 26 CD 00
		wavHeader = new WAVHeader();
		wavHeader.ChunkID = 0x46464952;      // "RIFF" ID
		//wavHeader.ChunkSize = 0xCD2695;
		wavHeader.ChunkSize = 0;
		wavHeader.Format = 0x45564157;       // "WAVE" ID
		wavHeader.fmtChunkID = 0x20746D66;   // "fmt " ID
		wavHeader.fmtChunkSize = 16;
		wavHeader.AudioFormat = 1;           // PCM Linear Quantization
		wavHeader.SampleRate = (int) getFrequency();
		wavHeader.Subchunk2ID = 0x61746164;  // "data" ID
		wavHeader.Subchunk2Size = 0;
		wavHeader.NumChannels = 1;
		wavHeader.BitsPerSample = 8;
		wavHeader.ByteRate = (int) getFrequency();
		wavHeader.BlockAlign = 1;
		
		// Send WAV header to the output
       // wavHeader.write();
	}

	/**
	 * Generate WAV data for "numsamples" samples.
	 * @param numsamples
	 */
	protected void write(int numsamples) {
		// Update data length in WAV header
		wavHeader.Subchunk2Size = (outputTell() + numsamples) - 44;
		wavHeader.ChunkSize = 36 + wavHeader.Subchunk2Size;

		// Write samples at current amplitude
		byte sample = (isLowAmp() ? (byte) 0x26 : (byte) 0xDA);
		outputByte(sample, numsamples);
	}

	/**
	 * Finalise output.
	 */
	protected void stop() {
		// Write updated WAV header to the output
        wavHeader.write();
        
        // seriously?
        //jemu.system.cpc.CPC.recordcount = bufPos;
	}

}
