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
            File outDir = new File("tests/bin/success/");
            outDir.mkdirs();
            File outDir1 = new File("tests/bin/failure/");
            outDir1.mkdirs();
            File dir = new File(System.getProperty("user.dir") + "/resources/bin_images/");
            int successes = 0;
            for (File f : dir.listFiles()) {
                String out = "tests/bin/";
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Bin test = new Bin();
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
    public void markBinLarge() {
        try {
            File outDir = new File("tests/bin_large/success/");
            outDir.mkdirs();
            File outDir1 = new File("tests/bin_large/failure/");
            outDir1.mkdirs();
            File dir = new File(System.getProperty("user.dir") + "/resources/bin_images/");
            int successes = 0;
            for (File f : dir.listFiles()) {
                String out = "tests/bin_large/";
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Bin test = new Bin(true);
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
                Bin test = new Bin();
                test.detectYoloV5(img);
                assertFalse(test.detected());
            }
        } catch (Exception e) {
            Assert.fail(e.getStackTrace().toString());
        }
    }
}
