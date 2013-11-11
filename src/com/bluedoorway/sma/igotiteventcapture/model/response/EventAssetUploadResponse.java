package com.bluedoorway.sma.igotiteventcapture.model.response;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.model.EventAssetMissingPartDetail;

public class EventAssetUploadResponse extends Response
{
	String assetId;
    public String file_id;
    
	// future use, ignore for now!
	ArrayList<EventAssetMissingPartDetail> missingParts;

	public EventAssetUploadResponse()
	{
	}

	public EventAssetUploadResponse(JSONObject object,String id) throws JSONException
	{
		super(object);

		missingParts = new ArrayList<EventAssetMissingPartDetail>();

		assetId = object.getString("AssetId");
		this.file_id = id;
		if(!object.isNull("MissingParts")){
			JSONArray array = object.optJSONArray("MissingParts");

			if (array == null)
				return;

			for (int i = 0; i < array.length(); i++)
			{
				JSONObject item = array.getJSONObject(i);
				EventAssetMissingPartDetail detail = new EventAssetMissingPartDetail(item);
				missingParts.add(detail);
			}
		}
	}
}
