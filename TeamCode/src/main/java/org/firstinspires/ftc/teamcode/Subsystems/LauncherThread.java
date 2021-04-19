package org.firstinspires.ftc.teamcode.Subsystems;

import android.annotation.SuppressLint;
import android.util.Log;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

import static java.lang.Math.abs;

/**
 * launcher thread - control launcher motor RPM
 * created by Andrew Chiang on 4/4/2021
 */
public class LauncherThread extends Thread{

    private Robot robot;
    private OpMode opMode;
    private ElapsedTime timer;

    private static final int ENCODER_DATA_DEPTH = 15;
    private double[] sampleTimePoint;
    private int[] sampleEncoderValue;
    private int sampleDataIndex;
    private int sampleDataStartIndex;
    private boolean isLauncherStarted;
    private boolean isLauncherRPMReliable;
    private double launcherRPM;


    public LauncherThread(OpMode opMode, Robot robot) {
        this.setName("LauncherThread");
        this.robot = robot;
        this.timer = robot.timer;
        opMode.telemetry.addData("Started: ", this.getName());
        opMode.telemetry.update();
        sampleTimePoint = new double[ENCODER_DATA_DEPTH];
        sampleEncoderValue = new int[ENCODER_DATA_DEPTH];
        sampleDataIndex = 0;
        sampleDataStartIndex = 0;
        isLauncherStarted = false;
        isLauncherRPMReliable = false;
        launcherRPM = 0.0;
    }

    // called when tread.start is called. thread stays in loop to do what it does until exit is
    // signaled by main code calling thread.interrupt.
    public void run() {
        int evenValue = 0;

        boolean initialized = false;
        boolean kiStart = false;
        double currentTime;
        double prevTime = 0.0;
        double targetRPM;
        double maxRPM;
        double Kp, Ki, Kd;
        double currentError;
        double alpha = 0.995;
        double acculError = 0.0;
        double prevError = 0.0;
        double errorSlope = 0.0;
        double currentPower;


        // Get access to a list of Expansion Hub Modules to enable changing caching methods.
//        List<LynxModule> allHubs = opMode.hardwareMap.getAll(LynxModule.class);

        // --------------------------------------------------------------------------------------
        // Run test cycles using AUTO cache mode
        // In this mode, only one bulk read is done per cycle, UNLESS you read a specific encoder/velocity item AGAIN in that cycle.
        // --------------------------------------------------------------------------------------

        // Set all Expansion hubs to use the AUTO Bulk Caching mode
//        for (LynxModule module : robot.allHubs) {
//            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
////            module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
//        }
        try {

            while (!isInterrupted()) {
                // Important Step 4: If you are using MANUAL mode, you must clear the BulkCache once per control cycle
//                for (LynxModule module : robot.allHubs) {
//                    module.clearBulkCache();
//                }

                currentTime = updateLauncherRPM();

                targetRPM = robot.control.getLauncherTargetRPM();

                Kp = robot.control.getLauncherKp();
                Ki = robot.control.getLauncherKi();
                Kd = robot.control.getLauncherKd();
                maxRPM = robot.control.getLauncherRPMLimit();

                currentError = launcherRPM - targetRPM;
                if ((launcherRPM < 0.9*targetRPM) || (launcherRPM < 1.1*targetRPM)) {
                    kiStart = false;
                    acculError = 0.0;
                }
                if (initialized) { // after the first point, the previous data is valid
                    if (kiStart) {
                        acculError = acculError*alpha + currentError*(currentTime-prevTime);  // integrate error
                    }
                    else {
                        if (currentError > 0)
                            kiStart = true;
                    }
                    errorSlope = (currentError - prevError)/(currentTime-prevTime);         // error slope
                    currentPower = launcherRPM/maxRPM - currentError*Kp - acculError*Ki - errorSlope*Kd; // apply PID correction
                }
                else { // at the first point, use Kp only
                    currentPower = launcherRPM/maxRPM - currentError*Kp;
                    initialized = true;
                }
                if (currentPower > 1.0) currentPower = 1.0;
                if (currentPower < 0.0) currentPower = 0.0;
                robot.launch1.setPower(-currentPower*0.7);
                robot.launch2a.setPower(-currentPower);
                robot.launch2b.setPower(-currentPower);

                prevError = currentError;
                prevTime = currentTime;
                sleep(10);

                @SuppressLint("DefaultLocale") String output = String.format("time %.3f count %d RPM %.2f currentErr %.2f acculErr %.2f errorSlope %.2f power %.3f",
                        currentTime, sampleEncoderValue[sampleDataIndex], launcherRPM, currentError, acculError, errorSlope, currentPower);
                Log.d("launcherEnc", output);
            }

            evenValue += 2;
        }
        // interrupted means time to shutdown. note we can stop by detecting isInterrupted = true
        // or by the interrupted exception thrown from the sleep function.
        // an error occurred in the run loop.
        catch (Exception e) {
//                e.printStackTrace(Logging.logPrintStream);
        }

//            Logging.log("end of thread %s", this.getName());
    }

    private double updateLauncherRPM() {
        int currentCount = -robot.launch2a.getCurrentPosition();
        double currentTime = ((double) timer.nanoseconds()) * 1.0e-6;       // currentTime is in msec

        if (isLauncherStarted) {
            if (isLauncherRPMReliable) {
                // stable operation
                launcherRPM = (currentCount - sampleEncoderValue[sampleDataIndex])*60000.0/(28*(currentTime - sampleTimePoint[sampleDataIndex]));
            }
            else {
                // still filling up data queue
                launcherRPM = (currentCount - sampleEncoderValue[sampleDataStartIndex])*60000.0/(28*(currentTime - sampleTimePoint[sampleDataStartIndex]));
                if (sampleDataStartIndex == sampleDataIndex)
                    isLauncherRPMReliable = true;
            }
        }
        else {
            // first time updating RPM
            isLauncherStarted = true;
            isLauncherRPMReliable = false;
            sampleDataStartIndex = sampleDataIndex;
            launcherRPM = 0.0;
        }
        sampleTimePoint[sampleDataIndex] = currentTime;
        sampleEncoderValue[sampleDataIndex] = currentCount;
        sampleDataIndex = sampleDataIndex + 1;
        if (sampleDataIndex == ENCODER_DATA_DEPTH)
            sampleDataIndex = 0;

        robot.control.setLauncherCurrentRPM(launcherRPM);
        return currentTime;
    }
}
