package com.sizemore.mindbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


public class CalendarActivity extends Activity {

public Calendar month;
public CalendarAdapter adapter;
public Handler handler;
public ArrayList<String> items; // container to store some random calendar items

private DatabaseMaker dm = new DatabaseMaker();
private HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database;
private InputCounter ic;
private boolean pickDate = false;
private String path = "/sdcard/MindBook/";
private Integer currentMonth;
private Integer currentYear;
private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
private String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};


public void onCreate(Bundle savedInstanceState) 
{
	super.onCreate(savedInstanceState);
	setRequestedOrientation(90);
	setContentView(R.layout.calendar);
	month = Calendar.getInstance();
	currentMonth = month.get(Calendar.MONTH);
	currentYear = month.get(Calendar.YEAR);
	
	Intent pickIntent = getIntent();
	try
		{
		pickDate = pickIntent.getExtras().getBoolean("pickDate");
		}
	catch (NullPointerException npe){}
	
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

	items = new ArrayList<String>();
	adapter = new CalendarAdapter(this, month);

	GridView gridview = (GridView) findViewById(R.id.gridview);
	gridview.setAdapter(adapter);

	handler = new Handler();
	handler.post(calendarUpdater);

	TextView title = (TextView) findViewById(R.id.title);
	title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

	TextView previous = (TextView) findViewById(R.id.previous);
	previous.setOnClickListener(new OnClickListener() {

@Override
	public void onClick(View v) 
	{
	if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) 
		{
		currentYear = month.get(Calendar.YEAR)-1;
		currentMonth = month.getActualMaximum(Calendar.MONTH);
		month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
		} 
	else 
		{
		currentMonth = month.get(Calendar.MONTH)-1;
		month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
		}
	refreshCalendar();
	}
});

TextView next = (TextView) findViewById(R.id.next);
next.setOnClickListener(new OnClickListener() 
{
@Override
	public void onClick(View v) 
		{
		if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) 
				{
				currentYear = month.get(Calendar.YEAR) + 1;
				currentMonth = month.getActualMinimum(Calendar.MONTH);
				month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
				} 
		else 
			{
			currentMonth = month.get(Calendar.MONTH)+1;
			month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
			}
		refreshCalendar();
		}
});

gridview.setOnItemClickListener(new OnItemClickListener() 
	{
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		{
		TextView date = (TextView)v.findViewById(R.id.date);
		if(date instanceof TextView && !date.getText().equals("")) 
			{
			if (pickDate)
				{
				Intent intent = new Intent();
				String day = date.getText().toString();
				if(day.length()==1) {
				day = "0"+day;
				}
				// return chosen date as string format
				intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day);
				setResult(RESULT_OK, intent);
				finish();
				}
			else
				{
				Intent intent = new Intent(v.getContext(),InputListViewer.class);
				String day = date.getText().toString();
				if(day.length()==1) 
					{
					day = "0"+day;
					}
				// get date to use with input list viewer
				intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day);
				startActivity(intent);
				}
			}

		}
	});
}

public void refreshCalendar()
	{
	TextView title = (TextView) findViewById(R.id.title);

	adapter.refreshDays();
	adapter.notifyDataSetChanged();
	handler.post(calendarUpdater); // generate some random calendar items

	title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

public void onNewIntent(Intent intent) 
	{
	String date = intent.getStringExtra("date");
	String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
	month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
	}

public Runnable calendarUpdater = new Runnable() 
	{

	@Override
	public void run() 
		{
		items.clear();
		// format random values. You can implement a dedicated class to provide real values
		/*for(int i=0;i<31;i++) 
			{
			Random r = new Random();

			if(r.nextInt(10)>6)
				{
				items.add(Integer.toString(i));
				}
			}*/
		/*Calendar calendar = new GregorianCalendar();
		int month = calendar.get(calendar.MONTH);
		int day = calendar.get(calendar.DAY_OF_MONTH);
		String time = calendar.getTime().toString().substring(11, 19);
		String year = new Integer(calendar.get(calendar.YEAR)).toString();*/
		HashMap<String,HashMap<String,String>> monthMap;
		try
			{
			monthMap = database.get(currentYear.toString()).get(months[currentMonth]);
			for (Integer i = 1; i<32; i++)
				{
				HashMap<String,String> dayMap = monthMap.get(i.toString());
				Set<String> dayInputs = dayMap.keySet();
				for (String input : dayInputs)
					{
					if (input != "")
						{
						String addition = Integer.toString(i);
						if (addition.length() == 1)
							addition = "0"+addition;
						items.add(addition);
						break;
						}
					}
					
				}
			}
		catch (NullPointerException npe) {}
		adapter.setItems(items);
		adapter.notifyDataSetChanged();
		}
	};
}

