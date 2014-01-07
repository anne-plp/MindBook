package com.sizemore.mindbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Set;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class InputListViewer extends ListActivity
{
	
	private DatabaseMaker dm = new DatabaseMaker();
	private HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database;
	private InputCounter ic;
	private String path = "/sdcard/MindBook/";
	private Integer currentMonth;
	private Integer currentYear;
	private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	private String date;
	private String year;
	private int month;
	private int day;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		// Create an array of Strings, that will be put to our ListActivity
		Intent dateIntent = getIntent();
		date = dateIntent.getExtras().getString("date");
		year = date.substring(0,4);
		month = (Integer.parseInt(date.substring(5,7)))-1;
		day = (Integer.parseInt(date.substring(8)))-1;
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
	
		HashMap<String,String> dayInputs = database.get(year).get(months[month]).get(days[day]);
		Set<String> inputTimes = dayInputs.keySet();
		int numberAM = 0;
		int numberPM = 0;
		for (String inputInfo : inputTimes)
		{

		if (inputInfo.contains("AM"))
			numberAM++;
		else
			numberPM++;	
		}
		String[] amEvents = new String[numberAM];
		int amIndex = 0;
		int pmIndex = 0;
		String[] pmEvents = new String[numberPM];
		String[] times = inputTimes.toArray(new String[inputTimes.size()]);
		java.util.Arrays.sort(times);
		for (int i = 0; i < times.length; i++)
		{
		if (times[i].contains("AM"))
				{
				amEvents[amIndex] = times[i];
				amIndex++;
				}
		else
				{
				pmEvents[pmIndex] = times[i];
				pmIndex++;
				}
		}
		int timeIndex = 0;
		for (int i = 0; i < amEvents.length; i++)
			{
			if (amEvents[i].startsWith("00"))
				amEvents[i] = "12" + amEvents[i].substring(2);
			times[timeIndex] = amEvents[i];
			timeIndex++;
			}
		for (int i = 0; i < pmEvents.length; i++)
			{
			if (pmEvents[i].startsWith("00"))
				pmEvents[i] = "12" + pmEvents[i].substring(2);
			times[timeIndex] = pmEvents[i];
			timeIndex++;
			}
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, times));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		Object o = this.getListAdapter().getItem(position);
		String key = o.toString();
		//String time = o.toString().substring(0,8);
		//Toast.makeText(this, "You selected: " + time, Toast.LENGTH_LONG)
		//		.show();
		Intent intent = new Intent(this,InputViewer.class);
		intent.putExtra("key",key);
		intent.putExtra("date",date);
		startActivity(intent);
	}
	
}
