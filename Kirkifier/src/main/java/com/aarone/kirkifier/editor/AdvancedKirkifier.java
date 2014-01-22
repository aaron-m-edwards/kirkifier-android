package com.aarone.kirkifier.editor;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.aarone.kirkifier.R;
import com.aarone.kirkifier.detector.CascadeDetector;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

import java.security.spec.ECField;
import java.util.List;

public class AdvancedKirkifier extends BasicKirkifier {

    private float topPad = 0f;
    private float bottomPad = 0f;
    private float leftPad;
    private float rightPad;

    public AdvancedKirkifier(Context context) {
        super(context);
        CascadeDetector cascadeDetector = new CascadeDetector();
        cascadeDetector.readCascadeXml(context, R.raw.haarcascade_frontalface_alt2);
        calibrate(cascadeDetector);
    }

    private void calibrate(CascadeDetector cascadeDetector) {
        cascadeDetector.initialize(kirkImage.width(), kirkImage.height(), 0.25F);
        List<Rect> faces = cascadeDetector.detect(kirkImage);
        cascadeDetector.cleanUp();

        if(faces.size() > 0){
            Rect face = faces.get(0);
            topPad = face.y / (float)face.height;
            leftPad = face.x / (float)face.width;
            bottomPad = ((kirkImage.rows() - (face.y + face.height)) / (float)face.height) / 2F;
            rightPad = (kirkImage.cols() - (face.x + face.width)) / (float)face.width;

        }
    }

    @Override
    public Mat drawOnFaces(Mat frame, List<Rect> faces) {

        for(Rect face: faces){
            try{
                int left =Math.max(0,face.x - (int)(face.width * leftPad));
                int top = Math.max(0,face.y - (int)(face.height * topPad));
                int width = Math.min(face.width + (int)(face.width * leftPad) + (int)(face.width * rightPad), frame.cols() - left);
                int height = Math.min(face.height + (int)(face.height * topPad) + (int)(face.height * bottomPad), frame.rows()-top);


            roi = frame.submat(
                    new Rect(
                            left,
                            top,
                            width,
                            height)
            );

            overlayImage(roi, kirkImage, kirkMask);
            }catch (Exception e){
                Log.e("AdvancedKirkifier", "error", e);
            }
        }

        return frame;
    }
}
