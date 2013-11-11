package com.bluedoorway.sma.igotiteventcapture.model.response;

import org.json.JSONException;
import org.json.JSONObject;

public class Response
{
	public boolean successful;
	public int errorCode;
	public String errorMessage; 
	
	public Response()
	{
	}
	
	public Response(JSONObject object) throws JSONException
	{
		successful = object.getBoolean("Successful");
		errorCode = object.getInt("ErrorCode");
		errorMessage = object.optString("ErrorMessage");
	}
}
