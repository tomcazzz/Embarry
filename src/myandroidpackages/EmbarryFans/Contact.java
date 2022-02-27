package myandroidpackages.EmbarryFans;

import library.TActionBar;
import library.TApplication;
import library.TRotator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;

import android.webkit.WebView;
import android.widget.ImageView;


public class Contact extends Activity implements OnClickListener
{
	// Define constant TAG for Log
	private static final String TAG = "Contact";
	
	private TActionBar mActionBar;	

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        // call onCreate of parent class Activity
    	super.onCreate(savedInstanceState);
    	
        // set main view
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contact);
        
        // buttons
        View bInfo = findViewById(R.id.contact_info_button);
        View bBooking = findViewById(R.id.contact_booking_button);   
        View iBand = findViewById(R.id.band);
        
        // add event listeners
        bInfo.setOnClickListener(this);
        bBooking.setOnClickListener(this);
        iBand.setOnClickListener(this);

        // fade in band image
        //MyFader.fadeIn(this, R.id.band, 1000);
    	
        // init action bar (for API versions >= 11 and Android >= 3.0
        // for versions below that, the overflow action button is shown down the screen
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.contact_title);

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
    }	
	
    // react on button clicks
    @Override
    public void onClick(View v)
    {	
    	Intent i;
    	
		switch(v.getId())
    	{
    		case R.id.contact_info_button:
    			i = TApplication.getMailIntent(this);
    			Log.d(TAG,"contact_info_button");
    	    	i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"info@embarry.com"});
    	    	i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Info");
    	    	i.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.contact_info_text));
    	    	startActivity(Intent.createChooser(i, "Email verschicken"));
    			break;
    		case R.id.contact_booking_button:
    			i = TApplication.getMailIntent(this);
    			Log.d(TAG,"contact_booking_button");
    	    	i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"booking@embarry.com"});
    	    	i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Buchungsanfrage");
    	    	i.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.contact_booking_text));
    	    	startActivity(Intent.createChooser(i, "Email verschicken"));
    			break;
    		case R.id.band:
    			Log.d(TAG,"band");
    			TRotator.rotate(this, R.id.band, 2000, TRotator.INFINITE, true);
    	}
		
    }  
}
