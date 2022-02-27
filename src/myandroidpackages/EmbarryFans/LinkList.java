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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LinkList extends ListActivity implements Constants,TTaskResultReceiver
{
	private static final String TAG = "LinkList";
	
	// filters for querying links fom joomla db
	private static final String ALL = "all";
	private static final String NEW = "new";
	private static final String ARCHIVED = "archived";
	
	// vars for db usage
	private static String[] FROM = {_ID, LINK_NAME, LINK_URL, LINK_COMMENT, LINK_CREATED};
	private static int[] TO = {R.id.link_id,R.id.link_name,R.id.link_url,R.id.link_comment,R.id.link_created};
	private static String ORDER_BY = LINK_NAME + " ASC";

	// objects
	private DBData linkData;
	private TActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// set view
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.link_list);
		
        // init action bar (for API versions >= 11 and Android >= 3.0
        // for versions below that, the overflow action button is shown down the screen
		// TODO: implement settings for versions < 11 (refresh action)
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.linklist_title);
        
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

        mActionBar.addActionIcon(R.drawable.icon_refresh,
            new OnClickListener() 
        	{
                @Override
                public void onClick(View v) 
                {
                	// call web service to refresh link list (all links!)
                	loadLinksFromWebservice(ALL);
                }
            }); 

		Log.d(TAG,"OnCreate");
		linkData = new DBData(this);
		
		// Caution! It seems as if after dropping the table, table won't be created
		// because onCreate in DBData is not called anymore -> only way to to this
		// is to increment the database version.
		try
		{	
			Cursor cursor = getLinks();
			
			// if list is empty, get links from webservice (e.g. when app has been installed)
			if (cursor.getCount() == 0) loadLinksFromWebservice(ALL);
			else
			{
				// refresh automatically if set in settings
				Log.d(TAG,"getLinksAutomation(this): " + Prefs.getLinksAutomation(this));
				if (Prefs.getLinksAutomation(this)) loadLinksFromWebservice(ALL);	
			}		
			showLinks(cursor);
		}
		finally
		{			
			linkData.close();
		}
	}
	
	
	@Override
    protected void onResume() 
    {
		Log.d(TAG,"OnResume");
		super.onResume();     
		linkData = new DBData(this);
		try
		{	
			Cursor cursor = getLinks();
			showLinks(cursor);
		}
		finally
		{
			linkData.close();
		}
    }

    // react on list item clicks
    @Override
    public void onListItemClick(ListView parent, View v,int position, long id)
    {
    	Intent i;
    	String URL;
    	Log.d(TAG,"List Item clicked. Postion: " + position + "; ID: " + id);
    	
    	Cursor cursor = (Cursor) getListAdapter().getItem(position);
    	Log.d(TAG,"Title: " + cursor.getString(1));
    	Log.d(TAG,"URL: " + cursor.getString(2));
    	Log.d(TAG,"Comment: " + cursor.getString(3));
    	Log.d(TAG,"Created: " + cursor.getString(4));
    	// If necessary, correct URL
    	URL = cursor.getString(2);
    	if (!URL.startsWith("http://") && !URL.startsWith("https://"))
    		URL = "http://" + URL;

		i = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
    	startActivity(i);
    }
    
	private void addLink(String name, String url, String comment, String created)
	{
		SQLiteDatabase db = linkData.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LINK_NAME, name);
		values.put(LINK_URL, url);
		values.put(LINK_COMMENT, comment);
		values.put(LINK_CREATED, created);
		db.insertOrThrow(TABLE_NAME_LINK, null, values);		
	}
	
	private Cursor getLinks()
	{
		SQLiteDatabase db = linkData.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_NAME_LINK, FROM, null, null, null, null, ORDER_BY);
		
		// To solve the following error for versions >= honeycomb:
	    // java.lang.RuntimeException: Unable to resume activity 
	    // java.lang.IllegalStateException: trying to requery an already closed cursor
		Log.d(TAG,"Version: " + Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT < 11) // < HONEYCOMB		
			startManagingCursor(cursor);
		
		return cursor;
	}
	
	private void showLinks(Cursor cursor)
	{
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.link_item,cursor,FROM,TO);
		setListAdapter(adapter);
		/*
		TextView t = (TextView) findViewById(R.id.link_date);
		Log.d(TAG,"textview: " + t.getText());
		t.setText(HelperFunctions.convertDate_DB2Locale((String)t.getText()));
		*/
	}	
	
	public void loadLinksFromWebservice(String filter)
	{
	    TWebServiceTask webServiceTask = new TWebServiceTask(this,this,mActionBar,"Links aktualisieren","Bitte warten");
	    webServiceTask.execute("http://www.embarry.de/api/index.php?method=getLinks&param="+filter);	    	
	}
	
	public void onTaskResult(JSONObject jObject)
	{
		try
		{
			// decode JSON object and put values in array			
			Map<Integer,Map<String,String>> linkList = new HashMap<Integer,Map<String,String>>();
			//Log.d(TAG,"Länge jObject: " + ((JSONArray)jObject.get("items")).length());
			for (Integer i=0; i<((JSONArray)jObject.get("items")).length(); i++)
			{
				JSONObject jlink = ((JSONArray)jObject.get("items")).getJSONObject(i).getJSONObject("item");
				Map<String, String> linkMap = new HashMap<String, String>();
				linkMap.put("link_name", jlink.getString("link_name"));
				linkMap.put("link_url", jlink.getString("link_url"));
				linkMap.put("link_comment", jlink.getString("link_comment"));
				linkMap.put("link_created", jlink.getString("link_created"));
				linkList.put(i, linkMap);
				
				//String linkName = ((JSONArray)jObject.get("links")).getJSONObject(0).getJSONObject("link").getString("link_name");
				Log.d(TAG,"link_name: " + linkMap.get("link_name"));
				Log.d(TAG,"link_url: " + linkMap.get("link_url"));
				Log.d(TAG,"link_comment: " + linkMap.get("link_comment"));
				Log.d(TAG,"link_created: " + linkMap.get("link_created"));
			}
			Log.d(TAG,"Länge Map-Liste: " + linkList.size());
			// make it simple: clear table and refresh it with web values
			linkData = new DBData(this);
			linkData.clearTable(TABLE_NAME_LINK);
			
			try
			{	
				// add links
				for (Integer i=0; i<linkList.size(); i++)
				{
					addLink(linkList.get(i).get("link_name"),
						   linkList.get(i).get("link_url"),
						   linkList.get(i).get("link_comment"),
						   linkList.get(i).get("link_created"));
				}
				Cursor cursor = getLinks();
				showLinks(cursor);
			}
			finally
			{			
				linkData.close();
			}				
		}
		catch (JSONException e)		
		{
	        e.printStackTrace();
	        Log.d(TAG,"JSONException " + e.getMessage());
		}       				
	}	
}
