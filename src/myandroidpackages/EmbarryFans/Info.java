package myandroidpackages.EmbarryFans;

import library.TActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public class Info extends Activity implements OnClickListener
{
	private TActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.info);
		
		View bOK = findViewById(R.id.info_ok_button);
		bOK.setOnClickListener(this);	
		
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.info_title);

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
    		case R.id.info_ok_button:
    			finish();
    			break;    		
    	}
    }	
}