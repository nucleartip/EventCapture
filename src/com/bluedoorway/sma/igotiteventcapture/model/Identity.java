package com.bluedoorway.sma.igotiteventcapture.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Identity extends BaseObject
{
	public String username;
	public String password;
	
	// not currently used, ignore!
	public String domain;
	public String deviceId;
	
	public Identity()
	{
	}

	@Override
	public JSONObject getJSON() throws JSONException
	{
		JSONObject object = super.getJSON();
		
		object.put("Username", username);
		object.put("Password", password);
		object.put("Domain", domain);
		object.put("DeviceId", deviceId);
		
		return object;
	}
}
