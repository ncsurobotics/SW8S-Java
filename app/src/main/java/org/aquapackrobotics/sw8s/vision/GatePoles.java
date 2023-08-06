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

public class GatePoles extends nn_cv2 {
    public enum Target {
        Gate_Large, Gate_Earth, Gate_Abydos, Pole;

        public int to_int() {
            switch (this) {
                case Gate_Large:
                    return 0;
                case Gate_Earth:
                    return 1;
                case Gate_Abydos:
                    return 2;
                case Pole:
                    return 3;
                default:
                    return -1;
            }
        }

        public static Target[] all() {
            return new Target[] { Gate_Large, Gate_Abydos, Gate_Earth, Pole };
        }
    }

    private static String model_path = "models/gate_320_poles.onnx";
    private static String larger_model_path = "models/gate_640_poles.onnx";
    private final Target[] find;

    // + left, - right [-1,1]
    // + up, - down [-1,1]
    // distance [0,1], higher means further away, based on object height
    public double[] translation = { 0, 0, 0 };

    public double[] rotation = { 0, 0, 0 };

    public GatePoles() {
        this(false, Target.all());
    }

    public GatePoles(Target[] find) {
        this(false, find);
    }

    public GatePoles(boolean larger) {
        this(larger, Target.all());
    }

    public GatePoles(boolean larger, Target[] find) {
        super.numObjects = 4;
        // super.CONFIDENCE_MIN = 0.3;
        if (larger) {
            super.loadModel(larger_model_path, 640, 1);
        } else {
            super.loadModel(model_path);
        }
        this.find = find;
    }

    public boolean detected() {
        System.out.println("TESTING DETECTION");
        for (var target : find) {
            if (super.output.indexOf(target.to_int()) >= 0) {
                return true;
            }
        }
        return false;
    }

    public int numDetected() {
        int i = 0;
        for (var target : find) {
            for (int val : super.output) {
                if (val == target.to_int())
                    ++i;
            }
        }
        return i;
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

    public void transAverage() {
        int count = 0;
        this.translation = new double[] { 0.0, 0.0, 0.0 };

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
                this.translation[0] += x;
                this.translation[1] += y;
                // distance is referenced as the ratio of the object height and image height
                // higher distance means further away, normalized between [0,1]
                double min_dist = super.processImg.height();
                double distance = (min_dist - super.output_description.get(super.output.indexOf(i)).height)
                        / min_dist;
                this.translation[2] += distance;
                ++count;
            }
        }

        this.translation[0] /= count;
        this.translation[1] /= count;
        this.translation[2] /= count;

        // transform the target with respect to the center of image, within [-1,1]
        this.translation[0] = (this.translation[0] - super.processImg.cols() / 2) / (super.processImg.cols() / 2);
        this.translation[1] = -(this.translation[1] - super.processImg.rows() / 2) / (super.processImg.rows() / 2);
    }

    public boolean transCompPole() {
        if (!transAlign(new Target[] { Target.Gate_Large, Target.Gate_Earth, Target.Gate_Abydos })) {
            return polesAverage(new Target[] { Target.Pole });
        }
        return true;
    }

    public boolean polesAverage(Target[] find_local) {
        int count = 0;
        this.translation = new double[] { 0.0, 0.0, 0.0 };

        for (var target : find_local) {
            int t = target.to_int();
            for (int i = 0; i < super.output.size(); ++i) {
                if (super.output.get(i) == t) {
                    double x = super.output_description.get(super.output.indexOf(i)).x +
                            super.output_description.get(super.output.indexOf(i)).width / 2;
                    x = x / (super.processImg.width() / 2);
                    x = x < 0.5 ? -x : x - 0.5;
                    double y = super.output_description.get(super.output.indexOf(i)).y +
                            super.output_description.get(super.output.indexOf(i)).height / 2;
                    y = y / (super.processImg.height() / 2);
                    y = y < 0.5 ? -y : y - 0.5;
                    this.translation[0] += x;
                    this.translation[1] += y;
                    // distance is referenced as the ratio of the object height and image height
                    // higher distance means further away, normalized between [0,1]
                    double min_dist = super.processImg.height();
                    double distance = (min_dist - super.output_description.get(super.output.indexOf(i)).height)
                            / min_dist;
                    this.translation[2] += distance;
                    ++count;
                    ;
                }
            }
        }

        this.translation[0] /= count;
        this.translation[1] /= count;
        this.translation[2] /= count;

        // transform the target with respect to the center of image, within [-1,1]
        this.translation[0] = (this.translation[0] - super.processImg.cols() / 2) / (super.processImg.cols() / 2);
        this.translation[1] = -(this.translation[1] - super.processImg.rows() / 2) / (super.processImg.rows() / 2);

        // If only one pole, we target the other side
        if (count == 1) {
            this.translation[0] = -this.translation[0];
            this.translation[1] = -this.translation[1];
        }

        System.out.println("Poles count: " + count);
        return count >= 1;
    }

    // turn the detected gate objects into a translation vector
    public boolean transAlign(Target[] local_find) {
        boolean found = false;
        for (var target : local_find) {
            int i = target.to_int();
            if (super.output.indexOf(i) >= 0) {
                this.translation = Translation.modelTranslate(super.processImg,
                        super.output_description.get(super.output.indexOf(i)));
                return true; // Only process for first match
            }
        }
        return false;
    }
}
