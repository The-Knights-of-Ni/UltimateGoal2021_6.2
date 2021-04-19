package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.IntakeToElevatorThread;
import org.firstinspires.ftc.teamcode.Subsystems.LauncherFeederThread;
import org.firstinspires.ftc.teamcode.Subsystems.LauncherThread;
import org.firstinspires.ftc.teamcode.Subsystems.Robot;
import org.firstinspires.ftc.teamcode.Subsystems.WobbleGoalArmTeleopThread;

import java.io.IOException;

/**
 * Created by Andrew Chiang on 4/18/2021
 */

@TeleOp(name = "TeleopMark3")
public class TeleopMark3 extends LinearOpMode {
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

    private boolean driveHighPower = true;

    private boolean isIntakeOn = false;
    private boolean isLaunchOn = false;
    private boolean isIntakeToElevator = false;
    private boolean isLauncherFeeder = false;
    private boolean isWobbleClawOpen = false;
    private double  wobbleGoalArmIncrement = 0.0;

    private double launchRPMHighGoal = 1600.0;
    private double launchRPMPowerShot = 1500.0;
    private double launchCurrentRPMTarget = launchRPMHighGoal;
    private double launchCurrentRPM;
    private double newLaunchRPMTarget;
    private double incrementRPM = 20.0;


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
//        robot.control.moveWobbleGoalArmDown();
        robot.control.openWideWobbleGoalClaw();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode();

        Thread intakeToElevatorThread = new IntakeToElevatorThread(this, robot);
        Thread launcherFeederThread = new LauncherFeederThread(this, robot);
        Thread wobbleGoalArmThread = new WobbleGoalArmTeleopThread(this, robot);
        Thread launcherThread = new LauncherThread(this, robot);
        robot.control.setLauncherTargetRPM(0.0);

        waitForStart();
        wobbleGoalArmThread.start();
        launcherFeederThread.start();
        intakeToElevatorThread.start();
        launcherThread.start();

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
            if (driveHighPower) {
                motorPowers = robot.drive.calcMotorPowers(robot.leftStickX, robot.leftStickY, robot.rightStickX);
            }
            else {
                motorPowers = robot.drive.calcMotorPowers(robot.leftStickX*0.5, robot.leftStickY*0.5, robot.rightStickX*0.5);
            }
            robot.drive.setDrivePowers(motorPowers);

            //Toggle intake regular
            if (robot.aButton && !robot.isaButtonPressedPrev){
                if(isIntakeOn){
                    robot.control.setIntake(false);
                    isIntakeOn = false;
                }
                else{
                    robot.control.moveElevatorToBottom();
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
                    robot.control.setLauncherTargetRPM(0.0);
                    isLaunchOn = false;
                }
                else{
                    robot.control.setLauncherTargetRPM(launchCurrentRPMTarget);
                    isLaunchOn = true;
                }
            }

            //Toggle drive power
            if (robot.yButton && !robot.isyButtonPressedPrev){
                if(driveHighPower) {
                    driveHighPower = false;
                }
                else{
                    driveHighPower = true;
                }
            }

            //Move elevator
            if(robot.dPadUp && !robot.isdPadUpPressedPrev && (robot.control.getElevatorStage() != 0)){
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

            //adjust launch RPM
            if(robot.dPadRight && !robot.isdPadRightPressedPrev && isLaunchOn){
                newLaunchRPMTarget = launchCurrentRPMTarget + incrementRPM;
                if(newLaunchRPMTarget > robot.control.getLauncherRPMLimit()){
                    newLaunchRPMTarget = robot.control.getLauncherRPMLimit();
                }
                robot.control.setLauncherTargetRPM(newLaunchRPMTarget);
                launchCurrentRPMTarget = newLaunchRPMTarget;

            }
            if(robot.dPadLeft && !robot.isdPadLeftPressedPrev && isLaunchOn){
                newLaunchRPMTarget = launchCurrentRPMTarget - incrementRPM;
                if(newLaunchRPMTarget < 0.0){
                    newLaunchRPMTarget = 0.0;
                }
                robot.control.setLauncherTargetRPM(newLaunchRPMTarget);
                launchCurrentRPMTarget = newLaunchRPMTarget;
            }

//            int currentPositions[] = robot.drive.getCurrentPositions();
//            telemetry.addData("position", "fl %d, fr %d, rl %d, rr %d",
//                    currentPositions[0], currentPositions[1], currentPositions[2], currentPositions[3]);
            launchCurrentRPM = robot.control.getLauncherCurrentRPM();
            telemetry.addData("set     RPM: ", launchCurrentRPMTarget);
            telemetry.addData("Current RPM: ", launchCurrentRPM);
            telemetry.update();
        }
        intakeToElevatorThread.interrupt();
        launcherFeederThread.interrupt();
        wobbleGoalArmThread.interrupt();
        launcherThread.interrupt();
    }




}