package org.aquapackrobotics.sw8s.vision;

import org.opencv.dnn.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import org.opencv.core.*;

/**
 * Code for general opencv dnn tasks
 * 
 * @author Xingjian Li
 *
 */

public class nn_cv2 extends ImagePrep {
    private Net net;
    private List<String> outBlobNames = new ArrayList<>();

    public int numObjects = 2;

    public List<Integer> output = new ArrayList<>();
    public List<Rect2d> output_description = new ArrayList<>();

    /**
     * load a Yolov5 model
     * 
     * @param model, onnx format, 640x640 model input
     */
    public void loadModel(String model) {
        this.net = Dnn.readNet(model);
        this.outBlobNames = getOutputNames(net);
        this.net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        this.net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
    }

    /**
     * inference (process the image)
     * 
     * @param image, 640x480 RGB image
     * @return processed image with drawn bounding boxes
     */
    public Mat detectYoloV5(Mat image) {
        super.processImg = image;
        output.clear();
        output_description.clear();
        List<Mat> result = new ArrayList<>();
        Mat blob = Dnn.blobFromImage(image, 1 / 255.0, new Size(320, 320), new Scalar(0), true, false);
        this.net.setInput(blob);
        this.net.forward(result, this.outBlobNames);
        List<Integer> clsIds = new ArrayList<>();
        List<Float> confs = new ArrayList<>();
        List<Rect2d> rects = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            Mat level = result.get(i);
            level = level.reshape(1, (int) level.total() / (5 + this.numObjects));

            System.out.println("row " + level.row(0).dump());
            System.out.println("row " + level.row(0).colRange(5, level.cols()).dump());
            System.out.println("row " + level.row(0).get(0, 4)[0]);
            for (int j = 0; j < level.rows(); j++) {
                Mat row = level.row(j);
                Mat scores = row.colRange(5, level.cols());
                Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                double confidence = (double) (row.get(0, 4)[0]);

                Point classIdPoint = mm.maxLoc;
                if (confidence > .7) {
                    int centerX = (int) (row.get(0, 0)[0]) * 2; // scaling for drawing the bounding boxes
                    int centerY = (int) ((row.get(0, 1)[0] * 2) / 640 * 480);
                    int width = (int) (row.get(0, 2)[0]) * 2;
                    int height = (int) ((row.get(0, 3)[0] * 2) / 640 * 480);
                    int left = centerX - width / 2;
                    int top = centerY - height / 2;
                    clsIds.add((int) classIdPoint.x);
                    confs.add((float) confidence);
                    rects.add(new Rect2d(left, top, width, height));

                }
            }
        }
        if (confs.size() == 0) {
            System.out.println("Nothing");
            return image;
        }
        MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));
        Rect2d[] boxesArray = rects.toArray(new Rect2d[0]);
        MatOfRect2d boxes = new MatOfRect2d(boxesArray);
        MatOfInt indices = new MatOfInt();
        Dnn.NMSBoxes(boxes, confidences, .5f, .5f, indices); // We draw the bounding boxes for objects here

        int[] ind = indices.toArray();
        int j = 0;
        Mat out = image.clone();
        for (int i = 0; i < ind.length; ++i) {
            int idx = ind[i];
            int clsId = clsIds.get(idx);
            Rect2d box = boxesArray[idx];
            Imgproc.rectangle(out, box.tl(), box.br(), new Scalar(0, 0, 255), clsId + 1);
            output.add(clsId);
            output_description.add(box);
        }
        return out;
    }

    private static List<String> getOutputNames(Net net) {
        List<String> names = new ArrayList<>();

        List<Integer> outLayers = net.getUnconnectedOutLayers().toList();
        List<String> layersNames = net.getLayerNames();

        outLayers.forEach((item) -> names.add(layersNames.get(item - 1))); // unfold and create R-CNN layers from the
                                                                            // loaded YOLO model
        return names;
    }
}
