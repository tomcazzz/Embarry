package myandroidpackages.EmbarryFans;

import library.TActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Cast extends Activity implements OnClickListener
{
	private TActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cast);
		
		View tChris = findViewById(R.id.cast_chris);
		tChris.setOnClickListener(this);
		View tTom = findViewById(R.id.cast_tom);
		tTom.setOnClickListener(this);
		View tJan = findViewById(R.id.cast_jan);
		tJan.setOnClickListener(this);
		View tMarkus = findViewById(R.id.cast_markus);
		tMarkus.setOnClickListener(this);
				
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.cast_title);

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
	}
	
    // react on button clicks
    @Override
    public void onClick(View v)
    {
    	switch(v.getId())
    	{
    		case R.id.cast_chris:
    			this.showImage(R.drawable.chris);
    			break;    		
    		case R.id.cast_tom:
    			this.showImage(R.drawable.tom3);
    			break;    		
    		case R.id.cast_jan:
    			this.showImage(R.drawable.jan);
    			break;    		
    		case R.id.cast_markus:
    			this.showImage(R.drawable.markus);
    			break;    		
    	}
    }	
    
    private void showImage(int iResID) 
    {
        final Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() 
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(this);
        imageView.setClickable(true);
        imageView.setOnClickListener(new OnClickListener() 
        {
	        @Override
	        public void onClick(View v) 
	        {
	            builder.cancel();
	        }
        });        
        imageView.setImageResource(iResID);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }    
}