package library;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

// Class contains static helper functions
public class THelperFunctions 
{
	public static final String TAG = "HelperFunctions";
	
	// function converts database date to local data (e.g. from 2012-05-20 to 20.05.2012)
	public static String convertDate_DB2Locale(String DBDate)
	{
		String date;
		
		String[] tmp = DBDate.split("[-]"); // regular expression
		date = tmp[2] + "." + tmp[1] + "." +tmp[0]; 
		Log.d(TAG,"Converted Date: " + date);
		return date;
	}
	
	// function converts DB date (e.g. 2012-11-06) to Date object
	public static Date getDateFromDBString(String DBDate)
	{
		Date dDate;
		String[] tmp = DBDate.split("[-]");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(tmp[2]),Integer.valueOf(tmp[1]),Integer.valueOf(tmp[0]));
		dDate = new Date(calendar.getTimeInMillis());
		
		return dDate;
	}
	
	// get Date from millis in db format (e.g. 2012-11-06)
	public static String getDBDateFromMillis(long milliSeconds)
	{
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(milliSeconds);
	    return formatter.format(calendar.getTime());
	}
		
	// returns hash code of app signature
	public static String getHashCode(Context context)
	{
		PackageInfo info;
		try 
		{
			info = context.getPackageManager().getPackageInfo("myandroidpackages.EmbarryFans", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) 
			{
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String hashKey = Base64.encodeToString(md.digest(),0);
			    return hashKey;
			} 
		}	    		
		catch (NameNotFoundException e1) 
		{
			Log.e("name not found", e1.toString());
		}

		catch (NoSuchAlgorithmException e) 
		{
			Log.e("no such an algorithm", e.toString());
		}
		catch (Exception e)
		{
			Log.e("exception", e.toString());
		}	
		return "";
	}
}
