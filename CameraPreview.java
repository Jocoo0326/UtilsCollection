package com.jocoo.demoapp;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getCanonicalName();
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mCameraId = 0;

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void onStart() {
        safeCameraOpen(mCameraId);
    }

    private void safeCameraOpen(int id) {

        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
        } catch (Exception e) {
            Log.e(TAG, "failed to open Camera");
            e.printStackTrace();
        }
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void onStop() {
        stopPreviewAndFreeCamera();
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            setCameraDisplayOrientation(mCameraId, mCamera);
            mCamera.setPreviewDisplay(mHolder);
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
            System.out.println(sizeList);
            System.out.println(holder.getSurfaceFrame());
            Rect matchedSize = findBestMatchedSize(
                    holder.getSurfaceFrame().width(),
                    holder.getSurfaceFrame().height(),
                    sizeList);
            System.out.println(matchedSize);
            if (matchedSize != null) {
                parameters.setPreviewSize(matchedSize.width(), matchedSize.height());
            } else {
                parameters.setPreviewSize(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
            }
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private Rect findBestMatchedSize(int width, int height, List<Camera.Size> sizeList) {
        long i = 0, j;
        Rect rect = new Rect();
        for (Camera.Size size : sizeList) {
            int w = size.height;
            int h = size.width;
            if (w > width || h > height) {
                continue;
            }
            if ((j = w * h) > i) {
                i = j;
                rect.right = h;
                rect.bottom = w;
            }
        }

        if (i == 0) {
            rect.right = height;
            rect.bottom = width;
        }
        return rect;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
