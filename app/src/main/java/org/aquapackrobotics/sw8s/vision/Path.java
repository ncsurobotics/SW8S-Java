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
      	private Path path_process = new Path();		// initiate
    	private ImagePrep path_prep = path_process; 
  		path_prep.setFrame(frame);					// set input for preprocess
    	path_prep.sliceSize(25, 25);				// preprocess (prepare for kmeans)
    	path_prep.localKmeans(2,4);					// preprocess (compute kmeans)
    	Mat local_kmeans = path_prep.resultImg;		// preprocess output
    	Mat pca_draw = path_process.iteratePathBinaryPCAAndDraw(local_kmeans); // image with drawn vectors
    	
 * numerical outputs see results_prop and results array
 * 
 * @author Xingjian Li
 *
 */

public class Path extends ImagePrep {
	private final int PATH_COLOR_LOW = 60;
	private final int PATH_COLOR_HIGH = 85;
	private final int PATH_WIDTH_LOW = 60;
	private final int PATH_WIDTH_HIGH = 400;
	private final double[] FORWARD = {0,-1};

	private int path_width_idx = 0;
	private int path_length_idx = 0;
	private int path_color = 0;

	public double[] mean = new double[2]; // [x,y]
	public double[] vectors = new double[4];
	public double[] values = new double[2];

	public ArrayList<double[]> results_prop = new ArrayList<>(); //array with each element containing [color, width, angle, vert_offset, hori_offset]
	public ArrayList<Boolean> result = new ArrayList<>(); // array containing boolean values where true = path, false = not path

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
			//System.out.println(on_points.toArray().length);
			// compute the PCA vectors and stuff
			List<Mat> PCA_output = binaryPCA(on_points);
			// convert Mat to double[]
			PCA_output.get(0).get(0,0, this.mean);
			PCA_output.get(1).get(0,0, this.vectors);
			PCA_output.get(2).get(0,0, this.values);
			// filter for the true path
			boolean is_path = pathFilter(color2);
			this.result.add(is_path);

			System.out.println(PCA_output.get(1).dump());
			System.out.println(Arrays.toString(this.vectors));
			//System.out.println(PCA_output.get(1).dump());
			//System.out.println(PCA_output.get(2).dump());

			// compute & store results
			double[] img_center = {colored_image.rows()/2.,colored_image.cols()/2.};
			double[] offset = computeOffset(img_center);
			double[] properties = {color2, computeAngle(), offset[0], offset[1]};
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
			//System.out.println(on_points.toArray().length);
			List<Mat> PCA_output = binaryPCA(on_points);
			PCA_output.get(0).get(0,0, this.mean);
			PCA_output.get(1).get(0,0, this.vectors);
			PCA_output.get(2).get(0,0, this.values);
			boolean is_path = pathFilter(all_colors.get(color));
			//System.out.println(PCA_output.get(1).dump());
			//System.out.println(PCA_output.get(2).dump());
			draw = drawPCA(draw, is_path);

			double[] img_center = {colored_image.rows()/2.,colored_image.cols()/2.};
			double[] offset = computeOffset(img_center);
			double[] properties = {all_colors.get(color), this.values[this.path_width_idx],computeAngle(), offset[0], offset[1]};
			this.results_prop.add(properties);
			this.result.add(is_path);
			System.out.println(Arrays.toString(this.results_prop.get(color)));
		}
		System.out.println(Collections.frequency(this.result, true));
		System.out.println(this.result.indexOf(true));
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
		//Imgproc.arrowedLine(output, center, p2, new Scalar(0,255,0));
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
		//System.out.println(color);
		//System.out.println(this.values[this.path_width_idx]);
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
}
