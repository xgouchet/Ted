package fr.xgouchet.androidlib.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Compare files by type (w/ folders listed first)
 * 
 * @author x.gouchet
 * 
 */
public class ComparatorFilesType implements Comparator<File> {
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(File file1, File file2) {
		// sort folders first
		if ((file1.isDirectory()) && (!file2.isDirectory()))
			return -1;
		if ((!file1.isDirectory()) && (file2.isDirectory()))
			return 1;

		// if both are folders
		if ((file1.isDirectory()) && (file2.isDirectory()))
			return file1.getName().toLowerCase().compareTo(
					file2.getName().toLowerCase());

		// both are files, we get the extension
		String ext1 = file1.getName().substring(
				file1.getName().lastIndexOf('.') + 1);
		String ext2 = file2.getName().substring(
				file2.getName().lastIndexOf('.') + 1);

		// same extension, we sort alphabetically
		if (ext1.toLowerCase().equals(ext2.toLowerCase()))
			return file1.getName().toLowerCase().compareTo(
					file2.getName().toLowerCase());

		return ext1.toLowerCase().compareTo(ext2.toLowerCase());
	}
}
