package library;

import myandroidpackages.EmbarryFans.R;
import myandroidpackages.EmbarryFans.R.raw;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

// This class is used for playing background music
public class TMusicService extends Service implements MediaPlayer.OnErrorListener 
{
	private static final String TAG = "MusicService";
	
	MediaPlayer mPlayer = null;
	private int length = 0;
	
    private final IBinder mBinder = new ServiceBinder();

    public TMusicService() { }

    public class ServiceBinder extends Binder 
    {
     	 TMusicService getService()
    	 {
    		return TMusicService.this;
    	 }
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
    	return mBinder;
    }	
	
	@Override
	public void onCreate() 
	{
		Toast.makeText(this, "Starte Hintergrundmusik...", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate"); 
		
		mPlayer = MediaPlayer.create(this, R.raw.little_man); 
		if(mPlayer != null)
		{		
			mPlayer.setLooping(true); // Set looping
			mPlayer.setVolume(20,20);
			
			mPlayer.setOnErrorListener(new OnErrorListener() 
			{
				public boolean onError(MediaPlayer mp, int what, int extra)
				{
					onError(mPlayer, what, extra);
					return true;
				}
			});
        }
	}

	@Override
	public void onDestroy() 
	{
		Toast.makeText(this, "Hintergrundmusik gestoppt", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		if(mPlayer != null)
		{
			try
			{
				mPlayer.stop();
				mPlayer.release();
			}
			finally 
			{
				mPlayer = null;
			}
		}
	}
	/*
	@Override
	public void onStart(Intent intent, int startid) 
	{
		Toast.makeText(this, "MusicService Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		mPlayer.start();
	}
	*/
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid) 
	{ 
		super.onStartCommand(intent, flags, startid); 
		
		// this method is called after the service has been killed from outside
		//Toast.makeText(this, "Hintergrundmusik reaktiviert", Toast.LENGTH_LONG).show();
		Log.d(TAG, "Received start id " + startid + ": " + intent);
		
        
		mPlayer.start();
		
		// with following return value, system tries to start the service again after it
		// has been killed. This is not what we want
		// We want this service to continue running until it is explicitly
        // stopped, so return sticky.
		//return Service.START_STICKY;	
		
		// With following return value the Service will not be started again if killed.
		return Service.START_NOT_STICKY;		
	}
	
	public void pauseMusic()
	{
		Log.d(TAG,"TMusicService mPlayer.isPlaying():" + mPlayer.isPlaying());
		if(mPlayer.isPlaying())
		{
			mPlayer.pause();
			length=mPlayer.getCurrentPosition();
		}
	}

	public void resumeMusic()
	{
		if(!mPlayer.isPlaying())
		{
			mPlayer.seekTo(length);
			mPlayer.start();
		}
	}

	public void stopMusic()
	{
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}	
	
	public boolean onError(MediaPlayer mp, int what, int extra) 
	{
		Toast.makeText(this, "Media Player failed", Toast.LENGTH_SHORT).show();
		if(mPlayer != null)
		{
			try
			{
				mPlayer.stop();
				mPlayer.release();
			}
			finally 
			{
				mPlayer = null;
			}
		}
		return false;
	}	
}