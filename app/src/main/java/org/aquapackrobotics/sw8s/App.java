/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.aquapackrobotics.sw8s;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.aquapackrobotics.sw8s.missions.*;
import org.aquapackrobotics.sw8s.comms.*;

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
        try {
            return (manager == null) ? manager = new CommsThreadManager(getPool()) : manager;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
            return null;
        }
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

            Mission mission;
            CameraFeedSender.openCapture(0, missionName);
            CameraFeedSender.openCapture(1, missionName);

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
                    CameraFeedSender.openCapture(0);
                    CameraFeedSender.openCapture(1);
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

                case "--path":
                    mission = (Mission) new PathYUV(getManager(), missionName);
                    break;
                case "--buoy":
                    mission = (Mission) new Buoys(getManager(), missionName);
                    break;
                case "--octagon":
                    mission = (Mission) new OctagonYUV(getManager(), missionName);
                    break;
                case "--bin":
                    mission = (Mission) new Bin(getManager(), missionName);
                    break;
                case "--arm":
                default:
                    mission = (Mission) new WaitArm(getManager(), missionName);
                    break;
            }
            System.out.println("RUN MISSION: " + mission.getClass().getName());
            mission.run();
        }
        System.exit(0);
    }
}
