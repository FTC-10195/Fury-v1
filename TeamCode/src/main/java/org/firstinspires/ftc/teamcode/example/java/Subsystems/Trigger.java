package org.firstinspires.ftc.teamcode.example.java.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Trigger {

    public enum TriggerStates {
        RESTING,
        RETURNING,
        SHOOTING
    }

    TriggerStates triggerState = TriggerStates.RESTING;
    Servo trigger;
    public static double triggerResting = 0.5;
    public static double triggerShooting = 0.7;
    public static double shootTime = 200;
    double timeSnapshot = System.currentTimeMillis();

    public void initiate(HardwareMap hardwareMap) {
        trigger = hardwareMap.servo.get("trig");
    }

    public void shoot() {
        if (triggerState == TriggerStates.RESTING) {
            timeSnapshot = System.currentTimeMillis();
            triggerState = TriggerStates.SHOOTING;
        }

    }

    public void update() {

        switch (triggerState) {
            case RESTING:
            case RETURNING:
                trigger.setPosition(triggerResting);
                break;
            case SHOOTING:
                trigger.setPosition(triggerShooting);
                break;
        }
        if (System.currentTimeMillis() - timeSnapshot > shootTime && System.currentTimeMillis() - timeSnapshot < (2 * shootTime)) {
            triggerState = TriggerStates.RETURNING;
        } else if (System.currentTimeMillis() - timeSnapshot > (shootTime * 2)) {
            triggerState = TriggerStates.RESTING;
        }

    }
}
