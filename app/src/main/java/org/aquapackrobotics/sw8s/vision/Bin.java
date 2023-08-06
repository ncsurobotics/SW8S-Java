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
        Cover, Bin_Abydos, Bin_Earth;

        public int to_int() {
            switch (this) {
                case Cover:
                    return 0;
                case Bin_Abydos:
                    return 1;
                case Bin_Earth:
                    return 2;
                default:
                    return -1;
            }
        }

        public static Target[] all() {
            return new Target[] { Cover, Bin_Abydos, Bin_Earth };
        }
    }

    private static String model_path = "models/bins_320.onnx";
    private static String larger_model_path = "models/bins_640.onnx";
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
            if (super.output.indexOf(target.to_int()) >= 0)
                return true;
        }
        return false;
    }

    // turn the detected buoys into a translation vector
    public void transAlign() {
        for (var target : find) {
            int i = target.to_int();
            if (super.output.indexOf(i) >= 0) {
                this.translation = Translation.modelTranslate(super.processImg,
                        super.output_description.get(super.output.indexOf(i)));
                break; // Only process for first match
            }
        }
    }
}
