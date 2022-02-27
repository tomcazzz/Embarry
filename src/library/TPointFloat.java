package library;

import android.graphics.Rect;

public class TPointFloat 
{
	// public members
	public float x;
	public float y;
	
	// constructors
	public TPointFloat(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public TPointFloat(TGeoPoint gP)
	{
		this.x = gP.getLatitude();
		this.y = gP.getLongitude();
	}
	
	// Get rect out of Point and radius (=circle) to create surrounding rect.
	// add just increase the size of the rect 2 pixel in height and length 
	public Rect getRect(float radius)
	{
		return new Rect(	(int)(this.x-radius-1),
							(int)(this.y-radius-1),
							(int)(this.x+radius+1),
							(int)(this.y+radius+1));
	}	
	
	// Get rect out of two points after locating position of p
	// add some more pixels...
	public Rect getRect(TPointFloat p)
	{
		Rect rect;
		
		if (p.x < this.x)
		{
			if (p.y < this.y) rect = new Rect((int)p.x-3,(int)p.y-3,(int)this.x+3,(int)this.y+3);
			else rect = new Rect((int)p.x-3,(int)this.y-3,(int)this.y+3,(int)p.y+3);
		}
		else
		{
			if (p.y < this.y) rect = new Rect((int)this.x-3,(int)p.y-3,(int)p.x+3,(int)this.y+3);
			else rect = new Rect((int)this.x-3,(int)this.y-3,(int)p.x+3,(int)p.y+3);
		}
		return rect;
	}
}
