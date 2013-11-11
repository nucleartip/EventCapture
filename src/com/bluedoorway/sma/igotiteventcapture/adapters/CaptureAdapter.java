package com.bluedoorway.sma.igotiteventcapture.adapters;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluedoorway.sma.igotiteventcapture.AssetDownloadingProgressActivity;
import com.bluedoorway.sma.igotiteventcapture.MapActivity;
import com.bluedoorway.sma.igotiteventcapture.R;
import com.bluedoorway.sma.igotiteventcapture.model.SavedCaptures;
import com.bluedoorway.sma.igotiteventcapture.model.response.EventAssetDownloadResponse;
import com.bluedoorway.sma.igotiteventcapture.task.EventAssetDownloadTask;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.EventAssetDownloadTaskDelegate;

@SuppressLint("SimpleDateFormat")
public class CaptureAdapter extends BaseAdapter implements EventAssetDownloadTaskDelegate
{
	private ImageView eventType;
	private TextView fileName;
	private ImageView location;
	private ImageView checkMark;
	private ArrayList<SavedCaptures> list;
	private Context context;
	private ViewGroup holder;

	public CaptureAdapter(Context context, ArrayList<SavedCaptures> list, int eventId)
	{
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View reuse, ViewGroup parent)
	{
		if (reuse != null)
		{
			eventType = (ImageView) reuse.findViewById(R.id.capture_list_cell_image_type);
			fileName = (TextView) reuse.findViewById(R.id.capture_list_cell_name);
			location = (ImageView) reuse.findViewById(R.id.capture_list_cell_image_location);
			checkMark = (ImageView) reuse.findViewById(R.id.capture_list_cell_image_checkmark);
			holder = (ViewGroup) reuse.findViewById(R.id.capture_list_cell_holder);
			final SavedCaptures obj = list.get(position);
			eventType.setBackgroundResource(setEventType(obj.getCaptureType()));
			fileName.setText(getFileName(obj.getFileName()));

			holder.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					SavedCaptures savedCap = obj;

					File file = new File(savedCap.getFileName());
					if (file.exists())
					{
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						if (savedCap.getCaptureType().equals("image/jpeg"))
							intent.setDataAndType(Uri.parse("file://" + savedCap.getFileName()), "image/*");
						else if (savedCap.getCaptureType().equals("audio/x-caf"))
							intent.setDataAndType(Uri.parse("file://" + savedCap.getFileName()), "audio/*");
						else if (savedCap.getCaptureType().equals("video/quicktime"))
							intent.setDataAndType(Uri.parse("file://" + savedCap.getFileName()), "video/*");
						context.startActivity(intent);
					}
					else
					{
						Intent intentDownload = new Intent(context, AssetDownloadingProgressActivity.class);
						Bundle b = new Bundle();
						b.putString("eventserverid", savedCap.getEventServerId());
						b.putString("assetserverid", savedCap.getAssetServerId());
						b.putString("filepath", file.getAbsolutePath());

						intentDownload.putExtras(b);
						context.startActivity(intentDownload);
					}
				}
			});
			if (obj.getLatitude() > 0)
				location.setImageResource(R.drawable.location_green);
			else
			{
				location.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						SavedCaptures savedCap = obj;

						Intent intent = new Intent(context, MapActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("file_id", savedCap.getID());
						context.startActivity(intent);
					}

				});
			}

			if (obj.getUploadStatus())
				checkMark.setImageResource(R.drawable.checkmark_green);

			return reuse;
		}
		else
		{
			ViewGroup item = getViewGroup(reuse, parent);

			eventType = (ImageView) item.findViewById(R.id.capture_list_cell_image_type);
			fileName = (TextView) item.findViewById(R.id.capture_list_cell_name);
			location = (ImageView) item.findViewById(R.id.capture_list_cell_image_location);
			checkMark = (ImageView) item.findViewById(R.id.capture_list_cell_image_checkmark);
			holder = (ViewGroup) item.findViewById(R.id.capture_list_cell_holder);
			final SavedCaptures obj = list.get(position);
			eventType.setBackgroundResource(setEventType(obj.getCaptureType()));
			fileName.setText(getFileName(obj.getFileName()));

			holder.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					SavedCaptures savedCap = obj;

					File file = new File(savedCap.getFileName());

					if (file.exists())
					{
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						if (savedCap.getCaptureType().equals("image/jpeg"))
							intent.setDataAndType(Uri.parse("file://" + savedCap.getFileName()), "image/*");
						else if (savedCap.getCaptureType().equals("audio/x-caf"))
							intent.setDataAndType(Uri.parse("file://" + savedCap.getFileName()), "audio/*");
						else if (savedCap.getCaptureType().equals("video/quicktime"))
							intent.setDataAndType(Uri.parse("file://" + savedCap.getFileName()), "video/*");
						context.startActivity(intent);
					}
					else
					{
						Intent intentDownload = new Intent(context, AssetDownloadingProgressActivity.class);
						Bundle b = new Bundle();
						b.putString("eventserverid", savedCap.getEventServerId());
						b.putString("assetserverid", savedCap.getAssetServerId());
						b.putString("filepath", file.getAbsolutePath());

						intentDownload.putExtras(b);
						context.startActivity(intentDownload);
					}
				}
			});

			if (obj.getLatitude() > 0)
				location.setImageResource(R.drawable.location_green);
			else
			{
				location.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						SavedCaptures savedCap = obj;

						Intent intent = new Intent(context, MapActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("file_id", savedCap.getID());
						context.startActivity(intent);
					}
				});
			}
			if (obj.getUploadStatus())
				checkMark.setImageResource(R.drawable.checkmark_green);
			return item;
		}
	}

	String getFileName(String fileName)
	{
		String ret = "";
		try
		{
			String fName[] = fileName.split("/");
			ret = fName[fName.length - 1].toString();
		}
		catch (Exception e)
		{
		}
		return ret;
	}

	int setEventType(String evenType)
	{
		if (evenType.equals("image/jpeg"))
			return R.drawable.play;
		else if (evenType.equals("video/quicktime"))
			return R.drawable.video;
		else if (evenType.equals("audio/x-caf"))
			return R.drawable.microphone;

		return R.drawable.play;
	}

	private ViewGroup getViewGroup(View reuse, ViewGroup parent)
	{
		if (reuse instanceof ViewGroup)
			return (ViewGroup) reuse;
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup item = (ViewGroup) inflater.inflate(R.layout.capture_list_cell, null);

		return item;
	}

	@Override
	public void Response(EventAssetDownloadResponse response)
	{
	}

	@Override
	public void Progress(EventAssetDownloadTask task, Integer progress)
	{	
	}
}
