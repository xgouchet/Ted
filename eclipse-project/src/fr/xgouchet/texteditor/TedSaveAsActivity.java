package fr.xgouchet.texteditor;

import static fr.xgouchet.texteditor.ui.Toaster.showToast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import fr.xgouchet.texteditor.common.ComparatorFilesAlpha;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.FileUtils;
import fr.xgouchet.texteditor.ui.adapter.FileListAdapter;

public class TedSaveAsActivity extends Activity implements Constants,
		OnClickListener, OnItemClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup content view
		setContentView(R.layout.layout_save_as);

		// buttons
		findViewById(R.id.buttonCancel).setOnClickListener(this);
		findViewById(R.id.buttonSave).setOnClickListener(this);

		// widgets
		mFileName = (EditText) findViewById(R.id.editFileName);
		mFoldersList = (ListView) findViewById(R.id.listItems);
		mFoldersList.setOnItemClickListener(this);

		// drawables
		mWriteable = getResources().getDrawable(R.drawable.folder_rw);
		mLocked = getResources().getDrawable(R.drawable.folder_r);
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();

		// initial folder
		File folder;
		if ((STORAGE.exists()) && (STORAGE.canRead()))
			folder = STORAGE;
		else
			folder = new File("/");

		fillFolderView(folder);
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
		case R.id.buttonSave:
			if (setSaveResult())
				finish();
		}
	}

	/**
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		File file;

		file = mList.get(position);

		// safe check : null pointer exception
		if (file == null)
			return;

		// safe check : file exists
		if (!file.exists())
			return;

		if (file.isDirectory()) {
			fillFolderView(file);
		} else {
			if (file.canWrite())
				mFileName.setText(file.getName());
		}
	}

	/**
	 * Fills the files list with the specified folder
	 * 
	 * @param file
	 *            the file of the folder to display
	 */
	protected void fillFolderView(File file) {
		file = new File(FileUtils.getCanonizePath(file));

		if (!file.exists()) {
			showToast(this, R.string.toast_folder_doesnt_exist, true);
			return;
		}

		if (!file.isDirectory()) {
			showToast(this, R.string.toast_folder_not_folder, true);
			return;
		}

		if (!file.canRead()) {
			showToast(this, R.string.toast_folder_cant_read, true);
			return;
		}

		listFiles(file);

		// create string list adapter
		mListAdapter = new FileListAdapter(this, mList);

		// set adpater
		mFoldersList.setAdapter(mListAdapter);

		// update path
		mCurrentFolder = file;

		if (mCurrentFolder.canWrite())
			mFileName.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.folder_rw, 0, 0, 0);
		else
			mFileName.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.folder_r, 0, 0, 0);
	}

	/**
	 * List the files in the given folder and store them in the list of files to
	 * display
	 * 
	 * @param folder
	 *            the folder to analyze
	 */
	protected void listFiles(File folder) {
		// get files list as array list
		if ((folder == null) || (!folder.isDirectory())) {
			mList = new ArrayList<File>();
			return;
		}

		mList = new ArrayList<File>(Arrays.asList(folder.listFiles()));

		// Sort list
		Collections.sort(mList, new ComparatorFilesAlpha());

		// Add parent folder
		if (folder.getParentFile() != null) {
			mList.add(0, folder.getParentFile());
		} else {
			mList.add(0, folder);
		}
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

		result = new Intent();
		result.putExtra("path", mCurrentFolder.getAbsolutePath()
				+ File.separator + fileName);

		setResult(RESULT_OK, result);
		return true;
	}

	/** The list of files to display */
	protected ArrayList<File> mList;
	/** the dialog's list view */
	protected ListView mFoldersList;
	/** The list adapter */
	protected ListAdapter mListAdapter;

	/** the current folder */
	protected File mCurrentFolder;

	/** the edit text input */
	protected EditText mFileName;

	/** */
	protected Drawable mWriteable;
	/** */
	protected Drawable mLocked;

}
