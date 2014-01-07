package com.sizemore.mindbook;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.*;
import android.app.*;
import android.content.Intent;
import android.widget.*;
import android.hardware.*;

public class LeftBrainActivity extends Activity implements OnClickListener
{
	private EditText text;
	private TimePicker tp;
	private TextView tv;
	public String date = "";
	private HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database;
	private String path = "/sdcard/MindBook/";
	private DatabaseMaker dm = new DatabaseMaker();
	private InputCounter ic;
	private String year;
	private String file = "";
	private String filename = "";
	private boolean isAM = true;
	private int month;
	private int day;
	private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(90);
		Intent intent = getIntent();
		String date = intent.getStringExtra("date");
		year = date.substring(0,4);
		month = (Integer.parseInt(date.substring(5,7)))-1;
		day = (Integer.parseInt(date.substring(8)))-1;
		setContentView(R.layout.leftbrainmenu);
		text = (EditText) findViewById(R.id.planField);
		tp = (TimePicker) findViewById(R.id.planningTimePicker);
		tv = (TextView) findViewById(R.id.planDate);
		tv.setText(months[month] + " " + days[day] + ", " + year);
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
	
	public void onClick(View v) 
	{
		if (v.getId() == R.id.planButton)
		{
			
		Editable plan = text.getText();
		String plannedEvent = plan.toString();
		if (plannedEvent.length() == 0)
			{
			Toast.makeText(this, "No plans written.", Toast.LENGTH_SHORT).show();
			return;
			}
		ic.numberOfEvents++;
		Integer hourI = tp.getCurrentHour();
		
		if (hourI >= 12)
			{
			int oldHour = hourI;
			hourI = hourI - 12;
			isAM = false;
			}
		
		Integer minuteI = tp.getCurrentMinute();
		String hour = hourI.toString();
		if (hour.length() == 1)
			hour = "0"+hour;
		String minute = minuteI.toString();
		if (minute.length() == 1)
			minute = "0"+minute;
		String time = hour + ":" + minute;
		if (isAM)
			time = time + " AM " + " ---		Planned Event";
		else
			time = time + " PM " + " ---		Planned Event";
		if (!database.containsKey(year))
			database = dm.addYear(database,year);
		database.get(year).get(months[month]).get(days[day]).put(time, plannedEvent);
		Toast.makeText(this, "Planned event saved to database.", Toast.LENGTH_LONG).show();
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
			{}	
		}
		
		if (v.getId() == R.id.planScheduleButton)
			{
			Intent oldIntent = getIntent();
			String date = oldIntent.getStringExtra("date");
			Intent intent = new Intent(this,Schedule.class);
			intent.putExtra("date", date);
			startActivity(intent);
			}
		
	}

}
