package com.bluedoorway.sma.igotiteventcapture.model.request;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.model.EventAsset;
import com.bluedoorway.sma.igotiteventcapture.model.EventCapture;

public class EventCaptureRequest extends Request
{
	public EventCapture capture;
	public ArrayList<EventAsset> assets;
	
	public EventCaptureRequest()
	{
	}

	@Override
	public JSONObject getJSON() 
			throws JSONException
	{
		
		JSONObject object = super.getJSON();
		object.put("EventCapture", capture.getJSON());
		JSONArray array = new JSONArray();
		for (int i = 0; i < assets.size(); i++)
		{
			EventAsset item = assets.get(i);
			array.put(item.getJSON());
		}
		object.put("Assets", array);
		
		return object;
	}
}
