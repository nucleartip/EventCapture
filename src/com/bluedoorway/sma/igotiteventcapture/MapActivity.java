package com.bluedoorway.sma.igotiteventcapture;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;
import com.bluedoorway.sma.igotiteventcapture.network.LocationFinder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity
{

	private GoogleMap mMap;
	private Location location;
	private int eventId = -1;
	private ContentResolver resolver;
	private Context context;
	private ProgressDialog dialog;
	private AsyncTask locator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		mMap = null;

		// Checking for eventId
		context = this;
		if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("file_id"))
			eventId = getIntent().getIntExtra("file_id", -1);
		setUpMapIfNeeded();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded()
	{
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null)
		{
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.setMyLocationEnabled(true);
			location = mMap.getMyLocation();

			mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener()
			{
				@Override
				public void onMyLocationChange(Location loc)
				{
					if (loc != null)
					{
						setUpMap(loc);
						LatLng point = new LatLng(loc.getLatitude(), loc.getLongitude());
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
						mMap.animateCamera(CameraUpdateFactory.zoomIn());
					}
				}

			});

			if (location != null)
			{
				double lat = location.getLatitude();
				double longi = location.getLongitude();
				setUpMap(location);
				LatLng point = new LatLng(lat, longi);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
				mMap.animateCamera(CameraUpdateFactory.zoomIn());
				System.out.println("#### lat:long " + lat + ":" + longi);
			}
			else
			{
				// Get the last known location
				LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null)
				{
					long fixedTime = location.getTime();
					long currentTime = System.currentTimeMillis();
					float diff = (currentTime - fixedTime) / (1000 * 60);
					if (diff < 45)
					{
						// this location fix is fine
						double latitude = location.getLatitude();
						double longitude = location.getLongitude();
						setUpMap(location);
						LatLng point = new LatLng(latitude, longitude);
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
						mMap.animateCamera(CameraUpdateFactory.zoomIn());
					}
				}
				locator = new LocationFinderTask().execute("locate");
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		if (locator != null && !locator.isCancelled())
			locator.cancel(true);
	}

	private void setUpMap(Location location)
	{
		mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
				.title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.location_green)));

		mMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker marker)
			{
				if (marker.getTitle().equals("My Location"))
				{
					// Update Lat long for this file
					resolver = context.getContentResolver();
					Uri uri = Uri.withAppendedPath(EventsDB.FILES_CONTENT_URI, String.valueOf(eventId));
					// Getting contentValue
					String selection = EventsDB.COLUMN_ID + "='" + eventId + "'";
					Cursor cursor = resolver.query(EventsDB.FILES_CONTENT_URI, null, selection, null, null);
					if (cursor != null && cursor.moveToNext())
					{
						LatLng position = marker.getPosition();
						ContentValues value = IGotItApplication.getContentValues(cursor, position.latitude, position.longitude);
						int id = resolver.update(uri, value, null, null);
						if (id != -1)
						{
							Uri toReturn = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI, String.valueOf(eventId));
							IGotItApplication.getAppContext().getContentResolver().notifyChange(toReturn, null);
						}
					}
					mMap = null;
					if (locator != null && !locator.isCancelled())
						locator.cancel(true);
					finish();
				}
				return false;
			}
		});
	}

	// Fetching user Location details
	class LocationFinderTask extends AsyncTask<String, Integer, Integer>
	{
		private LocationFinder locationFinder;
		private double latitude = -1;
		private double longitude = -1;

		@Override
		protected Integer doInBackground(String... params)
		{
			try
			{
				Looper.prepare();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			boolean flag = false;
			locationFinder = new LocationFinder();
			flag = locationFinder.getLocationScheduled(context);
			int index = 0;
			if (flag)
			{
				while (flag)
				{
					if (this.isCancelled())
						flag = false;
					try
					{
						location = locationFinder.getMyLocation();
						if (location == null)
						{
							Thread.sleep(1000);
							index = index + 1;
						}
						else
						{
							flag = false;
							return 1;
						}
						if (index > 20)
						{
							flag = false;
							return -1;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return -1;
					}
				}
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			super.onPostExecute(result);

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			if (result == 1)
			{
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				setUpMap(location);
				LatLng point = new LatLng(latitude, longitude);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
				mMap.animateCamera(CameraUpdateFactory.zoomIn());
			}
			else
				Toast.makeText(context, "Unable to detect location, please clik on locate me", Toast.LENGTH_SHORT).show();
		}
	}
}