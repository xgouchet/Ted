package fr.xgouchet.texteditor;


import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
<<<<<<< HEAD

public class SendSMSActivity extends Activity {

=======
 
public class SendSMSActivity extends Activity {
 
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
	Button buttonSend;
	EditText textPhoneNo;
	EditText textSMS;
	String message;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
<<<<<<< HEAD

		buttonSend = (Button) findViewById(R.id.buttonSend);
		textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
		textSMS = (EditText) findViewById(R.id.editTextSMS);

=======
 
		buttonSend = (Button) findViewById(R.id.buttonSend);
		textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
		textSMS = (EditText) findViewById(R.id.editTextSMS);
		
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
		//add the text of the Ted editor in the sms
		Bundle extras = getIntent().getExtras();
		message="";
		if(extras !=null)
		{
		     message = extras.getString("textofsms");
		}
		int start = textSMS.getSelectionStart();
		int end = textSMS.getSelectionEnd();
		textSMS.getText().replace(Math.min(start, end), Math.max(start, end),
		        message, 0, message.length());
<<<<<<< HEAD

		//send button
		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			  String phoneNo = textPhoneNo.getText().toString();
			  String sms = textSMS.getText().toString();

=======
		
		//send button
		buttonSend.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
 
			  String phoneNo = textPhoneNo.getText().toString();
			  String sms = textSMS.getText().toString();
 
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
			  try {
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(phoneNo, null, sms, null, null);
				Toast.makeText(getApplicationContext(), "SMS Sent!",
							Toast.LENGTH_LONG).show();
			  } catch (Exception e) {
				Toast.makeText(getApplicationContext(),
					"SMS faild, please try again later!",
					Toast.LENGTH_LONG).show();
				e.printStackTrace();
			  }
<<<<<<< HEAD

			}
		});
	}
}
=======
 
			}
		});
	}
}
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
