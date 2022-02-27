package myandroidpackages.EmbarryFans;

import library.TActionBar;
import library.TApplication;
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

public class VideoList extends ListActivity implements Constants
{
	private static final String TAG = "VideoList";
	
	// vars for db usage
	private static String[] FROM = {_ID, VIDEO_NAME, VIDEO_WHO, VIDEO_WHEN, VIDEO_YOUTUBE_ID};
	private static int[] TO = {R.id.video_id,R.id.video_name,R.id.video_who,R.id.video_when,R.id.video_youtube_id};
	private static String ORDER_BY = VIDEO_NAME + " ASC";

	// objects
	private DBData videoData;
	private TActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// set view
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_list);
		
        // init action bar (for API versions >= 11 and Android >= 3.0
        // for versions below that, the overflow action button is shown down the screen
		// TODO: implement settings for versions < 11 (refresh action)
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.videolist_title);
        
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
        /*
        mActionBar.addActionIcon(R.drawable.icon_refresh,
            new OnClickListener() 
        	{
                @Override
                public void onClick(View v) 
                {
                	// call web service to refresh video list
                	loadNewsFromWebservice(ALL);
                }
            }); 
        */
		Log.d(TAG,"OnCreate");
		videoData = new DBData(this);
		
		// Caution! It seems as if after dropping the table, table won't be created
		// because onCreate in VideoData is not called anymore -> only way to to this
		// is to increment the database version.
		videoData.clearTable(TABLE_NAME_VIDEO);
		try
		{	
			Cursor cursor = getVideos();

			// Add static data if video table is empty
			// In a later version of this app the video list will be populated by a web service call using joomla db
			if (cursor.getCount() == 0)
			{
				addVideo("Tut Uns Leid","embarryband","17.05.2011","R9WlYb6VHVw");
				addVideo("Believe","embarryband","09.04.2011","_fagiQQGJqc");
				addVideo("Heaven","embarryband","18.01.2010","3-0wW-8wkEE");
				addVideo("Circle of Live","embarryband","18.01.2010","EfavWVBXgrw");
				addVideo("Memories","embarryband","13.12.2008","tMeI9zOf-1A");
				addVideo("Blue Rose","embarryband","18.01.2010","6fLseZaedVo");	
				addVideo("Little Man","embarryband","30.12.2008","gmHM59CtpGo");
			}
			showVideos(cursor);
		}
		finally
		{			
			videoData.close();
		}
	}
	
	
	@Override
    protected void onResume() 
    {
		Log.d(TAG,"OnResume");
		super.onResume();     
		videoData = new DBData(this);
		try
		{	
			Cursor cursor = getVideos();
			showVideos(cursor);
		}
		finally
		{
			videoData.close();
		}
    }

    // react on list item clicks
    @Override
    public void onListItemClick(ListView parent, View v,int position, long id)
    {
    	Intent i;
    	Log.d(TAG,"List Item clicked. Postion: " + position + "; ID: " + id);
    	
    	Cursor cursor = (Cursor) getListAdapter().getItem(position);
    	
		i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+cursor.getString(4)));

		// Check whether youtube app is installed (it's integrated in some mobile phones)
		// otherwise intent chooser is started
    	if (TApplication.isInstalled(VideoList.this, "com.google.android.youtube"))
    		i.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
    	startActivity(i);
    }
    
	private void addVideo(String name, String who, String when, String youtube_id)
	{
		SQLiteDatabase db = videoData.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(VIDEO_NAME, name);
		values.put(VIDEO_WHO, who);
		values.put(VIDEO_WHEN, when);
		values.put(VIDEO_YOUTUBE_ID, youtube_id);
		db.insertOrThrow(TABLE_NAME_VIDEO, null, values);		
	}
	
	private Cursor getVideos()
	{
		SQLiteDatabase db = videoData.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_NAME_VIDEO, FROM, null, null, null, null, ORDER_BY);
		
		// To solve the following error for versions >= honeycomb:
	    // java.lang.RuntimeException: Unable to resume activity 
	    // java.lang.IllegalStateException: trying to requery an already closed cursor
		Log.d(TAG,"Version: " + Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT < 11) // < HONEYCOMB		
			startManagingCursor(cursor);
		
		return cursor;
	}
	
	private void showVideos(Cursor cursor)
	{
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.video_item,cursor,FROM,TO);
		setListAdapter(adapter);
	}	
}
