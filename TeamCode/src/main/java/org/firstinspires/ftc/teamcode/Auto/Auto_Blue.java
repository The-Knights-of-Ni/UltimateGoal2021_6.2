package org.firstinspires.ftc.teamcode.Auto;

import com.arcrobotics.ftclib.vision.UGContourRingPipeline;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.camera.delegating.DelegatingCaptureSequence;
import org.firstinspires.ftc.teamcode.Subsystems.LauncherThread;
import org.firstinspires.ftc.teamcode.Subsystems.Robot;
import org.openftc.easyopencv.OpenCvCamera;

import java.io.IOException;

/**
 * Created by Ryan on 1/10/2021.
 */

@Autonomous(name = "Auto Blue")
public class Auto_Blue extends LinearOpMode {
    private Robot robot;
    public ElapsedTime timer;

    private static final boolean isBlue = true;

    private static final int CAMERA_WIDTH = 320; // width  of wanted camera resolution
    private static final int CAMERA_HEIGHT = 240; // height of wanted camera resolution
    private static final int HORIZON = 100; // horizon value to tune

    private static final boolean DEBUG = false; // if debug is wanted, change to true

    private static final boolean USING_WEBCAM = true; // change to true if using webcam
    private static final String WEBCAM_NAME = "Webcam 1"; // insert webcam name from configuration if using webcam

    private static final String VUFORIA_KEY =
            "ATDGULf/////AAABmRRGSyLSbUY4lPoqBYjklpYqC4y9J7bCk42kjgYS5KtgpKL8FbpEDQTovzZG8thxB01dClvthxkSuSyCkaZi+JiD5Pu0cMVre3gDwRvwRXA7V9kpoYyMIPMVX/yBTGaW8McUaK9UeQUaFSepsTcKjX/itMtcy7nl1k84JChE4i8whbinHWDpaNwb5qcJsXlQwJhE8JE7t8NMxMm31AgzqjVf/7HwprTRfrxjTjVx5v2rp+wgLeeLTE/xk1JnL3fZMG6yyxPHgokWlIYEBZ5gBX+WJfgA+TDsdSPY/MnBp5Z7QxQsO9WJA59o/UzyEo/9BkbvYJZfknZqeoZWrJoN9jk9sivFh0wIPsH+JjZNFsPw";

    private UGContourRingPipeline pipeline;
    private OpenCvCamera camera;

    private VuforiaLocalizer.Parameters parameters = null;

    // Since ImageTarget trackables use mm to specify their dimensions, we must use mm for all the physical dimension.
    // Define constants
    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    // Define where camera is in relation to center of robot in inches
    final float CAMERA_FORWARD_DISPLACEMENT  = 0 * mmPerInch;
    final float CAMERA_VERTICAL_DISPLACEMENT = 0 * mmPerInch;
    final float CAMERA_LEFT_DISPLACEMENT     = 0 * mmPerInch;

    // Class Members
    private OpenGLMatrix lastLocation = null;
    private VuforiaLocalizer vuforia = null;

    private boolean targetVisible = false;
    private static final int MAX_ITERATION = 40;
    private static final double towerOffsetTargetX = -360.0;
    private static final double towerOffsetTargetY = -1743.0;
    private static final double towerOffsetTargetAngle = -0.6;

    private double launchPosY = 56.0;
    private double launchPosX = 11.0;
    private double secondGoalX = 36.5;
    private double secondGoalY = 12.0;
    private double firstGoalDropOffsetX = 2.0;
    private double firstGoalDropOffsetY = 2.0;
    private double secondGoalDropOffsetX = 6.0;
    private double secondGoalDropOffsetY = 3.0;

    private void initOpMode() throws IOException {
        telemetry.addData("Init Robot", "");
        telemetry.update();
        timer = new ElapsedTime();

        this.robot = new Robot(this, timer, isBlue);

        telemetry.addData("Wait for start", "");
        telemetry.update();

        robot.control.initServo();
    }

    @Override
    public void runOpMode() throws InterruptedException{
        try {
            initOpMode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread launcherThread = new LauncherThread(this, robot);
        robot.control.setLauncherTargetRPM(0.0);

        telemetry.addLine("WaitForStart");
        telemetry.update();
        waitForStart();
        telemetry.addLine("afterWaitForStart");

        launcherThread.start();

        //Detect rings
        String numRings = null;
        switch(robot.vision.ringDetect()) {
            case ZERO:
                numRings = "ZERO";
                telemetry.addData("[Ring Stack] >>", numRings);
                telemetry.update();
                break;
            case ONE:
                numRings = "ONE";
                telemetry.addData("[Ring Stack] >>", numRings);
                telemetry.update();
                break;
            case FOUR:
                numRings = "FOUR";
                telemetry.addData("[Ring Stack] >>", numRings);
                telemetry.update();
                break;
        }
        telemetry.addLine("afterDetectRings");

        if (numRings.equals("ZERO") || numRings.equals("ONE")) {
            // start launcher motors
            startLauncher(1500.0);

            // deploy claw and grab wobble goal
//        robot.control.openWobbleGoalClaw();   // claws are already open at startup
            robot.control.raiseWobbleGoalArmLow();
            sleep(300);
            robot.control.closeWobbleGoalClaw();
            sleep(500);
            robot.control.raiseWobbleGoalArmMed();
            sleep(300);

            // move to launch line
            robot.drive.moveForward_odometry(launchPosY*mmPerInch, 0.5);

            // look for Tower VuMark
            int vIteration = 0;
            while ((!robot.vision.towerTargetScan()) && (vIteration < MAX_ITERATION)) {
                vIteration = vIteration + 1;
                sleep(50);
            }
            telemetry.addData("tower ", " first %d not found", vIteration);
            telemetry.update();
//        sleep(2000);

            // use Tower VuMark to correct robot position
            double towerOffsetX = towerOffsetTargetX;
            double towerOffsetY = towerOffsetTargetY;
            double towerOffsetAngle = towerOffsetTargetAngle;
            if (vIteration < MAX_ITERATION) {
                towerOffsetX = robot.vision.getTowerOffsetX();
                towerOffsetY = robot.vision.getTowerOffsetY();
                towerOffsetAngle = robot.vision.getTowerOffsetAngle();
            }
            telemetry.addData("tower offset", " X %.1f Y %.1f Angle %.1f", towerOffsetX, towerOffsetY, towerOffsetAngle);
            telemetry.update();
//        sleep(2000);
            if (towerOffsetAngle > 90.0) {
                towerOffsetAngle = towerOffsetAngle - 180.0;
            }
            if (towerOffsetAngle < -90.0) {
                towerOffsetAngle = towerOffsetAngle + 180.0;
            }
            robot.drive.turnRobotByTick( -(towerOffsetAngle - towerOffsetTargetAngle));

            robot.drive.moveRight_odometry(launchPosX*mmPerInch - (towerOffsetX - towerOffsetTargetX));
//        sleep(5000);
            // launch rings
//        launchThreeRings();

            // scoop rings to the back
            robot.control.closeIntakeToElevator();
            sleep(600);
            robot.control.openIntakeToElevator();
            sleep(150);

            // raise elevator
            robot.control.moveElevator(1);
            sleep(800);

            // launch first ring
            robot.control.launchOneRing();
//        sleep(1000);
            robot.drive.moveRight_odometry(7.5*mmPerInch);

            // launch second ring
            robot.control.launchOneRing();
//        sleep(1000);
            robot.drive.moveRight_odometry(7.5*mmPerInch);

            // launch third ring
            robot.control.launchOneRing();

            // lower elevator to floor
            robot.control.moveElevatorToBottom();

            stopLauncher();

            // go to target zone to drop the first goal
            if(numRings.equals("ZERO")) {
                robot.drive.moveForward_odometry((70.75-launchPosY+firstGoalDropOffsetY)*mmPerInch, 0.65);
                robot.drive.moveLeft_odometry((launchPosX+15.0+firstGoalDropOffsetX)*mmPerInch, 0.45);
            }
            else if(numRings.equals("ONE")) {
                robot.drive.moveForward_odometry((94.25-launchPosY+firstGoalDropOffsetY)*mmPerInch, 0.65);
                robot.drive.moveLeft_odometry(((launchPosX+15.0+firstGoalDropOffsetX)-22.75)*mmPerInch, 0.45);
            }
            else {
                robot.drive.moveForward_odometry((117.75-launchPosY+firstGoalDropOffsetY)*mmPerInch, 0.65);
                robot.drive.moveLeft_odometry((launchPosX+15.0+firstGoalDropOffsetX)*mmPerInch, 0.45);
            }

            launcherThread.interrupt();

            // deploy claw and drop wobble goal
            robot.control.raiseWobbleGoalArmLow();
            sleep(50);
            robot.control.openWideWobbleGoalClaw();
            sleep(200);


            if(numRings.equals("ZERO")) {
                robot.drive.moveRight_odometry((secondGoalX+firstGoalDropOffsetX)*mmPerInch, 0.45);
                robot.control.moveWobbleGoalArmDown();
                robot.drive.moveBackward_odometry((70.75-secondGoalY+firstGoalDropOffsetY)*mmPerInch, 0.65);
            }
            else if(numRings.equals("ONE")) {
                robot.drive.moveRight_odometry((secondGoalX-22.75+firstGoalDropOffsetX)*mmPerInch,0.45);
                robot.control.moveWobbleGoalArmDown();
                robot.drive.moveBackward_odometry((94.25-secondGoalY+firstGoalDropOffsetY)*mmPerInch, 0.65);
            }
            else {
                robot.drive.moveRight_odometry((secondGoalX+firstGoalDropOffsetX)*mmPerInch, 0.45);
                robot.control.moveWobbleGoalArmDown();
                robot.drive.moveBackward_odometry((117.75-secondGoalY+firstGoalDropOffsetY)*mmPerInch, 0.65);
            }

            // deploy claw and grab wobble goal
            robot.control.raiseWobbleGoalArmMed();
            sleep(400);
            robot.control.closeWobbleGoalClaw();
            sleep(400);
            robot.control.raiseWobbleGoalArmHigh();
            sleep(300);


            if(numRings.equals("ZERO")) {
                robot.drive.moveForward_odometry((70.75-secondGoalY-secondGoalDropOffsetY)*mmPerInch, 0.65);
                robot.drive.moveLeft_odometry((secondGoalX-secondGoalDropOffsetX)*mmPerInch, 0.45);
            }
            else if(numRings.equals("ONE")) {
                robot.drive.moveForward_odometry((94.25-secondGoalY-secondGoalDropOffsetY)*mmPerInch, 0.65);
                robot.drive.moveLeft_odometry((secondGoalX-secondGoalDropOffsetX-22.75)*mmPerInch, 0.45);
            }
            else {
                robot.drive.moveForward_odometry((117.75-secondGoalY-secondGoalDropOffsetY)*mmPerInch, 0.65);
                robot.drive.moveLeft_odometry((secondGoalX-secondGoalDropOffsetX)*mmPerInch, 0.45);
            }

            // deploy claw and drop wobble goal
            robot.control.raiseWobbleGoalArmMed();
            sleep(50);
            robot.control.openWideWobbleGoalClaw();
            sleep(100);
            robot.control.moveWobbleGoalArmDown();
            sleep(350);

            // park robot
            if(numRings.equals("ZERO")) {
                // align robot
                robot.drive.moveForward_odometry((3.0+secondGoalDropOffsetY)*mmPerInch);
            }
            else if(numRings.equals("ONE")) {
                robot.drive.moveBackward_odometry((94.25-70.75-secondGoalDropOffsetY-4.0)*mmPerInch, 0.65);
//            robot.drive.moveRight(22.75/2*mmPerInch);
            }
            else {
                robot.drive.moveBackward_odometry((117.75-70.75-secondGoalDropOffsetY-4.0)*mmPerInch, 0.65);
//            robot.drive.moveLeft(22.75/2*mmPerInch);
            }

//        robot.drive.moveBackward(22.75/2 * mmPerInch);

        }
        else {
            // four rings -> use high goal
            // start launcher motors
            startLauncher(1600.0);

            // deploy claw and grab wobble goal
//        robot.control.openWobbleGoalClaw();   // claws are already open at startup
            robot.control.raiseWobbleGoalArmLow();
            sleep(300);
            robot.control.closeWobbleGoalClaw();
            sleep(500);
            robot.control.raiseWobbleGoalArmMed();
            sleep(300);

            // move to launch line
            robot.drive.moveForward_odometry(launchPosY*mmPerInch, 0.5);

            // look for Tower VuMark
            int vIteration = 0;
            while ((!robot.vision.towerTargetScan()) && (vIteration < MAX_ITERATION)) {
                vIteration = vIteration + 1;
                sleep(50);
            }
            telemetry.addData("tower ", " first %d not found", vIteration);
            telemetry.update();
//        sleep(2000);

            // use Tower VuMark to correct robot position
            double towerOffsetX = towerOffsetTargetX;
            double towerOffsetY = towerOffsetTargetY;
            double towerOffsetAngle = towerOffsetTargetAngle;
            if (vIteration < MAX_ITERATION) {
                towerOffsetX = robot.vision.getTowerOffsetX();
                towerOffsetY = robot.vision.getTowerOffsetY();
                towerOffsetAngle = robot.vision.getTowerOffsetAngle();
            }
            telemetry.addData("tower offset", " X %.1f Y %.1f Angle %.1f", towerOffsetX, towerOffsetY, towerOffsetAngle);
            telemetry.update();
//        sleep(2000);
            if (towerOffsetAngle > 90.0) {
                towerOffsetAngle = towerOffsetAngle - 180.0;
            }
            if (towerOffsetAngle < -90.0) {
                towerOffsetAngle = towerOffsetAngle + 180.0;
            }
            robot.drive.turnRobotByTick( -(towerOffsetAngle - towerOffsetTargetAngle));

            // launch rings
            launchThreeRings();

            stopLauncher();

            // go to target zone to drop the first goal
            if(numRings.equals("ZERO")) {
                robot.drive.moveForward_odometry((70.75-launchPosY+firstGoalDropOffsetY)*mmPerInch, 0.55);
//                robot.drive.moveLeft_odometry((launchPosX+15.0+firstGoalDropOffsetX)*mmPerInch, 0.45);
            }
            else if(numRings.equals("ONE")) {
                robot.drive.moveForward_odometry((94.25-launchPosY+firstGoalDropOffsetY)*mmPerInch, 0.55);
                robot.drive.moveRight_odometry((22.75)*mmPerInch, 0.45);
            }
            else {
                robot.drive.moveForward_odometry((117.75-launchPosY+firstGoalDropOffsetY)*mmPerInch, 0.55);
//                robot.drive.moveLeft_odometry((launchPosX+15.0+firstGoalDropOffsetX)*mmPerInch, 0.45);
            }

            launcherThread.interrupt();

            // deploy claw and drop wobble goal
            robot.control.raiseWobbleGoalArmLow();
            sleep(50);
            robot.control.openWideWobbleGoalClaw();
            sleep(200);


            if(numRings.equals("ZERO")) {
                robot.drive.moveRight_odometry((secondGoalX)*mmPerInch, 0.45);
                robot.control.moveWobbleGoalArmDown();
                robot.drive.moveBackward_odometry((70.75-secondGoalY+firstGoalDropOffsetY)*mmPerInch, 0.55);
            }
            else if(numRings.equals("ONE")) {
                robot.drive.moveRight_odometry((secondGoalX-22.75)*mmPerInch,0.45);
                robot.control.moveWobbleGoalArmDown();
                robot.drive.moveBackward_odometry((94.25-secondGoalY+firstGoalDropOffsetY)*mmPerInch, 0.55);
            }
            else {
                robot.drive.moveRight_odometry((secondGoalX)*mmPerInch, 0.45);
                robot.control.moveWobbleGoalArmDown();
                robot.drive.moveBackward_odometry((117.75-secondGoalY+firstGoalDropOffsetY)*mmPerInch, 0.55);
            }

            // deploy claw and grab wobble goal
            robot.control.raiseWobbleGoalArmMed();
            sleep(400);
            robot.control.closeWobbleGoalClaw();
            sleep(400);
            robot.control.raiseWobbleGoalArmHigh();
            sleep(300);


            if(numRings.equals("ZERO")) {
                robot.drive.moveForward_odometry((70.75-secondGoalY-secondGoalDropOffsetY)*mmPerInch, 0.55);
                robot.drive.moveLeft_odometry((secondGoalX-secondGoalDropOffsetX)*mmPerInch, 0.45);
            }
            else if(numRings.equals("ONE")) {
                robot.drive.moveForward_odometry((94.25-secondGoalY-secondGoalDropOffsetY)*mmPerInch, 0.55);
                robot.drive.moveLeft_odometry((secondGoalX-secondGoalDropOffsetX-22.75)*mmPerInch, 0.45);
            }
            else {
                robot.drive.moveForward_odometry((117.75-secondGoalY-secondGoalDropOffsetY)*mmPerInch, 0.55);
                robot.drive.moveLeft_odometry((secondGoalX-secondGoalDropOffsetX)*mmPerInch, 0.45);
            }

            // deploy claw and drop wobble goal
            robot.control.raiseWobbleGoalArmMed();
            sleep(50);
            robot.control.openWideWobbleGoalClaw();
            sleep(100);
            robot.control.moveWobbleGoalArmDown();
            sleep(350);

            // park robot
            if(numRings.equals("ZERO")) {
                // align robot
                robot.drive.moveForward_odometry((3.0+secondGoalDropOffsetY)*mmPerInch);
            }
            else if(numRings.equals("ONE")) {
                robot.drive.moveBackward_odometry((94.25-70.75-secondGoalDropOffsetY-4.0)*mmPerInch, 0.55);
//            robot.drive.moveRight(22.75/2*mmPerInch);
            }
            else {
                robot.drive.moveBackward_odometry((117.75-70.75-secondGoalDropOffsetY-4.0)*mmPerInch, 0.55);
//            robot.drive.moveLeft(22.75/2*mmPerInch);
            }

//        robot.drive.moveBackward(22.75/2 * mmPerInch);

        }

    }

    private void startLauncher(double launcherRPM) {
        // start launcher
        robot.control.setLauncherTargetRPM(launcherRPM);
    }

    private void stopLauncher() {
        // start launcher
        robot.control.setLauncherTargetRPM(0.0);
    }

    private void launchThreeRings() throws InterruptedException {

//        // start launcher
//        robot.control.setLaunchVelocity(-800.0); //722
//
//        // wait for launch motor to stabilize
//        sleep(5000);

        // scoop rings to the back
        robot.control.closeIntakeToElevator();
        sleep(600);
        robot.control.openIntakeToElevator();
        sleep(150);

        // raise elevator
        robot.control.moveElevator(1);
        sleep(800);

        // launch first ring
        robot.control.launchOneRing();
        sleep(800);

        // launch second ring
        robot.control.launchOneRing();
        sleep(800);

        // launch third ring
        robot.control.launchOneRing();

        // lower elevator to floor
        robot.control.moveElevatorToBottom();

    }

}
