package library;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class TBootReceiver extends BroadcastReceiver
{
	// receive system events
	public void onReceive(Context context, Intent intent) 
	{ 
		// in our case intent will always be BOOT_COMPLETED, so we can just set 
		// the alarm 
		// Note that a BroadcastReceiver is *NOT* a Context. Thus, we can't use 
		// "this" whenever we need to pass a reference to the current context. 
		// Thankfully, Android will supply a valid Context as the first parameter 
		int minutes = 2; 
    	AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); 
    	Intent i = new Intent(context, TNotificationService.class); 
    	PendingIntent pIntent = PendingIntent.getService(context, 0, i, 0); 
    	
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
		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + minutes*60*1000, minutes*60*1000, pIntent); 
	}
}
