package fr.xgouchet.androidlib.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Compare files by size (w/ folders listed first)
 * 
 * @author x.gouchet
 * 
 */
public class ComparatorFilesSize implements Comparator<File> {
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

		// both are files, we get the sizes
		long size1 = file1.length();
		long size2 = file2.length();

		// same extension, we sort alphabetically
		if (size1 == size2)
			return file1.getName().toLowerCase().compareTo(
					file2.getName().toLowerCase());

		return (int) (size1 - size2);
	}
}
