package myandroidpackages.EmbarryFans;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import library.TAccount;
import library.TActionBar;
import library.TApplication;
import library.TCalendar;
import library.THelperFunctions;
import library.TReminder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

public class GigDetail extends FragmentActivity  implements OnClickListener, OnSeekBarChangeListener
{
	private static final String TAG = "GigDetail";
	
	private TActionBar mActionBar;
	private TextView tGig_name;
	private TextView tGig_location;
	private TextView tGig_address;
	private TextView tGig_date;
	private TextView tGig_admittance;
	private TextView tGig_begin;
	private TextView tGig_comment;
	
	private String gig_name;
	private String gig_location;
	private String gig_address;
	private String gig_date;
	private String gig_admittance;
	private String gig_begin;
	private String gig_comment;
	
	private CheckBox cBSatellite;
	private SeekBar sBZoomBar;
	
	//private MapView oMap;
	//private MapController oController;
	private GoogleMap oGoogleMap;
	
	private class MyWebServiceTask extends AsyncTask<String, Integer, JSONObject>
	{
		private static final String TAG = "WebServiceTask";
	    private ProgressDialog dialog;
	    private String message;
	    private HttpResponse response;
	    private GigDetail gigDetail;
	    private TActionBar mActionBar;
	    
	    //Used as handler to cancel task if back button is pressed
	    private AsyncTask<String, Integer, JSONObject> updateTask = null;

	    public MyWebServiceTask(GigDetail gDetail, TActionBar actionBar, String ProgressMessage)
	    {
	    	gigDetail = gDetail;
	    	mActionBar = actionBar;
	    	dialog = new ProgressDialog(gigDetail);
	    	message = ProgressMessage;
	    }
	    
	    @Override
	    protected void onPreExecute()
	    {
	        updateTask = this;
	        dialog.setOnDismissListener(new OnDismissListener() 
	        {               
	            @Override
	            public void onDismiss(DialogInterface dialog) 
	            {
	            	mActionBar.hideProgressBar();
	                updateTask.cancel(true);
	            }
	        });

	        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
	        {
	            public void onCancel(DialogInterface dialog) 
	            {
	            	mActionBar.hideProgressBar();
	            	updateTask.cancel(true);
	                //finish();
	            }
	        });
	        
	        dialog.setTitle("Webservice");
	        dialog.setMessage(message);
	        dialog.show();
	        mActionBar.showProgressBar();
	    }

	    @Override
	    protected JSONObject doInBackground(String... urls) 
	    {
	        if(isCancelled()) return null;
	        JSONObject jsonObject = null;
	        Log.d(TAG,"URL: " + urls[0]);
	        StringBuilder stringBuilder = new StringBuilder();
	        HttpGet httpGet = new HttpGet(urls[0]);
		    HttpClient client = new DefaultHttpClient();
		    
		    try
		    {
		    	response = client.execute(httpGet);
		        HttpEntity entity = response.getEntity();
		        InputStream stream = entity.getContent();
		        int b;
		        while ((b = stream.read()) != -1)
		        {
		            stringBuilder.append((char) b);
		        }	    
		    	jsonObject = new JSONObject(stringBuilder.toString());
		    }
		    catch (ClientProtocolException e)
		    {
		    	e.printStackTrace();
		    	Log.d(TAG,"ClientProtocolException " + e.getMessage());
		    }
		    catch (IOException e)
		    {
		    	e.printStackTrace();
		    	Log.d(TAG,"IOException " + e.getMessage());
		    }
		    catch (JSONException e)
		    {
		        e.printStackTrace();
		        Log.d(TAG,"JSONException " + e.getMessage());
		    }

		    return jsonObject;	    
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress)
	    {
	    }

	    @Override
	    protected void onPostExecute(JSONObject response)
	    {
        	mActionBar.hideProgressBar();
	        dialog.dismiss();
	        if (response == null)
	        	Toast.makeText(gigDetail, "WebService wurde abgebrochen", 1000).show();
	        else 
	        {
	        	gigDetail.initLocation(response);
	        }
	    }
	}	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG,"OnCreate()");
		super.onCreate(savedInstanceState);
				
		// set view
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gig_detail);
		
		try
		{
			initializeMap();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		cBSatellite = (CheckBox) findViewById(R.id.satellite);	
		
		// Set check on satellite check box
		cBSatellite.setChecked(true);
		
		sBZoomBar = (SeekBar) findViewById(R.id.zoombar);
		
		cBSatellite.setOnClickListener(this);
		sBZoomBar.setOnSeekBarChangeListener((OnSeekBarChangeListener) this);
		
		tGig_name = (TextView) findViewById(R.id.gigdetail_name);
		tGig_location = (TextView) findViewById(R.id.gigdetail_location);
		tGig_address = (TextView) findViewById(R.id.gigdetail_address);
		tGig_date = (TextView) findViewById(R.id.gigdetail_date);
		tGig_admittance = (TextView) findViewById(R.id.gigdetail_admittance);
		tGig_begin = (TextView) findViewById(R.id.gigdetail_begin);
		tGig_comment = (TextView) findViewById(R.id.gigdetail_comment);
		
		gig_name = getIntent().getExtras().getString("GIG_NAME");
		gig_location= getIntent().getExtras().getString("GIG_LOCATION");
		gig_address= getIntent().getExtras().getString("GIG_ADDRESS");
		gig_date = getIntent().getExtras().getString("GIG_DATE");
		gig_admittance = getIntent().getExtras().getString("GIG_ADMITTANCE");
		gig_begin = getIntent().getExtras().getString("GIG_BEGIN");
		gig_comment = getIntent().getExtras().getString("GIG_COMMENT");
		
		tGig_name.setText(gig_name);
		tGig_location.setText(gig_location);
		tGig_address.setText(gig_address);
		tGig_date.setText(THelperFunctions.convertDate_DB2Locale(gig_date));
		tGig_admittance.setText(gig_admittance);
		tGig_begin.setText(gig_begin);
		tGig_comment.setText(gig_comment);
				
        // init action bar (for API versions >= 11 and Android >= 3.0
        // for versions below that, the overflow action button is shown down the screen
		// TODO: implement settings for versions < 11 (refresh action)
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.gigdetail_title);

        // set home icon
        mActionBar.setHomeLogo(R.drawable.icon,
                new OnClickListener() 
		    	{
		            @Override
		            public void onClick(View v) 
		            {
		            	// go back
		            	finish();
		            }
		        });
        // set action icons        
        mActionBar.addActionIcon(R.drawable.icon_previous,
                new OnClickListener() 
            	{
                    @Override
                    public void onClick(View v) 
                    {
                    	// go back
		            	finish();                    	
                    }
                }); 
        mActionBar.addActionIcon(R.drawable.icon_calendar,
                new OnClickListener() 
            	{
                    @Override
                    public void onClick(View v) 
                    {
                		// get user's google account (permission is declared in Manifest File)                    		
                		final String[] sAccounts = TAccount.getAccountStrings(GigDetail.this);
                		
                		
                		if (sAccounts.length > 0)
                		{
                			AlertDialog.Builder builder = new AlertDialog.Builder(GigDetail.this);
                		    builder.setTitle("Account wählen");
                		    builder.setItems(sAccounts, 
                		    		new DialogInterface.OnClickListener() 
                		            {
                		               public void onClick(DialogInterface dialog, int which) 
                		               {
                		            	   String selectedAccountName = sAccounts[which];
                		            	   Log.d(TAG,"Clicked on Account " + sAccounts[which]);
                		            	                   		            	   
                		            	   TReminder tReminder = new TReminder(TReminder.METHOD_ALERT,120);
                		            	   if (!TCalendar.addEvent(GigDetail.this,
                		            			   			  selectedAccountName,	
                	                    					  "Embarry " + gig_name, 
                	                    					  gig_date, 
                	                    					  gig_admittance,
                	                    					  gig_date,
                	                    					  "23:59:59",
                	                    					  gig_location, 
                	                    					  gig_comment,
                	                    					  tReminder))
                		            	   {
                		            		   Toast.makeText(GigDetail.this,"Fehler beim Anlegen des Kalenderevents.",Toast.LENGTH_LONG).show();	               			
                		            	   }
                		               }
                		            });
                		    
                		    builder.create();
                		    builder.show();                		    
                		}
                		else Toast.makeText(GigDetail.this,"Es ist kein Google-Account vorhanden. Dieser wird für das Anlegen des Kalenderevents benötigt.",Toast.LENGTH_LONG).show();
                    }
                });         
        mActionBar.addActionIcon(R.drawable.icon_refresh,
            new OnClickListener() 
        	{
                @Override
                public void onClick(View v) 
                {
                	// call webservice to refresh location
                	loadAdressLocationFromWebservice(gig_address);
                }
            }); 
        mActionBar.addActionIcon(R.drawable.icon_route,
                new OnClickListener() 
            	{
        			//Boolean GPSisOn = false;
        			
                    @Override
                    public void onClick(View v) 
                    {                    	
                    	// Check whether google navigation is installed
                    	if (!TApplication.isInstalled(GigDetail.this, "com.google.android.apps.maps"))
                    	{
                		    new AlertDialog.Builder(GigDetail.this)
                	    	.setTitle("Google Navigation ist nicht aktiviert!")
                	    	.setMessage("Navigation nicht möglich, weil bisher leider keine anderen Navigationsanwendungen außer Google Navigation unterstützt werden.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() 
                            {
                                public void onClick(DialogInterface dialog, int whichButton) 
                                {
                                	return;
                                }
                            })
                            .show();                    		
                    	}
                    	else
                    	{
                        	// Da es Zeit in Anspruch nimmt, GPS zu aktivieren und ich keine Schlafschleife einbauen will,
                        	// lasse ich das Anschalten erst mal weg
	                    	// Check whether GPS is active
                            /*
	                    	if (!TGPS.isGPSOn(GigDetail.this))
	                    	{
	                		    new AlertDialog.Builder(GigDetail.this)
	                	    	.setTitle("GPS ist nicht aktiviert!")
	                	    	.setMessage("Willst du es aktivieren?")
	                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() 
	                            {
	                                public void onClick(DialogInterface dialog, int whichButton) 
	                                {	                              
	                                	//Toast.makeText(GigDetail.this, "wird aktiviert", 1000).show();
	                                	// Da es Zeit in Anspruch nimmt, GPS zu aktivieren und ich keine Schlafschleife einbauen will,
	                                	// lasse ich das Anschalten erst mal weg
	                                	//TGPS.turnGPSOn(GigDetail.this);
	                                	//GPSisOn = true;
	                                	return;
	                                }
	                            })
	                            .setNegativeButton("Nein", new DialogInterface.OnClickListener() 
	                            {
	                                public void onClick(DialogInterface dialog, int whichButton) 
	                                {
	                                	GPSisOn = false;
	                                	return;
	                                }
	                            }
        						)
	                            .show();                    		
	                    	}
	                    	*/
	                    	//else GPSisOn = true;
	                    	//if (GPSisOn)
	                    	{	                    		
		                    	String address = "";
		                    	
	                        	// open google navigation
	                    	    try 
	                    	    { 
	                    	    	address = URLEncoder.encode(gig_address,"UTF-8"); 
	                    	    	Log.d(TAG,"adress: " + address);
	                    	    } 
	                    	    catch (UnsupportedEncodingException e) 
	                    	    { 
	                    	    	Log.d(TAG,"UnsupportedEncodingException: " + e.getMessage());
	                    	        e.printStackTrace(); 
	                    	    }
	                        	
	                        	Uri parse = Uri.parse("google.navigation:" + "q="+address);
	                        	
	                        	/* Achtung: 
	                        	- Hier bitte Intent mitgeben, dass er benachrichtig werden will,
	                        	  wenn google navigation beendet ist
	                        	- dann prüfen, ob GPS an, wenn nicht, fragen, ob GPS angesch. werden soll
	                        	- dann fragen, ob Navigation geöffnet werden soll 
	                        	*/
	                        	Intent intent = new Intent(Intent.ACTION_VIEW,parse);
	                        	startActivity(intent);
	                    	}	                    	
                    	}
                    }
                }); 

		//initMapView();
		loadAdressLocationFromWebservice(gig_address);
	}
	
    @Override
    protected void onResume() 
    {
        super.onResume();
        initializeMap();
    }

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.satellite:
				oGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				break;
		}
	}


	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) 
	{
	   //SetZoomLevel(sBZoomBar.getProgress()+1);
	}

	public void onStartTrackingTouch(SeekBar seekBar) 
	{
  
	}
	public void onStopTrackingTouch(SeekBar seekBar) 
	{
	}

/*		  
	@Override
	public void onWindowFocusChanged(boolean hasFocus) 
	{
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus==true)
        {        	
    		tGig_comment.setWidth(200);    		
        }
	}
	*/
	/*
	@Override
	protected boolean isRouteDisplayed()
	{
		// Required by MapActivity
		return false;
	}
	*/
	/*
	private void initMapView()
	{
		oMap = (MapView) findViewById(R.id.gigdetail_map);
		oController = oMap.getController();
		oMap.setSatellite(true);
		oMap.setBuiltInZoomControls(true);	
	}
	*/
	private void loadAdressLocationFromWebservice(String address)
	{
	    try 
	    { 
	    	address = URLEncoder.encode(address,"UTF-8"); 
	    	Log.d(TAG,"adress: " + address);
	    } 
	    catch (UnsupportedEncodingException e) 
	    { 
	    	Log.d(TAG,"UnsupportedEncodingException: " + e.getMessage());
	        e.printStackTrace(); 
	    }
	    
	    MyWebServiceTask webServiceTask = new MyWebServiceTask(this,mActionBar,"Lade Koordinaten der Adresse...");
	    Log.d(TAG,"Address: http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
    	webServiceTask.execute("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");	    	
	}
	
	// called from Webservice class when web service has been finished
	public void initLocation(JSONObject jObject)
	{
		Log.d(TAG,"initLocation");
		mActionBar.showProgressBar();
		//List<Overlay> mapOverlays = oMap.getOverlays();
		//Drawable drawable = this.getResources().getDrawable(R.drawable.logo_arrow_map_item);
		//TItemizedOverlay itemizedoverlay = new TItemizedOverlay(drawable, this);
			
		try
		{			
	        final GeoPoint p = getGeoPoint(jObject);       
	        Log.d(TAG,"GeoPoint: " + p.getLatitudeE6() + "; " + p.getLongitudeE6());
	        // Hint: At zoomLevel 1, the equator of the earth is 256 pixels long. Each successive zoom level is magnified by a factor of 2.

	        // create marker
	        MarkerOptions marker = new MarkerOptions().position(new LatLng(p.getLatitudeE6(), p.getLongitudeE6())).title("Hello Maps ");
	        
	        /*
	        // GREEN color icon
	        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
	        */
	        
	        // Changing marker icon
	        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_arrow_map_item));	        


	        // adding marker
	        oGoogleMap.addMarker(marker);

	        // set map type
	        //oGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	        oGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	        //oGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	        //oGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	        //oGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
	        
	        //oGoogleMap.getUiSettings().setZoomControlsEnabled(false); 
	        
	        // show my location button
	        oGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
	        
	        //SetZoomLevel(19);
	        CameraPosition cameraPosition = new CameraPosition.Builder().target(
	                new LatLng(p.getLatitudeE6(), p.getLongitudeE6())).zoom(19).build();
	 
	        oGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));	        
            // Set progress (21 is highest zoom level)
            sBZoomBar.setProgress((int)(sBZoomBar.getMax()*(float)(19f/21f)));
            //controller.setCenter(p);
            /*
            oController.animateTo(p);

            oMap.invalidate();
            
            OverlayItem overlayitem = new OverlayItem(p, gig_name,gig_comment);
            itemizedoverlay.addOverlay(overlayitem);
            mapOverlays.add(itemizedoverlay);
            */
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	    mActionBar.hideProgressBar();
	}
	

	private GeoPoint getGeoPoint(JSONObject jsonObject)
	{
	    Double lon = new Double(0);
	    Double lat = new Double(0);

	    try {

	        lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	            .getJSONObject("geometry").getJSONObject("location")
	            .getDouble("lng");

	        lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	            .getJSONObject("geometry").getJSONObject("location")
	            .getDouble("lat");

	    } 
	    catch (JSONException e)
	    {
	        e.printStackTrace();
	    }

	    return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
	}
/*	
	private void SetZoomLevel(int iLevel)
	{
		oController.setZoom(iLevel);
	}

*/	
	
    private void initializeMap() 
    {
        if (oGoogleMap == null) 
        {
        	oGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.gigdetail_map)).getMap();
 
            // check if map is created successfully or not
            if (oGoogleMap == null) 
            {
                Toast.makeText(getApplicationContext(),
                        "Karte kann nicht initialisiert werden!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }	
}


