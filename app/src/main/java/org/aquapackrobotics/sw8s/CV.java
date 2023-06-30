package org.aquapackrobotics.sw8s;

import java.io.File;

import nu.pattern.OpenCV;

public class CV {
    public static void open() {
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
            System.load(cvlibpath);

        } catch (UnsatisfiedLinkError | Exception e) {
            System.err.println("Unable to locate and load OpenCV native library!");
            OpenCV.loadLocally();
        }
    }
}
