package fr.xgouchet.androidlib.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class FileUtils {
	/** File of the external storage data */
	public static final File STORAGE = Environment.getExternalStorageDirectory();
	/** Path to the external storage data */
	public static final String STORAGE_PATH = STORAGE.getAbsolutePath();

	/** default android Download folder */
	public static final String DOWNLOAD_FOLDER = (STORAGE.getAbsolutePath() + File.separator + "Download")
			.toLowerCase();

	/** default android Trash folder */
	public static final String TRASH_FOLDER = (STORAGE.getAbsolutePath() + File.separator + "LOST.DIR")
			.toLowerCase();

	/**
	 * 
	 * @param file
	 *            the file
	 * @return the file extension
	 */
	public static String getFileExtension(File file) {
		String ext, name;
		int index;

		ext = "";
		name = file.getName();
		index = name.lastIndexOf(".");
		if (index != -1)
			ext = name.substring(index + 1);
		return ext;
	}

	/**
	 * @param file
	 *            the file name
	 * @return the Mime Type
	 */
	public static String getMimeType(File file) {

		String type;
		String ext;

		ext = getFileExtension(file);
		type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
		if (type == null)
			type = MimeTypeMapEnhanced.getMimeTypeFromExtension(ext.toLowerCase());

		if (type == null)
			type = "?/?";

		return type;
	}

	/**
	 * @param f
	 *            a file
	 * @return the canonical path of the file if possible, or the uncanonized
	 *         path
	 */
	public static String getCanonizePath(File f) {
		String path;
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			path = f.getPath();
			Log.w("AndroidLib", "Error while canonizing file path, using raw path instead : \""
					+ path + "\"");
		}
		return path;
	}

	/**
	 * Creates a new folder in the given parent folder
	 * 
	 * @param parent
	 *            the parent file for the folder
	 * @param name
	 *            the name of the folder to create
	 * @return if the folder was created
	 */
	public static boolean createFolder(File parent, String name) {
		File folder;
		boolean created;

		created = false;
		folder = new File(parent, name);
		if (parent.canWrite())
			if (folder.mkdirs())
				created = true;

		return created;
	}

	/**
	 * Creates a new file in the given parent folder
	 * 
	 * @param parent
	 *            the parent file for the folder
	 * @param name
	 *            the name of the file to create
	 * @return if the file was created
	 */
	public static boolean createFile(File parent, String name) {
		File file;
		boolean created;

		created = false;
		file = new File(parent, name);
		if (parent.canWrite())
			try {
				file.createNewFile();
				created = true;
			} catch (IOException e) {
				e.printStackTrace();
			}

		return created;
	}

	/**
	 * Delete the given file/folder
	 * 
	 * @param file
	 *            the file/folder to delete
	 * @return if the file/folder was deleted successfully
	 */
	public static boolean deleteItem(File file) {

		if (!file.exists())
			return true;

		if (!file.canWrite())
			return false;

		return file.delete();
	}

	/**
	 * Delete the given folder with all its content
	 * 
	 * @param folder
	 *            the folder to delete
	 * @return if the folder was deleted successfully
	 */
	public static boolean deleteRecursiveFolder(File folder) {
		if (folder == null)
			return false;

		File[] files = folder.listFiles();
		if (files == null) // usually if the folder is a file or has no children
			return folder.delete();
		else {
			boolean ok = true;
			for (File child : files) {
				if (child.isDirectory())
					ok = ok && deleteRecursiveFolder(child);
				else
					ok = ok && child.delete();
			}
			if (ok)
				return folder.delete();
			else
				return false;
		}
	}

	/**
	 * Delete the file/folder at the given path
	 * 
	 * @param path
	 *            the path of the file/folder to delete
	 * @return if the file/folder was deleted successfully
	 */
	public static boolean deleteItem(String path) {
		File file;

		file = new File(path);

		if (!file.exists())
			return true;

		if (!file.canWrite())
			return false;

		return file.delete();
	}

	/**
	 * 
	 * @param src
	 *            the source file
	 * @param dst
	 *            the destination file
	 * @return if the copy was successfull
	 */
	public static boolean copyFile(File src, File dst) {

		FileChannel inChannel = null;
		FileChannel outChannel = null;

		boolean complete = true;

		try {
			inChannel = new FileInputStream(src).getChannel();
			outChannel = new FileOutputStream(dst).getChannel();
		} catch (FileNotFoundException e) {
			Log.w("AndroidLib", "File not found or no R/W permission", e);
			complete = false;
		}

		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (Exception e) {
			Log.w("AndroidLib", "Error during copy", e);
			complete = false;
		}

		try {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		} catch (IOException e) {
			Log.w("AndroidLib", "Error when closing files", e);
			complete = false;
		}

		return complete;
	}

	/**
	 * Rename a file
	 * 
	 * @param file
	 *            the file/folder to rename
	 * @param newName
	 *            the new name to the file
	 * @return if the rename was succesfull
	 */
	public static boolean renameItem(File file, String newName) {
		File newFile;

		if ((!file.exists()) || (!file.canWrite()))
			return false;

		newFile = new File(file.getParentFile(), newName);

		return file.renameTo(newFile);
	}

	/**
	 * Rename a file
	 * 
	 * @param oldPath
	 *            the path of the file/folder to rename
	 * @param newPath
	 *            the new path to the file/folder
	 * @return if the rename was succesfull
	 */
	public static boolean renameItem(String oldPath, String newPath) {
		File file, newFile;

		file = new File(oldPath);

		if ((!file.exists()) || (!file.canWrite()))
			return false;

		newFile = new File(newPath);
		return file.renameTo(newFile);
	}
}
