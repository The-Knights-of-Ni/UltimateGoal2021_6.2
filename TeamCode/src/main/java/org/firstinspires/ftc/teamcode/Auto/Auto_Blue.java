package org.firstinspires.ftc.teamcode.Auto;

import com.arcrobotics.ftclib.vision.UGContourRingPipeline;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
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

    private double launchPos;

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
        waitForStart();
        telemetry.addLine("afterWaitForStart");

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

        // deploy claw and grab wobble goal
//        robot.control.openWobbleGoalClaw();   // claws are already open at startup
        robot.control.raiseWobbleGoalArmLow();
        sleep(500);
        robot.control.closeWobbleGoalClaw();
        sleep(500);
        robot.control.raiseWobbleGoalArmMed();
        sleep(500);

        // move to launch line
        launchPos = 56.0;
        robot.drive.moveForward(launchPos*mmPerInch);

        // launch rings
        launchRings();


        if(numRings.equals("ZERO")) {
            robot.drive.moveForward((70.75-launchPos)*mmPerInch);
//            robot.drive.moveRight(22.75/2*mmPerInch);
        }
        else if(numRings.equals("ONE")) {
            robot.drive.moveForward((94.25-launchPos)*mmPerInch);
            robot.drive.moveRight(22.75*mmPerInch);
        }
        else {
            robot.drive.moveForward((117.75-launchPos)*mmPerInch);
//            robot.drive.moveRight(22.75/2*mmPerInch);
        }

        // deploy claw and drop wobble goal
        robot.control.raiseWobbleGoalArmLow();
        sleep(500);
        robot.control.openWideWobbleGoalClaw();
        sleep(500);
        robot.control.moveWobbleGoalArmDown();
        sleep(500);


        // park robot
        if(numRings.equals("ZERO")) {
            // align robot
            robot.drive.moveForward(3.0*mmPerInch);
        }
        else if(numRings.equals("ONE")) {
            robot.drive.moveBackward((94.25-70.75-3.0)*mmPerInch);
//            robot.drive.moveRight(22.75/2*mmPerInch);
        }
        else {
            robot.drive.moveBackward((117.75-70.75-3.0)*mmPerInch);
//            robot.drive.moveLeft(22.75/2*mmPerInch);
        }

//        robot.drive.moveBackward(22.75/2 * mmPerInch);

    }

    private void launchRings() {

        // start launcher
        robot.control.setLaunchVelocity(-800.0); //722

        // wait for launch motor to stablize
        sleep(5000);

        // scoop rings to the back
        robot.control.closeIntakeToElevator();
        sleep(600);
        robot.control.openIntakeToElevator();
        sleep(400);

        // raise elevator
        robot.control.moveElevator(1);
        sleep(1000);

        // launch first ring
        robot.control.launchLauncherFeeder();
        sleep(1000);
        robot.control.restLauncherFeeder();
        sleep(1000);

        // raise elevator
        robot.control.moveElevator(1);
        sleep(500);


        // launch second ring
        robot.control.launchLauncherFeeder();
        sleep(1000);
        robot.control.restLauncherFeeder();
        sleep(1000);

        // lower elevator to floor
        robot.control.moveElevatorToBottom();
        sleep(1000);

    }
}
