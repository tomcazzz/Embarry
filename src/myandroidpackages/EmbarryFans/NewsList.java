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
import android.widget.SimpleCursorAdapter;

public class NewsList extends ListActivity implements Constants,TTaskResultReceiver
{
	private static final String TAG = "NewsList";
	
	// filters for querying news from joomla db
	private static final String ALL = "all";
	private static final String NEW = "new";
	private static final String ARCHIVED = "archived";
	
	private String newsFilter = ALL;
	
	// vars for db usage
	private static String[] FROM = {_ID, NEWS_TITLE, NEWS_TEXT, NEWS_CREATED, NEWS_ARCHIVED};
	private static int[] TO = {R.id.news_id,R.id.news_title,R.id.news_text,R.id.news_created, R.id.news_archived};
	private static String ORDER_BY = NEWS_CREATED + " DESC";

	// objects
	private DBData newsData;
	private TActionBar mActionBar;
	//private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// set view
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_list);
		
		if (getIntent().getExtras() != null)
			newsFilter = getIntent().getExtras().getString("NEWS_FILTER");
		
        // init action bar (for API versions >= 11 and Android >= 3.0
        // for versions below that, the overflow action button is shown down the screen
		// TODO: implement settings for versions < 11 (refresh action)
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.newslist_title);
        
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
                	// call web service to refresh news list
                	loadNewsFromWebservice(NEW);
                }
            }); 
        
		Log.d(TAG,"OnCreate");
		newsData = new DBData(this);
		
		// Caution! It seems as if after dropping the table, table won't be created
		// because onCreate in DBData is not called anymore -> only way to to this
		// is to increment the database version.	
		try
		{	
			// here the web service should be called to check whether there are new news		
			Cursor cursor = getNews();
			
			// if list is empty, get news from webservice (e.g. when app has been installed)
			if (cursor.getCount() == 0) loadNewsFromWebservice(NEW);
			else
			{
				// refresh automatically if set in settings
				Log.d(TAG,"getNewsAutomation(this): " + Prefs.getNewsAutomation(this));
				if (Prefs.getNewsAutomation(this)) loadNewsFromWebservice(NEW);		
			}
			
			showNews(cursor);
		}
		finally
		{			
			newsData.close();
		}
	}
	
	
	@Override
    protected void onResume() 
    {
		Log.d(TAG,"OnResume");
		super.onResume();     
		newsData = new DBData(this);
		try
		{	
			Cursor cursor = getNews();
			showNews(cursor);
		}
		finally
		{
			newsData.close();
		}
    }

    
	private void addNews(String title, String text, String created, String archived)
	{
		SQLiteDatabase db = newsData.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NEWS_TITLE, title);
		values.put(NEWS_TEXT, text);
		values.put(NEWS_CREATED, created);
		values.put(NEWS_ARCHIVED, archived);
		db.insertOrThrow(TABLE_NAME_NEWS, null, values);		
	}
	
	private Cursor getNews()
	{
		SQLiteDatabase db = newsData.getReadableDatabase();
		
		// build where statement
		String sWhere = "1=1";
		if (newsFilter == NEW) sWhere = "news_archived = '0'";
		else if (newsFilter == ARCHIVED) sWhere = "news_archived = '1'";

		Cursor cursor = db.query(TABLE_NAME_NEWS, FROM, sWhere, null, null, null, ORDER_BY);
		
		// To solve the following error for versions >= honeycomb:
	    // java.lang.RuntimeException: Unable to resume activity 
	    // java.lang.IllegalStateException: trying to requery an already closed cursor
		Log.d(TAG,"Version: " + Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT < 11) // < HONEYCOMB		
			startManagingCursor(cursor);
		
		return cursor;
	}
	
	private void showNews(Cursor cursor)
	{
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.news_item,cursor,FROM,TO);
		setListAdapter(adapter);
	}
	
	public void loadNewsFromWebservice(String filter)
	{
	    TWebServiceTask webServiceTask = new TWebServiceTask(this,this,mActionBar,"News aktualisieren","Bitte warten");
	    webServiceTask.execute("http://www.embarry.de/api/index.php?method=getNews&param="+filter);	    	
	}
	
	// Function evaluates the web service result, handles some actions and informs the user
	@Override
	public void onTaskResult(JSONObject jObject)
	{
		try
		{
			// decode JSON object and put values in array			
			Map<Integer,Map<String,String>> newsList = new HashMap<Integer,Map<String,String>>();
			Log.d(TAG,"Länge jObject: " + ((JSONArray)jObject.get("items")).length());
			for (Integer i=0; i<((JSONArray)jObject.get("items")).length(); i++)
			{
				JSONObject jNews = ((JSONArray)jObject.get("items")).getJSONObject(i).getJSONObject("item");
				Map<String, String> newsMap = new HashMap<String, String>();
				newsMap.put("news_title", jNews.getString("news_title"));
				newsMap.put("news_text", jNews.getString("news_text"));
				newsMap.put("news_created", jNews.getString("news_created"));
				newsMap.put("news_archived", jNews.getString("news_archived"));
				newsList.put(i, newsMap);
				
				Log.d(TAG,"news_title: " + newsMap.get("news_title"));
				Log.d(TAG,"news_text: " + newsMap.get("news_text"));
				Log.d(TAG,"news_created: " + newsMap.get("news_created"));
				Log.d(TAG,"news_archived: " + newsMap.get("news_archived"));
							
			}
			Log.d(TAG,"Länge Map-Liste: " + newsList.size());
			// make it simple: clear table and refresh it with web values			
			newsData = new DBData(this);
			newsData.clearTable(TABLE_NAME_NEWS);
			
			try
			{	
				// add News
				for (Integer i=0; i<newsList.size(); i++)
				{
					addNews(newsList.get(i).get("news_title"),
						   newsList.get(i).get("news_text"),
						   newsList.get(i).get("news_created"),
						   newsList.get(i).get("news_archived"));
				}
				Cursor cursor = getNews();
				showNews(cursor);
			}
			finally
			{			
				newsData.close();
			}				
		}
		catch (JSONException e)		
		{
	        e.printStackTrace();
	        Log.d(TAG,"JSONException " + e.getMessage());
		}       				
	}
}
