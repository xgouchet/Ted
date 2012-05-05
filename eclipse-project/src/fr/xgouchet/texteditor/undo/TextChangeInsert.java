package fr.xgouchet.texteditor.undo;

import android.text.Editable;

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
	 * @return the position of the caret at the end of the insertion
	 */
	public int getCaret() {
		if (mSequence.toString().contains(" "))
			return -1;
		if (mSequence.toString().contains("\n"))
			return -1;
		return mStart + mSequence.length();
	}

	/**
	 * Append a string after the current insertion
	 * 
	 * @param seq
	 *            the sequence to append
	 */
	public void append(CharSequence seq) {
		mSequence.append(seq);
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#undo(java.lang.String)
	 */
	public int undo(Editable s) {
		s.replace(mStart, mStart + mSequence.length(), "");
		return mStart;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "+\"" + mSequence.toString().replaceAll("\n", "~") + "\" @"
				+ mStart;
	}
}
