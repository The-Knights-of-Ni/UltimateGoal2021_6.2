package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import static java.lang.Thread.sleep;

public class IntakeToElevatorThread extends Thread{

    private Robot robot;
    private OpMode opMode;
    private boolean isIntakeToElevator = false;

    public IntakeToElevatorThread(OpMode opMode, Robot robot) {
        this.setName("IntakeToElevatorThread");
        this.robot = robot;
        this.opMode = opMode;
        opMode.telemetry.addData("IntakeToElevatorThread ", this.getName());
        opMode.telemetry.update();
    }

    // called when tread.start is called. thread stays in loop to do what it does until exit is
    // signaled by main code calling thread.interrupt.
    public void run() {
        int evenValue = 0;
        try {

            while (!isInterrupted()) {
                if (robot.bumperRight && !robot.isrBumperPressedPrev) {
                    if (isIntakeToElevator) {
                        isIntakeToElevator = false;
                    } else {
                        robot.control.closeIntakeToElevator();
                        sleep(600);
                        robot.control.openIntakeToElevator();
                        isIntakeToElevator = true;
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
