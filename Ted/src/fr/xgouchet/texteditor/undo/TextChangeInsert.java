package fr.xgouchet.texteditor.undo;

import fr.xgouchet.texteditor.BuildConfig;
import android.text.Editable;
import android.util.Log;

public class TextChangeInsert implements TextChange {

	protected StringBuffer mSequence;
	protected int mStart;

	/**
	 * @param seq
	 *            the initial sequence
	 * @param start
	 *            the start index for this sequence
	 * 
	 */
	public TextChangeInsert(CharSequence seq, int start) {
		mSequence = new StringBuffer();
		mSequence.append(seq);
		mStart = start;
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#getCaret()
	 */
	public int getCaret() {
		if (mSequence.toString().contains(" "))
			return -1;
		if (mSequence.toString().contains("\n"))
			return -1;
		return mStart + mSequence.length();
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#append(java.lang.CharSequence)
	 */
	public void append(CharSequence seq) {
		mSequence.append(seq);
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#canMergeChangeBefore(java.lang.CharSequence,
	 *      int, int, int)
	 */
	public boolean canMergeChangeBefore(CharSequence s, int start, int count, int after) {

		CharSequence sub;
		boolean append, replace;

		if (mSequence.toString().contains(" "))
			return false;
		if (mSequence.toString().contains("\n"))
			return false;

		sub = s.subSequence(start, start + count);
		append = (start == mStart + mSequence.length());
		replace = (start == mStart) && (after >= mSequence.length())
				&& (sub.toString().startsWith(mSequence.toString()));

		if (append) {
			// mSequence.append(sub);
			return true;
		}

		if (replace) {
			// mSequence = new StringBuffer();
			// mSequence.append(sub);
			return true;
		}
		return false;
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#canMergeChangeAfter(java.lang.CharSequence,
	 *      int, int, int)
	 */
	public boolean canMergeChangeAfter(CharSequence s, int start, int before, int count) {
		CharSequence sub;
		boolean append, replace;

		if (mSequence.toString().contains(" "))
			return false;
		if (mSequence.toString().contains("\n"))
			return false;

		sub = s.subSequence(start, start + count);
		append = (start == mStart + mSequence.length());
		replace = (start == mStart) && (count >= mSequence.length())
				&& (sub.toString().startsWith(mSequence.toString()));

		if (append) {
			mSequence.append(sub);
			return true;
		}

		if (replace) {
			mSequence = new StringBuffer();
			mSequence.append(sub);
			return true;
		}

		return false;
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#undo(java.lang.String)
	 */
	public int undo(Editable s) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, "Undo Insert : deleting " + mStart + " to " + (mStart + mSequence.length()));
		s.replace(mStart, mStart + mSequence.length(), "");
		return mStart;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "+\"" + mSequence.toString().replaceAll("\n", "~") + "\" @" + mStart;
	}

}
