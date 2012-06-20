package fr.xgouchet.texteditor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.Log;
import fr.xgouchet.androidlib.ui.Toaster;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.Settings;
import fr.xgouchet.texteditor.ui.view.AdvancedEditText;

@SuppressWarnings("deprecation")
public class TedSettingsActivity extends PreferenceActivity implements Constants,
		OnSharedPreferenceChangeListener {

	/**
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		getPreferenceManager().setSharedPreferencesName(PREFERENCES_NAME);

		addPreferencesFromResource(R.xml.ted_prefs);
		setContentView(R.layout.layout_prefs);

		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

		mSampleTED = (AdvancedEditText) findViewById(R.id.sampleEditor);

		Settings.updateFromPreferences(getPreferenceManager().getSharedPreferences());
		mSampleTED.updateFromSettings();
		mSampleTED.setEnabled(false);

		mPreviousHP = Settings.USE_HOME_PAGE;

		updateSummaries();
	}

	/**
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences,
	 *      java.lang.String)
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Settings.updateFromPreferences(sharedPreferences);
		mSampleTED.updateFromSettings();
		updateSummaries();

		CheckBoxPreference checkBox = (CheckBoxPreference) findPreference(PREFERENCE_USE_HOME_PAGE);
		checkBox.setSummaryOn(Settings.HOME_PAGE_PATH);

		if (Settings.USE_HOME_PAGE && !mPreviousHP) {
			Intent setHomePage = new Intent(ACTION_OPEN);
			setHomePage.putExtra(EXTRA_REQUEST_CODE, REQUEST_HOME_PAGE);
			try {
				startActivityForResult(setHomePage, REQUEST_HOME_PAGE);
			} catch (ActivityNotFoundException e) {
				Toaster.showToast(this, R.string.toast_activity_open, true);
			}
		}

		mPreviousHP = Settings.USE_HOME_PAGE;
	}

	/**
	 * @see android.preference.PreferenceActivity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle extras;

		Log.d(TAG, "onActivityResult");

		if (resultCode == RESULT_CANCELED) {
			Log.d(TAG, "Result canceled");
			((CheckBoxPreference) findPreference(PREFERENCE_USE_HOME_PAGE)).setChecked(false);
			return;
		}

		if ((resultCode != RESULT_OK) || (data == null)) {
			Log.e(TAG, "Result error or null data! / " + resultCode);
			((CheckBoxPreference) findPreference(PREFERENCE_USE_HOME_PAGE)).setChecked(false);
			return;
		}

		extras = data.getExtras();
		if (extras == null) {
			Log.e(TAG, "No extra data ! ");
			return;
		}

		switch (requestCode) {
		case REQUEST_HOME_PAGE:
			Log.d(TAG, "Open : " + extras.getString("path"));
			Settings.HOME_PAGE_PATH = extras.getString("path");
			Settings.saveHomePage(getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE));
			updateSummaries();
			break;
		}
	}

	/**
	 * Updates the summaries for every list
	 */
	protected void updateSummaries() {
		ListPreference listPref;
		CheckBoxPreference cbPref;

		listPref = (ListPreference) findPreference(PREFERENCE_COLOR_THEME);
		listPref.setSummary(listPref.getEntry());

		listPref = (ListPreference) findPreference(PREFERENCE_TEXT_SIZE);
		listPref.setSummary(listPref.getEntry());

		listPref = (ListPreference) findPreference(PREFERENCE_END_OF_LINES);
		listPref.setSummary(listPref.getEntry());

		listPref = (ListPreference) findPreference(PREFERENCE_ENCODING);
		listPref.setSummary(listPref.getEntry());

		listPref = (ListPreference) findPreference(PREFERENCE_MAX_RECENTS);
		listPref.setSummary(listPref.getEntry());

		listPref = (ListPreference) findPreference(PREFERENCE_MAX_UNDO_STACK);
		listPref.setSummary(listPref.getEntry());

		cbPref = (CheckBoxPreference) findPreference(PREFERENCE_USE_HOME_PAGE);
		if (cbPref.isChecked()) {
			cbPref.setSummaryOn(Settings.HOME_PAGE_PATH);
		}
	}

	protected AdvancedEditText mSampleTED;
	protected boolean mPreviousHP;

}
