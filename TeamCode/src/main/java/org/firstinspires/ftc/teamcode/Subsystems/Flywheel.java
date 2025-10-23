package org.firstinspires.ftc.teamcode.Subsystems;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class Flywheel {
    public enum States {
        SPINNING,
        RESTING,
    }
    public boolean IsReady = false;
    public long timeShot = System.currentTimeMillis();
    public static long waitTime = 1500;
    public static double targetVelocity = 1600;

    public States getState() {
        return currentState;

    }


    public States currentState = States.SPINNING;
    DcMotorEx flywheel;
    public void initiate(HardwareMap hardwareMap) {
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
    }


    public void setState(States newState) {
        if (newState == States.SPINNING && currentState != States.SPINNING) {
            timeShot = System.currentTimeMillis();
        }
        currentState = newState;
    }

    public void update() {
        switch (currentState) {
            case RESTING:
                flywheel.setPower(0);
                IsReady = false;

                break;
            case SPINNING:
                flywheel.setVelocity(targetVelocity);
                if (System.currentTimeMillis() - timeShot > waitTime){
                    IsReady = true;
                }
                break;


        }
    }
    public void status (Telemetry telemetry) {
        telemetry.addData("velocity", flywheel.getVelocity());
    }
}
