package org.firstinspires.ftc.teamcode.Subsystems;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class Kicker {
    public enum States {
        SHOOTING,
        RETURNING,
        RESTING,
    }
    public static long waitTime = 500;
    long timeSnapshot = System.currentTimeMillis();

    public States currentState = States.RESTING;

    Servo KickerServo;
    public void initiate(HardwareMap hardwareMap) {
        KickerServo = hardwareMap.servo.get("kicker");
    }
    public static double servoShootingPos = 0.33;
    public static double servoRestingPos = 0.5;
    public void setState(States newState) {
        currentState = newState;
        if (currentState == States.SHOOTING){
            timeSnapshot = System.currentTimeMillis();
        }
    }
    public States getCurrentServoState() {
        return currentState;
    }
    public void update() {
        switch (currentState) {
            case SHOOTING:
                KickerServo.setPosition(servoShootingPos);
                if (System.currentTimeMillis() - timeSnapshot > waitTime){
                    currentState = States.RETURNING;
                    timeSnapshot = System.currentTimeMillis();
                }
                break;
            case RETURNING:
                KickerServo.setPosition(servoRestingPos);
                if (System.currentTimeMillis() - timeSnapshot > waitTime){
                    currentState = States.RETURNING;
                }
                break;
            case RESTING:
                KickerServo.setPosition(servoRestingPos);
                break;
        }
    }
}
