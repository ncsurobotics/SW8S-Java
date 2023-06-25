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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class FilenameComparator implements Comparator<String> {
    private static final Pattern NUMBERS = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

    @Override
    public final int compare(String o1, String o2) {
        // Optional "NULLS LAST" semantics:
        if (o1 == null || o2 == null)
            return o1 == null ? o2 == null ? 0 : -1 : 1;

        // Splitting both input strings by the above patterns
        String[] split1 = NUMBERS.split(o1);
        String[] split2 = NUMBERS.split(o2);
        for (int i = 0; i < Math.min(split1.length, split2.length); i++) {
            char c1 = split1[i].charAt(0);
            char c2 = split2[i].charAt(0);
            int cmp = 0;

            // If both segments start with a digit, sort them numerically using
            // BigInteger to stay safe
            if (c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9')
                cmp = new BigInteger(split1[i]).compareTo(new BigInteger(split2[i]));

            // If we haven't sorted numerically before, or if numeric sorting yielded
            // equality (e.g 007 and 7) then sort lexicographically
            if (cmp == 0)
                cmp = split1[i].compareTo(split2[i]);

            // Abort once some prefix has unequal ordering
            if (cmp != 0)
                return cmp;
        }

        // If we reach this, then both strings have equally ordered prefixes, but
        // maybe one string is longer than the other (i.e. has more segments)
        return split1.length - split2.length;
    }
}

public class CameraFeedSender {

    static {
        try {
            // /path/to/opencv-VERSION.jar
            final File cvjarpath = new File("/opt/opencv-4.6.0/share/java/opencv4/opencv-460.jar");
            final String cvdir = cvjarpath.getParent();
            final String cvjarfile = cvjarpath.getName();
            final String cvsofile = cvjarfile.replace("opencv-", "libopencv_java").replace(".jar", ".so");
            final String cvdllfile = cvjarfile.replace("opencv-", "opencv_java").replace(".jar", ".dll");

            String cvlibpath;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                String arch = System.getProperty("os.arch");
                if (arch.equals("amd64")) {
                    cvlibpath = new File(new File(cvdir, "x64").getPath(), cvdllfile).getPath();
                } else {
                    throw new Exception("Windows is not supported for unknown architecture '" + arch + "'.");
                }
            } else {
                cvlibpath = new File(cvdir, cvsofile).getPath();
            }
            System.out.println("OpenCV Java Library:   " + cvjarpath);
            System.out.println("OpenCV Native Library: " + cvlibpath);
            System.load(cvlibpath);

        } catch (Exception e) {
            System.err.println("Unable to locate and load OpenCV native library!");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

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
            return String.format("omxh264enc bitrate=%d control-rate=variable ! video/x-h264,profile=baseline",
                    bitrate);
        } else {
            // No clue what system this is. Fallback on software encoder.
            return String.format(
                    "x264enc tune=zerolatency speed-preset=ultrafast bitrate=%d ! video/x-h264,profile=baseline",
                    bitrate);
        }
    }

    static String saveFile(String basename) {
        String dirPath = System.getProperty("user.home");
        if (new File("/etc/nv_tegra_release").exists()) {
            // Record to writable USB on SW8 Jetson if present
            File f = new File("/mnt/data/");
            if (f.exists() && f.isDirectory() && f.canWrite())
                dirPath = "/mnt/data/";
        }
        dirPath += "/camtest-recordings/";
        File dir = new File(dirPath);

        // Create recording directory if it does not exist
        if (!dir.exists())
            dir.mkdirs();

        // Files are named basename_number.mp4
        // where number increments over time
        // Pick the next unused number
        Set<String> filesSet = Stream.of(dir.listFiles())
                .filter(file -> !file.isDirectory())
                .filter(file -> file.getName().startsWith(basename + "_"))
                .filter(file -> file.getName().endsWith(".mp4"))
                .map(File::getName)
                .collect(Collectors.toSet());
        ArrayList<String> filesList = new ArrayList<String>();
        for (String file : filesSet) {
            filesList.add(file);
        }
        Collections.sort(filesList, new FilenameComparator());

        // Pick the next number. Loop is used to handle files
        // without number (ex: basename_abc) being present in the directory
        int num = 0;
        for (int i = filesList.size() - 1; i >= 0; --i) {
            String file = filesList.get(i);
            file = file.substring(basename.length() + 1, file.length() - 4);
            try {
                num = Integer.parseInt(file) + 1;
                break;
            } catch (Exception e) {
            }
        }
        String filePath = dirPath + "/" + basename + "_" + num + ".mp4";

        return filePath;
    }

    private static HashMap<Integer, VideoCapture> heldCaptures = new HashMap<>();

    public static void borrowed() {
        System.out.println("RETURN OPENED CAPTURE");

        String savefile0 = saveFile("cam0");
        String savefile1 = saveFile("cam1");

        String cap0Pl = openPipeline(0, 800, 600, 30) + " ! jpegdec ! tee name=raw " +
                "raw. ! queue  ! videoconvert ! appsink " +
                "raw. ! queue  ! videoconvert ! " + h264encPipeline(2048000) + " ! tee name=h264 " +
                "h264. ! queue ! h264parse config_interval=-1 ! video/x-h264,stream-format=byte-stream,alignment=au ! rtspclientsink location=rtsp://127.0.0.1:8554/cam0 "
                +
                "h264. ! queue ! mpegtsmux ! filesink location=\"" + savefile0 + "\" ";

        String cap1Pl = openPipeline(1, 800, 600, 30) + " ! jpegdec ! tee name=raw " +
                "raw. ! queue  ! videoconvert ! appsink " +
                "raw. ! queue  ! videoconvert ! " + h264encPipeline(2048000) + " ! tee name=h264 " +
                "h264. ! queue ! h264parse config_interval=-1 ! video/x-h264,stream-format=byte-stream,alignment=au ! rtspclientsink location=rtsp://127.0.0.1:8554/cam1 "
                +
                "h264. ! queue ! mpegtsmux ! filesink location=\"" + savefile1 + "\" ";

        System.out.println();
        System.out.println("------------------------------");
        System.out.print("Cap0 Pipeline: ");
        System.out.println(cap0Pl);
        System.out.print("Cap1 Pipeline: ");
        System.out.println(cap1Pl);
        System.out.println("------------------------------");
        System.out.print("Savefile0: ");
        System.out.println(savefile0);
        System.out.print("Savefile1: ");
        System.out.println(savefile1);
        System.out.println("------------------------------");
        System.out.println();

        VideoCapture cap0 = new VideoCapture(cap0Pl, Videoio.CAP_GSTREAMER);
        VideoCapture cap1 = new VideoCapture(cap1Pl, Videoio.CAP_GSTREAMER);

        heldCaptures.put(0, cap0);
        heldCaptures.put(1, cap1);

        Mat frame0 = new Mat();
        Mat frame1 = new Mat();
    }

    public static VideoCapture openCapture(int id) {
        if (!heldCaptures.containsKey(id)) {
            borrowed();
        }
        return heldCaptures.get(id);
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
