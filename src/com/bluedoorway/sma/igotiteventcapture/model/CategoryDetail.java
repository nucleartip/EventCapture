package com.bluedoorway.sma.igotiteventcapture.model;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryDetail extends BaseObject
{
	// server id that identifies this category
	public String categoryId;
	// display name of the category
	public String name;

	public CategoryDetail()
	{
	}

	public CategoryDetail(String name, String categoryId)
	{
		this.name = name;
		this.categoryId = categoryId;
	}

	public CategoryDetail(JSONObject object) throws JSONException
	{
		super(object);

		categoryId = object.getString("Id");
		name = object.getString("Name");
	}

	@Override
	public String toString()
	{
		return name;
	}
}
