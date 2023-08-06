package org.aquapackrobotics.sw8s.vision;

import java.util.Arrays;

import com.google.common.collect.Lists;

/**
 * Code for "Mark the Grade" task
 * 
 * Usage:
 * // init (load the model)
 * private Buoy buoy_process = new Buoy();
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

public class Buoy extends nn_cv2 {
    public enum Target {
        Abydos_1, Abydos_2, Earth_1, Earth_2;

        public int to_int() {
            switch (this) {
                case Earth_1:
                    return 0;
                case Earth_2:
                    return 1;
                case Abydos_1:
                    return 2;
                case Abydos_2:
                    return 3;
                default:
                    return -1;
            }
        }

        public static Target[] all() {
            return new Target[] { Earth_1, Earth_2, Abydos_1, Abydos_2 };
        }
    }

    public volatile static Target global_target = Target.Earth_1;

    private static String model_path = "models/buoy_320.onnx";
    private static String larger_model_path = "models/buoy_640.onnx";
    private final Target[] find;

    // + left, - right [-1,1]
    // + up, - down [-1,1]
    // distance [0,1], higher means further away, based on object height
    public double[] translation = { 0, 0, 0 };

    public double[] rotation = { 0, 0, 0 };

    public Buoy() {
        this(false, Target.all());
    }

    public Buoy(Target[] find) {
        this(false, find);
    }

    public Buoy(boolean larger) {
        this(larger, Target.all());
    }

    public Buoy(boolean larger, Target[] find) {
        if (larger) {
            super.loadModel(larger_model_path, 640, 1);
        } else {
            super.loadModel(model_path);
        }
        super.numObjects = 4; // left and right buoy
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
