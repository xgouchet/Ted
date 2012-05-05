package fr.xgouchet.texteditor.undo;

import android.text.Editable;

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
		mStart = start + mSequence.length() - 1;
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#undo(android.text.Editable)
	 */
	public int undo(Editable s) {
		s.insert(getCaret() + 1, mSequence);
		return mStart + 1;
	}

	/**
	 * @see fr.xgouchet.texteditor.undo.TextChange#getCaret()
	 */
	public int getCaret() {
		if (mSequence.toString().contains(" "))
			return -1;
		if (mSequence.toString().contains("\n"))
			return -1;
		return mStart - mSequence.length();
	}

	/**
	 * Append a string after the current insertion
	 * 
	 * @param seq
	 *            the sequence to append
	 */
	public void append(CharSequence seq) {
		mSequence.insert(0, seq);
		// TODO insert reverse
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "-\"" + mSequence.toString().replaceAll("\n", "~") + "\" @"
				+ mStart;
	}

}
