package library;

import myandroidpackages.EmbarryFans.R;
import myandroidpackages.EmbarryFans.R.anim;
import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class TRotator 
{
	// constants
	public final static int DEFAULT_DURATION = 1000;
	public final static int INFINITE = -1;
	
	// methods
	public static void rotate(Activity oActivity, int iViewID, int iDuration, int iCount, boolean bToggle)
	{
		Animation oAnimation = null;
		
		oAnimation = AnimationUtils.loadAnimation(oActivity, R.anim.rotate);
		if (oAnimation == null) return;
		
		oAnimation.reset();
		oAnimation.setDuration((long)iDuration);
		
		// Hint: iCount = 0: Animation is run once; 1: Animation is repeated once (run twice)
		oAnimation.setRepeatCount(iCount);
		
		// find View by its ID
		View oView = oActivity.findViewById(iViewID);
		
		// cancel any pending animation and start it
		if (oView != null)
		{		
			if (oView.getAnimation() == null) oView.startAnimation(oAnimation);
			else
			{
				// if bToogle is true then animation will be stopped
				if (bToggle) oView.clearAnimation();
				else oView.startAnimation(oAnimation);				
			}
		}
	}	
		
}
