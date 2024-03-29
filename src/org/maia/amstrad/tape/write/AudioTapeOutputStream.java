package org.maia.amstrad.tape.write;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.maia.amstrad.tape.model.Bit;

public class AudioTapeOutputStream extends TapeOutputStream {

	private File audioFile;

	private FileOutputStream outputStream;

	private static int SAMPLE_RATE = 44100;

	private static byte[] HEADER_44Khz = new byte[] { 82, 73, 70, 70, -46, 89, 12, 0, 87, 65, 86, 69, 102, 109, 116,
			32, 16, 0, 0, 0, 1, 0, 1, 0, 68, -84, 0, 0, -120, 88, 1, 0, 2, 0, 16, 0, 100, 97, 116, 97, -82, 89, 12, 0 };

	private static short[] SPACER = new short[] { -2417, -2214, -1979, -1792, -1695, -1628, -1525, -1423, -1374, -1404,
			-1426, -1390, -1329, -1296, -1262, -1179, -1052, -960, -894, -844, -767, -728, -755, -760, -726, -646,
			-627, -635, -605, -508, -406, -359, -320, -256, -200, -186, -173, -137, -75, -86, -106, -68, 58, 156, 179,
			172, 218, 282, 284, 263, 287, 357, 402, 370, 360, 388, 439, 463, 473, 528, 567, 580, 571, 616, 661, 683,
			664, 698, 758, 805, 798, 811, 855, 875, 849, 843, 929, 1027, 1058, 1032, 1052, 1114, 1133, 1092, 1086,
			1142, 1197, 1159, 1114, 1133, 1209, 1231, 1196, 1179, 1200, 1219, 1216, 1233, 1319, 1394, 1441, 1433, 1451,
			1428, 1397, 1361, 1381, 1416, 1419, 1380, 1384, 1388, 1387, 1324, 1299, 1308, 1356, 1349, 1351, 1334, 1365,
			1355, 1366, 1373, 1397, 1389, 1375, 1359, 1393, 1421, 1439, 1419, 1382, 1343, 1334, 1357, 1363, 1359, 1326,
			1331, 1361, 1381, 1387, 1381, 1417, 1431, 1445, 1415, 1393, 1387, 1394, 1422, 1442, 1424, 1388, 1367, 1411,
			1455, 1468, 1444, 1450, 1477, 1510, 1499, 1504, 1487, 1469, 1436, 1438, 1469, 1478, 1421, 1391, 1394, 1442,
			1446, 1421, 1418, 1443, 1449, 1465, 1483, 1530, 1517, 1483, 1468, 1521, 1544, 1516, 1460, 1465, 1494, 1469,
			1437, 1409, 1433, 1411, 1369, 1357, 1396, 1399, 1338, 1285, 1306, 1358, 1356, 1322, 1332, 1380, 1398, 1359,
			1372, 1421, 1458, 1403, 1342, 1349, 1395, 1380, 1314, 1275, 1308, 1336, 1326, 1307, 1315, 1334, 1309, 1297,
			1307, 1324, 1289, 1202, 1178, 1201, 1222, 1181, 1128, 1133, 1174, 1173, 1144, 1149, 1199, 1245, 1208, 1151,
			1141, 1164, 1181, 1140, 1119, 1132, 1166, 1154, 1127, 1128, 1143, 1155, 1104, 1100, 1122, 1188, 1174, 1119,
			1061, 1068, 1099, 1111, 1112, 1127, 1167, 1174, 1124, 1070, 1051, 1069, 1072, 1035, 1019, 1035, 1072, 1064,
			1039, 1018, 1063, 1091, 1068, 1016, 974, 998, 992, 968, 924, 943, 981, 984, 917, 862, 860, 892, 875, 846,
			831, 888, 903, 877, 816, 817, 853, 860, 837, 803, 825, 839, 825, 808, 812, 849, 855, 822, 807, 811, 826,
			797, 749, 721, 732, 745, 742, 725, 731, 736, 747, 709, 678, 651, 675, 689, 703, 655, 657, 661, 698, 697,
			688, 695, 736, 745, 712, 659, 623, 608, 581, 559, 571, 612, 621, 602, 594, 631, 668, 630, 576, 541, 572,
			573, 526, 488, 511, 545, 536, 467, 452, 498, 536, 494, 415, 399, 437, 467, 414, 380, 393, 448, 462, 408,
			363, 380, 394, 402, 367, 383, 386, 387, 327, 311, 331, 375, 362, 334, 331, 389, 420, 414, 372, 365, 378,
			357, 342, 328, 367, 372, 330, 290, 280, 297, 268, 199, 172, 195, 236, 222, 204, 213, 247, 232, 185, 178,
			221, 254, 211, 175, 189, 243, 220, 165, 143, 191, 215, 143, 82, 78, 145, 146, 121, 115, 168, 176, 140, 100,
			144, 165, 152, 104, 145, 195, 218, 171, 166, 190, 177, 110, 61, 102, 134, 118, 69, 91, 126, 99, 30, 4, 64,
			90, 54, 24, 57, 112, 89, 44, 26, 53, 37, 4, -4, 53, 76, 55, 28, 33, 45, 18, -10, 0, 15, 3, -46, -49, -31,
			-25, -56, -81, -83, -75, -72, -41, -1, 40, 28, -5, -34, -69, -77, -74, -32, -36, -76, -107, -95, -50, -53,
			-88, -102, -113, -142, -204, -241, -222, -219, -236, -246, -226, -211, -237, -253, -225, -175, -196, -235,
			-238, -175, -171, -212, -275, -258, -232, -261, -299, -313, -276, -266, -272, -271, -250, -266, -296, -298,
			-282, -281, -333, -346, -302, -245, -252, -287, -275, -257, -252, -291, -286, -230, -225, -257, -288, -239,
			-179, -167, -183, -156, -99, -92, -162, -207, -195, -166, -152, -161, -109, -72, -83, -141, -152, -82, -20,
			-21, -48, -53, -17, -34, -67, -99, -78, -81, -120, -156, -124, -65, -43, -86, -111, -91, -63, -85, -145,
			-154, -137, -121, -152, -187, -192, -190, -197, -218, -214, -213, -249, -263, -239, -146, -98, -122, -171,
			-152, -77, -37, -54, -95, -85, -74, -73, -117, -113, -94, -79, -120, -158, -152, -126, -114, -144, -153,
			-122, -106, -141, -200, -218, -174, -181, -224, -289, -244, -193, -193, -288, -363, -335, -277, -273, -324,
			-357, -364, -376, -416, -397, -326, -268, -282, -327, -318, -275, -292, -349, -377, -328, -274, -286, -305,
			-293, -240, -232, -257, -251, -228, -234, -298, -315, -272, -210, -232, -293, -294, -215, -163, -187, -236,
			-264, -255, -298, -330, -335, -297, -286, -309, -312, -273, -244, -268, -304, -308, -289, -309, -361, -385,
			-369, -360, -421, -437, -425, -375, -403, -460, -460, -394, -355, -406, -448, -432, -380, -399, -475, -479,
			-431, -359, -363, -356, -316, -296, -328, -383, -361, -303, -279, -307, -321, -292, -285, -322, -325, -307,
			-280, -333, -369, -336, -278, -286, -341, -367, -343, -371, -446, -475, -422, -390, -452, -550, -530, -461,
			-423, -464, -448, -399, -355, -419, -453, -442, -391, -425, -456, -433, -373, -364, -434, -452, -425, -396,
			-428, -462, -427, -385, -389, -431, -440, -416, -416, -443, -463, -429, -403, -403, -427, -437, -438, -427,
			-429, -413, -417, -414, -406, -395, -388, -399, -395, -369, -333, -312, -309, -300, -320, -352, -390, -389,
			-373, -358, -372, -372, -361, -361, -357, -358, -344, -326, -319, -299, -294, -286, -298, -268, -235, -221,
			-228, -251, -232, -239, -257, -274, -242, -207, -215, -234, -244, -206, -219, -251, -260, -208, -161, -156,
			-167, -140, -98, -99, -138, -161, -163, -169, -192, -200, -184, -185, -208, -202, -166, -133, -139, -186,
			-211, -215, -227, -216, -213, -220, -250, -282, -265, -251, -244, -271, -234, -194, -186, -225, -227, -168,
			-121, -157, -200, -188, -107, -96, -128, -164, -136, -129, -156, -203, -199, -181, -186, -197, -178, -147,
			-165, -203, -209, -174, -152, -203, -234, -225, -162, -157, -160, -167, -135, -154, -185, -238, -202, -193,
			-191, -240, -251, -270, -279, -314, -305, -253, -207, -196, -215, -223, -219, -233, -253, -235, -191, -143,
			-159, -194, -196, -176, -141, -171, -166, -159, -97, -99, -121, -150, -123, -97, -87, -139, -144, -124,
			-89, -100, -139, -143, -113, -78, -102, -148, -155, -125, -90, -86, -99, -89, -83, -100, -150, -169, -171,
			-166, -188, -232, -247, -255, -257, -270, -245, -220, -198, -217, -231, -206, -169, -150, -192, -229, -232,
			-192, -156, -139, -163, -154, -158, -161, -212, -268, -285, -250, -208, -216, -254, -264, -230, -217, -259,
			-328, -347, -310, -286, -297, -301, -255, -185, -176, -212, -215, -170, -105, -140, -192, -230, -195, -181,
			-218, -267, -252, -205, -202, -239, -271, -225, -193, -207, -263, -249, -182, -130, -161, -216, -204, -155,
			-122, -162, -203, -201, -174, -171, -202, -226, -207, -200, -193, -236, -222, -193, -127, -96, -121, -143,
			-153, -132, -126, -152, -153, -121, -83, -87, -130, -135, -85, -66, -94, -145, -130, -64, -42, -91, -110,
			-77, -19, -51, -137, -152, -86, -18, -47, -125, -149, -81, -39, -43, -100, -101, -74, -74, -118, -185,
			-161, -122, -86, -136, -184, -183, -135, -114, -132, -149, -113, -57, -65, -90, -87, -37, 17, -10, -37,
			-36, -18, -19, -39, -31, 3, -4, -49, -46, 9, 62, 38, -29, -19, 33, 59, 36, 25, 65, 74, 25, -20, -2, 61, 47,
			-3, -20, 40, 69, 45, 17, 46, 90, 69, 29, 50, 118, 131, 82, 53, 108, 159, 117, 57, 50, 115, 147, 104, 93,
			130, 180, 173, 139, 151, 193, 209, 215, 252, 324, 370 };

	private static short MIN_SAMPLE_VALUE = -32768;

	private static short MAX_SAMPLE_VALUE = 24576;

	public AudioTapeOutputStream(File audioFile) throws IOException {
		this.audioFile = audioFile;
		this.outputStream = new FileOutputStream(audioFile);
		writeHeader();
	}

	private void writeHeader() throws IOException {
		getOutputStream().write(HEADER_44Khz, 0, HEADER_44Khz.length);
	}

	private void writeSample(short sample) throws IOException {
		getOutputStream().write(sample % 256);
		getOutputStream().write(sample / 256);
	}

	@Override
	public void writeSilence(long millis) throws IOException {
		int samples = (int) (SAMPLE_RATE * millis / 1000);
		for (int i = 0; i < samples; i++) {
			writeSample((short) 0);
		}
	}

	@Override
	public void writeBit(Bit bit) throws IOException {
		int high = 0;
		int low = 0;
		if (Bit.ONE.equals(bit)) {
			high = 33;
			low = 33;
		} else {
			high = 15;
			low = 15;
		}
		for (int i = 0; i < high; i++)
			writeSample(MAX_SAMPLE_VALUE);
		for (int i = 0; i < low; i++)
			writeSample(MIN_SAMPLE_VALUE);
	}

	@Override
	public void writeSpacer() throws IOException {
		for (int i = 0; i < SPACER.length; i++)
			writeSample(SPACER[i]);
	}

	public void close() throws IOException {
		super.close();
		getOutputStream().flush();
		getOutputStream().close();
		// Set correct sizes in header
		int n = (int) getAudioFile().length();
		RandomAccessFile raf = new RandomAccessFile(getAudioFile(), "rw");
		writeHeaderValue(raf, 4, n - 8); // ChunkSize
		writeHeaderValue(raf, 40, n - 44); // Subchunk2Size
		raf.close();
	}

	private void writeHeaderValue(RandomAccessFile raf, int offset, int value) throws IOException {
		raf.seek(offset);
		raf.write(value & 0xff);
		raf.write((value & 0xff00) >>> 8);
		raf.write((value & 0xff0000) >>> 16);
		raf.write((value & 0xff000000) >>> 24);
	}

	public File getAudioFile() {
		return audioFile;
	}

	private FileOutputStream getOutputStream() {
		return outputStream;
	}

}
