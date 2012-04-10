package fr.xgouchet.texteditor;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.Settings;
import fr.xgouchet.texteditor.ui.view.AdvancedEditText;

public class TedSettingsActivity extends PreferenceActivity implements
		Constants, OnSharedPreferenceChangeListener {

	/**
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		getPreferenceManager().setSharedPreferencesName(
				"fr.xgouchet.texteditor");

		addPreferencesFromResource(R.xml.ted_prefs);
		setContentView(R.layout.layout_prefs);

		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

		mSampleTED = (AdvancedEditText) findViewById(R.id.sampleEditor);

		Settings.updateFromPreferences(getPreferenceManager()
				.getSharedPreferences());
		mSampleTED.updateFromSettings();
		mSampleTED.setEnabled(false);

		updateSummaries();
	}

	/**
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences,
	 *      java.lang.String)
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Settings.updateFromPreferences(sharedPreferences);
		mSampleTED.updateFromSettings();

		updateSummaries();
	}

	/**
	 * Updates the summaries for every list
	 */
	protected void updateSummaries() {
		ListPreference listPref;

		listPref = (ListPreference) findPreference("color_theme");
		listPref.setSummary(listPref.getEntry());

		listPref = (ListPreference) findPreference("text_size");
		listPref.setSummary(listPref.getEntry());

		listPref = (ListPreference) findPreference("end_of_lines");
		listPref.setSummary(listPref.getEntry());

		listPref = (ListPreference) findPreference("encoding");
		listPref.setSummary(listPref.getEntry());

		listPref = (ListPreference) findPreference("max_recent_files");
		listPref.setSummary(listPref.getEntry());
	}

	protected AdvancedEditText mSampleTED;

}
