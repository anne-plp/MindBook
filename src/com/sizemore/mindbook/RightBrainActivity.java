package com.sizemore.mindbook;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.*;
import android.app.*;
import android.content.Intent;


public class RightBrainActivity extends Activity implements OnClickListener
{


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rightbrainmenu);
		

	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		   MenuInflater inflater = getMenuInflater();
		   inflater.inflate(R.menu.options_menu, menu);
		   return true;
		}
	
	public void onClick(View v) 
	{
		if (v.getId() == R.id.photoButton)
			{
			Intent cameraIntent = new Intent(this,CameraActivity.class);
			startActivity(cameraIntent);
			}
		if (v.getId() == R.id.videoButton)
			{
			Intent videoIntent = new Intent(this,VideoActivity.class);
			startActivity(videoIntent);
			}
		if (v.getId() == R.id.speechButton)
			{
			Intent videoIntent = new Intent(this,AudioActivity.class);
			startActivity(videoIntent);
			}
		if (v.getId() == R.id.noteButton)
			{
			Intent noteIntent = new Intent(this,NoteActivity.class);
			startActivity(noteIntent);
			}
		if (v.getId() == R.id.calendarButton)
			{
			Intent calendarIntent = new Intent(this,CalendarActivity.class);
			startActivity(calendarIntent);
			}
		
	}
	
	/*ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture *//*
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	/** Handles data for jpeg picture *//*
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				// write to local sandbox file system
//				outStream = CameraDemo.this.openFileOutput(String.format("%d.jpg", System.currentTimeMillis()), 0);	
				// Or write to sdcard
				outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));	
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

	@Override
	public void onPictureTaken(byte[] data, Camera camera) 
	{
		Toast.makeText(this, "Pic", Toast.LENGTH_SHORT).show();
		
	}*/

}
