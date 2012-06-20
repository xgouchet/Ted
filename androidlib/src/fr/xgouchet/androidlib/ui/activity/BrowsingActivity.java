package fr.xgouchet.androidlib.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import fr.xgouchet.androidlib.R;
import fr.xgouchet.androidlib.comparator.ComparatorFilesAlpha;
import fr.xgouchet.androidlib.data.FileUtils;
import fr.xgouchet.androidlib.ui.Toaster;
import fr.xgouchet.androidlib.ui.adapter.FileListAdapter;

public abstract class BrowsingActivity extends Activity implements
		OnItemClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mComparator = new ComparatorFilesAlpha();
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();

		// Setup the widget
		mFilesList = (ListView) findViewById(android.R.id.list);
		mFilesList.setOnItemClickListener(this);

		// initial folder
		File folder;
		if (mCurrentFolder != null)
			folder = mCurrentFolder;
		else if ((FileUtils.STORAGE.exists()) && (FileUtils.STORAGE.canRead()))
			folder = FileUtils.STORAGE;
		else
			folder = new File("/");

		fillFolderView(folder);
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
			if (onFolderClick(file))
				fillFolderView(file);
		} else {
			onFileClick(file);
		}
	}

	/**
	 * @param folder
	 *            the folder being clicked
	 * @return if the folder should be opened in the browsing list view
	 */
	protected abstract boolean onFolderClick(File folder);

	/**
	 * @param file
	 *            the file being clicked (it is not a folder)
	 */
	protected abstract void onFileClick(File file);

	/**
	 * Folder view has been filled
	 */
	protected abstract void onFolderViewFilled();

	/**
	 * Fills the files list with the specified folder
	 * 
	 * @param file
	 *            the file of the folder to display
	 */
	protected void fillFolderView(File file) {
		file = new File(FileUtils.getCanonizePath(file));

		if (!file.exists()) {
			Toaster.showToast(this, R.string.toast_folder_doesnt_exist, true);
			return;
		}

		if (!file.isDirectory()) {
			Toaster.showToast(this, R.string.toast_folder_not_folder, true);
			return;
		}

		if (!file.canRead()) {
			Toaster.showToast(this, R.string.toast_folder_cant_read, true);
			return;
		}

		listFiles(file);

		// create string list adapter
		mListAdapter = new FileListAdapter(this, mList, file);

		// set adpater
		mFilesList.setAdapter(mListAdapter);

		// update path
		mCurrentFolder = file;
		setTitle(FileUtils.getCanonizePath(file));

		onFolderViewFilled();
	}

	/**
	 * List the files in the given folder and store them in the list of files to
	 * display
	 * 
	 * @param folder
	 *            the folder to analyze
	 */
	protected void listFiles(File folder) {
		File f;
		String ext;
		boolean remove;

		// get files list as array list
		if ((folder == null) || (!folder.isDirectory())) {
			mList = new ArrayList<File>();
			return;
		}

		mList = new ArrayList<File>(Arrays.asList(folder.listFiles()));

		// filter files
		for (int i = (mList.size() - 1); i >= 0; i--) {
			f = mList.get(i);
			remove = false;

			// filter hidden files
			if (!mShowHiddenFiles)
				if (f.getName().startsWith("."))
					remove = true;

			// filter non folders
			if (mShowFoldersOnly)
				if (!f.isDirectory())
					remove = true;

			// filter non writeable folders
			if (mHideNonWriteableFiles)
				if (!f.canWrite())
					remove = true;

			// filter extension
			if (f.isFile()) {
				ext = FileUtils.getFileExtension(f);
				if (mExtensionsWhiteList != null)
					if (!mExtensionsWhiteList.contains(ext))
						remove = true;

				if (mExtensionsBlackList != null)
					if (mExtensionsBlackList.contains(ext))
						remove = true;
			}
			// remove
			if (remove)
				mList.remove(i);
		}

		// Sort list
		if (mComparator != null)
			Collections.sort(mList, mComparator);

		// Add parent folder
		if (!folder.getPath().equals("/")) {
			mList.add(0, folder.getParentFile());
		}
	}

	/** The list of files to display */
	protected ArrayList<File> mList;
	/** the dialog's list view */
	protected ListView mFilesList;
	/** The list adapter */
	protected ListAdapter mListAdapter;

	/** the current folder */
	protected File mCurrentFolder;

	/** the current file sort */
	protected Comparator<File> mComparator;

	protected boolean mShowFoldersOnly = false;
	protected boolean mShowHiddenFiles = true;
	protected boolean mHideNonWriteableFiles = false;
	protected ArrayList<String> mExtensionsWhiteList = null;
	protected ArrayList<String> mExtensionsBlackList = null;
}
