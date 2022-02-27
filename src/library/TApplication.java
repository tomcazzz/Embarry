package library;

import java.util.List;

import myandroidpackages.EmbarryFans.R;
import myandroidpackages.EmbarryFans.R.string;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.util.Log;

public class TApplication 
{
	public static final String TAG = "TApplication";
	
	// Check whether application is installed
	public static Boolean isInstalled(Context context, String packageName)
	{
		try
		{
		     ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
		     
		     //-- application exists
		     Log.d(TAG,"application " + packageName + " exists; info: " + info);
		     		
		     return true;
		} 
		catch( PackageManager.NameNotFoundException e )
		{
			//-- application doesn't exists
			Log.d(TAG,"application " + packageName + " doesn't exists");
		    return false;
		}
	}
	
	// Check whether facebook app is installed (it's integrated in some mobile phones)
	// and return intent (facebook app intent or URL in webbrowser
	// otherwise intent chooser is started	
	public static Intent getFacebookIntent(Context context)
	{
		Intent i;
    	if (TApplication.isInstalled(context, "com.facebook.katana"))
    		i = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_facebook_app)));
    	else
    		i = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_facebook)));
    	
    	/* or
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClassName("com.facebook.katana", "com.facebook.katana.ProfileTabHostActivity");
		intent.putExtra("extra_user_id", "123456789l");
		this.startActivity(intent);
		*/
		return i;
	}
	
	// Check whether google mail app is installed
	// and return intent. Otherwise intent chooser is started	
	public static Intent getMailIntent(Context context)
	{
		Intent i = new Intent(Intent.ACTION_SEND);
		
		/* Doesn't work as expected
		// Caution! class names (e.g. com.google.android.gm) must be defined in Manifest!
    	if (TApplication.isInstalled(context, "com.google.android.gm"))
    	{
    		// start google mail
    		Log.d(TAG,"google mail");
    		i.setClassName("com.google.android.gm","com.google.android.gm.ConversationListActivity");
    	}
    	else if (TApplication.isInstalled(context, "com.android.email"))
    	{
    		// start default mail app
    		Log.d(TAG,"default mail app");
    		i.setClassName("com.android.email", "com.android.email.activity.MessageCompose");
    	}
    	else
    	{
    		// return chooser
    		Log.d(TAG,"intent chooser");
    		i.setType("message/rfc822"); // so the chooser will only list email applications
    		i = Intent.createChooser(i, "Email verschicken");    	
    	}
    	*/
		i.setType("message/rfc822"); // so the chooser will only list email applications
		
		return i;
	}	
	
	// Check whether intent is save (i.e. there is a receiver for the intent
	public static Boolean isIntentSave(Context context, Intent intent)
	{
		Boolean retValue = false;
		
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
		retValue = activities.size() > 0;
		
		return retValue;		
	}	
}
