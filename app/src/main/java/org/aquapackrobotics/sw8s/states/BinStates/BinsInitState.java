import org.aquapackrobotics.sw8s.comms.*;
import org.aquapackrobotics.sw8s.states.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.*;

public class BinsInitState extends State {

    ControlBoardThreadManager manager;

    public BinsInitState(ScheduledThreadPoolExecutor pool) {
        super(pool);
        manager = new ControlBoardThreadManager(pool);
    }

    public void onEnter() throws ExecutionException, InterruptedException {
        manager.setMode(ControlBoardMode.RAW);
        manager.setThrusterInversions(true, true, false, false, true, false, false, true);
        manager.setMotorSpeeds(0,0,0,0,0,0,0,0);
    }


    public boolean onPeriodic() {
        return false;
    }

    public void onExit() throws ExecutionException, InterruptedException{
    }

    public State nextState() {
        return null;
    }
}
