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

    //static final int POOLSIZE = 16;
    static final int POOLSIZE = 128;
    //static final int POOLSIZE = 8;
    
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        String helpFlag[] = {"\nBasic Utility:", "\n'test' -- The Command Flag used in Testing", "'help' or 'h' -- displays list of command flags", "\nStates:", "\n"};
        System.out.println("Basic Format: gradle run --args='_'");        

        /* Special case for testing without control board connection */
        if (args.length == 1 && args[0].equals("--local_comm_test")) {
            System.out.println("COMM TEST");
            Mission missionComms = (Mission) new LocalComms(null, 5000);
            missionComms.run();
            System.exit(0);
        }

        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(POOLSIZE);
        ControlBoardThreadManager manager = new ControlBoardThreadManager(pool);

        for (String str: args) {
            switch (str) {
                case "--test":
                    System.out.println("Yay! it worked!");
                    break;
                case "-h":
                    for(int i = 0; i < helpFlag.length; i++){
                        System.out.println(helpFlag[i]);
                    }
                case "--help":
                    for(int i = 0; i < helpFlag.length; i++){
                        System.out.println(helpFlag[i]);
                    }
                    break;
                case "--raw_test":
                    Mission missionRaw_Test = (Mission) new Raw_Test(manager);
                    missionRaw_Test.run();
                    break;
                case "--local_test":
                    Mission missionLocal_Test = (Mission) new Local_Test(manager);
                    missionLocal_Test.run();
                    break;
                case "--manual":
                    Mission missionManual = (Mission) new ManualMission(manager, 5000);
                    missionManual.run();
                    break;
                case "--motor_test":
                    Mission motorMission = (Mission) new MotorTest(manager);
                    motorMission.run();
                    break;
                case "--submerge_test":
                    Mission submergeMission = (Mission) new SubmergeTest(manager);
                    submergeMission.run();
                    break;
                case "--local_comms":
                    Mission localComms = (Mission) new LocalComms(manager, 5000);
                    localComms.run();
                case "--receive_test":
                    Mission recieveTest = (Mission) new ReceiveTest(manager);
                    recieveTest.run();
                case "--gate":
                    Mission gate = (Mission) new Gate(manager);
                    gate.run();
                case "--gate_stability":
                    Mission stabilityGate = (Mission) new StabilityGate(manager);
                    stabilityGate.run();
                case "--path":
                    Mission path = (Mission) new Path(manager);
                    path.run();
                default:
                    Mission missionAuto = (Mission) new AutoMission(manager);
                    missionAuto.run();
                    break;

            }
        }
        System.exit(0);
    }
}
