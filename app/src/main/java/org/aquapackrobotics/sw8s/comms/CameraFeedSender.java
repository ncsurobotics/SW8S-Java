package org.aquapackrobotics.sw8s.comms;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;


public class CameraFeedSender {
    
    static{
        try{
            // /path/to/opencv-VERSION.jar
            final File cvjarpath = new File(Core.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            final String cvdir = cvjarpath.getParent();
            final String cvjarfile = cvjarpath.getName();
            final String cvsofile = cvjarfile.replace("opencv-", "libopencv_java").replace(".jar", ".so");
            final String cvdllfile = cvjarfile.replace("opencv-", "opencv_java").replace(".jar", ".dll");
    
            String cvlibpath;
            if(System.getProperty("os.name").toLowerCase().contains("win")){
                String arch = System.getProperty("os.arch");
                if(arch.equals("amd64")){
                    cvlibpath = new File(new File(cvdir, "x64").getPath(), cvdllfile).getPath();
                }else{
                    throw new Exception("Windows is not supported for unknown architecture '" + arch + "'.");
                }
            }else{
                cvlibpath = new File(cvdir, cvsofile).getPath();
            }
            System.out.println("OpenCV Java Library:   " + cvjarpath);
            System.out.println("OpenCV Native Library: " + cvlibpath);
            System.load(cvlibpath);
    
        }catch(Exception e){
            System.err.println("Unable to locate and load OpenCV native library!");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /*
     * Partial pipeline to open a camera
     */
    static String openPipeline(int camIdx, int width, int height, int fps){
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            return String.format("mfvideosrc device-index=%d ! image/jpeg, width=%d, height=%d, framerate=%d/1", camIdx, width, height, fps);
        }else{
            return String.format("v4l2src device=/dev/video%d ! image/jpeg, width=%d, height=%d, framerate=%d/1", camIdx, width, height, fps);
        }
    }

    static String h264encPipeline(int bitrate){
        // Note: On raspberry pi can probably use omx264enc too
        // Could check for RPi by reading /proc/device-tree/model and checking for text "Raspberry Pi"

        if(new File("/etc/nv_tegra_release").exists()){
            // Running on a Jetson. Can use omx264enc
            return String.format("omxh264enc bitrate=%d control-rate=variable ! video/x-h264,profile=baseline ! h264parse config_interval=-1 ! video/x-h264,stream-format=byte-stream,alignment=au", bitrate);
        }else{
            // No clue what system this is. Fallback on software encoder.
            return String.format("x264enc tune=zerolatency speed-preset=ultrafast bitrate=%d ! video/x-h264,profile=baseline ! h264parse config_interval=-1 ! video/x-h264,stream-format=byte-stream,alignment=au", bitrate);
        }
    }

    public static void stream(String[] args){
        // NOTE: tee splits one src to multiple sinks
        String capPl = openPipeline(0, 800, 600, 30) + " ! tee name=t " + 
            "t. ! queue ! jpegdec ! videoconvert ! " + h264encPipeline(2048000) + " ! rtspclientsink location=rtsp://127.0.0.1:8554/cam0 " +
            "t. ! queue ! rtspclientsink location=rtsp://127.0.0.1:8554/cam0jpeg " +
            "t. ! queue ! jpegdec ! videoconvert ! appsink ";
        
        System.out.println(capPl);

        VideoCapture cap = new VideoCapture(capPl, Videoio.CAP_GSTREAMER);
        Mat frame = new Mat();
        while(true) {
            if (cap.read(frame)) {
                Imgcodecs.imwrite("test.jpeg", frame);
            }
        }
    }
}