package com.bluedoorway.sma.igotiteventcapture;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends Activity
{
	private TextView version;
	private TextView msg;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);

		version = (TextView) findViewById(R.id.setting_screen_main_version_id);
		msg = (TextView) findViewById(R.id.setting_screen_msg);
		version.setText("Version: " + getString(R.string.app_version));
		msg.setText("Welcome, " + IGotItApplication.getUserName());

	}
}
