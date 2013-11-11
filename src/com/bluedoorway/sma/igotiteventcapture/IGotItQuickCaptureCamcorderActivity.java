package com.bluedoorway.sma.igotiteventcapture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;

@SuppressLint("SimpleDateFormat")
public class IGotItQuickCaptureCamcorderActivity extends Activity implements SurfaceHolder.Callback
{
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	private String TAG = "CAM";
	File video;
	private Camera mCamera;
	Handler mHandler;
	int eventId = -1;
	String imageFilePath = "";
	final String FILE_TYPE = "video/quicktime";
	private Context context;
	int cameraId = -1;
	Parameters camParams;
	private boolean isPrimaryCameraAvail = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quick_capture_camcorder);

		context = this;

		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("eventid"))
			eventId = getIntent().getExtras().getInt("eventid");

		cameraId = getFrontCameraId();
		mCamera = Camera.open(cameraId);
		camParams = mCamera.getParameters();

		surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		final Button stopVideoBtn = (Button) findViewById(R.id.button_video);
		stopVideoBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				stopRecording();

				Intent intent = new Intent(context, IGotItQuickCaptureCameraActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle b = new Bundle();
				b.putInt("eventid", eventId);
				intent.putExtras(b);
				startActivity(intent);
				finish();
			}
		});

		mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
					case 0:
						((TextView) findViewById(R.id.recTV)).setText("");
						mHandler.sendEmptyMessageDelayed(0, 700);
						break;
					case 1:
						((TextView) findViewById(R.id.recTV)).setText("   REC");
						mHandler.sendEmptyMessageDelayed(1, 500);
						break;
				}
			}
		};

		mHandler.sendEmptyMessageDelayed(0, 1000);
		mHandler.sendEmptyMessageDelayed(1, 500);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

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
		mCamera.setParameters(camParams);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
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

// TODO: review
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		}
		else
			result = (info.orientation - degrees + 360) % 360;
		mCamera.setDisplayOrientation(result);
	}

	int getFrontCameraId()
	{
		CameraInfo ci = new CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++)
		{
			Camera.getCameraInfo(i, ci);
			if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
			{
				isPrimaryCameraAvail = false;
				return i;
			}
			else if (ci.facing == CameraInfo.CAMERA_FACING_BACK)
			{
				isPrimaryCameraAvail = true;
				return i;
			}

		}
		return -1; // No front-facing camera found
	}

	protected void startRecording() throws IOException
	{
		if (MainActivity.recorder == null)
			MainActivity.recorder = new MediaRecorder();
		else
		{
			MainActivity.recorder.reset();
			MainActivity.recorder.release();
		}
		mCamera.unlock();

		MainActivity.recorder.setCamera(mCamera);
		MainActivity.recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		MainActivity.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

		if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_480P))
			MainActivity.recorder.setProfile(CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_480P));
		else if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_LOW))
			MainActivity.recorder.setProfile(CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_LOW));

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int rotation = display.getRotation();

		if (cameraId == 1)
		{
			if (rotation == 0 || rotation == 2)
				MainActivity.recorder.setOrientationHint(90);
			else if (rotation == 1 || rotation == 3)
				MainActivity.recorder.setOrientationHint(0);
		}
		else
		{
			if (rotation == 0 || rotation == 2)
				MainActivity.recorder.setOrientationHint(270);
			else if (rotation == 1 || rotation == 3)
				MainActivity.recorder.setOrientationHint(180);
		}

		// MainActivity.recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// MainActivity.recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// MainActivity.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		// MainActivity.recorder.setVideoFrameRate(15);

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		imageFilePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/IGotIt/Video/IGotIt_" + timeStamp + ".mp4";
		MainActivity.recorder.setOutputFile(imageFilePath);
		MainActivity.recorder.setPreviewDisplay(surfaceHolder.getSurface());
		MainActivity.recorder.prepare();
		MainActivity.recorder.start();
	}

	protected void stopRecording()
	{
		if (MainActivity.recorder != null)
		{
			MainActivity.recorder.stop();
			MainActivity.recorder.reset();
			MainActivity.recorder.release();
			MainActivity.recorder = null;
			mCamera.stopPreview();
			mCamera.release();
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
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		try
		{
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
		}
		catch (Exception e)
		{
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (mCamera != null)
		{
			Parameters params = mCamera.getParameters();
			mCamera.setParameters(params);
			try
			{
				startRecording();
			}
			catch (FileNotFoundException e)
			{
				Toast.makeText(context, "Unable to open file", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(context, IGotItQuickCaptureCameraActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle b = new Bundle();
				b.putInt("eventid", eventId);
				intent.putExtras(b);
				startActivity(intent);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				String message = e.getMessage();
				Log.i(TAG, "Problem Start" + message);
				MainActivity.recorder.release();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Camera not available!", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
	}
}