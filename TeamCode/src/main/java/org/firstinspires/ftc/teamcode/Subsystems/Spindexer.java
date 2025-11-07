package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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
    public static int rotateTicks = 20;
    public static double kP = 0.001;
    public static int tolerance = 5;
    int rotateNumber = 0;
    int targetBallNumber = 0;
    public boolean doneRotating = false;
    public boolean readyToFire = false;
    LimeLight.BallColors currentColor = LimeLight.BallColors.NONE;
    LimeLight.BallColors targetColor = LimeLight.BallColors.NONE;
    LimeLight.BallColors[] motif;
    public void initiate(HardwareMap hardwareMap){
        colorSensor = hardwareMap.colorSensor.get("spinColor");
        motor = hardwareMap.dcMotor.get("spin");
    }
    public int getTargetBallNumber(){
        return targetBallNumber;
    }
    public void shoot(){
        currentState = States.SHOOTING;
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
        if (rotateNumber >= 3){
            rotateNumber = 0;
        }
    }
    public void setTargetColor(LimeLight.BallColors newColor){
        targetColor = newColor;
    }

    public void update(Telemetry telemetry){
        double power = 0;
        double error = motor.getTargetPosition() - motor.getCurrentPosition();
        doneRotating = Math.abs(error) < tolerance;

        power = error * kP;
        motor.setPower(power);
        switch (currentState){
            case RESTING:
            case INTAKING:
                readyToFire = false;
                break;
            case SHOOTING:
                if (doneRotating && currentColor == targetColor){
                    readyToFire = true;
                    currentState = States.RESTING;
                }
                break;
        }

        telemetry.addData("Spindexer Power", motor.getPower());
        telemetry.addData("Spindexer Target", motor.getTargetPosition());
        telemetry.addData("Spindexer current", motor.getCurrentPosition());
        telemetry.addData("Rotate Number",rotateNumber);
        telemetry.addData("Target Number",targetBallNumber);
        telemetry.addData("Ball Color",currentColor);
        telemetry.addData("Target Color",targetColor);
        telemetry.addData("Spindexer State",currentState);
        telemetry.addData("Spindexer readyToFire",readyToFire);
        telemetry.addData("SpindexerDoneRotating",doneRotating);
    }
}
