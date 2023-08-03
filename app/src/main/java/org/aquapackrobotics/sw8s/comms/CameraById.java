package org.aquapackrobotics.sw8s.comms;

import static java.util.Map.entry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class CameraById {
    static public ArrayList<String> findCamera(String targetSerial) throws IOException, InterruptedException {
        ArrayList<String> cameras = new ArrayList<String>();
        for (int i = 0; i < 30; ++i) {
            String device = "/dev/video" + i;
            File deviceFile = new File(device);
            if (deviceFile.exists()) {
                CommandResult res = Linux.runShellCommand("udevadm info --name=" + device);
                if (res.ec == 0) {
                    for (String line : res.stdout.split("\n")) {
                        int pos = line.indexOf("ID_SERIAL=");
                        if (pos > -1) {
                            String serial = line.substring(pos + 10);
                            if (serial.equals(targetSerial)) {
                                cameras.add(device);
                            }
                        }
                    }
                }
            }
        }
        return cameras;
    }

    static final Map<Integer, String> num_to_serial = Map.ofEntries(entry(0, "TODO"),
            entry(1, "VALUE NEEDED"));

    static public ArrayList<String> findCamera(int id) throws IOException, InterruptedException {
        String serial = num_to_serial.get(id);
        if (serial == null)
            return new ArrayList<>();
        return findCamera(serial);
    }
}
