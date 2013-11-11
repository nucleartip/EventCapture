package com.bluedoorway.sma.igotiteventcapture.model.request;

import org.json.JSONException;
import org.json.JSONObject;

public class EventAssetUploadRequest extends Request
{
	// see EventAsset for a description of these first three fields.
	public String assetId;
	public String serverId;
	public String contentType;
	// total length of the file being uploaded
	public long dataLength;
	
	// future use
	public int startingByte;
	public boolean multipartUpload;
	public boolean lastPart;
	
	public String filePath;
	
	public EventAssetUploadRequest()
	{
		// future capability, ignore for now!
		startingByte = 0;
		lastPart = true;
		multipartUpload = false;
	}

	@Override
	public JSONObject getJSON() 
			throws JSONException
	{
		JSONObject object = super.getJSON();
		
		object.put("AssetId", assetId);
		object.put("ServerId", serverId);
		object.put("ContentType", contentType);
		object.put("StartingByte", startingByte);
		object.put("DataLength", dataLength);
		object.put("MultipartUpload", multipartUpload);
		object.put("LastPart", lastPart);
		
		return object;
	}	
}
