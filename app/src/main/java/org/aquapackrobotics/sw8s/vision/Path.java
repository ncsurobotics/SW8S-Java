package org.aquapackrobotics.sw8s.vision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Code for "Path" task
 * 
   Usage:
        private Path path_process = new Path();     // initiate
        private ImagePrep path_prep = path_process; 
        path_prep.setFrame(frame);                  // set input for preprocess
        path_prep.sliceSize(25, 25);                // preprocess (prepare for kmeans)
        path_prep.localKmeans(2,4);                 // preprocess (compute kmeans)
        Mat local_kmeans = path_prep.resultImg;     // preprocess output
        
        path_process.iteratePathBinaryPCA(local_kmeans);    // no image output
        // or
        Mat pca_draw = path_process.iteratePathBinaryPCAAndDraw(local_kmeans); // draw image with drawn vectors
        
        // grab the output for first path (see results and results_prop below)
        if (path_process.result.indexOf(true) >= 0) {
            System.out.println(Arrays.toString(path_process.results_prop.get(path_process.result.indexOf(true))));
        }
        
 * numerical outputs see results_prop and results array
 * 
 * @author Xingjian Li
 *
 */

public class Path extends ImagePrep {
    private final int PATH_COLOR_LOW;
    private final int PATH_COLOR_HIGH;
    private final int PATH_WIDTH_LOW;
    private final int PATH_WIDTH_HIGH ;
    private final double[] FORWARD = {0,-1};

    private int path_width_idx = 0;
    private int path_length_idx = 0;
    private int path_color = 0;

    public double[] mean = new double[2]; // [x,y]
    public double[] vectors = new double[4];
    public double[] values = new double[2];

    // positive hori_offset means path is right of the center
    // positive vert_offset means path is down of the center
    // positive angle means path is sloped right(positive)
    public ArrayList<double[]> results_prop = new ArrayList<>(); //array with each element containing [color, width, angle, hori_offset, vert_offset]
    public ArrayList<Boolean> result = new ArrayList<>(); // array containing boolean values where true = path, false = not path

    /**
     * Constructs a new Path with given color and width targets
     */
    public Path(int color_low, int color_high, int width_low, int width_high) {
        PATH_COLOR_LOW = color_low;
        PATH_COLOR_HIGH = color_high;
        PATH_WIDTH_LOW = width_low;
        PATH_WIDTH_HIGH = width_high;
    }

    /**
     * Constructs a new Path using default parameters
     */
    public Path() {
        /* color low, color high, width low, width high */
        this(100, 170, 30, 400);
    }

    /**
     * finds the vectors of each color in the image (converted to gray scale), output are stored in results_prop and results
     * @param colored_image input colored image
     */
    public void iteratePathBinaryPCA(Mat colored_image) {
        Mat gray_image = new Mat();
        Imgproc.cvtColor(colored_image, gray_image, Imgproc.COLOR_BGR2GRAY);
        // obtain all unique colors
        List<Integer> all_colors = uniqueColor(gray_image);
        this.results_prop.clear();
        this.result.clear();
        // for each color
        for (Integer color2 : all_colors) {
            Mat current_bin_image = new Mat();
            // threshold this image
            Core.inRange(gray_image, new Scalar(color2), new Scalar(color2), current_bin_image);
            // find the coordinates where the threshold is high
            MatOfPoint on_points = cvtBinaryToPoints(current_bin_image);
            // compute the PCA vectors and stuff
            List<Mat> PCA_output = binaryPCA(on_points);
            // convert Mat to double[]
            PCA_output.get(0).get(0,0, this.mean);      // center of the path
            PCA_output.get(1).get(0,0, this.vectors);   // direction
            PCA_output.get(2).get(0,0, this.values);    // width
            // filter for the true path
            boolean is_path = pathFilter(color2);
            this.result.add(is_path);

            // compute & store results
            double[] img_center = {colored_image.cols()/2.,colored_image.rows()/2.};
            double[] offset = computeOffset(img_center);
            double[] properties = {color2, this.values[this.path_width_idx], computeAngle(), offset[0], offset[1]};
            this.results_prop.add(properties);

        }
    }
    /**
     * Same as iteratePathBinaryPCA(), but also returns image with vectors drawn, see drawPCA()
     * @param colored_image
     * @return images of vectors drawn
     */
    public Mat iteratePathBinaryPCAAndDraw(Mat colored_image) {
        Mat draw = colored_image.clone();
        Mat gray_image = new Mat();
        Imgproc.cvtColor(colored_image, gray_image, Imgproc.COLOR_BGR2GRAY);
        List<Integer> all_colors = uniqueColor(gray_image);
        this.results_prop.clear();
        this.result.clear();
        for (int color = 0; color < all_colors.size(); color++) {
            Mat current_bin_image = new Mat();
            Core.inRange(gray_image, new Scalar(all_colors.get(color)), new Scalar(all_colors.get(color)), current_bin_image);
            MatOfPoint on_points = cvtBinaryToPoints(current_bin_image);
            List<Mat> PCA_output = binaryPCA(on_points);
            PCA_output.get(0).get(0,0, this.mean);
            PCA_output.get(1).get(0,0, this.vectors);
            PCA_output.get(2).get(0,0, this.values);
            boolean is_path = pathFilter(all_colors.get(color));
            draw = drawPCA(draw, is_path);

            double[] img_center = {colored_image.cols()/2.,colored_image.rows()/2.};
            double[] offset = computeOffset(img_center);
            double[] properties = {all_colors.get(color), this.values[this.path_width_idx],computeAngle(), offset[0], offset[1]};
            this.results_prop.add(properties);
            this.result.add(is_path);
        }
        return draw;
    }

    /**
     * draw PCA vectors
     * @param input_image
     * @param is_path different color for the vector
     * @return drawn image
     */
    public Mat drawPCA(Mat input_image, boolean is_path) {
        Mat output = input_image.clone();
        Point center = new Point(this.mean[0],this.mean[1]);
        if (is_path) {
            Imgproc.circle(output, center, 5, new Scalar(0,255,0));
        } else {
            Imgproc.circle(output, center, 5, new Scalar(0,0,255));
        }
        Point p1 = new Point(center.x + 0.02 * this.vectors[this.path_length_idx]* this.values[this.path_length_idx],
                center.y + 0.02 * this.vectors[this.path_length_idx+1]* this.values[this.path_length_idx]);
        Point p2 = new Point(center.x + 0.02 * this.vectors[this.path_width_idx*2]* this.values[this.path_width_idx],
                center.y + 0.02 * this.vectors[2*this.path_width_idx+1]* this.values[this.path_width_idx]);
        Imgproc.arrowedLine(output, center, p1, new Scalar(255,255,255));
        return output;
    }
    /**
     * process the path properties and filter for the correct one
     * @param color
     * @return
     */
    private boolean pathFilter(int color) {
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
     * @param color
     * @return
     */
    private boolean isPath(int color) {
        if (color > this.PATH_COLOR_HIGH || color < this.PATH_COLOR_LOW || this.values[this.path_width_idx] > this.PATH_WIDTH_HIGH || this.values[this.path_width_idx] < this.PATH_WIDTH_LOW) {
            return false;
        }
        return true;
    }

    /**
     * output results for movements
     * @return
     */
    public double computeAngle() {
        double[] path_direction= {this.vectors[this.path_length_idx],this.vectors[this.path_length_idx+1]};
        double ret = VisionMath.computeAngle(path_direction, this.FORWARD);
        if (path_direction[0] < 0) {ret = -ret;}
        return ret;
    }
    public double[] computeOffset(double[] img_center) {
        double[] offset = {this.mean[0] - img_center[0], this.mean[1] - img_center[1]};
        return offset;
    }

    public Mat processFrame(Mat frame) {
        setFrame(frame);                    // set input for preprocess
        sliceSize(25, 25);              // preprocess (prepare for kmeans)
        localKmeans(2,4);                   // preprocess (compute kmeans)
        
        //iteratePathBinaryPCA(resultImg);  // no image output
        // or
        Mat pca_draw = iteratePathBinaryPCAAndDraw(resultImg); // draw image with drawn vectors
        
        // grab the output for first path (see results and results_prop below)
        if (result.indexOf(true) >= 0) {
            System.out.println(Arrays.toString(results_prop.get(result.indexOf(true))));
        }

        return pca_draw;
    }

    public VisualObject relativePosition(Mat frame) throws Exception {
        processFrame(frame);
        if (result.indexOf(true) >= 0) {
            return new VisualObject(results_prop.get(result.indexOf(true)));
        }
        throw new Exception("Not yet updated.");
    }
}
