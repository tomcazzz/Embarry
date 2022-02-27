package library;

import java.util.Calendar;
import java.util.TimeZone;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

public class TCalendar 
{
	public static final String TAG = "TCalendar";
	
	public static String getCalendarUriBase(Activity act) 
	{
		// TODO: solve it with if (Build.VERSION.SDK_INT <= 7 (=2.2) and without throwing errors
        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars"); // for < Android 2.2
        Cursor managedCursor = null;
        try 
        {
            managedCursor = act.managedQuery(calendars, null, null, null, null);
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
        if (managedCursor != null) 
        {
            calendarUriBase = "content://calendar/";
        } 
        else 
        {        	
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try 
            {
                managedCursor = act.managedQuery(calendars, null, null, null, null);
            } 
            catch (Exception e) 
            {
            	e.printStackTrace();
            }
            if (managedCursor != null) 
            {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
	}
	
	// function converts DB date (e.g. 2012-11-06) and Time (hh:mm:ss) to Calendar instance 
	public static Calendar getCalendarInstanceFromDBDateAndTime(String sDate, String sTime)
	{
		Calendar calendar = Calendar.getInstance();
		String[] dateArray = sDate.split("[-]");
		String[] timeArray = sTime.split("[:]");

		
		Log.d(TAG,"Values: " + Integer.valueOf(dateArray[0]) + "; " +
					 Integer.valueOf(dateArray[1]) + "; " +
					 Integer.valueOf(dateArray[2]) + "; " +
					 Integer.valueOf(timeArray[0]) + "; " +
					 Integer.valueOf(timeArray[1]) + "; " +
					 Integer.valueOf(timeArray[2]));
		
		calendar.set(Integer.valueOf(dateArray[0]),
					 Integer.valueOf(dateArray[1])-1, // 'cause months are indexed from 0 to 11!
					 Integer.valueOf(dateArray[2]),
					 Integer.valueOf(timeArray[0]),
					 Integer.valueOf(timeArray[1]),
					 Integer.valueOf(timeArray[2]));
		
		return calendar;
	}	
	
	// get calender id of the preferred google account
	public static String getCalendarID(Activity activity, String sAccountName)
	{
		String sCalID = null;
       	String sCalName = "";
		
 	    // go through all calendars and try to find the Google Calendar
       	Uri uri = CalendarContract.Calendars.CONTENT_URI;
       	String[] projection = new String[] 
       	{
       		CalendarContract.Calendars._ID,
       	    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
       	};
       	Cursor calendarCursor = activity.managedQuery(uri, projection, null, null, null);
       	
        if (calendarCursor.moveToFirst()) 
       	{
       		int nameColumn = calendarCursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME); 
       		int idColumn = calendarCursor.getColumnIndex(CalendarContract.Calendars._ID);
       		do 
       		{
       			sCalName = calendarCursor.getString(nameColumn);
       			sCalID = calendarCursor.getString(idColumn);
       		    Log.d(TAG,"Calendar Name: " + sCalName + "; Calendar ID: " + sCalID);
       		} 
       		while (calendarCursor.moveToNext() && !sCalName.equals(sAccountName));
       		
       	}
        
        // you don't need to close the cursor because it comes from managedQuery
        // Otherwise the StaleDataException is thrown
        //if (!calendarCursor.isClosed()) calendarCursor.close();
        
		return sCalID;
	}
	
	// add event to google calendar
	public static boolean addEvent(Activity activity, String sAccountName, String sName, String sBeginDate, String sBeginTime, String sEndDate, String sEndTime, String sLocation, String sComment, TReminder tReminder)
	{
		boolean retValue = false;
		String sCalID = TCalendar.getCalendarID(activity, sAccountName);
		
        if(sCalID != null)
        {
        	// check whether event already exists
        	Uri EVENTS_URI = Uri.parse(TCalendar.getCalendarUriBase(activity) + "events");
        	String[] eventProjection = new String[] {"calendar_id", "title"};
        	Cursor eventCursor = activity.getContentResolver().query(EVENTS_URI, eventProjection, "calendar_id="+sCalID+" and title=\'"+sName+"\'", null, null);
        	if (eventCursor.getCount() > 0)
        	{
        		eventCursor.moveToFirst();
        		do
        		{
        			Log.d(TAG,"ID: " + eventCursor.getString(0) +"; Title: " + eventCursor.getString(1));
        		}
        		while (eventCursor.moveToNext()); 
        		
        		Toast.makeText(activity,"Das Kalenderevent existiert bereits",Toast.LENGTH_LONG).show();
        	}
        	else // -> The new event doesn't exist yet
        	{
            	// Insert Event
            	Calendar beginTime = TCalendar.getCalendarInstanceFromDBDateAndTime(sBeginDate,sBeginTime);
            	Log.d(TAG,"beginTime Month: " + beginTime.getTime().toString());
            	Calendar endTime = TCalendar.getCalendarInstanceFromDBDateAndTime(sEndDate,sEndTime);
            	TimeZone timeZone = TimeZone.getDefault();
            	ContentResolver cr = activity.getContentResolver();
            	ContentValues values = new ContentValues();
            	
            	values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
            	values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
            	values.put(CalendarContract.Events.TITLE, sName);
            	values.put(CalendarContract.Events.DESCRIPTION, sComment);
            	values.put(CalendarContract.Events.EVENT_LOCATION, sLocation);
            	values.put(CalendarContract.Events.CALENDAR_ID, sCalID);
                values.put("hasAlarm", 1);
            	
            	values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            	Log.d(TAG,"calID: " + sCalID);
            	Uri calendarEvent = cr.insert(EVENTS_URI, values);
            	
            	Log.d(TAG,"CalendarContract.Events.CONTENT_URI: " + CalendarContract.Events.CONTENT_URI.toString());
            	//Uri calendarEvent = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            	if (calendarEvent != null)
            	{
            		Log.d(TAG,"calendarEvent: " + calendarEvent.getPath());
            		Toast.makeText(activity,"Event wurde im Kalender angelegt.",Toast.LENGTH_LONG).show();
            		
            		retValue = true;
            		
            		if (tReminder != null)
            		{
                    	// add reminder
                    	ContentValues reminderValues = new ContentValues();
                        reminderValues.put("event_id", Long.parseLong(calendarEvent.getLastPathSegment()));
                        reminderValues.put("method", tReminder.getMethod()); //METHOD_ALERT
                        reminderValues.put("minutes", tReminder.getMinutes()); // remind user 2 hours before event
                        
                        Uri REMINDERS_URI = Uri.parse(TCalendar.getCalendarUriBase(activity) + "reminders");
                        if (REMINDERS_URI != null)
                        {
                        	cr.insert(REMINDERS_URI, reminderValues);
                        	Toast.makeText(activity,"Event wurde mit einer Erinnerung versehen.",Toast.LENGTH_LONG).show();
                        }
                        else 
                        {
                        	Toast.makeText(activity,"Fehler beim Anlegen der Erinnerung.",Toast.LENGTH_LONG).show();
                        	retValue = false;
                        }
            		}
            	}
        	}
        	if (!eventCursor.isClosed()) eventCursor.close();
        }	                    		            	   
    	return retValue;
	}
}
