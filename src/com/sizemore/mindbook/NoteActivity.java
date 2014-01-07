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

import android.app.Activity;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends Activity
{
private EditText noteField;
private Button saveButton;
private DatabaseMaker dm = new DatabaseMaker();
private String filename = "";
private HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database;
private InputCounter ic;
private String file = "";
private boolean isAM = true;
private String path = "/sdcard/MindBook/";
private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
private String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};

public void onCreate(Bundle savedInstanceState) 
{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.notelayout);
	saveButton = (Button) findViewById(R.id.saveNoteButton);
	noteField = (EditText) findViewById(R.id.noteTextField);
	
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

public void onClick(View v) 
{
	if (v.getId() == R.id.saveNoteButton)
		{
		filename = "note" + ic.numberOfNotes + ".txt";
		ic.numberOfNotes++;
		file = path+filename;
		
		String note = noteField.getText().toString(); 
		if (note.length() == 0)
			{
			Toast.makeText(this, "No text entered.", Toast.LENGTH_LONG).show();
			return;
			}
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
			time = time + " AM " + " ---		Note";
		else
			time = time + " PM " + " ---		Note";
		String year = new Integer(calendar.get(calendar.YEAR)).toString();
		//database.get(year).get(months[month]).get(days[day-1]).put(time, file);
		database.get(year).get(months[month]).get(days[day-1]).put(time, note);
		//Toast.makeText(this, "Note saved to: " + file, Toast.LENGTH_LONG).show();
		Toast.makeText(this, "Note saved to database.", Toast.LENGTH_LONG).show();
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
		
}



}
