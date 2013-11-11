package com.bluedoorway.sma.igotiteventcapture.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserRecord extends BaseObject
{
	public EventCapture eventCapture;
	public ArrayList<EventAsset> assets;
	
	public UserRecord()
	{
	}

	public UserRecord(JSONObject object) throws JSONException
	{
		super(object);
		
		assets = new ArrayList<EventAsset>();
		
		JSONObject eventObj = object.getJSONObject("EventCapture");
		eventCapture = new EventCapture(eventObj);
		
		if(object.isNull("Assets") == false)
		{
			JSONArray array = object.getJSONArray("Assets");
			if(array != null)
			{
				for (int i = 0; i < array.length(); i++)
				{
					JSONObject item = array.getJSONObject(i);
					EventAsset detail = new EventAsset(item);
					assets.add(detail);
				}
			}
		}	
	}
}