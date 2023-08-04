package org.aquapackrobotics.sw8s.states.GatePathStates;

import java.io.File;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Future;

import org.aquapackrobotics.sw8s.comms.Camera;
import org.aquapackrobotics.sw8s.comms.CameraFeedSender;
import org.aquapackrobotics.sw8s.comms.CommsThreadManager;
import org.aquapackrobotics.sw8s.states.State;
import org.aquapackrobotics.sw8s.vision.Gate;
import org.aquapackrobotics.sw8s.vision.GatePoles;
import org.aquapackrobotics.sw8s.vision.PathYUV;
import org.aquapackrobotics.sw8s.vision.VisualObject;
import org.aquapackrobotics.sw8s.vision.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class GatePathDetectState extends State {

    private ScheduledFuture<byte[]> depthRead;
    private PathYUV path;
    private GatePoles gateNoPole;
    private GatePoles gateOnlyPole;
    private final File DirPath;
    private final File DirGate;

    private double[] PathYUVOpts = { 0.40, 0.35, 0.3, 0.2 };
    private int PathYUVidx = 0;
    private String missionName;
    private double initialYaw;
    private double curPitch;
    private double combinedAngle;
    private final double MISSION_DEPTH;

    private double strafe;
    private int count;
    private int count_max;

    private ScheduledFuture<Boolean> pathRunnable;
    private ScheduledFuture<Boolean> gateRunnable;
    private boolean lastPathVal;
    private boolean lastGateVal;

    public GatePathDetectState(CommsThreadManager manager, String missionName, double initialYaw,
            double MISSION_DEPTH) {
        super(manager);
        this.PathYUVidx = 0;
        path = new PathYUV(this.PathYUVOpts[this.PathYUVidx]);
        gateNoPole = new GatePoles(true,
                new GatePoles.Target[] { GatePoles.Target.Gate_Large, GatePoles.Target.Gate_Earth,
                        GatePoles.Target.Gate_Abydos });
        gateOnlyPole = new GatePoles(true, new GatePoles.Target[] { GatePoles.Target.Pole });
        DirPath = new File("/mnt/data/" + missionName + "/pathYUV");
        DirPath.mkdir();
        DirGate = new File("/mnt/data/" + missionName + "/gate");
        DirGate.mkdir();
        this.missionName = missionName;
        this.initialYaw = initialYaw;
        this.curPitch = 30;
        this.combinedAngle = initialYaw;
        this.MISSION_DEPTH = MISSION_DEPTH;
        this.strafe = -0.15;
        this.count = 0;
        this.count_max = 10;
        try {
            pathRunnable = manager.scheduleCallable(pathAlign());
            gateRunnable = manager.scheduleCallable(gateAlign(gateNoPole));
        } catch (Exception e) {
            e.printStackTrace();
        }
        lastPathVal = false;
        lastGateVal = false;
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        try {
            depthRead = manager.MSPeriodicRead((byte) 1);
            var mreturn = manager.setStability2Speeds(0.15, 0.8, curPitch, 0, initialYaw, MISSION_DEPTH);
            while (!mreturn.isDone())
                ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Callable<Boolean> pathAlign() {
        return new Callable<>() {
            @Override
            public Boolean call() {

                Mat frame = CameraFeedSender.getFrame(Camera.BOTTOM);
                try {
                    VisualObject footage = path.relativePosition(frame,
                            DirPath.toString() + "/" + Instant.now().toString());
                    System.out.println("Original: " + footage);
                    DoubleTriple trans = Translation.movement_triple(
                            new DoublePair(footage.horizontal_offset, footage.vertical_offset),
                            manager.getYaw(), footage.angle);
                    combinedAngle = trans.z;
                    System.out.println("Computed: " + trans);
                    var mreturn = manager.setStability2Speeds(trans.x, trans.y, 0, 0,
                            combinedAngle,
                            MISSION_DEPTH);
                    while (!mreturn.isDone())
                        ;
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        };
    }

    public Callable<Boolean> gateAlign(GatePoles gate) {
        return new Callable<>() {
            @Override
            public Boolean call() {
                Mat frame = CameraFeedSender.getFrame(Camera.FRONT);
                Mat yoloout = gate.detectYoloV5(frame);
                try {
                    if (gate.detected()) {
                        gate.transAverage(); // TODO CHECK IF WORKS INSTEAD OF transAlign()
                        PrintWriter printWriter = new PrintWriter(
                                DirGate.toString() + "/" + Instant.now().toString() + ".txt");
                        printWriter.println(Arrays.toString(gate.translation));
                        System.out.println(Arrays.toString(gate.translation));
                        System.out.println("Translation [x, y, distance]: " + Arrays.toString(gate.translation));
                        Imgcodecs.imwrite(DirGate.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);

                        DoublePair trans = Translation.movement(
                                new DoublePair(gate.translation[0], gate.translation[1]));
                        System.out.println("Computed: " + gate);
                        printWriter.println("Computed: " + gate);
                        printWriter.close();

                        manager.setStability2Speeds(trans.x, 0.2, 0, 0, combinedAngle, MISSION_DEPTH);
                        Imgcodecs.imwrite(DirGate.toString() + "/" + Instant.now().toString() + ".jpeg", yoloout);
                        return true;
                    } else {
                        System.out.println("Not detected");
                        manager.setStability2Speeds(0, 0, 0, 0, combinedAngle, MISSION_DEPTH);
                        Imgcodecs.imwrite(DirGate.toString() + "/failure/" + Instant.now().toString() + ".jpeg",
                                yoloout);
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }

    public boolean onPeriodic() {
        boolean update = false;
        if (pathRunnable.isDone()) {
            update = true;
            try {
                lastPathVal = pathRunnable.get();
                pathRunnable = manager.scheduleCallable(pathAlign());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (gateRunnable.isDone()) {
            update = true;
            try {
                lastGateVal = gateRunnable.get();
                gateRunnable = manager.scheduleCallable(gateAlign(gateNoPole));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (update && !lastPathVal && !lastGateVal) {
            try {
                // Gradually increasing strafe
                if (++count > count_max) {
                    count_max += 5;
                    count = 0;
                    strafe = -strafe;
                }
                manager.setStability2Speeds(strafe, 0.25, 0, 0, combinedAngle, MISSION_DEPTH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException {
        System.out.println("EXIT DETECT STATE");
    }

    public State nextState() {
        return new GatePathAlignState(manager, missionName, initialYaw, MISSION_DEPTH);
    }
}
