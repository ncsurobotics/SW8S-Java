package org.aquapackrobotics.sw8s.vision;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.vision.Path;

import nu.pattern.OpenCV;

public class BaseMatrix {
    static {
        OpenCV.loadLocally();
    }

    @Test
    public void markPath() {
        try {
            Mat img = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/1.png");
            Path path = new Path();
            path.processFrame(img, "drawn.png");
            // String filename = getClass().getResource("images/1.png").getFile();
            // Mat img = Imgcodecs.imread(getClass().getResource("images/1.png").getFile());
            // Path path = new Path();
            // path.relativePositions(img);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }
}
