package com.bluedoorway.sma.igotiteventcapture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;

@SuppressLint("SimpleDateFormat")
public class IGotItQuickCaptureAudioActivity extends Activity
{
	// private MediaRecorder recorder;
	int eventId = -1;
	private String imageFilePath = "";
	final String FILE_TYPE = "audio/x-caf";
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quick_capture_audio);

		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("eventid"))
			eventId = getIntent().getExtras().getInt("eventid");
		context = this;
		Button audioRecBtn = (Button) findViewById(R.id.button_audio);
		audioRecBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{

				try
				{
					stop(); // First save data
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				Intent intent = new Intent(context, IGotItQuickCaptureCameraActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle b = new Bundle();
				b.putInt("eventid", eventId);
				intent.putExtras(b);
				startActivity(intent);
				finish();
			}
		});
		
		if (MainActivity.recorder == null)
			MainActivity.recorder = new MediaRecorder();
		else
		{
			MainActivity.recorder.stop();
			MainActivity.recorder.reset();
			MainActivity.recorder.release();
		}
		
		try
		{
			start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Toast.makeText(context, "Unable to record, please try again", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (MainActivity.recorder != null)
		{
			MainActivity.recorder.stop();
			MainActivity.recorder.reset();
			MainActivity.recorder.release();
			MainActivity.recorder = null;
		}
	}

	public void start() throws IOException
	{
		// make sure the directory we plan to store the recording in exists
		File directory = new File(Environment.getExternalStorageDirectory() + "/IGotIt/Audio");
		if (!directory.exists() && !directory.mkdirs())
			throw new IOException("Path to file could not be created.");

		MainActivity.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		MainActivity.recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		MainActivity.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		imageFilePath = directory.getAbsolutePath() + "/IGotIt_" + timeStamp + "_.mp3";
		MainActivity.recorder.setOutputFile(imageFilePath);
		try
		{
			MainActivity.recorder.prepare();
			MainActivity.recorder.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void stop() throws IOException
	{
		if (MainActivity.recorder != null)
		{
			MainActivity.recorder.stop();
			MainActivity.recorder.reset();
			MainActivity.recorder.release();
			MainActivity.recorder = null;

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
}