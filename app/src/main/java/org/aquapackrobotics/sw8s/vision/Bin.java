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
    public enum Target {
        Cover, Bin_1, Bin_2;

        public int to_int() {
            switch (this) {
                case Cover:
                    return 0;
                case Bin_1:
                    return 1;
                case Bin_2:
                    return 2;
                default:
                    return -1;
            }
        }

        public static Target[] all() {
            return new Target[] { Cover, Bin_1, Bin_2 };
        }
    }

    private static String model_path = "bin.onnx";
    private static String larger_model_path = "bin_large.onnx";
    private final Target[] find;

    // + left, - right [-1,1]
    // + up, - down [-1,1]
    // distance [0,1], higher means further away, based on object height
    public double[] translation = { 0, 0, 0 };

    public double[] rotation = { 0, 0, 0 };

    public Bin() {
        this(false, Target.all());
    }

    public Bin(Target[] find) {
        this(false, find);
    }

    public Bin(boolean larger) {
        this(larger, Target.all());
    }

    public Bin(boolean larger, Target[] find) {
        super.numObjects = 3; // left and right buoy
        if (larger) {
            super.loadModel(larger_model_path, 640, 1);
        } else {
            super.loadModel(model_path);
        }
        this.find = find;
    }

    public boolean detected() {
        for (var target : find) {
            int target_int = target.to_int();
            if (super.idMap.containsKey(target_int) && super.output.indexOf(super.idMap.get(target_int)) >= 0)
                return true;
        }
        return false;
    }

    // turn the detected buoys into a translation vector
    public void transAlign() {
        for (var target : find) {
            if (super.idMap.containsKey(target.to_int())) {
                int i = super.idMap.get(target.to_int());
                if (super.output.indexOf(i) >= 0) {
                    // middle coordinate, top left + width or height
                    double x = super.output_description.get(super.output.indexOf(i)).x +
                            super.output_description.get(super.output.indexOf(i)).width / 2;
                    x = x / (super.processImg.width() / 2);
                    x = x < 0.5 ? -x : x - 0.5;
                    double y = super.output_description.get(super.output.indexOf(i)).y +
                            super.output_description.get(super.output.indexOf(i)).height / 2;
                    y = y / (super.processImg.height() / 2);
                    y = y < 0.5 ? -y : y - 0.5;
                    this.translation[0] = x;
                    this.translation[1] = y;
                    // distance is referenced as the ratio of the object height and image height
                    // higher distance means further away, normalized between [0,1]
                    double min_dist = super.processImg.height();
                    double distance = (min_dist - super.output_description.get(super.output.indexOf(i)).height)
                            / min_dist;
                    this.translation[2] = distance;
                    break; // Only process for first match
                }
            }
        }

        // transform the target with respect to the center of image, within [-1,1]
        this.translation[0] = (this.translation[0] - super.processImg.cols() / 2) / (super.processImg.cols() / 2);
        this.translation[1] = -(this.translation[1] - super.processImg.rows() / 2) / (super.processImg.rows() / 2);
    }
}
