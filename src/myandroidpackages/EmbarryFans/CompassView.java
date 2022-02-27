package myandroidpackages.EmbarryFans;

import library.TPointFloat;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

public class CompassView extends View 
{
	private static final String TAG = "CompassView";
	
	// dimensions
	private float fPositionHeight; // height of the text area for the current position
	private float fCompassRectHeight; // height of the rect area holding the compass
	private float fCompassRadius; // height of the compass circle
	private float fDistanceHeight; // height of the text area for the current distance
	private float fWidth;
	private float fFanRadius;
	private float fHomeRadius;	
	
	// rects
	private Rect rPosition;
	private Rect rDistance;
	private Rect rHomeRect;
	private RectF rect;
	private Rect rHand;
	
	// paints
	private Paint pBackground;
	private Paint pCompassRect;
	private Paint pCompassCircle;
	private Paint pPathFont;
	private Paint pFont;
	private Paint pCompassFont;
	private Paint pFanCircle;
	private Paint pHomeCircle;
	private Paint pRadarHand;
	private Paint pCompassHand;
	
	// paths
	private Path pCircle;
	private Path pFan;
	private Path pHome;
	
	// points
	private TPointFloat pointCircleOrigin;
	private TPointFloat pointHome;
	private TPointFloat pointHand;
	
	// text
	private String sPosition = "";
	private String sDistance = "";
	
	// others
	private float azimut = 0;
	
	public CompassView(Context context)
	{
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		pBackground = new Paint();
		pBackground.setColor(getResources().getColor(R.color.black));

		pCompassRect = new Paint();
		pCompassRect.setColor(getResources().getColor(R.color.black));

		pCompassCircle = new Paint();
		pCompassCircle.setColor(getResources().getColor(R.color.white));
		pCompassCircle.setAntiAlias(true);
		pCompassCircle.setStyle(Style.STROKE);
		pCircle = new Path();

		pPathFont = new Paint();
		pPathFont.setColor(getResources().getColor(R.color.white));
		pPathFont.setAntiAlias(true);
		pPathFont.setStyle(Style.FILL);
		pPathFont.setAlpha(75);

		pFont = new Paint();
		pFont.setColor(getResources().getColor(R.color.white));
		pFont.setAntiAlias(true);
		pFont.setStyle(Style.FILL);
		
		pCompassFont = new Paint();
		pCompassFont.setColor(getResources().getColor(R.color.green));
		pCompassFont.setAntiAlias(true);
		pCompassFont.setStyle(Style.FILL);		

		pFanCircle = new Paint();
		pFanCircle.setColor(getResources().getColor(R.color.green));
		pFanCircle.setAntiAlias(true);
		pFanCircle.setStyle(Style.FILL);
		pFan = new Path();
		
		pHomeCircle = new Paint();
		pHomeCircle.setColor(getResources().getColor(R.color.orange));
		pHomeCircle.setAntiAlias(true);
		pHomeCircle.setStyle(Style.FILL);
		pHome = new Path();
		
		pRadarHand = new Paint();
		pRadarHand.setColor(getResources().getColor(R.color.white));
		pRadarHand.setAntiAlias(true);
		pRadarHand.setStrokeWidth(5);
		
		pCompassHand = new Paint();
		pCompassHand.setColor(getResources().getColor(R.color.green));
		pCompassHand.setAntiAlias(true);
		pCompassHand.setStrokeWidth(5);
	}
	
	@Override	
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		Log.d(TAG,"onSizeChanged()");
		// The sizes are known to the system on first call of onSizeChanged, so don't set it before
		fPositionHeight = h/6f;
		fCompassRectHeight = 4 * (h/6f) ;
		fCompassRadius = (fCompassRectHeight * 0.75f) / 2;
		fDistanceHeight = h/6f;
		fWidth = w;
		fFanRadius = fCompassRadius * 0.1f;
		fHomeRadius = fCompassRadius * 0.1f;
		
		rPosition = new Rect(0,0,w,(int)fPositionHeight);
		rDistance = new Rect(0,(int)(fPositionHeight+fDistanceHeight),w,h);
		
		// Adjust dynamic values
		pointCircleOrigin = new TPointFloat(getWidth()/2f,getHeight()/2f);
		rect = new RectF(0,fPositionHeight,fWidth,getHeight()-fDistanceHeight);
		pCircle.addCircle(getWidth()/2,getHeight()/2,fCompassRadius,Direction.CW);
		pPathFont.setTextSize(fCompassRadius * 0.25f);
		pFont.setTextSize(fPositionHeight * 0.25f);	
		pCompassFont.setTextSize(fPositionHeight * 0.25f);
		pFan.addCircle(pointCircleOrigin.x,pointCircleOrigin.y,fFanRadius,Direction.CW);				
		pointHome = new TPointFloat(getWidth()/2f+fCompassRadius,getHeight()/2f);
		pointHand = new TPointFloat(getWidth()/2f+fCompassRadius*0.9f,getHeight()/2f);
		pHome.addCircle(pointHome.x,pointHome.y,fHomeRadius,Direction.CW);
		rHomeRect = pointHome.getRect(fHomeRadius);
		rHand = pointCircleOrigin.getRect(pointHand);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		// Paint background
		canvas.drawRect(0,0,getWidth(),getHeight(),pBackground);
		
		// Draw compass rect
		canvas.drawRoundRect(rect,10f,10f,pCompassRect);		
       
		// Draw compass
		canvas.drawPath(pCircle,pCompassCircle);
		
		// draw text on path
		canvas.drawTextOnPath("Embarry Manor is within reach. Keep on approaching!",pCircle,0,40,pPathFont);
		
		// Draw hair cross
		canvas.drawLine(getWidth()/2f,fPositionHeight,getWidth()/2f,fPositionHeight+fCompassRectHeight,pCompassCircle);
		canvas.drawLine(0,fPositionHeight+fCompassRectHeight/2f,getWidth(),fPositionHeight+fCompassRectHeight/2f,pCompassCircle);
		
		// Draw text (position + distance
		canvas.drawText(sPosition,10,fPositionHeight-(fPositionHeight-pFont.getTextSize())/2f,pFont);
		canvas.drawText(sDistance,10,getHeight()-(fDistanceHeight-pFont.getTextSize())/2f,pFont);
		
		// Draw Fan and Embarry Manor
		//canvas.drawPath(pFan,pFanCircle);
		//canvas.drawPath(pHome,pHomeCircle);
		
		// draw radar hand
		//canvas.drawLine(pointCircleOrigin.x,pointCircleOrigin.y,pointHand.x,pointHand.y,pRadarHand);
    
		// rotate compass due to sensor orientation data
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        
        canvas.rotate(-azimut*360/(2*3.14159f),cx,cy);
        
        Log.d(TAG,String.valueOf(azimut));
        
        // draw North label
        canvas.drawLine(pointCircleOrigin.x,pointCircleOrigin.y-fCompassRadius*0.9f,pointCircleOrigin.x,pointHand.y+fCompassRadius*0.9f,pCompassHand);
        canvas.drawLine(pointCircleOrigin.x,pointCircleOrigin.y-fCompassRadius*0.9f,pointCircleOrigin.x-10,pointCircleOrigin.y-fCompassRadius*0.9f+10,pCompassHand);
        canvas.drawLine(pointCircleOrigin.x,pointCircleOrigin.y-fCompassRadius*0.9f,pointCircleOrigin.x+10,pointCircleOrigin.y-fCompassRadius*0.9f+10,pCompassHand);
        canvas.drawText("N",pointCircleOrigin.x,pointCircleOrigin.y-fCompassRadius,pCompassFont);
	}
	
	// Updates position text
	public void drawPosition(String str)
	{
		sPosition = str;
		this.invalidate(rPosition); // refresh dirty rect rPosition
	}
	
	// update distance text
	public void drawDistance(String str)
	{
		sDistance = str;
		this.invalidate(rDistance); // refresh dirty rect rPosition
	}
	
	// update home point
	//public void drawHomePoint(float radius, PointFloat newPoint, Boolean blink)
	public void drawHomePoint(int angle, Boolean blink)
	{
		// TEST
		float radius = fHomeRadius;
		//
		
		// erase old one
		pHomeCircle.setAlpha(0);
		this.invalidate(rHomeRect);		
		
		if (blink)
		{
			pHomeCircle.setAlpha(255);			

			pointHome.x = pointCircleOrigin.x + (float)Math.cos(Math.toRadians((double)angle)) * fCompassRadius;
			pointHome.y = pointCircleOrigin.y - (float)Math.sin(Math.toRadians((double)angle)) * fCompassRadius;

			//Log.d(TAG,"pointHome.x: " + pointHome.x + "; pointHome.y: " + pointHome.y);
			// draw new one
			pHome = new Path();
			pHome.addCircle(pointHome.x,pointHome.y,fHomeRadius,Direction.CW);
			fHomeRadius = radius;
			Rect dirtyRect = pointHome.getRect(radius);
			this.invalidate(dirtyRect);
			
			// save current postion 
			rHomeRect = dirtyRect;
		}
	}	
	
	public void drawRadarHand(int angle)
	{
		// erase old one
		pRadarHand.setAlpha(0);
		this.invalidate(rHand);
		
		pRadarHand.setAlpha(255);			

		Log.d(TAG,"angle: " + angle + "; pointCircleOrigin.x: " + pointCircleOrigin.x + "; pointCircleOrigin.y: " + pointCircleOrigin.x + "; pointHome.x: " + pointHome.x + "; pointHome.y: " + pointHome.y);
		
		pointHand.x = pointCircleOrigin.x + (float)Math.cos(Math.toRadians((double)angle)) * fCompassRadius * 0.9f;
		pointHand.y = pointCircleOrigin.y - (float)Math.sin(Math.toRadians((double)angle)) * fCompassRadius * 0.9f;

		//Log.d(TAG,"pointHome.x: " + pointHome.x + "; pointHome.y: " + pointHome.y);
		// draw new one
		Rect rDirty = pointCircleOrigin.getRect(pointHand);
		this.invalidate(rDirty);
		
		// save old rect
		rHand = rDirty;
	}
	
	public void rotateCompass(float azimut)
	{
		if (azimut != 0.0) 
		{
			this.azimut = azimut;
			this.invalidate();			
		}		
	}
}

