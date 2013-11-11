package com.bluedoorway.sma.igotiteventcapture.task;

import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.IGotItApplication;
import com.bluedoorway.sma.igotiteventcapture.model.request.ApplicationDetailRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.ApplicationDetailResponse;
import com.bluedoorway.sma.igotiteventcapture.network.JSONClient;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.ApplicationDetailTaskDelegate;

import android.content.Context;
import android.os.AsyncTask;


public class ApplicationDetailTask extends AsyncTask<ApplicationDetailRequest, Void, Object>
{
	private static String ACTION = "ApplicationDetail";
	
	Context context;
	ApplicationDetailTaskDelegate requestor;

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		context = IGotItApplication.getAppContext();
	}

	@Override
	protected Object doInBackground(ApplicationDetailRequest... params)
	{
		if (requestor == null || isCancelled())
			return null;
		
		try
		{
			ApplicationDetailRequest p = params[0];
			JSONObject obj = JSONClient.postRequest(p, ACTION);
			
			ApplicationDetailResponse response = new ApplicationDetailResponse(obj);
			
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
		
		ApplicationDetailResponse response;
		
		if (result != null)
			response = (ApplicationDetailResponse)result;
		else
		{
			response = new ApplicationDetailResponse();
			response.errorCode = 1;
			response.errorMessage = "Unable to retrieve results from server";
		}

		requestor.Response(response);
	}

	public void setDelegate(ApplicationDetailTaskDelegate requestor)
	{
		this.requestor = requestor;
	}

	public void clearDelegate()
	{
		this.requestor = null;
	}
}
