package com.sizemore.mindbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MainMenuActivity extends Activity implements OnClickListener
{
	public static final int PICK_DAY_TO_PLAN = 6;
	FileOutputStream fos;
	Intent intent;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(90);
		setContentView(R.layout.mainmenu);
		// create a File object for the parent directory
		File wallpaperDirectory = new File("/sdcard/MindBook/");
		// have the object build the directory structure, if needed.
		wallpaperDirectory.mkdirs();
		// create a File object for the output file
		File outputFile = new File(wallpaperDirectory, "MindBook");
		// now attach the OutputStream to the file object, instead of a String representation
		try {
			fos = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		   MenuInflater inflater = getMenuInflater();
		   inflater.inflate(R.menu.options_menu, menu);
		   return true;
		}

	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.id.button1)
			{
			Toast.makeText(this, "Pick a day to plan", Toast.LENGTH_SHORT).show();		// Planner
			Intent intent = new Intent(this,CalendarActivity.class);
			intent.putExtra("pickDate", true);
			startActivityForResult(intent,PICK_DAY_TO_PLAN);
			}
		if (v.getId() == R.id.button2)
			{
			intent = new Intent(this,RightBrainActivity.class);		// Scrapbook
			startActivity(intent);
			}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	super.onActivityResult(requestCode, resultCode, data);
	if (requestCode == PICK_DAY_TO_PLAN) 
		{
	            if (resultCode == RESULT_OK) 
	            {
	             //Toast.makeText(getApplicationContext(), data.getStringExtra("date"), Toast.LENGTH_SHORT).show();
	             String date = data.getStringExtra("date");
	             Intent intent = new Intent(this,LeftBrainActivity.class);
	             intent.putExtra("date", date);
	             startActivity(intent);
	            }
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
		{
		if (item.getItemId() == R.id.menuSeeCalendar)
			{
			Intent intent = new Intent(this,CalendarActivity.class);
			startActivity(intent);
			}
		return true;
		}
}