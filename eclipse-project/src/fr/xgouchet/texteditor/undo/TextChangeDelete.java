package fr.xgouchet.texteditor.undo;

import android.text.Editable;
import android.util.Log;

public class TextChangeDelete implements TextChange {

	protected StringBuffer mSequence;
	protected int mStart;

	/**
	 * @param seq
	 *            the sequence being deleted
	 * @param start
	 *            the start index
	 */
	public TextChangeDelete(CharSequence seq, int start) {
		mSequence = new StringBuffer();
		mSequence.append(seq);
		mStart = start;
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#undo(android.text.Editable)
	 */
	public int undo(Editable s) {
		s.insert(mStart, mSequence);
		return mStart + mSequence.length();
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#getCaret()
	 */
	public int getCaret() {
		if (mSequence.toString().contains(" "))
			return -1;
		if (mSequence.toString().contains("\n"))
			return -1;
		return mStart;
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#append(java.lang.CharSequence)
	 */
	public void append(CharSequence seq) {
		mSequence.insert(0, seq);
		Log.d(TAG, mSequence.toString());
		mStart -= seq.length();
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#canMergeChangeAfter(java.lang.CharSequence,
	 *      int, int, int)
	 */
	public boolean canMergeChangeBefore(CharSequence s, int start, int count,
			int after) {
		CharSequence sub;
		if (mSequence.toString().contains(" "))
			return false;
		if (mSequence.toString().contains("\n"))
			return false;
		if ((count != 1) || (start + count != mStart))
			return false;

		sub = s.subSequence(start, start + count);
		append(sub);
		return true;
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#canMergeChangeBefore(java.lang.CharSequence,
	 *      int, int, int)
	 */
	public boolean canMergeChangeAfter(CharSequence s, int start, int before,
			int count) {
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "-\"" + mSequence.toString().replaceAll("\n", "~") + "\" @"
				+ mStart;
	}

}
