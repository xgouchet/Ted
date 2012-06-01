package fr.xgouchet.texteditor.box;
/*  
 * Box One Cloud Integration example
 *  This requires the OneCloupAppToApp SDK which can be found at:
 *  https://github.com/box/box-android-sdk/tree/experimental
 *  check that out and add the library to your project
 */

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.box.onecloud.android.OneCloudData;


public class TedBoxReceiver extends com.box.onecloud.android.BoxOneCloudReceiver{

	/* (non-Javadoc)
	 * @see com.box.onecloud.android.BoxOneCloudReceiver#onEditFileRequested(android.content.Context, long, com.box.onecloud.android.OneCloudFile, java.lang.String, java.lang.String)
	 */

	@Override
	public void onEditFileRequested(Context context, OneCloudData oneCloudData) {
		// TODO Auto-generated method stub
		System.out.println("onEditFileRequested" + oneCloudData.getFileName());
		Intent editIntent;
		editIntent = new Intent("fr.xgouchet.texteditor.ACTION_TED_BOX_OPEN");
		editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    editIntent.setPackage(context.getPackageName());
	    editIntent.putExtra("one_cloud_data", oneCloudData);          
	    editIntent.setType("text/plain");	

		try {
			context.startActivity(editIntent);
			
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void onCreateFileRequested(Context context, OneCloudData oneCloudData) {
		// TODO Auto-generated method stub
		Intent createIntent;
		createIntent = new Intent("fr.xgouchet.texteditor.ACTION_BOX_CREATE_FILE");
		createIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    createIntent.setPackage(context.getPackageName());
	    createIntent.putExtra("one_cloud_data", oneCloudData);          
	    createIntent.setType("text/plain");

		try {
			context.startActivity(createIntent);
			
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}		
		System.out.println("onCreateFileRequested" + oneCloudData.getFileName());
		
	}

	@Override
	public void onViewFileRequested(Context context, OneCloudData oneCloudData) {
		// TODO Auto-generated method stub
		System.out.println("onViewFileRequested" + oneCloudData.getFileName());
		
	}

	@Override
	public void onLaunchRequested(Context context, OneCloudData oneCloudData) {
		// TODO Auto-generated method stub
		System.out.println("onLaunchRequested" + oneCloudData.getFileName());
		
	};

}
