package org.aquapackrobotics.sw8s.vision;

import java.io.File;

import org.aquapackrobotics.sw8s.CV;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

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
            new File("tests").mkdirs();
            path.processFrame(img, "tests/drawn.jpeg");
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
            new File("tests").mkdirs();
            path.processFrame(img, "tests/drawn_shrunk.jpeg");
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
            new File("tests").mkdirs();
            path.processFrame(img, "tests/drawn_YUV");
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
            new File("tests").mkdirs();
            path.processFrame(img, "tests/drawn_shrunk_YUV");
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
            new File("tests").mkdirs();
            path.processFrame(img, "tests/drawn_decimated_YUV");
            path.relativePosition(img);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void noFalsePositives() {
        try {
            File dir = new File(System.getProperty("user.dir") + "/resources/blank_images/");
            for (File f : dir.listFiles()) {
                Mat img = Imgcodecs
                        .imread(f.getPath());
                PathYUV test = new PathYUV();
                test.relativePosition(img);
                Assert.fail("Found path");
            }
        } catch (Exception e) {
        }
    }

    @Test
    public void matchResult() {
        try {
            Mat img1 = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");
            Mat img2 = Imgcodecs
                    .imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");
            new Path().relativePosition(img1);
            new Path(0.25).relativePosition(img2);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }
}
