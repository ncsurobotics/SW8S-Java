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
import org.aquapackrobotics.sw8s.vision.PathYUV;

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
    public void markYUVPath() {
        try {
            Mat img = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");
            PathYUV path = new PathYUV();
            path.processFrame(img, "drawn_YUV");
            path.relativePosition(img);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void markShrunkYUVPath() {
        try {
            Mat img = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");
            PathYUV path = new PathYUV(0.25);
            path.processFrame(img, "drawn_shrunk_YUV");
            path.relativePosition(img);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void markDecimatedYUVPath() {
        try {
            Mat img = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");
            PathYUV path = new PathYUV(0.10);
            path.processFrame(img, "drawn_decimated_YUV");
            path.relativePosition(img);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void markGate() {
        try {
            for (int i = 1; i < 7; i++) {
                Mat img = Imgcodecs
                        .imread(System.getProperty("user.dir") + "/resources/gate_images/" + String.valueOf(i)
                                + ".jpeg");
                PathYUV path = new PathYUV(new IntPair(0, 125),
                        new IntPair(125, 255), 0,
                        70,
                        0.25,
                        new IntPair(4, 32));
                File dir = new File("gate/");
                dir.mkdirs();
                path.processFrame(img, "gate/" + String.valueOf(i));
                // path.relativePosition(img);
            }
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void markGate_BW() {
        try {
            for (int i = 1; i < 7; i++) {
                Mat img = Imgcodecs
                        .imread(System.getProperty("user.dir") + "/resources/gate_images/" + String.valueOf(i)
                                + ".jpeg");
                Path path = new Path(0,
                        200, 0,
                        Integer.MAX_VALUE,
                        0.5);
                File dir = new File("gate_BW/");
                dir.mkdirs();
                path.processFrame(img, "gate_BW/" + String.valueOf(i) + ".jpeg");
                // path.relativePosition(img);
            }
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void markGate_OnlyY() {
        try {
            for (int i = 1; i < 7; i++) {
                Mat img = Imgcodecs
                        .imread(System.getProperty("user.dir") + "/resources/gate_images/" + String.valueOf(i)
                                + ".jpeg");
                PathY path = new PathY();
                File dir = new File("gateY/");
                dir.mkdirs();
                path.processFrame(img, "gateY/" + String.valueOf(i));
                // path.relativePosition(img);
            }
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
