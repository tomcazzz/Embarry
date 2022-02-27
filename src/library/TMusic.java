package library;

import android.content.Context;
import android.media.MediaPlayer;

// This class should be used for normal sound effects. This is not meant to be a service
public class TMusic 
{
	private static MediaPlayer mp = null;

	// play music
    public static void play(Context context, int resource) 
    {
    	stop(context);

		mp = MediaPlayer.create(context, resource);
		mp.setLooping(true);
		mp.start();
    }
   

    // stop music
    public static void stop(Context context) 
    { 
    	if (mp != null) 
    	{
    		mp.stop();
    		mp.release();
    		mp = null;
    	}
    }
}