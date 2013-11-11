package com.bluedoorway.sma.igotiteventcapture.network;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

public class LocationFinder
{
	Timer timer1;
	LocationManager locationManager;
	boolean gps_enabled = false;
	boolean network_enabled = false;
	Location myCurrentLocationGps;
	Location myCurrentLocationNtw;
	Location net_loc;
	Location gps_loc;

	// Returning Latest Available Location
	public Location getMyLocation()
	{
		// do the Manipulation and return most latest Location

		if (myCurrentLocationGps == null && myCurrentLocationNtw == null)
			return null;
		else if (myCurrentLocationGps == null && myCurrentLocationNtw != null)
			return myCurrentLocationNtw;
		else if (myCurrentLocationGps != null && myCurrentLocationNtw == null)
			return myCurrentLocationGps;
		else
		{
			// Return the Latest one
			if (myCurrentLocationGps.getTime() > myCurrentLocationNtw.getTime())
				return myCurrentLocationGps;
			else
				return myCurrentLocationNtw;
		}
	}

	// Scheduling Location Update
	public boolean getLocationScheduled(Context context)
	{
		// Check if Location Manager Is Null
		if (locationManager == null)
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// Check for Location Provider
		try
		{
			gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch (Exception e)
		{
		}
		try
		{
			network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		catch (Exception e)
		{
		}
		
		// Return false if no Services is turned on
		if (!gps_enabled && !network_enabled)
			return false;
		
		// Check for Gps Provided Location Data
		if (gps_enabled)
		{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
			timer1 = new Timer();
			timer1.schedule(new GetLastLocationGps(), 15000);
		}
		else
		{
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
			timer1 = new Timer();
			timer1.schedule(new GetLastLocationNtw(), 10000);
		}

		return true;
	}

	LocationListener locationListenerGps = new LocationListener()
	{
		public void onLocationChanged(Location location)
		{
			timer1.cancel();
			myCurrentLocationGps = location;
			locationManager.removeUpdates(this);
		}

		public void onProviderDisabled(String provider)
		{
		}

		public void onProviderEnabled(String provider)
		{
		}

		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	};
	LocationListener locationListenerNetwork = new LocationListener()
	{
		public void onLocationChanged(Location location)
		{
			timer1.cancel();
			myCurrentLocationNtw = location;

			locationManager.removeUpdates(this);
		}

		public void onProviderDisabled(String provider)
		{
		}

		public void onProviderEnabled(String provider)
		{
		}

		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	};

	class GetLastLocationGps extends TimerTask
	{
		@Override
		public void run()
		{
			Looper.prepare();
			locationManager.removeUpdates(locationListenerGps);
			gps_loc = null;
			if (gps_enabled)
				gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			if (gps_loc != null)
				myCurrentLocationGps = gps_loc;

			// Start Receiving Network Location
			if (network_enabled)
			{
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
				// Initiate a Timer and to wait for a location update from GPS
				// Provider
				timer1 = new Timer();
				timer1.schedule(new GetLastLocationNtw(), 10000);
			}
		}
	}

	class GetLastLocationNtw extends TimerTask
	{
		@Override
		public void run()
		{
			locationManager.removeUpdates(locationListenerNetwork);
			net_loc = null;
			if (network_enabled)
				net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			// if there are both values use the latest one
			if (net_loc != null)
				myCurrentLocationNtw = net_loc;
		}
	}
}
