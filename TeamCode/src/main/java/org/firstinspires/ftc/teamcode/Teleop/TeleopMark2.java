package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.IntakeToElevatorThread;
import org.firstinspires.ftc.teamcode.Subsystems.LauncherFeederThread;
import org.firstinspires.ftc.teamcode.Subsystems.Robot;

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
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initOpMode();

        Thread intakeToElevatorThread = new IntakeToElevatorThread(this, robot);
        Thread launcherFeederThread = new LauncherFeederThread(this, robot);
        waitForStart();

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



//            //Open and close claw
//            if (wobbleClawControlDigital) {
//                if (robot.bumperRight2 && !robot.isrBumper2PressedPrev) { // toggle main claw arm deploy mode
//                    robot.control.openWobbleClaw();
//                        robot.isrBumper2PressedPrev = true;
//                    wobbleClawOpen = true;
//                }
//                else
//                    robot.isrBumper2PressedPrev = false;
//            }
//            if ((robot.triggerRight2 > 0.5) && (robot.triggerLeft2 < 0.5)){
//                robot.control.closeWobbleClaw();
//                wobbleClawOpen = false;
//            }
//
//            //Retract and deploy arm
//            if (wobbleClawControlDigital) {
//                if (robot.bumperLeft2 && !robot.islBumper2PressedPrev) { // toggle main claw arm deploy mode
//                    robot.control.deployWobble();
//                    wobbleClawDeployed = true;
//                }
//            }
//            if ((robot.triggerLeft2 > 0.5) && (robot.triggerRight2 < 0.5)){
//                robot.control.retractWobble();
//                wobbleClawDeployed = false;
//            }

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

            //Intake to elevator
            if (robot.bumperRight && !robot.isrBumperPressedPrev){
                if(isIntakeToElevator){
                    isIntakeToElevator = false;
                }
                else{
//                    robot.control.closeIntakeToElevator();
//                    robot.control.openIntakeToElevator();
                    intakeToElevatorThread.start();
                    isIntakeToElevator = true;
                }

            }

            if((robot.triggerRight > 0.3)){
                if(isLauncherFeeder){
                    isLauncherFeeder = false;
                }
                else{
                    launcherFeederThread.start();
                    isLauncherFeeder = true;
                }
            }

            if(robot.dPadUp && !robot.isdPadUpPressedPrev){
                robot.control.moveElevator(1);
            }

            if(robot.dPadDown && !robot.isdPadDownPressedPrev){
                robot.control.moveElevator(-1);
            }



//            int currentPositions[] = robot.drive.getCurrentPositions();
//            telemetry.addData("position", "fl %d, fr %d, rl %d, rr %d",
//                    currentPositions[0], currentPositions[1], currentPositions[2], currentPositions[3]);
            telemetry.update();
        }
        intakeToElevatorThread.interrupt();
        intakeToElevatorThread.interrupt();
    }

//    class IntakeToElevatorThread extends Thread {
//
//        private Robot robot;
//        public IntakeToElevatorThread(Robot robot) {
//            this.setName("IntakeToElevatorThread");
//            this.robot = robot;
//            telemetry.addData("IntakeToElevatorThread ", this.getName());
//            telemetry.update();
//        }
//
//        // called when tread.start is called. thread stays in loop to do what it does until exit is
//        // signaled by main code calling thread.interrupt.
//        @Override
//        public void run() {
//            telemetry.addData("Starting thread ", this.getName());
//            int evenValue = 0;
//            try {
//
//                robot.control.closeIntakeToElevator();
//                sleep(100);
//                robot.control.openIntakeToElevator();
//
////                while (!isInterrupted()) {
////
////
////                    telemetry.addData("Running thread ",evenValue);
////                    telemetry.update();
////                }
//
//                evenValue += 2;
//            }
//            // interrupted means time to shutdown. note we can stop by detecting isInterrupted = true
//            // or by the interrupted exception thrown from the sleep function.
//            // an error occurred in the run loop.
//            catch (Exception e) {
////                e.printStackTrace(Logging.logPrintStream);
//            }
//
////            Logging.log("end of thread %s", this.getName());
//        }
//    }

}