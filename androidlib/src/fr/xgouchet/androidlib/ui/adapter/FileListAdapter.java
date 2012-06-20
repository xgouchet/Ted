package fr.xgouchet.androidlib.ui.adapter;

import static fr.xgouchet.androidlib.data.FileUtils.DOWNLOAD_FOLDER;
import static fr.xgouchet.androidlib.data.FileUtils.STORAGE_PATH;
import static fr.xgouchet.androidlib.data.FileUtils.TRASH_FOLDER;
import static fr.xgouchet.androidlib.data.FileUtils.getMimeType;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.xgouchet.androidlib.R;
import fr.xgouchet.androidlib.data.FileUtils;

/**
 * A File List Adapter used to display folders and files
 * 
 */
public class FileListAdapter extends ArrayAdapter<File> {

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context
	 * @param objects
	 *            The objects to represent in the ListView.
	 * @param folder
	 *            the parent folder of the items presented
	 */
	public FileListAdapter(Context context, ArrayList<File> objects, File folder) {
		super(context, R.layout.item_file, objects);
		mFolder = folder;
	}

	/**
	 * @see ArrayAdapter#getView(int, View, ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		File file;
		View v;
		TextView textView;
		String text;
		int icon, style;

		// recycle the view
		v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_file, null);
		}

		// get the file infos
		file = getItem(position);
		style = Typeface.BOLD;
		if (file != null) {
			text = file.getName();
			if ((position == 0) && (!mFolder.getPath().equals("/"))) {
				icon = R.drawable.up;
				text = "..";
				style = Typeface.ITALIC;
			} else
				icon = getIconForFile(file);
		} else {
			text = "";
			icon = R.drawable.file_unknown;
		}

		// Setup name and icon
		textView = (TextView) v.findViewById(R.id.textFileName);
		if (textView != null) {
			textView.setText(text);
			textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
			textView.setTypeface(Typeface.DEFAULT, style);
		}

		return v;
	}

	public static int getIconForFile(File file) {
		int id;
		boolean locked, priv;
		String type, ext;

		priv = !file.canRead();
		locked = file.canRead() && !file.canWrite();

		if (file.getPath().equals(STORAGE_PATH)) // External storage
			id = R.drawable.sd_card;
		else if (file.getPath().toLowerCase().equals(DOWNLOAD_FOLDER))
			id = R.drawable.folder_downloads;
		else if (file.getPath().toLowerCase().equals(TRASH_FOLDER)) {
			if (file.list().length == 0)
				id = R.drawable.folder_trash_empty;
			else
				id = R.drawable.folder_trash_full;
		} else if (file.isDirectory()) {
			id = R.drawable.folder;
		} else { // f is file
			type = getMimeType(file);
			ext = FileUtils.getFileExtension(file);

			if (type == null)
				id = R.drawable.file;
			else if (type.startsWith("audio"))
				id = R.drawable.file_audio;
			else if (type.startsWith("video"))
				id = R.drawable.file_video;
			else if (type.startsWith("text"))
				id = R.drawable.file_text;
			else if (type.startsWith("application")) {
				if (type.equals("application/vnd.android.package-archive"))
					id = R.drawable.file_apk;
				else if (type.equals("application/x-compressed"))
					id = R.drawable.file_compressed;
				else if (type.equals("application/pdf"))
					id = R.drawable.file_pdf;
				else if (ext.endsWith("db"))
					id = R.drawable.file_db;
				else
					id = R.drawable.file_app;
			} else if (type.startsWith("image")) {
				id = R.drawable.file_image;
			} else
				id = R.drawable.file;
		}

		if (priv)
			return getPrivateDrawable(id);
		if (locked)
			return getLockedDrawable(id);
		return id;
	}

	protected static int getLockedDrawable(int id) {
		int res = id;
		if (id == R.drawable.file) {
			res = R.drawable.file_locked;
		} else if (id == R.drawable.folder) {
			res = R.drawable.folder_locked;
		} else if (id == R.drawable.file_text) {
			res = R.drawable.file_text_locked;
		} else if (id == R.drawable.file_audio) {
			res = R.drawable.file_audio_locked;
		} else if (id == R.drawable.file_video) {
			res = R.drawable.file_video_locked;
		} else if (id == R.drawable.file_image) {
			res = R.drawable.file_image_locked;
		} else if (id == R.drawable.file_apk) {
			res = R.drawable.file_apk_locked;
		} else if (id == R.drawable.file_app) {
			res = R.drawable.file_app_locked;
		} else if (id == R.drawable.file_pdf) {
			res = R.drawable.file_pdf_locked;
		} else if (id == R.drawable.file_db) {
			res = R.drawable.file_db_locked;
		} else if (id == R.drawable.file_compressed) {
			res = R.drawable.file_compressed_locked;
		}

		return res;
	}

	protected static int getPrivateDrawable(int id) {
		int res = id;
		if (id == R.drawable.file) {
			res = R.drawable.file_private;
		} else if (id == R.drawable.folder) {
			res = R.drawable.folder_private;
		} else if (id == R.drawable.file_text) {
			res = R.drawable.file_text_private;
		} else if (id == R.drawable.file_audio) {
			res = R.drawable.file_audio_private;
		} else if (id == R.drawable.file_video) {
			res = R.drawable.file_video_private;
		} else if (id == R.drawable.file_image) {
			res = R.drawable.file_image_private;
		} else if (id == R.drawable.file_apk) {
			res = R.drawable.file_apk_private;
		} else if (id == R.drawable.file_app) {
			res = R.drawable.file_app_private;
		} else if (id == R.drawable.file_pdf) {
			res = R.drawable.file_pdf_private;
		} else if (id == R.drawable.file_db) {
			res = R.drawable.file_db_private;
		} else if (id == R.drawable.file_compressed) {
			res = R.drawable.file_compressed_private;
		}
		return res;
	}

	protected File mFolder;
}
