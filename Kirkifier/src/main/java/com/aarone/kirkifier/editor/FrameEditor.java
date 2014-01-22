package com.aarone.kirkifier.editor;

import java.util.List;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public interface FrameEditor {

	void setUp(int width, int height);

	Mat drawOnFaces(Mat frame, List<Rect> faces);

	void cleanUp();

}
