package myandroidpackages.EmbarryFans;

import library.TActionBar;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

public class Prefs extends PreferenceActivity 
{
	private TActionBar mActionBar;
	
	// Option names and default values
	//private static final String OPT_MUSIC = "music";
	//private static final boolean OPT_MUSIC_DEF = false;
	//private static final String OPT_SPLASHSCREEN = "splashscreen";
	//private static final boolean OPT_SPLASHSCREEN_DEF = true;
	private static final String OPT_GIGS_AUTOMATION = "gigs_automation";
	private static final boolean OPT_GIGS_AUTOMATION_DEF = true;	
	private static final String OPT_NEWS_AUTOMATION = "news_automation";
	private static final boolean OPT_NEWS_AUTOMATION_DEF = true;	
	private static final String OPT_LINKS_AUTOMATION = "links_automation";
	private static final boolean OPT_LINKS_AUTOMATION_DEF = true;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		
		// add preferences and add view
		addPreferencesFromResource(R.xml.settings);		
		setContentView(R.layout.preference_container);		

    	// Populate Preferences with default values as defined in settings.xml
		// Bringt nichts:
    	//PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    	
		// init action bar
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.settings_title);
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
        mActionBar.addActionIcon(R.drawable.icon_ok,
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
/*
	// Get the current value of the music option 
	public static boolean getMusic(Context context) 
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
	}

	// Get the current value of the splash screen option
	public static boolean getSplashScreen(Context context) 
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_SPLASHSCREEN, OPT_SPLASHSCREEN_DEF);
	}
*/
	// Get the current value of the gig automation option */
	public static boolean getGigAutomation(Context context) 
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_GIGS_AUTOMATION, OPT_GIGS_AUTOMATION_DEF);
	}
	
	// Get the current value of the news automation option */
	public static boolean getNewsAutomation(Context context) 
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_NEWS_AUTOMATION, OPT_NEWS_AUTOMATION_DEF);
	}

	// Get the current value of the links automation option */
	public static boolean getLinksAutomation(Context context) 
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_LINKS_AUTOMATION, OPT_LINKS_AUTOMATION_DEF);
	}
}
