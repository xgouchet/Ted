package fr.xgouchet.texteditor;

import static fr.xgouchet.texteditor.ui.Toaster.showToast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import fr.xgouchet.texteditor.common.ComparatorFilesAlpha;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.FileUtils;
import fr.xgouchet.texteditor.ui.adapter.FileListAdapter;

public class TedOpenActivity extends Activity implements Constants,
		OnClickListener, OnItemClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup content view
		setContentView(R.layout.layout_open);

		// buttons
		findViewById(R.id.buttonCancel).setOnClickListener(this);

		// widgets
		mFoldersList = (ListView) findViewById(R.id.listItems);
		mFoldersList.setOnItemClickListener(this);
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
		}
	}

	/**
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		File file;

		file = mList.get(position);
		file = new File(FileUtils.getCanonizePath(file));

		// safe check : file exists
		if (!file.exists())
			return;

		if (file.isDirectory()) {
			fillFolderView(file);
		} else {
			if (setOpenResult(file))
				finish();
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
		setTitle(FileUtils.getCanonizePath(file));
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
	 * Set the result of this activity to open a file
	 * 
	 * @param file
	 *            the file to return
	 * @return if the result was set correctly
	 */
	protected boolean setOpenResult(File file) {
		Intent result;

		if (!file.canRead()) {
			showToast(this, R.string.toast_file_cant_read, true);
			return false;
		}

		result = new Intent();
		result.putExtra("path", file.getAbsolutePath());

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
}
