package com.aarone.kirkifier.detector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import com.aarone.kirkifier.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

public class CascadeDetector implements Detector{
	
	private static final String TAG = "Kirkifier::CascadeDetector";
	private CascadeClassifier cascadeClassifier;
	private int absoluteSize;
	private Mat frame;

	@Override
	public void readCascadeXml(Context parent, int xmlID) {
		InputStream is = parent.getResources().openRawResource(xmlID);
        File cascadeDir = parent.getDir("cascade", Context.MODE_PRIVATE);
        File mCascadeFile = new File(cascadeDir, "cascade.xml");
        try{
	        FileOutputStream os = new FileOutputStream(mCascadeFile);
	
	        byte[] buffer = new byte[4096];
	        int bytesRead;
	        while ((bytesRead = is.read(buffer)) != -1) {
	            os.write(buffer, 0, bytesRead);
	        }
	        is.close();
	        os.close();

	        cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
	        if (cascadeClassifier.empty()) {
	            Log.e(TAG, "Failed to load cascade classifier");
	            cascadeClassifier = null;
	        } else
	            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
        } catch( Exception e){
        	Log.e(TAG, "Failed to load cascade classifier", e);
        }

        cascadeDir.delete();
	}
	
	@Override
	public void initialize(int width, int height, float size) {
		absoluteSize = Math.round(height * size);
	}
	
	@Override
	public List<Rect> detect(Mat frame) {
		MatOfRect faces = new MatOfRect();
		
		cascadeClassifier.detectMultiScale(frame, faces, 1.1, 2, 2,
    			new Size(absoluteSize, absoluteSize), new Size());
		
		List<Rect> faceList = faces.toList();
		
		faces.release();
		
		return faceList;
	}

	@Override
	public void cleanUp() {}

}
