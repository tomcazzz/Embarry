package myandroidpackages.EmbarryFans;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns 
{
	// Interface for the database columns
	
	// Gigs
	public static final String TABLE_NAME_GIG = "gig";
	public static final String GIG_ID = "gig_id";	
	public static final String GIG_NAME = "gig_name";
	public static final String GIG_LOCATION = "gig_location";
	public static final String GIG_ADDRESS = "gig_address";
	public static final String GIG_DATE = "gig_date";
	public static final String GIG_ADMITTANCE = "gig_admittance";
	public static final String GIG_BEGIN = "gig_begin";
	public static final String GIG_COMMENT = "gig_comment";
	public static final String GIG_ARCHIVED = "gig_archived";
	public static final String GIG_CREATED = "gig_created";
	public static final String GIG_MODIFIED = "gig_modified";
	
	// News
	public static final String TABLE_NAME_NEWS = "news";
	public static final String NEWS_ID = "news_id";	
	public static final String NEWS_TITLE = "news_title";
	public static final String NEWS_TEXT = "news_text";
	public static final String NEWS_CREATED = "news_created";
	public static final String NEWS_ARCHIVED = "news_archived";	
	
	// Videos
	public static final String TABLE_NAME_VIDEO = "video";
	public static final String VIDEO_ID = "video_id";	
	public static final String VIDEO_NAME = "video_name";
	public static final String VIDEO_WHO = "video_who";
	public static final String VIDEO_WHEN = "video_when";
	public static final String VIDEO_YOUTUBE_ID = "video_youtube_id";	

	// Links
	public static final String TABLE_NAME_LINK = "links";
	public static final String LINK_ID = "link_id";	
	public static final String LINK_NAME = "link_name";
	public static final String LINK_URL = "link_url";
	public static final String LINK_COMMENT = "link_comment";
	public static final String LINK_CREATED = "link_created";	
}

