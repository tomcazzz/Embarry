package library;

import myandroidpackages.EmbarryFans.R;
import myandroidpackages.EmbarryFans.R.anim;
import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class TFader 
{
	// constants
	public final static int DEFAULT_DURATION = 1000;
	private final static int IN = 0;
	private final static int OUT = 1;
	
	// methods
	public static void fadeIn(Activity oActivity, int iViewID, int iDuration)
	{
		fade(IN,oActivity,iViewID,iDuration);
	}
	
	public static void fadeOut(Activity oActivity, int iViewID, int iDuration)
	{
		fade(OUT,oActivity,iViewID,iDuration);
	}

	private static void fade(int iDirection, Activity oActivity, int iViewID, int iDuration)
	{
		Animation oAnimation = null;
		
		switch(iDirection)
		{
			case IN:
				oAnimation = AnimationUtils.loadAnimation(oActivity, R.anim.alpha_in);
				break;
			case OUT:
				oAnimation = AnimationUtils.loadAnimation(oActivity, R.anim.alpha_out);
				break;
		}
		if (oAnimation == null) return;
		
		oAnimation.reset();
		oAnimation.setDuration((long)iDuration);
		
		// find View by its ID
		View oView = oActivity.findViewById(iViewID);
		
		// cancel any pending animation and start it
		if (oView != null)
		{
			oView.clearAnimation();
			oView.startAnimation(oAnimation);
		}
	}
}
