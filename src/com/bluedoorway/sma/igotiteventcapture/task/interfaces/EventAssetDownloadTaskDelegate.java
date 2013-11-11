package com.bluedoorway.sma.igotiteventcapture.task.interfaces;

import com.bluedoorway.sma.igotiteventcapture.model.response.EventAssetDownloadResponse;
import com.bluedoorway.sma.igotiteventcapture.task.EventAssetDownloadTask;

public interface EventAssetDownloadTaskDelegate
{
	public void Progress(EventAssetDownloadTask task, Integer progress);
	public void Response(EventAssetDownloadResponse response);
}
