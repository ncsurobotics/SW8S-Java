package org.aquapackrobotics.sw8s.vision;

/**
 * Code for "Mark the Grade" task
 * 
 * Usage:
 * // init (load the model)
 * private Gate gate_process = new Gate();
 * 
 * // optional initialization
 * // select which target is wanted in transAlign(), second takes priority if
 * both true
 * gate_process.<first/second> = true/false; // default both true
 * 
 * 
 * // detect where the gate is, it returns the bounding box image if you want
 * Mat detected_image = gate_process.detectYoloV5(image);
 * 
 * // process numerical values on where the target is
 * // align for translation vector (image_x, image_y, distance approximation)
 * gate_process.transAlign();
 * 
 * // access numerical values
 * gate_process.translation
 * gate_process.rotation // currently not considered
 * 
 * @author Xingjian Li
 *
 */

public class Gate extends nn_cv2 {
    public enum Target {
        Gate_Large, Gate_Earth, Gate_Abydos;

        public int to_int() {
            switch (this) {
                case Gate_Large:
                    return 0;
                case Gate_Earth:
                    return 1;
                case Gate_Abydos:
                    return 2;
                default:
                    return -1;
            }
        }

        public static Target[] all() {
            return new Target[] { Gate_Large, Gate_Abydos, Gate_Earth };
        }
    }

    private static String model_path = "models/gate_320.onnx";
    private static String larger_model_path = "models/gate_640.onnx";
    private final Target[] find;

    // + left, - right [-1,1]
    // + up, - down [-1,1]
    // distance [0,1], higher means further away, based on object height
    public double[] translation = { 0, 0, 0 };

    public double[] rotation = { 0, 0, 0 };

    public Gate() {
        this(false, Target.all());
    }

    public Gate(Target[] find) {
        this(false, find);
    }

    public Gate(boolean larger) {
        this(larger, Target.all());
    }

    public Gate(boolean larger, Target[] find) {
        super.numObjects = 3;
        if (larger) {
            super.loadModel(larger_model_path, 640, 1);
        } else {
            super.loadModel(model_path);
        }
        this.find = find;
    }

    public boolean detected() {
        for (var target : find) {
            if (super.output.indexOf(target.to_int()) >= 0)
                return true;
        }
        return false;
    }

    // turn the detected gate objects into a translation vector
    public void transAlign() {
        for (var target : find) {
            int i = target.to_int();
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

        // transform the target with respect to the center of image, within [-1,1]
        this.translation[0] = (this.translation[0] - super.processImg.cols() / 2) / (super.processImg.cols() / 2);
        this.translation[1] = -(this.translation[1] - super.processImg.rows() / 2) / (super.processImg.rows() / 2);
    }
}
