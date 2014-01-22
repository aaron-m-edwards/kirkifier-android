package com.aarone.kirkifier.editor;

import java.util.List;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class BoxFrameEditor implements FrameEditor {

	private static final Scalar FACE_BOX_COLOUR = new Scalar(0, 255, 0, 255);

	@Override
	public void setUp(int width, int height){}
	
	@Override
	public Mat drawOnFaces(Mat frame, List<Rect> faces){
		
		for(Rect face: faces){
			Core.rectangle(frame, face.tl(), face.br(), FACE_BOX_COLOUR, 3);
		}
		
		return frame;
	}
	
	@Override
	public void cleanUp(){}

}
