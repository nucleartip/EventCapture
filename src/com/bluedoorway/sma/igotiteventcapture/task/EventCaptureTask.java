package com.bluedoorway.sma.igotiteventcapture.task;

import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.IGotItApplication;
import com.bluedoorway.sma.igotiteventcapture.model.request.EventCaptureRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.EventCaptureResponse;
import com.bluedoorway.sma.igotiteventcapture.network.JSONClient;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.EventCaptureTaskDelegate;

import android.content.Context;
import android.os.AsyncTask;

// used to upload detail to the server that describe the "Event" and its "Assets"
public class EventCaptureTask extends AsyncTask<EventCaptureRequest, Void, Object>
{
	private static String ACTION = "EventCapture";

	Context context;
	EventCaptureTaskDelegate requestor;
	int clientID;

	public EventCaptureTask()
	{
		// TODO Auto-generated constructor stub
	}

	public EventCaptureTask(int eventId)
	{
		clientID = eventId;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		context = IGotItApplication.getAppContext();
	}

	@Override
	protected Object doInBackground(EventCaptureRequest... params)
	{
		if (requestor == null || isCancelled())
			return null;

		try
		{
			EventCaptureRequest p = params[0];
			JSONObject obj = JSONClient.postRequest(p, ACTION);

			EventCaptureResponse response = new EventCaptureResponse(obj, clientID);

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

		EventCaptureResponse response;

		if (result != null)
			response = (EventCaptureResponse) result;
		else
		{
			response = new EventCaptureResponse();
			response.errorCode = 1;
			response.errorMessage = "Unable to retrieve results from server";
		}

		requestor.Response(response);
	}

	public void setDelegate(EventCaptureTaskDelegate requestor)
	{
		this.requestor = requestor;
	}

	public void clearDelegate()
	{
		this.requestor = null;
	}
}
