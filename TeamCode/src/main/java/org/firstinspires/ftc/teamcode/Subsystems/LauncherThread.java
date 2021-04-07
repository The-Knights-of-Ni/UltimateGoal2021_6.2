package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

/**
 * launcher thread - control launcher motor RPM
 * created by Andrew Chiang on 4/4/2021
 */
public class LauncherThread extends Thread{

    private Robot robot;
    private OpMode opMode;
    private ElapsedTime timer;
    private boolean isLauncherFeeder = false;
    private boolean odRAPrev = false;
    private boolean odRBPrev = false;
    private boolean odRACurrent = false;
    private boolean odRBCurrent = false;
    private boolean odBAPrev = false;
    private boolean odBBPrev = false;
    private boolean odBACurrent = false;
    private boolean odBBCurrent = false;
    private boolean odLAPrev = false;
    private boolean odLBPrev = false;
    private boolean odLACurrent = false;
    private boolean odLBCurrent = false;


    public LauncherThread(OpMode opMode, Robot robot) {
        this.setName("LauncherThread");
        this.robot = robot;
        opMode.telemetry.addData("Started: ", this.getName());
        opMode.telemetry.update();
    }

    // called when tread.start is called. thread stays in loop to do what it does until exit is
    // signaled by main code calling thread.interrupt.
    public void run() {
        int evenValue = 0;

        // Get access to a list of Expansion Hub Modules to enable changing caching methods.
//        List<LynxModule> allHubs = opMode.hardwareMap.getAll(LynxModule.class);

        // --------------------------------------------------------------------------------------
        // Run test cycles using AUTO cache mode
        // In this mode, only one bulk read is done per cycle, UNLESS you read a specific encoder/velocity item AGAIN in that cycle.
        // --------------------------------------------------------------------------------------

        // Set all Expansion hubs to use the AUTO Bulk Caching mode
        for (LynxModule module : robot.allHubs) {
//            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
            module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        try {

            odRAPrev = robot.odometryRA.getState();
            odRBPrev = robot.odometryRB.getState();
            odBAPrev = robot.odometryBA.getState();
            odBBPrev = robot.odometryBB.getState();
            odLAPrev = robot.odometryLA.getState();
            odLBPrev = robot.odometryLB.getState();
            while (!isInterrupted()) {
                // Important Step 4: If you are using MANUAL mode, you must clear the BulkCache once per control cycle
                for (LynxModule module : robot.allHubs) {
                    module.clearBulkCache();
                }

                odRACurrent = robot.odometryRA.getState();
                odRBCurrent = robot.odometryRB.getState();
                odBACurrent = robot.odometryBA.getState();
                odBBCurrent = robot.odometryBB.getState();
                odLACurrent = robot.odometryLA.getState();
                odLBCurrent = robot.odometryLB.getState();

                // quadrature decoding
                if(odRACurrent != odRAPrev){
                    if(odRACurrent != odRBCurrent){
                        robot.odRCount ++;
                    }
                    else{
                        robot.odRCount --;
                    }

                }
                if(odRBCurrent != odRBPrev){
                    if(odRBCurrent == odRACurrent){
                        robot.odRCount ++;
                    }
                    else{
                        robot.odRCount --;
                    }
                }
                if(odBACurrent && (odBACurrent != odBAPrev)){
                    if(odBACurrent != odBBCurrent){
                        robot.odBCount ++;
                    }
                    else{
                        robot.odBCount --;
                    }
                }
                if(odBBCurrent != odBBPrev){
                    if(odBBCurrent == odBACurrent){
                        robot.odBCount ++;
                    }
                    else{
                        robot.odBCount --;
                    }
                }
                if(odLACurrent != odLAPrev){
                    robot.odLCount ++;
                    if(odLACurrent != odLBCurrent){
                        robot.odLCount ++;
                    }
                    else{
                        robot.odLCount --;
                    }
                }
                if(odLBCurrent != odLBPrev){
                    if(odLBCurrent == odLACurrent){
                        robot.odLCount ++;
                    }
                    else{
                        robot.odLCount --;
                    }
                }
                odRAPrev = odRACurrent;
                odRBPrev = odRBCurrent;
                odBAPrev = odBACurrent;
                odBBPrev = odBBCurrent;
                odLAPrev = odLACurrent;
                odLBPrev = odLBCurrent;

            }

            evenValue += 2;
        }
        // interrupted means time to shutdown. note we can stop by detecting isInterrupted = true
        // or by the interrupted exception thrown from the sleep function.
        // an error occurred in the run loop.
        catch (Exception e) {
//                e.printStackTrace(Logging.logPrintStream);
        }

//            Logging.log("end of thread %s", this.getName());
    }
}
