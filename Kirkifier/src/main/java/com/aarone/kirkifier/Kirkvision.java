package com.aarone.kirkifier;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import com.aarone.kirkifier.detector.CascadeDetector;
import com.aarone.kirkifier.detector.Detector;
import com.aarone.kirkifier.editor.AdvancedKirkifier;
import com.aarone.kirkifier.editor.BasicKirkifier;
import com.aarone.kirkifier.editor.BoxFrameEditor;
import com.aarone.kirkifier.editor.FrameEditor;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class Kirkvision extends Activity implements CvCameraViewListener2 {
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    private static final String TAG = "Kirkifier::Kirkvision";

    private CameraBridgeViewBase openCvCameraView;
    private Detector faceDetector = new CascadeDetector();
    private FrameEditor faceFrameEditor;

    private int cameraId;
    private Mat gray;
    private Mat rgbA;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    faceDetector.readCascadeXml(this.mAppContext, R.raw.haarcascade_frontalface_alt2);
                    faceFrameEditor = new AdvancedKirkifier(this.mAppContext);
                    openCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.kirkvision, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_settings:
                return true;
            case R.id.action_toggleCamera:
                cameraId = (cameraId + 1) % Camera.getNumberOfCameras();
                onCameraViewStopped();
                openCvCameraView.disableView();
                openCvCameraView.setCameraIndex(cameraId);
                openCvCameraView.enableView();

                Toast.makeText(this,
                        String.format("Switching to camera %s of %s", (cameraId+1), Camera.getNumberOfCameras()),
                        Toast.LENGTH_LONG).show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_kirkvision);
        openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fullscreen_content);
        openCvCameraView.setCameraIndex(0);
        openCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (openCvCameraView != null)
            openCvCameraView.disableView();
    }



    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        openCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        faceDetector.initialize(width, height, 0.15F);
        faceFrameEditor.setUp(width, height);

        gray = new Mat();
        rgbA = new Mat();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        gray = inputFrame.gray();
        rgbA = inputFrame.rgba();

        List<Rect> faces = faceDetector.detect(gray);
        return faceFrameEditor.drawOnFaces(rgbA, faces);
    }

    @Override
    public void onCameraViewStopped() {
        faceFrameEditor.cleanUp();
        faceDetector.cleanUp();
        gray.release();
        rgbA.release();
    }


}
