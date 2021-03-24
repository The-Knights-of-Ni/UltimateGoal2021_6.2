package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsystems.Robot;

import java.io.IOException;

/**
 * Created by tarunsingh on 12/5/17.
 * Modified by AndrewC on 1/17/2020.
 */

@TeleOp(name="Servo Test")
public class ServoTest extends LinearOpMode {
    private static final int targetPosition = 315;
    private static final double maxPower = 0;
    private static final float mmPerInch        = 25.4f;

    private Robot robot;

    double timePre;
    double timeCurrent;
    ElapsedTime timer;

    public void initOpMode() throws IOException {
        timer = new ElapsedTime();
        this.robot = new Robot(this, timer, true);

        timeCurrent = timer.nanoseconds();
        timePre = timeCurrent;

        telemetry.addData("Wait for start", "");
        telemetry.update();

    }
    public void runOpMode() {
        try {
            initOpMode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //robot.initServosAuto();
        telemetry.clearAll();
//        telemetry.addLine("Wait For Start");
//        telemetry.update();
        waitForStart();

        while(opModeIsActive()){
            //Get gamepad inputs
            robot.getGamePadInputs();

            //Get the current time
            timeCurrent = timer.nanoseconds();

            // elevator
            if (robot.triggerLeft2 > 0.5) {
                robot.elevatorL.setPosition(robot.elevatorL.getPosition() + 0.005);
            }
            else if (robot.triggerLeft2 > 0.1){
                robot.elevatorL.setPosition(robot.elevatorL.getPosition() + 0.001);
            }
            else if (robot.triggerRight2 > 0.5){
                robot.elevatorL.setPosition(robot.elevatorL.getPosition() - 0.005);
            }
            else if (robot.triggerRight2 > 0.1){
                robot.elevatorL.setPosition(robot.elevatorL.getPosition() - 0.001);
            }

            if (robot.triggerLeft > 0.5) {
                robot.elevatorR.setPosition(robot.elevatorR.getPosition() + 0.005);
            }
            else if (robot.triggerLeft > 0.1){
                robot.elevatorR.setPosition(robot.elevatorR.getPosition() + 0.001);
            }
            else if (robot.triggerRight > 0.5){
                robot.elevatorR.setPosition(robot.elevatorR.getPosition() - 0.005);
            }
            else if (robot.triggerRight > 0.1){
                robot.elevatorR.setPosition(robot.elevatorR.getPosition() - 0.001);
            }

            //intake to elevator
            if(robot.leftStickY > 0.5){
                robot.intakeToElevatorL.setPosition(robot.elevatorL.getPosition() + 0.005);
            }
            else if((robot.leftStickY <= 0.5) && (robot.leftStickY > 0.05)){
                robot.intakeToElevatorL.setPosition(robot.elevatorL.getPosition() + 0.001);
            }
            else if(robot.leftStickY < -0.5){
                robot.intakeToElevatorL.setPosition(robot.elevatorL.getPosition() - 0.005);
            }
            else if((robot.leftStickY >= -0.5) && (robot.leftStickY < -0.05)){
                robot.intakeToElevatorL.setPosition(robot.elevatorL.getPosition() - 0.001);
            }

            if(robot.rightStickY > 0.5){
                robot.intakeToElevatorR.setPosition(robot.elevatorR.getPosition() + 0.005);
            }
            else if((robot.rightStickY <= 0.5) && (robot.rightStickY > 0.05)){
                robot.intakeToElevatorR.setPosition(robot.elevatorR.getPosition() + 0.001);
            }
            else if(robot.rightStickY < -0.5){
                robot.intakeToElevatorR.setPosition(robot.elevatorR.getPosition() - 0.005);
            }
            else if((robot.rightStickY >= -0.5) && (robot.rightStickY < -0.05)){
                robot.intakeToElevatorR.setPosition(robot.elevatorR.getPosition() - 0.001);
            }

            //launcher feeder
            if(robot.leftStickY2 > 0.5){
                robot.launcherFeederL.setPosition(robot.launcherFeederL.getPosition() + 0.005);
            }
            else if((robot.leftStickY2 <= 0.5) && (robot.leftStickY2 > 0.05)){
                robot.launcherFeederL.setPosition(robot.launcherFeederL.getPosition() + 0.001);
            }
            else if(robot.leftStickY2 < -0.5){
                robot.launcherFeederL.setPosition(robot.launcherFeederL.getPosition() - 0.005);
            }
            else if((robot.leftStickY2 >= -0.5) && (robot.leftStickY2 < -0.05)){
                robot.launcherFeederL.setPosition(robot.launcherFeederL.getPosition() - 0.001);
            }

            if(robot.rightStickY2 > 0.5){
                robot.launcherFeederR.setPosition(robot.launcherFeederR.getPosition() + 0.005);
            }
            else if((robot.rightStickY2 <= 0.5) && (robot.rightStickY2 > 0.05)){
                robot.launcherFeederR.setPosition(robot.launcherFeederR.getPosition() + 0.001);
            }
            else if(robot.rightStickY2 < -0.5){
                robot.launcherFeederR.setPosition(robot.launcherFeederR.getPosition() - 0.005);
            }
            else if((robot.rightStickY2 >= -0.5) && (robot.rightStickY2 < -0.05)){
                robot.launcherFeederR.setPosition(robot.launcherFeederR.getPosition() - 0.001);
            }





            telemetry.addData("Left trigger", robot.triggerLeft2);
            telemetry.addData("Right trigger", robot.triggerRight2);

            telemetry.addData("Elevator R  ", "%.3f", robot.elevatorR.getPosition());
            telemetry.addData("Elevator L      ", "%.3f", robot.elevatorL.getPosition());

            telemetry.addData("intake to elevator R   ", "%.3f", robot.intakeToElevatorR.getPosition());
            telemetry.addData("intake to Elevator L   ", "%.3f", robot.intakeToElevatorL.getPosition());

            telemetry.addData("Launcher Feeder R      ", "%.3f", robot.launcherFeederR.getPosition());
            telemetry.addData("Launcher Feeder L      ", "%.3f", robot.launcherFeederL.getPosition());
            

            telemetry.update();
            sleep(100);
        }

    }
}