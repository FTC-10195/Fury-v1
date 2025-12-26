package org.firstinspires.ftc.teamcode.Subsystems.Spindexer;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Timer;

@Config
public class Kicker {
    public enum States {
        SHOOTING,
        RETURNING,
        RESTING,
    }
    public static long waitTime = 300;
    public static long kickerReturnPadding = 200;

    public States currentState = States.RESTING;
    Timer timer = new Timer();

    Servo kicker;
    public void initiate(HardwareMap hardwareMap) {
        kicker = hardwareMap.servo.get("kicker");
        kicker.setDirection(Servo.Direction.REVERSE);
    }
    public static double servoShootingPos = 0.75;
    public static double servoRestingPos = 0.45;
    public void kick(){
        currentState = States.SHOOTING;
        timer.setWait(waitTime);
    }
    public States getState(){
        return currentState;
    }
    public void update() {
        switch (currentState) {
            case SHOOTING:
                kicker.setPosition(servoShootingPos);
                if (timer.doneWaiting()){
                    currentState = States.RETURNING;
                    timer.setWait(waitTime + kickerReturnPadding);
                }
                break;
            case RETURNING:
                kicker.setPosition(servoRestingPos);
                if (timer.doneWaiting()){
                    currentState = States.RESTING;
                }
                break;
            case RESTING:
                kicker.setPosition(servoRestingPos);
                break;
        }
    }
    public void status(Telemetry telemetry){
        telemetry.addData("Kicker State", getState());
        telemetry.addData("Kicker Pos",kicker.getPosition());
    }
}
