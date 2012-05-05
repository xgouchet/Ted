package fr.xgouchet.texteditor;
/*  
 * Box One Cloud Integration example
 *  This requires the OneCloupAppToApp SDK which can be found at:
 *  https://github.com/box/box-android-sdk/tree/experimental
 *  check that out and add the library to your project
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.box.onecloud.android.Crypto.CryptoException;
import com.box.onecloud.android.OneCloudFile;

import fr.xgouchet.texteditor.box.TedBoxReceiver;
import fr.xgouchet.texteditor.common.Settings;

/** 
 *   Replace some Ted Activity functions by the equivalent operations
 *   for reading and decrypting a File from Box instead of reading from SD card
 *   and encrypting and sending the file to Box for upload instead of
 *   writing the file to SD card.
 */

public class TedBoxActivity extends TedActivity {	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
    	mBoxFileNameChangedReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (intent.getAction().equals("com.box.onecloud.android.RENAME")) {
        			// .d(TAG, "mFileNameCHangedReceiver " + intent.getStringExtra("BoxFileName"));	
        			mDirty = false;
                	mCurrentFileName = intent.getStringExtra("BoxFileName");
                	updateTitle();

                }
                else if (intent.getAction().equals("Renamed")) {
                    // onRenamedFile(intent);
                }
            }
        };
        Context myContext = getApplicationContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.box.onecloud.android.RENAME");
        myContext.registerReceiver(mBoxFileNameChangedReceiver, filter);
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.clear();

		menu.add(0, MENU_ID_NEW, Menu.NONE, R.string.menu_new).setIcon(
				R.drawable.file_new);
		menu.add(0, MENU_ID_OPEN, Menu.NONE, R.string.menu_open).setIcon(
				R.drawable.file_open);
		menu.add(1, MENU_ID_SAVE, Menu.NONE, R.string.menu_save).setIcon(
				R.drawable.file_save);
		menu.add(3, MENU_ID_SEARCH, Menu.NONE, R.string.menu_search).setIcon(
				R.drawable.search);
		menu.add(0, MENU_ID_OPEN_RECENT, Menu.NONE, R.string.menu_open_recent)
				.setIcon(R.drawable.recent);
		menu.add(2, MENU_ID_SETTINGS, Menu.NONE, R.string.menu_settings)
				.setIcon(R.drawable.settings);
		menu.add(2, MENU_ID_ABOUT, Menu.NONE, R.string.menu_about).setIcon(
				R.drawable.unknown);

		return true;
	}
	
    public void onDestroy() {
        super.onDestroy();
        if ((mBoxFileNameChangedReceiver != null)) {        	
        	getApplicationContext().unregisterReceiver(mBoxFileNameChangedReceiver);
         }       
    }

	@Override
	protected void saveContent() {
		// Log.d(TAG, "BoxMode saveContent");
		doSaveBoxFile(mCurrentFilePath, mOneCloudFile);
	}

	@Override
	protected void saveContentAs() {
		// TODO Auto-generated method stub
		// Log.d(TAG, "saveContentAs BoxMode true");
		this.getIntent().putExtra("SaveAs", true);
		
			doSaveBoxFile("", mOneCloudFile);
		
	}

	@Override
	protected void readIntent() {
		Intent intent = getIntent();
		mOneCloudFile = (OneCloudFile) intent.getSerializableExtra("BoxOneCloudFile");
		// Log.d(TAG, "readIntent: mOneCloudFile Uri " +  mOneCloudFile.getUri().getPath());			
		// Log.d(TAG, "readIntent: mOneCloudFile UriOut " +  mOneCloudFile.getUriOut().getPath());			
    	mEditor.setText(intent.getStringExtra("BoxContent")); 
		// Log.d(TAG, "readIntent: mOneCloudFile BoxContentLenght =  " +  mEditor.length());			
    	mDirty = false;
    	// allow content in Intent to be garbage collected
    	intent.putExtra("BoxContent", "");
    	mCurrentFileName = intent.getStringExtra("BoxFileName");
    	updateTitle();
	}
	
	@Override
	protected void updateTitle() {
		String title;
		String name;
		name = "?";
		if ((mCurrentFileName != null) && (mCurrentFileName.length() > 0))
			name = mCurrentFileName;
		if (mReadOnly)
			title = getString(R.string.title_box_editor_readonly, name);
		else {
			if (mDirty)
				title = getString(R.string.title_box_editor_dirty, name);
			else
				title = getString(R.string.title_box_editor, name);	
		}
		setTitle(title);
	}

	/**	
     *  Saves the text editor's content into a file at the given path. If an
	 * after save {@link Runnable} exists, run it
	 * 
	 * @param path
	 *            the path to the file (must be a valid path and not null)
	 *            
	 * @return The file object of the saved file or null if error occurred
	 */
	protected void doSaveBoxFile(String path, OneCloudFile ocf) {
		Log.d(TAG, "doSaveBoxFile: mOneCloudFile Uri " +  mOneCloudFile.getUri().getPath());			
	    
	    long boxToken = this.getIntent().getLongExtra("BoxToken", -1L);
	    boolean saveas = this.getIntent().getBooleanExtra("SaveAs", false);
		if (!saveas) {  // Save to existing file in Box
			writeBoxExternal (ocf, mEditor.getText().toString());
			Log.d(TAG, "doSaveBoxFile: uploadNewVersion ");						
			TedBoxReceiver.uploadNewVersion(getApplicationContext(), boxToken, ocf);			
		} else  { 
			// Save As:  Create a new file in Box (in the same directory)
			Log.d(TAG, "doSaveBoxFile: mOneCloudFile Uri " +  mOneCloudFile.getUri().getPath());
			writeBoxExternal (ocf, mEditor.getText().toString());
			TedBoxReceiver.uploadNewFile(getApplicationContext(), boxToken, ocf, this.getIntent().getStringExtra("BoxFileName"));
			this.getIntent().putExtra("SaveAs", false);
			
		}
		mDirty = false;
		updateTitle();
		return;
	}
		 
	 /**
	 * writeBoxExternal Encrypt a string to a OneCloudBoxFile
	 * 
	 * @param ocf
	 * @param text
	 * @return boolean  operation did succeed
	 */
	private boolean writeBoxExternal(OneCloudFile ocf, String text) {
		// File file = new File(path);
		// Log.e(TAG, "writeBoxExternal: " + ocf.getUriOut().toString() + " length of text is: "  + text.length()) ;
		OutputStreamWriter writer;
		BufferedWriter out;
		String eol_text = text;
		try {
			if (Settings.END_OF_LINE != EOL_LINUX) {
				eol_text = eol_text.replaceAll("\n", Settings.getEndOfLine());
			}
			writer = new OutputStreamWriter (ocf.openOutputStream());
			out = new BufferedWriter(writer);
			out.write(eol_text);
			out.close();
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "Out of memory error");
			return false;
		} catch (IOException e) {
			Log.e(TAG, "Can't write to file " + ocf.getUriOut().toString());
			return false;
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "CryptoException:  Can't write to file " + ocf.getUriOut().toString());
			e.printStackTrace();
		}
		return true;
	}
		
	 /** Were we started by Box and running with cloud storage only? */
	protected OneCloudFile mOneCloudFile;
	/**  Process NBotification of file name changes from Save As in Box */
	private BroadcastReceiver mBoxFileNameChangedReceiver;	 
}
