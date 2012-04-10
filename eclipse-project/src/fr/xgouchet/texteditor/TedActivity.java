package fr.xgouchet.texteditor;

import static fr.xgouchet.texteditor.ui.Toaster.showToast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.FileUtils;
import fr.xgouchet.texteditor.common.RecentFiles;
import fr.xgouchet.texteditor.common.Settings;
import fr.xgouchet.texteditor.ui.dialogs.DialogSave;
import fr.xgouchet.texteditor.ui.view.AdvancedEditText;

public class TedActivity extends Activity implements Constants, TextWatcher,
		OnClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setContentView(R.layout.layout_editor);

		Settings.updateFromPreferences(getSharedPreferences(
				"fr.xgouchet.texteditor", MODE_PRIVATE));

		// editor
		mEditor = (AdvancedEditText) findViewById(R.id.editor);
		// mEditor.setOnKeyListener(this);
		mEditor.addTextChangedListener(this);
		mEditor.updateFromSettings();

		// search
		mSearchLayout = findViewById(R.id.searchLayout);
		mSearchInput = (EditText) findViewById(R.id.textSearch);
		findViewById(R.id.buttonSearchClose).setOnClickListener(this);
		findViewById(R.id.buttonSearchNext).setOnClickListener(this);
		findViewById(R.id.buttonSearchPrev).setOnClickListener(this);
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");

		updateTitle();
		readIntent();
		mEditor.updateFromSettings();
	}

	/**
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle extras;

		Log.d(TAG, "onActivityResult");

		if (resultCode == RESULT_CANCELED) {
			Log.d(TAG, "Result canceled");
			return;
		}

		if ((resultCode != RESULT_OK) || (data == null)) {
			Log.e(TAG, "Result error or null data! / " + resultCode);
			return;
		}

		extras = data.getExtras();
		if (extras == null) {
			Log.e(TAG, "No extra data ! ");
			return;
		}

		switch (requestCode) {
		case REQUEST_SAVE_AS:
			Log.d(TAG, "Save as : " + extras.getString("path"));
			doSaveFile(extras.getString("path"));
			break;
		case REQUEST_OPEN:
			Log.d(TAG, "Open : " + extras.getString("path"));
			doOpenFile(new File(extras.getString("path")));
			break;
		}
	}

	/**
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d(TAG, "onConfigurationChanged");
	}

	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_ID_NEW, Menu.NONE, R.string.menu_new).setIcon(
				R.drawable.file_new);
		menu.add(0, MENU_ID_OPEN, Menu.NONE, R.string.menu_open).setIcon(
				R.drawable.file_open);
		menu.add(1, MENU_ID_SAVE, Menu.NONE, R.string.menu_save).setIcon(
				R.drawable.file_save);
		menu.add(3, MENU_ID_SEARCH, Menu.NONE, R.string.menu_search).setIcon(
				R.drawable.search);
		menu.add(0, MENU_ID_OPEN_RECENT, Menu.NONE, R.string.menu_open_recent)
				.setIcon(R.drawable.recent);
		menu.add(1, MENU_ID_SAVE_AS, Menu.NONE, R.string.menu_save_as).setIcon(
				R.drawable.file_save_as);
		menu.add(2, MENU_ID_SETTINGS, Menu.NONE, R.string.menu_settings)
				.setIcon(R.drawable.settings);
		menu.add(2, MENU_ID_ABOUT, Menu.NONE, R.string.menu_about).setIcon(
				R.drawable.unknown);

		return true;
	}

	/**
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		menu.findItem(MENU_ID_SAVE).setEnabled(!mReadOnly);
		return true;
	}

	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_NEW:
			newContent();
			return true;
		case MENU_ID_SAVE:
			saveContent();
			break;
		case MENU_ID_SAVE_AS:
			saveContentAs();
			break;
		case MENU_ID_OPEN:
			openFile();
			break;
		case MENU_ID_OPEN_RECENT:
			openRecentFile();
			break;
		case MENU_ID_SEARCH:
			search();
			break;
		case MENU_ID_SETTINGS:
			settingsActivity();
			return true;
		case MENU_ID_ABOUT:
			aboutActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
	 *      int, int, int)
	 */
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	/**
	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int,
	 *      int, int)
	 */
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	/**
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	public void afterTextChanged(Editable s) {
		if (!mDirty) {
			mDirty = true;
			updateTitle();
		}
	}

	/**
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyUp (" + keyCode + ")");

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (mSearchLayout.getVisibility() == View.GONE)
				quit();
			else
				search();
			return true;
		case KeyEvent.KEYCODE_SEARCH:
			search();
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	/**
	 * @see OnClickListener#onClick(View)
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSearchClose:
			search();
			break;
		case R.id.buttonSearchNext:
			searchNext();
			break;
		case R.id.buttonSearchPrev:
			searchPrevious();
			break;
		}
	}

	/**
	 * Read the intent used to start this activity (open the text file) as well
	 * as the non configuration instance if activity is started after a screen
	 * rotate
	 */
	protected void readIntent() {
		Intent intent;
		String action;

		intent = getIntent();
		if (intent == null) {
			Log.d(TAG, "No intent found, ignoring");
			return;
		}

		action = intent.getAction();
		if (action == null) {
			Log.d(TAG, "Intent w/o action, ignoring");
			return;
		}

		if ((action.equals(Intent.ACTION_VIEW))
				|| (action.equals(Intent.ACTION_EDIT))) {
			Log.d(TAG, "Intent w/ VIEW / EDIT action, trying to read file");
			try {
				doOpenFile(new File(new URI(intent.getData().toString())));
			} catch (URISyntaxException e) {
				showToast(this, R.string.toast_intent_invalid_uri, true);
			} catch (IllegalArgumentException e) {
				showToast(this, R.string.toast_intent_illegal, true);
			}
		}
	}

	/**
	 * Clears the content of the editor. Assumes that user was prompted and
	 * previous data was saved
	 */
	protected void doClearContents() {
		mEditor.setText("");
		mCurrentFilePath = null;
		mCurrentFileName = null;
		Settings.END_OF_LINE = Settings.DEFAULT_END_OF_LINE;
		mDirty = false;
		mReadOnly = false;
		updateTitle();
	}

	/**
	 * Opens the given file and replace the editors content with the file.
	 * Assumes that user was prompted and previous data was saved
	 * 
	 * @param file
	 *            the file to load
	 */
	protected void doOpenFile(File file) {
		String text;

		if (file == null)
			return;

		Log.i(TAG, "Openning file " + file.getName());

		try {
			text = FileUtils.readExternal(file);
			if (text != null) {
				mEditor.setText(text);
				mCurrentFilePath = FileUtils.getCanonizePath(file);
				mCurrentFileName = file.getName();
				RecentFiles.updateRecentList(mCurrentFilePath);
				RecentFiles.saveRecentList(getSharedPreferences(
						PREFERENCES_NAME, MODE_PRIVATE));
				mDirty = false;
				if (file.canWrite()) {
					mReadOnly = false;
					mEditor.setEnabled(true);
				} else {
					mReadOnly = true;
					mEditor.setEnabled(false);
				}

				updateTitle();
			} else {
				showToast(this, R.string.toast_open_error, true);
			}
		} catch (OutOfMemoryError e) {
			showToast(this, R.string.toast_memory_open, true);
		}
	}

	/**
	 * Saves the text editor's content into a file at the given path. If an
	 * after save {@link Runnable} exists, run it
	 * 
	 * @param path
	 *            the path to the file (must be a valid path and not null)
	 */
	protected void doSaveFile(String path) {
		String content;

		if (path == null) {
			showToast(this, R.string.toast_save_null, true);
			return;
		}

		content = mEditor.getText().toString();

		if (!FileUtils.writeExternal(path + ".tmp", content)) {
			showToast(this, R.string.toast_save_temp, true);
			return;
		}

		if (!FileUtils.deleteExternal(path)) {
			showToast(this, R.string.toast_save_delete, true);
			return;
		}

		if (!FileUtils.renameExternal(path + ".tmp", path)) {
			showToast(this, R.string.toast_save_rename, true);
			return;
		}

		mCurrentFilePath = FileUtils.getCanonizePath(new File(path));
		mCurrentFileName = (new File(path)).getName();
		RecentFiles.updateRecentList(path);
		RecentFiles.saveRecentList(getSharedPreferences(PREFERENCES_NAME,
				MODE_PRIVATE));
		mDirty = false;
		updateTitle();
		showToast(this, R.string.toast_save_success, false);

		runAfterSave();
	}

	/**
	 * Prompt the user to save the current file before doing something else
	 */
	protected void promptSaveDirty() {
		final DialogSave dlg;

		if (!mDirty) {
			runAfterSave();
			return;
		}

		dlg = new DialogSave(this);
		dlg.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				Log.d(TAG, "onDismiss ");

				if (dlg.isCanceled())
					return;

				if (dlg.isSaved())
					saveContent();
				else
					runAfterSave();
			}
		});

		dlg.show();
	}

	/**
	 * 
	 */
	protected void newContent() {
		mAfterSave = new Runnable() {
			public void run() {
				doClearContents();
			}
		};

		promptSaveDirty();
	}

	/**
	 * Runs the after save to complete
	 */
	protected void runAfterSave() {
		if (mAfterSave == null) {
			Log.d(TAG, "No After shave, ignoring...");
			return;
		}

		mAfterSave.run();

		mAfterSave = null;
	}

	/**
	 * Starts an activity to choose a file to open
	 */
	protected void openFile() {
		Log.d(TAG, "openFile");

		mAfterSave = new Runnable() {
			public void run() {
				Intent open;

				open = new Intent("fr.xgouchet.texteditor.ACTION_TED_OPEN");
				try {
					startActivityForResult(open, REQUEST_OPEN);
				} catch (ActivityNotFoundException e) {
					showToast(TedActivity.this, R.string.toast_activity_open,
							true);
				}
			}
		};

		promptSaveDirty();
	}

	/**
	 * Open the recent files activity to open
	 */
	protected void openRecentFile() {
		Log.d(TAG, "openRecentFile");

		if (RecentFiles.getRecentFiles().size() == 0) {
			showToast(this, R.string.toast_no_recent_files, true);
			return;
		}

		mAfterSave = new Runnable() {
			public void run() {
				Intent open;

				open = new Intent(
						"fr.xgouchet.texteditor.ACTION_TED_OPEN_RECENT");
				try {
					startActivityForResult(open, REQUEST_OPEN);
				} catch (ActivityNotFoundException e) {
					showToast(TedActivity.this,
							R.string.toast_activity_open_recent, true);
				}
			}
		};

		promptSaveDirty();
	}

	/**
	 * Quit the app (user pressed back)
	 */
	protected void quit() {
		mAfterSave = new Runnable() {
			public void run() {
				finish();
			}
		};

		promptSaveDirty();
	}

	/**
	 * General save command : check if a path exist for the current content,
	 * then save it , else invoke the {@link TedActivity#saveContentAs()} method
	 */
	protected void saveContent() {
		if ((mCurrentFilePath == null) || (mCurrentFilePath.length() == 0))
			saveContentAs();
		else
			doSaveFile(mCurrentFilePath);
	}

	/**
	 * General Save as command : prompt the user for a location and file name,
	 * then save the editor'd content
	 */
	protected void saveContentAs() {
		Log.d(TAG, "saveContentAs");
		Intent saveAs;

		saveAs = new Intent("fr.xgouchet.texteditor.ACTION_TED_SAVE_AS");
		try {
			startActivityForResult(saveAs, REQUEST_SAVE_AS);
		} catch (ActivityNotFoundException e) {
			showToast(this, R.string.toast_activity_save_as, true);
		}
	}

	/**
	 * Opens / close the search interface
	 */
	protected void search() {
		Log.d(TAG, "search");
		switch (mSearchLayout.getVisibility()) {
		case View.GONE:
			mSearchLayout.setVisibility(View.VISIBLE);
			break;
		case View.VISIBLE:
		default:
			mSearchLayout.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * Uses the user input to search a file
	 */
	protected void searchNext() {
		String search, text;
		int selection, next;

		search = mSearchInput.getText().toString();
		text = mEditor.getText().toString();
		selection = mEditor.getSelectionEnd();
		next = text.indexOf(search, selection);

		if (search.length() == 0) {
			showToast(this, R.string.toast_search_no_input, false);
			return;
		}

		if (!Settings.SEARCHMATCHCASE) {
			search = search.toLowerCase();
			text = text.toLowerCase();
		}

		if (next > -1) {
			mEditor.setSelection(next, next + search.length());
			if (!mEditor.isFocused())
				mEditor.requestFocus();
		} else {
			if (Settings.SEARCHWRAP) {
				next = text.indexOf(search);
				if (next > -1) {
					mEditor.setSelection(next, next + search.length());
					if (!mEditor.isFocused())
						mEditor.requestFocus();
				} else {
					showToast(this, R.string.toast_search_not_found, false);
				}
			} else {
				showToast(this, R.string.toast_search_eof, false);
			}
		}
	}

	/**
	 * Uses the user input to search a file
	 */
	protected void searchPrevious() {
		String search, text;
		int selection, next;

		search = mSearchInput.getText().toString();
		text = mEditor.getText().toString();
		selection = mEditor.getSelectionStart() - 1;
		next = text.lastIndexOf(search, selection);

		if (search.length() == 0) {
			showToast(this, R.string.toast_search_no_input, false);
			return;
		}

		if (!Settings.SEARCHMATCHCASE) {
			search = search.toLowerCase();
			text = text.toLowerCase();
		}

		if (next > -1) {
			mEditor.setSelection(next, next + search.length());
			if (!mEditor.isFocused())
				mEditor.requestFocus();
		} else {
			if (Settings.SEARCHWRAP) {
				next = text.lastIndexOf(search);
				if (next > -1) {
					mEditor.setSelection(next, next + search.length());
					if (!mEditor.isFocused())
						mEditor.requestFocus();
				} else {
					showToast(this, R.string.toast_search_not_found, false);
				}
			} else {
				showToast(this, R.string.toast_search_eof, false);
			}
		}
	}

	/**
	 * Opens the about activity
	 */
	protected void aboutActivity() {
		Intent about = new Intent();
		about.setAction("fr.xgouchet.texteditor.ACTION_TED_ABOUT");
		try {
			startActivity(about);
		} catch (ActivityNotFoundException e) {
			showToast(this, R.string.toast_activity_about, true);
		}
	}

	/**
	 * Opens the settings activity
	 */
	protected void settingsActivity() {
		Intent settings = new Intent();
		settings.setAction("fr.xgouchet.texteditor.ACTION_TED_SETTINGS");
		try {
			startActivity(settings);
		} catch (ActivityNotFoundException e) {
			showToast(this, R.string.toast_activity_settings, true);
		}
	}

	/**
	 * Update the window title
	 */
	protected void updateTitle() {
		String title;
		String name;

		name = "?";
		if ((mCurrentFileName != null) && (mCurrentFileName.length() > 0))
			name = mCurrentFileName;

		if (mReadOnly)
			title = getString(R.string.title_editor_readonly, name);
		else if (mDirty)
			title = getString(R.string.title_editor_dirty, name);
		else
			title = getString(R.string.title_editor, name);

		setTitle(title);
	}

	/** the text editor */
	protected AdvancedEditText mEditor;
	/** the path of the file currently opened */
	protected String mCurrentFilePath;
	/** the name of the file currently opened */
	protected String mCurrentFileName;
	/** the runable to run after a save */
	protected Runnable mAfterSave; // Mennen ? Axe ?

	/** is dirty ? */
	protected boolean mDirty;
	/** is read only */
	protected boolean mReadOnly;

	/** the search layout root */
	protected View mSearchLayout;
	/** the search input */
	protected EditText mSearchInput;
}