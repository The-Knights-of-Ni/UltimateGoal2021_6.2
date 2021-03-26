package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.Subsystems.Robot;

import java.io.IOException;

@TeleOp(name = "Multithreading")
public class Multithreading extends LinearOpMode {
    private Robot robot;
    private ElapsedTime timer;
    private double rightStickY;

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        timer = new ElapsedTime();
        try {
            robot = new Robot(this, timer, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread driveThread = new DriveThread(robot.rightStickY);
        // start thread to have robot move while vision is being initialized

        telemetry.addData("Mode", "waiting");
        telemetry.update();

        waitForStart();

        driveThread.start();

        int value = 1;

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                robot.getGamePadInputs();
                rightStickY = robot.rightStickY;
//                telemetry.addData("rightStick", rightStickY);
//                telemetry.addData("MainThread", value);
//
//                telemetry.update();

//                telemetry.addData("Main thread ",value);
//                telemetry.update();
                value += 2;
//
            }

        }

        driveThread.interrupt();
    }

    class DriveThread extends Thread {
        private Robot robot;

        public DriveThread(double rightStickY) {
            this.setName("DriveThread");

            telemetry.addData("DriveThread ", this.getName());
            telemetry.update();
        }

        // called when tread.start is called. thread stays in loop to do what it does until exit is
        // signaled by main code calling thread.interrupt.
        @Override
        public void run() {
            telemetry.addData("Starting thread ", this.getName());
            int evenValue = 0;
            try {

                while (!isInterrupted()) {
                    telemetry.addData("Running thread ", rightStickY);
                    telemetry.update();
                    evenValue += 2;
                }


            }
            // interrupted means time to shutdown. note we can stop by detecting isInterrupted = true
            // or by the interrupted exception thrown from the sleep function.
            // an error occurred in the run loop.
            catch (Exception e) {
//                e.printStackTrace(Logging.logPrintStream);
            }

//            Logging.log("end of thread %s", this.getName());
        }
    }
}