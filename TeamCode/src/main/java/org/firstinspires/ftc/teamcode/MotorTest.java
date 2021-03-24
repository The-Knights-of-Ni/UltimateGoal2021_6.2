package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.Robot;

import java.io.IOException;

/**
 * Created by Ryan on 1/10/2021.
 */

@TeleOp(name = "Motor Test")
public class MotorTest extends LinearOpMode {
    private Robot robot;
    public ElapsedTime timer;

    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor


    private void initOpMode() throws IOException {
        telemetry.addData("Init Robot", "");
        telemetry.update();
        timer = new ElapsedTime();

        robot = new Robot(this, timer);

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

        long startTime = timer.nanoseconds();
        telemetry.addLine("here1");
        telemetry.update();
        sleep(100);

        robot.control.setLaunchPower(1.0 );

        while(opModeIsActive()) {

            int currentCountL1 = -robot.launch1.getCurrentPosition();
            double currentTimeFL = ((double) (timer.nanoseconds() - startTime)) * 1.0e-6;
            int currentCountL2a = -robot.launch2a.getCurrentPosition();
            int currentCountL2b = -robot.launch2b.getCurrentPosition();

            String output = String.format("time %.3f launch1 %d launch2a %d launch2b %d",
                    currentTimeFL, currentCountL1, currentCountL2a, currentCountL2b);
            Log.d("launcherEnc", output);
        }


        // park robot
    }
}
