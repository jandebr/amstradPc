package jemu.settings;

import java.io.File;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradSettings;

/**
 * User settings from last session.
 * 
 * @author Roland.Barthel
 */
public class Settings {

	/** the settings instance. */
	private static final Settings instance = new Settings(AmstradFactory.getInstance().getAmstradContext()
			.getUserSettings());

	public static final String FLOPPYX = "floppy_xpos";
	public static final String FLOPPYY = "floppy_ypos";
	public static final String FRAMEX = "frame_xpos";
	public static final String FRAMEY = "frame_ypos";
	public static final String FLOPPYZOOM = "floppy_zoom";
	public static final String DF0HEAD = "df0_head";
	public static final String DF1HEAD = "df1_head";
	public static final String DIAGNOSE = "diagnostic";

	public static final String Settings = "settings";
	public static final String DIMENSION = "keep_display_propertions";

	public static final String SHOWDRIVE = "showdrive";
	public static final String POLARITY = "changePolarity";
	public static final String OVERRIDEP = "override_p";
	public static final String CRTC = "crtc";

	public static final String GZIP = "gzip_compression";
	public static final String KEYREC = "keyboard_recording";
	public static final String INTACK = "cpu_intack_cycles";
	public static final String BREAKPOINTS = "breakpoints";
	public static final String BREAKINST = "breakinstructions";
	public static final String KHZ44 = "recrate44";
	public static final String KHZ11 = "recrate11";
	public static final String PRINTER = "printer";
	public static final String FIRSTRUN = "firstrun";
	public static final String LOWPERFORMANCE = "lowperformance";
	public static final String FIRETIMER = "autofiretime";
	public static final String COMPUTERNAME = "computername";
	public static final String DIGIBLASTER = "digiblaster";
	public static final String FLOPPYTURBO = "floppyturbo";
	public static final String DBVOLUME = "digiblaster_volume";
	public static final String VSOFT = "vsoft_output";
	public static final String CPCE95 = "cpce95_output";
	public static final String KAYOut = "hacker_kay_output";
	public static final String AYEFFECT = "ay_effect";
	public static final String LINEAR = "linear_sound";
	public static final String VHOLD = "vertical_hold";
	public static final String SKINNED = "useskin";
	public static final String EXPANSION = "expansion";
	public static final String MEMORY = "memory";

	/** Property for small or large display (boolean). */
	public static final String LARGE = "large";
	public static final String TRIPLE = "triple_size";
	public static final String DOUBLE = "double_size";
	public static final String FULLSCREEN = "full_screen";

	/** Property for sound on/off (boolean). */
	public static final String AUDIO = "audio";

	public static final String MESSAGE = "show_about";
	public static final String AUTOCHECK = "autocheck";

	/** Property for autosave (boolean). */
	public static final String AUTOSAVE = "autosave";
	public static final String CHECKSAVE = "checksave";
	public static final String CHECKRENAME = "never_rename";
	public static final String AUTOBOOT = "autoboot";

	public static final String DRIVE_FILE = "file.drive";
	public static final String LOADDRIVE = "loaddrive";
	public static final String TAPE_FILE = "file.tape";
	public static final String LOADTAPE = "loadtape";
	public static final String FLOPPYSOUND = "floppy_sound";
	public static final String ONTOP = "on_top";
	public static final String HIDEFRAME = "hide_frame";
	public static final String SHOWMENU = "show_menu";
	public static final String NOTEBOOK = "notebook";
	public static final String STANDALONE = "standalone";
	public static final String CONSOLE = "console";

	public static final String SYSTEM_DIR = "system_dir";
	public static final String SNAPSHOT_FILE = "snapshot_file";

	/** Property for choosen system (String). */
	public static final String SYSTEM = "system";
	public static final String VOLUME = "volume";
	public static final String MONITOR = "monitor";
	public static final String BRIGHTNESS = "brightness";
	public static final String JOYSTICK = "joystick";
	public static final String OSD = "onscreendisplay";
	public static final String AUTOLOAD = "autoload";
	public static final String AUTOTYPE = "autotype";
	public static final String MOUSEJOY = "mousejoy";

	public static final String SCANLINES = "scanlines";
	public static final String SCANEFFECT = "display_effect";
	public static final String BILINEAR = "bilinear";

	public static final String ALLOW_SCANLINES_LOWPERFORMANCE = "lowperformance.allow_scanlines";
	public static final String ALLOW_SCANEFFECT_LOWPERFORMANCE = "lowperformance.allow_display_effect";
	public static final String ALLOW_BILINEAR_LOWPERFORMANCE = "lowperformance.allow_bilinear";

	// Enum values for "MONITOR" setting
	public static final String MONITOR_COLOUR = "COLOUR";
	public static final String MONITOR_GREEN = "GREEN";
	public static final String MONITOR_GRAY = "GRAY";
	public static final String MONITOR_COLOUR2 = "COLOUR2";

	// Rom banks
	// in progress
	//

	public static final String LOWER_ROM = "lower";
	public static final String UPPER_ROM_0 = "upper_0";
	public static final String UPPER_ROM_1 = "upper_1";
	public static final String UPPER_ROM_2 = "upper_2";
	public static final String UPPER_ROM_3 = "upper_3";
	public static final String UPPER_ROM_4 = "upper_4";
	public static final String UPPER_ROM_5 = "upper_5";
	public static final String UPPER_ROM_6 = "upper_6";
	public static final String UPPER_ROM_7 = "upper_7";
	public static final String UPPER_ROM_8 = "upper_8";
	public static final String UPPER_ROM_9 = "upper_9";
	public static final String UPPER_ROM_A = "upper_A";
	public static final String UPPER_ROM_B = "upper_B";
	public static final String UPPER_ROM_C = "upper_C";
	public static final String UPPER_ROM_D = "upper_D";
	public static final String UPPER_ROM_E = "upper_E";
	public static final String UPPER_ROM_F = "upper_F";

	public static final String romlow = "lowrom_index";
	public static final String rom0 = "upper0_index";
	public static final String rom1 = "upper1_index";
	public static final String rom2 = "upper2_index";
	public static final String rom3 = "upper3_index";
	public static final String rom4 = "upper4_index";
	public static final String rom5 = "upper5_index";
	public static final String rom6 = "upper6_index";
	public static final String rom7 = "upper7_index";
	public static final String rom8 = "upper8_index";
	public static final String rom9 = "upper9_index";
	public static final String rom10 = "upperA_index";
	public static final String rom11 = "upperB_index";
	public static final String rom12 = "upperC_index";
	public static final String rom13 = "upperD_index";
	public static final String rom14 = "upperE_index";
	public static final String rom15 = "upperF_index";

	/** the settings source */
	private AmstradSettings source;

	/**
	 * Create a new instance
	 */
	private Settings(AmstradSettings source) {
		this.source = source;
	}

	/**
	 * Return a boolean property value.
	 * 
	 * @param key
	 *            property key
	 * @param defaultValue
	 *            default when property is not set
	 * @return value or default
	 */
	public static boolean getBoolean(final String key, final boolean defaultValue) {
		final String value = get(key, String.valueOf(defaultValue));
		return value.equals("true");
	}

	/**
	 * Set a boolean property value.
	 * 
	 * @param key
	 *            property key
	 * @param value
	 *            boolean value to set
	 */
	public static void setBoolean(final String key, final boolean value) {
		set(key, String.valueOf(value));
	}

	/**
	 * Return a property value.
	 * 
	 * @param key
	 *            property key
	 * @param defaultValue
	 *            default when property is not set
	 * @return value or default
	 */
	public static String get(final String key, final String defaultValue) {
		return instance.getSource().get(key, defaultValue);
	}

	/**
	 * Set a property value.
	 * 
	 * @param key
	 *            property key
	 * @param value
	 *            value to set
	 */
	public static void set(final String key, final String value) {
		instance.getSource().set(key, value);
	}

	private AmstradSettings getSource() {
		return source;
	}

}