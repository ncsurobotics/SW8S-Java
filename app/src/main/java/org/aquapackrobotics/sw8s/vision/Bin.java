package org.aquapackrobotics.sw8s.vision;

/**
 * Code for "Mark the Grade" task
 * 
 * Usage:
 * // init (load the model)
 * private Bin buoy_process = new Bin();
 * 
 * // optional initialization
 * // select which target is wanted in transAlign(), second takes priority if
 * both true
 * buoy_process.<first/second> = true/false; // default both true
 * 
 * 
 * // detect where the buoy is, it returns the bounding box image if you want
 * Mat detected_image = buoy_process.detectYoloV5(image);
 * 
 * // process numerical values on where the target is
 * // align for translation vector (image_x, image_y, distance approximation)
 * buoy_process.transAlign();
 * 
 * // access numerical values
 * buoy_process.translation
 * buoy_process.rotation // currently not considered
 * 
 * @author Xingjian Li
 *
 */

public class Bin extends nn_cv2 {
    private static String model_path = "bin.onnx";
    private static String larger_model_path = "bin_large.onnx";
    private final boolean[] find;

    // + left, - right [-1,1]
    // + up, - down [-1,1]
    // distance [0,1], higher means further away, based on object height
    public double[] translation = { 0, 0, 0 };

    public double[] rotation = { 0, 0, 0 };

    public Bin() {
        this(false, new boolean[] { true, true });
    }

    public Bin(boolean[] find) {
        this(false, find);
    }

    public Bin(boolean larger) {
        this(larger, new boolean[] { true, true });
    }

    public Bin(boolean larger, boolean[] find) {
        if (larger) {
            super.loadModel(larger_model_path, 640, 1);
        } else {
            super.loadModel(model_path);
        }
        super.numObjects = 2; // left and right buoy
        this.find = find;
    }

    public boolean detected() {
        for (int i = 0; i < find.length; i++) {
            if (find[i] && super.output.indexOf(i) >= 0)
                return true;
        }
        return false;
    }

    // turn the detected buoys into a translation vector
    public void transAlign() {
        for (int i = 0; i < find.length; i++) {
            if (find[i] && super.output.indexOf(i) >= 0) {
                // middle coordinate, top left + width or height
                double x = super.output_description.get(super.output.indexOf(i)).x +
                        super.output_description.get(super.output.indexOf(i)).width / 2;
                double y = super.output_description.get(super.output.indexOf(i)).y +
                        super.output_description.get(super.output.indexOf(i)).height / 2;
                this.translation[0] = x;
                this.translation[1] = y;
                // distance is referenced as the ratio of the object height and image height
                // higher distance means further away, normalized between [0,1]
                double min_dist = super.processImg.height();
                double distance = (min_dist - super.output_description.get(super.output.indexOf(i)).height)
                        / min_dist;
                this.translation[2] = distance;
                return; // Only process for first match
            }
        }

        // transform the target with respect to the center of image, within [-1,1]
        this.translation[0] = (this.translation[0] - super.processImg.cols() / 2) / (super.processImg.cols() / 2);
        this.translation[1] = -(this.translation[1] - super.processImg.rows() / 2) / (super.processImg.rows() / 2);
    }
}
