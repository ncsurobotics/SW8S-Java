package org.aquapackrobotics.sw8s.vision;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.aquapackrobotics.sw8s.CV;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Buoy_Test {
    static {
        CV.open();
    }

    @Test
    public void markBuoy() {
        try {
            File outDir = new File("tests/buoy/success");
            outDir.mkdirs();
            File outDir1 = new File("tests/buoy/failure");
            outDir1.mkdirs();
            File dir = new File(System.getProperty("user.dir") + "/resources/buoy_images/");
            int successes = 0;
            for (File f : dir.listFiles()) {
                String out = "tests/buoy/";
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Buoy test = new Buoy();
                if (test.detected()) {
                    out += "success/";
                    successes++;
                } else {
                    out += "failure/";
                }
                Imgcodecs.imwrite(out + f.getName(), test.detectYoloV5(img));
            }
            // assertTrue((double) successes / dir.listFiles().length > 0.9);
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void markBuoyLarge() {
        try {
            File outDir = new File("tests/buoy_large/success");
            outDir.mkdirs();
            File outDir1 = new File("tests/buoy_large/failure");
            outDir1.mkdirs();
            File dir = new File(System.getProperty("user.dir") + "/resources/buoy_images/");
            int successes = 0;
            for (File f : dir.listFiles()) {
                String out = "tests/buoy_large/";
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Buoy test = new Buoy(true);
                if (test.detected()) {
                    out += "success/";
                    successes++;
                } else {
                    out += "failure/";
                }
                Imgcodecs.imwrite(out + f.getName(), test.detectYoloV5(img));
            }
            // assertTrue((double) successes / dir.listFiles().length > 0.9);
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
                Buoy test = new Buoy();
                test.detectYoloV5(img);
                assertFalse(test.detected());
            }
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }
}
