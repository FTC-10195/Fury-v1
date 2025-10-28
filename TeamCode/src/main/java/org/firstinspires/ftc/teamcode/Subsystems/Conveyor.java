package org.firstinspires.ftc.teamcode.Subsystems;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class Conveyor {
    public enum States {
        ON,
        OFF,
        EJECT
    }
    public static double onPower = 1;
    public static double offPower = -1;
    public States currentState = States.OFF;
    public void setState(States newState){
        currentState = newState;
    }
    DcMotor conveyor;
    public void initiate(HardwareMap hardwaremap){
        conveyor = hardwaremap.dcMotor.get("conveyor");
        conveyor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



    }
    public void update(){
        switch(currentState){
            case ON:
                conveyor.setPower(onPower);
                break;
            case OFF:
                conveyor.setPower(0.0);
                break;
            case EJECT:
                conveyor.setPower(offPower);
        }
    }
}



