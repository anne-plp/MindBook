package com.sizemore.mindbook;


import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;


public class AudioActivity extends Activity implements OnClickListener {

	private String path = "/sdcard/MindBook/";
	private String filename = "";
	private String file = "";
	final static int RQS_RECORDING = 1;
	private HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database;
	private InputCounter ic;
	private DatabaseMaker dm = new DatabaseMaker();
	private boolean isAM = true;
	private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};

Uri savedUri;

Button buttonRecord;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
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
       setContentView(R.layout.audiolayout);
       buttonRecord = (Button)findViewById(R.id.record);
       buttonRecord.setOnClickListener(new Button.OnClickListener()
       {
    	   @Override
    	   public void onClick(View arg0) 
    	   	{
    		   Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    		   startActivityForResult(intent, RQS_RECORDING);
    	   	}
       });
   }

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	if(requestCode == RQS_RECORDING)
		{
		try
			{
			savedUri = data.getData();
			String file = savedUri.toString();
			Toast.makeText(this, "Audio saved to: " + file, Toast.LENGTH_LONG).show();
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
				time = time + " AM " + " ---		Audio";
			else
				time = time + " PM " + " ---		Audio";
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

@Override
public void onClick(View arg0) 
	{
	
	}






}
