package fr.xgouchet.texteditor.common;

import android.content.SharedPreferences;

public class Settings implements Constants {

	/** Number of recent files to remember */
	public static int MAX_RECENT_FILES = 10;

	/** Show the lines numbers */
	public static boolean SHOW_LINE_NUMBERS = true;

	/** automatic break line to fit one page */
	public static boolean WORDWRAP = false;

	/** when search reaches the end of a file, search wrap */
	public static boolean SEARCHWRAP = false;
	/** only search for matchin case */
	public static boolean SEARCHMATCHCASE = false;

	/** Text size setting */
	public static int TEXT_SIZE = 12;

	/** Default end of line */
	public static int DEFAULT_END_OF_LINE = EOL_LINUX;

	/** End Of Line style */
	public static int END_OF_LINE = EOL_LINUX;

	/** Encoding */
	public static String ENCODING = ENC_UTF8;

	/** Let auto save on quit be triggered */
	public static boolean AUTO_SAVE_MODE = true;

	/** color setting */
	public static int COLOR = COLOR_CLASSIC;

	/** enable fling to scroll */
	public static boolean FLING_TO_SCROLL = false;

	/**
	 * @return the end of line characters according to the current settings
	 */
	public static String getEndOfLine() {
		switch (END_OF_LINE) {
		case EOL_MAC: // Mac OS
			return "\r";
		case EOL_WINDOWS: // Windows
			return "\r\n";
		case EOL_LINUX: // Linux / Android
		default:
			return "\n";
		}
	}

	/**
	 * Update the settings from the given {@link SharedPreferences}
	 * 
	 * @param settings
	 *            the settings to read from
	 */
	public static void updateFromPreferences(SharedPreferences settings) {

		Settings.MAX_RECENT_FILES = getStringPreferenceAsInteger(settings,
				PREFERENCE_MAX_RECENTS, "10");
		Settings.SHOW_LINE_NUMBERS = settings.getBoolean(
				PREFERENCE_SHOW_LINE_NUMBERS, true);
		Settings.WORDWRAP = settings.getBoolean(PREFERENCE_WORDWRAP, false);
		Settings.TEXT_SIZE = getStringPreferenceAsInteger(settings,
				PREFERENCE_TEXT_SIZE, "12");
		Settings.DEFAULT_END_OF_LINE = getStringPreferenceAsInteger(settings,
				PREFERENCE_END_OF_LINES, ("" + EOL_LINUX));
		Settings.AUTO_SAVE_MODE = settings.getBoolean(
				PREFERENCE_AUTO_SAVE_MODE, true);
		Settings.COLOR = getStringPreferenceAsInteger(settings,
				PREFERENCE_COLOR_THEME, ("" + COLOR_CLASSIC));
		Settings.SEARCHWRAP = settings.getBoolean(PREFERENCE_SEARCHWRAP, false);
		Settings.SEARCHMATCHCASE = settings.getBoolean(
				PREFERENCE_SEARCH_MATCH_CASE, false);
		Settings.ENCODING = settings.getString(PREFERENCE_ENCODING, ENC_UTF8);
		Settings.FLING_TO_SCROLL = settings.getBoolean(
				PREFERENCE_FLING_TO_SCROLL, true);

		RecentFiles.loadRecentFiles(settings.getString(PREFERENCE_RECENTS, ""));
	}

	/**
	 * Reads a preference stored as a string and returns the numeric value
	 * 
	 * @param prefs
	 *            the prefernce to read from
	 * @param key
	 *            the key
	 * @param def
	 *            the default value
	 * @return the value as an int
	 */
	protected static int getStringPreferenceAsInteger(SharedPreferences prefs,
			String key, String def) {
		String strVal;
		int intVal;

		strVal = null;
		try {
			strVal = prefs.getString(key, def);
		} catch (Exception e) {
			strVal = def;
		}

		try {
			intVal = Integer.parseInt(strVal);
		} catch (NumberFormatException e) {
			intVal = 0;
		}

		return intVal;
	}
}
