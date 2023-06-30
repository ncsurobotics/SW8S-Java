package org.aquapackrobotics.sw8s.vision;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import org.aquapackrobotics.sw8s.CV;
import org.aquapackrobotics.sw8s.vision.Buoy;

@State(Scope.Benchmark)
public class BuoyBench {
    static {
        CV.open();
    }

    private static Mat img = Imgcodecs.imread(System.getProperty("user.dir") + "/resources/path_images/1.jpg");

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void nodetect_320(Blackhole bh) {
        try {
            Buoy buoy = new Buoy();
            bh.consume(buoy.detectYoloV5(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void nodetect_640(Blackhole bh) {
        try {
            Buoy buoy = new Buoy(true);
            bh.consume(buoy.detectYoloV5(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
