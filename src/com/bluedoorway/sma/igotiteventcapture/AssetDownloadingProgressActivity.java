package com.bluedoorway.sma.igotiteventcapture;

import java.io.File;

import com.bluedoorway.sma.igotiteventcapture.model.request.EventAssetDownloadRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.EventAssetDownloadResponse;
import com.bluedoorway.sma.igotiteventcapture.task.EventAssetDownloadTask;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.EventAssetDownloadTaskDelegate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AssetDownloadingProgressActivity extends Activity implements EventAssetDownloadTaskDelegate
{
	private String assetServerId;
	private String eventServerId;
	private ProgressDialog dialog;
	private ProgressBar progressBarPercentage;
	private String filePath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_asset_download_progress);

		progressBarPercentage = (ProgressBar) findViewById(R.id.progressBarPercentage);

		if (getIntent().getExtras() != null)
		{

			dialog = new ProgressDialog(this);
			dialog.setMessage("Downloading...");
			dialog.setCancelable(false);
			dialog.show();
			assetServerId = getIntent().getExtras().getString("assetserverid");
			eventServerId = getIntent().getExtras().getString("eventserverid");
			filePath = getIntent().getExtras().getString("filepath");
			assetDownload(eventServerId, assetServerId, filePath);
		}
	}

	public void assetDownload(String eventServerID, String assetServerId, String fileAbsPath)
	{
		EventAssetDownloadRequest request = new EventAssetDownloadRequest(eventServerID, assetServerId);

		request.user.username = IGotItApplication.getUserName();
		request.user.password = IGotItApplication.getUserPswd();

		File f = new File(fileAbsPath);

		if (f.exists() == false)
		{
			EventAssetDownloadTask task = new EventAssetDownloadTask();
			task.setFilename(fileAbsPath);
			task.setDelegate(this);
			task.execute(request);
		}
	}

	@Override
	public void Progress(EventAssetDownloadTask task, Integer progress)
	{
		progressBarPercentage.setProgress(progress);
	}

	@Override
	public void Response(EventAssetDownloadResponse response)
	{
		if (response.errorCode == 0)
		{
			dialog.dismiss();
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			if (filePath.contains("Pics"))
				intent.setDataAndType(Uri.parse("file://" + filePath), "image/*");
			else if (filePath.contains("Audio"))
				intent.setDataAndType(Uri.parse("file://" + filePath), "audio/*");
			else if (filePath.contains("Video"))
				intent.setDataAndType(Uri.parse("file://" + filePath), "video/*");
			startActivity(intent);
			AssetDownloadingProgressActivity.this.finish();
		}
		else
		{
			dialog.dismiss();
			Toast.makeText(AssetDownloadingProgressActivity.this, "Could not download the file. Please try again.",
					Toast.LENGTH_SHORT).show();
			AssetDownloadingProgressActivity.this.finish();
		}
	}
}
