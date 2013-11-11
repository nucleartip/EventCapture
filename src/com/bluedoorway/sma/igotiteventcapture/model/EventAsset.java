package com.bluedoorway.sma.igotiteventcapture.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

// represents the "Asset" that the user has captured; e.g., video, audio, photo
public class EventAsset extends BaseObject
{
	// the client assigned GUID that uniquely identifies this asset
	public String assetId;
	// the server assigned GUID that uniquely identifies the asset 
	// do not set this property on creation of an asset
	// not that it can be the same as the assetId if so determine by the server
	public String serverId;
	// create date of the asset, only assign this once upon creation
	public Date creation;
	// update date, if this record's content is modified update this date
	public Date updated;
	public Double longitude;
	public Double latitude;
	// this is expected to be the mime type of the asset
	// legal values right now, "video/quicktime", "image/jpeg", "audio/x-caf"
	// contact me if we need to add additional support for java
	public String contentType;
	
	// not currently used, ignore
	public String title = null;
	public String locationAccuracy = null;
	
	public EventAsset()
	{
	}
	
	public EventAsset(JSONObject object) throws JSONException
	{
		super(object);
		
		assetId = object.getString("AssetId");
		serverId = object.getString("ServerId");
		title = object.getString("Title");
		
		creation = dateFromString(object.getString("Creation"));
		updated = dateFromString(object.getString("Updated"));
		
		locationAccuracy = object.getString("LocationAccuracy");
		longitude = object.getDouble("Longitude");
		latitude = object.getDouble("Latitude");
		contentType = object.getString("ContentType");
	}
	
	@Override
	public JSONObject getJSON() throws JSONException
	{
		JSONObject object = super.getJSON();
		
		object.put("AssetId", assetId);
		object.put("ServerId", serverId);
		object.put("Title", title);
		
		object.put("Creation", dateToString(creation));
		object.put("Updated", dateToString(updated));
		
		object.put("LocationAccuracy", locationAccuracy);
		object.put("Longitude", longitude);
		object.put("Latitude", latitude);
		object.put("ContentType", contentType);
		
		return object;
	}

}
