package fr.xgouchet.texteditor.box;
/*  
 * Box One Cloud Integration example
 *  This requires the OneCloupAppToApp SDK which can be found at:
 *  https://github.com/box/box-android-sdk/tree/experimental
 *  check that out and add the library to your project
 */

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.box.onecloud.android.Crypto;
import com.box.onecloud.android.OneCloudFile;

public class TedBoxReceiver extends com.box.onecloud.android.BoxOneCloudReceiver{

	/* (non-Javadoc)
	 * @see com.box.onecloud.android.BoxOneCloudReceiver#onEditFileRequested(android.content.Context, long, com.box.onecloud.android.OneCloudFile, java.lang.String, java.lang.String)
	 */
	@Override
	public void onEditFileRequested(Context context, long boxToken, OneCloudFile oneCloudFile, String fileName, String type) {
	
		Intent editIntent;
		editIntent = new Intent("fr.xgouchet.texteditor.ACTION_TED_BOX_OPEN");
		editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    editIntent.setPackage(context.getPackageName());
	    editIntent.putExtra("BoxToken", boxToken);
	    editIntent.putExtra("BoxFileName", fileName);
	    editIntent.putExtra("BoxType", type);
	    editIntent.putExtra("BoxOneCloudFile", oneCloudFile);          
	    editIntent.putExtra("BoxContent", readStreamAsString(oneCloudFile));
	    editIntent.putExtra("BoxMode", true);
	    editIntent.setType("text/plain");	
		System.out.println("onEditFileRequested");

		try {
			context.startActivity(editIntent);
			
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreateFileRequested(final Context context, final long boxToken, final OneCloudFile oneCloudFile, final String type) {
		// TODO Auto-generated method stub
		System.out.println("onCreateFileRequested");
		// showToast(context, "onCreateFileRequested", false);		
	}

	@Override
	public void onLaunchRequested(Context context, long boxToken) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFileSaved(Context context, long boxToken, String fileName) {	     
		System.out.println("onFileSaved" + fileName);
	     Intent renameIntent = new Intent();
	     renameIntent.setAction("com.box.onecloud.android.RENAME");
	     renameIntent.putExtra("BoxFileName", fileName);
	     renameIntent.putExtra("BoxToken", boxToken);	     
	     context.sendBroadcast(renameIntent);
	}

	@Override
	public void onFileSaving(Context context, long boxToken, String fileName,
			long bytesTransferred) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFileSavedError(Context context, long boxToken, String fileName) {
		// TODO Auto-generated method stub
		
	}

    /**
     * Use the OneCloudFile object passed to decrypt the file passed from Box
     * 
     * @param ocf OnceCloudFile
     * @return A String containg the plain text of the file contents
     */
    private static String readStreamAsString(OneCloudFile ocf) {
        StringBuilder fileData = new StringBuilder(1024);
        char[] buf = new char[1024];
        try {
        	Reader in = new InputStreamReader(ocf.openInputStream(),"UTF-8");
        	int len=0;
         	while((len =in.read(buf)) != -1) {
        		fileData.append(buf, 0, len);
            }
            in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "IO UnsuportedEncodingException";
		} catch (IOException e) {
			e.printStackTrace();
		   return "IO Exception";
		} catch (Crypto.CryptoException e) {
			e.printStackTrace();
			return "CryptoException";			
		}
        return fileData.toString();
    }

    @Override
    public void onViewFileRequested(final Context context, final long boxToken, final OneCloudFile oneCloudFile, final String fileName,
            final String type) {
		System.out.println("onViewFileRequested" + fileName);
    };

}
