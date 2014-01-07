package com.sizemore.mindbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class InputViewer extends Activity implements SurfaceHolder.Callback
{
	private DatabaseMaker dm = new DatabaseMaker();
	private HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database;
	private String path = "/sdcard/MindBook/";
	private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	private String date, key, year, time;
	private int month, day;
	private Button prevButton;
	private Button nextButton;
	private Button playAudioButton;
	private Button playVideoButton;
	private Button stopAudioButton;
	private Button stopVideoButton;
	private TextView tv;
	private SurfaceView surface;
	private VideoView videoViewer;
	private ImageView photoViewer;
	private MediaPlayer mp = new MediaPlayer();
	private SurfaceHolder holder;
	private EditText noteViewer;
	private int keyIndex = 0;
	private String[] keys, times;
	private HashMap<String,String> dayInputs;
	private Canvas canvas;
	private MediaController mc;
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputviewer);
		setRequestedOrientation(90);
		prevButton = (Button) findViewById(R.id.previousViewer);
		nextButton = (Button) findViewById(R.id.nextViewer);
		playVideoButton = (Button) findViewById(R.id.viewPlayVideoButton);
		playAudioButton = (Button) findViewById(R.id.viewPlayAudioButton);
		stopVideoButton = (Button) findViewById(R.id.viewStopVideoButton);
		stopAudioButton = (Button) findViewById(R.id.viewStopAudioButton);
		surface = (SurfaceView) findViewById(R.id.inputViewerSurface);
		surface.setVisibility(View.INVISIBLE);
		holder = surface.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setFixedSize(400, 300);
		tv = (TextView) findViewById(R.id.dateViewer);
		photoViewer = (ImageView) findViewById(R.id.photoViewer);
		noteViewer = (EditText) findViewById(R.id.noteViewer);
		photoViewer.setVisibility(View.INVISIBLE);
		noteViewer.setVisibility(View.INVISIBLE);
		playVideoButton.setVisibility(View.INVISIBLE);
		playAudioButton.setVisibility(View.INVISIBLE);
		stopVideoButton.setVisibility(View.INVISIBLE);
		stopAudioButton.setVisibility(View.INVISIBLE);
		// Create an array of Strings, that will be put to our ListActivity
		Intent dateIntent = getIntent();
		date = dateIntent.getExtras().getString("date");
		year = date.substring(0,4);
		month = (Integer.parseInt(date.substring(5,7)))-1;
		day = (Integer.parseInt(date.substring(8)))-1;
		key = dateIntent.getExtras().getString("key");
		if (key.contains("Planned"))
			time = key.substring(0,8);
		else
			time = key.substring(0,11);
		if (key.startsWith("12"))
			key = "00" + key.substring(2);
		tv.setText(months[month] + " " + days[day] + ", " + year + " : " + time);
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

	
		dayInputs = database.get(year).get(months[month]).get(days[day]);
		Set<String> inputKeys = dayInputs.keySet();
		keys = inputKeys.toArray(new String[inputKeys.size()]);
		times = inputKeys.toArray(new String[inputKeys.size()]);
		java.util.Arrays.sort(times);
		java.util.Arrays.sort(keys);
		Vector<String> rearrangeTimes = new Vector<String>();
		for (int i = 0; i < times.length; i++)
			{
			if (times[i].contains("AM"))
				rearrangeTimes.add(times[i]);
			}
		for (int i = 0; i < times.length; i++)
			{
			if (times[i].contains("PM"))
				rearrangeTimes.add(times[i]);			
			}
		for (int i = 0; i < rearrangeTimes.size(); i++)	// get AMs and PMs organized correctly by time
			{
			times[i] = rearrangeTimes.elementAt(i);
			keys[i] = rearrangeTimes.elementAt(i);
			}
		
		String[] events = new String[keys.length];
		for (int i = 0; i < keys.length; i++)
			{
			events[i] = dayInputs.get(keys[i]);
			if (keys[i].contains("Planned"))
				times[i] = keys[i].substring(0,8);
			else
				times[i] = keys[i].substring(0,11);
			}
		for (int i = 0; i < times.length; i++)
		{
		if (times[i].startsWith("00"))
			times[i] = "12" + times[i].substring(2);
		}
		for (int i = 0; i < keys.length; i++)
			{
			if (keys[i].equals(key))
				{
				keyIndex = i;
				break;
				}
			}

		if (key.contains("Picture"))
			{
			displayPicture(keyIndex);
			}
		else if (key.contains("Video"))
			{
			displayVideo(keyIndex);
			}
		else if (key.contains("Audio"))
			{
			displayAudio(keyIndex);
			}
		else if (key.contains("Note"))
			{
			displayNote(keyIndex);
			}
		else
			{
			displayEvent(keyIndex);
			}
	}
	
	public void onClick(View v) 
		{
			photoViewer.setVisibility(View.INVISIBLE);
			noteViewer.setVisibility(View.INVISIBLE);
			surface.setVisibility(View.INVISIBLE);
		playVideoButton.setVisibility(View.INVISIBLE);
		playAudioButton.setVisibility(View.INVISIBLE);
		stopVideoButton.setVisibility(View.INVISIBLE);
		stopAudioButton.setVisibility(View.INVISIBLE);
		
		if (v.getId() == R.id.nextViewer)
			{
			mp.reset();
			keyIndex++;
			if (keyIndex > (keys.length-1))
				keyIndex = 0;
			key = keys[keyIndex];
			time = times[keyIndex];
			tv.setText(months[month] + " " + days[day] + ", " + year + " : " + time);
			if (key.contains("Picture"))
				{
				displayPicture(keyIndex);
				}
			else if (key.contains("Video"))
				{
				displayVideo(keyIndex);
				}
			else if (key.contains("Audio"))
				{
				displayAudio(keyIndex);
				}
			else if (key.contains("Note"))
				{
				displayNote(keyIndex);
				}
			else
				{
				displayEvent(keyIndex);
				}
			}
		
		if (v.getId() == R.id.previousViewer)
			{
			mp.reset();
			keyIndex--;
			if (keyIndex < 0)
				keyIndex = keys.length - 1;
			key = keys[keyIndex];
			time = times[keyIndex];
			tv.setText(months[month] + " " + days[day] + ", " + year + " : " + time);
			if (key.contains("Picture"))
				{
				displayPicture(keyIndex);
				}
			else if (key.contains("Video"))
				{
				displayVideo(keyIndex);
				}
			else if (key.contains("Audio"))
				{
				displayAudio(keyIndex);
				}
			else if (key.contains("Note"))
				{
				displayNote(keyIndex);
				}
			else
				{
				displayEvent(keyIndex);
				}
			}
		
		}
	
	public void displayPicture(int keyIndex)  // http://android-er.blogspot.com/2010/05/draw-bitmap-on-view.html
		{										//http://android-er.blogspot.com/2010/01/how-to-display-jpg-in-sdcard-on.html
		photoViewer.setVisibility(View.VISIBLE); // http://www.anddev.org/resize_and_rotate_image_-_example-t621.html
		String imagePath = dayInputs.get(keys[keyIndex]);
		Bitmap picture = BitmapFactory.decodeFile(imagePath);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
		int width = bm.getWidth();
		int height = bm.getHeight();
		int newWidth = width;
		int newHeight = height;
		
		float scaleWidth = ((float) newWidth)/width;
		float scaleHeight = ((float) newHeight) / height;
		
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		matrix.postRotate(90);
		Bitmap resizedBM = Bitmap.createBitmap(bm,0,0,width,height,matrix,true);
		
		photoViewer.setImageBitmap(resizedBM); 
		}
	
	public void displayVideo(int keyIndex) // http://www.androidpeople.com/android-videoview-example
		{
		//mp.reset();
		surface.setVisibility(View.VISIBLE);
		String filepath = dayInputs.get(keys[keyIndex]);
		Uri uri = Uri.parse(filepath);
		Context context = getApplicationContext();
		try
			{
			mp.setDisplay(holder);
			mp.setDataSource(context,uri);
			mp.prepare();
			mp.start();
			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}
	
	
	public void displayAudio(int keyIndex)
		{
		String filepath = dayInputs.get(keys[keyIndex]);
		Uri uri = Uri.parse(filepath);
		Context context = getApplicationContext();
		try {
			mp.setDataSource(context, uri);
			} 
		catch (Exception e) 
			{
			e.printStackTrace();
			}
		try {
			mp.prepare();
			} 
		catch (Exception e) 
			{
			e.printStackTrace();
			} 
		mp.start();
		}
	
	public void displayNote(int keyIndex)
	{
		noteViewer.setVisibility(View.VISIBLE);
		String note = dayInputs.get(keys[keyIndex]);
		noteViewer.setText(note);
		}
	public void displayEvent(int keyIndex)
		{
		noteViewer.setVisibility(View.VISIBLE);
		String event = dayInputs.get(keys[keyIndex]);
		noteViewer.setText("Planned event at this time: " + event);
		}
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	mp.setDisplay(holder);	
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	//mp.release();	
	}


}
