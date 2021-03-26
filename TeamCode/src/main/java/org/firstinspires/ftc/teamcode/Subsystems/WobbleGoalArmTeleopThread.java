package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Teleop.Teleop;
import org.firstinspires.ftc.teamcode.Teleop.TeleopMark2;

public class WobbleGoalArmTeleopThread extends Thread{

    private double wobbleGoalArmNewPos;
    private double wobbleGoalArmIncrement = 0.1;
    private double currentPos = 0.0;
    private TeleopMark2 teleopMark2;
    private OpMode opMode;
    private Robot robot;

    public WobbleGoalArmTeleopThread(TeleopMark2 teleopMark2) {
        this.setName("WobbleGoalArmTeleopThread");
        this.teleopMark2 = teleopMark2;
        teleopMark2.telemetry.addData("WobbleGoalArmTeleopThread ", this.getName());
        teleopMark2.telemetry.update();
    }

    public WobbleGoalArmTeleopThread(OpMode opMode, Robot robot) {
        this.setName("WobbleGoalArmTeleopThread");
        this.robot = robot;
        this.opMode = opMode;
        opMode.telemetry.addData("WobbleGoalArmTeleopThread ", this.getName());
        opMode.telemetry.update();
    }

    // called when tread.start is called. thread stays in loop to do what it does until exit is
    // signaled by main code calling thread.interrupt.
    public void run() {
        int evenValue = 0;
        double rightSTICKY = 0.0;
        try {
            while(!isInterrupted()) {
//                rightSTICKY = teleopMark2.getRightSTICKY();
////                teleopMark2.telemetry.addData("THREAD ", rightSTICKY);
//                currentPos = teleopMark2.getRobot().wobbleGoalArm.getPosition();
//
//
//                if(rightSTICKY > 0.3){
//                    wobbleGoalArmNewPos = currentPos + wobbleGoalArmIncrement;
//                    if(wobbleGoalArmNewPos > 1.0){
//                        wobbleGoalArmNewPos = 1.0;
//                    }
//                    teleopMark2.getRobot().wobbleGoalArm.setPosition(wobbleGoalArmNewPos);
////                    teleopMark2.telemetry.addData("rightSTICKY > 0.3 ", wobbleGoalArmNewPos);
//
//                }
//
//                if(rightSTICKY < -0.3){
//                    wobbleGoalArmNewPos = currentPos - wobbleGoalArmIncrement;
//                    if(wobbleGoalArmNewPos < 0.0){
//                        wobbleGoalArmNewPos = 0.0;
//                    }
//                    teleopMark2.getRobot().wobbleGoalArm.setPosition(wobbleGoalArmNewPos);
////                    teleopMark2.telemetry.addData("rightSTICKY < -0.3 ", wobbleGoalArmNewPos);
//
//                }
                currentPos = robot.wobbleGoalArm.getPosition();
                rightSTICKY = -robot.rightStickY;


                if(rightSTICKY > 0.3){
                    wobbleGoalArmNewPos = currentPos + wobbleGoalArmIncrement;
                    if(wobbleGoalArmNewPos > 1.0){
                        wobbleGoalArmNewPos = 1.0;
                    }
                    robot.wobbleGoalArm.setPosition(wobbleGoalArmNewPos);
//                    teleopMark2.telemetry.addData("rightSTICKY > 0.3 ", wobbleGoalArmNewPos);

                }

                if(rightSTICKY < -0.3){
                    wobbleGoalArmNewPos = currentPos - wobbleGoalArmIncrement;
                    if(wobbleGoalArmNewPos < 0.0){
                        wobbleGoalArmNewPos = 0.0;
                    }
                    robot.wobbleGoalArm.setPosition(wobbleGoalArmNewPos);
//                    teleopMark2.telemetry.addData("rightSTICKY < -0.3 ", wobbleGoalArmNewPos);

                }

//                teleopMark2.telemetry.update();
                sleep(200);

//

                evenValue += 2;
            }
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
