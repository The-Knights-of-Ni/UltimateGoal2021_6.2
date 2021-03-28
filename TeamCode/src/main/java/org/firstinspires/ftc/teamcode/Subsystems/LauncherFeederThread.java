package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Teleop.TeleopMark2;

public class LauncherFeederThread extends Thread{

    private Robot robot;
    private OpMode opMode;
    private boolean isLauncherFeeder = false;

    public LauncherFeederThread(OpMode opMode, Robot robot) {
        this.setName("LauncherFeederThread");
        this.robot = robot;
        opMode.telemetry.addData("LauncherFeederThread ", this.getName());
        opMode.telemetry.update();
    }

    // called when tread.start is called. thread stays in loop to do what it does until exit is
    // signaled by main code calling thread.interrupt.
    public void run() {
        int evenValue = 0;
        try {

            while (!isInterrupted()) {
                if((robot.triggerRight > 0.3)){
                    if(isLauncherFeeder){
                        isLauncherFeeder = false;
                    }
                    else{
                        robot.control.launchLauncherFeeder();
                        sleep(350);
                        robot.control.restLauncherFeeder();
                        sleep(355);
                        robot.control.moveElevator(1);
                        isLauncherFeeder = true;
                    }
                }

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
