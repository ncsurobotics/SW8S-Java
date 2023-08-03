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
import org.aquapackrobotics.sw8s.vision.Path;

@State(Scope.Benchmark)
public class PathBench {
    static {
        CV.open();
    }

    private static Mat img = Imgcodecs.imread(System.getProperty("user.dir") + "/resources/path_images/1.jpeg");

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void _50(Blackhole bh) {
        try {
            Path path = new Path();
            bh.consume(path.relativePosition(img));
        } catch (Exception e) {
        }
    }
}
