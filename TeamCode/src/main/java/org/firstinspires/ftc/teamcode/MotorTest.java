package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.IntakeToElevatorThread;
import org.firstinspires.ftc.teamcode.Subsystems.LauncherFeederThread;
import org.firstinspires.ftc.teamcode.Subsystems.LauncherThread;
import org.firstinspires.ftc.teamcode.Subsystems.Robot;
import org.firstinspires.ftc.teamcode.Subsystems.WobbleGoalArmTeleopThread;

import java.io.IOException;

/**
 * Created by Ryan on 1/10/2021.
 * modified by Andrew Chiang on 3/24/2021
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
    double targetRPM = 1460.0;
    double incrementRPM = 20.0;
    double incrementKp = 0.0002;
    double incrementKi = 0.00001;
    double incrementKd = 0.0002;
    double Kp = 0.005;
    double Ki = 0.001;
    double Kd = 0.002;

    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor
    private static final double     LAUNCHER_ANG_PER_SEC_LIMIT = 722.0*2.0;
    private static final double     LAUNCHER_RPM_LIMIT = 1800.0;

    private void initOpMode() {
        telemetry.addData("Init Robot", "");
        telemetry.update();
        //Initialize DC motor objects
        timer = new ElapsedTime();
        try {
            robot = new Robot(this, timer, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        telemetry.addData("Wait for start", "");
        telemetry.update();

        robot.control.restLauncherFeeder();
        robot.control.openIntakeToElevator();
        robot.control.moveElevatorToBottom();
    }

    @Override
    public void runOpMode() throws InterruptedException{
        initOpMode();

        Thread intakeToElevatorThread = new IntakeToElevatorThread(this, robot);
        Thread launcherFeederThread = new LauncherFeederThread(this, robot);
        Thread launcherThread = new LauncherThread(this, robot);

        robot.control.setLauncherTargetRPM(0.0);
        robot.control.setLauncherKp(Kp);
        robot.control.setLauncherKi(Ki);
        robot.control.setLauncherKd(Kd);
        robot.control.setLauncherTargetRPM(targetRPM);

        // list REV internal PID coefficients
//        PIDFCoefficients revPIDFCoeficients = robot.launch2a.getPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER);
//        telemetry.addData("REV PIDF Kp: ", revPIDFCoeficients.p);       // Kp = 10.0
//        telemetry.addData("REV PIDF Ki: ", revPIDFCoeficients.i);       // Ki = 3.0
//        telemetry.addData("REV PIDF Kd: ", revPIDFCoeficients.d);       // Kd = 0.0
//        telemetry.addData("REV PIDF Kf: ", revPIDFCoeficients.f);       // Kf = 0.0
        telemetry.addData("Wait for start", "");
        telemetry.update();


        waitForStart();

        intakeToElevatorThread.start();
        launcherFeederThread.start();
        launcherThread.start();

        // Set all Expansion hubs to use the AUTO Bulk Caching mode
        for (LynxModule module : robot.allHubs) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        long startTime = timer.nanoseconds();
        telemetry.addLine("here1");
        telemetry.update();
        sleep(10);

//        robot.control.setLaunchPower(power);
//        robot.control.setLaunchVelocity(-velocity);

        while(opModeIsActive()) {
            robot.getGamePadInputs();

            if(robot.yButton && !robot.isyButtonPressedPrev){
//                power = power + incrementPower;
//                if(power > 1.0){
//                    power = 1.0;
//                }
//                robot.control.setLaunchPower(power);

                Kp = Kp + incrementKp;
                robot.control.setLauncherKp(Kp);
            }
            if(robot.xButton && !robot.isxButtonPressedPrev){
//                power = power - incrementPower;
//                if(power < 0.0){
//                    power = 0.0;
//                }
//                robot.control.setLaunchPower(power);

                Kp = Kp - incrementKp;
                robot.control.setLauncherKp(Kp);
            }
            if(robot.yButton2 && !robot.isyButton2PressedPrev){
                Ki = Ki + incrementKi;
                robot.control.setLauncherKi(Ki);
            }
            if(robot.xButton2 && !robot.isxButton2PressedPrev){
                Ki = Ki - incrementKi;
                robot.control.setLauncherKi(Ki);
            }
            if(robot.bButton2 && !robot.isbButton2PressedPrev){
                Kd = Kd + incrementKd;
                robot.control.setLauncherKd(Kd);
            }
            if(robot.aButton2 && !robot.isaButton2PressedPrev){
                Kd = Kd - incrementKd;
                robot.control.setLauncherKd(Kd);
            }
            if(robot.bButton && !robot.isbButtonPressedPrev){
//                newVelocity = velocity + incrementVelocity;
//                if(newVelocity > LAUNCHER_ANG_PER_SEC_LIMIT){
//                    newVelocity = LAUNCHER_ANG_PER_SEC_LIMIT;
//                }
//                robot.control.setLaunchVelocity(-newVelocity);
//                velocity = newVelocity;

                targetRPM = targetRPM + incrementRPM;
                if (targetRPM > LAUNCHER_RPM_LIMIT) {
                    targetRPM = LAUNCHER_RPM_LIMIT;
                }
                robot.control.setLauncherTargetRPM(targetRPM);
            }
            if(robot.aButton && !robot.isaButtonPressedPrev){
//                newVelocity = velocity - incrementVelocity;
//                if(newVelocity < 0.0){
//                    newVelocity = 0.0;
//                }
//                robot.control.setLaunchVelocity(-newVelocity);
//                velocity = newVelocity;

                targetRPM = targetRPM - incrementRPM;
                if (targetRPM < 0.0) {
                    targetRPM = 0.0;
                }
                robot.control.setLauncherTargetRPM(targetRPM);
            }

            //Move elevator
            if(robot.dPadUp && !robot.isdPadUpPressedPrev && (robot.control.getElevatorStage() != 0)){
                robot.control.moveElevator(1);
            }

            if(robot.dPadDown && !robot.isdPadDownPressedPrev){
                robot.control.moveElevator(-1);
            }


            telemetry.addData("power: ", robot.launch2a.getPower());
            telemetry.addData("set     RPM: ", targetRPM);
            telemetry.addData("current RPM: ", robot.control.getLauncherCurrentRPM());
            telemetry.addData("Kp: ", Kp);
            telemetry.addData("Ki: ", Ki);
            telemetry.addData("Kd: ", Kd);
//            telemetry.addData("set  V: ", robot.control.tickPerSecTORPM(velocity));
//            telemetry.addData("L1   V: ", robot.control.tickPerSecTORPM(robot.launch1.getVelocity()));
//            telemetry.addData("L2a  V: ", robot.control.tickPerSecTORPM(robot.launch2a.getVelocity()));
//            telemetry.addData("L2b  V: ", robot.control.tickPerSecTORPM(robot.launch2b.getVelocity()));

            robot.vision.towerTargetScan();

//            telemetry.update();

            int currentCountL1 = -robot.launch1.getCurrentPosition();
            double currentTime = ((double) (timer.nanoseconds() - startTime)) * 1.0e-6;
            int currentCountL2a = -robot.launch2a.getCurrentPosition();
            int currentCountL2b = -robot.launch2b.getCurrentPosition();

            String output = String.format("time %.3f launch1 %d launch2a %d launch2b %d",
                    currentTime, currentCountL1, currentCountL2a, currentCountL2b);
            Log.d("launcherEnc", output);
        }

        intakeToElevatorThread.interrupt();
        launcherFeederThread.interrupt();
        launcherThread.interrupt();

        // park robot
    }
}
