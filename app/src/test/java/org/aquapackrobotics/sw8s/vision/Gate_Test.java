package org.aquapackrobotics.sw8s.vision;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.aquapackrobotics.sw8s.CV;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Gate_Test {
    static {
        CV.open();
    }

    @Test
    public void markGate() {
        try {
            File outDir = new File("tests/gate/");
            outDir.mkdirs();
            File dir = new File(System.getProperty("user.dir") + "/resources/gate_images/");
            for (File f : dir.listFiles()) {
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Gate test = new Gate();
                Imgcodecs.imwrite("tests/gate/" + f.getName(), test.detectYoloV5(img));
                assertTrue(test.detected());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getStackTrace().toString());
        }
    }

    @Test
    public void markGateLarge() {
        try {
            File outDir = new File("tests/gate_large/");
            outDir.mkdirs();
            File dir = new File(System.getProperty("user.dir") + "/resources/gate_images/");
            for (File f : dir.listFiles()) {
                Mat img = Imgcodecs
                        .imread(f.getPath());
                Gate test = new Gate(true);
                Imgcodecs.imwrite("tests/gate_large/" + f.getName(), test.detectYoloV5(img));
                assertTrue(test.detected());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                Gate test = new Gate();
                test.detectYoloV5(img);
                assertFalse(test.detected());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getStackTrace().toString());
        }
    }
}
