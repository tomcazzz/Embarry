package library;

import java.lang.Math;
import android.location.Location;

public class TGeoPoint
{
	// private members
	private float fLatitude;
	private float fLongitude;
	
	// constructor
	public TGeoPoint(float fLat, float fLong)
	{
		this.fLatitude = fLat;
		this.fLongitude = fLong;
	}
	
	// methods
	public float getLatitude()
	{
		return this.fLatitude;
	}
	
	public float getLongitude()
	{
		return this.fLongitude;
	}
	

	// returns the distance between two GeoPoints in km
	public static float getDistanceBetween(TGeoPoint p1, TGeoPoint p2)
	{
		float l1,l2,w1,w2,dg,dist;
		
		// convert to radian
		l1 = (float)Math.toRadians(p1.getLatitude());
		l2 = (float)Math.toRadians(p2.getLatitude());
		w1 = (float)Math.toRadians(p1.getLongitude());
		w2 = (float)Math.toRadians(p2.getLongitude());
		dg = w2-w1;
		
		dist = (float)(1.852f * 60.0f * 
		Math.toDegrees(Math.acos(Math.sin(l1)*Math.sin(l2)+ Math.cos(l1)*Math.cos(l2)*Math.cos(dg))));
		
		return Math.round(dist * 100.f) / 100.f;
	}	
	
	public float getDistanceToPoint(TGeoPoint p)
	{
		return TGeoPoint.getDistanceBetween(this, p);
	}	

	public static float getDistanceBetween(TGeoPoint p1, Location p2)
	{
		return TGeoPoint.getDistanceBetween(p1, new TGeoPoint((float)p2.getLatitude(),(float)p2.getLongitude()));
	}
	
	public static float getDistanceBetween(Location p1, Location p2)
	{
		return TGeoPoint.getDistanceBetween(new TGeoPoint((float)p1.getLatitude(),(float)p1.getLongitude()), new TGeoPoint((float)p2.getLatitude(),(float)p2.getLongitude()));
	}	
	
	public static float getAngleBetween(TGeoPoint p1, Location p2)
	{
		float fTangens = (float)(Math.abs(p1.getLatitude()-(float)p2.getLatitude()) / Math.abs(p1.getLongitude()-(float)p2.getLongitude()));
		return (float)Math.toDegrees(Math.atan(Math.toRadians((double)fTangens)));
	}
	
}
