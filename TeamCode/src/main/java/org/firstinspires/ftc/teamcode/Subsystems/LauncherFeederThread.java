package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class LauncherFeederThread extends Thread{

    private Robot robot;
    private OpMode opMode;

    public LauncherFeederThread(OpMode opMode, Robot robot) {
        this.setName("LauncherFeederThread");
        this.robot = robot;
        this.opMode = opMode;
        opMode.telemetry.addData("LauncherFeederThread ", this.getName());
        opMode.telemetry.update();
    }

    // called when tread.start is called. thread stays in loop to do what it does until exit is
    // signaled by main code calling thread.interrupt.
    public void run() {
        int evenValue = 0;
        try {

            robot.control.launchLauncherFeeder();
            sleep(350);
            robot.control.restLauncherFeeder();

//                while (!isInterrupted()) {
//
//
//                    telemetry.addData("Running thread ",evenValue);
//                    telemetry.update();
//                }

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
