package org.maia.amstrad.tape.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.net.URL;
import java.text.NumberFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class UIResourcesTape {

	public static Icon windowIcon = loadIcon("tape24.png");

	public static Icon amstradIcon = loadIcon("amstrad.png");

	public static Icon wavesIcon = loadIcon("waves.png");

	public static Icon spinnerIcon = loadIcon("spinner48.gif");

	public static Icon errorIcon = loadIcon("error.png");

	public static Icon tapeIcon = loadIcon("tape16.png");

	public static Icon pencilIcon = loadIcon("pencil16.png");

	public static String openCodeInspectorLabel = "Inspect";

	public static String openCodeInspectorTooltip = "Inspect the source code as recorded on tape";

	public static Icon openCodeInspectorIcon = loadIcon("code-trace-glass32.png");

	public static String revertCodeLabel = "Revert code";

	public static String revertCodeTooltip = "Revert all changes to the source code";

	public static Icon revertCodeIcon = loadIcon("code-revert32.png");

	public static String editCodeLabel = "Edit code";

	public static String editCodeTooltip = "Edit the source code";

	public static Icon editCodeIcon = loadIcon("code-edit32.png");

	public static String editMetadataLabel = "Edit metadata";

	public static String editMetadataTooltip = "Edit the program metadata";

	public static Icon editMetadataIcon = loadIcon("metadata-edit32.png");

	public static String loadProgramLabel = "Load";

	public static String loadProgramTooltip = "Load (and edit) the source code in an emulated CPC";

	public static Icon loadProgramIcon = loadIcon("cpc32.png");

	public static String runProgramLabel = "Run";

	public static String runProgramTooltip = "Run the source code (staged) in an emulated CPC";

	public static Icon runProgramIcon = loadIcon("cpc-run32.png");

	public static String clearSelectionLabel = "Unselect";

	public static String clearSelectionTooltip = "Unselect the program";

	public static Icon clearSelectionIcon = loadIcon("unselect32.png");

	public static String readAudioFileLabel = "Read audio file...";

	public static String readAudioFileTooltip = "Restore Basic programs from an audio file";

	public static Icon readAudioFileIcon = loadIcon("tape32.png");

	public static Icon metadataDocumentSmallIcon = loadIcon("metadata-doc16.png");

	public static Icon metadataDocumentLargeIcon = loadIcon("metadata-doc32.png");

	public static Icon sourceCodeDocumentSmallIcon = loadIcon("code-doc16.png");

	public static Icon sourceCodeDocumentLargeIcon = loadIcon("code-doc32.png");

	public static Dimension sourceCodeViewSize = new Dimension(1024, 600);

	public static Dimension byteCodeViewSize = new Dimension(1024, 600);

	public static Dimension sourceCodeInspectorViewSize = new Dimension(1024, 300);

	public static Dimension byteCodeInspectorViewSize = new Dimension(1024, 300);

	public static Dimension audioInspectorViewSize = new Dimension(1024, 284);

	public static int audioViewHeight = 240;

	public static int audioProfileViewHeight = 240;

	public static int audioPositionViewHeight = 24;

	public static int audioPositionFileNameRepeatGap = 200;

	public static int audioExtendedViewHeight = audioViewHeight + audioPositionViewHeight + 20;

	private static float[] hsbComps = new float[3];

	private static float[] rgbaComps = new float[4];

	private static NumberFormat twoDigitNumberFormat;

	private static NumberFormat threeDigitNumberFormat;

	static {
		twoDigitNumberFormat = NumberFormat.getIntegerInstance();
		twoDigitNumberFormat.setMinimumIntegerDigits(2);
		threeDigitNumberFormat = NumberFormat.getIntegerInstance();
		threeDigitNumberFormat.setMinimumIntegerDigits(3);
	}

	public static Color adjustBrightness(Color color, double factor) {
		if (factor == 0)
			return color;
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComps);
		double brightness = hsbComps[2];
		double darkness = 1.0 - brightness;
		if (factor >= 0) {
			// increase brightness
			brightness = 1.0 - darkness * (1.0 - factor);
		} else {
			// increase darkness
			darkness = 1.0 - brightness * (1.0 + factor);
			brightness = 1.0 - darkness;
		}
		int rgba = (color.getAlpha() << 24)
				| (Color.HSBtoRGB(hsbComps[0], hsbComps[1] * (float) (Math.min(1.0, 1.0 - factor)), (float) brightness)
						& 0x00ffffff);
		return new Color(rgba, true);
	}

	public static Color setTransparency(Color color, double transparency) {
		color.getRGBColorComponents(rgbaComps);
		rgbaComps[3] = (float) (1.0 - transparency);
		return new Color(rgbaComps[0], rgbaComps[1], rgbaComps[2], rgbaComps[3]);
	}

	public static String formatTimeOfAudioSamplePosition(long samplePosition, int sampleRate, boolean millisPrecision) {
		long seconds = Math.floorDiv(samplePosition, sampleRate);
		long hours = Math.floorDiv(seconds, 3600);
		int secondsInHour = Math.floorMod(seconds, 3600);
		int minutesInHour = Math.floorDiv(secondsInHour, 60);
		int secondsInMinute = Math.floorMod(secondsInHour, 60);
		String str = twoDigitNumberFormat.format(hours) + ':' + twoDigitNumberFormat.format(minutesInHour) + ':'
				+ twoDigitNumberFormat.format(secondsInMinute);
		if (millisPrecision) {
			int samplesInSecond = Math.floorMod(samplePosition, sampleRate);
			int millis = Math.round(samplesInSecond * 1000f / sampleRate);
			str += '.' + threeDigitNumberFormat.format(millis);
		}
		return str;
	}

	private static Icon loadIcon(String resourceName) {
		URL url = UIResourcesTape.class.getResource("icons/" + resourceName);
		return new ImageIcon(url);
	}

}