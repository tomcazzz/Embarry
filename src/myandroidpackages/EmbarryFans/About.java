package myandroidpackages.EmbarryFans;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class About extends Activity implements OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		View bOK = findViewById(R.id.about_ok_button);
		bOK.setOnClickListener(this);		
	}
	
    // react on button clicks
    @Override
    public void onClick(View v)
    {
    	switch(v.getId())
    	{
    		case R.id.about_ok_button:
    			finish();
    			break;    		
    	}
    }	
}