package com.bluedoorway.sma.igotiteventcapture.view;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;

	public CameraPreview(Context context, Camera camera)
	{
		super(context);
		this.mCamera = camera;
		this.mSurfaceHolder = this.getHolder();
		this.mSurfaceHolder.addCallback(this);
	}

	public CameraPreview(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public CameraPreview(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public CameraPreview(Context context)
	{
		super(context);
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder)
	{
		try
		{
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
		}
		catch (IOException e)
		{
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder)
	{
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height)
	{
		try
		{
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
		}
		catch (Exception e)
		{
		}
	}
	
	
}