package org.aquapackrobotics.sw8s.vision;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.aquapackrobotics.sw8s.CV;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Octagon_Test {
    static {
        CV.open();
    }

    @Test
    public void markOctagon() {
        File outDir = new File("tests/octagon/success");
        outDir.mkdirs();
        File outDir1 = new File("tests/octagon/failure");
        outDir1.mkdirs();
        File dir = new File(System.getProperty("user.dir") + "/resources/octagon_images/");
        int successes = 0;
        int i = 0;
        for (File f : dir.listFiles()) {
            try {
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Octagon octagon = new Octagon();
                octagon.processFrame(img, outDir.toString() + "/" + String.valueOf(++i));
                octagon.relativePosition(img);
                ++successes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // assertTrue((double) successes / dir.listFiles().length > 0.9);
    }

    @Test
    public void noFalsePositives() {
        try {
            File dir = new File(System.getProperty("user.dir") + "/resources/blank_images/");
            for (File f : dir.listFiles()) {
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Octagon test = new Octagon();
                test.relativePosition(img);
                Assert.fail("Found octagon");
            }
        } catch (Exception e) {
        }
    }
}
