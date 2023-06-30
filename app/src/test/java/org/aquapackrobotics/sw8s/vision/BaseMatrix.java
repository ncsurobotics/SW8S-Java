package org.aquapackrobotics.sw8s.vision;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import org.aquapackrobotics.sw8s.CV;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.vision.Path;

import nu.pattern.OpenCV;

public class BaseMatrix {
    static {
        CV.open();
    }

    @Test
    public void markPath() {
        try {
            Mat img = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");
            Path path = new Path();
            path.processFrame(img, "drawn.jpeg");
            path.relativePosition(img);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void markShrunkPath() {
        try {
            Mat img = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");
            Path path = new Path(0.25);
            path.processFrame(img, "drawn_shrunk.jpeg");
            path.relativePosition(img);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void matchResult() {
        try {
            Mat img1 = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");
            Mat img2 = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");
            VisualObject original = new Path().relativePosition(img1);
            VisualObject shrunk = new Path(0.25).relativePosition(img2);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }
}
