package com.aarone.kirkifier.editor;

import android.content.Context;

import com.aarone.kirkifier.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.List;

public class BasicKirkifier implements FrameEditor{

    Mat roi;
    Mat kirkImage;
    Mat kirkMask;

    public BasicKirkifier(Context context) {
        try {
            //Load the kirk image and convert its colour to RGBA
            Mat temp = Utils.loadResource(context,R.raw.kirk, Highgui.CV_LOAD_IMAGE_UNCHANGED);
            kirkImage = new Mat();
            Imgproc.cvtColor(temp, kirkImage, Imgproc.COLOR_BGRA2RGBA);
            temp.release();

            kirkMask = Utils.loadResource(context,  R.raw.kirkmask, Highgui.CV_LOAD_IMAGE_UNCHANGED);
        } catch (IOException e) {
        }
    }



    @Override
    public void setUp(int width, int height) {
        roi = new Mat();
    }

    @Override
    public Mat drawOnFaces(Mat frame, List<Rect> faces) {

        for(Rect face: faces){
            roi = frame.submat(
                    new Rect(face.x, face.y,
                            face.width, face.height)
            );

            overlayImage(roi, kirkImage, kirkMask);
        }

        return frame;
    }

    public Mat overlayImage(Mat background, Mat foreground, Mat mask)//, Point location)
    {
        Mat resizedMask = new Mat();
        Imgproc.resize(mask, resizedMask, background.size());

        Mat source = new Mat();
        Imgproc.resize(foreground, source, background.size());

        source.copyTo(background, resizedMask);
        source.release();
        resizedMask.release();
        return background;
    }

    @Override
    public void cleanUp() {
        roi.release();
    }
}
