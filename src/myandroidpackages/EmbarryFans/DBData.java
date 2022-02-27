package myandroidpackages.EmbarryFans;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBData extends SQLiteOpenHelper implements Constants
{
	// Define constant TAG for Log
	private static final String TAG = "DBData";
	private static final String DATABASE_PATH = "/data/data/myandroidpackages.EmbarryFans/databases/";
	private static final String DATABASE_NAME = "embarry.db";
	private static final int DATABASE_VERSION = 1;
	
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
	public DBData(Context ctx)
    {		
    	super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }
    	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{   
		// on create is only called if database has been created before !!!
		Log.d(TAG,"OnCreate");
		
		// create gigs table
		db.execSQL("CREATE TABLE " + TABLE_NAME_GIG + " (" 
		+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "			
		+ GIG_NAME + " TEXT NOT NULL, "
		+ GIG_LOCATION + " TEXT NOT NULL, "
		+ GIG_ADDRESS + " TEXT NOT NULL, "
		+ GIG_DATE + " TEXT NOT NULL, "
		+ GIG_ADMITTANCE + " TEXT NOT NULL, "
		+ GIG_BEGIN + " TEXT NOT NULL, "
		+ GIG_COMMENT + " TEXT NOT NULL, "
		+ GIG_ARCHIVED + " TEXT NOT NULL, " 
		+ GIG_CREATED + " TEXT NOT NULL, "
		+ GIG_MODIFIED + " TEXT NOT NULL);");		

		// create news table
		db.execSQL("CREATE TABLE " + TABLE_NAME_NEWS + " (" 
		+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "			
		+ NEWS_TITLE + " TEXT NOT NULL, "
		+ NEWS_TEXT + " TEXT NOT NULL, "
		+ NEWS_CREATED + " TEXT NOT NULL, "
		+ NEWS_ARCHIVED + " TEXT NOT NULL);");
		
		// create videos table
		db.execSQL("CREATE TABLE " + TABLE_NAME_VIDEO + " (" 
		+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "			
		+ VIDEO_NAME + " TEXT NOT NULL, "
		+ VIDEO_WHEN + " TEXT NOT NULL, "
		+ VIDEO_WHO + " TEXT NOT NULL, "
		+ VIDEO_YOUTUBE_ID + " TEXT NOT NULL);");	
		
		// create links table
		db.execSQL("CREATE TABLE " + TABLE_NAME_LINK + " (" 
		+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "			
		+ LINK_NAME + " TEXT NOT NULL, "
		+ LINK_URL + " TEXT NOT NULL, "
		+ LINK_COMMENT + " TEXT NOT NULL, "
		+ LINK_CREATED + " TEXT NOT NULL);");			
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// method is called if DATABASE_VERSION has been changed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GIG);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NEWS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_VIDEO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LINK);
		
		// create db
		onCreate(db);
	}	

    private boolean DBExists()
    {
    	SQLiteDatabase checkDB = null;
 
    	try
    	{
    		checkDB = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
    	}
    	catch(SQLiteException e)
    	{
    		//database doesn't exist yet.
    		Log.d(TAG,"database doesn't exist yet");
    	}
    	
    	if(checkDB != null) checkDB.close();
    	
    	Log.d(TAG,"Database exists? " + (checkDB != null ? true : false));
    	return checkDB != null ? true : false;
    }
    
    // Delete table content
    public void clearTable(String cTableName)
    {
    	if (DBExists())
    	{
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	db.execSQL("DELETE FROM " + cTableName + ";");
    	}
    }    
}
