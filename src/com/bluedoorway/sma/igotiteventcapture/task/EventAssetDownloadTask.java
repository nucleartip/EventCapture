package com.bluedoorway.sma.igotiteventcapture.task;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import com.bluedoorway.sma.igotiteventcapture.IGotItApplication;
import com.bluedoorway.sma.igotiteventcapture.model.request.EventAssetDownloadRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.EventAssetDownloadResponse;
import com.bluedoorway.sma.igotiteventcapture.network.JSONClient;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.EventAssetDownloadTaskDelegate;


import android.content.Context;
import android.os.AsyncTask;

public class EventAssetDownloadTask extends AsyncTask<EventAssetDownloadRequest, Integer, Object>
{
	private static String ACTION = "AssetDownload";

	Context context;
	String filename;
	EventAssetDownloadTaskDelegate requestor;

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		context = IGotItApplication.getAppContext();
	}

	@Override
	protected Object doInBackground(EventAssetDownloadRequest... params)
	{
		if (requestor == null || isCancelled())
			return null;

		try
		{
			EventAssetDownloadRequest p = params[0];
			HttpEntity entity = JSONClient.executePostRequest(p, ACTION);

			int count;
			long contentLength = entity.getContentLength();
			if (contentLength == 0)
				return null;
			
			InputStream input = new BufferedInputStream(entity.getContent());
			FileOutputStream output = new FileOutputStream(filename);

			byte data[] = new byte[1024];

			long total = 0;
			while ((count = input.read(data)) != -1)
			{
				total += count;
				publishProgress((int) ((total * 100) / contentLength));
				output.write(data, 0, count);
				
	             if (isCancelled())
	            	 break;
			}

			output.flush();
			output.close();
			input.close();

			EventAssetDownloadResponse response = new EventAssetDownloadResponse();
			response.filename = filename;
			return response;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress)
	{
		super.onProgressUpdate(progress);
		
		if (requestor == null || isCancelled())
			return;

		requestor.Progress(this, progress[0]);
	}

	@Override
	protected void onPostExecute(Object result)
	{
		super.onPostExecute(result);

		if (requestor == null || isCancelled())
			return;

		EventAssetDownloadResponse response;

		if (result != null)
			response = (EventAssetDownloadResponse) result;
		else
		{
			response = new EventAssetDownloadResponse();
			response.errorCode = 1;
			response.errorMessage = "Unable to retrieve results from server";
		}

		requestor.Response(response);
	}

	public void setDelegate(EventAssetDownloadTaskDelegate requestor)
	{
		this.requestor = requestor;
	}

	public void clearDelegate()
	{
		this.requestor = null;
	}
	
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
}
