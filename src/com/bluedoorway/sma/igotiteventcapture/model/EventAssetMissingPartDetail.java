package com.bluedoorway.sma.igotiteventcapture.model;

import org.json.JSONException;
import org.json.JSONObject;

// these are details sent back from the server about the request
public class EventAssetMissingPartDetail extends BaseObject
{
	//future use case, ignore for now!
	
	// the byte where a gap in the data starts
	public int startingByte;
	// the length of the gap in the data
	public int dataLength;

	public EventAssetMissingPartDetail()
	{
	}
	
	public EventAssetMissingPartDetail(JSONObject object) throws JSONException
	{
		super(object);
		
		startingByte = object.getInt("StartingByte");
		dataLength = object.getInt("DataLength");
	}
}
