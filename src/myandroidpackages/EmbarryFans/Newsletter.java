package myandroidpackages.EmbarryFans;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import library.TActionBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Newsletter extends Activity implements OnClickListener
{
	// Define constant TAG for Log
	private static final String TAG = "Newsletter";
	
	private static final String SUBSCRIBE = "subscribe";
	private static final String UNSUBSCRIBE = "unsubscribe";
	private TActionBar mActionBar;	
	private Boolean bHTMLFormat = true;
	private EditText editTextName;
	private EditText editTextEmail;

	// inner class for the web service
	private class MyWebServiceTask extends AsyncTask<String, Integer, JSONObject>
	{
		private static final String TAG = "WebServiceTask";
	    private ProgressDialog dialog;
	    private String message;
	    private HttpResponse response;
	    private Newsletter aNewsletter;
	    private TActionBar mActionBar;	    

	    //Used as handler to cancel task if back button is pressed
	    private AsyncTask<String, Integer, JSONObject> updateTask = null;

	    public MyWebServiceTask(Newsletter newsletter, TActionBar actionBar, String ProgressMessage)
	    {
	    	aNewsletter = newsletter;
	    	mActionBar = actionBar;
	    	dialog = new ProgressDialog(newsletter);
	    	message = ProgressMessage;
	    }
	    
	    @Override
	    protected void onPreExecute()
	    {
	        updateTask = this;
	        dialog.setOnDismissListener(new OnDismissListener() 
	        {               
	            @Override
	            public void onDismiss(DialogInterface dialog) 
	            {
	                updateTask.cancel(true);
	            }
	        });

	        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
	        {
	            public void onCancel(DialogInterface dialog) 
	            {
	            	updateTask.cancel(true);
	                //finish();
	            }
	        });
	        
	        dialog.setTitle("Webservice");
	        dialog.setMessage(message);
	        dialog.show();
	        mActionBar.showProgressBar();
	    }

	    @Override
	    protected JSONObject doInBackground(String... urls) 
	    {
	        if(isCancelled()) return null;
	        JSONObject jsonObject = null;
	        Log.d(TAG,"URL: " + urls[0]);
	        StringBuilder stringBuilder = new StringBuilder();
	        HttpGet httpGet = new HttpGet(urls[0]);
		    HttpClient client = new DefaultHttpClient();
		    
		    try
		    {
		    	response = client.execute(httpGet);
		        HttpEntity entity = response.getEntity();        
		        InputStream stream = entity.getContent();
		        int b;
		        while ((b = stream.read()) != -1)
		        {
		            stringBuilder.append((char) b);
		        }	    
		    	jsonObject = new JSONObject(stringBuilder.toString());
		    }
		    catch (ClientProtocolException e)
		    {
		    	e.printStackTrace();
		    	Log.d(TAG,"ClientProtocolException " + e.getMessage());
		    }
		    catch (IOException e)
		    {
		    	e.printStackTrace();
		    	Log.d(TAG,"IOException " + e.getMessage());
		    }
		    catch (JSONException e)
		    {
		        e.printStackTrace();
		        Log.d(TAG,"JSONException " + e.getMessage());
		    }

		    return jsonObject;	    
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress)
	    {
	    }

	    @Override
	    protected void onPostExecute(JSONObject response)
	    {
	        dialog.dismiss();
	        mActionBar.hideProgressBar();
	        if (response == null)
	        	Toast.makeText(aNewsletter, "WebService wurde abgebrochen", 1000).show();
	        else 
	        {	        	
	        	aNewsletter.notifyUser(response);
	        }
	    }
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        // call onCreate of parent class Activity
    	super.onCreate(savedInstanceState);
    	
        // set main view
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsletter);
        
        // buttons
        View bSubscribe = findViewById(R.id.newsletter_subscribe);
        View bUnsubscribe = findViewById(R.id.newsletter_unsubscribe);    
        
        // edit texts
        editTextName = (EditText) findViewById(R.id.newsletter_name_edit);
        editTextEmail = (EditText) findViewById(R.id.newsletter_email_edit); 
        
        // prevent soft keyboard from showing. It should only be shown when user touches one of the edit views
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // set default-value of Toggle
        ToggleButton tButton = (ToggleButton) findViewById(R.id.newsletter_toggle);
        tButton.setChecked(true);
        
        // add event listeners
        bSubscribe.setOnClickListener(this);
        bUnsubscribe.setOnClickListener(this);  	
    	
        // init action bar (for API versions >= 11 and Android >= 3.0
        // for versions below that, the overflow action button is shown down the screen
        mActionBar = (TActionBar) findViewById(R.id.myactionbar);
        mActionBar.setTitle(R.string.newsletter_title);

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
    	String sName = editTextName.getText().toString();
    	String sEmail = editTextEmail.getText().toString();

		switch(v.getId())
    	{
    		case R.id.newsletter_subscribe:
    			if (sName.equals(""))
    			{
    				Toast.makeText(this, "Du hast keinen Namen angegeben!", Toast.LENGTH_SHORT).show();
    			} 
    			else
    			{	
	    			if (!sEmail.equals(""))
	    			{
	    				if (isValidEmail(sEmail)) setNewsletter(SUBSCRIBE);
	    				else Toast.makeText(this, "Du hast keine gültige Email-Adresse angegeben!", Toast.LENGTH_SHORT).show();
	    			}
	    			else Toast.makeText(this, "Du hast keine Email-Adresse angegeben!", Toast.LENGTH_SHORT).show();
    			}		   				
    			break;
    		case R.id.newsletter_unsubscribe:
    			if (!sEmail.equals(""))
    			{
    				if (isValidEmail(sEmail)) setNewsletter(UNSUBSCRIBE);
    				else Toast.makeText(this, "Du hast keine gültige Email-Adresse angegeben!", Toast.LENGTH_SHORT).show();
    			}
    			else Toast.makeText(this, "Du hast keine Email-Adresse angegeben!", Toast.LENGTH_SHORT).show();
    			break;
    	}
    }
    
	public void onToggleClicked(View v) 
	{
	    // Perform action on clicks
	    if (((ToggleButton) v).isChecked()) 
	    {
	        bHTMLFormat = true;
	    } 
	    else 
	    {
	        bHTMLFormat = false;
	    }
	}	
	
	public void notifyUser(JSONObject jObject)
	{
		try
		{
			String sName = jObject.getString("name");
			String sMethod = jObject.getString("method");
			String sAction = jObject.getString("action");
			Log.d(TAG,"Name: " + sName + "; Method: " + sMethod + "; Action: " + sAction);
			
			if (sMethod.equals("subscribe"))
			{
				if (sAction.equals("none"))
				{
					Toast.makeText(this, "Hallo " + sName + ", Du abonnierst den Newsletter bereits und das gewählte Format ist schon korrekt eingestellt.", Toast.LENGTH_LONG).show();
				}
				else if (sAction.equals("format_changed"))
				{
					Toast.makeText(this, "Hallo " + sName + ", Du erhälst den nächsten Newsletter nun im gewünschten Format.", Toast.LENGTH_LONG).show();
				}
				else if (sAction.equals("enabled"))
				{
					Toast.makeText(this, "Hallo " + sName + ", der Newsletter wurde nun für Dich aktiviert.", Toast.LENGTH_LONG).show();
				}
				else if (sAction.equals("subscribed"))
				{
					Toast.makeText(this, "Hallo " + sName + ", Du hast den Embarry Newsletter nun abonniert.", Toast.LENGTH_LONG).show();
				}
			}
			else if (sMethod.equals("unsubscribe"))
			{
				Log.d(TAG,"unsubscribe..");
				if (sAction.equals("none"))
				{
					Toast.makeText(this, "Hallo " + sName + ", Du kannst den Newsletter nicht abbestellen, weil für Dich kein Eintrag existiert.", Toast.LENGTH_LONG).show();
				}
				else if (sAction.equals("disabled"))
				{
					Toast.makeText(this, "Hallo " + sName + ", der Newsletter wurde erfolgreich abbestellt.", Toast.LENGTH_LONG).show();
				}				
			}			
		}
		catch (JSONException e)		
		{
	        e.printStackTrace();
	        Log.d(TAG,"JSONException " + e.getMessage());
		}  		
	}
	
	private void setNewsletter(String action)
	{
        ToggleButton tButton = (ToggleButton) findViewById(R.id.newsletter_toggle);
		int iHTML = bHTMLFormat == true ? 1 : 0;
		String sAction = "";
		String sName = editTextName.getText().toString();
		String sEmail = editTextEmail.getText().toString();

		try
		{
			// sName can contain spaces. These must be UTF-8 encoded. Otherwise window leaked error will occur
			sName = URLEncoder.encode(sName,"UTF-8");
		}
	    catch (UnsupportedEncodingException e) 
	    { 
	    	Log.d(TAG,"UnsupportedEncodingException: " + e.getMessage());
	        e.printStackTrace(); 
	    }
	    
		if (action.equals(SUBSCRIBE)) sAction = "Newsletter abonnieren...";
		else if (action.equals(UNSUBSCRIBE)) sAction = "Newsletter abbestellen...";
		
		/*
		return (assoc. array):
		
			name -> to name the user
			method -> to know which action was required
			action_performed (format_changed,subscribed/enabled/disabled/none)	
		*/
		
	    MyWebServiceTask webServiceTask = new MyWebServiceTask(this,mActionBar,sAction);
	    webServiceTask.execute("http://www.embarry.de/api/index.php?method=setNewsletter&param="+action+"&name="+sName+"&email="+sEmail+"&html="+iHTML);
	    Log.d(TAG,"http://www.embarry.de/api/index.php?method=setNewsletter&param="+action+"&name="+sName+"&email="+sEmail+"&html="+iHTML);
	}


	private Boolean isValidEmail(CharSequence target) 
	{	
	    try 
	    {
	    	final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	    	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	    	          "\\@" +
	    	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	    	          "(" +
	    	          "\\." +
	    	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	    	          ")+"
	    	      );
	    	
	    	// Since API Level 8 only:
	    	//return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    	return EMAIL_ADDRESS_PATTERN.matcher(target).matches();
	    	
	    }
	    catch (NullPointerException exception) 
	    {
	    	return false;
	    }	    
	}

}
