package com.bluedoorway.sma.igotiteventcapture.model;

import org.json.JSONException;
import org.json.JSONObject;

// these are details sent back from the server about the request
public class AssetServerDetail extends BaseObject
{
	// the client assigned GUID that uniquely identifies this asset
	public String assetId;
	// the server assigned GUID that uniquely identifies the asset 
	public String serverId;

	public AssetServerDetail()
	{
	}
	
	public AssetServerDetail(JSONObject object) throws JSONException
	{
		super(object);
		
		assetId = object.getString("AssetId");
		serverId = object.getString("ServerId");
	}
}