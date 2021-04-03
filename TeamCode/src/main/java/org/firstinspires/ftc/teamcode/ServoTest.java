package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.Robot;

import java.io.IOException;

/**
 * Created by tarunsingh on 12/5/17.
 * Modified by AndrewC on 1/17/2020.
 */

@TeleOp(name="Servo Test")
public class ServoTest extends LinearOpMode {
    private static final int targetPosition = 315;
    private static final double maxPower = 0;
    private static final float mmPerInch        = 25.4f;

    private Robot robot;

    double timePre;
    double timeCurrent;
    double absIncrementStep = 0.005;

    ElapsedTime timer;

    public void initOpMode() throws IOException {
        timer = new ElapsedTime();
        this.robot = new Robot(this, timer, true);

        timeCurrent = timer.nanoseconds();
        timePre = timeCurrent;

        telemetry.addData("Wait for start", "");
        telemetry.update();

        robot.intakeToElevatorR.setPosition(0.5);
        robot.intakeToElevatorL.setPosition(0.5);

        robot.launcherFeederR.setPosition(0.5);
        robot.launcherFeederL.setPosition(0.5);

        robot.elevatorL.setPosition(0.5);
        robot.elevatorR.setPosition(0.5);

        robot.wobbleClaw.setPosition(0.5);
        robot.wobbleGoalArm.setPosition(0.5);

    }
    public void runOpMode() {
        try {
            initOpMode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //robot.initServosAuto();
        telemetry.clearAll();
//        telemetry.addLine("Wait For Start");
//        telemetry.update();
        waitForStart();

        while(opModeIsActive()){
            //Get gamepad inputs
            robot.getGamePadInputs();

            //Get the current time
            timeCurrent = timer.nanoseconds();

            if(robot.aButton && !robot.isaButtonPressedPrev){
                double newStep = absIncrementStep - 0.005;
                if(newStep < 0.0){
                    newStep = 0.0;
                }
                absIncrementStep = newStep;
            }

            if (robot.bButton && !robot.isbButtonPressedPrev){
                absIncrementStep = absIncrementStep + 0.005;
            }

            // elevator
            if (robot.triggerLeft2 > 0.5) {
                robot.elevatorL.setPosition(robot.elevatorL.getPosition() + absIncrementStep);
            }
            else if (robot.triggerRight2 > 0.5){
                robot.elevatorL.setPosition(robot.elevatorL.getPosition() - absIncrementStep);
            }

            if (robot.triggerLeft > 0.5) {
                robot.elevatorR.setPosition(robot.elevatorR.getPosition() + absIncrementStep);
            }
            else if (robot.triggerRight > 0.5){
                robot.elevatorR.setPosition(robot.elevatorR.getPosition() - absIncrementStep);
            }

            //intake to elevator
            if(robot.leftStickY > 0.5){
                robot.intakeToElevatorL.setPosition(robot.intakeToElevatorL.getPosition() + absIncrementStep);
            }
            else if(robot.leftStickY < -0.5){
                robot.intakeToElevatorL.setPosition(robot.intakeToElevatorL.getPosition() - absIncrementStep);
            }
            if(robot.rightStickY > 0.5){
                robot.intakeToElevatorR.setPosition(robot.intakeToElevatorR.getPosition() + absIncrementStep);
            }
            else if(robot.rightStickY < -0.5){
                robot.intakeToElevatorR.setPosition(robot.intakeToElevatorR.getPosition() - absIncrementStep);
            }

            //wobble Goal claw and arm
            if(robot.leftStickX > 0.5){
                robot.wobbleGoalArm.setPosition(robot.wobbleGoalArm.getPosition() + absIncrementStep);
            }
            else if(robot.leftStickX < -0.5){
                robot.wobbleGoalArm.setPosition(robot.wobbleGoalArm.getPosition() - absIncrementStep);
            }
            if(robot.rightStickX > 0.5){
                robot.wobbleClaw.setPosition(robot.wobbleClaw.getPosition() + absIncrementStep);
            }
            else if(robot.rightStickX < -0.5){
                robot.wobbleClaw.setPosition(robot.wobbleClaw.getPosition() - absIncrementStep);
            }

            //launcher feeder
            if(robot.leftStickY2 > 0.5){
                robot.launcherFeederL.setPosition(robot.launcherFeederL.getPosition() + absIncrementStep);
            }
            else if(robot.leftStickY2 < -0.5){
                robot.launcherFeederL.setPosition(robot.launcherFeederL.getPosition() - absIncrementStep);
            }

            if(robot.rightStickY2 > 0.5){
                robot.launcherFeederR.setPosition(robot.launcherFeederR.getPosition() + absIncrementStep);
            }
            else if(robot.rightStickY2 < -0.5){
                robot.launcherFeederR.setPosition(robot.launcherFeederR.getPosition() - absIncrementStep);
            }




            telemetry.addData("increment    ", absIncrementStep);
            telemetry.addData("Left trigger", robot.triggerLeft2);
            telemetry.addData("Right trigger", robot.triggerRight2);

            telemetry.addData("Wobble Goal Claw  ", "%.3f", robot.wobbleClaw.getPosition());
            telemetry.addData("Wobble Goal Arm      ", "%.3f", robot.wobbleGoalArm.getPosition());

            telemetry.addData("Elevator L ", "%.3f", robot.elevatorL.getPosition());
            telemetry.addData("Elevator R ", "%.3f", robot.elevatorR.getPosition());

            telemetry.addData("intake to elevator R ", "%.3f", robot.intakeToElevatorR.getPosition());
            telemetry.addData("intake to Elevator L   ", "%.3f", robot.intakeToElevatorL.getPosition());

            telemetry.addData("Launcher Feeder R      ", "%.3f", robot.launcherFeederR.getPosition());
            telemetry.addData("Launcher Feeder L      ", "%.3f", robot.launcherFeederL.getPosition());


            telemetry.update();
            sleep(100);
        }

    }
}