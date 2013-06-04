package fr.xgouchet.texteditor;



import java.util.Locale;

import fr.xgouchet.texteditor.common.Constants;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
 
public class RatingActivity extends Activity {
 
  private RatingBar ratingBar;
  private TextView txtRatingValue;
  private Button btnSubmit;
  
  //
  private String path=TedActivity.mCurrentFilePath; 

  
  
  SQLiteDatabase db;

  
  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.rating);
	
	//create database
	db = openOrCreateDatabase("tedratingdata.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
	db.setVersion(1);
	db.setLocale(Locale.getDefault());
	db.setLockingEnabled(true);
	
	//create table
	final String CREATE_TABLE_FILES ="CREATE TABLE IF NOT EXISTS ted (ID integer primary key autoincrement,NAME text,RATING integer);";
	db.execSQL(CREATE_TABLE_FILES);
	db.execSQL("SELECT * FROM ted ORDER BY ID desc;");
	
	//find last rating given to the file using its path
	int rating=2;
	if (path != null ) {
		Cursor c = db.rawQuery("SELECT RATING FROM ted " +
	           "where NAME='"+path+"' LIMIT 1;", null);
	
		if (c != null ) {
			if  (c.moveToFirst()) {
				do {
					rating = c.getInt(c.getColumnIndex("RATING"));
				}while (c.moveToNext());
			}
		}
		c.close();
	}

	float rating2=(float) rating;
	addListenerOnRatingBar(rating2);
	addListenerOnButton();
	
  }
 
  public void addListenerOnRatingBar(float rating2) {
 
	ratingBar = (RatingBar) findViewById(R.id.ratingBar);
	txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);
	ratingBar.setRating(rating2);

	//if rating value is changed,
	//display the current rating value in the result (textview) automatically
	ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
		public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
			txtRatingValue.setText(String.valueOf(rating));
 
		}
	});
  }
 
  public void addListenerOnButton() {
 
	ratingBar = (RatingBar) findViewById(R.id.ratingBar);
	btnSubmit = (Button) findViewById(R.id.btnSubmit);

	//if click on me, then display the current rating value.
	btnSubmit.setOnClickListener(new OnClickListener() {
 
		@Override
		public void onClick(View v) {
 
			Toast.makeText(RatingActivity.this,
				String.valueOf(ratingBar.getRating()),
					Toast.LENGTH_SHORT).show();
			int ratinginsert=(int) ratingBar.getRating();
			db.execSQL("INSERT INTO ted (NAME, RATING) VALUES ('"+path+"'," + ratinginsert  +");");			

			
			
		}
 
	});
 
  }

}