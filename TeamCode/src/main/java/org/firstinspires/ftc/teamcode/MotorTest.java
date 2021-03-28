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

    double incrementPower = 0.1;
    double power = 1.0;
    double incrementVelocity = 20.0;
    double velocity = 722.0; // ticks/sec
    double newVelocity = velocity;



    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor
    private static final double     LAUNCHER_ANG_PER_SEC_LIMIT = 722.0*2.0;

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

        robot.control.setLaunchPower(power);
        robot.control.setLaunchVelocity(-velocity);

        while(opModeIsActive()) {
            robot.getGamePadInputs();

            if(robot.yButton && !robot.isyButtonPressedPrev){
                power = power + incrementPower;
                if(power > 1.0){
                    power = 1.0;
                }
                robot.control.setLaunchPower(power);

            }
            if(robot.xButton && !robot.isxButtonPressedPrev){
                power = power - incrementPower;
                if(power < 0.0){
                    power = 0.0;
                }
                robot.control.setLaunchPower(power);

            }
            if(robot.bButton && !robot.isbButtonPressedPrev){
                newVelocity = velocity + incrementVelocity;
                if(newVelocity > LAUNCHER_ANG_PER_SEC_LIMIT){
                    newVelocity = LAUNCHER_ANG_PER_SEC_LIMIT;
                }
                robot.control.setLaunchVelocity(-newVelocity);
                velocity = newVelocity;

            }
            if(robot.aButton && !robot.isaButtonPressedPrev){
                newVelocity = velocity - incrementVelocity;
                if(newVelocity < 0.0){
                    newVelocity = 0.0;
                }
                robot.control.setLaunchVelocity(-newVelocity);
                velocity = newVelocity;
            }

            telemetry.addData("power: ", power);
            telemetry.addData("set  V: ", robot.control.tickPerSecTORPM(velocity));
            telemetry.addData("L1   V: ", robot.control.tickPerSecTORPM(robot.launch1.getVelocity()));
            telemetry.addData("L2a  V: ", robot.control.tickPerSecTORPM(robot.launch2a.getVelocity()));
            telemetry.addData("L2b  V: ", robot.control.tickPerSecTORPM(robot.launch2b.getVelocity()));

            telemetry.update();

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
