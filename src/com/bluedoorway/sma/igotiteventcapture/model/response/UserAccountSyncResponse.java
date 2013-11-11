package com.bluedoorway.sma.igotiteventcapture.model.response;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.model.UserRecord;

public class UserAccountSyncResponse extends Response
{
	public int startingPosition;
	public int recordCount;
	public int totalRecordCount;
	public ArrayList<UserRecord> records;

	public UserAccountSyncResponse()
	{
	}

	public UserAccountSyncResponse(JSONObject object) throws JSONException
	{
		super(object);

		records = new ArrayList<UserRecord>();

		startingPosition = object.optInt("StartPosition");
		recordCount = object.optInt("RecordCount");
		totalRecordCount = object.optInt("TotalRecordCount");

		JSONArray array = object.getJSONArray("Records");

		for (int i = 0; i < array.length(); i++)
		{
			JSONObject item = array.getJSONObject(i);
			UserRecord detail = new UserRecord(item);
			records.add(detail);
		}
	}

	public ArrayList<UserRecord> getCategories()
	{
		return records;
	}
}
