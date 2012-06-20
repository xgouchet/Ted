package fr.xgouchet.androidlib.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Compare files by date (w/ folders listed first)
 * 
 * @author x.gouchet
 * 
 */
public class ComparatorFilesDate implements Comparator<File> {
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(File file1, File file2) {
		// parent folder always first
		if (file1.getName().equals(".."))
			return -1;
		// parent folder always first
		if (file2.getName().equals(".."))
			return 1;
		
		// sort folders first
		if ((file1.isDirectory()) && (!file2.isDirectory()))
			return -1;
		if ((!file1.isDirectory()) && (file2.isDirectory()))
			return 1;

		// both are files, or both are folders...
		// get modif date
		long modif1 = file1.lastModified();
		long modif2 = file2.lastModified();

		// same extension, we sort alphabetically
		if (modif1 == modif2)
			return file1.getName().toLowerCase().compareTo(
					file2.getName().toLowerCase());

		return (int) (modif1 - modif2);
	}
}
