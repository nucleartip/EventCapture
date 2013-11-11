package com.bluedoorway.sma.igotiteventcapture.task;

import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.IGotItApplication;
import com.bluedoorway.sma.igotiteventcapture.model.request.UserAccountSyncRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.UserAccountSyncResponse;
import com.bluedoorway.sma.igotiteventcapture.network.JSONClient;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.UserAccountSyncTaskDelegate;

import android.content.Context;
import android.os.AsyncTask;

public class UserAccountSyncTask extends AsyncTask<UserAccountSyncRequest, Void, Object>
{
	private static String ACTION = "UserSync";

	Context context;
	UserAccountSyncTaskDelegate requestor;

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		context = IGotItApplication.getAppContext();
	}

	@Override
	protected Object doInBackground(UserAccountSyncRequest... params)
	{
		if (requestor == null || isCancelled())
			return null;

		try
		{
			UserAccountSyncRequest p = params[0];
			JSONObject obj = JSONClient.postRequest(p, ACTION);

			UserAccountSyncResponse response = new UserAccountSyncResponse(obj);

			return response;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Object result)
	{
		super.onPostExecute(result);

		if (requestor == null || isCancelled())
			return;

		UserAccountSyncResponse response;

		if (result != null)
			response = (UserAccountSyncResponse) result;
		else
		{
			response = new UserAccountSyncResponse();
			response.errorCode = 1;
			response.errorMessage = "Unable to retrieve results from server";
		}

		requestor.Response(response);
	}

	public void setDelegate(UserAccountSyncTaskDelegate requestor)
	{
		this.requestor = requestor;
	}

	public void clearDelegate()
	{
		this.requestor = null;
	}
}
