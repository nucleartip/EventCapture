package com.bluedoorway.sma.igotiteventcapture.model;

public class SavedCaptures
{
	private String captureType;
	private String fileName;
	private int ID;
	private double Longitude = 0;
	private double Latitude = 0;
	private boolean UploadStatus;
	private String assetServerId;
	private String eventServerId;

	public int getID()
	{
		return ID;
	}

	public void setID(int iD)
	{
		ID = iD;
	}

	public String getCaptureType()
	{
		return captureType;
	}

	public void setCaptureType(String captureType)
	{
		this.captureType = captureType;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public double getLongitude()
	{
		return Longitude;
	}

	public void setLongitude(double longitude)
	{
		Longitude = longitude;
	}

	public double getLatitude()
	{
		return Latitude;
	}

	public void setLatitude(double latitude)
	{
		Latitude = latitude;
	}

	public boolean getUploadStatus()
	{
		return UploadStatus;
	}

	public void setUploadStatus(boolean uploadStatus)
	{
		UploadStatus = uploadStatus;
	}

	public String getAssetServerId()
	{
		return assetServerId;
	}

	public void setAssetServerId(String assetServerId)
	{
		this.assetServerId = assetServerId;
	}

	public String getEventServerId()
	{
		return eventServerId;
	}

	public void setEventServerId(String eventServerId)
	{
		this.eventServerId = eventServerId;
	}

}
