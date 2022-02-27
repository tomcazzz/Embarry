package myandroidpackages.EmbarryFans;

import java.util.HashMap;
import java.util.Map;

import library.TActionBar;
import library.TTaskResultReceiver;
import library.TWebServiceTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class GigList extends ListActivity implements Constants,TTaskResultReceiver
{
	private static final String TAG = "GigList";
	
	// filters for querying gigs fom joomla db
	private static final String ALL = "all";
	private static final String NEW = "new";
	private static final String ARCHIVED = "archived";
	
	private String gigFilter = NEW;
	
	// vars for db usage
	private static String[] FROM = {_ID, GIG_NAME, GIG_LOCATION, GIG_ADDRESS, GIG_DATE, GIG_ADMITTANCE, GIG_BEGIN, GIG_COMMENT, GIG_ARCHIVED, GIG_CREATED, GIG_MODIFIED};
	private static int[] TO = {R.id.gig_id,R.id.gig_name,R.id.gig_location,R.id.gig_address, R.id.gig_date,R.id.gig_admittance,R.id.gig_begin,R.id.gig_comment,R.id.gig_archived,R.id.gig_created,R.id.gig_modified};
	private static String ORDER_BY = GIG_DATE + " DESC";

	// objects
	private DBData gigData;
	private TActionBar mActionBar;
	private Boolean lShowNewGigs = true;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// set view
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gig_list);
		
		if (getIntent().getExtras() != null)
			gigFilter = getIntent().getExtras().getString("GIG_FILTER");
		
        // init action bar (for API versions >= 11 and Android >= 3.0
        // for versions below that, the overflow action button is shown down the screen
		// TODO: implement settings for versions < 11 (refresh action)
		mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.giglist_title_new);
        
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
        mActionBar.addActionIcon(R.drawable.icon_important_active,
                new OnClickListener() 
            	{
                    @Override
                    public void onClick(View v) 
                    {
                    	// toggle between new and archived gigs
                		try
                		{	
                			if(!lShowNewGigs) 
                			{
                				gigFilter = NEW;
                				mActionBar.setTitle(R.string.giglist_title_new);
                				mActionBar.changeActionIcon(1,R.drawable.icon_important_active);
                			}
                			else
                			{	
                				gigFilter = ARCHIVED;
                				mActionBar.setTitle(R.string.giglist_title_archived);
                				mActionBar.changeActionIcon(1,R.drawable.icon_important_inactive);
                			}
                			Cursor cursor = getGigs();
                			showGigs(cursor);
                			lShowNewGigs = !lShowNewGigs;
                		}
                		finally
                		{			
                			gigData.close();
                		}                    	
                    }
                });         
        mActionBar.addActionIcon(R.drawable.icon_refresh,
            new OnClickListener() 
        	{
                @Override
                public void onClick(View v) 
                {
                	// call web service to refresh gig list (only new gigs!)  
                	// Funktioniert noch nicht, da die Funktion getMaxCreatedDate Probleme bereitet
                	// Ich hole einfach stur alle Gigs
                	//loadGigsFromWebservice("modified > " + getMaxCreatedDate());
                	loadGigsFromWebservice(ALL);
                }
            }); 

		Log.d(TAG,"OnCreate");
		gigData = new DBData(this);
		
		// Caution! It seems as if after dropping the table, table won't be created
		// because onCreate in DBData is not called anymore -> only way to to this
		// is to increment the database version.
		try
		{	
			// here the web service should be called to check whether there are new gigs
			
			// CAUTION!
			// city and street are not enough. ZIP must be added also
			// check whether Joomla databases contain URL to Google Maps location, that would
			// be much easier
			//Log.d(TAG, "Date: " + HelperFunctions.convertDate_DB2Locale("1978-05-20"));
			Cursor cursor = getGigs();
			
			// if list is empty, get gigs from webservice (e.g. when app has been installed)
			if (cursor.getCount() == 0) loadGigsFromWebservice(ALL);
			else
			{			
				// refresh automatically if set in settings
				// As there is a problem with function getMaxCreatedDate, we will get all gigs
				// TODO: fix problem to increase performance
				Log.d(TAG,"getGigAutomation(this): " + Prefs.getGigAutomation(this));
				//Log.d(TAG,"getNewsAutomation(this): " + Prefs.getNewsAutomation(this));
				//Log.d(TAG,"getLinksAutomation(this): " + Prefs.getLinksAutomation(this));
				//Log.d(TAG,"getMusic(this): " + Prefs.getMusic(this));
				//Log.d(TAG,"getSplashScreen(this): " + Prefs.getSplashScreen(this));
				if (Prefs.getGigAutomation(this)) loadGigsFromWebservice(ALL);
			}		
			showGigs(cursor);
		}
		finally
		{			
			gigData.close();
		}
	}
	
	
	@Override
    protected void onResume() 
    {
		Log.d(TAG,"OnResume");
		super.onResume();     
		gigData = new DBData(this);
		try
		{	
			Cursor cursor = getGigs();
			showGigs(cursor);
		}
		finally
		{
			gigData.close();
		}
    }

    // react on list item clicks
    @Override
    public void onListItemClick(ListView parent, View v,int position, long id)
    {
    	Log.d(TAG,"List Item clicked. Postion: " + position + "; ID: " + id);
    	
    	Cursor cursor = (Cursor) getListAdapter().getItem(position);
    	
    	// with id the correct database table entry can be displayed in 
    	// GigDetail-Activity where additional data is shown (Google Map, Comment...)
    	Intent i;
    	i = new Intent(this, GigDetail.class);
    	
    	// save database values or database index "id"    	
    	i.putExtra("GIG_NAME",cursor.getString(1));
    	i.putExtra("GIG_LOCATION",cursor.getString(2));
    	i.putExtra("GIG_ADDRESS",cursor.getString(3));
    	i.putExtra("GIG_DATE",cursor.getString(4));
    	i.putExtra("GIG_ADMITTANCE",cursor.getString(5));
    	i.putExtra("GIG_BEGIN",cursor.getString(6));
    	i.putExtra("GIG_COMMENT",cursor.getString(7));
    	
		startActivity(i);    	
    }
    
	private void addGig(String name, String location, String address, String date, String admittance, String begin, String comment, String archived, String created, String modified)
	{		
		SQLiteDatabase db = gigData.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(GIG_NAME, name);
		values.put(GIG_LOCATION, location);
		values.put(GIG_ADDRESS, address);
		values.put(GIG_DATE, date);
		values.put(GIG_ADMITTANCE, admittance);
		values.put(GIG_BEGIN, begin);
		values.put(GIG_COMMENT, comment);
		values.put(GIG_ARCHIVED, archived);
		values.put(GIG_CREATED, created);
		values.put(GIG_MODIFIED, modified);
		db.insertOrThrow(TABLE_NAME_GIG, null, values);		
	}
	
	private Cursor getGigs()
	{
		SQLiteDatabase db = gigData.getReadableDatabase();
		
		// build where statement
		String sWhere = "1=1";
		if (gigFilter == NEW) sWhere = "gig_archived = '0'";
		else if (gigFilter == ARCHIVED) sWhere = "gig_archived = '1'";

		Cursor cursor = db.query(TABLE_NAME_GIG, FROM, sWhere, null, null, null, ORDER_BY);
		
		// To solve the following error for versions >= honeycomb:
	    // java.lang.RuntimeException: Unable to resume activity 
	    // java.lang.IllegalStateException: trying to requery an already closed cursor
		Log.d(TAG,"Version: " + Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT < 11) // < HONEYCOMB		
			startManagingCursor(cursor);
		
		return cursor;
	}
	
	/*
	 * 2012-10-09: Hier bekomme ich den Fehler index -1 requested, keine Ahnung warum,
	 * deswegen benutze ich die Funktion vorerst nicht
	 */
	/*
	private String getMaxCreatedDate()
	{
		String sMaxDate = "";
		SQLiteDatabase db = gigData.getReadableDatabase();	
		//Cursor cursor = db.rawQuery("select max(gig_created) from " + TABLE_NAME_GIG, null);
		Cursor cursor = db.rawQuery("select gig_created from " + TABLE_NAME_GIG, null);
		
		// To solve the following error for versions >= honeycomb:
	    // java.lang.RuntimeException: Unable to resume activity 
	    // java.lang.IllegalStateException: trying to requery an already closed cursor
		Log.d(TAG,"Version: " + Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT < 11) // < HONEYCOMB		
			startManagingCursor(cursor);
		
		Log.d(TAG,"Cursor Anzahl Elemente: " + cursor.getCount() + "; Cursor Anzahl Spalten: " + cursor.getColumnCount());
		
		if (cursor.getCount() != 0) 
		{
			try
			{
				sMaxDate = cursor.getString(1);
			}
			catch(CursorIndexOutOfBoundsException e)
			{
		        e.printStackTrace();
		        Log.d(TAG,"SQLException " + e.getMessage());
			}
		}
			
		return sMaxDate;
	}
	*/
	
	private void showGigs(Cursor cursor)
	{
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.gig_item,cursor,FROM,TO);
		setListAdapter(adapter);
	}
	
	public void loadGigsFromWebservice(String filter)
	{
	    TWebServiceTask webServiceTask = new TWebServiceTask(this,this,mActionBar,"Gigs aktualisieren","Bitte warten");
	    webServiceTask.execute("http://www.embarry.de/api/index.php?method=getGigs&param="+filter);	    	
	}
	
	// Function evaluates the web service result, handles some actions and informs the user	
	@Override
	public void onTaskResult(JSONObject jObject)
	{
		try
		{
			// decode JSON object and put values in array			
			Map<Integer,Map<String,String>> gigList = new HashMap<Integer,Map<String,String>>();
			Log.d(TAG,"Länge jObject: " + ((JSONArray)jObject.get("items")).length());
			for (Integer i=0; i<((JSONArray)jObject.get("items")).length(); i++)
			{
				JSONObject jGig = ((JSONArray)jObject.get("items")).getJSONObject(i).getJSONObject("item");
				Map<String, String> gigMap = new HashMap<String, String>();
				gigMap.put("gig_name", jGig.getString("gig_name"));
				gigMap.put("gig_location", jGig.getString("gig_location"));
				gigMap.put("gig_address", jGig.getString("gig_address"));
				gigMap.put("gig_date", jGig.getString("gig_date"));
				gigMap.put("gig_comment", jGig.getString("gig_comment"));
				gigMap.put("gig_admittance", jGig.getString("gig_admittance"));
				gigMap.put("gig_begin", jGig.getString("gig_begin"));
				gigMap.put("gig_archived", jGig.getString("gig_archived"));
				gigMap.put("gig_created", jGig.getString("gig_created"));
				gigMap.put("gig_modified", jGig.getString("gig_modified"));
				gigList.put(i, gigMap);
			}
			Log.d(TAG,"Länge Map-Liste: " + gigList.size());
			
			
			gigData = new DBData(this);
			// Vorerst werden jedesmal alle Gigs gelöscht und dann wieder alle geholt,
			// weil die Funktion getMaxCreatedDate Probleme bereitet
			gigData.clearTable(TABLE_NAME_GIG);			
					
			try
			{	
				// add Gigs
				for (Integer i=0; i<gigList.size(); i++)
				{
					addGig(gigList.get(i).get("gig_name"),
						   gigList.get(i).get("gig_location"),
						   gigList.get(i).get("gig_address"),
						   gigList.get(i).get("gig_date"),						   
						   gigList.get(i).get("gig_admittance"),
						   gigList.get(i).get("gig_begin"),
						   gigList.get(i).get("gig_comment"),
						   gigList.get(i).get("gig_archived"),
						   gigList.get(i).get("gig_created"),
						   gigList.get(i).get("gig_modified"));
				}
				Cursor cursor = getGigs();
				showGigs(cursor);
			}
			finally
			{			
				gigData.close();
			}				
		}
		catch (JSONException e)		
		{
	        e.printStackTrace();
	        Log.d(TAG,"JSONException " + e.getMessage());
		}       				
	}
}

