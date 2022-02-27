package myandroidpackages.EmbarryFans;

import library.TActionBar;
import library.TApplication;
import library.TNotificationService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
//import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class Embarry extends Activity implements OnClickListener
{
	// Define constant TAG for Log
	private static final String TAG = "EmbarryMain";
	
	private TActionBar mActionBar;
	//private NotificationManager notificationManager;
	//private int NOTIFICATION = R.string.NOTIFICATION_ID_NEW_EMBARRY_DATA;
	
	//private boolean mIsBound = false;
    //private TMusicService mService;
    //private boolean bIsMusicServiceRunning = false;
	/*
    private ServiceConnection sConn = new ServiceConnection()
    {    	
    	@Override
    	public void onServiceConnected(ComponentName name, IBinder binder) 
    	{    
    		Log.d(TAG,"onServiceConnected. mService:" + mService);
    		mService = ((TMusicService.ServiceBinder)binder).getService();   		
    	}

    	@Override	
    	public void onServiceDisconnected(ComponentName name) 
    	{    
    		Log.d(TAG,"onServiceDisconnected: mService: " + mService);
    		mService = null;
    		
    	}
    };

	void doBindService()
	{
 		bindService(new Intent(this,TMusicService.class),sConn,Context.BIND_AUTO_CREATE);
 		mIsBound = true;
	}

	void doUnbindService()
	{
		if(mIsBound)
		{
			unbindService(sConn);
      		mIsBound = false;
		}
	}    
    */
	// Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        // call onCreate of parent class Activity
    	super.onCreate(savedInstanceState);
    	Log.d(TAG,"onCreate()");
    	
        // set main view
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        // logo view
        View tLogoFont = findViewById(R.id.logo_font);
        View tLogo = findViewById(R.id.logo_image);
        
        // declare / define buttons 
        View bInfo = findViewById(R.id.info_button);
        View bCast = findViewById(R.id.cast_button);
        View bNews = findViewById(R.id.news_button);
        View bGigs = findViewById(R.id.gigs_button);
        View bContact = findViewById(R.id.contact_button);
        View bNewsletter = findViewById(R.id.newsletter_button);
        View bVids= findViewById(R.id.vids_button);
        View bLinks = findViewById(R.id.links_button);
        View bTellafriend = findViewById(R.id.tellafriend_button);
        View bFacebook = findViewById(R.id.facebook_button);
        
        // add event listeners
        tLogoFont.setOnClickListener(this);
        tLogo.setOnClickListener(this);
        
        bInfo.setOnClickListener(this);
        bCast.setOnClickListener(this);
        bNews.setOnClickListener(this); 
        bGigs.setOnClickListener(this);
        bContact.setOnClickListener(this);
        bNewsletter.setOnClickListener(this);
        bVids.setOnClickListener(this);
        bLinks.setOnClickListener(this);
        bTellafriend.setOnClickListener(this);
        bFacebook.setOnClickListener(this);
        
        // play background music if set in preferences.
        /*
        Log.d(TAG,"Music Preference: " + String.valueOf(Prefs.getMusic(this)));
        if (Prefs.getMusic(this))
        {
	        Log.d(TAG, "onCreate: starting MusicService");
	        startService(new Intent(this, TMusicService.class));
	        bIsMusicServiceRunning = true;
        }
        */
        
        // init action bar (for API versions >= 11 and Android >= 3.0
        // for versions below that, the overflow action button is shown down the screen
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.app_name);
        // set home icon that does nothing when the user clicks on it
        mActionBar.setHomeLogo(R.drawable.icon);
        // set action icons        
        mActionBar.addActionIcon(R.drawable.icon_about,
            new OnClickListener() 
        	{
                @Override
                public void onClick(View v) 
                {
                	// show about window
    	    		Intent i = new Intent(Embarry.this, About.class);
    	    		startActivity(i);
                }
            }); 
        mActionBar.addActionIcon(R.drawable.icon_settings,
                new OnClickListener() 
            	{
                    @Override
                    public void onClick(View v) 
                    {
                        // show preferences
        	    		Intent i = new Intent(Embarry.this, Prefs.class);
        	    		startActivity(i);
                    }
                });     
        mActionBar.addActionIcon(R.drawable.icon_cancel,
                new OnClickListener() 
            	{
                    @Override
                    public void onClick(View v) 
                    {
                        // finish application
                    	// TODO: Info + Beenden - Buttons wegnehmen (aus beiden Layouts)
            	    	// show confirm message
            		    new AlertDialog.Builder(Embarry.this)
            	    	.setTitle("Schade...")
            	    	.setMessage("Willst Du wirklich beenden?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() 
                        {
                            public void onClick(DialogInterface dialog, int whichButton) 
                            {
                            	/*
                            	Log.d(TAG,"OnCancel clicked. mService: " + mService);
                            	
                            	cleanUp(); 
                            	*/                                                         	
                            	finish();
                            }
                        })
                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() 
                        {
                            public void onClick(DialogInterface dialog, int whichButton) 
                            {
                            }
                        })
                        .show();                    	
                    }
                });  
        
        // animate logo font
		//MyFader.fadeIn(this, findViewById(R.id.logo_font).getId(), MyFader.DEFAULT_DURATION);

		// animate logo
		//MyFader.fadeIn(this, findViewById(R.id.logo_image).getId(), MyFader.DEFAULT_DURATION);
    	
        
        // show noification (only a test; we will later use this for a background task which will check for new Embarry data via Webservice
    	//showNotification();
    }

    // react on button clicks
    @Override
    public void onClick(View v)
    {
    	Intent i;
    	
    	switch(v.getId())
    	{
			case R.id.logo_font:
				Log.d(TAG,"Logo font clicked");
				// open splashscreen
				startActivity(new Intent(this, Info.class));
				break;    	
    		case R.id.logo_image:
    			Log.d(TAG,"Logo clicked");
    			// animate logo
    			//MyRotator.rotate(this, R.id.logo_image, MyRotator.DEFAULT_DURATION, 0, false);
    			//Toast.makeText(this, "Streicheln ist erlaubt ...", 1000).show();
    			ImageView imageView = (ImageView) findViewById(R.id.logo_image);
    			imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));    			
    			break;
	    	case R.id.info_button:
	    		Log.d(TAG,"info clicked");
	    		i = new Intent(this, Info.class);
	    		startActivity(i);
	    		break;
	    	case R.id.cast_button:
	    		Log.d(TAG,"cast clicked");
	    		i = new Intent(this, Cast.class);
	    		startActivity(i);
	    		break;
	    	case R.id.news_button:
	    		Log.d(TAG,"news clicked");
	    		i = new Intent(this, NewsList.class);
	    		startActivity(i);
	    		break;
	    	case R.id.gigs_button:
	    		Log.d(TAG,"gigs clicked");
	    		i = new Intent(this, GigList.class);
	    		startActivity(i);
	    		break;
	    	case R.id.contact_button:
	    		Log.d(TAG,"contact clicked");
	    		i = new Intent(this, Contact.class);
	    		startActivity(i);
	    		break;
	    	case R.id.newsletter_button:
	    		Log.d(TAG,"newsletter clicked");
	    		i = new Intent(this, Newsletter.class);
	    		startActivity(i);
	    		break;
	    	case R.id.vids_button:
	    		Log.d(TAG,"vids clicked");
	    		i = new Intent(this, VideoList.class);
	    		startActivity(i);
	    		break;
	    	case R.id.links_button:
	    		Log.d(TAG,"links clicked");
	    		i = new Intent(this, LinkList.class);
	    		startActivity(i);
	    		break;
	    	case R.id.tellafriend_button:
	    		Log.d(TAG,"tellafriend clicked");
	    		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	    		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Embarry App");
	    		//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.tellafriendtext));
	    		String body = new StringBuilder()
	    		.append("<!DOCTYPE html><html><body>Hallo,<br>Kennst Du schon die Embarry App? ")
	    		.append("Mit Ihrer Hilfe bist Du immer auf dem neuesten Stand, was die Band Embarry betrifft! Gigs, News, Links, Videos der Band: ")
	    		.append("Alles aus erster Hand!<br><br>Hier ist der Link: <a href=\"https://play.google.com/store/apps/details?id=myandroidpackages.EmbarryFans\" ")
	    		.append("target=\"_blank\">https://play.google.com/store/apps/details?id=myandroidpackages.EmbarryFans</a><br><br>viel Spaﬂ!</body></html>")
	    		.toString();
	    		
	    		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
	    		//emailIntent.setType("text/plain");
	    		//emailIntent.setType("message/rfc822"); // so the chooser will only list email applications
	    		emailIntent.setType("text/html");
	    		startActivity(Intent.createChooser(emailIntent, "Info senden..."));   		
	    		break;
	    	case R.id.facebook_button:
	    		Log.d(TAG,"facebook clicked");	    		
	    		// open facebook app or, if not installed, URL in Webbrowser 
	    		startActivity(TApplication.getFacebookIntent(this));	    		
	    		break;
    	}    	
    }
 
    @Override
    protected void onResume() 
    {
    	super.onResume();
    	Log.d(TAG,"onResume()");
    	
    	// animate logo
		//ImageView imageView = (ImageView) findViewById(R.id.logo_image);
		//imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
       
    	/*
    	// resume music (paused in OnPause)
    	if(bIsMusicServiceRunning && mService != null)
    	{
    		mService.resumeMusic();
    	}
    	
        // play background music if set in preferences.
        Log.d(TAG,"Music Preference: " + String.valueOf(Prefs.getMusic(this)) + "; mService: "+ mService);
        if(bIsMusicServiceRunning)
        {
        	if(Prefs.getMusic(this))
        	{
		        Log.d(TAG, "onResume: starting MusicService");
		        startService(new Intent(this, TMusicService.class));
		        bIsMusicServiceRunning = true;
        	}
        } 
        else 
        {
        	if(!Prefs.getMusic(this)) 
        	{
        		Log.d(TAG, "onResume: stopping MusicService");
	        	stopService(new Intent(this, TMusicService.class));
	        	bIsMusicServiceRunning = false;
        	}
        }	
        */
    	// initialize trigger for notification service. Thie notification service
    	// will poll for new gigs and News    	    	
    	//initNotificationTrigger();    
    }

    @Override
    protected void onPause() 
    {  
    	/*
    	Log.d(TAG, "onPause: mService: " + mService);
    	if(bIsMusicServiceRunning && mService != null)
    	{           	   
    		mService.pauseMusic();
    	}
    	*/
    	super.onPause();
    	
    }
    
	@Override
	protected void onStop() 
	{
		/*
		Log.d(TAG,"OnStop mService: " + mService);
		
		cleanUp();
		*/
        
        super.onStop();        
	}	 
	
	@Override
	protected void onDestroy()
	{
        // cancel specific notification. This method also deletes ongoing notifications
        //notificationManager.cancel(NOTIFICATION);
        
        super.onDestroy();
	}	
    
    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    // react on menu items clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId())
    	{
    		case R.id.settings:
    			Log.d(TAG,"Menu: settings");
    			startActivity(new Intent(this, Prefs.class));
    			return true;
    	}
    	return false;
    }   
    
    // garbage collection and finalisation
    /*
    private void cleanUp()
    {
    	Log.d(TAG,"Clean up...");
        if(bIsMusicServiceRunning && mService != null)
        {
        	Log.d(TAG, "onStop: stopping MusicService. mService: " + mService);
	        mService.stopMusic();
	        stopService(new Intent(this, TMusicService.class));
	        bIsMusicServiceRunning = false;
        }         
    }
    */

    // TEST: Show Notification in status bar
    private void showNotification() 
    {
    	Log.d(TAG,"showNotification()....");
    	//notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.NOTIFICATION_TEXT_NEW_GIG);

        // Set the icon, scrolling text and timestamp
        //Notification notification = new Notification(R.drawable.icon, text,System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this, GigList.class),0);
        
        // Set the info for the views that show in the notification panel.
        //notification.setLatestEventInfo(this, getText(R.string.NOTIFICATION_LABEL_NEW_GIG),text, pendingIntent);
        
        // You should use the following for the deprecated things:
        // But caution! This might not work for low API levels! Please check it!
        // VON HIERAB AUSKOMMENTIEREN
        /*
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Neues von Embarry")
            .setContentText("Neuer Gig!");
		
        
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, ResultActivity.class);
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ResultActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
        stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        );    
        mBuilder.setContentIntent(resultPendingIntent);  
        mNotificationManager.notify(mId, mBuilder.build());          
        // BIS HIERHER AUSKOMMENTIEREN
        //
         */
        
        // Send the notification.    
        //notificationManager.notify(NOTIFICATION, notification);        
    }
    
    private void initNotificationTrigger()
    {
    	Log.d(TAG,"initNotificationTrigger()");
    	
    	int minutes = 2; 
    	AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE); 
    	Intent intent = new Intent(this, TNotificationService.class); 
    	PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0); 
    	
    	// remove any alarms with matching intent pIntent;
    	am.cancel(pIntent); 
    	
		/*
		Schedule a repeating alarm that has inexact trigger time requirements; 
		for example, an alarm that repeats every hour, but not necessarily 
		at the top of every hour. These alarms are more power-efficient 
		than the strict recurrences supplied by setRepeating(int, long, long, PendingIntent), 
		since the system can adjust alarms' phase to cause them to fire simultaneously, 
		avoiding waking the device from sleep more than necessary.
		*/
		//am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + minutes*60*1000, minutes*60*1000, pIntent);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + minutes*60*1000, minutes*60*1000, pIntent);
    }
}
