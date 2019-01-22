package fr.xgouchet.texteditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
<<<<<<< HEAD
<<<<<<< HEAD

public class SendEmailActivity extends Activity {

=======
 
public class SendEmailActivity extends Activity {
 
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
=======
 
public class SendEmailActivity extends Activity {
 
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
	Button buttonSend;
	EditText textTo;
	EditText textSubject;
	EditText textMessage;
	String message="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mail);
<<<<<<< HEAD
<<<<<<< HEAD

		//add the text of the Ted editor in the mail
=======
		
		//add the text of the Ted editor in the mail	
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
=======
		
		//add the text of the Ted editor in the mail	
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
		Bundle extras = getIntent().getExtras();
		message="";
		if(extras !=null)
		{
		     message = extras.getString("textofmail");
		}
		buttonSend = (Button) findViewById(R.id.buttonSend);
		textTo = (EditText) findViewById(R.id.editTextTo);
		textSubject = (EditText) findViewById(R.id.editTextSubject);
		textMessage = (EditText) findViewById(R.id.editTextMessage);
		int start = textMessage.getSelectionStart();
		int end = textMessage.getSelectionEnd();
		textMessage.getText().replace(Math.min(start, end), Math.max(start, end),
		        message, 0, message.length());
<<<<<<< HEAD
<<<<<<< HEAD


		//send email button
		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

=======
=======
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
		
		
		//send email button
		buttonSend.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
 
<<<<<<< HEAD
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
=======
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361

			  String to = textTo.getText().toString();
			  String subject = textSubject.getText().toString();
			  String messagetext=textMessage.getText().toString();
			  Intent email = new Intent(Intent.ACTION_SEND);
			  email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
			  //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
			  //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
			  email.putExtra(Intent.EXTRA_SUBJECT, subject);
			  email.putExtra(Intent.EXTRA_TEXT, messagetext);
<<<<<<< HEAD
<<<<<<< HEAD

			  //need this to prompts email client only
			  email.setType("message/rfc822");

			  startActivity(Intent.createChooser(email, "Choose an Email client :"));

=======
=======
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
 
			  //need this to prompts email client only
			  email.setType("message/rfc822");
 
			  startActivity(Intent.createChooser(email, "Choose an Email client :"));
 
<<<<<<< HEAD
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
=======
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
			}
		});
	}
	protected void onResume() {
		super.onResume();

	}
<<<<<<< HEAD
<<<<<<< HEAD
}
=======
}
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
=======
}
>>>>>>> 2c6f4c24e20f894028726ec87f4c3191b0fd7361
