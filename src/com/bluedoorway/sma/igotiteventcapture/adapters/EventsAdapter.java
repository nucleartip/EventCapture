package com.bluedoorway.sma.igotiteventcapture.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.bluedoorway.sma.igotiteventcapture.R;
import com.bluedoorway.sma.igotiteventcapture.model.Events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class EventsAdapter extends BaseAdapter
{
	private TextView eventName;
	private TextView eventCount;
	private TextView eventDate;
	private ArrayList<Events> list;
	private SimpleDateFormat DateFormatter;

	public EventsAdapter(ArrayList<Events> list)
	{
		this.list = list;
		DateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
			eventName = (TextView) reuse.findViewById(R.id.event_list_cell_name);
			eventCount = (TextView) reuse.findViewById(R.id.event_list_cell_count);
			eventDate = (TextView) reuse.findViewById(R.id.event_list_cell_date);

			Events obj = list.get(position);
			eventName.setText(obj.getEventName() + "");
			eventCount.setText("" + obj.getCount());
			try
			{
				String date = DateFormatter.format(new Date(obj.getTimeStamp()));
				eventDate.setText(date);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return reuse;
		}
		else
		{
			ViewGroup item = getViewGroup(reuse, parent);

			eventName = (TextView) item.findViewById(R.id.event_list_cell_name);
			eventCount = (TextView) item.findViewById(R.id.event_list_cell_count);
			eventDate = (TextView) item.findViewById(R.id.event_list_cell_date);

			Events obj = list.get(position);
			eventName.setText(obj.getEventName() + "");
			eventCount.setText("" + obj.getCount());
			try
			{
				String date = DateFormatter.format(new Date(obj.getTimeStamp()));
				eventDate.setText(date);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return item;
		}
	}

	private ViewGroup getViewGroup(View reuse, ViewGroup parent)
	{
		if (reuse instanceof ViewGroup)
			return (ViewGroup) reuse;
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup item = (ViewGroup) inflater.inflate(R.layout.event_list_cell, null);
		

		return item;
	}
}
