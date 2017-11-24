package com.example.mshd.argame;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

//카메라 미리보기 화면
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    Context context;
    Camera mCamera;
    SurfaceHolder holder;



    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

        //카메라에서 지원되는 PrieViewSize 확인
//        Camera.Parameters params = mCamera.getParameters();
//        List<Camera.Size> curSize = params.getSupportedPreviewSizes();
//        for(Camera.Size s : curSize ) {
//            //s.width, s.height
//        }
//        params.setPreviewSize(width, height);
//        mCamera.setParameters(params);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
