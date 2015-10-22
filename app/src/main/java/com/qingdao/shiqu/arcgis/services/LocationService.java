package com.qingdao.shiqu.arcgis.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service implements LocationListener
{
	LocationManager locationManager;
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	  @Override
	public void onCreate()
	{
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}
	  
	  @Override
	public void onStart(Intent intent, int startId)
	{
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	  
	@Override
	public boolean stopService(Intent name)
	{
		return super.stopService(name);
	}

	@Override
	public void onLocationChanged(Location location)
	{
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		
	}
    
	public interface ILocationListener
	{
		public void onLocationChanged(Double Latitude,Double Longitude);
	}
}
