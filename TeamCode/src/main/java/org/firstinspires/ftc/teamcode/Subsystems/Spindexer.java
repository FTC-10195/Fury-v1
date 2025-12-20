package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Spindexer {
    public enum States{
        MANUAL,
        RESTING,
        INTAKING,
        SHOOTING
    }
    public static double startPos = .05;
    public static double maxPos = .95;
    public static double rotateTicks = .2;
    public static long rotateWaitTime = 200;
    States state = States.RESTING;
    Servo rightServo; //Dominant
    Servo leftServo;
    ColorSensor colorSensor;
    public static double targetPos = startPos;
    boolean rotating = false;
    Timer rotateTimer = new Timer();

    public void setState(States newState){
        state = newState;
    }
    public States getState(){
        return state;
    }
    public void initiate(HardwareMap hardwareMap){
        rightServo = hardwareMap.servo.get("rspin");
        leftServo = hardwareMap.servo.get("lspin");
        colorSensor = hardwareMap.colorSensor.get("color");
    }
    public void rotate(){
        if (rotating){
            return;
        }

        targetPos += rotateTicks;
        rotateTimer.setWait(rotateWaitTime);
        rotating = true;
        if (targetPos > maxPos){
            reset();
        }
    }
    public void reset(){
        double ticksPassed = targetPos - startPos;
        rotateTimer.setWait((long)( ticksPassed/rotateTicks * rotateWaitTime));
        targetPos = startPos;
        rotating = true;
    }
    public void update(){
        rightServo.setPosition(targetPos);
        leftServo.setPosition(1 - targetPos);

        if (rotating){
            if (rotateTimer.doneWaiting()){
                rotating = false;
            }else{
                return;
            }
        }

        if (state == States.MANUAL){
            return;
        }

        //Automatic here

        switch (state){
            case INTAKING:

                break;
            case SHOOTING:
                break;
        }
    }
    public void status(Telemetry telemetry){
        telemetry.addData("SpindexerPosition",targetPos);
        telemetry.addData("SpindexerRotating",rotating);
        telemetry.addData("SpindexerState",state);
    }

}
