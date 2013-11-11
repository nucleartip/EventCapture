package com.bluedoorway.sma.igotiteventcapture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bluedoorway.sma.igotiteventcapture.database.CaptureObserver;
import com.bluedoorway.sma.igotiteventcapture.database.EventObserver;
import com.bluedoorway.sma.igotiteventcapture.database.EventUpdateService;
import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;

public class IGotItEventsActivity extends MainActivity implements OnClickListener
{
	private Button quickCaptureBtn;
	private Button newEventsBtn;
	private Button savedEventsBtn;
	private Context context;
	private static EventObserver eventObserver;
	private static CaptureObserver fileObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Setting up form view
		setContentView(R.layout.activity_events_menu_page);

		// Fetching form objects
		context = this;
		quickCaptureBtn = (Button) findViewById(R.id.quickCaptureBtn);
		newEventsBtn = (Button) findViewById(R.id.newEventsBtn);
		savedEventsBtn = (Button) findViewById(R.id.savedEventsBtn);

		// Initializing Listeners
		quickCaptureBtn.setOnClickListener(this);
		newEventsBtn.setOnClickListener(this);
		savedEventsBtn.setOnClickListener(this);
		registerObserver();
	}

	public static void registerObserver()
	{
		// Registering Observer
		eventObserver = new EventObserver(null);
		fileObserver = new CaptureObserver(null);
		IGotItApplication.getAppContext().getContentResolver()
				.registerContentObserver(EventsDB.EVENTS_CONTENT_URI, true, eventObserver);
		IGotItApplication.getAppContext().getContentResolver()
				.registerContentObserver(EventsDB.FILES_CONTENT_URI, true, fileObserver);
	}

	public static void unRegisterObserver()
	{
		IGotItApplication.getAppContext().getContentResolver().unregisterContentObserver(eventObserver);
		IGotItApplication.getAppContext().getContentResolver().unregisterContentObserver(fileObserver);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		startService(new Intent(IGotItApplication.getAppContext(), EventUpdateService.class));
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		// Notifying waiting activity
		setResult(IGotItApplication.FINISH_APP);
		unRegisterObserver();
		finish(); // Destroying current activity

	}

	@Override
	public void onClick(View view)
	{
		Intent intent;
		switch (view.getId())
		{
			case R.id.quickCaptureBtn:
				intent = new Intent(context, IGotItQuickCaptureCameraActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				MainActivity.FLOW = IGotItApplication.FLOW_QUICK_CAPTURE;
				startActivity(intent);
				break;
			case R.id.newEventsBtn:
				intent = new Intent(context, IGotItNewEventsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.savedEventsBtn:
				intent = new Intent(context, IGotItSavedEventsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			default:
				break;
		}
	}
}
