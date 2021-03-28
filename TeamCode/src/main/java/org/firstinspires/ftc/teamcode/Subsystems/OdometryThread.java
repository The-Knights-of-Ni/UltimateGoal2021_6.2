package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Teleop.TeleopMark2;

import java.util.List;

public class OdometryThread extends Thread{

    private Robot robot;
    private OpMode opMode;
    private boolean isLauncherFeeder = false;
    private boolean odAPrev = false;
    private boolean odBPrev = false;
    private boolean odACurrent = false;
    private boolean odBCurrent = false;


    public OdometryThread(OpMode opMode, Robot robot) {
        this.setName("OdometryThread");
        this.robot = robot;
        opMode.telemetry.addData("OdometryThread ", this.getName());
        opMode.telemetry.update();
    }

    // called when tread.start is called. thread stays in loop to do what it does until exit is
    // signaled by main code calling thread.interrupt.
    public void run() {
        int evenValue = 0;

        List<LynxModule> allHubs = opMode.hardwareMap.getAll(LynxModule.class);

        try {

            odAPrev = robot.odometryA.getState();
            odBPrev = robot.odometryB.getState();
            while (!isInterrupted()) {
                odACurrent = robot.odometryA.getState();
                odBCurrent = robot.odometryB.getState();

                if(odACurrent && (odACurrent != odAPrev)){
                    if(!odBCurrent){
                        robot.odLCount ++;
                    }
                    else{
                        robot.odLCount --;
                    }

                }
                if(odBCurrent && (odBCurrent != odBPrev)){
                    if(odACurrent){
                        robot.odLCount ++;
                    }
                    else{
                        robot.odLCount --;
                    }
                }
                odAPrev = odACurrent;
                odBPrev = odBCurrent;



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
