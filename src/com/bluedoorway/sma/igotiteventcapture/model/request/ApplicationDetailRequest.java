package com.bluedoorway.sma.igotiteventcapture.model.request;

import org.json.JSONException;
import org.json.JSONObject;

public class ApplicationDetailRequest extends Request
{
	public int applicationId;
	
	public ApplicationDetailRequest()
	{
	}

	@Override
	public JSONObject getJSON() 
			throws JSONException
	{
		JSONObject object = super.getJSON();
		
		object.put("ApplicationId", applicationId);
		
		return object;
	}
}
