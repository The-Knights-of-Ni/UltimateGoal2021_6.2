package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.IntakeToElevatorThread;
import org.firstinspires.ftc.teamcode.Subsystems.LauncherFeederThread;
import org.firstinspires.ftc.teamcode.Subsystems.Robot;
import org.firstinspires.ftc.teamcode.Subsystems.WobbleGoalArmTeleopThread;

import java.io.IOException;

/**
 * Created by Andrew Chiang on 3/23/2021
 */

@TeleOp(name = "TeleopMark2")
public class TeleopMark2 extends LinearOpMode {
    //Declare DC motor objects
    private Robot robot;

    double deltaT;
    double timeCurrent;
    double timePre;

    double leftStickY;
    ElapsedTime timer;

    enum Prospective {
        ROBOT,
        DRIVER,
    }

    //    enum MainClawState {
//        CLOSE,
//        OPEN,
//        WIDEOPEN,
//    }
    private double robotAngle;
    private boolean visionEnabled = false;
    private boolean wobbleClawControlDigital = true;
    private boolean wobbleClawDeployed = false;
    private boolean wobbleClawOpen = false;

    private boolean isIntakeOn = false;
    private boolean isLaunchOn = false;
    private boolean isIntakeToElevator = false;
    private boolean isLauncherFeeder = false;
    private boolean isWobbleClawOpen = false;
    private double  wobbleGoalArmIncrement = 0.0;







    private void initOpMode() {
        //Initialize DC motor objects
        timer = new ElapsedTime();
        try {
            robot = new Robot(this, timer, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        timeCurrent = timer.nanoseconds();
        timePre = timeCurrent;

        telemetry.addData("Wait for start", "");
        telemetry.update();

        robot.control.restLauncherFeeder();
        robot.control.openIntakeToElevator();
        robot.control.moveElevatorToBottom();
        robot.wobbleGoalArm.setPosition(0.0);
        robot.control.closeWobbleGoalClaw();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode();

        Thread intakeToElevatorThread = new IntakeToElevatorThread(this, robot);
        Thread launcherFeederThread = new LauncherFeederThread(this, robot);
        Thread wobbleGoalArmThread = new WobbleGoalArmTeleopThread(this, robot);


        waitForStart();
        wobbleGoalArmThread.start();
        launcherFeederThread.start();
        intakeToElevatorThread.start();


//        robot.initServosTeleop();

        telemetry.clearAll();
        timeCurrent = timer.nanoseconds();
        timePre = timeCurrent;

        while(opModeIsActive()) {
            // Get gamepad inputs
            robot.getGamePadInputs();

            // Get the current time
            timeCurrent = timer.nanoseconds();
            deltaT = timeCurrent - timePre;
            timePre = timeCurrent;

            // Drive the motors
            double[] motorPowers;
            robotAngle = robot.imu.getAngularOrientation().firstAngle;

            motorPowers = robot.drive.calcMotorPowers(robot.leftStickX, robot.leftStickY, robot.rightStickX);
            robot.drive.setDrivePowers(motorPowers);

            //Toggle intake regular
            if (robot.aButton && !robot.isaButtonPressedPrev){
                if(isIntakeOn){
                    robot.control.setIntake(false);
                    isIntakeOn = false;
                }
                else{
                    robot.control.setIntake(true);
                    isIntakeOn = true;
                }
            }

            //Toggle intake REVERSE
            if (robot.bButton && !robot.isbButtonPressedPrev){
                if(isIntakeOn){
                    robot.control.setIntakeReverse(false);
                    isIntakeOn = false;
                }
                else{
                    robot.control.setIntakeReverse(true);
                    isIntakeOn = true;
                }
            }

            //Toggle launcher
            if (robot.xButton && !robot.isxButtonPressedPrev){
                if(isLaunchOn) {
                    robot.control.setLaunch(false);
                    isLaunchOn = false;
                }
                else{
                    robot.control.setLaunch(true);
                    isLaunchOn = true;
                }
            }


            //Move elevator
            if(robot.dPadUp && !robot.isdPadUpPressedPrev){
                robot.control.moveElevator(1);
            }

            if(robot.dPadDown && !robot.isdPadDownPressedPrev){
                robot.control.moveElevator(-1);
            }

            //toggle wobble goal claw
            if(robot.bumperLeft && !robot.islBumperPressedPrev){
                if(isWobbleClawOpen){
                    robot.control.closeWobbleGoalClaw();
                    isWobbleClawOpen = false;
                }
                else{
                    robot.control.openWobbleGoalClaw();
                    isWobbleClawOpen = true;
                }

            }



//            int currentPositions[] = robot.drive.getCurrentPositions();
//            telemetry.addData("position", "fl %d, fr %d, rl %d, rr %d",
//                    currentPositions[0], currentPositions[1], currentPositions[2], currentPositions[3]);
            telemetry.update();
        }
        intakeToElevatorThread.interrupt();
        launcherFeederThread.interrupt();
        wobbleGoalArmThread.interrupt();
    }




}