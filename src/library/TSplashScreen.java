package library;

import myandroidpackages.EmbarryFans.Embarry;
import myandroidpackages.EmbarryFans.Prefs;
import myandroidpackages.EmbarryFans.R;
import myandroidpackages.EmbarryFans.R.layout;
import myandroidpackages.EmbarryFans.R.raw;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

public class TSplashScreen extends Activity
{	
	// Define constant TAG for Log
	private static final String TAG = "Splashscreen";	
	private static boolean lIsBackButtonPressed;
    private static final long SPLASHDURATION = 5000;
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/*
		// check in settings whether splash screen should be shown
		if (!Prefs.getSplashScreen(this))
		{
			// make sure we close the splash screen so the user won't come back when it presses back key	
			finish();
			
			Intent intent = new Intent(TSplashScreen.this, Embarry.class);
            TSplashScreen.this.startActivity(intent);
		}
		else
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.splashscreen);
			
			// play music when music is enabled in preferences

			Log.d(TAG,"Test");
			Log.d(TAG,"Prefs.getMusic(this): " + Prefs.getMusic(this));
		    if (Prefs.getMusic(this))  	
		    	TMusic.play(this, R.raw.little_man);
  			
			Handler mHandler = new Handler();
			
			// run a thread after SPLASHDURATION milliseconds to start the real app
			mHandler.postDelayed(new Runnable() 
			{			 
	            @Override
	            public void run() 
	            { 
	                // make sure we close the splash screen so the user won't come back when it presses back key 
	                finish();
	                 
	                if (!lIsBackButtonPressed) {
	                    // start the home screen if the back button wasn't pressed already
	                    Intent intent = new Intent(TSplashScreen.this, Embarry.class);
	                    TSplashScreen.this.startActivity(intent);
	               }
	                 
	            }
	 
	        }, SPLASHDURATION); // time in milliseconds (1 second = 1000 milliseconds) until the run() method will be called
		}	
		*/
	}
	
    @Override
    protected void onResume() 
    {     
    	/*
		// play music when music is enabled in preferences
    	if (Prefs.getMusic(this))  	
    	   TMusic.play(this, R.raw.little_man);
    	*/
    	super.onResume();
    }
    
    @Override
    protected void onPause() 
    {       
       TMusic.stop(this);
       super.onPause();
    }
    
	@Override
	protected void onStop() 
	{
		TMusic.stop(this);
		super.onStop();        
	}	
	
    @Override
    public void onBackPressed() {
  
         // set the flag to true so the next activity won't start up
         lIsBackButtonPressed = true;
         TMusic.stop(this);
         super.onBackPressed();
  
     }
}