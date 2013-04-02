package fr.xgouchet.texteditor;

import java.io.File;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import fr.xgouchet.androidlib.data.FileUtils;
import fr.xgouchet.androidlib.ui.Toaster;
import fr.xgouchet.androidlib.ui.activity.AbstractBrowsingActivity;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.WidgetPrefs;

public class TedWidgetConfigActivity extends AbstractBrowsingActivity implements Constants, OnClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_widget_config);

		setResult(RESULT_CANCELED, null);

		// buttons
		findViewById(R.id.buttonCancel).setOnClickListener(this);

		Toaster.showToast(this, R.string.toast_widget_select, false);
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();

		// get widget id
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonCancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
	}

	/**
	 * @see fr.xgouchet.androidlib.ui.activity.BrowserActivity#onFileClick(java.io.File)
	 */
	protected void onFileClick(File file) {
		if (file.canRead()) {
			mSelectedFile = file;
			setResultComplete();
		}
	}

	/**
	 * @see fr.xgouchet.androidlib.ui.activity.BrowserActivity#onFolderClick(java.io.File)
	 */
	protected boolean onFolderClick(File folder) {
		mSelectedFile = null;
		return true;
	}

	/**
	 * @see fr.xgouchet.androidlib.ui.activity.BrowsingActivity#onFolderViewFilled()
	 */
	protected void onFolderViewFilled() {

	}

	/**
	 * 
	 */
	protected void setResultComplete() {
		AppWidgetManager appWidgetManager;
		Intent resultValue;
		WidgetPrefs prefs;
		boolean readOnly;

		readOnly = ((CheckBox) findViewById(R.id.readOnly)).isChecked();

		prefs = new WidgetPrefs();
		prefs.mTargetPath = FileUtils.getCanonizePath(mSelectedFile);
		prefs.mReadOnly = readOnly;
		prefs.store(this, mAppWidgetId);

		appWidgetManager = AppWidgetManager.getInstance(this);
		TedAppWidgetProvider.updateWidget(this, appWidgetManager, mAppWidgetId);

		//
		resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

	/** the id of the widget being configured */
	protected int mAppWidgetId;

	/** the selected file */
	protected File mSelectedFile;
}
