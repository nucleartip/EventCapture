package com.bluedoorway.sma.igotiteventcapture;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluedoorway.sma.igotiteventcapture.model.CategoryDetail;
import com.bluedoorway.sma.igotiteventcapture.model.request.ApplicationDetailRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.ApplicationDetailResponse;
import com.bluedoorway.sma.igotiteventcapture.task.ApplicationDetailTask;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.ApplicationDetailTaskDelegate;

public class IGotItLoginActivity extends Activity implements ApplicationDetailTaskDelegate
{
	private EditText userName;
	private EditText password;
	private Context context;
	private Button login;
	private ApplicationDetailTask task;
	private ProgressDialog dialog;
	private boolean reValidating = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Setting up View
		setContentView(R.layout.activity_login_page);

		// Fetching form Objects
		context = this;
		userName = (EditText) findViewById(R.id.activity_login_username);
		password = (EditText) findViewById(R.id.activity_login_password);
		login = (Button) findViewById(R.id.activity_login_login);

		login.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (IGotItApplication.checkNetworkStatus(context) >= 0)
				{
					task = new ApplicationDetailTask();
					task.setDelegate(IGotItLoginActivity.this);
					reValidating = false;

					String userID = userName.getText().toString();
					String userPswd = password.getText().toString();
					if (userID.length() > 0 && userPswd.length() > 0)
					{
						dialog = new ProgressDialog(context);
						dialog.setMessage("Logging in...");
						dialog.setCancelable(true);
						dialog.show();
						ApplicationDetailRequest request = new ApplicationDetailRequest();
						request.user.username = userID;
						request.user.password = userPswd;

						request.applicationId = IGotItApplication.getApplicationId();
						task.execute(request);
					}
					else
						Toast.makeText(context, "Both Username and Password are mandatory ", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(context, "Please connect to a valid data network", Toast.LENGTH_SHORT).show();
			} // On Click
		});

		// Setting up environment
		File imageFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/IGotIt/Pics");
		File videoFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/IGotIt/Video");
		File audioFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/IGotIt/Audio");
		if (!imageFile.exists())
			imageFile.mkdirs();
		if (!videoFile.exists())
			videoFile.mkdirs();
		if (!audioFile.exists())
			audioFile.mkdirs();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == IGotItApplication.START_EVENT_ACTIVITY)
		{
			if (resultCode == IGotItApplication.FINISH_APP)
				finish();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// Auto loggin user
		if (IGotItApplication.hasUserDetails())
		{
			if (IGotItApplication.checkNetworkStatus(context) >= 0)
			{
				// Perform re validation
				task = new ApplicationDetailTask();
				task.setDelegate(IGotItLoginActivity.this);
				reValidating = true;
				dialog = new ProgressDialog(context);
				dialog.setMessage("Logging in...");
				dialog.setCancelable(true);
				dialog.show();

				SharedPreferences prefs = getSharedPreferences(IGotItApplication.USER_PREFS, 0);

				String userID = prefs.getString(IGotItApplication.USER_NAME, "");
				String userPswd = prefs.getString(IGotItApplication.USER_PSWD, "");
				ApplicationDetailRequest request = new ApplicationDetailRequest();
				request.user.username = userID;
				request.user.password = userPswd;
				request.applicationId = IGotItApplication.getApplicationId();
				task.execute(request);
			}
			else
			{
				// User is already logged in
				Intent intent = new Intent(context, IGotItEventsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, IGotItApplication.START_EVENT_ACTIVITY);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if (task != null && !task.isCancelled())
			task.cancel(true);
	}

	@Override
	public void Response(ApplicationDetailResponse response)
	{
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

		String userID = userName.getText().toString();
		String userPswd = password.getText().toString();
		if (response.errorCode != 1)
		{
			// Saving Categories
			int i = 0;
			ArrayList<CategoryDetail> list = response.getCategories();
			if (list != null)
			{
				String[][] categories = new String[list.size()][2];

				for (CategoryDetail detail : list)
				{
					//System.out.println("#### Categories: " + detail.name);
					String[] data = new String[] { detail.categoryId, detail.name };
					categories[i] = data;
					i = i + 1;
				}
				MainActivity.saveCategories(categories);
			}
			if (!reValidating)
				MainActivity.saveUserPreference(userID, userPswd);

			Intent intent = new Intent(context, IGotItEventsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, IGotItApplication.START_EVENT_ACTIVITY);
		}
		else
		{
			if (reValidating)
			{
				MainActivity.saveUserPreference(userID, userPswd);
				Intent intent = new Intent(context, IGotItEventsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, IGotItApplication.START_EVENT_ACTIVITY);
			}
			else
				Toast.makeText(context, response.errorMessage, Toast.LENGTH_SHORT).show();
		}
	}
}
