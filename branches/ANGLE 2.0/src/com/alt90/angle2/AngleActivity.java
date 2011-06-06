package com.alt90.angle2;

import java.io.IOException;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

//UBW Public field for speed. Beware of undefined behavior

/**
 * Use this activity to utilize Angle engine
 * @author Ivan Pajuelo
 *
 */
public class AngleActivity extends Activity
{
	private static final int sMaxFingers = 10; //Max number of multitouch points
	public static AngleActivity uInstance = null; //UBW selft instance
	public static boolean[] iKeys = new boolean[KeyEvent.MAX_KEYCODE]; //UBW State of keys
	public static Pointer[] iPointer = new Pointer[sMaxFingers]; //UBW Pointers array for multitouch
	public static Fling[] iFling = new Fling[sMaxFingers]; //UBW Flings array for multitouch
	protected GLSurfaceView lGLSurfaceView;

	public class Pointer
	{
		public AngleVector fPosition_uu = new AngleVector(); //UBW Position of the pointer in User Units
		public boolean isDown; //UBW Finger is pushing the screen
		public boolean newData; //Data of the pointer is changed (clear this after check it)

	}

	public class Fling
	{
		private long lBegin; //time where fling began
		private AngleVector lOrigin_uu = new AngleVector(); //point where fling began 
		public AngleVector fDelta_uu = new AngleVector(); //UBW Vector of fling movement
		public float fTime; //UBW Duration of fling movement
		public boolean newData; //Data of the fling is changed (clear this after check it)

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		for (int p = 0; p < sMaxFingers; p++)
		{
			iPointer[p] = new Pointer();
			iFling[p] = new Fling();
		}
		uInstance = this;
		lGLSurfaceView = new GLSurfaceView(this);
		lGLSurfaceView
				.setRenderer(new AngleRenderer(getResources().getDimension(R.dimen.Width), getResources().getDimension(R.dimen.Height)));
		setContentView(lGLSurfaceView);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		lGLSurfaceView.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		lGLSurfaceView.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		iKeys[keyCode] = true;
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		iKeys[keyCode] = false;
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		for (int p = 0; p < event.getPointerCount(); p++)
		{
			int pid = event.getPointerId(p);
			iPointer[pid].fPosition_uu.fX = event.getX(pid);
			iPointer[pid].fPosition_uu.fY = event.getY(pid);
			iPointer[pid].fPosition_uu.set(AngleRenderer.coordsScreenToUser(iPointer[pid].fPosition_uu));
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					iPointer[pid].isDown = true;
					iPointer[pid].newData = true;
					if (iFling[pid].lBegin==0)
					{
						iFling[pid].lBegin=android.os.SystemClock.uptimeMillis();
						iFling[pid].lOrigin_uu.set(iPointer[pid].fPosition_uu);
					}
					break;
				case MotionEvent.ACTION_UP:
					iPointer[pid].isDown = false;
					iPointer[pid].newData = true;
					if (iFling[pid].lBegin!=0)
					{
						iFling[pid].fTime=(android.os.SystemClock.uptimeMillis()-iFling[pid].lBegin)/1000.f;
						iFling[pid].lBegin=0;
						iFling[pid].fDelta_uu.set(iPointer[pid].fPosition_uu);
						iFling[pid].fDelta_uu.sub(iFling[pid].lOrigin_uu);
						iFling[pid].newData = true;
					}
					break;
			}
		}
		// --- Prevent flooding ---
		try
		{
			Thread.sleep(16);
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		// ------------------------
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event)
	{
		return super.onTrackballEvent(event);
	}
}