package myandroidpackages.EmbarryFans;

import library.TGeoPoint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

public class GraphicalCompass extends Activity implements LocationListener,SensorEventListener
{ 
	// Define constant TAG for Log
	private static final String TAG = "GraphicalCompass";
	private CompassView compassView;
	
	// The exact Position of Embarry Home (originating from Google Maps)
	private static final TGeoPoint pEmbarryHome = new TGeoPoint(48.508683f,10.460695f);
	private ProgressDialog progressDialog;
	
	// private members
	private LocationManager lMgr;
	private SensorManager sMgr;
	private Sensor sAccelerometer;
	private Sensor sMagnetometer;
	private float[] aSensorValues;
	private float[] mGravity;
	private float[] mGeomagnetic;

	private String best;
	private Handler handler;
	private Runnable runnable;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		compassView = new CompassView(this);
		setContentView(compassView);
		compassView.requestFocus();
		sMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
	    sAccelerometer = sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    sMagnetometer = sMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		if (checkGPS())
		{
			Log.d(TAG,"checkGPS successful");
			lMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
	
			Criteria criteria = new Criteria(); 
			best = lMgr.getBestProvider(criteria, true);
			if (best != null) 
			{  
				Location location = lMgr.getLastKnownLocation(best);
				Log.d(TAG,"location: " + location);
				if (location != null)
				{
					dumpPosition(location);
					dumpDistance(location);
					dumpAngle(location);
				}
			}

			// start thread radar hand
			handler = new Handler();
			
			runnable = new Runnable()
			{
				int angle = 360;
				
				public void run() 
				{
					//Log.d("Runnable","blink: " + blink);
					handler.postDelayed(this, 100);
				    //compassView.drawHomePoint(angle,blink);
					//compassView.drawRadarHand(angle);
				    

				    //angle = angle - 2 % -360; // rotate clock wise				    
					angle = angle - 5;
					
				    if (angle == 0) angle = 360;
				    //Log.d("Runnable","angle: " + angle);
				}
			};

			handler.postDelayed(runnable, 100);
		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		// Start updates (doc recommends delay >= 60000 ms)
		if (best != null) 
		{
			// 2nd parameter is update time in ms, 3rd parameter is min distance in meters
			lMgr.requestLocationUpdates(best, 15000, 1, this);
		}
		handler.postDelayed(runnable, 75);
	    sMgr.registerListener(this, sAccelerometer, SensorManager.SENSOR_DELAY_UI);
	    sMgr.registerListener(this, sMagnetometer, SensorManager.SENSOR_DELAY_UI);

	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		// Stop updates to save power while app paused
		lMgr.removeUpdates(this);
		sMgr.unregisterListener(this);
		handler.removeCallbacks(runnable);
		//this.turnGPSOff();
	}

	@Override
	protected void onStop() 
	{
		super.onStop();
		// Stop updates to save power while app paused
		lMgr.removeUpdates(this);
		sMgr.unregisterListener(this);
		handler.removeCallbacks(runnable);
		//this.turnGPSOff();
	}	

	public void onLocationChanged(Location location) 
	{
		dumpPosition(location);
		dumpDistance(location);
		dumpAngle(location);
	}

	public void onProviderDisabled(String provider) 
	{
		//if (GraphicalCompass.debug) log("\nProvider disabled: " + provider);
	}

	public void onProviderEnabled(String provider) 
	{
		//if (GraphicalCompass.debug) log("\nProvider enabled: " + provider);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		//if (GraphicalCompass.debug) log("\nProvider status changed: " + provider + ", status=" + S[status] + ", extras=" + extras);
	}

	public void onSensorChanged(SensorEvent event)
	{
		float azimut = 0;
		
		Log.d(TAG, "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");

	    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) mGeomagnetic = event.values;
		
		if (mGravity != null && mGeomagnetic != null) 
		{
	      float R[] = new float[9];
	      float I[] = new float[9];
	      boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
	      if (success) 
	      {
	        float orientation[] = new float[3];
	        SensorManager.getOrientation(R, orientation);
	        azimut = orientation[0]; // orientation contains: azimut, pitch and roll
	      }
	    }
	    compassView.rotateCompass(azimut);
	}
	public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
	
	public Boolean checkGPS()
	{
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(!provider.contains("gps"))
	    { 
	    	// show message
		    new AlertDialog.Builder(this)
	    	.setTitle("GPS ist nicht aktiviert!")
	    	.setMessage("Willst du GPS aktivieren?")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() 
            {
                public void onClick(DialogInterface dialog, int whichButton) 
                {
                	turnGPSOn();
                	//tPosition.setText("Bitte verlasse die aktuelle Ansicht und rufe sie erneut auf");
                }
            })
            .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() 
            {
                public void onClick(DialogInterface dialog, int whichButton) 
                {
                	turnGPSOff();
                	//tPosition.setText("GPS-Berechnungen sind ohne aktiviertes GPS nicht möglich");
                }
            })
            .show();
		    //Log.d(TAG,"lGPSActive: " + String.valueOf(lGPSActive));
		    //if (lGPSActive) return true;
		    //else return false;
	    }	
	    return true;
	}
	
	// Dumps the Position (Latitude, Longitude) to the screen
	private void dumpPosition(Location loc)
	{
		compassView.drawPosition("Position: B[" + Math.round(loc.getLatitude() * 100.) / 100. + "] L[" + Math.round(loc.getLongitude() * 100.) / 100.+"]");
	}
   
	// Dumps the Distance between current position and Embarry Manor to the screen
	private void dumpDistance(Location loc)
	{	
		compassView.drawDistance("Entfernung: " + String.valueOf(TGeoPoint.getDistanceBetween(GraphicalCompass.pEmbarryHome, loc)) + " km");
	}
	
	// Dumps the current angle between current position and Embarry Manor to the screen
	// (unit circle -> angle starts from right to top)
	private void dumpAngle(Location loc)
	{
		//Log.d(TAG,"Angle: " + String.valueOf(GeoPoint.getAngleBetween(pEmbarryHome, loc)));
	}
	
	// Turns GPS on
	private void turnGPSOn()
	{
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    
	    // only proceed if GPS is off
	    if (!provider.contains("gps"))
	    { 
	    	Log.d(TAG,"Turn on GPS");
/*
	    	progressDialog = ProgressDialog.show(this, "", "GPS wird aktiviert...");

	    	new Thread(new Runnable()
	    				   {
	    						public void run()
	    						{
	    					    	final Intent poke = new Intent();
	    						    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	    						    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	    						    poke.setData(Uri.parse("3")); 
	    						    sendBroadcast(poke);
	    						    
	    						    //Funktioniert, aber nur für Prozesse, die hier abgeschlossen sind
	    							progressDialog.dismiss();
	    						}
	    				   }
	    	).start();
	    	*/
			// Funktioniert, aber wie finde ich heraus, dass GPS aktiviert ist? 
			progressDialog = ProgressDialog.show(this, "", "GPS wird aktiviert...");
			progressDialog.setCancelable(true);
			new Thread() 
			{
				public void run() 
				{
					try
					{
				    	final Intent poke = new Intent();
					    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
					    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
					    poke.setData(Uri.parse("3")); 
					    sendBroadcast(poke);
					    
					    //String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
					    sleep(7000);
					    /*
					    // only proceed if GPS is off
					    while(!provider.contains("gps")) 
					    {
					    	Log.d(TAG,String.valueOf(provider.contains("gps")));
					    	sleep(3000);
					    }
					    */
					} 
					catch (Exception e) 
					{
						Log.e(TAG, e.getMessage());
					}
		
					// dismiss the progress dialog
					progressDialog.dismiss();					
				
				}
			}.start(); 
    	}
	}
	
	// Turns GPS off
	private void turnGPSOff()
	{
		String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    // only proceed if GPS is on
	    if (provider.contains("gps"))
	    { 
	    	Log.d(TAG,"Turn off GPS");
	    	final Intent poke = new Intent();
		    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
		    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		    poke.setData(Uri.parse("3")); 
		    sendBroadcast(poke);
    	}
    }
}