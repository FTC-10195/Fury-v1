package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class Spindexer {
    ColorSensor colorSensor;
    DcMotor motor;
    public static int rotateTicks = 20;
    public static double kP = 0.001;
    public static int tolerance = 5;
    int rotateNumber = 0;
    int targetBallNumber = 0;
    public boolean doneRotating = false;
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
        targetBallNumber = targetBallNumber + 1;
        if (targetBallNumber >= 3){
            targetBallNumber = 0;
        }
    }
    public void rotate(){
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
        currentColor = ColorSensors.getBallColor(colorSensor);;

        telemetry.addData("Spindexer Power", motor.getPower());
        telemetry.addData("Spindexer Target", motor.getTargetPosition());
        telemetry.addData("Spindexer current", motor.getCurrentPosition());
        telemetry.addData("Rotate Number",rotateNumber);
        telemetry.addData("Target Number",targetBallNumber);
        telemetry.addData("Ball Color",currentColor);
        telemetry.addData("Target Color",targetColor);
    }
}
