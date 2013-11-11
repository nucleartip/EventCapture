package com.bluedoorway.sma.igotiteventcapture.model;

public class FileDetail
{
	public static final int TYPE_VIDEO = 0;
	public static final int TYPE_AUDIO = 1;
	public static final int TYPE_IMAGE = 2;

	private String FileName;
	private int FileType;

	public String getFileName()
	{
		return FileName;
	}

	public void setFileName(String fileName)
	{
		FileName = fileName;
	}

	public int getFileType()
	{
		return FileType;
	}

	public void setFileType(int fileType)
	{
		FileType = fileType;
	}

}
