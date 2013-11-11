package com.bluedoorway.sma.igotiteventcapture;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bluedoorway.sma.igotiteventcapture.model.CategoryDetail;
import com.bluedoorway.sma.igotiteventcapture.model.response.ApplicationDetailResponse;
import com.bluedoorway.sma.igotiteventcapture.model.response.EventAssetDownloadResponse;
import com.bluedoorway.sma.igotiteventcapture.model.response.EventCaptureResponse;
import com.bluedoorway.sma.igotiteventcapture.model.response.UserAccountSyncResponse;
import com.bluedoorway.sma.igotiteventcapture.task.EventAssetDownloadTask;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.ApplicationDetailTaskDelegate;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.EventAssetDownloadTaskDelegate;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.EventCaptureTaskDelegate;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.UserAccountSyncTaskDelegate;

public class MainActivity extends Activity implements ApplicationDetailTaskDelegate, EventCaptureTaskDelegate,
		UserAccountSyncTaskDelegate, EventAssetDownloadTaskDelegate
{

	private Context context;
	public static MediaRecorder recorder;
	private AlertDialog.Builder confirmExitDialog;
	public static int FLOW = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Setting up view
		setContentView(R.layout.activity_main);

		// Fetching form objects
		context = this;

		// Initialing Objects
		confirmExitDialog = new AlertDialog.Builder(context);
		confirmExitDialog.setMessage("You sure, want to logout !");
		confirmExitDialog.setPositiveButton("Yes", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// Clear all the saved preferences
				performLogout();
				Intent intent = new Intent(context, IGotItLoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		confirmExitDialog.setNegativeButton("Cancel", null);
		confirmExitDialog.setCancelable(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_events, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		String title = item.getTitle().toString();
		if (title.equals("Logout"))
		{
			// Pop a confirmation box
			confirmExitDialog.show();
		}
		else if (title.equals("Setting"))
		{
			Intent intent = new Intent(context, SettingsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		else if (title.equals("Sync"))
		{
			Intent intent = new Intent(context, SynchronizerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void Response(ApplicationDetailResponse response)
	{
		// Log.v("response", response.toString());
	}

	@Override
	public void Response(EventCaptureResponse response)
	{
		// Log.v("response", response.toString());
	}

	public static ArrayList<ContentValues> getMultipleValues()
	{
		ArrayList<ContentValues> list = new ArrayList<ContentValues>();
		return list;
	}

	public void performLogout()
	{
		Context context = IGotItApplication.getAppContext();
		Editor editor = context.getSharedPreferences(IGotItApplication.USER_PREFS, 0).edit();
		editor.putString(IGotItApplication.USER_NAME, "");
		editor.putString(IGotItApplication.USER_PSWD, "");
		editor.commit();
	}

	public static void saveUserPreference(String name, String pswd)
	{
		Context context = IGotItApplication.getAppContext();
		Editor editor = context.getSharedPreferences(IGotItApplication.USER_PREFS, 0).edit();
		editor.putString(IGotItApplication.USER_NAME, name);
		editor.putString(IGotItApplication.USER_PSWD, pswd);
		editor.commit();
	}

	public static void saveCategories(String[][] categories)
	{
		String category = "";
		for (String[] data : categories)
		{
			category = category + data[0] + "," + data[1] + ":";
		}

		Context context = IGotItApplication.getAppContext();
		Editor editor = context.getSharedPreferences(IGotItApplication.USER_PREFS, 0).edit();
		editor.putString(IGotItApplication.USER_CATEGORIES, category);
		editor.commit();
	}

	public static ArrayList<CategoryDetail> getCategories()
	{
		ArrayList<CategoryDetail> list = new ArrayList<CategoryDetail>();
		Context context = IGotItApplication.getAppContext();
		SharedPreferences prefs = context.getSharedPreferences(IGotItApplication.USER_PREFS, 0);
		String data = prefs.getString(IGotItApplication.USER_CATEGORIES, "");
		CategoryDetail dummy = new CategoryDetail("Select Category", "-1");
		list.add(dummy);
		if (!data.equals("") && data.contains(":"))
		{
			String[] splits = data.split(":");
			for (String str : splits)
			{
				if (str.contains(","))
				{
					String[] actual = str.split(",");
					CategoryDetail detail = new CategoryDetail(actual[1], actual[0]);
					list.add(detail);
				}
			}
		}

		return list;
	}

	@Override
	public void Response(UserAccountSyncResponse response)
	{
		//Log.v("response", response.toString());
	}

	@Override
	public void Progress(EventAssetDownloadTask task, Integer progress)
	{
		//Log.v("progress", progress.toString());
	}

	@Override
	public void Response(EventAssetDownloadResponse response)
	{
		//Log.v("response", response.toString());
	}

}
