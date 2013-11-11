package com.bluedoorway.sma.igotiteventcapture.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.model.request.EventAssetUploadRequest;
import com.bluedoorway.sma.igotiteventcapture.model.request.Request;

public class JSONClient
{
	static String IGotItHTTPClientBaseURLString = "https://api.imagekeeper.com:5038/";

	public static JSONObject postRequest(Request request, String action) throws Exception
	{
		HttpEntity entity = executePostRequest(request, action);

		InputStream inputStream = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		StringBuilder sb = new StringBuilder();

		String line = null;
		while ((line = reader.readLine()) != null)
			sb.append(line);

		return new JSONObject(sb.toString());
	}

	public static DefaultHttpClient getHttpClient()
	{
		DefaultHttpClient client;
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		client = new DefaultHttpClient(httpParameters);
		return client;
	}

	public static HttpEntity executePostRequest(Request request, String action) throws Exception
	{
		// JSON Data
		JSONObject data = request.getJSON();
		StringEntity requestEntity = new StringEntity(data.toString());
		requestEntity.setContentEncoding("UTF-8");

		// SEND Data
		DefaultHttpClient httpclient = getHttpClient();
		HttpPost httppost = new HttpPost(IGotItHTTPClientBaseURLString + action);
		httppost.setHeader("Content-type", "application/json");
		httppost.setHeader("Accept", "application/json");
		httppost.setEntity(requestEntity);

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		return entity;
	}

	public static JSONObject postMultipartRequest(EventAssetUploadRequest request, String action) throws Exception
	{
		MultipartEntity requestEntity = new MultipartEntity();

		// JSON Part
		JSONObject data = request.getJSON();
		requestEntity.addPart("EventAssetUploadRequest",
				new StringBody(data.toString(), "application/json", Charset.forName("UTF-8")));

		// FILE Part
		File fileToUpload = new File(request.filePath);
		FileBody fileBody = new FileBody(fileToUpload, "multipart/form-data");
		requestEntity.addPart("AssetData", fileBody);

		// SEND Data
		DefaultHttpClient httpclient = getHttpClient();
		HttpPost httppost = new HttpPost(IGotItHTTPClientBaseURLString + action);
		httppost.setHeader("Accept", "application/json");
		httppost.setEntity(requestEntity);

		InputStream inputStream = null;
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		inputStream = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		StringBuilder sb = new StringBuilder();

		String line = null;
		while ((line = reader.readLine()) != null)
			sb.append(line);

		return new JSONObject(sb.toString());
	}
}
