package com.bluedoorway.sma.igotiteventcapture.model.request;

import org.json.JSONException;
import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.model.BaseObject;
import com.bluedoorway.sma.igotiteventcapture.model.Identity;

public class Request extends BaseObject
{
	public Identity user;
	
	public Request()
	{
		user = new Identity();
	}
	
	@Override
	public JSONObject getJSON()
	 throws JSONException
	{
		JSONObject object = super.getJSON();
		
		object.put("User", user.getJSON());
		
		return object;
	}
}
