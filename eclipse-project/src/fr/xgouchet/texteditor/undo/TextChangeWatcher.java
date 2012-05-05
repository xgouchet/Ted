package fr.xgouchet.texteditor.undo;

import java.util.Stack;

import android.text.Editable;
import android.util.Log;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.Settings;

public class TextChangeWatcher implements Constants {

	/**
	 * 
	 */
	public TextChangeWatcher() {
		mChanges = new Stack<TextChange>();
	}

	/**
	 * Undo the last operation
	 * 
	 * @param text
	 *            the text to undo on
	 * @return the caret position
	 */
	public int undo(Editable text) {
		pushCurrentChange();

		if (mChanges.size() == 0) {
			Log.i(TAG, "Nothing to undo");
			return -1;
		}

		TextChange change = mChanges.pop();
		if (change != null)
			return change.undo(text);
		else
			Log.w(TAG, "Null change ?!");

		return -1;
	}

	/**
	 * A change to the text {@linkplain s} will be made, where the
	 * {@linkplain count} characters starting at {@linkplain start} will be
	 * replaced by {@linkplain after} characters
	 * 
	 * @param s
	 *            the sequence being changed
	 * @param start
	 *            the start index
	 * @param count
	 *            the number of characters that will change
	 * @param after
	 *            the number of characters that will replace the old ones
	 * 
	 */
	public void beforeChange(CharSequence s, int start, int count, int after) {
		if (count == 0) {
			// ignore, will be processed after
		} else if (after == 0) {
			processDelete(s, start, count);
		} else {
			// replace is like a delete and an insert !
			processDelete(s, start, count);
		}
	}

	/**
	 * A change to the text {@linkplain s} has been made, where the
	 * {@linkplain count} characters starting at {@linkplain start} have
	 * replaced the substring of length {@linkplain before}
	 * 
	 * @param s
	 *            the sequence being changed
	 * @param start
	 *            the start index
	 * @param before
	 *            the number of character that were replaced
	 * @param count
	 *            the number of characters that will change
	 */
	public void afterChange(CharSequence s, int start, int before, int count) {
		if (before == 0) {
			processInsert(s, start, count);
		} else if (count == 0) {
			// ignore, should have been treated before
		} else {
			// replace is like a delete and an insert !
			processInsert(s, start, count);
		}
	}

	/**
	 * @param s
	 *            the sequence being modified
	 * @param start
	 *            the first character index
	 * @param count
	 *            the number of inserted text
	 */
	public void processInsert(CharSequence s, int start, int count) {
		CharSequence sub = s.subSequence(start, start + count);

		if (mCurrentChange instanceof TextChangeInsert)
			if (((TextChangeInsert) mCurrentChange).getCaret() == start)
				((TextChangeInsert) mCurrentChange).append(sub);
			else
				pushCurrentChange();
		else
			pushCurrentChange();

		if (mCurrentChange == null)
			mCurrentChange = new TextChangeInsert(sub, start);
	}

	/**
	 * @param s
	 *            the sequence being modified
	 * @param start
	 *            the first character index
	 * @param count
	 *            the number of inserted text
	 */
	public void processDelete(CharSequence s, int start, int count) {
		CharSequence sub = s.subSequence(start, start + count);
		if (mCurrentChange instanceof TextChangeDelete)
			if ((((TextChangeDelete) mCurrentChange).getCaret() == start)
					&& (count == 1))
				((TextChangeDelete) mCurrentChange).append(sub);
			else
				pushCurrentChange();
		else if (mCurrentChange != null)
			pushCurrentChange();

		if (mCurrentChange == null)
			mCurrentChange = new TextChangeDelete(sub, start);
	}

	/**
	 * Pushes the current change on top of the stack
	 */
	protected void pushCurrentChange() {
		if (mCurrentChange == null)
			return;

		mChanges.push(mCurrentChange);
		while (mChanges.size() > Settings.UNDO_MAX_STACK) {
			mChanges.remove(0);
		}
		mCurrentChange = null;
	}

	/**
	 * Prints the current stack
	 */
	protected void printStack() {
		Log.i(TAG, "STACK");
		for (TextChange change : mChanges) {
			Log.d(TAG, change.toString());
		}
	}

	protected TextChange mCurrentChange;
	protected final Stack<TextChange> mChanges;

}
