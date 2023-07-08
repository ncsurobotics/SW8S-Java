package org.aquapackrobotics.sw8s.vision;

import org.aquapackrobotics.sw8s.vision.IntPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Code for "PathYUV" task
 * 
 * Usage:
 * private PathYUV path_process = new PathYUV(); // initiate
 * private ImagePrep path_prep = path_process;
 * path_prep.setFrame(frame); // set input for preprocess
 * path_prep.sliceSize(25, 25); // preprocess (prepare for kmeans)
 * path_prep.localKmeans(2,4); // preprocess (compute kmeans)
 * Mat local_kmeans = path_prep.resultImg; // preprocess output
 * 
 * path_process.iteratePathBinaryPCA(local_kmeans); // no image output
 * // or
 * Mat pca_draw = path_process.iteratePathBinaryPCAAndDraw(local_kmeans); //
 * draw image with drawn vectors
 * 
 * // grab the output for first path (see results and results_prop below)
 * if (path_process.result.indexOf(true) >= 0) {
 * System.out.println(Arrays.toString(path_process.results_prop.get(path_process.result.indexOf(true))));
 * }
 * 
 * numerical outputs see results_prop and results array
 * 
 * @author Xingjian Li
 *
 */

public class PathYUV extends ImagePrep {
    private final IntPair PATH_COLOR_LOW;
    private final IntPair PATH_COLOR_HIGH;
    private final int PATH_WIDTH_LOW;
    private final int PATH_WIDTH_HIGH;
    private final double[] FORWARD = { 0, -1 };

    private int path_width_idx = 0;
    private int path_length_idx = 0;
    private int path_color = 0;

    public double[] mean = new double[2]; // [x,y]
    public double[] vectors = new double[4];
    public double[] values = new double[2];

    // positive hori_offset means path is right of the center
    // positive vert_offset means path is down of the center
    // positive angle means path is sloped right(positive)
    public ArrayList<double[]> results_prop = new ArrayList<>(); // array with each element containing [color, width,
                                                                    // angle, hori_offset, vert_offset]
    public ArrayList<Boolean> result = new ArrayList<>(); // array containing boolean values where true = path, false =
                                                            // not path

    /**
     * Constructs a new PathYUV with given color and width targets
     */
    public PathYUV(IntPair color_low, IntPair color_high, int width_low, int width_high, double scale) {
        super(scale);
        PATH_COLOR_LOW = color_low;
        PATH_COLOR_HIGH = color_high;
        PATH_WIDTH_LOW = width_low;
        PATH_WIDTH_HIGH = width_high;
    }

    public PathYUV(double scale) {
        // OpenCV uses range [0, 255] for colors, so 127 is halfway on the axis
        this(new IntPair(Integer.MIN_VALUE, 127), new IntPair(127, Integer.MAX_VALUE), 30,
                400, scale);
    }

    /**
     * Constructs a new PathYUV using default parameters
     */
    public PathYUV() {
        /* color low, color high, width low, width high */
        this(0.5);
    }

    /**
     * Same as iteratePathBinaryPCA(), but also returns image with vectors drawn,
     * see drawPCA()
     * 
     * @param colored_image
     * @return images of vectors drawn
     */
    public Mat iteratePathBinaryPCAAndDraw(Mat colored_image) {
        Mat draw = colored_image.clone();
        Mat yuv_image = new Mat();
        Imgproc.cvtColor(colored_image, yuv_image, Imgproc.COLOR_BGR2YUV);
        Set<IntPair> all_colors = uniqueColor_YUV(yuv_image);
        this.results_prop.clear();
        this.result.clear();
        int i = 0;
        for (IntPair color : all_colors) {
            Mat current_bin_image = new Mat();
            /*
             * Core.inRange(yuv_image, new Scalar(Double.MIN_VALUE, color.x, color.y),
             * new Scalar(Double.MAX_VALUE, color.x, color.y),
             * current_bin_image);
             */
            Core.inRange(yuv_image, new Scalar(0, color.x, color.y),
                    new Scalar(255, color.x, color.y),
                    current_bin_image);
            MatOfPoint on_points = cvtBinaryToPoints(current_bin_image);
            List<Mat> PCA_output = binaryPCA(on_points);
            PCA_output.get(0).get(0, 0, this.mean);
            PCA_output.get(1).get(0, 0, this.vectors);
            PCA_output.get(2).get(0, 0, this.values);
            boolean is_path = pathFilter(color);
            draw = drawPCA(draw, is_path);

            double[] img_center = { colored_image.cols() / 2., colored_image.rows() / 2. };
            double[] offset = computeOffset(img_center);
            double[] properties = { color.x, this.values[this.path_width_idx], computeAngle(), offset[0],
                    offset[1] };
            this.results_prop.add(properties);
            this.result.add(is_path);
        }
        return draw;
    }

    /**
     * draw PCA vectors
     * 
     * @param input_image
     * @param is_path     different color for the vector
     * @return drawn image
     */
    public Mat drawPCA(Mat input_image, boolean is_path) {
        Mat output = input_image.clone();
        Point center = new Point(this.mean[0], this.mean[1]);
        if (is_path) {
            Imgproc.circle(output, center, 5, new Scalar(0, 255, 0));
        } else {
            Imgproc.circle(output, center, 5, new Scalar(0, 0, 255));
        }
        Point p1 = new Point(center.x + 0.02 * this.vectors[this.path_length_idx] * this.values[this.path_length_idx],
                center.y + 0.02 * this.vectors[this.path_length_idx + 1] * this.values[this.path_length_idx]);
        Point p2 = new Point(center.x + 0.02 * this.vectors[this.path_width_idx * 2] * this.values[this.path_width_idx],
                center.y + 0.02 * this.vectors[2 * this.path_width_idx + 1] * this.values[this.path_width_idx]);
        Imgproc.arrowedLine(output, center, p1, new Scalar(255, 255, 255));
        return output;
    }

    /**
     * process the path properties and filter for the correct one
     * 
     * @param color
     * @return
     */
    private boolean pathFilter(IntPair color) {
        correctDirection();
        decideLenWidth();
        return isPath(color);
    }

    /**
     * make all vectors point upwards
     */
    private void correctDirection() {
        if (this.vectors[1] > 0) {
            this.vectors[0] = -this.vectors[0];
            this.vectors[1] = -this.vectors[1];
        }
        if (this.vectors[3] > 0) {
            this.vectors[2] = -this.vectors[2];
            this.vectors[3] = -this.vectors[3];
        }
    }

    /**
     * decide which vectors is the length and which is the width
     */
    private void decideLenWidth() {
        if (this.values[1] > this.values[0]) {
            this.path_length_idx = 1;
            this.path_width_idx = 0;
        } else {
            this.path_length_idx = 0;
            this.path_width_idx = 1;
        }
    }

    /**
     * filter with some thresholds
     * 
     * @param color
     * @return
     */
    private boolean isPath(IntPair color) {
        System.out.println("");
        System.out.println(String.valueOf(color.x) + " <= " + String.valueOf(this.PATH_COLOR_HIGH.x));
        System.out.println(String.valueOf(color.y) + " <= " + String.valueOf(this.PATH_COLOR_HIGH.y));
        System.out.println(String.valueOf(color.x) + " >= " + String.valueOf(this.PATH_COLOR_LOW.x));
        System.out.println(String.valueOf(color.y) + " >= " + String.valueOf(this.PATH_COLOR_LOW.y));
        System.out.println(
                String.valueOf(this.values[this.path_width_idx]) + " <= " + String.valueOf(this.PATH_WIDTH_HIGH));
        System.out.println(
                String.valueOf(this.values[this.path_width_idx]) + " >= " + String.valueOf(this.PATH_WIDTH_LOW));
        return color.x <= this.PATH_COLOR_HIGH.x && color.y <= this.PATH_COLOR_HIGH.y &&
                color.x >= this.PATH_COLOR_LOW.x && color.y >= this.PATH_COLOR_LOW.y &&
                this.values[this.path_width_idx] <= this.PATH_WIDTH_HIGH &&
                this.values[this.path_width_idx] >= this.PATH_WIDTH_LOW;
    }

    /**
     * output results for movements
     * 
     * @return
     */
    public double computeAngle() {
        double[] path_direction = { this.vectors[this.path_length_idx], this.vectors[this.path_length_idx + 1] };
        double ret = VisionMath.computeAngle(path_direction, this.FORWARD);
        if (path_direction[0] < 0) {
            ret = -ret;
        }
        return ret;
    }

    public double[] computeOffset(double[] img_center) {
        double[] offset = { this.mean[0] - img_center[0], this.mean[1] - img_center[1] };
        return offset;
    }

    public void processFrame(Mat frame) {
        processFrame(frame, null);
    }

    public void processFrame(Mat frame, String saveFile) {
        setFrame(frame); // set input for preprocess
        sliceSize(25, 25); // preprocess (prepare for kmeans)
        localKmeans(2, 4); // preprocess (compute kmeans)
                            //
        Mat pca_draw = iteratePathBinaryPCAAndDraw(resultImg); // draw image with drawn vectors
        if (saveFile != null) {
            // TODO replace with log: System.out.println("SAVING FILE: " + saveFile);
            Imgcodecs.imwrite(saveFile, pca_draw);
        }
    }

    public VisualObject relativePosition(Mat frame) throws Exception {
        processFrame(frame);
        if (result.indexOf(true) >= 0) {
            return new VisualObject(results_prop.get(result.indexOf(true)));
        }
        throw new Exception("Not yet updated.");
    }

    public VisualObject relativePosition(Mat frame, String saveFile) throws Exception {
        processFrame(frame, saveFile);
        if (result.indexOf(true) >= 0) {
            return new VisualObject(results_prop.get(result.indexOf(true)));
        }
        throw new Exception("Not yet updated.");
    }

    public VisualObject[] relativePositions(Mat frame) throws Exception {
        processFrame(frame);
        if (result.indexOf(true) >= 0) {
            ArrayList<VisualObject> paths = new ArrayList<>();
            for (int i = 0; i < result.size(); ++i) {
                if (result.get(i) == true)
                    paths.add(new VisualObject(results_prop.get(i)));
            }
            return paths.toArray(VisualObject[]::new);
        }
        throw new Exception("Not yet updated.");
    }
}
