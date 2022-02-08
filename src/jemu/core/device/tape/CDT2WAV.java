package jemu.core.device.tape;

import jemu.core.Util;

/**
 * CDT2WAV conversion utilities
 * Converted from "TZX to WAV Converter v0.2 (C) 2006 Francisco Javier Crespo <tzx2wav@ya.com>"
 *
 * @author John Girvin
 *
 */
public class CDT2WAV {

	//
	// CONSTANTS
	//

    // Header magic number bytes of ZXTAPE format file
    private static byte[] ZXTAPE_HEADER = new byte[] { 0x5A, 0x58, 0x54, 0x61, 0x70, 0x65, 0x21 };  

	//
	// INSTANCE VARIABLES
	//
    
    protected boolean debug = false;

    private byte[] inpbuf;
	private int    frequency = 44100;
	
	private CDT2WAVBaseOutput output;

	private int currentBlock;       // Current block that is playing
	private int numBlocks;          // Total Num. of blocks
	private int blockStart[];		// Array of block start positions

	private int id;                 // Current Block ID
	private int data;				// Data to be played
	private int datalen;            // Len of ^^^
	private int datapos;            // Position in ^^^
	private int bitcount;           // How many bits to play in current byte ?
	private int sb_bit;             // should we play bit 0 or 1 ?
	private byte databyte;          // Current Byte to be replayed of the data
	private int pilot;              // Len of Pilot signal (in hp's)
	private int sb_pilot;           // Pilot pulse
	private int sb_sync1;           // Sync first half-period (hp)
	private int sb_sync2;           // Sync second
	private int sb_bit0;            // Bit-0
	private int sb_bit1;            // Bit-1
	private int sb_pulse;           // Pulse in Sequence of pulses and direct recording block
	private int lastbyte;           // How many bits are in last byte of data ?
	private int pause;              // Pause after current block (in milliseconds)
	private int singlepulse;        // Flag to activate single pulse waves
	private short jump;      		// Relative Jump 

	private int loop_start=0;       // Position of the last Loop Start block
	private int loop_count=0;       // Counter of the Loop
	
	private int call_pos=0;         // Position of the last Call Sequence block
	private int call_num=0;         // Number of Calls in the last Call Sequence block
	private int call_cur=0;         // Current Call to be made

	/*
	private int cpc=0;              // Amstrad CPC tape ?
	*/

	/**
	 * Create an object to convert ZXTAPE/CDT data to WAV format.
	 * 
	 * @param input - the input data
	 * @param output - buffer to hold the WAV data. Supply null for test mode.
	 * @param freq - desired frequency in Hz for the WAV data
	 */
	public CDT2WAV(byte[] input, int freq) {
		super();
		inpbuf = input;
		frequency = freq;
	}
	
	/**
	 * Dereference any allocated resources. Do not call this object again
	 * after calling dispose().
	 */
	public void dispose() {
		inpbuf = null;
		blockStart = null;

		if (output != null) {
			output.dispose();
		}
		output = null;
	}
	
	//
	// TZX Blocks Parsing routines
	//
	
	// ...Standard Loading Data block
	private void analyseID10() {
		pause=get2(inpbuf, data);
		datalen=get2(inpbuf, data+2);
		data+=4;
		if (inpbuf[data]==0x00)
			pilot=8064;
		else
			pilot=3220;
		sb_pilot=output.samples(2168);
		sb_sync1=output.samples(667);
		sb_sync2=output.samples(735);
		sb_bit0=output.samples(885);
		sb_bit1=output.samples(1710);
		lastbyte=8;
	}

	// ...Custom Loading Data block
	private void analyseID11() {
		sb_pilot=output.samples(get2(inpbuf, data+0));
		sb_sync1=output.samples(get2(inpbuf, data+2));
		sb_sync2=output.samples(get2(inpbuf, data+4));
		sb_bit0=output.samples(get2(inpbuf, data+6));
		sb_bit1=output.samples(get2(inpbuf, data+8));
		pilot= get2(inpbuf, data+10);
		lastbyte=(int) inpbuf[data+12];
		pause= get2(inpbuf, data+13);
		datalen= get3(inpbuf, data+15);
		data+=18;
        if (debug)
        System.out.println("Pilot is: " +pilot + " pause is: " + pause + " Length is: " + datalen);
	}
	
	// ...Pure Tone
	private void analyseID12() {
		sb_pilot=output.samples(get2(inpbuf, data+0));
		pilot=get2(inpbuf, data+2);
		while (pilot > 0) {
			output.play(sb_pilot);
			output.toggleAmp();
			pilot--;
		}
	}

	// ...Sequence of Pulses
	private void analyseID13() {
		pilot=(int) inpbuf[data+0];
		data++;
		while (pilot > 0) {
			sb_pulse = output.samples(get2(inpbuf, data+0));
			output.play(sb_pulse);
			output.toggleAmp();
			pilot--;
			data+=2;
		}
	}

	// ...Pure Data
	private void analyseID14() {
		sb_pilot=pilot=sb_sync1=sb_sync2=0;
		sb_bit0=output.samples(get2(inpbuf, data+0));
		sb_bit1=output.samples(get2(inpbuf, data+2));
		lastbyte=(int) inpbuf[data+4];
		pause=get2(inpbuf, data+5);
		datalen=get3(inpbuf, data+7);
		data+=10;
	}

	// ...Direct Recording
	private void analyseID15() {
		// For now the BEST way is to use the sample frequency for replay that is
		// exactly the SAME as the Original Freq. used when sampling this block !
		// i.e. NO downsampling is handled YET ... use TAPER when you need it ! ;-)

		sb_pulse=output.samples(get2(inpbuf, data+0));
		if (sb_pulse == 0) sb_pulse=1;       	// In case sample frequency > 44100
		pause=get2(inpbuf, data+2);            	// (Should work for frequencies up to 48000)
		lastbyte=(int) inpbuf[data+4];
		datalen=get3(inpbuf, data+5);

		data = data+8;
		datapos=0;
		// Replay Direct Recording block ... 
		while (datalen > 0) {
			if (datalen!=1) bitcount=8;
			else bitcount=lastbyte;
			databyte=inpbuf[data+datapos];
			while (bitcount > 0) {
				output.setAmp((databyte & 0x80) != 0);
				output.play(sb_pulse);
				databyte<<=1;
				bitcount--;
			}
			datalen--;
			datapos++;
		}
		output.toggleAmp(); // Changed on 26-01-2005
		if (pause != 0) output.pause(pause);
	}

	// ...Pause or Stop the Tape
	private void analyseID20() {
		pause=get2(inpbuf, data+0);
		output.setAmpLow();
		if (debug)
			System.out.println("Pause is " + pause);
		if (pause != 0) {
			output.pause(pause);
		} else {
			output.pause(5000); // 5 seconds of pause in "Stop Tape" wave output
			System.out.println("Pause is added: 5 secs");
		}
		output.setAmpLow();
	}

	// ...Group Start
	private void analyseID21() {
		// do nothing
	}

	// ...Group End
	private void analyseID22() {
		// do nothing
	}

	// ...Jump To Relative
	private void analyseID23() {
	  jump = (short)(inpbuf[data+0] + inpbuf[data+1]*256);
      currentBlock+=jump;
      currentBlock--;
	}
	
	// ...Loop Start
	private void analyseID24() {
		loop_start=currentBlock;
		loop_count=get2(inpbuf, data+0);
	}
	
	// ...Loop End
	private void analyseID25() {
		loop_count--;
		if (loop_count>0) {
			currentBlock=loop_start;
		}
	}
	
	// ...Call Sequence
	private void analyseID26() {
		call_pos=currentBlock;
		call_num=get2(inpbuf, data+0);
		call_cur=0;
		jump = (short)(inpbuf[data+2] + inpbuf[data+3]*256);
		currentBlock+=jump;
		currentBlock--;
	}
	
	// ...Return from Sequence
	private void analyseID27() {
		call_cur++;
		if (call_cur==call_num) {
			currentBlock=call_pos;
		} else {
			currentBlock = call_pos;
			data = blockStart[currentBlock]+1;
			jump = (short)(inpbuf[data+call_cur*2+2] + inpbuf[data+call_cur*2+3]*256);
			currentBlock+=jump;
			currentBlock--;
		}
	}
	
	// ...Stop the tape if in 48k mode
	private void analyseID2A() {
	    output.pause(5000);
	    output.setAmpLow();
	}
	
	// ...Hardware Info
	private void analyseID33() {
		/*
		if (inpbuf[data+1]==0 && inpbuf[data+2]>0x14 && inpbuf[data+2]<0x1a && inpbuf[data+3]==1) {
			cpc=1;
		}
		*/
	}
	
	//
	// UTILITY METHODS
	//
	
	// Conversion routines to fetch bytes in Big Endian order
	private static int get2(byte[] data, int pos) {
        return (data[pos] & 0xff | ((data[pos+1]<<8) & 0xff00));
	}
	private static int get3(byte[] data, int pos) {
		return (data[pos] & 0xff | (data[pos+1]<<8) & 0xff00 | (data[pos+2]<<16) & 0xff0000 );
	}
	private static int get4(byte[] data, int pos) {
		return (data[pos] & 0xff | (data[pos+1]<<8) & 0xff00 | (data[pos+2]<<16) & 0xff0000 | (data[pos+3]<<24) & 0xff000000 );
	}


	/**
	 * Count and optionally store the start positions of TZX blocks
	 * in the input[] data array
	 * 
	 * @param blockstarts - array to be filled in with block start positions
	 * or null if this is not required.
	 * 
	 * @return number of blocks found, or -1 if an unknown block was encountered
	 */
	private int countBlocks(int[] blockstarts) {
		// Go through the file and record block starts
		int pos = 10;
		int numblocks = 0;

		while(pos < inpbuf.length) {
			if (blockstarts != null) {
				blockstarts[numblocks] = pos;
			}
			pos++;

			switch(inpbuf[pos-1]) {
				case 0x10: pos += get2(inpbuf, pos+0x02) + 0x04; break;
				case 0x11: pos += get3(inpbuf, pos+0x0F) + 0x12; break;
				case 0x12: pos += 0x04; break;
				case 0x13: pos += (inpbuf[pos+0x00]*0x02) + 0x01; break;
				case 0x14: pos += get3(inpbuf, pos+0x07) + 0x0A; break;
				case 0x15: pos += get3(inpbuf, pos+0x05) + 0x08; break;
				case 0x16: pos += get4(inpbuf, pos+0x00) + 0x04; break;
				case 0x17: pos += get4(inpbuf, pos+0x00) + 0x04; break;
	
				case 0x20: pos += 0x02; break;
				case 0x21: pos += inpbuf[pos+0x00] + 0x01; break;
				case 0x22: break;
				case 0x23: pos += 0x02; break;
				case 0x24: pos += 0x02; break;
				case 0x25: break;
				case 0x26: pos += get2(inpbuf, pos+0x00) * 0x02 + 0x02; break;
				case 0x27: break;
				case 0x28: pos += get2(inpbuf, pos+0x00) + 0x02; break;
				case 0x2A: pos += 0x04; break;
	
				case 0x30: pos += inpbuf[pos+0x00] + 0x01; break;
				case 0x31: pos += inpbuf[pos+0x01] + 0x02; break;
				case 0x32: pos += get2(inpbuf, pos+0x00) + 0x02; break;
				case 0x33: pos += (inpbuf[pos+0x00]*0x03) + 0x01; break;
				case 0x34: pos += 0x08; break;
				case 0x35: pos += get4(inpbuf, pos+0x10) + 0x14; break;
	
				case 0x40: pos += get3(inpbuf, pos+0x01) + 0x04; break;
	
				case 0x5A: pos += 0x09; break;
	
				default: return -1;
			}

			numblocks++;
		}

		return numblocks;
	}


	/**
	 * Run a pass of the conversion process, sending data to the supplied
	 * output manager object.
	 * 
	 * @param output - the output manager
	 */
	private void convertPass(CDT2WAVBaseOutput output) {
		// Initialise
		currentBlock = 0;
		singlepulse = 0;
		
		// disable debug mode if this is a test run
        if (output == null) {
            debug = false;
        } 
        
		// Start replay of blocks (Main loop of the program)
		while (currentBlock < numBlocks) {
			// Get ID of next block and start position in input byte array
			id = inpbuf[blockStart[currentBlock]];
            if (debug)
                System.out.println("ID is " + Util.hex(id));
			data = blockStart[currentBlock]+1;
			
			switch (id) {
				case 0x10:
					analyseID10(); // Standard Loading Data block
					break;
				case 0x11:
					analyseID11(); // Custom Loading Data block
					break;
				case 0x12:
					analyseID12(); // Pure Tone
					break;
				case 0x13:
					analyseID13(); // Sequence of Pulses
					break;
				case 0x14:
					analyseID14(); // Pure Data
					break;
				case 0x15:
					analyseID15(); // Direct Recording
					break;
				case 0x20:
					analyseID20(); // Pause or Stop the Tape command
					break;
				case 0x21:
					analyseID21(); // Group Start
					break;
				case 0x22:
					analyseID22(); // Group End
					break;
				case 0x23:
					analyseID23(); // Jump To Relative
					break;
				case 0x24:
					analyseID24(); // Loop Start
					break;
				case 0x25:
					analyseID25(); // Loop End
					break;
				case 0x26:
					analyseID26(); // Call Sequence
					break;
				case 0x27:
					analyseID27(); // Return from Sequence
					break;
				case 0x2A:
					analyseID2A(); // Stop the tape if in 48k mode
					break;
				case 0x33:
					analyseID33(); // Hardware Info
					break;
	
				// Ignored
				case 0x30: // Description
				case 0x31: // Message
				case 0x32: // Archive Info
				case 0x34: // Emulation info
				case 0x35: // Custom Info
				case 0x40: // Snapshot
				case 0x5A: // ZXTape!
					break;
	
				// Unknown/Unsupported blocks
				case 0x16: // C64 ROM Type Data Block
				case 0x17: // C64 Turbo Tape Data Block
				case 0x28: // Select Block
				default:{
					System.out.println("ERR_TZX_UNSUPPORTED");
					break;
                }
			}

			// TZX file blocks analysis finished
			// Now we start generating the sound waves

			if (id==0x10 || id==0x11 || id==0x14) {
				// One of the data blocks ...

				// Play PILOT TONE
				while (pilot > 0) {
					output.play(sb_pilot);
					output.toggleAmp();
					pilot--;
				}
				
				// Play first SYNC pulse
				if (sb_sync1 > 0) {
					output.play(sb_sync1);
					output.toggleAmp();
				}
				
				// Play second SYNC pulse
				if (sb_sync2 > 0) {
					output.play(sb_sync2);
					output.toggleAmp();
				}
				
				// Play actual DATA
				datapos=0;
				while (datalen > 0) {
					if (datalen!=1) bitcount=8;
					else bitcount=lastbyte;
					
					databyte = inpbuf[data+datapos];
					
					while (bitcount > 0) {
						if ((databyte&0x80) != 0) sb_bit=sb_bit1;
						else sb_bit=sb_bit0;
						output.play(sb_bit); // Play first pulse of the bit
						output.toggleAmp();
						if (singlepulse == 0) {
							output.play(sb_bit); // Play second pulse of the bit
							output.toggleAmp();
						}
						databyte<<=1;
						bitcount--;
					}
					datalen--;
					datapos++;
				}
				singlepulse=0;   // Reset flag for next TZX blocks

				// If there is pause after block present then make first millisecond the oposite
				// pulse of last pulse played and the rest in LOAMP ... otherwise don't do ANY pause
				if (pause > 0) {
					output.pause(1);
					output.setAmpLow();
					if (pause>1) output.pause(pause-1);
				}
			}

			// We continue to replay the next TZX block
			currentBlock++; 
		}
        
		// 5 seconds of pause in "Stop Tape" wave output
		output.pause(5000);
		if (debug)
			System.out.println("End of tape... 5 seconds pause added");
		
		output.stop();
		
		if (debug)
			System.out.println(" OK");
	}

	/**
	 * MAIN CONVERSION ENTRY POINT
	 * 
	 * @return array of data bytes generated, or null on error
	 */
	public byte[] convert() {
		// Sanity checks
		// ...check input and output
		if (inpbuf == null || inpbuf.length < 10) {
			System.out.println("ERR_ILLEGAL_ARGUMENT") ;
            return null;
		}

		// ...check for TZX header
		for (int i = 0; i < ZXTAPE_HEADER.length; i++) {
			if (inpbuf[i] != ZXTAPE_HEADER[i]) {
				System.out.println("ERR_NOT_TZX");
                return null;
			}
		}

		// ...check TZX version is supported
		int cdt_major = inpbuf[8];
		if (cdt_major == 0) {
			System.out.println("ERR_TZX_UNSUPPORTED");
            return null;
		}

		// Count blocks
		currentBlock = 0;
		numBlocks = countBlocks(null);
		if (numBlocks < 0) {
			System.out.println("ERR_TZX_UNSUPPORTED");
            return null;
		}
		
		// Store the start positions of all blocks
		blockStart = new int[numBlocks];
		countBlocks(blockStart);

		// Initialise WAV format output manager, initially in test mode
		// @note: if you ever need to support a different output format,
		// create a new CDT2WAVxyzOutput class and switch it in here.
		output = new CDT2WAVWAVOutput(frequency);

		// Run first pass in test mode to determine data length
        debug = false;
		convertPass(output);
		
		// If the first pass succeeded, allocate an output buffer
		// of the correct length to hold the output data
		int dataLength = output.outputTell();
		byte[] data = null;	
		if (dataLength > 0) {
			// Run second pass to generate data and store in output buffer
			data = new byte[dataLength];
			output.setOutputBuffer(data);
			convertPass(output);
		}
		
		// Return data buffer
		return data;
	}

}
