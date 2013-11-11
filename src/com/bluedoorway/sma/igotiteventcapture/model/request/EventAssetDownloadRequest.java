package com.bluedoorway.sma.igotiteventcapture.model.request;

import org.json.JSONException;
import org.json.JSONObject;

public class EventAssetDownloadRequest extends Request
{
	public String eventServerId;
	public String assetServerId;
	
	public EventAssetDownloadRequest(String eventServerId, String assetServerId)
	{
		this.assetServerId = assetServerId;
		this.eventServerId= eventServerId;
	}

	@Override
	public JSONObject getJSON() 
			throws JSONException
	{
		JSONObject object = super.getJSON();
		
		object.put("EventServerId", eventServerId);
		object.put("AssetServerId", assetServerId);
		
		return object;
	}
}
