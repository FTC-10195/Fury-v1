package org.firstinspires.ftc.teamcode.Subsystems;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class Kicker {
    public enum ServoState {
        SHOOTING,
        RETURNING,
        RESTING,
    }
    public static long waitTime = 500;
    long timeSnapshot = System.currentTimeMillis();

    public ServoState currentServoState = ServoState.RESTING;
    Servo KickerServo;
    public void initiate(HardwareMap hardwareMap) {
        KickerServo = hardwareMap.servo.get("kicker");

    }
    public static double servoShootingPos = 0.33;
    public static double servoRestingPos = 0.5;
    public void setState(ServoState newState) {
        currentServoState = newState;
        if (currentServoState == ServoState.SHOOTING){
            timeSnapshot = System.currentTimeMillis();
        }
    }
    public ServoState getCurrentServoState() {
        return currentServoState;
    }
    public void update() {
        switch (currentServoState) {
            case SHOOTING:
                KickerServo.setPosition(servoShootingPos);
                if (System.currentTimeMillis() - timeSnapshot > waitTime){
                    currentServoState = ServoState.RETURNING;
                    timeSnapshot = System.currentTimeMillis();
                }
                break;
            case RETURNING:
                KickerServo.setPosition(servoRestingPos);
                if (System.currentTimeMillis() - timeSnapshot > waitTime){
                    currentServoState = ServoState.RETURNING;
                }
                break;
            case RESTING:
                KickerServo.setPosition(servoRestingPos);
                break;
        }
    }
}
