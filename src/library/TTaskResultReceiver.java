package library;

import org.json.JSONObject;

// Interface which has to be implemented by classes which use TWebServiceTask
public interface TTaskResultReceiver 
{
	public void onTaskResult(JSONObject jObject);
}
