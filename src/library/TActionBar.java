package library;

import myandroidpackages.EmbarryFans.R;
import myandroidpackages.EmbarryFans.R.id;
import myandroidpackages.EmbarryFans.R.layout;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TActionBar extends RelativeLayout 
{
	private final static String TAG = "MyActionBar";
    private LayoutInflater mInflater;
    private ImageView mLogoView;
    private TextView mTitleView;
    private ProgressBar mProgress;
    private LinearLayout mActionIconContainer;
    private Context mContext;
    
    public TActionBar(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout barView = (RelativeLayout) mInflater.inflate(R.layout.actionbar, null);
        addView(barView);
        mLogoView = (ImageView) barView.findViewById(R.id.actionbar_home_logo);
        mProgress = (ProgressBar) barView.findViewById(R.id.actionbar_progress);
        mTitleView = (TextView) barView.findViewById(R.id.actionbar_title);
        mActionIconContainer = (LinearLayout) barView.findViewById(R.id.actionbar_actionIcons);
    }

    public void setHomeLogo(int resId) 
    {
    	setHomeLogo(resId, null);
    }
    
    public void setHomeLogo(int resId, OnClickListener onClickListener) 
    {
    	mLogoView.setImageResource(resId);
    	mLogoView.setVisibility(View.VISIBLE);
    	mLogoView.setOnClickListener(onClickListener);
    }

    public void setTitle(CharSequence title) 
    {
    	mTitleView.setText(title);
    }
    
    public void setTitle(int resid) 
    {
    	mTitleView.setText(resid);
    }

    public void showProgressBar() 
    {
    	setProgressBarVisibility(View.VISIBLE);
    }
    
    public void hideProgressBar() 
    {
    	setProgressBarVisibility(View.GONE);
    }
    
    public boolean isProgressBarVisible() 
    {
    	return mProgress.getVisibility() == View.VISIBLE;
    }
    
    private void setProgressBarVisibility(int visibility) 
    {
    	mProgress.setVisibility(visibility);
    }
    
    // Adds ActionIcons to the ActionBar (adds to the left-end)
    // @param iconResourceId
    // @param onClickListener to handle click actions on the ActionIcon.
    public void addActionIcon(int iconResourceId, OnClickListener onClickListener) 
    {
        // Inflate
        View view = mInflater.inflate(R.layout.actionbar_icon, mActionIconContainer, false);
        ImageButton imgButton = (ImageButton) view.findViewById(R.id.actionbar_item);   
        imgButton.setImageResource(iconResourceId);
        imgButton.setOnClickListener(onClickListener);
        mActionIconContainer.addView(view, mActionIconContainer.getChildCount());
    }
    
    // Changes the picture of an action icon
    public void changeActionIcon(int iChildIndex, int iToResourceId)
    {
    	ImageButton imgButton = (ImageButton) mActionIconContainer.getChildAt(iChildIndex);
    	imgButton.setImageResource(iToResourceId);
    }

    // Removes the action icon from the given index
    // @param index 0-based index corresponding to the ActionIcon to remove
    // @return <code>true</code> if the item was removed
    public boolean removeActionIconAt(int index) 
    {
    	int count = mActionIconContainer.getChildCount();
    	if (count > 0 && index >= 0 && index < count) 
    	{
	    	mActionIconContainer.removeViewAt(index);
	    	return true;
    	}
    	return false;
    }
}
