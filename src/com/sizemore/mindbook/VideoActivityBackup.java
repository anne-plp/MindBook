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
import android.util.Log;
import android.view.*;


import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
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

public class VideoActivityBackup extends Activity implements OnClickListener, SurfaceHolder.Callback
{
	private MediaRecorder mediaRecorder;
	private String filename = "";
	private HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database;
	private DatabaseMaker dm = new DatabaseMaker();
	private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private Camera camera;
	private static final String TAG = "VideoActivity";
	private InputCounter ic;
	private String file = "";
	private boolean isAM = true;
	private String path = "/sdcard/MindBook/";
	private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videolayout);
		
		Button keepButton = (Button) findViewById(R.id.keepVideoButton);
		keepButton.setVisibility(View.INVISIBLE);
		Button recordButton = (Button) findViewById(R.id.recordVideoButton);
		recordButton.setVisibility(View.INVISIBLE);
		Button videoTryAgainButton = (Button) findViewById(R.id.videoTryAgainButton);
		videoTryAgainButton.setVisibility(View.INVISIBLE);
		Button playbackButton = (Button) findViewById(R.id.videoPlaybackButton);
		playbackButton.setVisibility(View.INVISIBLE);
		Button stopButton = (Button) findViewById(R.id.videoStopButton);
		stopButton.setVisibility(View.INVISIBLE);
		
		surfaceView = (SurfaceView) findViewById(R.id.videoSurface);
		holder = surfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setFixedSize(400,300);
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
			mediaRecorder.start();
			Button stopButton = (Button) findViewById(R.id.videoStopButton);
			stopButton.setVisibility(View.VISIBLE);
			Button recordButton = (Button) findViewById(R.id.recordVideoButton);
			recordButton.setVisibility(View.INVISIBLE);
			}
		if (v.getId() == R.id.videoTryAgainButton)
			{
			try {
				camera.reconnect();
				} 
			catch (IOException e) 
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			camera.release();
			Button keepButton = (Button) findViewById(R.id.keepVideoButton);
			keepButton.setVisibility(View.INVISIBLE);
			Button cameraTryAgainButton = (Button) findViewById(R.id.videoTryAgainButton);
			cameraTryAgainButton.setVisibility(View.INVISIBLE);
			Button recordButton = (Button) findViewById(R.id.recordVideoButton);
			recordButton.setVisibility(View.VISIBLE);
			mediaRecorder.reset();
			reinitVideo(holder);
			}
		if (v.getId() == R.id.keepVideoButton)
			{
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
				isAM = false;
				}
			if (isAM)
				time = time + " AM " + " ---		Video";
			else
				time = time + " PM " + " ---		Video";
			String year = new Integer(calendar.get(calendar.YEAR)).toString();
			database.get(year).get(months[month]).get(days[day-1]).put(time, file);
			Toast.makeText(this, "Video saved to: " + file, Toast.LENGTH_LONG).show();
			ic.numberOfVideos++;
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
		if (v.getId() == R.id.videoStopButton)
			{
			mediaRecorder.stop();
			Button keepButton = (Button) findViewById(R.id.keepVideoButton);
			keepButton.setVisibility(View.VISIBLE);
			Button cameraTryAgainButton = (Button) findViewById(R.id.videoTryAgainButton);
			cameraTryAgainButton.setVisibility(View.VISIBLE);
			Button stopButton = (Button) findViewById(R.id.videoStopButton);
			stopButton.setVisibility(View.INVISIBLE);
			}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		/*camera.stopPreview();
		Camera.Parameters parameters = camera.getParameters();
		Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		setRequestedOrientation(90);
		if(display.getOrientation() == Surface.ROTATION_0)
	    {
	        parameters.setPreviewSize(height, width);                           
	        camera.setDisplayOrientation(90);
	    }
		camera.setParameters(parameters);
		try
		{
		camera.setPreviewDisplay(holder);       
		}
		catch (Exception e){}
	    camera.startPreview();
	    camera.unlock();*/
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		if (mediaRecorder == null)
		{
		try
			{
			mediaRecorder = new MediaRecorder();
			camera = Camera.open();
			Log.d(TAG,"Camera opened");
			Camera.Parameters parameters = camera.getParameters();
			camera.setParameters(parameters);
			camera.setDisplayOrientation(90);
			setRequestedOrientation(90);
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			camera.unlock();
			mediaRecorder.setCamera(camera);
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			
			
			
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
			
			filename = "video" + ic.numberOfVideos + ".mp4";
			file = path+filename;
			
			mediaRecorder.setOutputFile(file);
			mediaRecorder.setPreviewDisplay(holder.getSurface());
			
			mediaRecorder.prepare();
			Button recordButton = (Button) findViewById(R.id.recordVideoButton);
			recordButton.setVisibility(View.VISIBLE);
			}
		catch (IllegalArgumentException e)
			{
			Log.d(TAG,e.getMessage());
			}
		catch (IllegalStateException e)
			{
			Log.d(TAG,e.getMessage());
			}
		catch (IOException e)
			{
			Log.d(TAG,e.getMessage());
			}
		
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
	camera.release();
	mediaRecorder.release();
	}
	
	public void reinitVideo(SurfaceHolder holder) 
	{
		try
			{
			camera = Camera.open();
			Log.d(TAG,"Camera opened");
			Camera.Parameters parameters = camera.getParameters();
			camera.setParameters(parameters);
			camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			camera.unlock();
			mediaRecorder.setCamera(camera);
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);  // use this only to get it working
			
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
			filename = "video" + ic.numberOfVideos + ".mp4";
			file = path+filename;
			
			mediaRecorder.setOutputFile(file);
			mediaRecorder.setPreviewDisplay(holder.getSurface());
			
			mediaRecorder.prepare();
			Button recordButton = (Button) findViewById(R.id.recordVideoButton);
			recordButton.setVisibility(View.VISIBLE);
			}
		catch (IllegalArgumentException e)
			{
			Log.d(TAG,e.getMessage());
			}
		catch (IllegalStateException e)
			{
			Log.d(TAG,e.getMessage());
			}
		catch (IOException e)
			{
			Log.d(TAG,e.getMessage());
			}
		
		}
	

}