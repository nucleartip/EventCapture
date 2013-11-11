package com.bluedoorway.sma.igotiteventcapture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;
import com.bluedoorway.sma.igotiteventcapture.model.Events;
import com.bluedoorway.sma.igotiteventcapture.view.CameraPreview;

public class IGotItQuickCaptureCameraActivity extends Activity implements OnClickListener
{

	private Camera mCamera;
	private CameraPreview mCameraPreview;
	Button captureMode;
	Button flashOnMode;
	Button flashOffMode;
	Button flashAutoMode;
	int flashCount = 0;
	private Context context;
	Parameters camParams;// = mCamera.getParameters();
	int eventId = -1;
	static String imageFilePath = "";
	private FrameLayout preview;
	final String FILE_TYPE = "image/jpeg";
	private ContentResolver resolver;
	private boolean isPrimaryCameraAvail = false;
	int cameraId = -1;
	
	// MediaRecorder recorder;
	// Called when shutter is opened
	ShutterCallback shutterCallback = new ShutterCallback()
	{
		public void onShutter()
		{
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quick_capture_pic);

		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("eventid"))
			eventId = getIntent().getExtras().getInt("eventid");

		preview = (FrameLayout) findViewById(R.id.camera_preview);
		// Adding listener
		ImageButton captureButton = (ImageButton) findViewById(R.id.action_button_capture);
		ImageButton camcorderButton = (ImageButton) findViewById(R.id.action_button_video);
		ImageButton audioButton = (ImageButton) findViewById(R.id.action_button_audio);
		captureButton.setOnClickListener(this);
		camcorderButton.setOnClickListener(this);
		audioButton.setOnClickListener(this);

		captureMode = (Button) findViewById(R.id.flashMainBtn);
		flashOnMode = (Button) findViewById(R.id.flashOnBtn);
		flashOffMode = (Button) findViewById(R.id.flashOffBtn);
		flashAutoMode = (Button) findViewById(R.id.flashAutoBtn);

		captureMode.setOnClickListener(this);
		flashOnMode.setOnClickListener(this);
		flashOffMode.setOnClickListener(this);
		flashAutoMode.setOnClickListener(this);

		flashAutoMode.setVisibility(View.GONE);
		flashOffMode.setVisibility(View.GONE);
		flashOnMode.setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		if (mCamera != null)
		{
			try
			{
				mCamera.stopPreview();
				mCamera.release();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		Intent intent;
		if (IGotItApplication.FLOW_NEW_CAPTURE == MainActivity.FLOW)
		{
			finish();
		}
		else if (IGotItApplication.FLOW_QUICK_CAPTURE == MainActivity.FLOW)
		{
			intent = new Intent(context, IGotItEventsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		else if (IGotItApplication.FLOW_NEW_EVENT == MainActivity.FLOW && eventId > -1)
		{
			resolver = getContentResolver();
			Cursor cursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, EventsDB.EVENT_ID + " = '" + eventId + "'", null,
					null);
			Events event = new Events();
			if (cursor != null && cursor.getCount() > 0)
			{
				if (cursor.moveToNext())
				{
					event.setEventName(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_TITLE)));
					event.setEventCategory(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY)));
					event.setEventComments(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_COMMENT)));
					event.setEventID(cursor.getInt(cursor.getColumnIndex(EventsDB.EVENT_ID)));
					event.setTimeStamp(cursor.getLong(cursor.getColumnIndex(EventsDB.EVENT_TIME_STAMP)));

					Bundle bundle = new Bundle();
					bundle.putInt(IGotItApplication.EVENT_ID_KEY, event.getEventID());
					bundle.putString(IGotItApplication.EVENT_TITLE_KEY, event.getEventName());
					bundle.putString(IGotItApplication.EVENT_CATEGORY_KEY, event.getEventCategory());
					bundle.putString(IGotItApplication.EVENT_COMMENT_KEY, event.getEventComments());
					bundle.putLong(IGotItApplication.EVENT_TIMESTAMP_KEY, event.getTimeStamp());

					intent = new Intent(context, IGotItNewEventsActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}

		finish();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		context = this;

		mCamera = getCameraInstance();
		camParams = mCamera.getParameters();

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int rotation = display.getRotation();

		if (isPrimaryCameraAvail)
		{
			if (rotation == 0 || rotation == 2)
				camParams.setRotation(90);
			else if (rotation == 1 || rotation == 3)
				camParams.setRotation(0);
		}
		else
		{
			if (rotation == 0 || rotation == 2)
				camParams.setRotation(270);
			else if (rotation == 1 || rotation == 3)
				camParams.setRotation(180);
		}

		mCamera.setParameters(camParams);
		setCameraDisplayOrientation();

		mCameraPreview = new CameraPreview(this, mCamera);
		preview.addView(mCameraPreview);
		if (isPrimaryCameraAvail)
			camParams.setFlashMode(Parameters.FLASH_MODE_AUTO);

		AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);

		mCamera.startPreview();
		mCameraPreview.refreshDrawableState();
	}

	public void setCameraDisplayOrientation()
	{
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation)
		{
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		}
		else
		{ // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		mCamera.setDisplayOrientation(result);
		mCamera.setParameters(camParams);
	}

	int getFrontCameraId()
	{
		CameraInfo ci = new CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++)
		{
			Camera.getCameraInfo(i, ci);

			if (ci.facing == CameraInfo.CAMERA_FACING_BACK)
			{
				isPrimaryCameraAvail = true;
				cameraId = i;
				return i;
			}
			else if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
			{
				isPrimaryCameraAvail = false;
				cameraId = i;
				return i;
			}

		}
		return -1; // No front-facing camera found
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (mCamera != null)
		{
			try
			{
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.action_button_capture:
				mCamera.takePicture(shutterCallback, null, mPicture);
				break;
			case R.id.action_button_audio:
				releaseCamera();
				IGotItQuickCaptureCameraActivity.this.finish();
				Intent audioIntent = new Intent(IGotItQuickCaptureCameraActivity.this, IGotItQuickCaptureAudioActivity.class);
				audioIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle b = new Bundle();
				b.putInt("eventid", eventId);
				audioIntent.putExtras(b);
				startActivity(audioIntent);
				break;
			case R.id.action_button_video:
				releaseCamera();
				IGotItQuickCaptureCameraActivity.this.finish();
				Intent intent = new Intent(IGotItQuickCaptureCameraActivity.this, IGotItQuickCaptureCamcorderActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle b1 = new Bundle();
				b1.putInt("eventid", eventId);
				intent.putExtras(b1);
				startActivity(intent);
				break;
			case R.id.flashMainBtn:
				flashCount++;
				if (flashCount % 2 == 0)
				{
					flashAutoMode.setVisibility(View.GONE);
					flashOffMode.setVisibility(View.GONE);
					flashOnMode.setVisibility(View.GONE);
				}
				else
				{
					flashAutoMode.setVisibility(View.VISIBLE);
					flashOffMode.setVisibility(View.VISIBLE);
					flashOnMode.setVisibility(View.VISIBLE);
				}

				break;
			case R.id.flashOnBtn:

				if (isPrimaryCameraAvail)
				{
					camParams.setFlashMode(Parameters.FLASH_MODE_ON);
					mCamera.setParameters(camParams);
				}

				flashCount = 0;
				flashAutoMode.setVisibility(View.GONE);
				flashOffMode.setVisibility(View.GONE);
				flashOnMode.setVisibility(View.GONE);
				captureMode.setText("Flash: On");
				break;
			case R.id.flashOffBtn:
				if (isPrimaryCameraAvail)
				{
					camParams.setFlashMode(Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(camParams);
				}
				
				flashCount = 0;
				flashAutoMode.setVisibility(View.GONE);
				flashOffMode.setVisibility(View.GONE);
				flashOnMode.setVisibility(View.GONE);
				captureMode.setText("Flash: Off");
				break;
			case R.id.flashAutoBtn:
				if (isPrimaryCameraAvail)
				{
					camParams.setFlashMode(Parameters.FLASH_MODE_AUTO);
					mCamera.setParameters(camParams);
				}
				
				flashCount = 0;
				flashAutoMode.setVisibility(View.GONE);
				flashOffMode.setVisibility(View.GONE);
				flashOnMode.setVisibility(View.GONE);
				captureMode.setText("Flash: Auto");
				break;
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (mCamera != null)
			releaseCamera();
	}

	void releaseCamera()
	{
		try
		{
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private Camera getCameraInstance()
	{
		Camera camera = null;

		try
		{
			camera = Camera.open(getFrontCameraId());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return camera;
	}

	PictureCallback mPicture = new PictureCallback()
	{

		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{
			File pictureFile = getOutputMediaFile();
			if (pictureFile == null)
				return;

			try
			{
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				if (eventId == -1)
				{
					ContentResolver resolver = getContentResolver();
					ContentValues value = IGotItApplication.getContentValues("", "", "", "");
					Uri uri = resolver.insert(EventsDB.EVENTS_CONTENT_URI, value);
					if (uri != null)
					{
						eventId = Integer.parseInt(uri.getLastPathSegment());
						ContentValues values = IGotItApplication.getFilesContentValues(eventId, imageFilePath, FILE_TYPE,
								IGotItApplication.getGUID());
						resolver.insert(EventsDB.FILES_CONTENT_URI, values);
					}
				}
				else
				{
					ContentValues values = IGotItApplication.getFilesContentValues(eventId, imageFilePath, FILE_TYPE,
							IGotItApplication.getGUID());
					ContentResolver resolver = getContentResolver();
					resolver.insert(EventsDB.FILES_CONTENT_URI, values);
				}

				mCamera.startPreview();
				mCameraPreview.refreshDrawableState();

			}
			catch (FileNotFoundException e)
			{
				Toast.makeText(context, "Unable to process", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				mCamera.startPreview();
				mCameraPreview.refreshDrawableState();
			}
			catch (IOException e)
			{
				Toast.makeText(context, "Unable to process", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				mCamera.startPreview();
				mCameraPreview.refreshDrawableState();
			}
		}
	};

	private static File getOutputMediaFile()
	{
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/IGotIt/Pics");

		if (!mediaStorageDir.exists())
		{
			if (!mediaStorageDir.mkdirs())
			{
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		File mediaFile;
		imageFilePath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
		mediaFile = new File(imageFilePath);
		return mediaFile;
	}
}
