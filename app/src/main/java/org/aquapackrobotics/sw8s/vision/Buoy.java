package org.aquapackrobotics.sw8s.vision;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

/**
 * Code for "Mark the Grade" task
 * @author Xingjian Li
 *
 */

public class Buoy extends nn_cv2 {
	private static String cfg_path = "D:\\eclipse-workspace\\Data\\models\\yolov3.cfg";
	private static String weights_path = "D:\\eclipse-workspace\\Data\\models\\yolov3.weights";
	private final int bootlegger_classid = 1;
	private final int GMan_classid = 2;
	public boolean Bootlegger = true;
	public boolean GMan = true;
	// x, y ,z
	public double[] translation = {0, 0, 0};
	public double[] rotation = {0, 0, 0};

	// load buoy specific network
	public void load_buoy_model() {
		super.loadModel(cfg_path, weights_path);
	}
	
	// turn the detected point into [x,y,z]
	public void calcAlignVector() {
		// aiming for Bootlegger, and Bootlegger detected
		if (Bootlegger && super.output.indexOf(bootlegger_classid) >= 0) {
			// middle coordinate, top left + width or height
			double x =  super.output_description.get(super.output.indexOf(bootlegger_classid)).x +
						super.output_description.get(super.output.indexOf(bootlegger_classid)).width / 2;
			double y =  super.output_description.get(super.output.indexOf(bootlegger_classid)).y +
						super.output_description.get(super.output.indexOf(bootlegger_classid)).height / 2;
			this.translation[0] = x;
			this.translation[2] = y;
			// min dist would be when the object takes the entire image
			double min_dist = super.processImg.rows() * super.processImg.height();
			// higher distance means further away, normalized between [0,1]
			double distance = (min_dist - super.output_description.get(super.output.indexOf(bootlegger_classid)).area())/min_dist;
			this.translation[1] = distance;
		}
		
		// aiming for GMan, and GMan detected
		if (GMan && super.output.indexOf(GMan_classid) >= 0) {
			double x =  super.output_description.get(super.output.indexOf(GMan_classid)).x +
						super.output_description.get(super.output.indexOf(GMan_classid)).width / 2;
			double y =  super.output_description.get(super.output.indexOf(GMan_classid)).y +
						super.output_description.get(super.output.indexOf(GMan_classid)).height / 2;
			this.translation[0] = x;
			this.translation[2] = y;
			// min dist would be when the object takes the entire image
			double min_dist = super.processImg.rows() * super.processImg.height();
			// higher distance means further away, normalized between [0,1]
			double distance = (min_dist - super.output_description.get(super.output.indexOf(GMan_classid)).area())/min_dist;
			this.translation[1] = distance;
		}
		
		// transform the target with respect to the center of image
		this.translation[0] -= super.processImg.cols()/2;
		this.translation[2] -= super.processImg.rows()/2;
	}
}