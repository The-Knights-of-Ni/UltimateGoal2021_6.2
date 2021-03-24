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

    private void initOpMode() throws IOException {
        telemetry.addData("Init Robot", "");
        telemetry.update();
        timer = new ElapsedTime();

        this.robot = new Robot(this, timer, isBlue);

        telemetry.addData("Wait for start", "");
        telemetry.update();
    }

    @Override
    public void runOpMode() throws InterruptedException{
        try {
            initOpMode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        waitForStart();

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
                robot.getOpmode().telemetry.addData("[Ring Stack] >>", numRings);
                robot.getOpmode().telemetry.update();
                break;
            case FOUR:
                numRings = "FOUR";
                robot.getOpmode().telemetry.addData("[Ring Stack] >>", numRings);
                robot.getOpmode().telemetry.update();
                break;
        }

        if(numRings.equals("ZERO")) {
            robot.drive.moveForward(70.75*mmPerInch);
            robot.drive.moveRight(22.75/2*mmPerInch);
        }
        else if(numRings.equals("ONE")) {
            robot.drive.moveForward(94.25*mmPerInch);
            robot.drive.moveLeft(22.75/2*mmPerInch);
        }
        else {
            robot.drive.moveForward(117.75*mmPerInch);
            robot.drive.moveRight(22.75/2*mmPerInch);
        }

        // deploy claw and drop wobble goal
        robot.control.deployWobble();
        robot.control.openWobbleClaw();
        robot.control.retractWobble();
        robot.control.closeWobbleClaw();

        if(numRings.equals("ZERO")) {
            // align robot
        }
        else if(numRings.equals("ONE")) {
            robot.drive.moveBackward((94.25-70.75)*mmPerInch);
            robot.drive.moveLeft(22.75/2*mmPerInch);
        }
        else {
            robot.drive.moveBackward((117.75-70.75)*mmPerInch);
            robot.drive.moveRight(22.75/2*mmPerInch);
        }

        robot.drive.moveBackward(22.75/2 * mmPerInch);

        // launch rings
        launchRings();

        // park robot
    }

    private void launchRings() {
        // align robot
        // detect high goal with vision
        // launch rings to high goal

        // this should prob be in control tbh, but we can figure that out once launcher is operational
    }
}
