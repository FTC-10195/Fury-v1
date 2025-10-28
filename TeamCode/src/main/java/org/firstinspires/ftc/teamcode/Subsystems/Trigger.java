package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
@Config
public class Trigger {
    public enum States {
        SHOOTING,
        RESTING
    }

    public States currentState = States.RESTING;
    Servo TriggerServo;
    public void initiate(HardwareMap hardwareMap) {
        TriggerServo = hardwareMap.servo.get("trigger");
    }
    public static double servoShootingPos = 0.2;
    public static double servoRestingPos = 0.5;

    public void setState(States newState){
        currentState = newState;
    }
    public States getState(){
        return currentState;
    }
    public void update(){

        switch (currentState){
            case SHOOTING:
                TriggerServo.setPosition(servoShootingPos);
                break;
            case RESTING:
                TriggerServo.setPosition(servoRestingPos);
                break;
        }
    }
}
