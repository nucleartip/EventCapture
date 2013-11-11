package com.bluedoorway.sma.igotiteventcapture.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

// represents the "Event" the user is capturing.
public class EventCapture extends BaseObject
{
	// client/app assigned GUID that uniquely identifies this event
	public String eventId;
	// user entered title
	public String title;
	// user selected type 'name', see Category Detail
	public String type;
	// user selected type 'id', see Category Detail
	public String typeId;
	// user entere
	public String comment;
	// the server assigned GUID that uniquely identifies the event 
	// do not set this property on creation of an event
	// not that it can be the same as the eventId if so determine by the server
	public String serverId;
	// create date of the asset, only assign this once upon creation
	public Date creation;
	// update date, if this record's content is modified update this date
	public Date updated;

	public EventCapture()
	{
	}
	
	public EventCapture(JSONObject object) throws JSONException
	{
		super(object);
		
		eventId = object.getString("EventId");
		title = object.getString("Title");
		type = object.getString("Type");
		typeId = object.getString("TypeId");
		comment = object.optString("Comment");
		serverId = object.optString("ServerId");
		
		creation = dateFromString(object.getString("Creation"));
		updated = dateFromString(object.getString("Updated"));
	}
	
	@Override
	public JSONObject getJSON() throws JSONException
	{
		JSONObject object = super.getJSON();
		
		object.put("EventId", eventId);
		object.put("Title", title);
		object.put("Type", type);
		object.put("TypeId", typeId);
		object.put("Comment", comment);
		object.put("ServerId", serverId);
		object.put("Creation", dateToString(creation));
		object.put("Updated", dateToString(updated));
		
		return object;
	}
}
