package org.aquapackrobotics.sw8s.vision;

/**
 * Code for "Mark the Grade" task
 * 
 * Usage:
 * 		// init (load the model)
 * 		private Buoy buoy_process = new Buoy();
 * 		
 * 		// optional initialization
 * 		// select which target is wanted in transAlign(), second takes priority if both true
 * 		buoy_process.<first/second> = true/false; // default both true
 * 		
 * 
 * 		// detect where the buoy is, it returns the bounding box image if you want
 * 		Mat detected_image = buoy_process.detectYoloV5(image);
 * 		
 * 		// process numerical values on where the target is
 * 		// align for translation vector (image_x, image_y, distance approximation)
 * 		buoy_process.transAlign(); 
 * 
 * 		// access numerical values
 * 			buoy_process.translation
 * 			buoy_process.rotation // currently not considered
 * 		
 * @author Xingjian Li
 *
 */

public class Buoy extends nn_cv2 {
	private static String model_path = "D:\\eclipse-workspace\\Data\\models\\buoy_v5n_best.onnx";
	private final int first_classid = 0;
	private final int second_classid = 1;
	public boolean first = true;
	public boolean second = true;
	
	// + left, - right [-1,1]
	// + up, - down [-1,1]
	// distance [0,1], higher means further away, based on object height
	public double[] translation = {0, 0, 0};
	
	public double[] rotation = {0, 0, 0};

	// load buoy specific network
	public void load_buoy_model() {
		super.loadModel(model_path);
		super.numObjects = 2;	// left and right buoy
	}
	
	// turn the detected buoys into a translation vector
	public void transAlign() {
		// aiming for Bootlegger, and Bootlegger detected
		if (first && super.output.indexOf(first_classid) >= 0) {
			// middle coordinate, top left + width or height
			System.out.println(super.output);
			System.out.println(super.output_description);
			double x =  super.output_description.get(super.output.indexOf(first_classid)).x +
						super.output_description.get(super.output.indexOf(first_classid)).width / 2;
			double y =  super.output_description.get(super.output.indexOf(first_classid)).y +
						super.output_description.get(super.output.indexOf(first_classid)).height / 2;
			this.translation[0] = x;
			this.translation[1] = y;
			// distance is referenced as the ratio of the object height and image height
			// higher distance means further away, normalized between [0,1]
			double min_dist = super.processImg.height();
			double distance = (min_dist - super.output_description.get(super.output.indexOf(first_classid)).height)/min_dist;
			this.translation[2] = distance;
		}
		
		// aiming for GMan, and GMan detected
		if (second && super.output.indexOf(second_classid) >= 0) {
			double x =  super.output_description.get(super.output.indexOf(second_classid)).x +
						super.output_description.get(super.output.indexOf(second_classid)).width / 2;
			double y =  super.output_description.get(super.output.indexOf(second_classid)).y +
						super.output_description.get(super.output.indexOf(second_classid)).height / 2;
			this.translation[0] = x;
			this.translation[1] = y;
			// min dist would be when the object takes the entire image
			double min_dist = super.processImg.height();
			double distance = (min_dist - super.output_description.get(super.output.indexOf(second_classid)).height)/min_dist;
			this.translation[2] = distance;
		}
		
		// transform the target with respect to the center of image, within [-1,1]
		this.translation[0] = (this.translation[0] - super.processImg.cols()/2) / (super.processImg.cols()/2);
		this.translation[1] = -(this.translation[1] - super.processImg.rows()/2) / (super.processImg.rows()/2);
		System.out.printf("Translation to Target Buoy: \n\t x: %.2f \n\t y: %.2f \n\t distance: %.2f\n",
				this.translation[0], this.translation[1], this.translation[2]);
	}
}