package com.bluedoorway.sma.igotiteventcapture.task;

import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.IGotItApplication;
import com.bluedoorway.sma.igotiteventcapture.model.request.EventAssetUploadRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.EventAssetUploadResponse;
import com.bluedoorway.sma.igotiteventcapture.network.JSONClient;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.EventAssetUploadTaskDelegate;
import android.content.Context;
import android.os.AsyncTask;

// before an Asset can be uploaded, the details of the "Event" & "Asset" must be uploaded to the server
// via the EventCapture task.
public class AssetUploadTask extends AsyncTask<EventAssetUploadRequest, Void, Object>
{
	private static String ACTION = "AssetUpload";

	Context context;
	EventAssetUploadTaskDelegate requestor;
	private String file_id;

	public AssetUploadTask(String id)
	{
		this.file_id = id;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		context = IGotItApplication.getAppContext();
	}

	@Override
	protected Object doInBackground(EventAssetUploadRequest... params)
	{
		if (requestor == null || isCancelled())
			return null;

		try
		{
			EventAssetUploadRequest p = params[0];
			JSONObject obj = JSONClient.postMultipartRequest(p, ACTION);

			EventAssetUploadResponse response = new EventAssetUploadResponse(obj, file_id);

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

		EventAssetUploadResponse response;

		if (result != null)
			response = (EventAssetUploadResponse) result;
		else
		{
			response = new EventAssetUploadResponse();
			response.errorCode = 1;
			response.errorMessage = "Unable to retrieve results from server";
		}

		requestor.Response(response);
	}

	public void setDelegate(EventAssetUploadTaskDelegate requestor)
	{
		this.requestor = requestor;
	}

	public void clearDelegate()
	{
		this.requestor = null;
	}
}
