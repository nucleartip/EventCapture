package com.bluedoorway.sma.igotiteventcapture.model.response;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bluedoorway.sma.igotiteventcapture.model.CategoryDetail;

public class ApplicationDetailResponse extends Response
{
	String uploadServer;
	ArrayList<CategoryDetail> categories;

	public ApplicationDetailResponse()
	{
	}

	public ApplicationDetailResponse(JSONObject object) throws JSONException
	{
		super(object);

		categories = new ArrayList<CategoryDetail>();
		uploadServer = object.optString("UploadServer");
		JSONArray array = object.getJSONArray("Categories");

		for (int i = 0; i < array.length(); i++)
		{
			JSONObject item = array.getJSONObject(i);
			CategoryDetail detail = new CategoryDetail(item);
			categories.add(detail);
		}
	}

	public ArrayList<CategoryDetail> getCategories()
	{
		return categories;
	}
}
