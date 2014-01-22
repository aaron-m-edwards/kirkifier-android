package com.aarone.kirkifier.detector;

import java.util.List;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import android.app.Activity;
import android.content.Context;

public interface Detector {
	
	public void readCascadeXml(Context parent, int xmlID);
	
	public void initialize(int width, int height, float size);
	
	public List<Rect> detect(Mat frame);

	public void cleanUp();

}
