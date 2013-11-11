package com.bluedoorway.sma.igotiteventcapture.model.request;

import org.json.JSONException;
import org.json.JSONObject;

public class UserAccountSyncRequest extends Request
{
	// start record position to get the "recordCount" from
	public int startingPosition;
	// page size, fetch this number of records from the server
	public int recordCount;
	
	public UserAccountSyncRequest()
	{
	}

	@Override
	public JSONObject getJSON() 
			throws JSONException
	{
		JSONObject object = super.getJSON();
		
		object.put("StartingPosition", startingPosition);
		object.put("RecordCount", recordCount);
		
		return object;
	}
}
