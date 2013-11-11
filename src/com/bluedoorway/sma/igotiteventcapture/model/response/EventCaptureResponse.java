package com.bluedoorway.sma.igotiteventcapture.model.response;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.model.AssetServerDetail;

public class EventCaptureResponse extends Response
{
	String eventId;
	public String serverId;
	public int clientEventId = -1;
	public ArrayList<AssetServerDetail> assetIds;

	public EventCaptureResponse()
	{
	}

	public EventCaptureResponse(JSONObject object, int clientId) throws JSONException
	{
		super(object);

		assetIds = new ArrayList<AssetServerDetail>();
		eventId = object.getString("EventId");
		clientEventId = clientId;
		serverId = object.getString("ServerId");
		if (!object.isNull("AssetIds"))
		{
			JSONArray array = object.getJSONArray("AssetIds");
			if (array != null)
			{
				for (int i = 0; i < array.length(); i++)
				{
					JSONObject item = array.getJSONObject(i);
					AssetServerDetail detail = new AssetServerDetail(item);
					assetIds.add(detail);
				}
			}
		}
	}
}
