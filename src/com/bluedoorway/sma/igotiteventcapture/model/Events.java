package com.bluedoorway.sma.igotiteventcapture.model;

import java.util.ArrayList;

public class Events
{
	private String EventName;
	private int EventID;
	private String Count;
	private String EventCategory;
	private String EventCategoryID;
	private String EventComments;
	private long timeStamp;
	private long updated;
	private boolean syncStatus;
	private String EventGUID;
	private String serverID;

	public String getServerID()
	{
		return serverID;
	}

	public void setServerID(String serverID)
	{
		this.serverID = serverID;
	}

	private ArrayList<FileDetail> fileList;

	public String getEventGUID()
	{
		return EventGUID;
	}

	public void setEventGUID(String eventGUID)
	{
		EventGUID = eventGUID;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public int getEventID()
	{
		return EventID;
	}

	public void setUpdated(long timeStamp)
	{
		this.updated = timeStamp;
	}

	public long getUpdated()
	{
		return this.updated;
	}

	public void setEventID(int eventID)
	{
		EventID = eventID;
	}

	public String getEventComments()
	{
		return EventComments;
	}

	public void setEventComments(String eventComments)
	{
		EventComments = eventComments;
	}

	public String getEventCategory()
	{
		return EventCategory;
	}

	public void setEventCategory(String eventCategory)
	{
		EventCategory = eventCategory;
	}

	public ArrayList<FileDetail> getFileList()
	{
		return fileList;
	}

	public void setFileList(ArrayList<FileDetail> fileList)
	{
		this.fileList = fileList;
	}

	public String getEventName()
	{
		return EventName;
	}

	public void setEventName(String eventName)
	{
		EventName = eventName;
	}

	public String getCount()
	{
		return Count;
	}

	public void setCount(String count)
	{
		Count = count;
	}

	public String getEventCategoryID()
	{
		return EventCategoryID;
	}

	public void setEventCategoryID(String eventCategoryID)
	{
		EventCategoryID = eventCategoryID;
	}

	public boolean isSyncStatus()
	{
		return syncStatus;
	}

	public void setSyncStatus(boolean syncStatus)
	{
		this.syncStatus = syncStatus;
	}
}
