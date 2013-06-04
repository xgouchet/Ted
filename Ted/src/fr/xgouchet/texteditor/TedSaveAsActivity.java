package fr.xgouchet.texteditor;

import static fr.xgouchet.androidlib.ui.Toaster.showToast;

import java.io.File;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List; 

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import fr.xgouchet.androidlib.ui.activity.AbstractBrowsingActivity;
import fr.xgouchet.texteditor.common.Constants;

public class TedSaveAsActivity extends AbstractBrowsingActivity implements
		Constants, OnClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup content view
		setContentView(R.layout.layout_save_as);

		// buttons
		findViewById(R.id.buttonCancel).setOnClickListener(this);
		findViewById(R.id.buttonOk).setOnClickListener(this);
		((Button) findViewById(R.id.buttonOk)).setText(R.string.ui_save);

		// widgets
		mFileName = (EditText) findViewById(R.id.editFileName);

		// drawables
		mWriteable = getResources().getDrawable(R.drawable.folder_rw);
		mLocked = getResources().getDrawable(R.drawable.folder_r);
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
		case R.id.buttonOk:
			if (setSaveResult())
				finish();
		}
	}

	/**
	 * @see fr.xgouchet.androidlib.ui.activity.BrowserActivity#onFileClick(java.io.File)
	 */
	protected void onFileClick(File file) {
		if (file.canWrite())
			mFileName.setText(file.getName());
	}

	/**
	 * @see fr.xgouchet.androidlib.ui.activity.BrowserActivity#onFolderClick(java.io.File)
	 */
	protected boolean onFolderClick(File folder) {
		return true;
	}

	/**
	 * @see fr.xgouchet.androidlib.ui.activity.BrowsingActivity#onFolderViewFilled()
	 */
	protected void onFolderViewFilled() {

	}

	/**
	 * Sets the result data when the user presses save
	 * 
	 * @return if the result is OK (if not, it means the user must change its
	 *         selection / input)
	 */
	protected boolean setSaveResult() {
		Intent result;
		String fileName;

		if ((mCurrentFolder == null) || (!mCurrentFolder.exists())) {
			showToast(this, R.string.toast_folder_doesnt_exist, true);
			return false;
		}

		if (!mCurrentFolder.canWrite()) {
			showToast(this, R.string.toast_folder_cant_write, true);
			return false;
		}


		fileName = mFileName.getText().toString();
		if (fileName.length() == 0) {
			showToast(this, R.string.toast_filename_empty, true);
			return false;
		}
		
		//automatically add txt as extension if the application doesn't find a known extension
		String extension="";
	     int dotposition= fileName.lastIndexOf(".");
	     extension = fileName.substring(dotposition + 1, fileName.length());
	 
		List<String> list = Arrays.asList("doc", "docx", "log", "ascii", "txt", "html", "htm", "lst", "odt", "upd", "readme", "awp", "awt", "bean","php","ini","sh","js","db","conf","cfg");
		
		if (!(list.contains(extension)))
		{
		   fileName+=".txt";
		}
		
		//save result
		result = new Intent();
		result.putExtra("path", mCurrentFolder.getAbsolutePath()
				+ File.separator + fileName);
		
		setResult(RESULT_OK, result);
		
		//make file read-only if the radio button is checked
		File file = new File(mCurrentFolder.getAbsolutePath()
				+ File.separator + fileName);
		RadioButton readOnly;

		readOnly = (RadioButton) findViewById(R.id.readonly);
	
		if (readOnly.isChecked()) {
			file.setWritable(false);
		}
		return true;
	}


	/** the edit text input */
	protected EditText mFileName;

	/** */
	protected Drawable mWriteable;
	/** */
	protected Drawable mLocked;

}
