package org.aquapackrobotics.sw8s.vision;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.aquapackrobotics.sw8s.CV;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Bin_Test {
    static {
        CV.open();
    }

    @Test
    public void markBin() {
        try {
            File outDir = new File("tests/bin/");
            outDir.mkdirs();
            File dir = new File(System.getProperty("user.dir") + "/resources/bin_images/");
            for (File f : dir.listFiles()) {
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Bin test = new Bin();
                Imgcodecs.imwrite("tests/bin/" + f.getName(), test.detectYoloV5(img));
                assertTrue(test.detected());
            }
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void markBinLarge() {
        try {
            File outDir = new File("tests/bin_large/");
            outDir.mkdirs();
            File dir = new File(System.getProperty("user.dir") + "/resources/bin_images/");
            for (File f : dir.listFiles()) {
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Bin test = new Bin(true);
                Imgcodecs.imwrite("tests/bin_large/" + f.getName(), test.detectYoloV5(img));
                assertTrue(test.detected());
            }
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
                Bin test = new Bin();
                test.detectYoloV5(img);
                assertFalse(test.detected());
            }
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }
}
