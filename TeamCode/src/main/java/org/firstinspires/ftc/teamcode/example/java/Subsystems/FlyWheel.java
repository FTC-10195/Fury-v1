package org.firstinspires.ftc.teamcode.example.java.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlyWheel {
    public enum States {
        RESTING,
        SPINNING
    }

    States flyState = States.RESTING;
    DcMotor flyMotorLeft;
    DcMotor flyMotorRight; //Right dominant

    public void initiate(HardwareMap hardwareMap) {
        flyMotorLeft = hardwareMap.dcMotor.get("flyL");
        flyMotorRight = hardwareMap.dcMotor.get("flyR");
    }

    public void setState(States state) {
        flyState = state;
    }

    public States getState() {
        return flyState;
    }

    public void update() {
        switch (flyState) {
            case RESTING:
                break;
            case SPINNING:
                flyMotorLeft.setPower(1);
                flyMotorRight.setPower(-1);
                break;
        }

    }
}
