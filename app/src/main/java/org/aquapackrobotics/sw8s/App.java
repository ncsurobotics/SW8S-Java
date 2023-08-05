/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.aquapackrobotics.sw8s;

import java.awt.color.CMMException;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.missions.*;
import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.comms.meb.MEBStatus;

import java.util.concurrent.*;

public class App {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT | %4$s | %5$s %n");
    }

    static final int POOLSIZE = 128;
    static ScheduledThreadPoolExecutor pool = null;
    static CommsThreadManager manager = null;

    public static ScheduledThreadPoolExecutor getPool() {
        return (pool == null) ? pool = new ScheduledThreadPoolExecutor(POOLSIZE) : pool;
    }

    public static CommsThreadManager getManager() {
        /*
         * try {
         * return (manager == null) ? manager = new CommsThreadManager(getPool()) :
         * manager;
         * } catch (Exception e) {
         * e.printStackTrace();
         * System.exit(2);
         * return null;
         * }
         */
        return null;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        String helpFlag[] = { "\nBasic Utility:",
                "\n'test' -- The Command Flag used in Testing",
                "'help' or 'h' -- displays list of command flags", "\nStates:", "\n" };
        System.out.println("Basic Format: gradle run --args='_'");

        String missionName = null;

        for (String str : args) {
            if (!str.contains("-")) {
                missionName = str;
                System.out.println("Mission name: " + missionName);
                continue;
            }

            Mission mission = null;
            // CameraFeedSender.openCapture(Camera.BOTTOM, missionName);
            // CameraFeedSender.openCapture(Camera.FRONT, missionName);
            // Linux.changeExposure(Camera.BOTTOM, 20);
            // Linux.changeExposure(Camera.FRONT, 18);
            // Linux.disableAutofocus(Camera.FRONT);
            // Linux.disableAutofocus(Camera.BOTTOM);

            if (str.startsWith("--set-cb-tty=")) {
                String port = str.substring(13);
                System.out.println("Setting control board serial port to '" + port + "'.");
                CommsThreadManager.setControlBoardPort(port);
            } else if (str.startsWith("--set-meb-tty=")) {
                String port = str.substring(14);
                System.out.println("Setting MEB board serial port to '" + port + "'.");
                CommsThreadManager.setMEBPort(port);
            } else {

                switch (str) {
                    case "--test":
                        System.out.println("Yay! it worked!");
                        System.exit(0);
                    case "-h":
                        for (int i = 0; i < helpFlag.length; i++) {
                            System.out.println(helpFlag[i]);
                        }
                        System.exit(0);
                    case "--help":
                        for (int i = 0; i < helpFlag.length; i++) {
                            System.out.println(helpFlag[i]);
                        }
                        System.exit(0);
                    case "--raw_test":
                        mission = (Mission) new Raw_Test(getManager());
                        break;
                    case "--local_test":
                        mission = (Mission) new Local_Test(getManager());
                        break;
                    case "--manual":
                        mission = (Mission) new ManualMission(getManager(), 5000);
                        break;
                    case "--motor_test":
                        mission = (Mission) new MotorTest(getManager());
                        break;
                    case "--submerge_test":
                        mission = (Mission) new SubmergeTest(getManager());
                        break;
                    case "--local_comms":
                        mission = (Mission) new LocalComms(getManager(), 5000);
                        break;
                    case "--receive_test":
                        mission = (Mission) new ReceiveTest(getManager());
                        break;
                    case "--cam_test":
                        CameraFeedSender.openCapture(Camera.BOTTOM);
                        CameraFeedSender.openCapture(Camera.FRONT);
                        Thread.sleep(60_000);
                    case "--dropper_test":
                        mission = (Mission) new DropperTest(getManager());
                        break;
                    case "--kill-confirm":
                        while (true) {
                            try {
                                getManager().setMotorSpeeds((float) 0.3, (float) 0.0, (float) 0.0, (float) 0.0,
                                        (float) 0.0,
                                        (float) 0.0, (float) 0.0, (float) 0.0).wait();
                                while (true)
                                    ;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    case "--path_yuv":
                    case "--path":
                        mission = (Mission) new PathYUV(getManager(), missionName);
                        break;
                    case "--buoys":
                    case "--buoy":
                        mission = (Mission) new Buoys(getManager(), missionName);
                        break;
                    case "--buoy_path":
                        mission = (Mission) new BuoyPath(getManager(), missionName);
                        break;
                    case "--buoys_spin":
                    case "--buoys_spins":
                    case "--buoy_spins":
                    case "--buoy_spin":
                        mission = (Mission) new BuoySpin(getManager(), missionName);
                        break;
                    case "--octagon":
                        mission = (Mission) new OctagonYUV(getManager(), missionName);
                        break;
                    case "--bins":
                    case "--bin":
                        mission = (Mission) new Bin(getManager(), missionName);
                        break;
                    case "--bin_variant":
                        mission = (Mission) new VariantBin(getManager(), missionName);
                        break;
                    case "--gate":
                        mission = (Mission) new GateMission(getManager(), missionName);
                        break;
                    case "--gate_path":
                        mission = (Mission) new GatePathMission(getManager(), missionName);
                        break;
                    case "--flip":
                        System.out.println("Old BOTTOM: " + String.valueOf(Camera.BOTTOM.getID()));
                        System.out.println("Old TOP: " + String.valueOf(Camera.BOTTOM.getID()));
                        Camera.swap();
                        System.out.println("New BOTTOM: " + String.valueOf(Camera.BOTTOM.getID()));
                        System.out.println("New TOP: " + String.valueOf(Camera.BOTTOM.getID()));
                        continue;
                    case "--arm":
                        CameraFeedSender.openCapture(Camera.FRONT); // TODO REMOVE
                        while (!MEBStatus.isArmed) {
                            Thread.sleep(100);
                        }
                        continue;
                }
            }

            if (mission == null)
                mission = (Mission) new WaitArm(getManager(), missionName);

            System.out.println("RUN MISSION: " + mission.getClass().getName());
            mission.run();
        }
        System.exit(0);
    }
}
