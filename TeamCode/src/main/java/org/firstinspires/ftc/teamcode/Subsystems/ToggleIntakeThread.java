package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class ToggleIntakeThread extends Thread{

    private Robot robot;
    private OpMode opMode;
    private boolean isIntakeToElevator = false;

    public ToggleIntakeThread(OpMode opMode, Robot robot) {
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
                if ((robot.bumperRight && !robot.isrBumperPressedPrev) || (robot.dPadUp && !robot.isdPadUpPressedPrev && (robot.control.getElevatorStage() == 0))) {
                    if (isIntakeToElevator) {
                        isIntakeToElevator = false;
                    } else {
                        robot.control.closeIntakeToElevator();
                        sleep(600);
                        robot.control.openIntakeToElevator();
                        sleep(300);
                        if(robot.control.getElevatorStage() == 0){
                            robot.control.moveElevator(1);
                        }
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
