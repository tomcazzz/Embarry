package library;


// class for saving calendar reminder data
public class TReminder 
{
	public static long METHOD_ALERT = 0;
	
	private long lMethod;
	private long lMinutes;
	
	public TReminder(long method, long minutes)
	{
		lMethod = method;
		lMinutes = minutes;
	}
	
	public long getMethod()
	{
		return lMethod;
	}

	public long getMinutes()
	{
		return lMinutes;
	}
}
