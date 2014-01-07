package com.sizemore.mindbook;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.app.*;
import android.util.Log;
import android.view.*;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;


import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
//http://stackoverflow.com/questions/4181774/show-image-view-from-file-path-in-android
public class CameraActivity extends Activity implements OnClickListener, SurfaceHolder.Callback
{
	private Camera camera;
	private DatabaseMaker dm = new DatabaseMaker();
	private MediaRecorder mediaRecorder;
	private SurfaceView surface;
    private SurfaceHolder holder;
	private static final String TAG = "CameraActivity";
	private String filename = "";
	private HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>> database;
	private InputCounter ic;
	private String file = "";
	private byte[] picture;
	private Canvas canvas;
	private boolean isAM = true;
	private String path = "/sdcard/MindBook/";
	private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] days = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photolayout);
		Button saveButton = (Button) findViewById(R.id.savePhotoButton);
		saveButton.setVisibility(View.INVISIBLE);
		Button takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
		takePhotoButton.setVisibility(View.INVISIBLE);
		Button cameraTryAgainButton = (Button) findViewById(R.id.cameraTryAgainButton);
		cameraTryAgainButton.setVisibility(View.INVISIBLE);
		Log.d(TAG,"Buttons invisible");
		surface = (SurfaceView) findViewById(R.id.cameraSurface);
		holder = surface.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setFixedSize(400,300);
		canvas = holder.lockCanvas();
		Log.d(TAG,"Added callback to SH");
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
		if (v.getId() == R.id.takePhotoButton)
			{
			camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			Button takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
			takePhotoButton.setVisibility(View.INVISIBLE);
			}
		if (v.getId() == R.id.cameraTryAgainButton)
			{
			Button saveButton = (Button) findViewById(R.id.savePhotoButton);
			saveButton.setVisibility(View.INVISIBLE);
			Button cameraTryAgainButton = (Button) findViewById(R.id.cameraTryAgainButton);
			cameraTryAgainButton.setVisibility(View.INVISIBLE);
			Button takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
			takePhotoButton.setVisibility(View.VISIBLE);
			camera.startPreview();
			/*canvas = holder.lockCanvas();
			Picture picture = new Picture();
			picture.draw(canvas);
			canvas.drawPicture(picture);
			holder.unlockCanvasAndPost(canvas);*/
			}
		if (v.getId() == R.id.savePhotoButton)
			{
			filename = "picture" + ic.numberOfPictures + ".jpg";		// STEP ONE : Set filename
			ic.numberOfPictures++;
			file = path+filename;
				
			Calendar calendar = new GregorianCalendar();				// STEP TWO : Get Calendar object
			int month = calendar.get(calendar.MONTH);					//			  and time/date
			int day = calendar.get(calendar.DAY_OF_MONTH);
			String time = calendar.getTime().toString().substring(11, 19);
			int hour = Integer.valueOf(time.substring(0,2));
			if (hour > 12)
				{
				int oldHour = hour;										// STEP THREE: Format time
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
				time = time + " AM " + " ---		Picture";
			else
				time = time + " PM " + " ---		Picture";
			String year = new Integer(calendar.get(calendar.YEAR)).toString();
			database.get(year).get(months[month]).get(days[day-1]).put(time, file);			// STEP FOUR: Pull day out of
			Toast.makeText(this, "Picture saved to: " + file, Toast.LENGTH_LONG).show();	//			  the database
			Button saveButton = (Button) findViewById(R.id.savePhotoButton);
			saveButton.setVisibility(View.INVISIBLE);
			Button takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
			takePhotoButton.setVisibility(View.INVISIBLE);
				try
					{
					FileOutputStream camFOS = new FileOutputStream(file);	// STEP FIVE: Save picture to MindBook directory
					camFOS.write(picture);
					camFOS.close();
					}
				catch (FileNotFoundException fnfe)
					{
					Log.d("CAMERA", fnfe.getMessage());
					}
				catch (IOException ioe)
					{
					Log.d("CAMERA",ioe.getMessage());
					}
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
    camera.startPreview();*/

		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
	if (mediaRecorder == null)
		{
		try
			{
			Log.d(TAG,"Opening camera");
			camera = Camera.open();
			Log.d(TAG,"Camera opened");
			Camera.Parameters parameters = camera.getParameters();
			setRequestedOrientation(90);
			camera.setDisplayOrientation(90);
			camera.setParameters(parameters);
			Log.d(TAG,"Set parameters");
			camera.setPreviewDisplay(holder);		// set SurfaceHolder as destination for frames
			Log.d(TAG,"Set preview display");
			camera.startPreview();
			Button takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
			takePhotoButton.setVisibility(View.VISIBLE);
			Log.d(TAG,"Button visible");
			Log.d(TAG,"Preview started");
			}
		catch (Exception e)
			{
			Log.d(TAG,e.getMessage());
			}
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
	camera.stopPreview();
	camera.release();
	}
	
	ShutterCallback shutterCallback = new ShutterCallback()
		{
		public void onShutter()
			{
			
			}
		};
		
	PictureCallback rawCallback = new PictureCallback()
		{
		public void onPictureTaken(byte[] data, Camera camera)
			{
			
			}
		};
		
	PictureCallback jpegCallback = new PictureCallback()
		{
		public void onPictureTaken(byte[] data, Camera camera)
			{
			picture = data;
			Button saveButton = (Button) findViewById(R.id.savePhotoButton);
			saveButton.setVisibility(View.VISIBLE);
			Button cameraTryAgainButton = (Button) findViewById(R.id.cameraTryAgainButton);
			cameraTryAgainButton.setVisibility(View.VISIBLE);
			}
		};

}
