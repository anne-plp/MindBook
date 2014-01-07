package com.sizemore.mindbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.app.*;
import android.content.Intent;
import android.util.Log;
import android.view.*;


import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class VideoActivity extends Activity implements OnClickListener
{
	//private MediaRecorder mediaRecorder;
	private String filename = "";
	private HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database;
	private DatabaseMaker dm = new DatabaseMaker();
	//private SurfaceView surfaceView;
    //private SurfaceHolder holder;
    //private Camera camera;
	private static int RECORD_VIDEO = 1;
	private static int HIGH_VIDEO_QUALITY = 1;
	private static final String TAG = "VideoActivity";
	private InputCounter ic;
	private String file = "";
	private boolean isAM = true;
	Uri savedUri;
	private String path = "/sdcard/MindBook/";
	Button recordButton;
	private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videolayout);
		recordButton  = (Button) findViewById(R.id.recordVideoButton);
		recordButton.setVisibility(View.VISIBLE);
		Button keepButton = (Button) findViewById(R.id.keepVideoButton);
		keepButton.setVisibility(View.INVISIBLE);
		Button videoTryAgainButton = (Button) findViewById(R.id.videoTryAgainButton);
		videoTryAgainButton.setVisibility(View.INVISIBLE);
		Button playbackButton = (Button) findViewById(R.id.videoPlaybackButton);
		playbackButton.setVisibility(View.INVISIBLE);
		Button stopButton = (Button) findViewById(R.id.videoStopButton);
		stopButton.setVisibility(View.INVISIBLE);
		

		try
			{
			ObjectInputStream dataOIS = new ObjectInputStream(new FileInputStream(path + "database.ser"));
			database = (HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>>) dataOIS.readObject();
			dataOIS.close();
			}
		catch (IOException ioe)
			{
			database = dm.makeDatabase();
			Toast.makeText(this, "No database found on phone. New database created.", Toast.LENGTH_LONG).show();
			}
		catch (ClassNotFoundException cnfe){}
		try
			{
			ObjectInputStream counterOIS = new ObjectInputStream(new FileInputStream(path + "counter.ser"));
			ic = (InputCounter) counterOIS.readObject();
			counterOIS.close();
			}
		catch (IOException ioe)
			{
			ic = new InputCounter();
			}
		catch (ClassNotFoundException cnfe){}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		   MenuInflater inflater = getMenuInflater();
		   inflater.inflate(R.menu.options_menu, menu);
		   return true;
		}

	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.id.recordVideoButton)
			{
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, HIGH_VIDEO_QUALITY);
			startActivityForResult(intent, RECORD_VIDEO);
			}
			
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
		{
		if(requestCode == RECORD_VIDEO)
			{
			try
				{
				savedUri = data.getData();
				String file = savedUri.toString();
				Toast.makeText(this, "Video saved to: " + file, Toast.LENGTH_LONG).show();
				//ic.numberOfAudioRecordings++;
				Calendar calendar = new GregorianCalendar();
				int month = calendar.get(calendar.MONTH);
				int day = calendar.get(calendar.DAY_OF_MONTH);
				String time = calendar.getTime().toString().substring(11, 19);
				int hour = Integer.valueOf(time.substring(0,2));
				if (hour > 12)
					{
					int oldHour = hour;
					hour = hour - 12;
					String oldS = Integer.toString(oldHour);
					String hourS = Integer.toString(hour);
					if (hourS.length() == 1)
						hourS = "0" + hourS;
					time = time.substring(2);
					time = hourS+time;
					time.replaceFirst(oldS,hourS);
					isAM = false;
					}
				if (isAM)
					time = time + " AM " + " ---		Video";
				else
					time = time + " PM " + " ---		Video";
				String year = new Integer(calendar.get(calendar.YEAR)).toString();
				database.get(year).get(months[month]).get(days[day-1]).put(time, file);
					try
					{
					ObjectOutputStream dataOOS = new ObjectOutputStream(new FileOutputStream(path + "database.ser"));
					dataOOS.writeObject(database);
					dataOOS.close();
					ObjectOutputStream counterOOS = new ObjectOutputStream(new FileOutputStream(path + "counter.ser"));
					counterOOS.writeObject(ic);
					counterOOS.close();
					}
				catch (Exception e)
					{
					Log.d("CAMERA",e.getMessage());
					}
				}
			catch (NullPointerException npe){}

			}
		}

}