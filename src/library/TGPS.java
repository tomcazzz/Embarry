package library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

public class TGPS 
{
	private static final String TAG = "TGPS";
	
	// Checks if GPS is on
	public static Boolean isGPSOn(Context context)
	{
	    String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    return (provider.contains("gps"));
	}    	
	    	
	// Turns GPS off
	public static void turnGPSOff(Context context)
	{
		String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    // only proceed if GPS is on
	    if (provider.contains("gps"))
	    { 
	    	Log.d(TAG,"Turn off GPS");
	    	final Intent poke = new Intent();
		    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
		    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		    poke.setData(Uri.parse("3")); 
		    context.sendBroadcast(poke);
    	}
    }
	
	// Turns GPS on
	public static void turnGPSOn(Context context)
	{
	    String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    
	    // only proceed if GPS is off
	    if (!provider.contains("gps"))
	    { 
	    	Log.d(TAG,"Turn on GPS");

			try
			{
		    	final Intent poke = new Intent();
			    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
			    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			    poke.setData(Uri.parse("3")); 
			    context.sendBroadcast(poke);					  
			} 
			catch (Exception e) 
			{
				Log.e(TAG, e.getMessage());
			}
    	}
	}	
}
