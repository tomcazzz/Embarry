package library;

import java.io.IOException;
import java.io.InputStream;

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
import android.util.Log;
import android.widget.Toast;

public class TWebServiceTask extends AsyncTask<String, Integer, JSONObject>
{
	private static final String TAG = "TWebServiceTask";
	
    private ProgressDialog dialog;
    private String sProgressTitle;
    private String sProgressText;
    private HttpResponse response;
    private TTaskResultReceiver resultReceiver;  
    private Activity hostActivity;
    private TActionBar mActionBar;

    //	Used as handler to cancel task if back button is pressed    
    private AsyncTask<String, Integer, JSONObject> updateTask = null;
    
	public TWebServiceTask(Activity activity, TTaskResultReceiver rReceiver, TActionBar actionBar, String sTitle, String sText)
	{
		//Log.d(TAG,"TWebServiceTask:constructor called");
		resultReceiver = rReceiver;
		mActionBar = actionBar;
		dialog = new ProgressDialog(activity);
		sProgressTitle = sTitle;
		sProgressText = sText;
		hostActivity = activity;
	}

	@Override
	protected void onPreExecute()
	{
		//Log.d(TAG,"TWebServiceTask:onPreExecure()");
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
	    
	    dialog.setTitle(sProgressTitle);
	    dialog.setMessage(sProgressText);
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
	    	Toast.makeText(hostActivity, "WebService wurde abgebrochen", Toast.LENGTH_SHORT).show();
	    else 
	    {	        	
	    	resultReceiver.onTaskResult(response);
	    }
	}
}	    
