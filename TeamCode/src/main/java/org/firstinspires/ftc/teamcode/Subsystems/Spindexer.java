package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Mat;

@Config
public class Spindexer {
    public enum States{
        RESTING,
        INTAKING,
        SHOOTING
    }
    States currentState = States.RESTING;
    ColorSensor colorSensor;
    DcMotor motor;
    public static double maxPower = 0.05;
    public static int rotateTicks = 20;
    public static double kP = 0.001;
    public static int tolerance = 5;
    public static int maxNumberOfRotationsPerBall = 2;
    int numberOfShots = 0;
    int rotateNumber = 0;
    int rotateSnapshot = 0;
    int targetBallNumber = 0;
    public boolean doneRotating = false;
    public boolean readyToFire = false;
    LimeLight.BallColors currentColor = LimeLight.BallColors.NONE;
    LimeLight.BallColors targetColor = LimeLight.BallColors.NONE;
    public void initiate(HardwareMap hardwareMap){
        colorSensor = hardwareMap.colorSensor.get("spinColor");
        motor = hardwareMap.dcMotor.get("spin");
    }
    public int getTargetBallNumber(){
        return targetBallNumber;
    }
    public void shoot(){
        currentState = States.SHOOTING;
        rotateSnapshot = rotateNumber;
        rotate();
        targetBallNumber = targetBallNumber + 1;
        if (targetBallNumber >= 3){
            targetBallNumber = 0;
        }
    }
    public void rotate(){
        //Read the color 1 slot before the kicking slot
        currentColor = ColorSensors.getBallColor(colorSensor);
        rotateNumber = rotateNumber + 1;
        motor.setTargetPosition(motor.getTargetPosition() + rotateTicks);
    }
    public void setTargetColor(LimeLight.BallColors newColor){
        targetColor = newColor;
    }

    public void update(Telemetry telemetry){
        double error = motor.getTargetPosition() - motor.getCurrentPosition();
        doneRotating = Math.abs(error) < tolerance;

        double power = error * kP;
        if (Math.abs(power) > maxPower){
            power = Math.signum(power) * maxPower;
        }
        motor.setPower(power);
        switch (currentState){
            case RESTING:
            case INTAKING:
                readyToFire = false;
                break;
            case SHOOTING:
                //Done rotating and target color found OR too many rotations - ball isn't here, shoot anyways
                //if (doneRotating && (currentColor == targetColor || rotateNumber - rotateSnapshot > maxNumberOfRotationsPerBall)){
                if (doneRotating && (currentColor == targetColor)){
                    readyToFire = true;
                    currentState = States.RESTING;
                    numberOfShots = numberOfShots + 1;
                    //Ball shot, rotate number resets
                    rotateNumber = 0;
                    rotateSnapshot = 0;
                }
                break;
        }

        telemetry.addData("Spindexer Power", motor.getPower());
        telemetry.addData("Spindexer Target", motor.getTargetPosition());
        telemetry.addData("Spindexer current", motor.getCurrentPosition());
        telemetry.addData("Spindexer Rotate Number",rotateNumber);
        telemetry.addData("Spindexer Rotate Snapshot",rotateSnapshot);
        telemetry.addData("Spindexer Target Ball Number",targetBallNumber);
        telemetry.addData("Spindexer Current Color",currentColor);
        telemetry.addData("Spindexer Target Color",targetColor);
        telemetry.addData("Spindexer State",currentState);
        telemetry.addData("Spindexer readyToFire",readyToFire);
        telemetry.addData("Spindexer DoneRotating",doneRotating);
        telemetry.addData("Spindexer Number of Shots",numberOfShots);
    }
}
