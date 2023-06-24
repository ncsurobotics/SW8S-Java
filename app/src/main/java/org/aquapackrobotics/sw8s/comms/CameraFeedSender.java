package org.aquapackrobotics.sw8s.comms;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.CvType;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class CameraFeedSender {

    /*
     * Partial pipeline to open a camera
     */
    static String openPipeline(int camIdx, int width, int height, int fps) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return String.format("mfvideosrc device-index=%d ! image/jpeg, width=%d, height=%d, framerate=%d/1", camIdx,
                    width, height, fps);
        } else {
            return String.format("v4l2src device=/dev/video%d ! image/jpeg, width=%d, height=%d, framerate=%d/1",
                    camIdx, width, height, fps);
        }
    }

    static String h264encPipeline(int bitrate) {
        // Note: On raspberry pi can probably use omx264enc too
        // Could check for RPi by reading /proc/device-tree/model and checking for text
        // "Raspberry Pi"

        if (new File("/etc/nv_tegra_release").exists()) {
            // Running on a Jetson. Can use omx264enc
            return String.format(
                    "omxh264enc bitrate=%d control-rate=variable ! video/x-h264,profile=baseline ! h264parse config_interval=-1 ! video/x-h264,stream-format=byte-stream,alignment=au",
                    bitrate);
        } else {
            // No clue what system this is. Fallback on software encoder.
            return String.format(
                    "x264enc tune=zerolatency speed-preset=ultrafast bitrate=%d ! video/x-h264,profile=baseline ! h264parse config_interval=-1 ! video/x-h264,stream-format=byte-stream,alignment=au",
                    bitrate);
        }
    }

    public static void stream(String[] args) {
        // NOTE: tee splits one src to multiple sinks
        String capPl = openPipeline(0, 800, 600, 30) + " ! tee name=t " +
                "t. ! queue ! jpegdec ! videoconvert ! " + h264encPipeline(2048000)
                + " ! rtspclientsink location=rtsp://127.0.0.1:8554/cam0 " +
                "t. ! queue ! rtspclientsink location=rtsp://127.0.0.1:8554/cam0jpeg " +
                "t. ! queue ! jpegdec ! videoconvert ! appsink ";

        System.out.println(capPl);

        VideoCapture cap = new VideoCapture(capPl, Videoio.CAP_GSTREAMER);
        Mat frame = new Mat();
        while (true) {
            if (cap.read(frame)) {
                Imgcodecs.imwrite("test.jpeg", frame);
            }
        }
    }

    private static HashMap<Integer, VideoCapture> heldCaptures = new HashMap<>();

    public static VideoCapture openCapture(int id) {
        if (heldCaptures.containsKey(id)) {
            return heldCaptures.get(id);
        } else {
            // NOTE: tee splits one src to multiple sinks
            String capPl = openPipeline(id, 800, 600, 30) + " ! tee name=t " +
                    "t. ! queue ! jpegdec ! videoconvert ! " + h264encPipeline(2048000)
                    + " ! rtspclientsink location=rtsp://127.0.0.1:8554/cam" + Integer.toString(id) + " " +
                    "t. ! queue ! rtspclientsink location=rtsp://127.0.0.1:8554/cam" + Integer.toString(id) + "jpeg " +
                    "t. ! queue ! jpegdec ! videoconvert ! appsink ";

            System.out.println(capPl);

            var cap = new VideoCapture(capPl, Videoio.CAP_GSTREAMER);
            heldCaptures.put(id, cap);
            return cap;
        }
    }

    public static VideoCapture openCapture() {
        return openCapture(0);
    }

    public static Mat convertImage(File input) throws IOException {
        BufferedImage image = ImageIO.read(input);
        BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        imageCopy.getGraphics().drawImage(image, 0, 0, null);
        byte[] data = ((DataBufferByte) imageCopy.getRaster().getDataBuffer()).getData();
        return new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
    }
}
